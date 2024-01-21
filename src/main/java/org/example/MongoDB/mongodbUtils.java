package org.example.MongoDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.ApplicationContextProvider;
import org.example.DFA.DFAState;
import org.example.NFA.Node;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class mongodbUtils {
    private static MongoClient mongoClient;
    private static mongoDBConfig config;

//    private static ApplicationContext applicationContext = new AnnotationConfigApplicationContext(mongoDBConfig.class);
    static {
        mongoDBConfig bean = ApplicationContextProvider.getBean(mongoDBConfig.class);
        config = bean;
    }
    public static MongoClient getMongoClient(){
        if(mongoClient == null){
            String connectString = config.getUrl();
            mongoClient = MongoClients.create(connectString);
        }
        return mongoClient;
    }

    public static MongoDatabase getDatabase(String dbName){
        return getMongoClient().getDatabase(dbName);
    }



    public static Document nodeToDocument(Node n){
        return new Document().append("nodeName",n.getNodeName());
    }

    public static Document dfaStateToDocument(DFAState state){
        List<Document> docs = state.getNfaNodes().stream().map(
                mongodbUtils::nodeToDocument
        ).collect(Collectors.toList());
        Document document = new Document();
        document.append("nfaNodes",docs).append("stateName",state.getStateName());
        return document;
    }

    public static DFAState convertDocumentToDFAState(Document document) {
        if (document == null) {
            return null;
        }

        // 从 Document 中提取字段值
        List<Document> nfaNodesDocs = document.getList("nfaNodes", Document.class);
        String stateName = document.getString("stateName");

        // 创建 DFAState 对象
        DFAState state = new DFAState();
        state.setStateName(stateName);

        // 将 nfaNodesDocs 转换为 Node 对象，并添加到 DFAState 中
        List<Node> nfaNodes = nfaNodesDocs.stream().map(mongodbUtils::convertDocumentToNode).collect(Collectors.toList());
        state.setNfaNodes(new HashSet<>(nfaNodes));

        return state;
    }

    private static Node convertDocumentToNode(Document document) {
        // 从 Document 中提取字段值，并创建 Node 对象
        String nodeName = document.getString("nodeName");
        Node node = new Node();
        node.setNodeName(nodeName);
        return node;
    }

}
