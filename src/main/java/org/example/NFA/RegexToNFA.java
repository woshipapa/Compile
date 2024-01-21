package org.example.NFA;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
@Slf4j
@Component
public class RegexToNFA {

    private boolean isLetter(char c){
        if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') return true;
        return false;
    }

    /**
     * 该方法将正规式显式的用 & 连接起来
     * @param regex
     * @return
     */
    public String turnToConnect(String regex){
        int len = regex.length();
        if(len < 2) return regex;
        StringBuilder sb = new StringBuilder();
        char[] chars = regex.toCharArray();
        char pre,now = ' ';
        for(int i =0 ;i+1<len;i++){
             pre = chars[i];
             now = chars[i+1];
            sb.append(pre);
//            if(isLetter(pre) && isLetter(now)){
//                sb.append("+");
//            }else if(pre == ')' && isLetter(now)) sb.append("+");
//            else if(isLetter(pre) && now == '(' || pre == ')' && now == '(') sb.append("+");
//            else if(pre == '*' && isLetter(now) || pre == '*' && now == '(') sb.append("+");
            if(isLetter(now)&& pre!='|' && pre!='(' ) sb.append("&");
            else if(now == '(' && pre!='(' && pre !='|' && pre != '\\') sb.append("&");
            else if (now == '\\' && pre != ' ') {
                //针对需要转义的乘号和加号
                sb.append("&");
            } else if(now == '/'&& pre == '/' || now == '-' && pre == '-'){
                //针对注释为了连接起来
                sb.append("&");
            };
        }
        sb.append(now);
        return sb.toString();
    }
    private int getPriority(int c)
    {//运算符的优先级
        int level = 0; // 优先级
        switch (c)
        {
            case '(':
                level = 1;
                break;
            case '|':
                level = 2;
                break;
            case '&':
                level = 3;
                break;
            case '*':
            case '+':
                level = 4;
                break;
            default:
                break;
        }
        return level;
    }
    boolean isOperator(char c)
    {//判断是不是运算符
        switch (c)
        {
            case '*':
            case '|':
            case '&':
            case '+':
                return true;
            default:
                return false;
        }
    }

