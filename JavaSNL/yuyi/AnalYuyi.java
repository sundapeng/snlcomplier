package yuyi;
import java.util.*;
import java.io.*;
import java.awt.*;

/******************************************/
class SymbTable  /* 在语义分析时用到 */
{
    String idName;
    AttributeIR  attrIR=new AttributeIR();
    SymbTable next=null;
}
class AttributeIR
{
    TypeIR  idtype=new TypeIR();
    String kind;	
    Var var;
    Proc proc;
}
class Var
{
    String access;
    int level;
    int off;
    boolean isParam;
}
class Proc
{
    int level;
    ParamTable param;
    int mOff;
    int nOff;
    int procEntry;
    int codeEntry;
}
class ParamTable
{
    SymbTable entry=new SymbTable();
    ParamTable next=null;
}
class TypeIR
{
    int size;
    String kind;
    Array array;
    FieldChain body;
}
class Array
{
    TypeIR indexTy=new TypeIR();
    TypeIR elementTy=new TypeIR();
    int low;
    int up;
}
class FieldChain
{
    String id;
    int off;
    TypeIR unitType=new TypeIR();
    FieldChain next=null;
}
/*******************************************/
class TreeNode   /* 语法树结点的定义 */
{
    TreeNode child[]=new TreeNode[3];
    TreeNode sibling=null;
    int lineno;
    String nodekind;
    String kind;
    int idnum;
    String name[]=new String[10];
    SymbTable table[]=new SymbTable[10];
    Attr attr=new Attr();
}   
class Attr
{
    ArrayAttr arrayAttr=null;  /* 只用到其中一个，用到时再分配内存 */
    ProcAttr procAttr=null;
    ExpAttr expAttr=null;
    String type_name=null;
}
class ArrayAttr
{
    int low;
    int up;
    String childtype;
}
class ProcAttr
{
    String paramt;
}
class ExpAttr
{
    String op;
    int val;
    String varkind;
    String type;
}
/************************************************/
class TokenType       /****Token序列的定义*******/
{
    int lineshow;
    String Lex;
    String Sem;
} 

