package parse;
/**
 * 语法分析类
 * Parse.java
 * @author 张玉明
 *
 */
import java.util.StringTokenizer;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;


import scanner.Token;
public class Parse
{       
Token token=new Token();

int MAXTOKENLEN=10;
int  lineno=0;
String temp_name;
StringTokenizer fenxi;

public boolean Error=false;
public String stree;
public String serror;
public TreeNode yufaTree;

final Display display = Display.getDefault();
final Shell shell = new Shell();
Tree treeg = null;
int MAX = 6500;
TreeItem treeItem[] = new TreeItem[MAX];



public Parse(String s)
{	
	
    yufaTree=Program(s);
    printTree(yufaTree,0);
}

/*
 * 生成树状目录
 */
public void printTreeEgg(String s)
{
	shell.setSize(500, 375);
	shell.setText("Tree实例");
	shell.setLayout(new FillLayout());
	// 定义一个树对象
	treeg = new Tree(shell, SWT.SINGLE);
	printTreeEg(yufaTree,0);
}

/********************************************************************/
/* 功  能 总程序的处理函数								        	*/
/* 产生式 < program > ::= programHead declarePart programBody .     */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/*        语法树的根节点的第一个子节点指向程序头部分programHead,    */
/*        DeclaraPart为programHead的兄弟节点,程序体部分programBody  */
/*        为declarePart的兄弟节点.                                  */
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
        if (!(token.getLex().equals("ENDFILE")))
	    syntaxError("Code ends before file\n");

        if (Error==true)
            return null;
	return root;
}

/**************************函数头部分********************************/
/********************************************************************/
/* 功  能 程序头的处理函数								        	*/
/* 产生式 < programHead > ::= PROGRAM  ProgramName                  */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProgramHead()
{
    TreeNode t = newNode("PheadK");
    match("PROGRAM");
    if (token.getLex().equals("ID"))
        t.name[0]=token.getSem();
    match("ID");
    return t;
}	
    
