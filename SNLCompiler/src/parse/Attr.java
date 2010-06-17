package parse;

/**
 * 记录语法树节点其他属性，为结构体类型
 * Attr.java
 * @author 张玉明
 *
 */
public class Attr 
{
	/*  只用到其中一个，用到时再分配内存  */
	ArrayAttr arrayAttr = null;			//记录数组类型的属性
	ProcAttr procAttr = null;			//记录过程的属性
	ExpAttr expAttr = null;				//记录表达式的属性
	String type_name;					//记录类型名，当节点为声明类型，且类型是由类型标识表示时有效
}
