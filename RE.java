package 词法分析;

import java.util.Stack;
import java.util.StringTokenizer;

/**
 * 处理正则表达式
 * 实现了.*|() 和转义
 * 还有\s \w . + ?
 * @author earayu
 *
 */
public class RE {

	private String re;
	private DFA dfa;
	private String escape = "A.*|()\\+?";//为了让转义的第一个字符索引为1，添上个A（A不转意，所以无所谓。）
	
	public RE(String s)
	{
		re = s;
		makeDFA();
	}
	
	public String getRE()
	{
		return re;
	}

	private boolean isCharacter(char c) {
		char[] alp = Utils.alphetbet1.toCharArray();
		for(int j=0;j<alp.length;++j)
		{
			if(alp[j]==c)
				return true;
		}
		return false;
	}
	
	/**
	 * 将正则表达式转义。
	 * @param s
	 * @return
	 */
	private String escapeRE(String s)
	{
		for(int i=1;i<s.length();++i)
		{
			if(s.charAt(i-1)=='\\' && 
					(	s.charAt(i)=='.'|| s.charAt(i)=='|' || s.charAt(i)=='*'
					||	s.charAt(i)=='(' || s.charAt(i)==')' || s.charAt(i)=='\\'
					//||  s.charAt(i)=='+' || s.charAt(i)=='?'
					)	
			  )
			{
				String h = s.substring(0,i-1);
				String m = String.valueOf( (char)escape.indexOf( s.charAt(i) ) );
				String t = s.substring(i+1,s.length());
				s = h + m + t;
			}
		}
		return s;
	}
	
	private String addDot(String re) {
		StringBuffer sb = new StringBuffer();
		sb.append(re.charAt(0));
		for(int i=1; i<re.length(); i++) {
			//添加毗邻运算符(.)
			boolean flag = isCharacter(re.charAt(i)) && (re.charAt(i-1) == '*' || re.charAt(i-1) == ')' || isCharacter(re.charAt(i-1))) 
					 || (re.charAt(i) == '(' && (isCharacter(re.charAt(i-1)) || re.charAt(i-1)==')' || re.charAt(i-1)=='*'));
			if(flag) 
				sb.append('.');
			sb.append(re.charAt(i));
		}
		return sb.toString();
	}
	
	
	private String infixToPostfix(String expression) {
		StringBuffer postfix = new StringBuffer();
		//存储操作符的栈
		Stack<Character> operatorStack = new Stack<Character>();
		// 将操作符与操作数分开
	    StringTokenizer tokens =
	    	new StringTokenizer(expression, "()*|.", true);
	    // 阶段1: 扫描符号串
	    while(tokens.hasMoreTokens()) {
	    	String token = tokens.nextToken();
	    	if(token.charAt(0) == '|') {
	    		// Process all * , . in the top of the operator stack
	    		while(!operatorStack.isEmpty() 
	    			&& (operatorStack.peek() == '*' || operatorStack.peek() == '.')) {
	    			postfix.append(operatorStack.pop());
	    		}
	    		// Push the | operator into the operator stack
	    		operatorStack.push(token.charAt(0));
	    		
	    	} else if(token.charAt(0) == '.') {
	    		// Process all . in the top of the operator stack
	    		while (!operatorStack.isEmpty() && operatorStack.peek().equals('.')) {
					postfix.append(operatorStack.pop());
		        }
	    		// Push the . operator into the operator stack
		        operatorStack.push(token.charAt(0));
		        
	    	} else if(token.charAt(0) == '*') {
	    		postfix.append(token.charAt(0));    		
	    	} else if(token.charAt(0) == '(') {
	    		operatorStack.push(new Character('(')); // Push '(' to stack
	    	} else if(token.charAt(0) == ')') { 
	    		// Process all the operators in the stack until seeing '('
		        while (!operatorStack.peek().equals('(')) {
					postfix.append(operatorStack.pop());
		        }
		        operatorStack.pop();
		        
	    	} else {
	    		postfix.append(token);
	    	}	    	
	    }
	    
	    // 阶段 2: process all the remaining operators in the stack
	    while(!operatorStack.isEmpty()) {
	    	postfix.append(operatorStack.pop());
	    }
	    return postfix.toString();
	}
	

	
	private NFA evaluateExpression(String postfix) {
		//创建一个操作数栈来存储操作数		
		Stack<NFA> operandStack = new Stack<NFA>();
		//分离操作数与操作符
		//StringTokenizer tokens = new StringTokenizer(postfix, "*|.() ", true);
		//遍历符号
		for(int i=0;i<postfix.length();++i) {
			//String token = tokens.nextToken().trim();
			char c = postfix.charAt(i);//正在处理的字符
			if(c == '*') {	//*操作符(单目运算符)
				NFA nfa = operandStack.pop();
				nfa.closure();			//进行闭包运算
				operandStack.push(nfa);
			} else if(c == '|'
					|| c == '.') {
				processAnOperator(operandStack, c);
			}
			else {		//操作数
				operandStack.push(NFA.ins(c)); //为单个字符构造NFA对象
			}
		}
		return operandStack.pop();
	}
	
