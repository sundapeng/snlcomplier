package parse;

/**
 * 语法树结点的定义
 * TreeNode.java
 * @author 张玉明
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