    public String midToSuffix(String s){
        Stack<Character> op = new Stack<Character>();
        char[] chars = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ;i<chars.length;i++){
            char c = chars[i];
            if(isOperator(c)){
                if(op.isEmpty()) op.push(c);
                else {
                    while(!op.isEmpty()){
                        char top = op.peek();
                        if(getPriority(top) >= getPriority(c)){
                            op.pop();
                            sb.append(top);
                        }
                        else break;
                    }
                    op.push(c);
                }
            }else if(c == '('){
                op.push(c);
            }else if(c == ')'){
                if(op.isEmpty()){
                    //说明不是完整的左右括号匹配，这里就是为了处理 \) 这种情况
                    sb.append(c);
                    break;
                }
                while(op.peek()!='('){
                    char top = op.peek();
                    sb.append(top);
                    op.pop();
                }
                op.pop();
            }else if(c == '\''){
                continue;
            }
            else{
                sb.append(c);
            }
        }
        while(!op.isEmpty()){
            char c = op.peek();
            sb.append(c);
            op.pop();
        }
        return sb.toString();
    }

    private static Integer nodeNum = 0;

    public String getNodeName(Integer index){
        StringBuilder name = new StringBuilder();
        while(index>=0){
            name.insert(0,(char)('A'+index % 26));
            index = index/26 - 1;
        }
        return name.toString();
    }


    public Node createNode(){
        Node node = new Node();
        node.setNodeName(getNodeName(nodeNum++));
        return node;
    }

    public void elem_copy(NFA_elem dest,NFA_elem sour){
        dest.getEdgeList().addAll(sour.getEdgeList());
        Integer count = dest.getEdgeCount()+sour.getEdgeCount();
        dest.setEdgeCount(count);
    }

    /**
     * 处理单个字符的情况
     */
    public NFA_elem act_elem(Character c){
        Node start = createNode();
        Node end = createNode();

        Edge edge = new Edge();
        edge.setStartNode(start);
        edge.setEndNode(end);
        edge.setC(c);

        NFA_elem elem = new NFA_elem();
        elem.setStart(start);
        elem.setEnd(end);
        List<Edge> edgeList = new ArrayList<>();
        edgeList.add(edge);
        elem.setEdgeList(edgeList);
        elem.setEdgeCount(edgeList.size());
        return elem;
    }

    /**
     * 处理 a|b 的情况
     *
     *        e1          e3
     *  start ----> first ----> end
     *     \                     ^
     *      \                   /
     *       \-> second -------/
     *      e2            e4
     */
    public NFA_elem act_unit(NFA_elem first,NFA_elem second){
        NFA_elem elem = new NFA_elem();

        Node start = createNode();
        Node end = createNode();

        Edge e1 = new Edge();
        e1.setStartNode(start);
        e1.setEndNode(first.getStart());
        e1.setC('#');

        Edge e2 = new Edge();
        e2.setStartNode(start);
        e2.setEndNode(second.getStart());
        e2.setC('#');


        Edge e3 = new Edge();
        e3.setStartNode(first.getEnd());
        e3.setEndNode(end);
        e3.setC('#');

        Edge e4 = new Edge();
        e4.setStartNode(second.getEnd());
        e4.setEndNode(end);
        e4.setC('#');

        elem_copy(elem,first);
        elem_copy(elem,second);

        elem.insertEdgeBatch(Arrays.asList(new Edge[]{e1,e2,e3,e4}));

        elem.setStart(start);
        elem.setEnd(end);

        return elem;
    }

    /**
     * 处理连接的情况，a&b
     *
     * first ----> second
     */
    public NFA_elem act_join(NFA_elem first,NFA_elem second){
        List<Edge> sec_edges = second.getEdgeList();
        String secStartNode = second.getStart().getNodeName();
        //修改被连接的图中边的起点或者终点如果是图的起点，就要修改为第一个连接的终点
        for(Edge e : sec_edges){
            if(e.getStartNode().getNodeName().equals(secStartNode)){
                e.setStartNode(first.getEnd());
            }else if(e.getEndNode().getNodeName().equals(secStartNode)){
                e.setEndNode(first.getEnd());
            }
        }
        second.setStart(first.getEnd());

        elem_copy(first,second);
        first.setEnd(second.getEnd());
        return first;
    }

    /**
     * 处理a+
     * @param elem
     * @return
     */
    public NFA_elem act_OneOrMore(NFA_elem elem){
        NFA_elem newElem = new NFA_elem();

        Node start = createNode();
        Node end = createNode();


        Edge e1 = new Edge();
        e1.setStartNode(start);
        e1.setEndNode(elem.getStart());
        e1.setC('#');

        Edge e2 = new Edge();
        e2.setStartNode(elem.getEnd());
        e2.setEndNode(elem.getStart());
        e2.setC('#');

        Edge e3 = new Edge();
        e3.setStartNode(elem.getEnd());
        e3.setEndNode(end);
        e3.setC('#');

        List<Edge> newEdges = Arrays.asList(new Edge[]{e1,e2,e3});
        elem_copy(newElem,elem);
        newElem.insertEdgeBatch(newEdges);

        newElem.setStart(start);
        newElem.setEnd(end);
        return newElem;
    }

    /**
     * 处理闭包运算a*
     *
     *
     *           e1
     *   start  ----->   end
     *     |             ^
     *     | e2         | e4
     *     |----> elem ----|
     *        <--------|
     *               e3
     */
    public NFA_elem act_star(NFA_elem elem){
        NFA_elem newElem = new NFA_elem();

        Node start = createNode();
        Node end = createNode();

        //可以不经过a的一条边，直接跳过
        Edge e1 = new Edge();
        e1.setStartNode(start);
        e1.setEndNode(end);
        e1.setC('#');

        Edge e2 = new Edge();
        e2.setStartNode(start);
        e2.setEndNode(elem.getStart());
        e2.setC('#');

        //回路，回到a的起点，来表示闭包可以重复执行
        Edge e3 = new Edge();
        e3.setStartNode(elem.getEnd());
        e3.setEndNode(elem.getStart());
        e3.setC('#');

        Edge e4 = new Edge();
        e4.setStartNode(elem.getEnd());
        e4.setEndNode(end);
        e4.setC('#');

        List<Edge> newEdges = Arrays.asList(new Edge[]{e1,e2,e3,e4});
        elem_copy(newElem,elem);
        newElem.insertEdgeBatch(newEdges);

        newElem.setStart(start);
        newElem.setEnd(end);
        return newElem;
    }

    /**
     * 后缀表达式运算
     * @param exp
     * @return
     */
    public NFA_elem expToNFA(String exp){
        Stack<NFA_elem> stack = new Stack<>();
        int len = exp.length();
        char[] chars =exp.toCharArray();
        NFA_elem fir,sec;
        for(int i = 0 ;i<len;i++){
            switch (chars[i]){
                case '|':{
                    sec = stack.peek();
                    stack.pop();
                    fir = stack.peek();
                    stack.pop();
                    NFA_elem elem = act_unit(fir, sec);
                    stack.push(elem);
                };break;
                case '*':{
                    fir = stack.peek();
                    stack.pop();
                    NFA_elem elem = act_star(fir);
                    stack.push(elem);
                };break;
                case '&':{
                    sec = stack.peek();
                    stack.pop();
                    fir = stack.peek();
                    stack.pop();
                    fir = act_join(fir,sec);
                    stack.push(fir);
                };break;
                case '+':{
                    fir = stack.peek();
                    stack.pop();
                    NFA_elem elem = act_OneOrMore(fir);
                    stack.push(elem);
                };break;
                case '\\':{
                    i++;//得到转义字符实际的字符(加号或者乘号)
                };
                default:{
                    //注意空白字符
                    NFA_elem elem = act_elem(chars[i]);
                    stack.push(elem);
                };break;

            }
        }
        return stack.peek();
    }

    public NFA_elem printNFA(String regex){
        String connect = turnToConnect(regex);
        log.info("经过+连接之后的正规式 : {}",connect);
        String suffix = midToSuffix(connect);
        log.info("转成后缀表达式之后的正规式 : {}",suffix);
        NFA_elem nfa = expToNFA(suffix);
        nfa.setRegex(regex);
        DrawNFA drawNFA = new DrawNFA();
        String path = drawNFA.generateDot(nfa,regex);
        path = drawNFA.generatePng(path);
        nfa.setFilePath(path);
        return nfa;
        }
}