	//处理一次双目运算符运算
	private void processAnOperator(Stack<NFA> operandStack, char c) {		
		NFA op1 = operandStack.pop();	//操作数1
		NFA op2 = operandStack.pop();	//操作数2
		if(c == '|') {			//connect运算
			op2.parallel(op1);
			operandStack.push(op2);
		} else if(c == '.') {	//concatenation运算
			op2.connect(op1);
			operandStack.push(op2);
		}
	}

	/*
	 * 预处理正则表达式：转义,解释语法糖（未实现）,添加连接符,转成后缀式等.
	 */
	private String pre()
	{
		return infixToPostfix(addDot(/*candy*/(escapeRE(re))));
	}
	
	private String candy(String s)
	{
		//.
		for(int i=0;i<s.length();++i)
			if(s.charAt(i)=='.')
				s = s.substring(0,i) + "( |!|\"|#|$|%|&|'|,|-|/|0|1|2|3|4|5|6|7|8|9|:|;|<|=|>|@|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|[|]|^|_|`|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|{|}|~|||||||)"
				+ s.substring(i+1,s.length());
		//+
			else if(s.charAt(i)=='+')//TODO 遇到括号就错了 (a|b)+
			{
				if(i==0)
					try {
						throw new Exception("正则表达式语法错误: '+' 不能在首位");
					} catch (Exception e) {
						e.printStackTrace();
					}
				s = s.substring(0,i) + s.charAt(i-1) + "*" + s.substring(i+1,s.length());
			}
		//?
			else if(s.charAt(i)=='?')//TODO
			{
				if(i==0)
					try {
						throw new Exception("正则表达式语法错误: '?' 不能在首位");
					} catch (Exception e) {
						e.printStackTrace();
					}
				s = s.substring(0,i-1) + "(" + s.charAt(i-1) + "|" + (char)0 + ")" + s.substring(i+1,s.length());
			}
			else if(s.charAt(i)=='s')
			{
				if(i-1>=0 && s.charAt(i-1)=='\\')
					s = s.substring(0,i-1) + "( |	|" + (char)10+ "|" + (char)13 +")" + s.substring(i+1,s.length());
			}
			else if(s.charAt(i)=='w')
			{
				if(i-1>=0 && s.charAt(i-1)=='\\')
					s = s.substring(0,i-1) + "(0|1|2|3|4|5|6|7|8|9|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|_|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)" + s.substring(i+1,s.length());
			}
			
		//[a-zA-z0-9]
		
		//{m,n}
		
		return s;
	}
	
	//根据后缀式生成NFA, 然后生成DFA
	private void makeDFA()
	{
		String preRE = pre();
		long t0 = System.currentTimeMillis();
		NFA nfa = this.evaluateExpression(preRE);
		long t1 = System.currentTimeMillis();
		this.dfa = new DFA(nfa);
		long t2 = System.currentTimeMillis();
		System.out.println("nfa:" + (t1-t0) + "ms");
		System.out.println("dfa:" + (t2-t1) + "ms");
	}
	
	/**
	 * 要匹配的表达式没有闭包、连接和或运算，所以遇到这些字符直接转义，然后模拟DFA进行匹配。
	 */
	public boolean match(String s)
	{
		for(int i=0;i<s.length();++i)
		{
			if(s.charAt(i)=='.'|| s.charAt(i)=='|' || s.charAt(i)=='*'
					|| s.charAt(i)=='(' || s.charAt(i)==')' || s.charAt(i)=='\\'
					//|| s.charAt(i)=='+' || s.charAt(i)=='?'
					)
			{
				String h = s.substring(0,i);
				String m = String.valueOf((char)escape.indexOf(s.charAt(i)));
				String t = s.substring(i+1,s.length());
				s = h + m + t;
			}
		}
		return dfa.match(s);
	}
	
	public boolean contains(String s)
	{
		return dfa.contains(s);
	}
	
	public int search(String s)
	{
		return dfa.search(s);
	}
}
