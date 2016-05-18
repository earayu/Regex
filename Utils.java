package 词法分析;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import 词法分析.NFA.NFANode;


public class Utils {

	//注意：.*|()\这些需要转义的字符本身不能加入alphetbet。要将他们转义后的值加入！！
	//因为我们要把他们变成中缀式，所以他们本身不能是“字符”，以免在他们和字符之间加入'.'
	//例如：“a*”的中缀式就是“a*”。我们不能在a和*之间加入'.'。“a.*”是错误的。
	//而“a\\*”的中缀式为“a.”。  ‘’的值为‘\’在RE类中escape字符串中的索引位置.
	public static String alphetbet = (char)10+(char)13+ new String(" 	!\"#$%&',-/0123456789:;<=>@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{}~");
	public static String alphetbet1 = new String(" !\"#$%&'+,-/0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{}~?");
	public static char[] alphetbet2 = {' ','!','\"','#','$','%','&','\'','+',',','-','/','0','1','2','3','4','5','6','7','8','9',':',';','<','=','>','?','@','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','[',']','^','_','`','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','{','}','~','','?','','','','','','',};
	
	
	
	/**
	 * 计算NFA中一个状态的闭包，结果（一个集合）存放在一个线性表中返回。
	 */
	public static HashSet<NFANode> eClosure(NFANode n)
	{
		Queue<NFANode> que = new LinkedList<>();
		HashSet<NFANode> dstates = new HashSet<>();
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
					if(!dstates.contains(node.desNode.get(i)))
					{
						dstates.add(node.desNode.get(i));
						que.offer(node.desNode.get(i));
					}
				}
			}
		}
		return dstates;
	}
	
	
	/**
	 * 计算一个集合中所有状态的闭包
	 */
	public static HashSet<NFANode> eClosure(HashSet<NFANode> T)//600ms
	{
		
		HashSet<NFANode> result = new HashSet<>();
		for(NFANode node:T)
		{
			//对T中的每一个节点都使用eClosure(NFANode n)方法
			HashSet<NFANode> r = eClosure(node);
			//然后将节点不重复地加入result
			result.addAll(r);
		}
		
		return result;
	}
	/**
	 * 根据“子集构造法”所写的方法。计算一个集合内的状态输入某个字符后的结果。
	 */
	public static HashSet<NFANode> move(HashSet<NFANode> T, Character s)
	{
		
		HashSet<NFANode> dstates = new HashSet<>();
		
		for(NFANode node:T)
			for(int i=0;i<node.edge.size();++i)//
				if(node.edge.get(i).equals(s))
					dstates.add(node.desNode.get(i));
		
		return dstates;
	}

	
	public static boolean inResult(HashSet<HashSet<NFANode>> result, HashSet<NFANode> T)
	{
		return result.contains(T);
	}
	
}