/**************************声明部分**********************************/
/* 函数名 DeclarePart											    */
/* 功  能 声明部分的处理函数								     	*/
/* 产生式 < declarePart > ::= typeDec  varDec  procDec              */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
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
/* 函数名TypeDec									     		    */
/* 功  能 类型声明部分的处理函数						        	*/
/* 产生式 < typeDec > ::= ε | TypeDeclaration                      */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode TypeDec()
{
    TreeNode t = null;
    if (token.getLex().equals("TYPE"))
        t = TypeDeclaration();
    else if ((token.getLex().equals("VAR"))||(token.getLex().equals("PROCEDURE"))      
            ||(token.getLex().equals("BEGIN"))) {}
         else      
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名TypeDeclaration									  	    */
/* 功  能 类型声明部分的处理函数						        	*/
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
/* 函数名 TypeDecList		 							  	        */
/* 功  能 类型声明部分的处理函数						        	*/
/* 产生式 < TypeDecList > ::= typeId = typeName ; typeDecMore       */
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
/* 函数名TypeDecMore		 							            */
/* 功  能 类型声明部分的处理函数						        	*/
/* 产生式 < typeDecMore > ::=    ε | TypeDecList                   */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode TypeDecMore()
{
    TreeNode t=null;
    if (token.getLex().equals("ID"))
        t = TypeDecList();
    else if ((token.getLex().equals("VAR"))||(token.getLex().equals("PROCEDURE"))||(token.getLex().equals("BEGIN")))
    {}       
         else
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 TypeId		 							  	            */
/* 功  能 类型声明部分的处理函数	处理类型标识符			        	*/
/* 产生式 < typeId > ::= id                                         */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void TypeId(TreeNode t)
{
    if ((token.getLex().equals("ID"))&&(t!=null))
    {
	t.name[(t.idnum)]=token.getSem();
	t.idnum = t.idnum+1;
    }
    match("ID");
}

/********************************************************************/
/* 函数名 TypeName		 							  	            */
/* 功  能 类型声明部分的处理函数		处理类型名				        	*/
/* 产生式 < typeName > ::= baseType | structureType | id            */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void TypeName(TreeNode t)
{
   if (t !=null)
   {
      if ((token.getLex().equals("INTEGER"))||(token.getLex().equals("CHAR")))    
          BaseType(t);
      else if ((token.getLex().equals("ARRAY"))||(token.getLex().equals("RECORD")))   
              StructureType(t);
      else if (token.getLex().equals("ID")) 
           {
                 t.kind = "IdK";
	         t.attr.type_name = token.getSem();    
                 match("ID");  
           }
	   else
	       ReadNextToken();
   }
}

/********************************************************************/
/* 函数名 BaseType		 							  	            */
/* 功  能 类型声明部分的处理函数			处理基本类型	        	*/
/* 产生式 < baseType > ::=  INTEGER | CHAR                          */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void BaseType(TreeNode t)
{
       if (token.getLex().equals("INTEGER"))
       { 
             match("INTEGER");
             t.kind = "IntegerK";//类型
       }
       else if (token.getLex().equals("CHAR"))     
             {
                 match("CHAR");
                 t.kind = "CharK";
             }
             else
                ReadNextToken();   
}

/********************************************************************/
/* 函数名 StructureType		 							            */
/* 功  能 类型声明部分的处理函数			处理结构类型			        	*/
/* 产生式 < structureType > ::=  arrayType | recType                */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void StructureType(TreeNode t)
{
       if (token.getLex().equals("ARRAY"))
       {
           ArrayType(t); 
       }          
       else if (token.getLex().equals("RECORD"))     
            {
                 t.kind = "RecordK";
                 RecType(t);
            }
            else
                ReadNextToken();   
}

/********************************************************************/
/* 函数名 ArrayType		 							                */
/* 功  能 类型声明部分的处理函数				处理数组类型		        	*/
/* 产生式 < arrayType > ::=  ARRAY [low..top] OF baseType           */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void ArrayType(TreeNode t)
{
     t.attr.arrayAttr = new ArrayAttr();
     match("ARRAY");
     match("LMIDPAREN");
     if (token.getLex().equals("INTC"))
	 t.attr.arrayAttr.low = Integer.parseInt(token.getSem());
     match("INTC");
     match("UNDERANGE");
     if (token.getLex().equals("INTC"))
	 t.attr.arrayAttr.up = Integer.parseInt(token.getSem());
     match("INTC");
     match("RMIDPAREN");
     match("OF");
     BaseType(t);
     t.attr.arrayAttr.childtype = t.kind;
     t.kind = "ArrayK";
}

/********************************************************************/
/* 函数名 RecType		 							                */
/* 功  能 类型声明部分的处理函数				处理记录类型		        	*/
/* 产生式 < recType > ::=  RECORD fieldDecList END                  */
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
/* 函数名 FieldDecList		 							            */
/* 功  能 类型声明部分的处理函数			处理域			        	*/
/* 产生式 < fieldDecList > ::=   baseType idList ; fieldDecMore     */
/*                             | arrayType idList; fieldDecMore     */ 
/*说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点   */
/********************************************************************/
TreeNode  FieldDecList()
{
    TreeNode t = newNode("DecK");
    TreeNode p = null;
    if (t != null)
    {
        if ((token.getLex().equals("INTEGER"))||(token.getLex().equals("CHAR")))
        {
                    BaseType(t);
	            IdList(t);
	            match("SEMI");
	            p = FieldDecMore();
        }
	else if (token.getLex().equals("ARRAY")) 
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
/* 函数名 FieldDecMore		 							            */
/* 功  能 类型声明部分的处理函数		处理域				        	*/
/* 产生式 < fieldDecMore > ::=  ε | fieldDecList                   */ 
/*说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点   */
/********************************************************************/
TreeNode FieldDecMore()
{
    TreeNode t = null;   
    if (token.getLex().equals("INTEGER")||token.getLex().equals("CHAR")                          ||token.getLex().equals("ARRAY"))
	t = FieldDecList();
    else if (token.getLex().equals("END")) {}
	 else
             ReadNextToken();
    return t;	
}
/********************************************************************/
/* 函数名 IdList		 						     	            */
/* 功  能 类型声明部分的处理函数			处理标识符			        	*/
/* 产生式 < idList > ::=  id  idMore                                */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void IdList(TreeNode  t)
{
    if (token.getLex().equals("ID"))
    {
	t.name[(t.idnum)] = token.getSem();
	t.idnum = t.idnum + 1;
        match("ID");
    }
    IdMore(t);
}

/********************************************************************/
/* 函数名IdMore		 						     	            */
/* 功  能 类型声明部分的处理函数		处理标识符				        	*/
/* 产生式 < idMore > ::=  ε |  , idList                            */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void IdMore(TreeNode t)
{
    if (token.getLex().equals("COMMA"))
    {
        match("COMMA");
        IdList(t);
    }
    else if (token.getLex().equals("SEMI")) {}
         else
	     ReadNextToken();	
}

/**************************变量声明部分******************************/
/********************************************************************/
/* 函数名 VarDec		 						     	            */
/* 功  能 变量声明部分的处理函数						        	*/
/* 产生式 < varDec > ::=  ε |  varDeclaration                      */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode VarDec()
{
    TreeNode t = null;
    if (token.getLex().equals("VAR"))
        t = VarDeclaration();
    else if ((token.getLex().equals("PROCEDURE"))||(token.getLex().equals("BEGIN")))                    {}
	 else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* 函数名 VarDeclaration		 						            */
/* 功  能 变量声明部分的处理函数						        	*/
/* 产生式 < varDeclaration > ::=  VAR  varDecList                   */ 
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
/* 函数名 VarDecList		 						                */
/* 功  能 变量声明部分的处理函数						        	*/
/* 产生式 < varDecList > ::=  typeName varIdList; varDecMore        */ 
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
/* 函数名 VarDecMore		 						                */
/* 功  能 变量声明部分的处理函数						        	*/
/* 产生式 < varDecMore > ::=  ε |  varDecList                      */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode VarDecMore()
{
    TreeNode t =null;
    if ((token.getLex().equals("INTEGER"))||(token.getLex().equals("CHAR"))                        ||(token.getLex().equals("ARRAY"))||(token.getLex().equals("RECORD"))                       ||(token.getLex().equals("ID")))
	t = VarDecList();
    else if ((token.getLex().equals("PROCEDURE"))||(token.getLex().equals("BEGIN")))
	     {}
	 else
             ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 VarIdList		 						                    */
/* 功  能 变量声明部分的处理函数		 处理标识符				        	*/
/* 产生式 < varIdList > ::=  id  varIdMore                          */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void VarIdList(TreeNode t)
{
    if (token.getLex().equals("ID"))
    {
        t.name[(t.idnum)] = token.getSem();
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
/* 函数名 VarIdMore		 						                    */
/* 功  能 变量声明部分的处理函数			处理标识符			        	*/
/* 产生式 < varIdMore > ::=  ε |  , varIdList                      */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/  
void VarIdMore(TreeNode t)
{
    if (token.getLex().equals("COMMA"))
    {   
        match("COMMA");
        VarIdList(t);
    }
    else if (token.getLex().equals("SEMI"))  {}
         else
             ReadNextToken();	
}
/****************************过程声明部分****************************/
/********************************************************************/
/* 函数名ProcDec		 						                    */
/* 功  能 函数声明部分的处理函数						        	*/
/* 产生式 < procDec > ::=  ε |  procDeclaration                    */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProcDec()
{
    TreeNode t = null;
    if (token.getLex().equals("PROCEDURE"))
        t = ProcDeclaration();
    else if (token.getLex().equals("BEGIN")) {}
         else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* 函数名 ProcDeclaration		 						            */
/* 功  能 函数声明部分的处理函数						        	*/
/* 产生式 < procDeclaration > ::=  PROCEDURE                        */
/*                                 ProcName(paramList);             */
/*                                 procDecPart                      */
/*                                 procBody                         */
/*                                 procDec                          */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/*        函数的根节点用于记录该函数的名字；第一个子节点指向参数节  */
/*        点，第二个节点指向函数中的声明部分节点；第三个节点指向函  */
/*        数体。
/********************************************************************/
TreeNode ProcDeclaration()
{
    TreeNode t = newNode("ProcDecK");
    match("PROCEDURE");
    if (token.getLex().equals("ID"))
    {
        t.name[0] = token.getSem();
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
/* 函数名 ProcDecMore	 						                    */
/* 功  能 函数声明部分的处理函数						        	*/
/* 产生式 < procDec > ::=  ε |  procDeclaration                    */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProcDecMore()
{
    TreeNode t = null;
    if (token.getLex().equals("PROCEDURE"))
        t = ProcDeclaration();
    else if (token.getLex().equals("BEGIN"))  {}
	 else
             ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 ParamList		 						                    */
/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
/* 产生式 < paramList > ::=  ε |  paramDecList                     */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void ParamList(TreeNode t)     
{
    TreeNode p = null;
    if ((token.getLex().equals("INTEGER"))||(token.getLex().equals("CHAR"))||                      (token.getLex().equals("ARRAY"))||(token.getLex().equals("RECORD"))||                       (token.getLex().equals("ID"))||(token.getLex().equals("VAR")))
    {
        p = ParamDecList();
        t.child[0] = p;
    } 
    else if (token.getLex().equals("RPAREN")) {} 
	 else
	     ReadNextToken();
}

/********************************************************************/
/* 函数名 ParamDecList		 			    	                    */
/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
/* 产生式 < paramDecList > ::=  param  paramMore                    */ 
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
/* 函数名 ParamMore		 			    	                        */
/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
/* 产生式 < paramMore > ::=  ε | ; paramDecList                     */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ParamMore()
{
    TreeNode t = null;
    if (token.getLex().equals("SEMI"))
    {
        match("SEMI");
        t = ParamDecList();
	if (t==null)
           syntaxError("a param declaration is request!");
    }
    else if (token.getLex().equals("RPAREN"))  {} 
	 else
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 Param		 			    	                            */
/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
/* 产生式 < param > ::=  typeName formList | VAR typeName formList  */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode Param()
{
    TreeNode t = newNode("DecK");
    if ((token.getLex().equals("INTEGER"))||(token.getLex().equals("CHAR"))                        ||(token.getLex().equals("ARRAY"))||(token.getLex().equals("RECORD"))
       || (token.getLex().equals("ID")))
    {
         t.attr.procAttr = new ProcAttr();
         t.attr.procAttr.paramt = "valparamType";
	 TypeName(t);
	 FormList(t);
    }
    else if (token.getLex().equals("VAR"))
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
/* 函数名FormList		 			    	                        */
/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
/* 产生式 < formList > ::=  id  fidMore                             */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void FormList(TreeNode t)
{
    if (token.getLex().equals("ID"))
    {
	t.name[(t.idnum)] = token.getSem();
	t.idnum = t.idnum + 1;
	match("ID");
    }
    FidMore(t);   
}

/********************************************************************/
/* 函数名 FidMore		 			    	                        */
/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
/* 产生式 < fidMore > ::=   ε |  , formList                        */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
void FidMore(TreeNode t)
{      
    if (token.getLex().equals("COMMA"))
    {
        match("COMMA");
	FormList(t);
    }
    else if ((token.getLex().equals("SEMI"))||(token.getLex().equals("RPAREN")))  
             {}
         else
	     ReadNextToken();	  
}

/********************************************************************/
/* 函数名 ProcDecPart		 			  	                        */
/* 功  能 函数中的声明部分的处理函数	             	        	*/
/* 产生式 < procDecPart > ::=  declarePart                          */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ProcDecPart()
{
    TreeNode t = DeclarePart();
    return t;
}

/********************************************************************/
/* 函数名 ProcBody		 			  	                            */
/* 功  能 函数体部分的处理函数	                    	        	*/
/* 产生式 < procBody > ::=  programBody                             */ 
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
/* 函数名 ProgramBody		 			  	                        */
/* 功  能 程序体部分的处理函数	                    	        	*/
/* 产生式 < programBody > ::=  BEGIN  stmList   END                 */ 
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
/* 函数名StmList		 			  	                            */
/* 功  能 语句部分的处理函数	                    	        	*/
/* 产生式 < stmList > ::=  stm    stmMore                           */ 
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
/* 函数名 StmMore		 			  	                            */
/* 功  能 语句部分的处理函数	                    	        	*/
/* 产生式 < stmMore > ::=   ε |  ; stmList                         */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode StmMore()
{
    TreeNode t = null;
    if ((token.getLex().equals("ELSE"))||(token.getLex().equals("FI"))||                        (token.getLex().equals("END"))||(token.getLex().equals("ENDWH"))) {}
    else if (token.getLex().equals("SEMI"))
	 {
             match("SEMI");
	     t = StmList();
	 }
	 else
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 Stm   		 			  	                            */
/* 功  能 语句部分的处理函数	                    	        	*/
/* 产生式 < stm > ::=   conditionalStm   {IF}                       */
/*                    | loopStm          {WHILE}                    */
/*                    | inputStm         {READ}                     */
/*                    | outputStm        {WRITE}                    */
/*                    | returnStm        {RETURN}                   */
/*                    | id  assCall      {id}                       */ 
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode Stm()
{
    TreeNode t = null;
    if (token.getLex().equals("IF"))
        t = ConditionalStm();
    else if (token.getLex().equals("WHILE"))         
	     t = LoopStm();
    else if (token.getLex().equals("READ"))  
	     t = InputStm();	 
    else if (token.getLex().equals("WRITE"))   
	     t = OutputStm();	 
    else if (token.getLex().equals("RETURN"))  
	     t = ReturnStm();	 
    else if (token.getLex().equals("ID"))
         {
             temp_name = token.getSem();
	     match("ID");              
             t = AssCall();
         }
	 else
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名 AssCall		 			  	                            */
/* 功  能 语句部分的处理函数	               处理赋值和调用      	        	*/
/* 产生式 < assCall > ::=   assignmentRest   {:=,LMIDPAREN,DOT}     */
/*                        | callStmRest      {(}                    */  
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode AssCall()
{
    TreeNode t = null;
    if ((token.getLex().equals("ASSIGN"))||(token.getLex().equals("LMIDPAREN"))||                  (token.getLex().equals("DOT")))
	t = AssignmentRest();
    else if (token.getLex().equals("LPAREN"))
	     t = CallStmRest();
	 else 
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* 函数名AssignmentRest		 			                        */
/* 功  能 赋值语句部分的处理函数	                    	        */
/* 产生式 < assignmentRest > ::=  variMore : = exp                  */ 
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
/* 函数名 ConditionalStm		 			                        */
/* 功  能 条件语句部分的处理函数	                    	        */
/* 产生式 < conditionalStm > ::= IF exp THEN stmList ELSE stmList FI*/ 
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
    if(token.getLex().equals("ELSE"))
    {
	match("ELSE");   
	t.child[2] = StmList();
    }
    match("FI");
    return t;
}

/********************************************************************/
/* 函数名 LoopStm          		 			                        */
/* 功  能 循环语句部分的处理函数	                    	        */
/* 产生式 < loopStm > ::=      WHILE exp DO stmList ENDWH           */
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
/* 函数名 InputStm          		     	                        */
/* 功  能 输入语句部分的处理函数	                    	        */
/* 产生式 < inputStm > ::=    READ(id)                              */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode InputStm()
{
    TreeNode t = newStmtNode("ReadK");
    match("READ");
    match("LPAREN");
    if (token.getLex().equals("ID"))	
    {
	t.name[0] = token.getSem();
        t.idnum = t.idnum+1;
    }
    match("ID");
    match("RPAREN");
    return t;
}

/********************************************************************/
/* 函数名 OutputStm          		     	                        */
/* 功  能 输出语句部分的处理函数	                    	        */
/* 产生式 < outputStm > ::=   WRITE(exp)                            */
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
/* 函数名 ReturnStm          		     	                        */
/* 功  能 返回语句部分的处理函数	                    	        */
/* 产生式 < returnStm > ::=   RETURN                                */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ReturnStm()
{
    TreeNode t = newStmtNode("ReturnK");
    match("RETURN");
    return t;
}

/********************************************************************/
/* 函数名 CallStmRest          		     	                        */
/* 功  能 函数调用语句部分的处理函数	                  	        */
/* 产生式 < callStmRest > ::=  (actParamList)                       */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode CallStmRest()
{
    TreeNode t=newStmtNode("CallK");
    match("LPAREN");
    TreeNode c = newExpNode("VariK"); 
    c.name[0] = temp_name;
    c.idnum = c.idnum+1;
    t.child[0] = c;
    t.child[1] = ActParamList();
    match("RPAREN");
    return t;
}

/********************************************************************/
/* 函数名 ActParamList          		   	                        */
/* 功  能 函数调用实参部分的处理函数	                	        */
/* 产生式 < actParamList > ::=     ε |  exp actParamMore           */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ActParamList()
{
    TreeNode t = null;
    if (token.getLex().equals("RPAREN"))  {}
    else if ((token.getLex().equals("ID"))||(token.getLex().equals("INTC")))
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
/* 函数名 ActParamMore          		   	                        */
/* 功  能 函数调用实参部分的处理函数	                	        */
/* 产生式 < actParamMore > ::=     ε |  , actParamList             */
/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
/********************************************************************/
TreeNode ActParamMore()
{
    TreeNode t = null;
    if (token.getLex().equals("RPAREN"))  {}
    else if (token.getLex().equals("COMMA"))
         {
	     match("COMMA");
	     t = ActParamList();
	 }
	 else	
	     ReadNextToken();
    return t;
}

/*************************表达式部分********************************/

/****************************************************************************/
/* 函数名 Exp																*/
/* 功  能 表达式处理函数													*/
/* 产生式 < 表达式 > ::= < 简单表达式 > [< 关系运算符 > < 简单表达式 > ]	*/
/* 说  明 该函数根据产生式调用相应递归处理函数,生成表达式类型语法树节点		*/
/****************************************************************************/
TreeNode Exp()
{
    TreeNode t = simple_exp();		//调用简单表达式处理函数simple_exp(),返回语法树节点指针给t

    /* 当前单词token为逻辑运算单词LT或者EQ */
    if ((token.getLex().equals("LT"))||(token.getLex().equals("EQ"))) 
    {
        TreeNode p = newExpNode("OpK");

	/* 将当前单词token(为EQ或者LT)赋给语法树节点p的运算符成员attr.op*/
	p.child[0] = t;
        p.attr.expAttr.op = token.getLex();
        t = p;  //将新的表达式类型语法树节点p作为函数返回值t
 
        /* 当前单词token与指定逻辑运算符单词(为EQ或者LT)匹配 */ 
        match(token.getLex());
        /* 语法树节点t非空,调用简单表达式处理函数simple_exp()	 函数返回语法树节点指针给t的第二子节点成员child[1]	*/
        if (t!=null)
            t.child[1] = simple_exp();
    }
    return t;
}

/************************************************************************/
/* 函数名 Simple_exp													*/
/* 功  能 简单表达式处理函数											*/
/* 产生式 < 简单表达式 >::=	< 项 > { < 加法运算符 > < 项 > }			*/
/* 说  明 该函数根据产生式调用相应递归处理函数,生成表达式类型语法树节点	*/
/************************************************************************/
TreeNode simple_exp()
{
    TreeNode t = term();

    /* 当前单词token为加法运算符单词PLUS或MINUS */
    while ((token.getLex().equals("PLUS"))||(token.getLex().equals("MINUS")))
    {
	TreeNode p = newExpNode("OpK");		//创建新OpK表达式类型语法树节点，新语法树节点指针赋给p
	p.child[0] = t;						//语法树节点p创建成功,初始化p第一子节点成员child[0]
        p.attr.expAttr.op = token.getLex();		//返回语法树节点指针给p的运算符成员attr.op
        t = p;									//将函数返回值t赋成语法树节点p

        match(token.getLex());				//当前单词token与指定加法运算单词(为PLUS或MINUS)匹配

	/* 调用元处理函数term(),函数返回语法树节点给t的第二子节点成员child[1] */
        t.child[1] = term();
    }
    return t;
}

/****************************************************************************/
/* 函数名 Term																*/
/* 功  能 项处理函数														*/
/* 产生式 < 项 > ::= < 因子 > { < 乘法运算符 > < 因子 > }					*/
/* 说  明 该函数根据产生式调用相应递归处理函数,生成表达式类型语法树节点		*/
/****************************************************************************/
TreeNode term()
{
    TreeNode t = factor();				//调用因子处理函数factor(),函数返回语法树节点指针给t 

    /* 当前单词token为乘法运算符单词TIMES或OVER */
    while ((token.getLex().equals("TIMES"))||(token.getLex().equals("OVER")))
    {
	TreeNode p = newExpNode("OpK");				//创建新的OpK表达式类型语法树节点,新节点指针赋给p
	p.child[0] = t;								//新语法树节点p创建成功,初始化第一个子节点成员child[0]为t
        p.attr.expAttr.op = token.getLex();		//将当前单词token赋值给语法树节点p的运算符成员attr.op
        t = p;	
        match(token.getLex());					//当前单词token与指定乘法运算符单词(为TIMES或OVER)匹配 
        p.child[1] = factor();    				//调用因子处理函数factor(),函数返回语法树节点指针赋给p第二个子节点成员child[1]
    }
    return t;
}

/****************************************************************************/
/* 函数名 Factor															*/
/* 功  能 因子处理函数														*/
/* 产生式 factor ::= ( exp ) | INTC | variable                  			*/
/* 说  明 该函数根据产生式调用相应的递归处理函数,生成表达式类型语法树节点	*/
/****************************************************************************/
TreeNode factor()
{
    TreeNode t = null;
    if (token.getLex().equals("INTC")) 
    {
        t = newExpNode("ConstK");

	/* 将当前单词名tokenString转换为整数赋给t的数值成员attr.val */
        t.attr.expAttr.val = Integer.parseInt(token.getSem());
        match("INTC");
    }
    else if (token.getLex().equals("ID"))  	  
	     t = Variable();
    else if (token.getLex().equals("LPAREN")) 
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
/* 函数名 Variable													*/
/* 功  能 变量处理函数												*/
/* 产生式 variable   ::=   id variMore                   			*/
/* 说  明 该函数根据产生式,	处理变量，生成其语法树节点              */
/********************************************************************/
TreeNode Variable()
{
    TreeNode t = newExpNode("VariK");
    if (token.getLex().equals("ID"))
    {
	t.name[0] = token.getSem();
        t.idnum = t.idnum+1;
    }
    match("ID");
    VariMore(t);
    return t;
}

/********************************************************************/
/* 函数名 VariMore													*/
/* 功  能 变量处理函数												*/
/* 产生式 variMore   ::=  ε                             			*/
/*                       | [exp]            {[}                     */
/*                       | . fieldvar       {DOT}                   */ 
/* 说  明 该函数根据产生式调用相应的递归处理变量中的几种不同类型	*/
/********************************************************************/	
void VariMore(TreeNode t)
{
        if ((token.getLex().equals("EQ"))||(token.getLex().equals("LT"))||                       (token.getLex().equals("PLUS"))||(token.getLex().equals("MINUS"))||                           (token.getLex().equals("RPAREN"))||(token.getLex().equals("RMIDPAREN"))||                     (token.getLex().equals("SEMI"))||(token.getLex().equals("COMMA"))|| 
           (token.getLex().equals("THEN"))||(token.getLex().equals("ELSE"))||                       (token.getLex().equals("FI"))||(token.getLex().equals("DO"))||(token.getLex().equals           ("ENDWH"))||(token.getLex().equals("END"))||(token.getLex().equals("ASSIGN"))||           (token.getLex().equals("TIMES"))||(token.getLex().equals("OVER")))    {}
	else if (token.getLex().equals("LMIDPAREN"))
             {
	         match("LMIDPAREN");
	         t.child[0] = Exp();
                 t.attr.expAttr.varkind = "ArrayMembV";
	         match("RMIDPAREN");
	     }
	else if (token.getLex().equals("DOT"))
             {
	         match("DOT");
	         t.child[0] = FieldVar();
                 t.attr.expAttr.varkind = "FieldMembV";
	     }
	     else
	         ReadNextToken();
}

/********************************************************************/
/* 函数名 FieldVar													*/
/* 功  能 变量处理函数				处理域变量								*/
/* 产生式 fieldvar   ::=  id  fieldvarMore                          */ 
/* 说  明 该函数根据产生式，处理域变量，并生成其语法树节点       	*/
/********************************************************************/
TreeNode FieldVar()
{
    TreeNode t = newExpNode("VariK");
    if (token.getLex().equals("ID"))
    {
	t.name[0] = token.getSem();
        t.idnum = t.idnum+1;
    }	
    match("ID");	
    FieldVarMore(t);
    return t;
}

/********************************************************************/
/* 函数名 FieldVarMore  											*/
/* 功  能 变量处理函数			处理域变量									*/
/* 产生式 fieldvarMore   ::=  ε                             		*/
/*                           | [exp]            {[}                 */ 
/* 说  明 该函数根据产生式调用相应的递归处理域变量为数组类型的情况	*/
/********************************************************************/
void FieldVarMore(TreeNode t)
{
    if ((token.getLex().equals("ASSIGN"))||(token.getLex().equals("TIMES"))||               (token.getLex().equals("EQ"))||(token.getLex().equals("LT"))||                          (token.getLex().equals("PLUS"))||(token.getLex().equals("MINUS"))||                      (token.getLex().equals("OVER"))||(token.getLex().equals("RPAREN"))||               (token.getLex().equals("SEMI"))||(token.getLex().equals("COMMA"))||               (token.getLex().equals("THEN"))||(token.getLex().equals("ELSE"))||               (token.getLex().equals("FI"))||(token.getLex().equals("DO"))||
       (token.getLex().equals("ENDWH"))||(token.getLex().equals("END")))
       {}
    else if (token.getLex().equals("LMIDPAREN"))
         { 
	     match("LMIDPAREN");
	     t.child[0] = Exp();
             t.child[0].attr.expAttr.varkind = "ArrayMembV";
	     match("RMIDPAREN");
	 }
	 else
	     ReadNextToken();
}

/************************相关辅助函数************************************/
/********************************************************************/
/* 函数名 Match														*/
/* 功  能 终极符匹配处理函数										*/
/* 说  明 函数参数expected给定期望单词符号与当前单词符号token相匹配	*/
/*        如果不匹配,则报非期望单词语法错误							*/
/********************************************************************/
void match(String expected)
{ 
      if (token.getLex().equals(expected))   
	  ReadNextToken();
      else 
      {
	  syntaxError("not match error ");
	  ReadNextToken();
      }     
}

/********************************************************************/
/* 函数名 SyntaxError												*/
/* 功  能 语法错误处理函数											*/
/* 说  明 将函数参数message指定的错误信息格式化写入字符串	*/
/*		  设置错误追踪标志Error为TRUE								*/
/********************************************************************/
void syntaxError(String s)     
{
    serror=serror+"\n>>> ERROR :"+"Syntax error at "+String.valueOf(token.getLineshow())+": "+s; 

    /* 设置错误追踪标志Error为TRUE,防止错误进一步传递 */
    Error = true;
}

/*****************************************************************/
/* 函数名 ReadNextToken							        		 */	
/* 功  能 将文件tokenlist中的信息作为返回值                      */							    
/*        一般，listing指向标准输出。                            */
/* 说  明 返回值为Token类型，用于语法分析中                  */
/*****************************************************************/
void ReadNextToken()
{
    if (fenxi.hasMoreTokens())
    {
        int i=1;
	String stok=fenxi.nextToken();
        StringTokenizer fenxi1=new StringTokenizer(stok,":,");  //用：和,做分解字符串的标记,就是输出的字符不含有:和,这两个字母
        while (fenxi1.hasMoreTokens())
        {
            String fstok=fenxi1.nextToken();
            if (i==1)
            {
	        token.setLineshow(Integer.parseInt(fstok));
	        lineno=token.getLineshow();
            }
            if (i==2)
	        token.setLex(fstok);
            if (i==3)
                token.setSem(fstok);
            i++;
        }
    }    
}


 
/********************************************************
 *********以下是创建语法树所用的各类节点的申请***********
 ********************************************************/

/********************************************************/

/* 函数名 newNode									*/	
/* 功  能 创建语法树节点函数			        		*/
/* 说  明 该函数为语法树创建一个新的结点      		*/
/********************************************************/
TreeNode newNode(String s)
{
    TreeNode t=new TreeNode();
    t.nodekind = s;
    t.lineno = lineno;
    return t;
}

/********************************************************/
/* 函数名 newStmtNode									*/	
/* 功  能 创建语句类型语法树节点函数					*/
/* 说  明 该函数为语法树创建一个新的语句类型结点		*/
/*        并将语法树节点成员初始化						*/
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
/* 函数名 newExpNode									*/
/* 功  能 表达式类型语法树节点创建函数					*/
/* 说  明 该函数为语法树创建一个新的表达式类型结点		*/
/*        并将语法树节点的成员初始化					*/
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

/********************************************************/
/*写字符串函数*/
/********************************************************/
void writeStr(String s)                   /*写字符串*/
{
    stree=stree+s;
} 

/********************************************************/
/*写空格函数*/
/********************************************************/
void writeSpace()                            /*写空格*/
{
    stree=stree+"  ";
}

/********************************************************/
/*写换行符和4个空格函数*/
/********************************************************/
void writeTab(int x)              /*写换行符和1个空格*/
{
    stree=stree+"\n";    
    while (x!=0)
    {  stree=stree+"    ";   x--;  }
}

/******************************************************/
/* 函数名 printTree                                   */
/* 功  能 把语法树输出           */
/******************************************************/
void printTree(TreeNode t,int l)
{
     TreeNode tree=t;
     while (tree!=null)
     {
         if (tree.nodekind.equals("ProcK"))
         { 
             stree="ProcK";
         }      
         else if (tree.nodekind.equals("PheadK"))
         {
                  writeTab(1); 
                  writeStr("PheadK");
                  writeSpace();
                  writeStr(tree.name[0]);
         } 
         else if (tree.nodekind.equals("DecK")) 
         {
                  writeTab(l);
                  writeStr("DecK");
                  writeSpace();
                  if (tree.attr.procAttr!=null)
                  {
                      if (tree.attr.procAttr.paramt.equals("varparamType"))
                          writeStr("Var param:"); 
                      else if (tree.attr.procAttr.paramt.equals("valparamType"))
                               writeStr("Value param:");
                  }
                  if (tree.kind.equals("ArrayK"))
                  { 
                     writeStr("ArrayK");
                     writeSpace();  
                     writeStr(String.valueOf(tree.attr.arrayAttr.low));
                     writeSpace();
                     writeStr(String.valueOf(tree.attr.arrayAttr.up));
                     writeSpace();
                     if (tree.attr.arrayAttr.childtype.equals("CharK"))
                         writeStr("CharK");
                     else if(tree.attr.arrayAttr.childtype.equals("IntegerK"))
                             writeStr("IntegerK");
                  }
                  else if (tree.kind.equals("CharK"))
                           writeStr("CharK");
                  else if (tree.kind.equals("IntegerK"))
                           writeStr("IntegerK");
                  else if (tree.kind.equals("RecordK"))
                           writeStr("RecordK");
                  else if (tree.kind.equals("IdK"))
                       {
                           writeStr("IdK");
                           writeStr(tree.attr.type_name);
                       }
                       else 
                           syntaxError("error1!");
                  if (tree.idnum !=0)
	              for (int i=0 ; i < (tree.idnum);i++)
		           {  writeSpace();  writeStr(tree.name[i]); }   
                  else
                      syntaxError("wrong!no var!");
              }
         else if (tree.nodekind.equals("TypeK"))
         {
                  writeTab(l);           
                  writeStr("TypeK");
         }
         else if (tree.nodekind.equals("VarK"))
         {
                  writeTab(l);
                  writeStr("VarK");
         }
         else if (tree.nodekind.equals("ProcDecK"))
         {
                  writeTab(l); 
                  writeStr("ProcDecK");
                  writeSpace();
                  writeStr(tree.name[0]);
         } 
         else if (tree.nodekind.equals("StmLK"))
         {
                  writeTab(l);
                  writeStr("StmLK");
         }
         else if (tree.nodekind.equals("StmtK"))
         {
                  writeTab(l);
                  writeStr("StmtK");
                  writeSpace();
                  if (tree.kind.equals("IfK"))
                	  writeStr("If");
                  else if (tree.kind.equals("WhileK"))
                	  writeStr("While");
                  else if (tree.kind.equals("AssignK"))
                	  writeStr("Assign");
                  else if (tree.kind.equals("ReadK"))
                  {
                	  	   writeStr("Read");
                           writeSpace();
                           writeStr(tree.name[0]);
                  }
                  else if (tree.kind.equals("WriteK"))
		           writeStr("Write");
                  else if (tree.kind.equals("CallK"))
		           writeStr("Call");  
                  else if (tree.kind.equals("ReturnK"))
		           writeStr("Return");
                       else 
                           syntaxError("error2!");
         }
         else if (tree.nodekind.equals("ExpK"))
         {
                  writeTab(l);
                  writeStr("ExpK");
                  if (tree.kind.equals("OpK"))
                  {
                      writeSpace();
                      writeStr("Op");
                      writeSpace();
                      if (tree.attr.expAttr.op.equals("EQ"))
                          		writeStr("=");
                      else if (tree.attr.expAttr.op.equals("LT"))
                               writeStr("<");
                      else if (tree.attr.expAttr.op.equals("PLUS"))
                               writeStr("+");
                      else if (tree.attr.expAttr.op.equals("MINUS"))
                               writeStr("-");
                      else if (tree.attr.expAttr.op.equals("TIMES"))
                               writeStr("*");
                      else if (tree.attr.expAttr.op.equals("OVER"))
                               writeStr("/");
                           else
                               syntaxError("error3!");
                  }
                  else if (tree.kind.equals("ConstK"))
                  {
                           writeSpace();
                           writeStr("Const");
                           writeSpace();
                           writeStr(String.valueOf(tree.attr.expAttr.val));  
		          }        
                  else if (tree.kind.equals("VariK"))
		          {
                           writeSpace();
                           writeStr("Vari");
                           writeSpace();
                           if (tree.attr.expAttr.varkind.equals("IdV"))
                           {
                        	   writeStr("Id");
                               writeSpace();
                               writeStr(tree.name[0]);
                           }       
                           else if (tree.attr.expAttr.varkind.equals("FieldMembV"))
                           {
                        	   	   writeStr("FieldMember");
                                   writeSpace();
                                   writeStr(tree.name[0]);
                           }  
                           else if (tree.attr.expAttr.varkind.equals("ArrayMembV"))
                           {
                        	   	   writeStr("ArrayMember");
                                   writeSpace();
                                   writeStr(tree.name[0]);
                           }
                           else
                                    syntaxError("var type error!");  
		      }
                      else 
                          syntaxError("error4!");
              }    
              else 
                  syntaxError("error5!");
            
         /* 对语法树结点tree的各子结点递归调用printTree过程 */
         for (int i=0;i<3;i++)
              printTree(tree.child[i],l+1);
         /* 对语法树结点tree的各兄弟结点递归调用printTree过程 */
         tree=tree.sibling;       
    }             
}



/*画出树状目录*/
public void printTreeEg(TreeNode t,int l)
{	
	
	
	int i = 1;
	int j = 500;
	int k = 1500;
	int z = 2500;
	int m = 3500;
	int n = 4000;
	int o = 4500;
	int p = 5000;
	int q = 5500;
	int r = 6000;
	StringTokenizer treenodelist;
	treenodelist=new StringTokenizer(stree,"\n");
	treeItem[0] = new TreeItem(treeg, SWT.NONE);
	treeItem[0].setText("ProcK");
	String linenodelist = treenodelist.nextToken();
	 while (treenodelist.hasMoreTokens())
	 {
		 
		 linenodelist = treenodelist.nextToken();
		 linenodelist = linenodelist + "                                        ";
		 if(linenodelist.substring(0,40).equals("                                        "))
		 {
			 treeItem[r] = new TreeItem(treeItem[q-1], SWT.NONE);
			 treeItem[r].setText(linenodelist.substring(40,linenodelist.length()));
			 r = r+1;
			 continue;
		 }
		 else if(linenodelist.substring(0,36).equals("                                    "))
		 {
			 treeItem[q] = new TreeItem(treeItem[p-1], SWT.NONE);
			 treeItem[q].setText(linenodelist.substring(36,linenodelist.length()));
			 q = q+1;
			 continue;
		 }
		 else if(linenodelist.substring(0,32).equals("                                "))
		 {
			 treeItem[p] = new TreeItem(treeItem[o-1], SWT.NONE);
			 treeItem[p].setText(linenodelist.substring(32,linenodelist.length()));
			 p = p+1;
			 continue;
		 }
		 else if(linenodelist.substring(0,28).equals("                            "))
		 {
			 treeItem[o] = new TreeItem(treeItem[n-1], SWT.NONE);
			 treeItem[o].setText(linenodelist.substring(28,linenodelist.length()));
			 o = o+1;
			 continue;
		 }
		 else if(linenodelist.substring(0,24).equals("                        "))
		 {
			 treeItem[n] = new TreeItem(treeItem[m-1], SWT.NONE);
			 treeItem[n].setText(linenodelist.substring(24,linenodelist.length()));
			 n = n+1;
			 continue;
		 }
		 else if(linenodelist.substring(0,20).equals("                    "))
		 {
			 treeItem[m] = new TreeItem(treeItem[z-1], SWT.NONE);
			 treeItem[m].setText(linenodelist.substring(20,linenodelist.length()));
			 m = m+1;
			 continue;
		 }
		 else if(linenodelist.substring(0,16).equals("                "))
		 {
			 treeItem[z] = new TreeItem(treeItem[k-1], SWT.NONE);
			 treeItem[z].setText(linenodelist.substring(16,linenodelist.length()));
			 z = z+1;
			 continue;
		 }
		 else if(linenodelist.substring(0,12).equals("            "))
		 {
			 treeItem[k] = new TreeItem(treeItem[j-1], SWT.NONE);
			 treeItem[k].setText(linenodelist.substring(12,linenodelist.length()));
			 k = k+1;
			 continue;
		 }
		 else if(linenodelist.substring(0,8).equals("        "))
		 {
			 treeItem[j] = new TreeItem(treeItem[i-1], SWT.NONE);
			 treeItem[j].setText(linenodelist.substring(8,linenodelist.length()));
			 j = j+1;
			 continue;
		 }
	 	 else if(linenodelist.substring(0,4).equals("    "))
		 {
			 treeItem[i] = new TreeItem(treeItem[0], SWT.NONE);
			 treeItem[i].setText(linenodelist.substring(4,linenodelist.length()));
	     	 i = i+1;
		 }
			 
	 }

	shell.open();
	shell.layout();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch())
			display.sleep();
	}
	
}

}
