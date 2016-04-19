package 词法分析;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import 词法分析.NFA.NFANode;

public class DFA {

	private ArrayList<DFANode> dfa;
	
	/**
	 * 与NFA类似
	 * @author earayu
	 */
	class DFANode
	{
		String name = null;//del?不同于NFA的状态名，DFA的状态名并没有什么卵用
		ArrayList<NFANode> dstates;
		ArrayList<Character> edge;
		ArrayList<DFANode> desNode;
		boolean start;
		boolean end;
		
		DFANode(ArrayList<NFANode> ds)
		{
			dstates = new ArrayList<>();
			edge = new ArrayList<>();
			desNode = new ArrayList<>();
			dstates.addAll(ds);
			start = false;
			end = false;
		}
		
		public void addEdge(Character s, DFANode n)
		{
			edge.add(s);
			desNode.add(n);
		}
		
		public boolean hasPath(Character s)
		{
			for(Character x:edge)
			{
				if(x.equals(s))
					return true;
			}
			return false;
		}
	}
	
	public DFA()
	{
		dfa = new ArrayList<>();
	}
	
	//从NFA转DFA。关键代码。实现了子集构造法
//	public DFA(NFA nfa)
//	{	//计算开始节点的闭包
//		NFANode s0 = nfa.getStart();
//		
//		ArrayList<ArrayList<NFANode>> node = new ArrayList<>();
//		node.add(Utils.eClosure(s0));
//		
//		ArrayList<ArrayList<NFANode>> result = new ArrayList<>();
//		Stack<ArrayList<NFANode>> Dstates = new Stack<>();
//		Dstates.push(Utils.eClosure(s0));
//		
//		while(!Dstates.isEmpty())
//		{
//			ArrayList<NFANode> currentNode = Dstates.pop();
//			//为DFA添加一个状态
//			result.add(currentNode);
//			
//			for(Character c:Utils.alphetbet.toCharArray())
//			{
//				ArrayList<NFANode> T = Utils.eClosure(Utils.move(currentNode,c));
//				if(Utils.inResult(result, T)==false)
//				{
//					Dstates.push(T);
//					node.add(T);
//				}
//				result.add(T);
//			}
//		}
//		
//		
//		DFA dfa = new DFA();
//		for(ArrayList<NFANode> T:result)
//			dfa.addNodeToDFA(T);
//		/**
//		 * 下面这段代码是最慢的
//		 */
//		int count = 1;
//		for(int i=0;i<result.size();)
//		{
//			ArrayList<NFANode> from = result.get(i++);
//			for(Character c:Utils.alphetbet.toCharArray())
//			{
//				ArrayList<NFANode> to = result.get(i++);
//				dfa.addEdgeToState(from, c, to);
//				count++;
//				System.out.println(c);
//			}
//		}
//		System.out.println(count);
//		this.dfa = dfa.dfa;
//	}
	
	public DFA(NFA nfa)
	{	
		DFA dfa = new DFA();
		//计算开始节点的闭包
		NFANode s0 = nfa.getStart();
		
		ArrayList<ArrayList<NFANode>> result = new ArrayList<>();
		Stack<ArrayList<NFANode>> Dstates = new Stack<>();
		Dstates.push(Utils.eClosure(s0));
		result.add(Utils.eClosure(s0));
		dfa.addNodeToDFA(Utils.eClosure(s0));
		
		while(!Dstates.isEmpty())
		{
			ArrayList<NFANode> currentNode = Dstates.pop();
			//为DFA添加一个状态
			
			
			for(Character c:Utils.alphetbet.toCharArray())
			{
				ArrayList<NFANode> T = Utils.eClosure(Utils.move(currentNode,c));
				if(!Utils.inResult(result, T))
				{
					Dstates.push(T);
					dfa.addNodeToDFA(T);
					result.add(T);
					//dfa.addEdgeToState(currentNode, c, T);
				}
				if(currentNode.size()>0 && T.size()>0)
					dfa.addEdgeToState(currentNode, c, T);
				//result.add(T);
			}
			
		}

		this.dfa = dfa.dfa;
	}

	void addNodeToDFA(ArrayList<NFANode> dstates)
	{
		if(dfa == null)
			return;
		if(getNode(dstates)==null)
		{
			DFANode node = new DFANode(dstates);
			dfa.add(node);
			for(NFANode n:dstates)
			{
				if(n.start)
					node.start = true;
				if(n.end)
					node.end = true;
			}
		}
	}
	
	void addEdgeToState(ArrayList<NFANode> from, Character edge, ArrayList<NFANode> to)
	{
		if(this.dfa == null)
			return;
		DFANode f = getNode(from);
		DFANode t = getNode(to);
		f.addEdge(edge, t);
	}
	
	public DFANode getNode(ArrayList<NFANode> node)//TODO
	{
		if(dfa == null)
			return null;
		for(DFANode elem:this.dfa)
		{
			if(elem.dstates.size()==node.size())
			{
				Set<String> s1 = new TreeSet<>();
				Set<String> s2 = new TreeSet<>();
				for(NFANode n1:elem.dstates)
					s1.add(n1.state);
				for(NFANode n2:node)
					s2.add(n2.state);
				if(s1.hashCode()==s2.hashCode())
				{
					return elem;
				}
			}
		}
		return null;
	}
	
	public boolean match(String s)
	{
		if(dfa==null)
			return false;
		DFANode startNode = null;
		for(DFANode n:dfa)
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

	public void stateSort()
	{
		for(int i=0;i<dfa.size();++i)
		{
			dfa.get(i).name = String.valueOf(i);
		}
	}
	
}