/********************************************************************/
/* 类  名 AnalYuyi	                                            */
/* 功  能 总程序的处理					            */
/* 说  明 建立一个类，处理总程序                                    */
/********************************************************************/
public class AnalYuyi
{
/* SCOPESIZE为符号表scope栈的大小*/
int SCOPESIZE = 1000;

/*scope栈*/
SymbTable scope[]=new SymbTable[SCOPESIZE];

/*记录当前层数*/
int  Level=-1;
/*记录当前偏移；*/
int  Off;
/*记录主程序的displayOff*/
int mainOff;
/*记录当前层的displayOff*/
int savedOff;

/*注：域成员的偏移量从0开始。*/
int fieldOff = 0;

/*记录主程序display表的偏移量*/
int StoreNoff;

/*根据目标代码生成需要，initOff应为AR首地址sp到形参变量区的偏移7*/	
int initOff=7;

/*分别指向整型，字符型，bool类型的内部表示*/
TypeIR  intptr = new TypeIR();
TypeIR  charptr = new TypeIR();
TypeIR  boolptr = new TypeIR();

/*错误追踪标志*/
public boolean Error=false;
public boolean Error1=false;
public String ytable=" ";
public String yerror;
public String serror;
public TreeNode yuyiTree;

public AnalYuyi(String s)
{
    Recursion r=new Recursion(s);
    Error1=r.Error;
    if (Error1)
        serror=r.serror;
    else
    {
        yuyiTree=r.yufaTree;
        Analyze(yuyiTree);
    }
}
/****************************************************/
/****************************************************/
/****************************************************/
/* 函数名  Analyze				    */
/* 功  能  语义分析主函数        		    */
/* 说  明  从语法树的根节点开始，进行语义分析       */	
/****************************************************/
void Analyze(TreeNode t)	
{ 
    TreeNode p = null;
    TreeNode pp = t;

    /*建立一个新的符号表，开始语义分析*/
    CreatSymbTable();

    /*调用类型内部表示初始化函数*/
    initiate();

    /*语法树的声明节点*/
    p=t.child[1];
    while (p!=null) 
    {
        if(p.nodekind.equals("TypeK") ) 
	    TypeDecPart(p.child[0]); 
        else if(p.nodekind.equals("VarK") )  
	    VarDecPart(p.child[0]);  
        else if(p.nodekind.equals("ProcDecK") )  
            procDecPart(p);       
	else
	    AnalyzeError(t,"no this node kind in syntax tree!",null);
        p = p.sibling ;/*循环处理*/
     }
	
    /*程序体*/
    t = t.child[2];
    if(t.nodekind.equals("StmLK"))
	BodyA(t);
	
    /*撤销符号表*/
    if (Level!=-1)
        DestroySymbTable();
	
    /*输出语义错误*/
    if(Error)
	AnalyzeError(null," Analyze Error ",null);
} 

/****************************************************/
/* 函数名  TypeDecPart				    */
/* 功  能  处理一个类型声明     		    */
/* 说  明  根据语法树中的类型声明节点，取相应内容， */
/*	   将类型标识符添入符号表.	            */
/****************************************************/            
void TypeDecPart(TreeNode t)
{ 
    boolean present=false;
    AttributeIR Attrib=new AttributeIR();  /*存储当前标识符的属性*/
    SymbTable entry = new SymbTable();

    Attrib.kind="typekind";  
    while (t!=null)   
    {
	/*调用记录属性函数，返回是否重复声明错和入口地址*/
	present = Enter(t.name[0],Attrib,entry);	
	
	if (present)
	{
	    AnalyzeError(t," id repeat declaration ",t.name[0]); 
	    entry = null;
	}
	else 
	    entry.attrIR.idtype = TYPEA(t,t.kind);
	t = t.sibling;
    }  
}
/****************************************************/
/* 函数名  TYPEA 				    */
/* 功  能  建立类型的内部表示     		    */
/* 说  明  调用具体类型处理完成类型内部表示的构造   */
/*	   返回指向类型内部表示的指针.	            */
/****************************************************/   
TypeIR TYPEA(TreeNode t,String kind)
{ 
    TypeIR typeptr=null;
	
    /*根据不同类型信息，调用相应的类型处理函数*/
    if (kind.equals("IdK")) 
	typeptr= NameTYPEA(t);
    else if (kind.equals("IntegerK"))  
        typeptr= intptr;                                        
    else if (kind.equals("CharK"))  
	typeptr= charptr;                                      
    else if (kind.equals("ArrayK"))    
        typeptr= ArrayTYPEA(t); 
    else if (kind.equals("RecordK"))  
        typeptr= RecordTYPEA(t); 
    else
    { 
        AnalyzeError(t,"bug: no this type in syntax tree ",null);
        return null;
    }	
    return typeptr;
}
/****************************************************/
/* 函数名  NameTYPEA				    */
/* 功  能  处理类型为类型标识符时的情形	            */
/* 说  明  不构造新的类型，返回此类型标识符的类型， */
/*	   并检查语义错误                           */
/****************************************************/  
TypeIR NameTYPEA(TreeNode t)
{  
    SymbTable Entry=new SymbTable();                     
    TypeIR temp=null;
    boolean present;

    present= FindEntry(t.attr.type_name,Entry);
    /*检查类型标识符未声明错*/
    if (!present)
	AnalyzeError(t," id use before declaration ",t.attr.type_name);
    /*检查非类型标识符错*/  
    else if (!(Entry.attrIR.kind.equals("typekind")))
	AnalyzeError(t," id is not type id ",t.attr.type_name);
    /*返回标识符的类型的内部表示*/
    else
    {  
        temp= Entry.attrIR.idtype;
        return temp;
    }
    return temp;
}	 
/****************************************************/
/* 函数名  ArrayTypeA				    */
/* 功  能  构造数组类型的内部表示   		    */
/* 说  明  处理下标类型，成员类型，计算数组大小，   */
/*	   并检查下标超界错误		            */
/****************************************************/   
TypeIR ArrayTYPEA(TreeNode t)
{ 
    TypeIR tempforchild;

    /*建立一个新的数组类型的内部表示*/
    TypeIR typeptr=new TypeIR();
    typeptr.array=new Array();
    typeptr.kind="arrayTy";
    /*下标类型是整数类型*/
    typeptr.array.indexTy=intptr;                         
    /*成员类型*/
    tempforchild=TYPEA(t,t.attr.arrayAttr.childtype);
    typeptr.array.elementTy=tempforchild;

    /*检查数组下标出界错误*/
    int up=t.attr.arrayAttr.up;
    int low=t.attr.arrayAttr.low;
    if (up < low)
	AnalyzeError(t," array up smaller than under ",null);
    else  /*上下界计入数组类型内部表示中*/
    {       
        typeptr.array.low = low;
        typeptr.array.up = up;
    }
    /*计算数组的大小*/
    typeptr.size= (up-low+1)*(tempforchild.size);
    /*返回数组的内部表示*/
    return typeptr;
}
/****************************************************/
/* 函数名  RecordTYPEA				    */
/* 功  能  构造记录类型的内部表示   		    */
/* 说  明  构造域表，指针存储在记录的内部表示中，   */
/*	   并计算记录的大小                         */  
/****************************************************/
TypeIR RecordTYPEA(TreeNode t)
{ 
    TypeIR Ptr=new TypeIR();  /*新建记录类型的节点*/
    Ptr.body=new FieldChain();
    Ptr.kind="recordTy";
	
    t = t.child[0];                /*从语法数的儿子节点读取域信息*/

    FieldChain Ptr2=null;
    FieldChain Ptr1=null;
    FieldChain body=null;

    while (t != null)				/*循环处理*/
    {
	/*填写ptr2指向的内容节点*
	 *此处循环是处理此种情况int a,b; */
	for(int i=0 ; i < t.idnum ; i++)
	{     
	    /*申请新的域类型单元结构Ptr2*/  
	    Ptr2 = new FieldChain();            
	    if(body == null)
            {
		body = Ptr2; 
                Ptr1 = Ptr2;
	    }
	    /*填写Ptr2的各个成员内容*/
	    Ptr2.id=t.name[i];
	    Ptr2.unitType = TYPEA(t,t.kind);			 
	    
	    /*如果Ptr1!=Ptr2，那么将指针后移*/
	    if(Ptr2 != Ptr1)          
	    {
		/*计算新申请的单元off*/
		Ptr2.off = (Ptr1.off) + (Ptr1.unitType.size);
		Ptr1.next = Ptr2;
		Ptr1 = Ptr2;
	    }
	}
	/*处理完同类型的变量后，取语法树的兄弟节点*/
	t = t.sibling;
    }	
    /*处理记录类型内部结构*/
	
    /*取Ptr2的off为最后整个记录的size*/
    Ptr.size = Ptr2.off + (Ptr2.unitType.size);
    /*将域链链入记录类型的body部分*/   
    Fcopy(Ptr.body,body);   

    return Ptr;
}
/****************************************************/
/* 函数名  VarDecPart				    */
/* 功  能  变量声明序列分析函数    		    */
/* 说  明  处理所有的变量声明			    */
/****************************************************/
void VarDecPart(TreeNode t) 
{  
    varDecList(t);
}   
/****************************************************/
/* 函数名  varDecList 				    */
/* 功  能  处理一个变量声明或形参声明		    */
/* 说  明  处理一个声明节点中声明的所有标识符，	    */	
/*	   将相关信息添入符号表中，若是形参，还要   */
/*	   构造一个参数信息表，各个标识符在符号表的 */
/*	   位置存储在表中，返回参数表的表头指针     */					
/****************************************************/
void varDecList(TreeNode t)
{ 
    boolean present = false;
    SymbTable  Entry=new SymbTable();
    /*纪录变量的属性*/
    AttributeIR Attrib=new AttributeIR();

    while(t!=null)	/*循环过程*/
    {
	Attrib.kind="varkind";  
	for(int i=0;i<(t.idnum);i++)
	{
	    Attrib.idtype=TYPEA(t,t.kind);
			
	    /*判断识值参还是变参acess(dir,indir)*/	
	    if((t.attr.procAttr!=null)&&(t.attr.procAttr.paramt.equals("varparamType")))
	    {
                Attrib.var = new Var();
		Attrib.var.access = "indir";
		Attrib.var.level = Level;
		/*计算形参的偏移*/
				
		Attrib.var.off = Off;
		Off = Off+1;
	    }/*如果是变参，则偏移加1*/
	    else
	    {
                Attrib.var = new Var();
		Attrib.var.access = "dir";
		Attrib.var.level = Level;
		/*计算值参的偏移*/
		if(Attrib.idtype.size!=0)				
		{
		    Attrib.var.off = Off;
		    Off = Off + (Attrib.idtype.size);
		}
	    }/*其他情况均为值参，偏移加变量类型的size*/
			
	    /*登记该变量的属性及名字,并返回其类型内部指针*/
	    present = Enter(t.name[i],Attrib,Entry);	
	    if(present)
	        AnalyzeError(t," id repeat  declaration ",t.name[0]);
	    else
	        t.table[i] = Entry;
	}
	if(t!=null)
	    t = t.sibling;
    }
	
    /*如果是主程序，则记录此时偏移，用于目标代码生成时的displayOff*/
    if(Level==0)
    {
	mainOff = Off;
	/*存储主程序AR的display表的偏移到全局变量*/
	StoreNoff = Off;
    }
    /*如果不是主程序，则记录此时偏移，用于下面填写过程信息表的noff信息*/ 
    else 
	savedOff = Off;
} 
/****************************************************/
/* 函数名  procDecPart				    */
/* 功  能  一个过程声明的语义分析  		    */
/* 说  明  处理过程头，声明，过程体		    */	
/****************************************************/
void procDecPart(TreeNode t)
{ 
    TreeNode p =t;
    SymbTable entry = HeadProcess(t);   /*处理过程头*/
		
    t = t.child[1];
    /*如果过程内部存在声明部分，则处理声明部分*/	
    while (t!=null) 
    {
	if ( t.nodekind.equals("TypeK") ) 
	    TypeDecPart(t.child[0]); 
        else if ( t.nodekind.equals("VarK") )  
            VarDecPart(t.child[0]);  

	/*如果声明部分有函数声明，则跳出循环，先填写noff和moff等信息，*
	*再处理函数声明的循环处理，否则无法保存noff和moff的值。      */
	else if ( t.nodekind.equals("ProcDecK") )  {}
	else
	    AnalyzeError(t,"no this node kind in syntax tree!",null);
				
	if(t.nodekind.equals("ProcDecK"))
            break;
	else
            t=t.sibling ;
    }
    entry.attrIR.proc.nOff = savedOff;
    entry.attrIR.proc.mOff = entry.attrIR.proc.nOff + entry.attrIR.proc.level+1;
    /*过程活动记录的长度等于nOff加上display表的长度*
    *diplay表的长度等于过程所在层数加一           */

    /*处理程序的声明部分*/
    while(t!=null)
    {
	procDecPart(t);
	t = t.sibling;
    }
    t = p;
    BodyA(t.child[2]);/*处理Block*/

    /*函数部分结束，删除进入形参时，新建立的符号表*/
    if ( Level!=-1)
	DestroySymbTable();/*结束当前scope*/
}
/****************************************************/
/* 函数名  HeadProcess				    */
/* 功  能  形参处理函数         		    */
/* 说  明  循环处理各个节点，并将处理每个节点得到   */
/*	   的参数表连接起来，组成整个形参链表，返回 */
/*         这个表的指针				    */	
/****************************************************/
SymbTable HeadProcess(TreeNode t)
{ 
    AttributeIR attrIr = new AttributeIR();
    boolean present = false;
    SymbTable entry = new SymbTable();
		
    /*填属性*/
    attrIr.kind = "prockind";
    attrIr.idtype = null; 
    attrIr.proc = new Proc();
    attrIr.proc.param = new ParamTable();
    attrIr.proc.level = Level+1;	
	
    if(t!=null)
    {
	/*登记函数的符号表项*/		
	present = Enter(t.name[0],attrIr,entry);
	t.table[0] = entry;
	/*处理形参声明表*/
    }
    entry.attrIR.proc.param = ParaDecList(t);

    return entry;
}   
/****************************************************/
/* 函数名  ParaDecList				    */
/* 功  能  处理一个形参节点        		    */
/* 说  明  根据参数是形参还是变参，分别调用变量声明 */
/*	   节点的处理函数，另一个实参是Add，表示处理*/
/*	   的是函数的形参。			    */	
/****************************************************/
ParamTable ParaDecList(TreeNode t)
{ 
    TreeNode p=null;
    ParamTable Ptr1=null; 
    ParamTable Ptr2=null;
    ParamTable head=null;
	
    if(t!=null)
    {
	if(t.child[0]!=null)
	    p = t.child[0];   	/*程序声明节点的第一个儿子节点*/
	
	CreatSymbTable();		/*进入新的局部化区*/
	Off = 7;                /*子程序中的变量初始偏移设为8*/

	VarDecPart(p);			/*变量声明部分*/

	SymbTable Ptr0 = scope[Level];      		 
                                    
	while(Ptr0 != null)         /*只要不为空，就访问其兄弟节点*/
	{
	    /*构造形参符号表，并使其连接至符号表的param项*/
	    Ptr2 = new ParamTable();
	    if(head == null)
            {
		head = Ptr2;
                Ptr1 = Ptr2;
            }
	    //Ptr0.attrIR.var.isParam = true;
	    copy(Ptr2.entry,Ptr0);
			
	    if(Ptr2 != Ptr1)          
	    {
		Ptr1.next = Ptr2;
		Ptr1 = Ptr2;
	    }
	    Ptr0 = Ptr0.next;
	}
    }
    return head;   /*返回形参符号表的头指针*/
}
/****************************************************/
/* 函数名  BodyA				    */
/* 功  能  语句序列处理函数        		    */
/* 说  明  用于处理过程体或者程序体，		    */
/*	  循环处理各个语句			    */	
/****************************************************/
void BodyA(TreeNode t)
{  
    /*令指针指向第一条语句*/
    if (t.nodekind.equals("StmLK"))
	t=t.child[0];

    /*处理语句序列*/
    while (t!=null)
    { 
        /*调用语句处理函数*/
	StatementA(t);
        t= t.sibling;
    }
}
/****************************************************/
/* 函数名  StatementA				    */
/* 功  能  语句处理函数	        		    */
/* 说  明  根据语句的具体类型，分别调用相应的       */
/*	   语句处理函数				    */
/****************************************************/
void StatementA(TreeNode t) 
{  
    if (t.kind.equals("AssignK"))
	AssignSA(t);  
    else if (t.kind.equals("CallK"))      
        CallSA(t);   
    else if (t.kind.equals("ReadK"))     
        ReadSA(t);    
    else if (t.kind.equals("WriteK"))     
        WriteSA(t);	 
    else if (t.kind.equals("IfK"))     
        IfSA(t);	  
    else if (t.kind.equals("WhileK"))	
        WhileSA(t);	  
    else if (t.kind.equals("ReturnK")) 	
        ReturnSA(t);  
    else
        AnalyzeError(t," bug:no this statement in syntax tree ",null);	
}
/****************************************************/
/* 函数名  AssignSA				    */
/* 功  能  赋值语句处理函数	       		    */
/* 说  明  检查左部标识符，调用表达式处理函数，	    */	
/*	   并检查标识符未声明错，非期望标识符错，   */
/*	   赋值不兼容错				    */
/****************************************************/
void AssignSA(TreeNode t)
{ 
    SymbTable entry = new SymbTable();
	
    boolean present = false;
    TypeIR ptr = null;
    TypeIR Eptr = null;
	
    TreeNode child1;
    TreeNode child2;

    child1 = t.child[0];
    child2 = t.child[1];

    if(child1.child[0]==null)
    {	
	/*在符号表中查找此标识符*/
	present = FindEntry(child1.name[0],entry);
		
	if(present)
	{   /*id不是变量*/
            if (!(entry.attrIR.kind.equals("varkind")))
	    {				
                AnalyzeError(t," left and right is not compatible in assign ",null);				                      Eptr = null;
	    }
	    else
            {
	        Eptr = entry.attrIR.idtype;
		child1.table[0] = entry;
            }
	} 
	else /*标识符无声明*/
	    AnalyzeError(t,"is not declarations!",child1.name[0]);
	}
	else/*Var0[E]的情形*/
	{	
            if(child1.attr.expAttr.varkind.equals("ArrayMembV"))
		Eptr = arrayVar(child1);	
	    else /*Var0.id的情形*/
		if(child1.attr.expAttr.varkind.equals("FieldMembV"))
		    Eptr = recordVar(child1);
	}
	if(Eptr != null)
	{	
	    if((t.nodekind.equals("StmtK"))&&(t.kind.equals("AssignK")))
	    {
		/*检查是不是赋值号两侧 类型等价*/
		ptr = Expr(child2,null);
		if (!Compat(ptr,Eptr)) 
		    AnalyzeError(t,"ass_expression error!",child2.name[0]);
	    }
	    /*赋值语句中不能出现函数调用*/
	}
}
/***********************************************************/
/* 函数名 Compat                                           */
/* 功  能 判断类型是否相容                                 */
/* 说  明 由于TINY语言中只有整数类型、字符类型、数组类型和 */
/*        记录类型，故类型相容等于类型等价，只需判断每个结 */
/*        构类型的内部表示产生的指针值是否相同即可。       */
/***********************************************************/
boolean Compat(TypeIR tp1,TypeIR tp2)
{
    boolean  present; 
    if (tp1!=tp2)
	present = false;  /*类型不等*/
    else
	present = true;   /*类型等价*/
    return present;
}

/************************************************************/
/* 函数名  Expr                                             */
/* 功  能  该函数处理表达式的分析                           */
/* 说  明  表达式语义分析的重点是检查运算分量的类型相容性， */
/*         求表达式的类型。其中参数Ekind用来表示实参是变参  */
/*         还是值参。    	                            */
/************************************************************/
TypeIR Expr(TreeNode t,String Ekind)
{
    boolean present = false;
    SymbTable entry = new SymbTable();

    TypeIR Eptr0=null;
    TypeIR Eptr1=null;
    TypeIR Eptr = null;
    if(t!=null)
    {
        if(t.kind.equals("ConstK"))
        { 
	    Eptr = intptr;
	    Eptr.kind = "intTy";
	    if(Ekind!=null)
	        Ekind = "dir";   /*直接变量*/ 
        }
        else if(t.kind.equals("VariK"))
        {
	    /*Var = id的情形*/
	    if(t.child[0]==null)
	    {	
		/*在符号表中查找此标识符*/
		present = FindEntry(t.name[0],entry);				
		t.table[0] = entry;

		if(present)
		{   /*id不是变量*/
		    if (!(entry.attrIR.kind.equals("varkind")))
		    {
			AnalyzeError(t," syntax bug: no this kind of exp ",t.name[0]);				                              Eptr = null;
		    }
		    else
		    {
			Eptr = entry.attrIR.idtype;	
			if (Ekind!=null)
			    Ekind = "indir";  /*间接变量*/						
		    }
		} 
		else /*标识符无声明*/
		    AnalyzeError(t,"is not declarations!",t.name[0]);				
	    }
	    else/*Var = Var0[E]的情形*/
	    {	
                if(t.attr.expAttr.varkind.equals("ArrayMembV"))
		    Eptr = arrayVar(t);	
		/*Var = Var0.id的情形*/
		else if(t.attr.expAttr.varkind.equals("FieldMembV"))
		    Eptr = recordVar(t);
	    }
	}
        else if(t.kind.equals("OpK"))
        {
	    /*递归调用儿子节点*/
	    Eptr0 = Expr(t.child[0],null);
	    if(Eptr0==null)
	        return null;
	    Eptr1 = Expr(t.child[1],null);
	    if(Eptr1==null)
		return null;
							
	    /*类型判别*/
	    present = Compat(Eptr0,Eptr1);
	    if (present)
	    {
		if((t.attr.expAttr.op.equals("LT"))||(t.attr.expAttr.op.equals("EQ")))
		    Eptr = boolptr;
                else if((t.attr.expAttr.op.equals("PLUS"))||(t.attr.expAttr.op.equals("MINUS"))||(t.attr.expAttr.op.equals("TIMES"))||(t.attr.expAttr.op.equals("OVER")))  
		    Eptr = intptr;
                                /*算数表达式*/
		if(Ekind != null)
	            Ekind = "dir"; /*直接变量*/
	    }
	    else 
		AnalyzeError(t,"operator is not compat!",null);
	}
    }
    return Eptr;
}			

/************************************************************/
/* 函数名  arrayVar                                         */
/* 功  能  该函数处理数组变量的下标分析                     */
/* 说  明  检查var := var0[E]中var0是不是数组类型变量，E是不*/
/*         是和数组的下标变量类型匹配。                     */
/************************************************************/
TypeIR arrayVar(TreeNode t)
{
    boolean present = false;
    SymbTable entry = new SymbTable();

    TypeIR Eptr0=null;
    TypeIR Eptr1=null;
    TypeIR Eptr = null;
	
	
    /*在符号表中查找此标识符*/

    present = FindEntry(t.name[0],entry);				
    t.table[0] = entry;	
    /*找到*/
    if(present)
    {
	/*Var0不是变量*/
	if (!(entry.attrIR.kind.equals("varkind")))
	{
	    AnalyzeError(t,"is not variable error!",t.name[0]);			
	    Eptr = null;
	}
	/*Var0不是数组类型变量*/
	else if(entry.attrIR.idtype!=null)
        {
	    if(!(entry.attrIR.idtype.kind.equals("arrayTy")))
	    {
		AnalyzeError(t,"is not array variable error !",t.name[0]);
		Eptr = null;
	    }
	    else
	    {	
		/*检查E的类型是否与下标类型相符*/
		Eptr0 = entry.attrIR.idtype.array.indexTy;
		if(Eptr0==null)
		    return null;
		Eptr1 = Expr(t.child[0],null);//intPtr;
		if(Eptr1==null)
		    return null;
		present = Compat(Eptr0,Eptr1);
		if(!present)
		{
		    AnalyzeError(t,"type is not matched with the array member error !",null);
		    Eptr = null;
		}
		else
		    Eptr = entry.attrIR.idtype.array.elementTy;
	    }
        }
    }
    else/*标识符无声明*/
	AnalyzeError(t,"is not declarations!",t.name[0]);
    return Eptr;
}

/************************************************************/
/* 函数名  recordVar                                        */
/* 功  能  该函数处理记录变量中域的分析                     */
/* 说  明  检查var:=var0.id中的var0是不是记录类型变量，id是 */
/*         不是该记录类型中的域成员。                       */
/************************************************************/
TypeIR recordVar(TreeNode t)
{
	boolean present = false;
	boolean result = false;
	SymbTable entry = new SymbTable();

	TypeIR Eptr0=null;
	TypeIR Eptr1=null;
	TypeIR Eptr = null;
	FieldChain currentP = new FieldChain();
	
	
	/*在符号表中查找此标识符*/
	present = FindEntry(t.name[0],entry);				
	t.table[0] = entry;	
	/*找到*/
	if(present)
	{
	    /*Var0不是变量*/
	    if (!(entry.attrIR.kind.equals("varkind")))
	    {
		AnalyzeError(t,"is not variable error!",t.name[0]);				
		Eptr = null;
	    }
	    /*Var0不是记录类型变量*/
	    else if(!(entry.attrIR.idtype.kind.equals("recordTy")))
	    {
		AnalyzeError(t,"is not record variable error!",t.name[0]);
		Eptr = null;
	    }
	    else/*检查id是否是合法域名*/
	    {
		Eptr0 = entry.attrIR.idtype;
		currentP = Eptr0.body;
		while((currentP!=null)&&(!result))
		{  
        	    result = t.child[0].name[0].equals(currentP.id);
		    /*如果相等*/
		    if(result)
			Eptr = currentP.unitType;
		    else
			currentP = currentP.next;
		}	 
		if(currentP==null)
		    if(!result)
		    {
		        AnalyzeError(t,"is not field type!",t.child[0].name[0]);    				                        Eptr = null;
		    }
	            /*如果id是数组变量*/
		    else if(t.child[0].child[0]!=null)
			Eptr = arrayVar(t.child[0]);
	    }
	}
	else/*标识符无声明*/
	    AnalyzeError(t,"is not declarations!",t.name[0]);
	return Eptr;
}
		
/****************************************************/
/* 函数名  CallSA				    */
/* 功  能  函数调用语句处理函数    		    */
/* 说  明  检查非函数标识符错，调用检查形实参是否   */	
/*		   相容函数			    */
/****************************************************/
void CallSA(TreeNode t)
{ 
	String Ekind=" ";
	boolean present = false;
	SymbTable entry=new SymbTable();
	TreeNode p = null;

	/*用id检查整个符号表*/
	present = FindEntry(t.child[0].name[0],entry);		
        t.child[0].table[0] = entry;

	/*未查到表示函数无声明*/
	if (!present)                     
	    AnalyzeError(t,"function is not declarationed!",t.child[0].name[0]);  
        else 
	    /*id不是函数名*/
	    if (!(entry.attrIR.kind.equals("prockind")))     
		AnalyzeError(t,"is not function name!",t.child[0].name[0]);
	    else/*形实参匹配*/
	    {
		p = t.child[1];
		/*paramP指向形参符号表的表头*/
		ParamTable paramP = entry.attrIR.proc.param;	
		while((p!=null)&&(paramP!=null))
		{
		    SymbTable paraEntry = paramP.entry;
		    TypeIR Etp = Expr(p,Ekind);/*实参*/
		    /*参数类别不匹配*/
		    if ((paraEntry.attrIR.var.access.equals("indir"))&&(Ekind.equals("dir")))
			AnalyzeError(t,"param kind is not match!",null);  
			/*参数类型不匹配*/
                    else if((paraEntry.attrIR.idtype)!=Etp)
			AnalyzeError(t,"param type is not match!",null);
		    p = p.sibling;
		    paramP = paramP.next;
		}
		/*参数个数不匹配*/
		if ((p!=null)||(paramP!=null))
		    AnalyzeError(t,"param num is not match!",null); 
	    }
}
/****************************************************/
/* 函数名  ReadSA				    */
/* 功  能  读语句处理函数	    		    */
/* 说  明  检查标识符未声明错，非变量标识符错	    */
/****************************************************/
void ReadSA(TreeNode t)
{ 
    SymbTable Entry=new SymbTable();
    boolean present=false;
    /*查找变量标识符*/
    present = FindEntry(t.name[0],Entry);
    /*变量在符号表中的地址写入语法树*/
    t.table[0] = Entry;

    if (!present)   /*检查标识符未声明错*/
	AnalyzeError(t," id no declaration in read ",t.name[0]);
    else if (!(Entry.attrIR.kind.equals("varkind")))   /*检查非变量标识符错*/ 
        AnalyzeError(t," not var id in read statement ", null);
}
/****************************************************/
/* 函数名  WriteSA				    */
/* 功  能  写语句处理函数	    		    */
/* 说  明  调用表达式处理函数，检查语义错误	    */
/****************************************************/
void WriteSA(TreeNode t)  
{ 
    TypeIR Etp = Expr(t.child[0],null);	
    if(Etp!=null)
	/*如果表达式类型为bool类型，报错*/
	if (Etp.kind.equals("boolTy"))
		AnalyzeError(t,"exprssion type error!",null);
}
/****************************************************/
/* 函数名  IfSA					    */
/* 功  能  条件语句处理函数	    		    */
/* 说  明  检查非布尔表达式错，并调用语句序列函数   */
/*	   处理then部分和 else 部分	            */	
/****************************************************/
void IfSA(TreeNode t)
{ 
    String Ekind=null;
    TypeIR Etp;
    Etp=Expr(t.child[0],Ekind);
    
    if (Etp!=null)   /*表达式没有错误*/
        if (!(Etp.kind.equals("boolTy")))   /*检查非布尔表达式错*/
	    AnalyzeError(t," not bool expression in if statement ",null);
	else
	{
	    TreeNode p = t.child[1];
	    /*处理then语句序列部分*/
	    while(p!=null)
	    {
		StatementA(p);
		p=p.sibling;
	    }
	    t = t.child[2];		/*必有三儿子*/
	    /*处理else语句部分*/
	    while(t!=null)
	    {
		StatementA(t);	
		t=t.sibling;
	    }
	}
}
/****************************************************/
/* 函数名  WhileSA				    */
/* 功  能  循环语句处理函数	    		    */
/* 说  明  检查非布尔表达式错，并调用语句序列函数   */
/*	   处理循环体			            */	
/****************************************************/
void  WhileSA(TreeNode t)
{ 
    TypeIR Etp;
    Etp=Expr(t.child[0],null);
   
    if (Etp!=null)  /*表达式没有错*/	  
        if (!(Etp.kind.equals("boolTy")))   /*检查非布尔表达式错*/
	    AnalyzeError(t," not bool expression in if statement ",null);
    /*处理循环体*/
    else
    {
	t = t.child[1];
	/*处理循环部分*/
	while(t!=null)
	{ 
	    StatementA(t);
	    t=t.sibling;
	}
    }
}
/****************************************************/
/* 函数名  ReturnSA				    */
/* 功  能  返回语句处理函数	    		    */
/* 说  明  若出现在主程序中，则语义错误		    */	
/****************************************************/
void  ReturnSA(TreeNode t)
{
    if (Level == 0)
	AnalyzeError(t," return statement cannot in main program ",null);
}

/****************************************************/
/*****************功能函数***************************/
/****************************************************/
/* 函数名  AnalyzeError				    */
/* 功  能  给出语义错误提示信息			    */
/* 说  明  Error设置为true,防止错误的传递	    */
/****************************************************/
void AnalyzeError(TreeNode t,String message,String s)
{   
    if (t==null)
        yerror=yerror+"\n>>> ERROR:"+"Analyze error "+":"+message+s+"\n"; 
    else
        yerror=yerror+"\n>>> ERROR :"+"Analyze error at "+String.valueOf(t.lineno)+": "+message+s+"\n";  

    /* 设置错误追踪标志Error为TRUE,防止错误进一步传递 */
    Error = true;
}
/****************************************************/
/* 函数名  initiate				    */
/* 功  能  建立整型，字符型，布尔类型的内部表示	    */
/* 说  明  这几个类型的内部表示式固定的，只需建立   */
/*	   一次，以后引用相应的引用即可	            */	
/****************************************************/
void initiate()
{   
    /*整数类型的内部表示*/
    intptr.kind="intTy";
    intptr.size=1;
    /*字符类型的内部表示*/
    charptr.kind="charTy";
    charptr.size=1;
    /*布尔类型的内部表示*/
    boolptr.kind="boolTy";
    boolptr.size=1;
}

/********************************************************/
/*************符号表相关函数*****************************/
/********************************************************/
/* 函数名  CreatSymbTable			        */
/* 功  能  创建一个符号表				*/
/* 说  明  并没有真正生成新的符号表，只是层数加一	*/
/********************************************************/
void  CreatSymbTable()
{ 	
    Level = Level +1; 
    scope[Level] = null;	
    Off = initOff;  /* 根据目标代码生成需要，initOff应为AR首地址sp
		       到形参变量区的偏移7 */	
}
/********************************************************/
/* 函数名  DestroySymbTable				*/
/* 功  能  删除一个符号表				*/
/* 说  明  并不真正释放这个符号表空间，只是改变scope栈  */
/********************************************************/
void  DestroySymbTable()
{
    PrintOneLayer(Level);
    /*用层数减1，来表示删除当前符号表*/
    Level = Level - 1;
}
/**********************************************************/
/* 函数名  Enter					  */
/* 功  能  将一个标识符及其属性登记到符号表		  */
/* 说  明  返回值决定标识符是否重复，由Entry带回此标识符  */
/*         在符号表中的位置，若重复，则不登记，Entry为找  */
/*	   到的那个标识符的位置				  */
/**********************************************************/
boolean Enter(String id,AttributeIR attribP,SymbTable entry)
{ 
    boolean present = false;
    boolean result = false;
    SymbTable curentry=null;
    SymbTable prentry=null;

    if(scope[Level]==null)
    {
	scope[Level] = new SymbTable();
	curentry = scope[Level];
    }
    else
    {
        curentry = scope[Level];
	while (curentry != null)
	{
	    prentry = curentry;
	    result = id.equals(curentry.idName);
	    if(result)
	    {
		AnalyzeError(null," Enter , id repeat declaration ",null);
		present = true;
	    }
	    else
		curentry = prentry.next;
	}   /*在该层符号表内检查是否有重复定义错误*/
    
	if(!present)
	{
	    prentry.next = new SymbTable();
	    curentry = prentry.next;
	}
    }
		
    /*将标识符名和属性登记到表中*/
    curentry.idName = id;

    curentry.attrIR.idtype = attribP.idtype;
    curentry.attrIR.kind = attribP.kind;
    if (attribP.kind.equals("typekind"))
	{}
    else if (attribP.kind.equals("varkind")) 
    {
        curentry.attrIR.var=new Var();
	curentry.attrIR.var.level=attribP.var.level;
	curentry.attrIR.var.off=attribP.var.off;
	curentry.attrIR.var.access=attribP.var.access;
    }
    else if (attribP.kind.equals("prockind")) 
    {
        curentry.attrIR.proc=new Proc();
	curentry.attrIR.proc.level=attribP.proc.level;
	curentry.attrIR.proc.param=attribP.proc.param;
    }
    copy(entry,curentry);
	
    return present;
}
/********************************************************/
/* 函数名  FindEntry    				*/
/* 功  能  查找一个标识符是否在符号表中			*/
/* 说  明  根据flag决定是查找当前符号表，还是所有符号表 */
/*	   返回值决定是否找到，变量Entry返回此标识符在  */
/*	   符号表中的位置				*/
/********************************************************/
boolean FindEntry(String id,SymbTable entry)
{ 
	boolean  present=false;  /*返回值*/
	boolean result = false;         /*标识符名字比较结果*/
	int lev = Level;	 /*临时记录层数的变量*/

	SymbTable findentry = scope[lev];

	while((lev!=-1)&&(!present))
	{
	    while ((findentry!=null)&&(!present))
	    {
		result = id.equals(findentry.idName);
		if (result)
		    present = true;    
		/*如果标识符名字相同，则返回TRUE*/
		else 
		    findentry = findentry.next;
		/*如果没找到，则继续链表中的查找*/
	    }
	    if(!present)
	    {
		lev = lev-1;
                if(lev!=-1)
		    findentry = scope[lev];			
	    }
	}/*如果在本层中没有查到，则转到上一个局部化区域中继续查找*/
        if (!present)
	    entry = null;
	else 
	    copy(entry,findentry);

	return present;
}
/********************************************************/
/* 函数名  copy	  				        */
/* 功  能  复制函数    				        */
/* 说  明  将b中的内容复制给a			        */
/********************************************************/
void copy(SymbTable a,SymbTable b)
{
    a.idName=b.idName;
    a.attrIR=b.attrIR;
    a.next=b.next;
}
/********************************************************/
/* 函数名  Fcopy	  				*/
/* 功  能  复制函数    				        */
/* 说  明  将b中的内容复制给a			        */
/********************************************************/
void Fcopy(FieldChain a,FieldChain b)
{
    a.id=b.id;
    a.off=b.off;
    a.unitType=b.unitType;
    a.next=b.next;
}

/********************************************************/
/* 函数名  PrintFieldChain				*/
/* 功  能  打印纪录类型的域表				*/
/* 说  明						*/
/********************************************************/
void PrintFieldChain(FieldChain currentP)
{ 
    FieldChain t=currentP;
    
    ytable=ytable+"\n--------------Field chain--------------------\n";
    while (t!=null)
    { 
                /*输出标识符名字*/
	            ytable=ytable+t.id;
	        /*输出标识符的类型信息*/	
	        if (t.unitType.kind.equals("intTy"))
	            ytable=ytable+"  intTy  ";
	        else if (t.unitType.kind.equals("charTy"))
	            ytable=ytable+"  charTy  ";   
	        else if (t.unitType.kind.equals("arrayTy"))
	            ytable=ytable+"  arrayTy  ";
	        else if (t.unitType.kind.equals("recordTy"))
	            ytable=ytable+"  recordTy  ";
	        else
      	            ytable=ytable+"error  type!  ";               
	        ytable=ytable+" off = ";
	        ytable=ytable+String.valueOf(t.off); 
	        ytable=ytable+"\n";                     	
                t = t.next;   
       }
}
/********************************************************/
/* 函数名  PrintOneLayer				*/
/* 功  能  打印符号表的一层				*/
/* 说  明  有符号表打印函数PrintSymbTable调用	        */
/********************************************************/
void PrintOneLayer(int level)
{
    SymbTable t = scope[level];

    ytable=ytable+"\n--------------SymbTable in level "+String.valueOf(level)+"--------------------\n";
    while (t!=null)
    { 
                /*输出标识符名字*/
	        ytable=ytable+t.idName;
	        AttributeIR  Attrib = t.attrIR;
	        /*输出标识符的类型信息，过程标识符除外*/
	        if (Attrib.idtype!=null)  /*过程标识符*/
                {
	            if (Attrib.idtype.kind.equals("intTy"))
	                ytable=ytable+"  intTy  ";
	            else if (Attrib.idtype.kind.equals("charTy"))
	                ytable=ytable+"  charTy  ";   
	            else if (Attrib.idtype.kind.equals("arrayTy"))
	                ytable=ytable+"  arrayTy  ";
	            else if (Attrib.idtype.kind.equals("recordTy"))
	                ytable=ytable+"  recordTy  ";
	            else
      	                ytable=ytable+"  error  type!  ";  
		}
	        /*输出标识符的类别，并根据不同类型输出不同其它属性*/
	       if (Attrib.kind.equals("typekind"))
	           ytable=ytable+"typekind  "; 
	       else if (Attrib.kind.equals("varkind"))
               {
	           ytable=ytable+"varkind  "+String.valueOf(Attrib.var.level)+"  "+String.valueOf(Attrib.var.off);
                   if (Attrib.var.access.equals("dir"))
	               ytable=ytable+"  dir  "; 
                   else if (Attrib.var.access.equals("indir"))
	               ytable=ytable+"  indir  "; 
                   else 
	               ytable=ytable+"  errorkind  "; 
               }
	       else if (Attrib.kind.equals("prockind")) 
               {
	           ytable=ytable+"  prockind  "+String.valueOf(Attrib.proc.level)+"  "+String.valueOf(Attrib.proc.nOff); 
	       }
               else 	          
                   ytable=ytable+"  error  "; 
	       ytable=ytable+"\n";  
               t = t.next;   
       }
}
/********************************************************/
/* 函数名  PrintSymbTable				*/
/* 功  能  打印生成的符号表				*/
/* 说  明						*/
/********************************************************/
void PrintSymbTable()
{ 
    /*层数从0开始*/
    int level=0;
    while (scope[level]!=null)
    { 
        PrintOneLayer(level);
        level++;
    }
}
}



