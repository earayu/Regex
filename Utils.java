package 词法分析;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import 词法分析.NFA.NFANode;

public class Utils {

	//注意：.*|()\这些需要转义的字符本身不能加入alphetbet。要将他们转义后的值加入！！
	//因为我们要把他们变成中缀式，所以他们本身不能是“字符”，以免在他们和字符之间加入'.'
	//例如：“a*”的中缀式就是“a*”。我们不能在a和*之间加入'.'。“a.*”是错误的。
	//而“a\\*”的中缀式为“a.”。
	public static String alphetbet = new String(" !\"#$%&'+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{}~?");
	
	/**
	 * 计算NFA中一个状态的闭包，结果（一个集合）存放在一个线性表中返回。
	 */
	public static ArrayList<NFANode> eClosure(NFANode n)
	{
		Queue<NFANode> que = new LinkedList<>();
		ArrayList<NFANode> dstates = new ArrayList<>();
		dstates.add(n);
		que.offer(n);
		while(!que.isEmpty())
		{
			NFANode node = que.poll();
			for(int i=0;i<node.edge.size();++i)
			{
				if(node.edge.get(i).equals('\0'))
				{
					//判断该元素有没有遍历过。如果没有出现在dstates中，说明该元素是第一次被访问。所以
					//要加入队列；不然不加入队列，以免发生循环。如(a*)*的情况。
					if(!addIntoIfNotHaveTheSameElement(dstates,node.desNode.get(i)))
						que.offer(node.desNode.get(i));
				}
			}
		}
		return dstates;
	}
	
	//有重复元素返回true，没有则返回false。
	/**
	 * 计算闭包可能有重复的元素。而我们需要的结果是一个集合（集合3要素：确定性，互异性，无序性）
	 * 所以我们需要有一个判断某个元素是否在集合中的方法。
	 */
	public static boolean addIntoIfNotHaveTheSameElement(ArrayList<NFANode> result, NFANode r)
	{
		boolean flag = false;
		for(NFANode node:result)
		{
			if(node.state.equals(r.state))
				flag = true;
		}
		if(!flag)
		{
			result.add(r);
		}
		return flag;
	}
	
	/**
	 * 计算一个集合中所有状态的闭包
	 */
	public static ArrayList<NFANode> eClosure(ArrayList<NFANode> T)
	{
		ArrayList<NFANode> result = new ArrayList<>();
		for(NFANode node:T)
		{
			//对T中的每一个节点都使用eClosure(NFANode n)方法
			ArrayList<NFANode> r = eClosure(node);
			//然后将节点不重复地加入result
			for(NFANode n:r)
				addIntoIfNotHaveTheSameElement(result,n);
		}
		return result;
	}
	
	/**
	 * 根据“子集构造法”所写的方法。计算一个集合内的状态输入某个字符后的结果。
	 */
	public static ArrayList<NFANode> move(ArrayList<NFANode> T, Character s)
	{
		ArrayList<NFANode> dstates = new ArrayList<>();
		
		for(NFANode node:T)
			for(int i=0;i<node.edge.size();++i)
				if(node.edge.get(i).equals(s))
					dstates.add(node.desNode.get(i));
		
		return dstates;
	}
	
	/**
	 * 判断某一状态集是否“出现”过。若是第一次出现，则返回false。
	 */
	public static boolean inResult(ArrayList<ArrayList<NFANode>> result, ArrayList<NFANode> T)
	{
		for(ArrayList<NFANode> elem:result)
		{
			if(elem.size()==T.size())
			{
				Set<String> s1 = new TreeSet<>();
				Set<String> s2 = new TreeSet<>();
				for(NFANode n1:elem)
					s1.add(n1.state);
				for(NFANode n2:T)
					s2.add(n2.state);
				if(s1.hashCode()==s2.hashCode())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	public static void print(ArrayList<NFANode> T)
	{
		for(NFANode n:T)
		{
			System.out.print(n.state + " ");
		}
		System.out.print("\t| \t");
	}
}
