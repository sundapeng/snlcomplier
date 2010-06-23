package parse;

/**
 * 语法树结点的定义
 * TreeNode.java
 * @author 张玉明
 *
 */
public class TreeNode 
{
	TreeNode child[] = new TreeNode[3];			//指向子语法树节点指针，为语法树结点指针类型
	TreeNode sibling = null;     				//指向兄弟语法树节点指针，为语法树节点指针类型			
	int lineno;									//记录源程序行号
	String nodekind;							//记录语法树节点的类型，取值为ProK，PheadK,TypeK,VarK,ProcDecK,StmLK,DecK,StmtK,ExpK
	String kind;								//记录语法树节点的具体类型
	int idnum;								    //记录一个节点中的标识符的个数
	String name[] = new String[10];				//字符串数组，数组成员是节点中的标识符的名字
	Attr attr = new Attr();						//记录语法树的其他属性，为结构体类型
}
