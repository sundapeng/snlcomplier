package parse;

/**
 * 记录语法树节点其他属性
 * Attr.java
 * @author 张玉明
 *
 */
public class Attr 
{
	/*  只用到其中一个，用到时再分配内存  */
	ArrayAttr arrayAttr = null;
	ProcAttr procAttr = null;
	ExpAttr expAttr = null;
	String type_name;
}
