package parse;

/**
 * �﷨�����Ķ���
 * TreeNode.java
 * @author ������
 *
 */
public class TreeNode 
{
	TreeNode child[] = new TreeNode[3];
	TreeNode sibling = null;
	int lineno;
	String nodekind;
	String kind;
	int idnum;
	String name[] = new String[10];
	Attr attr = new Attr();
}
