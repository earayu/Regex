package 词法分析;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import 词法分析.NFA.NFANode;



public class DFA {

	private HashSet<DFANode> dfa;
	
	/**
	 * 与NFA类似
	 * @author earayu
	 */
	class DFANode
	{
		String name = null;//del?不同于NFA的状态名，DFA的状态名并没有什么卵用
		HashSet<NFANode> dstates;
		ArrayList<Character> edge;
		ArrayList<DFANode> desNode;
		boolean start;
		boolean end;
		
		DFANode(HashSet<NFANode> ds)
		{
			dstates = new HashSet<>();
			edge = new ArrayList<>();
			desNode = new ArrayList<>();
			dstates.addAll(ds);
			start = false;
			end = false;
		}
		
		@Override
		public int hashCode() {
			return dstates.hashCode();
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
		dfa = new HashSet<>();
	}
	
	public DFA(NFA nfa)
	{	
		DFA dfa = new DFA();
		//计算开始节点的闭包
		NFANode s0 = nfa.getStart();
		
		HashSet<HashSet<NFANode>> result = new HashSet<>();
		Stack<HashSet<NFANode>> Dstates = new Stack<>();
		HashSet<NFANode> start = Utils.eClosure(s0);
		Dstates.push(start);
		result.add(start);
		dfa.addNodeToDFA(start);
		
		while(!Dstates.isEmpty())
		{
			HashSet<NFANode> currentNode = Dstates.pop();
			//为DFA添加一个状态
			for(char c:Utils.alphetbet1.toCharArray())
			{
				HashSet<NFANode> T = Utils.eClosure(Utils.move(currentNode,c));//2259ms -> 335ms
				if(!Utils.inResult(result, T))//6ms
				{
					Dstates.push(T);
					dfa.addNodeToDFA(T);
					result.add(T);
				}
				if(currentNode.size()>0 && T.size()>0)
					dfa.addEdgeToState(currentNode, c, T);//1100ms  ->70ms
			}
		}
		this.dfa = dfa.dfa;
	}
	
	void addNodeToDFA(HashSet<NFANode> dstates)
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
	
	
	void addEdgeToState(HashSet<NFANode> from, Character edge, HashSet<NFANode> to)
	{
		if(this.dfa == null)
			return;
		DFANode f = getNode(from);
		DFANode t = getNode(to);
		f.addEdge(edge, t);
	}
	
	
	public DFANode getNode(HashSet<NFANode> node)//TODO 这里可以再提高点效率吗？
	{
		if(dfa == null)
			return null;
		for(DFANode elem:this.dfa)
			if(elem.dstates.size()==node.size())
				if(elem.dstates.hashCode()==node.hashCode())//NFANode重写了hashCode方法，所以可以用elem.hashCode()==node.hashCode()比较
				{
					return elem;
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
	
	public static int[] getNext2(String s)
	{
		int[] next = new int[s.length()];
		for(int i=0;i<s.length();++i)
		{
			int max = 0;
			for(int j=0;j<i;++j)
			{
				if(s.substring(0, j+1).equals(s.substring(i-j, i+1)))
					if(max<j+1)
						max = j+1;
			}
			if(max==0)
				next[i] = 1;
			else next[i] =  i - max + 1;
		}
		return next;
	}
	
	public boolean contains(String s)
	{
		int index = 0;
		boolean has = false;
		if(dfa==null)
			return false;
		DFANode startNode = null;
		for(DFANode n:dfa)
			if(n.start==true)
				startNode = n;
		
		for(Character c:s.toCharArray())
		{
			index++;
			if(startNode.hasPath(c))
			{
				startNode = startNode.desNode.get(startNode.edge.indexOf(c));
				if(startNode.end==true)//非贪婪
					return true;
			}
			else
			{
				if(has)
					return contains(s.substring(Math.max(index, 1)));
				return contains(s.substring(Math.max(index-1, 1)));
			}
		}
		return false;
	}
	
	public int search(String s)
	{
		int[] count = new int[1];
		count[0] = 0;
		search(s,count);
		return count[0];
	}
	
	/*
	 * O(N)
	 */
	public boolean search(String s, int[] count)//获取index,不存在则返回-1
	{
		int index = 0;
		boolean has = false;
		if(dfa==null)
			return false;
		DFANode startNode = null;
		for(DFANode n:dfa)
			if(n.start==true)
				startNode = n;
		
		for(Character c:s.toCharArray())
		{
			index++;
			if(startNode.hasPath(c))
			{
				startNode = startNode.desNode.get(startNode.edge.indexOf(c));
				if(startNode.end==true)//非贪婪
					return true;
			}
			else
			{
				if(has)
				{
					count[0]+=Math.max(index, 1);
					return search(s.substring(Math.max(index, 1)),count);
				}
				count[0]+=Math.max(index-1, 1);
				return search(s.substring(Math.max(index-1, 1)),count);
			}
		}
		return false;
	}
}