/********************************************************************/
/************************语 法 分 析*********************************/
/********************************************************************/
/********************************************************************/
/* 类  名 Recursion	                                            */
/* 功  能 总程序的处理					            */
/* 说  明 建立一个类，处理总程序                                    */
/********************************************************************/
class Recursion
{       
TokenType token=new TokenType();

int MAXTOKENLEN=10;
int  lineno=0;
String temp_name;
StringTokenizer fenxi;

boolean Error=false;
String serror;
TreeNode yufaTree;

Recursion(String s)
{
    yufaTree=Program(s);
}

/********************************************************************/
/********************************************************************/
/********************************************************************/
/* 函数名 Program					            */
/* 功  能 总程序的处理函数					    */
/* 产生式 < Program > ::= ProgramHead DeclarePart ProgramBody .     */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/*        语法树的根节点的第一个子节点指向程序头部分ProgramHead,    */
/*        DeclaraPart为ProgramHead的兄弟节点,程序体部分ProgramBody  */
/*        为DeclarePart的兄弟节点.                                  */
/********************************************************************/
TreeNode Program(String ss)
{
        fenxi=new StringTokenizer(ss,"\n");
        ReadNextToken();

        TreeNode root = newNode("ProcK");
	TreeNode t=ProgramHead();
	TreeNode q=DeclarePart();
	TreeNode s=ProgramBody();		
       
	if (t!=null) 
            root.child[0] = t;
	else 
            syntaxError("a program head is expected!");
	if (q!=null) 
            root.child[1] = q;
	if (s!=null) 
            root.child[2] = s;
	else syntaxError("a program body is expected!");

	match("DOT");
        if (!(token.Lex.equals("ENDFILE")))
	    syntaxError("Code ends before file\n");

	return root;
}

/**************************函数头部分********************************/
/********************************************************************/
/********************************************************************/
/* 函数名 ProgramHead						    */
/* 功  能 程序头的处理函数					    */
/* 产生式 < ProgramHead > ::= PROGRAM  ProgramName                  */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProgramHead()
{
    TreeNode t = newNode("PheadK");
    match("PROGRAM");
    if (token.Lex.equals("ID"))
        t.name[0]=token.Sem;
    match("ID");
    return t;
}	
    
/**************************声明部分**********************************/
/********************************************************************/	
/********************************************************************/
/* 函数名 DeclarePart						    */
/* 功  能 声明部分的处理					    */
/* 产生式 < DeclarePart > ::= TypeDec  VarDec  ProcDec              */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode DeclarePart()
{
    /*类型*/
    TreeNode typeP = newNode("TypeK");    	 
    TreeNode tp1 = TypeDec();
    if (tp1!=null)
        typeP.child[0] = tp1;
    else
	typeP=null;

    /*变量*/
    TreeNode varP = newNode("VarK");
    TreeNode tp2 = VarDec();
    if (tp2 != null)
        varP.child[0] = tp2;
    else 
        varP=null;
		 
    /*函数*/
    TreeNode procP = ProcDec();
    if (procP==null)  {}
    if (varP==null)   { varP=procP; }	 
    if (typeP==null)  { typeP=varP; }
    if (typeP!=varP)
	typeP.sibling = varP;
    if (varP!=procP)
        varP.sibling = procP;
    return typeP;
}

/**************************类型声明部分******************************/
/********************************************************************/
/* 函数名 TypeDec					            */
/* 功  能 类型声明部分的处理    				    */
/* 产生式 < TypeDec > ::= ε | TypeDeclaration                      */
/* 说  明 根据文法产生式,调用相应的递归处理函数,生成语法树节点      */
/********************************************************************/
TreeNode TypeDec()
{
    TreeNode t = null;
    if (token.Lex.equals("TYPE"))
        t = TypeDeclaration();
    else if ((token.Lex.equals("VAR"))||(token.Lex.equals("PROCEDURE"))      
            ||(token.Lex.equals("BEGIN"))) {}
         else      
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 TypeDeclaration					    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < TypeDeclaration > ::= TYPE  TypeDecList                 */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode TypeDeclaration()
{
    match("TYPE");
    TreeNode t = TypeDecList();
    if (t==null)
        syntaxError("a type declaration is expected!");
    return t;
}

/********************************************************************/
/* 函数名 TypeDecList		 				    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < TypeDecList > ::= TypeId = TypeName ; TypeDecMore       */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode TypeDecList()
{
    TreeNode t = newNode("DecK");
    if (t != null)
    {
	TypeId(t);                               
	match("EQ");  
	TypeName(t); 
	match("SEMI");                           
        TreeNode p = TypeDecMore();
	if (p!=null)
	    t.sibling = p;
    }
    return t;
}

/********************************************************************/
/* 函数名 TypeDecMore		 				    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < TypeDecMore > ::=    ε | TypeDecList                   */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode TypeDecMore()
{
    TreeNode t=null;
    if (token.Lex.equals("ID"))
        t = TypeDecList();
    else if ((token.Lex.equals("VAR"))||(token.Lex.equals("PROCEDURE"))||(token.Lex.equals("BEGIN"))) {}       
         else
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 TypeId		 				    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < TypeId > ::= id                                         */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void TypeId(TreeNode t)
{
    if ((token.Lex.equals("ID"))&&(t!=null))
    {
	t.name[(t.idnum)]=token.Sem;
	t.idnum = t.idnum+1;
    }
    match("ID");
}

/********************************************************************/
/* 函数名 TypeName		 				    */
/* 功  能 类型声明部分的处理				            */
/* 产生式 < TypeName > ::= BaseType | StructureType | id            */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void TypeName(TreeNode t)
{
   if (t !=null)
   {
      if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR")))    
          BaseType(t);
      else if ((token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD")))   
              StructureType(t);
      else if (token.Lex.equals("ID")) 
           {
                 t.kind = "IdK";
	         t.attr.type_name = token.Sem;    
                 match("ID");  
           }
	   else
	       ReadNextToken();
   }
}
/********************************************************************/
/* 函数名 BaseType		 				    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < BaseType > ::=  INTEGER | CHAR                          */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void BaseType(TreeNode t)
{
       if (token.Lex.equals("INTEGER"))
       { 
             match("INTEGER");
             t.kind = "IntegerK";
       }
       else if (token.Lex.equals("CHAR"))     
             {
                 match("CHAR");
                 t.kind = "CharK";
             }
             else
                ReadNextToken();   
}

/********************************************************************/
/* 函数名 StructureType		 				    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < StructureType > ::=  ArrayType | RecType                */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void StructureType(TreeNode t)
{
       if (token.Lex.equals("ARRAY"))
       {
           ArrayType(t); 
       }          
       else if (token.Lex.equals("RECORD"))     
            {
                 t.kind = "RecordK";
                 RecType(t);
            }
            else
                ReadNextToken();   
}
/********************************************************************/
/* 函数名 ArrayType                                                 */
/* 功  能 类型声明部分的处理函数			            */
/* 产生式 < ArrayType > ::=  ARRAY [low..top] OF BaseType           */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void ArrayType(TreeNode t)
{
     t.attr.arrayAttr = new ArrayAttr();
     match("ARRAY");
     match("LMIDPAREN");
     if (token.Lex.equals("INTC"))
	 t.attr.arrayAttr.low = Integer.parseInt(token.Sem);
     match("INTC");
     match("UNDERANGE");
     if (token.Lex.equals("INTC"))
	 t.attr.arrayAttr.up = Integer.parseInt(token.Sem);
     match("INTC");
     match("RMIDPAREN");
     match("OF");
     BaseType(t);
     t.attr.arrayAttr.childtype = t.kind;
     t.kind = "ArrayK";
}

/********************************************************************/
/* 函数名 RecType		 				    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < RecType > ::=  RECORD FieldDecList END                  */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void RecType(TreeNode t)
{
    TreeNode p = null;
    match("RECORD");
    p = FieldDecList();
    if (p!=null)
        t.child[0] = p;
    else
        syntaxError("a record body is requested!");         
    match("END");
}
/********************************************************************/
/* 函数名 FieldDecList		 				    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < FieldDecList > ::=   BaseType IdList ; FieldDecMore     */
/*                             | ArrayType IdList; FieldDecMore     */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode  FieldDecList()
{
    TreeNode t = newNode("DecK");
    TreeNode p = null;
    if (t != null)
    {
        if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR")))
        {
                    BaseType(t);
	            IdList(t);
	            match("SEMI");
	            p = FieldDecMore();
        }
	else if (token.Lex.equals("ARRAY")) 
             {
	            ArrayType(t);
	            IdList(t);
	            match("SEMI");
	            p = FieldDecMore();
             }
             else
             {
		    ReadNextToken();
		    syntaxError("type name is expected");
             }
        t.sibling = p;
    }	    
    return t;	
}
/********************************************************************/
/* 函数名 FieldDecMore		 				    */
/* 功  能 类型声明部分的处理函数			            */
/* 产生式 < FieldDecMore > ::=  ε | FieldDecList                   */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode FieldDecMore()
{
    TreeNode t = null;   
    if (token.Lex.equals("INTEGER")||token.Lex.equals("CHAR")||token.Lex.equals("ARRAY"))
	t = FieldDecList();
    else if (token.Lex.equals("END")) {}
	 else
             ReadNextToken();
    return t;	
}
/********************************************************************/
/* 函数名 IdList		 				    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < IdList > ::=  id  IdMore                                */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void IdList(TreeNode  t)
{
    if (token.Lex.equals("ID"))
    {
	t.name[(t.idnum)] = token.Sem;
	t.idnum = t.idnum + 1;
        match("ID");
    }
    IdMore(t);
}

/********************************************************************/
/* 函数名 IdMore		 				    */
/* 功  能 类型声明部分的处理函数				    */
/* 产生式 < IdMore > ::=  ε |  , IdList                            */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void IdMore(TreeNode t)
{
    if (token.Lex.equals("COMMA"))
    {
        match("COMMA");
        IdList(t);
    }
    else if (token.Lex.equals("SEMI")) {}
         else
	     ReadNextToken();	
}

/**************************变量声明部分******************************/
/********************************************************************/
/* 函数名 VarDec		 				    */
/* 功  能 变量声明部分的处理				            */
/* 产生式 < VarDec > ::=  ε |  VarDeclaration                      */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode VarDec()
{
    TreeNode t = null;
    if (token.Lex.equals("VAR"))
        t = VarDeclaration();
    else if ((token.Lex.equals("PROCEDURE"))||(token.Lex.equals("BEGIN")))                    {}
	 else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* 函数名 VarDeclaration		 			    */
/* 功  能 变量声明部分的处理函数				    */
/* 产生式 < VarDeclaration > ::=  VAR  VarDecList                   */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode VarDeclaration()
{
    match("VAR");
    TreeNode t = VarDecList();
    if (t==null)
	syntaxError("a var declaration is expected!");
    return t;
}

/********************************************************************/
/* 函数名 VarDecList		 				    */
/* 功  能 变量声明部分的处理函数				    */
/* 产生式 < VarDecList > ::=  TypeName VarIdList; VarDecMore        */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode VarDecList()
{
    TreeNode t = newNode("DecK");
    TreeNode p = null;
    if (t != null)
    {
	TypeName(t);
	VarIdList(t);
	match("SEMI");
        p = VarDecMore();
	t.sibling = p;
    }
    return t;
}

/********************************************************************/
/* 函数名 VarDecMore		 				    */
/* 功  能 变量声明部分的处理函数				    */
/* 产生式 < VarDecMore > ::=  ε |  VarDecList                      */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode VarDecMore()
{
    TreeNode t =null;
    if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR"))                        ||(token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD"))                       ||(token.Lex.equals("ID")))
	t = VarDecList();
    else if ((token.Lex.equals("PROCEDURE"))||(token.Lex.equals("BEGIN")))
	     {}
	 else
             ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 VarIdList		 				    */
/* 功  能 变量声明部分的处理函数			            */
/* 产生式 < VarIdList > ::=  id  VarIdMore                          */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void VarIdList(TreeNode t)
{
    if (token.Lex.equals("ID"))
    {
        t.name[(t.idnum)] = token.Sem;
	t.idnum = t.idnum + 1;
        match("ID");
    }
    else 
    {
	syntaxError("a varid is expected here!");
	ReadNextToken();
    }
    VarIdMore(t);
}

/********************************************************************/
/* 函数名 VarIdMore		 				    */
/* 功  能 变量声明部分的处理函数				    */
/* 产生式 < VarIdMore > ::=  ε |  , VarIdList                      */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void VarIdMore(TreeNode t)
{
    if (token.Lex.equals("COMMA"))
    {   
        match("COMMA");
        VarIdList(t);
    }
    else if (token.Lex.equals("SEMI"))  {}
         else
             ReadNextToken();	
}
/****************************过程声明部分****************************/
/********************************************************************/
/* 函数名 ProcDec		 		                    */
/* 功  能 函数声明部分的处理					    */
/* 产生式 < ProcDec > ::=  ε |  ProcDeclaration                    */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProcDec()
{
    TreeNode t = null;
    if (token.Lex.equals("PROCEDURE"))
        t = ProcDeclaration();
    else if (token.Lex.equals("BEGIN")) {}
         else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* 函数名 ProcDeclaration		 			    */
/* 功  能 函数声明部分的处理函数				    */
/* 产生式 < ProcDeclaration > ::=  PROCEDURE ProcName(ParamList);   */
/*                                 ProcDecPart                      */
/*                                 ProcBody                         */
/*                                 ProcDecMore                      *
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProcDeclaration()
{
    TreeNode t = newNode("ProcDecK");
    match("PROCEDURE");
    if (token.Lex.equals("ID"))
    {
        t.name[0] = token.Sem;
	t.idnum = t.idnum+1;
	match("ID");
    }
    match("LPAREN");
    ParamList(t);
    match("RPAREN");
    match("SEMI");
    t.child[1] = ProcDecPart();
    t.child[2] = ProcBody();
    t.sibling = ProcDecMore();
    return t;
}
/********************************************************************/
/* 函数名 ProcDecMore    				            */
/* 功  能 更多函数声明中处理函数        	        	    */
/* 产生式 < ProcDecMore > ::=  ε |  ProcDeclaration                */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProcDecMore()
{
    TreeNode t = null;
    if (token.Lex.equals("PROCEDURE"))
        t = ProcDeclaration();
    else if (token.Lex.equals("BEGIN"))  {}
	 else
             ReadNextToken();
    return t;
}
/********************************************************************/
/* 函数名 ParamList		 				    */
/* 功  能 函数声明中参数声明部分的处理函数	        	    */
/* 产生式 < ParamList > ::=  ε |  ParamDecList                     */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void ParamList(TreeNode t)     
{
    TreeNode p = null;
    if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR"))||                      (token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD"))||                       (token.Lex.equals("ID"))||(token.Lex.equals("VAR")))
    {
        p = ParamDecList();
        t.child[0] = p;
    } 
    else if (token.Lex.equals("RPAREN")) {} 
	 else
	     ReadNextToken();
}

/********************************************************************/
/* 函数名 ParamDecList		 			    	    */
/* 功  能 函数声明中参数声明部分的处理函数	        	    */
/* 产生式 < ParamDecList > ::=  Param  ParamMore                    */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ParamDecList()
{
    TreeNode t = Param();
    TreeNode p = ParamMore();
    if (p!=null)
        t.sibling = p;
    return t;
}

/********************************************************************/
/* 函数名 ParamMore		 			    	    */
/* 功  能 函数声明中参数声明部分的处理函数	        	    */
/* 产生式 < ParamMore > ::=  ε | ; ParamDecList                    */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ParamMore()
{
    TreeNode t = null;
    if (token.Lex.equals("SEMI"))
    {
        match("SEMI");
        t = ParamDecList();
	if (t==null)
           syntaxError("a param declaration is request!");
    }
    else if (token.Lex.equals("RPAREN"))  {} 
	 else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* 函数名 Param		 			    	            */
/* 功  能 函数声明中参数声明部分的处理函数	        	    */
/* 产生式 < Param > ::=  TypeName FormList | VAR TypeName FormList  */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode Param()
{
    TreeNode t = newNode("DecK");
    if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR"))                        ||(token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD"))
       || (token.Lex.equals("ID")))
    {
         t.attr.procAttr = new ProcAttr();
         t.attr.procAttr.paramt = "valparamType";
	 TypeName(t);
	 FormList(t);
    }
    else if (token.Lex.equals("VAR"))
         {
             match("VAR");
             t.attr.procAttr = new ProcAttr();
             t.attr.procAttr.paramt = "varparamType";
	     TypeName(t);
	     FormList(t);
	 }
         else
             ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 FormList		 			    	    */
/* 功  能 函数声明中参数声明部分的处理函数	        	    */
/* 产生式 < FormList > ::=  id  FidMore                             */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void FormList(TreeNode t)
{
    if (token.Lex.equals("ID"))
    {
	t.name[(t.idnum)] = token.Sem;
	t.idnum = t.idnum + 1;
	match("ID");
    }
    FidMore(t);   
}

/********************************************************************/
/* 函数名 FidMore		 			    	    */
/* 功  能 函数声明中参数声明部分的处理函数	        	    */
/* 产生式 < FidMore > ::=   ε |  , FormList                        */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void FidMore(TreeNode t)
{      
    if (token.Lex.equals("COMMA"))
    {
        match("COMMA");
	FormList(t);
    }
    else if ((token.Lex.equals("SEMI"))||(token.Lex.equals("RPAREN")))  
             {}
         else
	     ReadNextToken();	  
}
/********************************************************************/
/* 函数名 ProcDecPart		 			  	    */
/* 功  能 函数中的声明部分的处理函数	             	            */
/* 产生式 < ProcDecPart > ::=  DeclarePart                          */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProcDecPart()
{
    TreeNode t = DeclarePart();
    return t;
}

/********************************************************************/
/* 函数名 ProcBody		 			  	    */
/* 功  能 函数体部分的处理函数	                    	            */
/* 产生式 < ProcBody > ::=  ProgramBody                             */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProcBody()
{
    TreeNode t = ProgramBody();
    if (t==null)
	syntaxError("a program body is requested!");
    return t;
}

/****************************函数体部分******************************/
/********************************************************************/
/********************************************************************/
/* 函数名 ProgramBody		 			  	    */
/* 功  能 程序体部分的处理	                    	            */
/* 产生式 < ProgramBody > ::=  BEGIN  StmList   END                 */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProgramBody()
{
    TreeNode t = newNode("StmLK");
    match("BEGIN");
    t.child[0] = StmList();
    match("END");
    return t;
}

/********************************************************************/
/* 函数名 StmList		 			  	    */
/* 功  能 语句部分的处理函数	                    	            */
/* 产生式 < StmList > ::=  Stm    StmMore                           */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode StmList()
{
    TreeNode t = Stm();
    TreeNode p = StmMore();
    if (t!=null)
    {
       if (p!=null)
	   t.sibling = p;
    }
    return t;
}

/********************************************************************/
/* 函数名 StmMore		 			  	    */
/* 功  能 语句部分的处理函数	                    	            */
/* 产生式 < StmMore > ::=   ε |  ; StmList                         */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode StmMore()
{
    TreeNode t = null;
    if ((token.Lex.equals("ELSE"))||(token.Lex.equals("FI"))||                        (token.Lex.equals("END"))||(token.Lex.equals("ENDWH"))) {}
    else if (token.Lex.equals("SEMI"))
	 {
             match("SEMI");
	     t = StmList();
	 }
	 else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* 函数名 Stm   		 			  	    */
/* 功  能 语句部分的处理函数	                    	            */
/* 产生式 < Stm > ::=   ConditionalStm   {IF}                       */
/*                    | LoopStm          {WHILE}                    */
/*                    | InputStm         {READ}                     */
/*                    | OutputStm        {WRITE}                    */
/*                    | ReturnStm        {RETURN}                   */
/*                    | id  AssCall      {id}                       */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode Stm()
{
    TreeNode t = null;
    if (token.Lex.equals("IF"))
        t = ConditionalStm();
    else if (token.Lex.equals("WHILE"))         
	     t = LoopStm();
    else if (token.Lex.equals("READ"))  
	     t = InputStm();	 
    else if (token.Lex.equals("WRITE"))   
	     t = OutputStm();	 
    else if (token.Lex.equals("RETURN"))  
	     t = ReturnStm();	 
    else if (token.Lex.equals("ID"))
         {
             temp_name = token.Sem;
	     match("ID");              
             t = AssCall();
         }
	 else
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 AssCall		 			  	    */
/* 功  能 语句部分的处理函数	                    	            */
/* 产生式 < AssCall > ::=   AssignmentRest   {:=,LMIDPAREN,DOT}     */
/*                        | CallStmRest      {(}                    */  
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode AssCall()
{
    TreeNode t = null;
    if ((token.Lex.equals("ASSIGN"))||(token.Lex.equals("LMIDPAREN"))||                  (token.Lex.equals("DOT")))
	t = AssignmentRest();
    else if (token.Lex.equals("LPAREN"))
	     t = CallStmRest();
	 else 
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 AssignmentRest		 			    */
/* 功  能 赋值语句部分的处理函数	                    	    */
/* 产生式 < AssignmentRest > ::=  VariMore : = Exp                  */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode AssignmentRest()
{
    TreeNode t = newStmtNode("AssignK");
	
    /* 赋值语句节点的第一个儿子节点记录赋值语句的左侧变量名，
    /* 第二个儿子结点记录赋值语句的右侧表达式*/

    /*处理第一个儿子结点，为变量表达式类型节点*/
    TreeNode c = newExpNode("VariK");
    c.name[0] = temp_name;
    c.idnum = c.idnum+1;
    VariMore(c);
    t.child[0] = c;
		
    match("ASSIGN");
	  
    /*处理第二个儿子节点*/
    t.child[1] = Exp(); 
				
    return t;
}

/********************************************************************/
/* 函数名 ConditionalStm		 			    */
/* 功  能 条件语句部分的处理函数	                    	    */
/* 产生式 <ConditionalStm>::=IF RelExp THEN StmList ELSE StmList FI */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ConditionalStm()
{
    TreeNode t = newStmtNode("IfK");
    match("IF");
    t.child[0] = Exp();
    match("THEN");
    if (t!=null)  
        t.child[1] = StmList();
    if(token.Lex.equals("ELSE"))
    {
	match("ELSE");   
	t.child[2] = StmList();
    }
    match("FI");
    return t;
}

/********************************************************************/
/* 函数名 LoopStm          		 			    */
/* 功  能 循环语句部分的处理函数	                    	    */
/* 产生式 < LoopStm > ::=   WHILE RelExp DO StmList ENDWH           */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode LoopStm()
{
    TreeNode t = newStmtNode("WhileK");
    match("WHILE");
    t.child[0] = Exp();
    match("DO");
    t.child[1] = StmList();
    match("ENDWH");
    return t;
}

/********************************************************************/
/* 函数名 InputStm          		     	                    */
/* 功  能 输入语句部分的处理函数	                    	    */
/* 产生式 < InputStm > ::=  READ(id)                                */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode InputStm()
{
    TreeNode t = newStmtNode("ReadK");
    match("READ");
    match("LPAREN");
    if (token.Lex.equals("ID"))	
    {
	t.name[0] = token.Sem;
        t.idnum = t.idnum+1;
    }
    match("ID");
    match("RPAREN");
    return t;
}

/********************************************************************/
/* 函数名 OutputStm          		     	                    */
/* 功  能 输出语句部分的处理函数	                    	    */
/* 产生式 < OutputStm > ::=   WRITE(Exp)                            */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode OutputStm()
{
    TreeNode t = newStmtNode("WriteK");
    match("WRITE");
    match("LPAREN");
    t.child[0] = Exp();
    match("RPAREN");
    return t;
}

/********************************************************************/
/* 函数名 ReturnStm          		     	                    */
/* 功  能 返回语句部分的处理函数	                    	    */
/* 产生式 < ReturnStm > ::=   RETURN(Exp)                           */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ReturnStm()
{
    TreeNode t = newStmtNode("ReturnK");
    match("RETURN");
    return t;
}

/********************************************************************/
/* 函数名 CallStmRest          		     	                    */
/* 功  能 函数调用语句部分的处理函数	                  	    */
/* 产生式 < CallStmRest > ::=  (ActParamList)                       */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode CallStmRest()
{
    TreeNode t=newStmtNode("CallK");
    match("LPAREN");
    /*函数调用时，其子节点指向实参*/
    /*函数名的结点也用表达式类型结点*/
    TreeNode c = newExpNode("VariK"); 
    c.name[0] = temp_name;
    c.idnum = c.idnum+1;
    t.child[0] = c;
    t.child[1] = ActParamList();
    match("RPAREN");
    return t;
}

/********************************************************************/
/* 函数名 ActParamList          		   	            */
/* 功  能 函数调用实参部分的处理函数	                	    */
/* 产生式 < ActParamList > ::=     ε |  Exp ActParamMore           */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ActParamList()
{
    TreeNode t = null;
    if (token.Lex.equals("RPAREN"))  {}
    else if ((token.Lex.equals("ID"))||(token.Lex.equals("INTC")))
	 {
	     t = Exp();
             if (t!=null)
	         t.sibling = ActParamMore();
         }
	 else
             ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 ActParamMore          		   	            */
/* 功  能 函数调用实参部分的处理函数	                	    */
/* 产生式 < ActParamMore > ::=     ε |  , ActParamList             */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ActParamMore()
{
    TreeNode t = null;
    if (token.Lex.equals("RPAREN"))  {}
    else if (token.Lex.equals("COMMA"))
         {
	     match("COMMA");
	     t = ActParamList();
	 }
	 else	
	     ReadNextToken();
    return t;
}

/*************************表达式部分********************************/
/*******************************************************************/
/* 函数名 Exp							   */
/* 功  能 表达式处理函数					   */
/* 产生式 Exp ::= simple_exp | 关系运算符  simple_exp              */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点 */
/*******************************************************************/
TreeNode Exp()
{
    TreeNode t = simple_exp();

    /* 当前单词token为逻辑运算单词LT或者EQ */
    if ((token.Lex.equals("LT"))||(token.Lex.equals("EQ"))) 
    {
        TreeNode p = newExpNode("OpK");

	/* 将当前单词token(为EQ或者LT)赋给语法树节点p的运算符成员attr.op*/
	p.child[0] = t;
        p.attr.expAttr.op = token.Lex;
        t = p;
 
        /* 当前单词token与指定逻辑运算符单词(为EQ或者LT)匹配 */ 
        match(token.Lex);

        /* 语法树节点t非空,调用简单表达式处理函数simple_exp()	   
           函数返回语法树节点给t的第二子节点成员child[1]  */ 
        if (t!=null)
            t.child[1] = simple_exp();
    }
    return t;
}

/*******************************************************************/
/* 函数名 simple_exp						   */
/* 功  能 表达式处理						   */
/* 产生式 simple_exp ::=   term  |  加法运算符  term               */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点 */
/*******************************************************************/
TreeNode simple_exp()
{
    TreeNode t = term();

    /* 当前单词token为加法运算符单词PLUS或MINUS */
    while ((token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS")))
    {
	TreeNode p = newExpNode("OpK");
	p.child[0] = t;
        p.attr.expAttr.op = token.Lex;
        t = p;

        match(token.Lex);

	/* 调用元处理函数term(),函数返回语法树节点给t的第二子节点成员child[1] */
        t.child[1] = term();
    }
    return t;
}

/********************************************************************/
/* 函数名 term						            */
/* 功  能 项处理函数						    */
/* 产生式 < 项 > ::=  factor | 乘法运算符  factor		    */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点 */
/********************************************************************/
TreeNode term()
{
    TreeNode t = factor();

    /* 当前单词token为乘法运算符单词TIMES或OVER */
    while ((token.Lex.equals("TIMES"))||(token.Lex.equals("OVER")))
    {
	TreeNode p = newExpNode("OpK");
	p.child[0] = t;
        p.attr.expAttr.op = token.Lex;
        t = p;	
        match(token.Lex);
        p.child[1] = factor();    
    }
    return t;
}

/*********************************************************************/
/* 函数名 factor						     */
/* 功  能 因子处理函数						     */
/* 产生式 factor ::= INTC | Variable | ( Exp )                       */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点 */
/*********************************************************************/
TreeNode factor()
{
    TreeNode t = null;
    if (token.Lex.equals("INTC")) 
    {
        t = newExpNode("ConstK");

	/* 将当前单词名tokenString转换为整数赋给t的数值成员attr.val */
        t.attr.expAttr.val = Integer.parseInt(token.Sem);
        match("INTC");
    }
    else if (token.Lex.equals("ID"))  	  
	     t = Variable();
    else if (token.Lex.equals("LPAREN")) 
	 {
             match("LPAREN");					
             t = Exp();
             match("RPAREN");					
         }
         else 			
             ReadNextToken();	  
    return t;
}


/********************************************************************/
/* 函数名 Variable						    */
/* 功  能 变量处理函数						    */
/* 产生式 Variable   ::=   id VariMore                   	    */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点 */
/********************************************************************/
TreeNode Variable()
{
    TreeNode t = newExpNode("VariK");
    if (token.Lex.equals("ID"))
    {
	t.name[0] = token.Sem;
        t.idnum = t.idnum+1;
    }
    match("ID");
    VariMore(t);
    return t;
}

/********************************************************************/
/* 函数名 VariMore						    */
/* 功  能 变量处理						    */
/* 产生式 VariMore   ::=  ε                             	    */
/*                       | [Exp]            {[}                     */
/*                       | . FieldVar       {DOT}                   */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点 */
/********************************************************************/	
void VariMore(TreeNode t)
{
        if ((token.Lex.equals("EQ"))||(token.Lex.equals("LT"))||                       (token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS"))||                           (token.Lex.equals("RPAREN"))||(token.Lex.equals("RMIDPAREN"))||                     (token.Lex.equals("SEMI"))||(token.Lex.equals("COMMA"))|| 
           (token.Lex.equals("THEN"))||(token.Lex.equals("ELSE"))||                       (token.Lex.equals("FI"))||(token.Lex.equals("DO"))||(token.Lex.equals           ("ENDWH"))||(token.Lex.equals("END"))||(token.Lex.equals("ASSIGN"))||           (token.Lex.equals("TIMES"))||(token.Lex.equals("OVER")))    {}
	else if (token.Lex.equals("LMIDPAREN"))
             {
	         match("LMIDPAREN");
	         t.child[0] = Exp();
                 t.attr.expAttr.varkind = "ArrayMembV";
	         match("RMIDPAREN");
	     }
	else if (token.Lex.equals("DOT"))
             {
	         match("DOT");
	         t.child[0] = FieldVar();
                 t.attr.expAttr.varkind = "FieldMembV";
	     }
	     else
	         ReadNextToken();
}
/********************************************************************/
/* 函数名 FieldVar						    */
/* 功  能 变量处理函数				                    */
/* 产生式 FieldVar   ::=  id  FieldVarMore                          */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点 */
/********************************************************************/
TreeNode FieldVar()
{
    TreeNode t = newExpNode("VariK");
    if (token.Lex.equals("ID"))
    {
	t.name[0] = token.Sem;
        t.idnum = t.idnum+1;
    }	
    match("ID");	
    FieldVarMore(t);
    return t;
}

/********************************************************************/
/* 函数名 FieldVarMore  			                    */
/* 功  能 变量处理函数                                              */
/* 产生式 FieldVarMore   ::=  ε| [Exp]            {[}              */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点 */
/********************************************************************/
void FieldVarMore(TreeNode t)
{
    if ((token.Lex.equals("ASSIGN"))||(token.Lex.equals("TIMES"))||               (token.Lex.equals("EQ"))||(token.Lex.equals("LT"))||                          (token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS"))||                      (token.Lex.equals("OVER"))||(token.Lex.equals("RPAREN"))||               (token.Lex.equals("SEMI"))||(token.Lex.equals("COMMA"))||               (token.Lex.equals("THEN"))||(token.Lex.equals("ELSE"))||               (token.Lex.equals("FI"))||(token.Lex.equals("DO"))||
       (token.Lex.equals("ENDWH"))||(token.Lex.equals("END")))
       {}
    else if (token.Lex.equals("LMIDPAREN"))
         { 
	     match("LMIDPAREN");
	     t.child[0] = Exp();
             t.child[0].attr.expAttr.varkind = "ArrayMembV";
	     match("RMIDPAREN");
	 }
	 else
	     ReadNextToken();
}

/********************************************************************/
/********************************************************************/
/* 函数名 match							    */
/* 功  能 终极符匹配处理函数				            */
/* 说  明 函数参数expected给定期望单词符号与当前单词符号token相匹配 */
/*        如果不匹配,则报非期望单词语法错误			    */
/********************************************************************/
void match(String expected)
{ 
      if (token.Lex.equals(expected))   
	  ReadNextToken();
      else 
      {
	  syntaxError("not match error ");
	  ReadNextToken();
      }     
}

/************************************************************/
/* 函数名 syntaxError                                       */
/* 功  能 语法错误处理函数		                    */
/* 说  明 将函数参数message指定的错误信息输出               */	
/************************************************************/
void syntaxError(String s)     /*向错误信息.txt中写入字符串*/
{
    serror=serror+"\n>>> ERROR :"+"Syntax error at "                              +String.valueOf(token.lineshow)+": "+s; 

    /* 设置错误追踪标志Error为TRUE,防止错误进一步传递 */
    Error = true;
}

/********************************************************************/
/* 函数名 ReadNextToken                                             */
/* 功  能 从Token序列中取出一个Token				    */
/* 说  明 从文件中存的Token序列中依次取一个单词，作为当前单词       */	
/********************************************************************/ 
void ReadNextToken()
{
    if (fenxi.hasMoreTokens())
    {
        int i=1;
	String stok=fenxi.nextToken();
        StringTokenizer fenxi1=new StringTokenizer(stok,":,");
        while (fenxi1.hasMoreTokens())
        {
            String fstok=fenxi1.nextToken();
            if (i==1)
            {
	        token.lineshow=Integer.parseInt(fstok);
	        lineno=token.lineshow;
            }
            if (i==2)
	        token.Lex=fstok;
            if (i==3)
                token.Sem=fstok;
            i++;
        }
    }    
}

/********************************************************
 *********以下是创建语法树所用的各类节点的申请***********
 ********************************************************/
/********************************************************/
/* 函数名 newNode				        */	
/* 功  能 创建语法树节点函数			        */
/* 说  明 该函数为语法树创建一个新的结点      	        */
/*        并将语法树节点成员赋初值。 s为ProcK, PheadK,  */
/*        DecK, TypeK, VarK, ProcDecK, StmLK	        */
/********************************************************/
TreeNode newNode(String s)
{
    TreeNode t=new TreeNode();
    t.nodekind = s;
    t.lineno = lineno;
    return t;
}
/********************************************************/
/* 函数名 newStmtNode					*/	
/* 功  能 创建语句类型语法树节点函数			*/
/* 说  明 该函数为语法树创建一个新的语句类型结点	*/
/*        并将语法树节点成员初始化			*/
/********************************************************/
TreeNode newStmtNode(String s)
{
    TreeNode t=new TreeNode();
    t.nodekind = "StmtK";
    t.lineno = lineno;
    t.kind = s;
    return t;
}
/********************************************************/
/* 函数名 newExpNode					*/
/* 功  能 表达式类型语法树节点创建函数			*/
/* 说  明 该函数为语法树创建一个新的表达式类型结点	*/
/*        并将语法树节点的成员赋初值			*/
/********************************************************/
TreeNode newExpNode(String s)
{
    TreeNode t=new TreeNode();
    t.nodekind = "ExpK";
    t.kind = s;
    t.lineno = lineno;
    t.attr.expAttr = new ExpAttr();
    t.attr.expAttr.varkind = "IdV";
    t.attr.expAttr.type = "Void";
    return t;
}

}