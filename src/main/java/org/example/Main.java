package org.example;

import lombok.extern.slf4j.Slf4j;

import org.example.DFA.NFAToDFA;

import org.example.Draw.Draw;
import org.example.MongoDB.mongoDBConfig;
import org.example.NFA.RegexToNFA;
import org.example.Parser.Parser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;
import java.util.Set;

@Slf4j
public class Main {
    public static void main(String[] args) {
        ApplicationContext ioc = new AnnotationConfigApplicationContext(mongoDBConfig.class);
        RegexToNFA toNFA = new RegexToNFA();
        NFAToDFA toDFA = ioc.getBean(NFAToDFA.class);
//        String regex = "' '|\n|\f|\r|\t";
//        String s = toNFA.turnToConnect(regex);
//        String suffix = toNFA.midToSuffix(s);
//        System.out.println(suffix); //  \*\*  ---------> \*\*&
//        toNFA.printNFA(regex);
//        DFAdao dao = ioc.getBean(DFAdao.class);
//        dao.saveRegexToId(TokenRegex.getIdregex(),"657188a90feb176963b7448a");
//        String res = test.turnToConnect("(a*b)*ba(a|b)*");
//        log.info("res = {}",res);
//        res = test.turnToConnect("(ab)*(a*|b*)(ba)*");
//        log.info("res = {}",res);
//        String s = test.midToSuffix(res);
//        log.info("s = {}",s);
//        NFA_elem el = test.expToNFA(s);
//        DrawNFA.draw_NFA(el);
//        String regex = "a(b|c)*de";
//        String regex = "//|--";
//        toNFA.printNFA(regex);
//        String regex = TokenRegex.getConstIdRegex();
//        test.printNFA(regex);
//        NFA_elem nfaElem = test.printNFA(regex);
//        NFAToDFA toDFA = new NFAToDFA();
//        DFA dfa = toDFA.buildDFA(nfaElem);
//        generateDFA_Dot.generate(dfa);
////        String regex = "a+(b|c)*";
//        NFA_elem nfaElem = toNFA.printNFA(regex);
//        DFA dfa = toDFA.printDFA(nfaElem);
////        System.out.println(dfa);
////        String test = "a";
//        String t2 = "123ab";
//        String t3 = "ab123";
////        String t4 = "for";
////        toDFA.verify(dfa,test);
//        toDFA.verify(dfa,t2);
//        toDFA.verify(dfa,t3);
//        toDFA.verify(dfa,t4);


//        Map<String, Token_Type> regexMap = TokenRegex.regexMap;
//        Set<Map.Entry<String, Token_Type>> entries = regexMap.entrySet();
//        for(Map.Entry<String,Token_Type> m : entries){
//            String key = m.getKey();
//            toDFA.getDFA(key);
//        }
//        toDFA.getDFA("=");
////        toDFA.printDFA(TokenRegex.getWhiteSpace());
//        Lexer lexer = ioc.getBean(Lexer.class);
//        Token match = lexer.match("PI/");
//        System.out.println(match);
//        toNFA.printNFA(TokenRegex.getWhiteSpace());

//        Scanner bean = ioc.getBean(Scanner.class);
//        Token scan = bean.scan();
//        System.out.println(scan);
//        System.out.println(bean.scan());
//        System.out.println(bean.scan());


        Parser parser = ioc.getBean(Parser.class);
        parser.Parse();
        Draw draw = ioc.getBean(Draw.class);
        draw.display();
    }
}