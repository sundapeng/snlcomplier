package parse;
/**
 * 记录表达式的属性
 * ExpAttr.java
 * @author 张玉明
 *
 */
public class ExpAttr 
{
	String op;			//记录语法树节点的运算符单词，为但此类型
	int val;			//记录语法树节点的数值，当语法树节点为“数字因子“对应的语法树节点时有效，为整数类型
	String varkind;		//记录变量的类别;值有标识符常量，数组成员变量，还有域成员变量
	String type;		//记录语法树节点的检查类型
}
