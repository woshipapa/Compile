package org.example.DFA;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.example.FADrawer;
import org.example.MongoDB.mongoDBConfig;
import org.example.MongoDB.mongodbUtils;
import org.example.NFA.Edge;
import org.example.NFA.NFA_elem;
import org.example.NFA.Node;
import org.example.NFA.RegexToNFA;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Component
public class NFAToDFA {


    /**
     * 计算给定的状态集合的e闭包
     * @param nfaNodes --- 一个状态集合
     * @param nfa ----- NFA图
     * @return
     */
    public DFAState eClosure(Set<Node> nfaNodes, NFA_elem nfa){
        DFAState dfaState = new DFAState();
        dfaState.setNfaNodes(nfaNodes);
        Stack<Node> s = new Stack<>();
        for(Node n : nfaNodes){
            s.push(n);
        }
        while(!s.isEmpty()){
            Node top = s.peek();
            s.pop();
            for(int i =0;i<nfa.getEdgeCount();i++){
                Edge e = nfa.getEdgeList().get(i);
                if(top.getNodeName().equals(e.getStartNode().getNodeName()) && e.getC().equals('#')){
                    boolean res = dfaState.getNfaNodes().add(e.getEndNode());
                    if(res) s.push(e.getEndNode());//防止重复插入
                }
            }
        }
        if(!dfaState.getNfaNodes().isEmpty())
            dfaState.setStateName(getNodeName(dfaState.getNfaNodes()));
        return dfaState;
    }

    private String getNodeName(Set<Node> nodes){
        TreeSet<Node> sortedNodes = new TreeSet<>(
                new Comparator<Node>() {
                    @Override
                    public int compare(Node o1, Node o2) {
                        return o1.getNodeName().compareTo(o2.getNodeName());
                    }
                }
        );
        sortedNodes.addAll(nodes);
        StringBuilder sb = new StringBuilder();
        for(Node n : sortedNodes){
            sb.append(n.getNodeName()+"-");
        }
        String substring = sb.substring(0, sb.lastIndexOf("-"));
        return substring;
    }
    public DFAState sMove(DFAState dfa,Character c,NFA_elem nfaElem){
        DFAState newDFA = new DFAState();
        Set<Node> nfaNodes = dfa.getNfaNodes();
        for(Node n : nfaNodes){
            List<Edge> edges = nfaElem.getEdgeList();
            for(int i = 0;i<edges.size();i++){
                Edge e = edges.get(i);
                if(n.getNodeName().equals(e.getStartNode().getNodeName()) && e.getC().equals(c) && !e.getC().equals('#')){
                    newDFA.getNfaNodes().add(e.getEndNode());
                }
            }
        }
        if(!newDFA.getNfaNodes().isEmpty())
            newDFA.setStateName(getNodeName(newDFA.getNfaNodes()));
        return newDFA;
    }

    private boolean isDFAStateExists(List<DFAState> states,DFAState state){
        for(DFAState dfaState : states){
            if(dfaState.getStateName().equals(state.getStateName())){
                return true;
            }
        }
        return false;

    }

    private boolean isTransitionExists(DFAState state,DFAState nextState,Character c,List<DFATransition> transitions){
        for(DFATransition t : transitions){
            if(t.getStart().getStateName().equals(state.getStateName()) && t.getEnd().getStateName().equals(nextState.getStateName()) && t.getTrans().equals(c)){
                return true;
            }
        }
        return false;
    }


