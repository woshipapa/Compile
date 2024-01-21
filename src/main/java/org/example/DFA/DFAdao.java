package org.example.DFA;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.result.InsertOneResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.MongoDB.mongoDBConfig;
import org.example.MongoDB.mongodbUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.print.Doc;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Component
public class DFAdao {
    private static final int THRESHOLD = 12 * 1024 * 1024;//设置为12MB

//    public static final String filesCollection = "fs.files";
//
//    public static final String chunksCollection = "fs.chunks";
//
//    public static final String DFACollection = "DFA";
//    public static final String regexToIdCollection = "regexToId";
    @Resource
    private mongoDBConfig config;

    private Document convertStateToDocument(DFAState state){
        return mongodbUtils.dfaStateToDocument(state);
    }

    private DFAState convertDocumentToDFAState(Document document){
        return mongodbUtils.convertDocumentToDFAState(document);
    }
    private Document convertTransitionToDocument(DFATransition transition){
        Document document = new Document();
        document.append("startState",convertStateToDocument(transition.start))
                .append("endState",convertStateToDocument(transition.end))
                .append("edge",transition.getTrans());
        return document;
    }

    private MongoCollection<Document> getCollection(String collec){
        MongoDatabase db = mongodbUtils.getDatabase("compile");
        MongoCollection<Document> collection = db.getCollection(collec);
        return collection;
    }
    private GridFSBucket getGridFSBucket(){
        MongoDatabase db = mongodbUtils.getDatabase("compile");
        return GridFSBuckets.create(db);
    }
    public void dynamicStore(DFA dfa,String regex){
        byte[] bytes = dfa.toByteArray();
        int length = bytes.length;
        if(length <= THRESHOLD){
            log.info("使用Document（BSON）格式来插入mongoDB数据库");
            saveDFA(dfa,regex);
        }else{
            log.info("使用GridFS来分块存储");
            storeDFAInGridFS(regex,bytes);
        }
    }
    private  void storeDFAInGridFS(String regex,byte[] dfaData) {
        // 使用 GridFS 存储 DFA 数据
        GridFSBucket bucket = getGridFSBucket();
        GridFSUploadOptions uploadOptions = new GridFSUploadOptions().chunkSizeBytes(1024*1024);
        ObjectId objectId = bucket.uploadFromStream("compile", new ByteArrayInputStream(dfaData), uploadOptions);
        String ID = objectId.toHexString();
        saveRegexToId(regex,ID);
        log.info("DFA data uploaded with ID: {}",objectId);
    }
    public void saveRegexToId(String regex,String objectId){
        MongoCollection<Document> regexToId = getCollection(config.getRegexToIdCollection());
        Document document = new Document().append("regex",regex).append("objectId",objectId);
        regexToId.insertOne(document);
        log.info("成功保存regex与objectId的映射关系");
    }
    public int saveDFA(DFA dfa,String regex){
        MongoCollection<Document> collection = getCollection(config.getDFACollection());
        Document start = convertStateToDocument(dfa.getStart());
        List<Document> states = dfa.getStates().stream().map(this::convertStateToDocument).collect(Collectors.toList());
        List<Document> ends = dfa.getEndStates().stream().map(this::convertStateToDocument).collect(Collectors.toList());
        List<Document> trans = dfa.getTransitions().stream().map(this::convertTransitionToDocument).collect(Collectors.toList());
        Document append = new Document("regex",regex).append("start", start)
                .append("states", states).append("ends", ends).
                append("trans", trans).append("edgeCount",dfa.getEdgeCount()).append("filePath",dfa.getFilePath());
        InsertOneResult insertOneResult = collection.insertOne(append);
        //获取到插入之后的主键objectID
        String objectID = insertOneResult.getInsertedId().asObjectId().getValue().toHexString();

        saveRegexToId(regex,objectID);
        return 1;
    }

    public DFA convertDocumentToDFA(Document document){
        if (document == null) {
            return null;
        }

        // 从 Document 中提取字段值
        Document startStateDoc = document.get("start", Document.class);
        List<Document> statesDoc = document.getList("states", Document.class);
        List<Document> endStatesDoc = document.getList("ends", Document.class);
        List<Document> transitionsDoc = document.getList("trans", Document.class);
        int edgeCount = document.getInteger("edgeCount");
        String filePath = document.getString("filePath");
        String regex = document.getString("regex");

        // 创建 DFAState 对象
        DFAState startState = convertDocumentToDFAState(startStateDoc);
        List<DFAState> states = statesDoc.stream().map(this::convertDocumentToDFAState).collect(Collectors.toList());
        List<DFAState> endStates = endStatesDoc.stream().map(this::convertDocumentToDFAState).collect(Collectors.toList());
        List<DFATransition> transitions = transitionsDoc.stream().map(this::convertDocumentToDFATransition).collect(Collectors.toList());

        // 创建 DFA 对象并设置属性
        DFA dfa = new DFA();
        dfa.setStart(startState);
        dfa.setStates(states);
        dfa.setEndStates(endStates);
        dfa.setTransitions(transitions);
        dfa.setEdgeCount(edgeCount);
        dfa.setFilePath(filePath);
        // 设置其他属性

        return dfa;
    }
    private DFATransition convertDocumentToDFATransition(Document document){
        if(document == null) return null;
        Document startDoc = document.get("startState", Document.class);
        DFAState startState = convertDocumentToDFAState(startDoc);

        Document endDoc = document.get("endState",Document.class);
        DFAState endState = convertDocumentToDFAState(endDoc);

        String edge = document.getString("edge");
        DFATransition transition = new DFATransition();
        transition.setStart(startState);
        transition.setEnd(endState);
        char[] chars = edge.toCharArray();
        transition.setTrans(chars[0]);
        return transition;
    }
}
