package org.example.Lexer;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.DFA.DFA;
import org.example.DFA.NFAToDFA;
import org.example.MongoDB.mongoDBConfig;
import org.example.MongoDB.mongodbUtils;
import org.example.Token.Token;
import org.example.Token.TokenRegex;
import org.example.Token.Token_Table;
import org.example.Token.Token_Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class Lexer {
    private static final Logger log = LoggerFactory.getLogger(Lexer.class);
    private Map<String, DFA> dfaMap;
    @Resource
    private NFAToDFA toDFA ;
    @Resource
    private mongoDBConfig config;

    @Autowired
    public Lexer(){
        dfaMap = new HashMap<>();
        //从数据库中加载所有的regex和DFA到map中
    }

    /**
     * 初始化时，就把所有的DFA对应关系放到map中了
     */
    @PostConstruct
    public void init(){
        loadDFAsFromMongoDB();
    }

    public DFA getWhiteSpaceDFA(){
        return dfaMap.get(TokenRegex.getWhiteSpace());
    }

    private void loadDFAsFromMongoDB(){
        MongoDatabase database = mongodbUtils.getDatabase(config.getDatabase());
        MongoCollection<Document> collection = database.getCollection(config.getRegexToIdCollection());
//        Document document = new Document();
        FindIterable<Document> documents = collection.find();
        for(Document d : documents){
            String regex = d.getString("regex");
            DFA dfa = toDFA.getDFA(regex);
            dfaMap.put(regex,dfa);
        }
    }

    public Token match(String s){
        for(Map.Entry<String,DFA> entry : dfaMap.entrySet()){
            DFA dfa = entry.getValue();
            boolean res = dfa.verify(s);
            if(res) {
                Token_Type type = TokenRegex.getType(entry.getKey());
                log.info("Input '{}' matches DFA.", s);
                Token token = new Token();
                token.setLexeme(s);//设置原始输入
                token.setType(type);
                if(type.equals(Token_Type.ID)){
                    //需要去查询符号表
                    Token token1 = Token_Table.getToken(s);
                    //符号表中可能没有，比如自定义的变量名字
                    if(token1!=null){
                        token.setType(token1.getType());
                        token.setFunction(token1.getFunction());
                        token.setValue(token1.getValue());
                    }
                    type = token.getType();
                }else if(type.equals(Token_Type.CONST_ID)){
                    //针对常数
                    token.setValue(Double.valueOf(s));
                    token.setFunction(null);
                }
                log.info("{}的类型为{}",s,type);
                return token;
            }
        }
        return null;
    }

}