    public DFA buildDFA(NFA_elem nfa){
        this.nfa_elem = nfa;
        Set<Node> initStateSet = new HashSet<>();
        //对NFA的起始状态进行e闭包，获得DFA的初始状态

        initStateSet.add(nfa.getStart());
        DFAState initState = eClosure(initStateSet, nfa);

        DFA dfa = new DFA();
        dfa.setStart(initState);
        List<DFAState> states = dfa.getStates();
        states.add(initState);
        List<DFATransition> transitions = dfa.getTransitions();
        List<DFAState> endStates = dfa.getEndStates();
        for(int i =0 ; i<states.size();i++){
            DFAState dfaState = states.get(i);
            for(int j = 0; j < nfa.getEdgeCount();j++){
                char c = nfa.getEdgeList().get(j).getC();
                DFAState newState = sMove(dfaState, c, nfa);
                //这里一定要判断经过Smove之后的状态集合中是否还有状态
                if(!newState.getNfaNodes().isEmpty()){
                    DFAState dfaNextState = eClosure(newState.getNfaNodes(),nfa);
                    if(!isDFAStateExists(states,dfaNextState)){
                        //确保是新的状态集
                        states.add(dfaNextState);
                        //如果状态集中包含NFA中的终点就加入DFA的终点集合中
                        List<Node> collect = dfaNextState.getNfaNodes().stream().filter(it -> it.getNodeName().equals(nfa.getEnd().getNodeName())).collect(Collectors.toList());
                        if(collect!=null&&collect.size()>0) endStates.add(dfaNextState);
                    }
                    if(!isTransitionExists(dfaState,dfaNextState,c,transitions)){
                        DFATransition transition = new DFATransition();
                        transition.setStart(dfaState);
                        transition.setEnd(dfaNextState);
                        transition.setTrans(c);
                        transitions.add(transition);
                    }
                }

            }
        }
        dfa.setEdgeCount(dfa.getTransitions().size());
        return dfa;
    }
    public DFA buildDFA(String regex){
        //先得到NFA
        String regexWithConnect = toNFA.turnToConnect(regex);
        String regexWithSuffixFormat = toNFA.midToSuffix(regexWithConnect);
        NFA_elem nfa = toNFA.expToNFA(regexWithSuffixFormat);
        return buildDFA(nfa);
    }

    @Resource
    private mongoDBConfig config;

    @Resource
    private DFAdao dao;

    public DFA getDFA(String regex){
        MongoDatabase db = mongodbUtils.getDatabase("compile");
        MongoCollection<Document> regexToId = db.getCollection(config.getRegexToIdCollection());
        //按照regex正规式去查找document记录
        Document query = new Document("regex",regex);
        Document reToId = regexToId.find(query).first();
        //说明其正规式有与之对应的objectID
        if(reToId!=null) {
            String objectId = reToId.getString("objectId");
            ObjectId id = new ObjectId(objectId);
            //从DFA的集合中查找
            MongoCollection<Document> collection = db.getCollection(config.getDFACollection());
            Document dfaQuery = new Document("_id", id);
            Document res = collection.find(dfaQuery).first();
            if (res != null) {
                log.info("{}------>从mongoDB数据库中查询到DFA记录",regex);
                return dao.convertDocumentToDFA(res);
            }else{
                //这里是还有可能数据使用gridFS存储
                MongoCollection<Document> filesCollection = db.getCollection(config.getFilesCollection());
                Document fileQuery = new Document("_id",id);
                Document fileInfo = filesCollection.find(fileQuery).first();
                if(fileInfo!=null){
                    int chunkSize = fileInfo.getInteger("chunkSize");
                    long totalSize = fileInfo.getLong("length");

                    ByteArrayOutputStream fileContent = new ByteArrayOutputStream();
                    int chunkNum = 0;
                    long offset = 0;
                    MongoCollection<Document> chunksCollection = db.getCollection(config.getChunksCollection());
                    while(offset < totalSize){
                        Document chunkQuery = new Document("files_id",id).append("n",chunkNum++);
                        Document chunk = chunksCollection.find(chunkQuery).first();
                        if(chunk != null){
                        Binary binaryData = (Binary)chunk.get("data");
                        byte[] data = binaryData.getData();
                        try{
                            fileContent.write(data);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                            offset+=data.length;
                        }else{
                            log.info("未找到文件块{}",chunkNum-1);
                            break;
                        }
                    }
                    byte[] bytes = fileContent.toByteArray();

                    try{
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                        ObjectInputStream ois = new ObjectInputStream(inputStream);
                        Object obj =ois.readObject();
                        if(obj instanceof DFA){
                            DFA dfa = (DFA) obj;
                            log.info("{}------>成功从gridFS的文件分块chunks中组装回DFA",regex);
                            return dfa;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }else {
                    log.info("未找到文件信息");
                }
            }
        }
        log.info("{}------->mongoDB数据库中还没有，进行构建DFA",regex);
        DFA dfa = buildDFA(regex);
        dao.dynamicStore(dfa,regex);
        return dfa;
    }

    @Resource
    private RegexToNFA toNFA;

    public DFA printDFA(String regex){
        NFA_elem nfa = toNFA.printNFA(regex);
        DFA dfa = buildDFA(nfa);
        FADrawer<DFA> drawer = new DrawDFA();
        String path = drawer.generateDot(dfa, nfa.getFilePath());
        String pngPath = drawer.generatePng(path);
        dfa.setFilePath(pngPath);
        return dfa;
    }

    private NFA_elem nfa_elem;


}
