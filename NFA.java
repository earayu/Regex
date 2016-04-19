package 词法分析;

import java.util.ArrayList;

/**
 * 实现的NFA,有些地方没有错误检测。
 * 可以看成一张加权有向图
 * @author earayu
 *
 */
public class NFA{
	
	private ArrayList<NFANode> nfa;
	
	/**
	 * 约定：索引为i的边到达索引为i的节点
	 * @author earayu
	 *
	 */
	class NFANode implements Comparable
	{
		String state;//状态名
		ArrayList<Character> edge;
		ArrayList<NFANode> desNode;
		boolean start;//true则为开始状态
		boolean end;
		
		NFANode(String state)
		{
			this.state = state;
			edge = new ArrayList<>();
			desNode = new ArrayList<>();
			start = false;
			end = false;
		}
		
		void addEdge(Character s, NFANode d)
		{
			edge.add(s);
			desNode.add(d);
		}
		
		boolean hasPath(char c)
		{
			for(Character x:edge)
			{
				if(x.equals(c))
					return true;
			}
			return false;
		}

		@Override
		public int compareTo(Object o) {
			if(o instanceof NFANode)
			{
				NFANode node = (NFANode) o;
				System.out.println(this.state + " VS " + node.state);
				return this.state.compareTo(node.state);
			}
			throw new ClassCastException("Cannot compare Pair with "  
	                + o.getClass().getName());
		}
	}
	
	
	public NFA()
	{
		nfa = new ArrayList<>();
	}
	
	/**
	 * 添加开始状态
	 */
	private void addStart(String s)
	{
		if(nfa==null)
			return;
		getNode(s).start = true;
	}

	private void addEnd(String s)
	{
		if(nfa==null)
			return;
		getNode(s).end = true;
	}
	
	/**
	 * 添加状态名为s的状态之NFA
	 */
	private void addNodeToNFA(String s)
	{
		if(nfa==null)
			return;
		nfa.add(new NFANode(s));
	}
	
	private void addNodeToNFA(NFANode node)
	{
		if(nfa==null)
			return;
		nfa.add(node);
	}
	
	private void addEdgeToState(String state, Character edge, String des)
	{
		if(this.nfa == null)
			return;
		NFANode from = getNode(state);
		NFANode to = getNode(des);
		from.addEdge(edge, to);
	}
	
	/**
	 * 获取状态名为state的节点
	 */
	private NFANode getNode(String state)
	{
		if(nfa == null)
			return null;
		for(NFANode n:this.nfa)
			if(n.state.equals(state))
				return n;
		return null;
	}
	
	/**
	 * 获取开始状态
	 */
	public NFANode getStart()
	{
		if(nfa==null)
			return null;
		for(NFANode n:nfa)
			if(n.start)
				return n;
		return null;
	}
	
	/**
	 * 获取终结状态
	 */
	public NFANode getEnd()
	{
		if(nfa==null)
			return null;
		for(NFANode n:nfa)
			if(n.end)
				return n;
		return null;
	}
	
	/**
	 * 因为一个大的NFA（具有很多状态的NFA）是由许多小的NFA拼接而成的。
	 * 而小的NFA是各自生成的，这意味着他们的状态名会重名。
	 * 这个函数会给大的NFA的状态重新命名。
	 */
	private void stateSort()
	{
		for(int i=0;i<nfa.size();++i)
			nfa.get(i).state = String.valueOf(i);
	}
	
	/**
	 * 连接操作
	 * @param n2
	 */
	public void connect(NFA n2)
	{
		if(n2.nfa.size()==0)
			return;
		if(nfa.size()==0)
		{
			this.nfa = n2.nfa;
			return;
		}
		NFANode n = getEnd();
		n.end = false;
		n.addEdge('\0', n2.getStart());
		n2.getStart().start = false;
		for(NFANode node:n2.nfa)
			this.addNodeToNFA(node);
		this.stateSort();
	}
	
	/**
	 * 并操作
	 * @param n2
	 */
	public void parallel(NFA n2)//TODO
	{
		NFA n0 = new NFA();
		n0.addNodeToNFA("S");
		n0.addStart("S");
		NFANode s1 = this.getStart();
		NFANode s2 = n2.getStart();
		n0.getStart().addEdge('\0', s1);
		n0.getStart().addEdge('\0', s2);
		s1.start = false;
		s2.start = false;
		n0.addNodeToNFA("F");
		n0.addEnd("F");
		NFANode e1 = this.getEnd();
		NFANode e2 = n2.getEnd();
		e1.addEdge('\0', n0.getEnd());
		e2.addEdge('\0', n0.getEnd());
		e1.end = false;
		e2.end = false;
		for(NFANode node:this.nfa)
			n0.addNodeToNFA(node);
		for(NFANode node:n2.nfa)
			n0.addNodeToNFA(node);
		n0.stateSort();
		this.nfa = n0.nfa;
	}
	
	/**
	 * 为一个NFA添加闭包
	 */
	public void closure()//TODO
	{
		NFA n = new NFA();
		n.addNodeToNFA("S");
		n.addNodeToNFA("E");
		n.addStart("S");
		n.addEnd("E");
		NFANode s = this.getStart();
		NFANode e = this.getEnd();
		s.start = false;
		e.end = false;
		n.getStart().addEdge('\0', s);
		e.addEdge('\0', s);
		e.addEdge('\0', n.getEnd());
		n.getStart().addEdge('\0', n.getEnd());
		for(NFANode node:this.nfa)
			n.addNodeToNFA(node);
		n.stateSort();
		nfa = n.nfa;
	}
	
	/**
	 * 生成一个NFA实例，它有一个开始状态、一个终结状态、一条输入字符为c的边。
	 */
	public static NFA ins(Character c)
	{
		NFA n = new NFA();
		n.addNodeToNFA("S");
		n.addStart("S");
		n.addNodeToNFA("E");
		n.addEnd("E");
		n.addEdgeToState("S", c, "E");
		n.stateSort();
		return n;
	}
	
	/**
	 * 模拟NFA，匹配字符串s。这样就能免得转换成DFA。
	 */
	public boolean match(String s)//TODO 这个方法错了
	{
		if(nfa==null)
			return false;
		NFANode startNode = null;
		for(NFANode n:nfa)
			if(n.start==true)
				startNode = n;
		
		for(Character c:s.toCharArray())
		{
			if(startNode.hasPath(c))
				startNode = startNode.desNode.get(startNode.edge.indexOf(c));
			else
				return false;
		}
		if(startNode.end==true)
			return true;
		else
			return false;
	}
	
	public static void main(String[] args) {
		char c = (char)2;
		NFA n1 = NFA.ins(c);
		NFANode node = n1.getStart();
		
		System.out.println(node.hasPath(c));
		System.out.println(n1.match(String.valueOf(c)));
	}


}
