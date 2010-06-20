package parse;
/**
 * �﷨������
 * Parse.java
 * @author ������
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
public String serror = "";
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
 * ������״Ŀ¼
 */
public void printTreeEgg(String s)
{
	shell.setSize(500, 375);
	shell.setText("�﷨Tree");
	shell.setLayout(new FillLayout());
	// ����һ��������
	treeg = new Tree(shell, SWT.SINGLE);
	printTreeEg(yufaTree,0);
}

/********************************************************************/
/* ��  �� �ܳ���Ĵ�����								        	*/
/* ����ʽ < program > ::= programHead declarePart programBody .     */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/*        �﷨���ĸ��ڵ�ĵ�һ���ӽڵ�ָ�����ͷ����programHead,    */
/*        DeclaraPartΪprogramHead���ֵܽڵ�,�����岿��programBody  */
/*        ΪdeclarePart���ֵܽڵ�.                                  */
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
            syntaxError("ȱ�ٳ���ͷ��");
	if (q!=null) 
            root.child[1] = q;
	if (s!=null) 
            root.child[2] = s;
	else syntaxError("ȱ�ٳ����壡");

	match("DOT");
        if (!(token.getLex().equals("ENDFILE")))
	    syntaxError("����δִ����\n");

        if (Error==true)
            return null;
	return root;
}

/**************************����ͷ����********************************/
/********************************************************************/
/* ��  �� ����ͷ�Ĵ�����								        	*/
/* ����ʽ < programHead > ::= PROGRAM  ProgramName                  */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
    
/**************************��������**********************************/
/* ������ DeclarePart											    */
/* ��  �� �������ֵĴ�����								     	*/
/* ����ʽ < declarePart > ::= typeDec  varDec  procDec              */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
TreeNode DeclarePart()
{
    /*����*/
    TreeNode typeP = newNode("TypeK");    	 
    TreeNode tp1 = TypeDec();
    if (tp1!=null)
        typeP.child[0] = tp1;
    else
	typeP=null;

    /*����*/
    TreeNode varP = newNode("VarK");
    TreeNode tp2 = VarDec();
    if (tp2 != null)
        varP.child[0] = tp2;
    else 
        varP=null;
		 
    /*����*/
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

/**************************������������******************************/
/********************************************************************/
/* ������TypeDec									     		    */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < typeDec > ::= �� | TypeDeclaration                      */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������TypeDeclaration									  	    */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < TypeDeclaration > ::= TYPE  TypeDecList                 */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode TypeDeclaration()
{
    match("TYPE");
    TreeNode t = TypeDecList();
    if (t==null)
        syntaxError("ȱ����������");
    return t;
}

/********************************************************************/
/* ������ TypeDecList		 							  	        */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < TypeDecList > ::= typeId = typeName ; typeDecMore       */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������TypeDecMore		 							            */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < typeDecMore > ::=    �� | TypeDecList                   */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ TypeId		 							  	            */
/* ��  �� �����������ֵĴ�����	�������ͱ�ʶ��			        	*/
/* ����ʽ < typeId > ::= id                                         */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ TypeName		 							  	            */
/* ��  �� �����������ֵĴ�����		����������				        	*/
/* ����ʽ < typeName > ::= baseType | structureType | id            */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ BaseType		 							  	            */
/* ��  �� �����������ֵĴ�����			�����������	        	*/
/* ����ʽ < baseType > ::=  INTEGER | CHAR                          */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void BaseType(TreeNode t)
{
       if (token.getLex().equals("INTEGER"))
       { 
             match("INTEGER");
             t.kind = "IntegerK";//����
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
/* ������ StructureType		 							            */
/* ��  �� �����������ֵĴ�����			����ṹ����			        	*/
/* ����ʽ < structureType > ::=  arrayType | recType                */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ ArrayType		 							                */
/* ��  �� �����������ֵĴ�����				������������		        	*/
/* ����ʽ < arrayType > ::=  ARRAY [low..top] OF baseType           */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ RecType		 							                */
/* ��  �� �����������ֵĴ�����				�����¼����		        	*/
/* ����ʽ < recType > ::=  RECORD fieldDecList END                  */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void RecType(TreeNode t)
{
    TreeNode p = null;
    match("RECORD");
    p = FieldDecList();
    if (p!=null)
        t.child[0] = p;
    else
        syntaxError("ȱ�ټ�¼������");         
    match("END");
}

/********************************************************************/
/* ������ FieldDecList		 							            */
/* ��  �� �����������ֵĴ�����			������			        	*/
/* ����ʽ < fieldDecList > ::=   baseType idList ; fieldDecMore     */
/*                             | arrayType idList; fieldDecMore     */ 
/*˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�   */
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
		    syntaxError("ȱ��������");
             }
        t.sibling = p;
    }	    
    return t;	
}

/********************************************************************/
/* ������ FieldDecMore		 							            */
/* ��  �� �����������ֵĴ�����		������				        	*/
/* ����ʽ < fieldDecMore > ::=  �� | fieldDecList                   */ 
/*˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�   */
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
/* ������ IdList		 						     	            */
/* ��  �� �����������ֵĴ�����			�����ʶ��			        	*/
/* ����ʽ < idList > ::=  id  idMore                                */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������IdMore		 						     	            */
/* ��  �� �����������ֵĴ�����		�����ʶ��				        	*/
/* ����ʽ < idMore > ::=  �� |  , idList                            */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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

/**************************������������******************************/
/********************************************************************/
/* ������ VarDec		 						     	            */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < varDec > ::=  �� |  varDeclaration                      */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ VarDeclaration		 						            */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < varDeclaration > ::=  VAR  varDecList                   */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode VarDeclaration()
{
    match("VAR");
    TreeNode t = VarDecList();
    if (t==null)
	syntaxError("ȱ�ٱ�������");
    return t;
}

/********************************************************************/
/* ������ VarDecList		 						                */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < varDecList > ::=  typeName varIdList; varDecMore        */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ VarDecMore		 						                */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < varDecMore > ::=  �� |  varDecList                      */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ VarIdList		 						                    */
/* ��  �� �����������ֵĴ�����		 �����ʶ��				        	*/
/* ����ʽ < varIdList > ::=  id  varIdMore                          */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	syntaxError("ȱ�ٱ�ʶ��");
	ReadNextToken();
    }
    VarIdMore(t);
}

/********************************************************************/
/* ������ VarIdMore		 						                    */
/* ��  �� �����������ֵĴ�����			�����ʶ��			        	*/
/* ����ʽ < varIdMore > ::=  �� |  , varIdList                      */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/****************************������������****************************/
/********************************************************************/
/* ������ProcDec		 						                    */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < procDec > ::=  �� |  procDeclaration                    */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ ProcDeclaration		 						            */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < procDeclaration > ::=  PROCEDURE                        */
/*                                 ProcName(paramList);             */
/*                                 procDecPart                      */
/*                                 procBody                         */
/*                                 procDec                          */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/*        �����ĸ��ڵ����ڼ�¼�ú��������֣���һ���ӽڵ�ָ�������  */
/*        �㣬�ڶ����ڵ�ָ�����е��������ֽڵ㣻�������ڵ�ָ��  */
/*        ���塣
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
/* ������ ProcDecMore	 						                    */
/* ��  �� �����������ֵĴ�����						        	*/
/* ����ʽ < procDec > ::=  �� |  procDeclaration                    */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ ParamList		 						                    */
/* ��  �� ���������в����������ֵĴ�����	        	        	*/
/* ����ʽ < paramList > ::=  �� |  paramDecList                     */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ ParamDecList		 			    	                    */
/* ��  �� ���������в����������ֵĴ�����	        	        	*/
/* ����ʽ < paramDecList > ::=  param  paramMore                    */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ ParamMore		 			    	                        */
/* ��  �� ���������в����������ֵĴ�����	        	        	*/
/* ����ʽ < paramMore > ::=  �� | ; paramDecList                     */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ParamMore()
{
    TreeNode t = null;
    if (token.getLex().equals("SEMI"))
    {
        match("SEMI");
        t = ParamDecList();
	if (t==null)
           syntaxError("ȱ�ٲ�������");
    }
    else if (token.getLex().equals("RPAREN"))  {} 
	 else
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* ������ Param		 			    	                            */
/* ��  �� ���������в����������ֵĴ�����	        	        	*/
/* ����ʽ < param > ::=  typeName formList | VAR typeName formList  */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������FormList		 			    	                        */
/* ��  �� ���������в����������ֵĴ�����	        	        	*/
/* ����ʽ < formList > ::=  id  fidMore                             */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ FidMore		 			    	                        */
/* ��  �� ���������в����������ֵĴ�����	        	        	*/
/* ����ʽ < fidMore > ::=   �� |  , formList                        */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ ProcDecPart		 			  	                        */
/* ��  �� �����е��������ֵĴ�����	             	        	*/
/* ����ʽ < procDecPart > ::=  declarePart                          */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ProcDecPart()
{
    TreeNode t = DeclarePart();
    return t;
}

/********************************************************************/
/* ������ ProcBody		 			  	                            */
/* ��  �� �����岿�ֵĴ�����	                    	        	*/
/* ����ʽ < procBody > ::=  programBody                             */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ProcBody()
{
    TreeNode t = ProgramBody();
    if (t==null)
	syntaxError("ȱ�ٺ�����");
    return t;
}

/****************************�����岿��******************************/

/********************************************************************/
/* ������ ProgramBody		 			  	                        */
/* ��  �� �����岿�ֵĴ�����	                    	        	*/
/* ����ʽ < programBody > ::=  BEGIN  stmList   END                 */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������StmList		 			  	                            */
/* ��  �� ��䲿�ֵĴ�����	                    	        	*/
/* ����ʽ < stmList > ::=  stm    stmMore                           */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ StmMore		 			  	                            */
/* ��  �� ��䲿�ֵĴ�����	                    	        	*/
/* ����ʽ < stmMore > ::=   �� |  ; stmList                         */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ Stm   		 			  	                            */
/* ��  �� ��䲿�ֵĴ�����	                    	        	*/
/* ����ʽ < stm > ::=   conditionalStm   {IF}                       */
/*                    | loopStm          {WHILE}                    */
/*                    | inputStm         {READ}                     */
/*                    | outputStm        {WRITE}                    */
/*                    | returnStm        {RETURN}                   */
/*                    | id  assCall      {id}                       */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ AssCall		 			  	                            */
/* ��  �� ��䲿�ֵĴ�����	               ����ֵ�͵���      	        	*/
/* ����ʽ < assCall > ::=   assignmentRest   {:=,LMIDPAREN,DOT}     */
/*                        | callStmRest      {(}                    */  
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������AssignmentRest		 			                        */
/* ��  �� ��ֵ��䲿�ֵĴ�����	                    	        */
/* ����ʽ < assignmentRest > ::=  variMore : = exp                  */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode AssignmentRest()
{
    TreeNode t = newStmtNode("AssignK");
	
    /* ��ֵ���ڵ�ĵ�һ�����ӽڵ��¼��ֵ��������������
    /* �ڶ������ӽ���¼��ֵ�����Ҳ���ʽ*/

    /*�����һ�����ӽ�㣬Ϊ�������ʽ���ͽڵ�*/
    TreeNode c = newExpNode("VariK");
    c.name[0] = temp_name;
    c.idnum = c.idnum+1;
    VariMore(c);
    t.child[0] = c;
		
    match("ASSIGN");
	  
    /*����ڶ������ӽڵ�*/
    t.child[1] = Exp(); 
				
    return t;
}

/********************************************************************/
/* ������ ConditionalStm		 			                        */
/* ��  �� ������䲿�ֵĴ�����	                    	        */
/* ����ʽ < conditionalStm > ::= IF exp THEN stmList ELSE stmList FI*/ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ LoopStm          		 			                        */
/* ��  �� ѭ����䲿�ֵĴ�����	                    	        */
/* ����ʽ < loopStm > ::=      WHILE exp DO stmList ENDWH           */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ InputStm          		     	                        */
/* ��  �� ������䲿�ֵĴ�����	                    	        */
/* ����ʽ < inputStm > ::=    READ(id)                              */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ OutputStm          		     	                        */
/* ��  �� �����䲿�ֵĴ�����	                    	        */
/* ����ʽ < outputStm > ::=   WRITE(exp)                            */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ ReturnStm          		     	                        */
/* ��  �� ������䲿�ֵĴ�����	                    	        */
/* ����ʽ < returnStm > ::=   RETURN                                */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ReturnStm()
{
    TreeNode t = newStmtNode("ReturnK");
    match("RETURN");
    return t;
}

/********************************************************************/
/* ������ CallStmRest          		     	                        */
/* ��  �� ����������䲿�ֵĴ�����	                  	        */
/* ����ʽ < callStmRest > ::=  (actParamList)                       */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ ActParamList          		   	                        */
/* ��  �� ��������ʵ�β��ֵĴ�����	                	        */
/* ����ʽ < actParamList > ::=     �� |  exp actParamMore           */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
/* ������ ActParamMore          		   	                        */
/* ��  �� ��������ʵ�β��ֵĴ�����	                	        */
/* ����ʽ < actParamMore > ::=     �� |  , actParamList             */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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

/*************************���ʽ����********************************/

/****************************************************************************/
/* ������ Exp																*/
/* ��  �� ���ʽ������													*/
/* ����ʽ < ���ʽ > ::= < �򵥱��ʽ > [< ��ϵ����� > < �򵥱��ʽ > ]	*/
/* ˵  �� �ú������ݲ���ʽ������Ӧ�ݹ鴦����,���ɱ��ʽ�����﷨���ڵ�		*/
/****************************************************************************/
TreeNode Exp()
{
    TreeNode t = simple_exp();		//���ü򵥱��ʽ������simple_exp(),�����﷨���ڵ�ָ���t

    /* ��ǰ����tokenΪ�߼����㵥��LT����EQ */
    if ((token.getLex().equals("LT"))||(token.getLex().equals("EQ"))) 
    {
        TreeNode p = newExpNode("OpK");

	/* ����ǰ����token(ΪEQ����LT)�����﷨���ڵ�p���������Աattr.op*/
	p.child[0] = t;
        p.attr.expAttr.op = token.getLex();
        t = p;  //���µı��ʽ�����﷨���ڵ�p��Ϊ��������ֵt
 
        /* ��ǰ����token��ָ���߼����������(ΪEQ����LT)ƥ�� */ 
        match(token.getLex());
        /* �﷨���ڵ�t�ǿ�,���ü򵥱��ʽ������simple_exp()	 ���������﷨���ڵ�ָ���t�ĵڶ��ӽڵ��Աchild[1]	*/
        if (t!=null)
            t.child[1] = simple_exp();
    }
    return t;
}

/************************************************************************/
/* ������ Simple_exp													*/
/* ��  �� �򵥱��ʽ������											*/
/* ����ʽ < �򵥱��ʽ >::=	< �� > { < �ӷ������ > < �� > }			*/
/* ˵  �� �ú������ݲ���ʽ������Ӧ�ݹ鴦����,���ɱ��ʽ�����﷨���ڵ�	*/
/************************************************************************/
TreeNode simple_exp()
{
    TreeNode t = term();

    /* ��ǰ����tokenΪ�ӷ����������PLUS��MINUS */
    while ((token.getLex().equals("PLUS"))||(token.getLex().equals("MINUS")))
    {
	TreeNode p = newExpNode("OpK");		//������OpK���ʽ�����﷨���ڵ㣬���﷨���ڵ�ָ�븳��p
	p.child[0] = t;						//�﷨���ڵ�p�����ɹ�,��ʼ��p��һ�ӽڵ��Աchild[0]
        p.attr.expAttr.op = token.getLex();		//�����﷨���ڵ�ָ���p���������Աattr.op
        t = p;									//����������ֵt�����﷨���ڵ�p

        match(token.getLex());				//��ǰ����token��ָ���ӷ����㵥��(ΪPLUS��MINUS)ƥ��

	/* ����Ԫ������term(),���������﷨���ڵ��t�ĵڶ��ӽڵ��Աchild[1] */
        t.child[1] = term();
    }
    return t;
}

/****************************************************************************/
/* ������ Term																*/
/* ��  �� �����														*/
/* ����ʽ < �� > ::= < ���� > { < �˷������ > < ���� > }					*/
/* ˵  �� �ú������ݲ���ʽ������Ӧ�ݹ鴦����,���ɱ��ʽ�����﷨���ڵ�		*/
/****************************************************************************/
TreeNode term()
{
    TreeNode t = factor();				//�������Ӵ�����factor(),���������﷨���ڵ�ָ���t 

    /* ��ǰ����tokenΪ�˷����������TIMES��OVER */
    while ((token.getLex().equals("TIMES"))||(token.getLex().equals("OVER")))
    {
	TreeNode p = newExpNode("OpK");				//�����µ�OpK���ʽ�����﷨���ڵ�,�½ڵ�ָ�븳��p
	p.child[0] = t;								//���﷨���ڵ�p�����ɹ�,��ʼ����һ���ӽڵ��Աchild[0]Ϊt
        p.attr.expAttr.op = token.getLex();		//����ǰ����token��ֵ���﷨���ڵ�p���������Աattr.op
        t = p;	
        match(token.getLex());					//��ǰ����token��ָ���˷����������(ΪTIMES��OVER)ƥ�� 
        p.child[1] = factor();    				//�������Ӵ�����factor(),���������﷨���ڵ�ָ�븳��p�ڶ����ӽڵ��Աchild[1]
    }
    return t;
}

/****************************************************************************/
/* ������ Factor															*/
/* ��  �� ���Ӵ�����														*/
/* ����ʽ factor ::= ( exp ) | INTC | variable                  			*/
/* ˵  �� �ú������ݲ���ʽ������Ӧ�ĵݹ鴦����,���ɱ��ʽ�����﷨���ڵ�	*/
/****************************************************************************/
TreeNode factor()
{
    TreeNode t = null;
    if (token.getLex().equals("INTC")) 
    {
        t = newExpNode("ConstK");

	/* ����ǰ������tokenStringת��Ϊ��������t����ֵ��Աattr.val */
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
/* ������ Variable													*/
/* ��  �� ����������												*/
/* ����ʽ variable   ::=   id variMore                   			*/
/* ˵  �� �ú������ݲ���ʽ,	����������������﷨���ڵ�              */
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
/* ������ VariMore													*/
/* ��  �� ����������												*/
/* ����ʽ variMore   ::=  ��                             			*/
/*                       | [exp]            {[}                     */
/*                       | . fieldvar       {DOT}                   */ 
/* ˵  �� �ú������ݲ���ʽ������Ӧ�ĵݹ鴦������еļ��ֲ�ͬ����	*/
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
/* ������ FieldVar													*/
/* ��  �� ����������				���������								*/
/* ����ʽ fieldvar   ::=  id  fieldvarMore                          */ 
/* ˵  �� �ú������ݲ���ʽ����������������������﷨���ڵ�       	*/
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
/* ������ FieldVarMore  											*/
/* ��  �� ����������			���������									*/
/* ����ʽ fieldvarMore   ::=  ��                             		*/
/*                           | [exp]            {[}                 */ 
/* ˵  �� �ú������ݲ���ʽ������Ӧ�ĵݹ鴦�������Ϊ�������͵����	*/
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

/************************��ظ�������************************************/
/********************************************************************/
/* ������ Match														*/
/* ��  �� �ռ���ƥ�䴦����										*/
/* ˵  �� ��������expected�����������ʷ����뵱ǰ���ʷ���token��ƥ��	*/
/*        �����ƥ��,�򱨷����������﷨����							*/
/********************************************************************/
void match(String expected)
{ 
      if (token.getLex().equals(expected))   
	  ReadNextToken();
      else 
      {
	  syntaxError("�ռ�����ƥ��!");
	  ReadNextToken();
      }     
}

/********************************************************************/
/* ������ SyntaxError												*/
/* ��  �� �﷨��������											*/
/* ˵  �� ����������messageָ���Ĵ�����Ϣ��ʽ��д���ַ���	*/
/*		  ���ô���׷�ٱ�־ErrorΪTRUE								*/
/********************************************************************/
void syntaxError(String s)     
{
    serror=serror+">>> ERROR :"+"�������ڵ� "+String.valueOf(token.getLineshow())+" ��"+": "+s+"\n"; 

    /* ���ô���׷�ٱ�־ErrorΪTRUE,��ֹ�����һ������ */
    Error = true;
}

/*****************************************************************/
/* ������ ReadNextToken							        		 */	
/* ��  �� ���ļ�tokenlist�е���Ϣ��Ϊ����ֵ                      */							    
/*        һ�㣬listingָ���׼�����                            */
/* ˵  �� ����ֵΪToken���ͣ������﷨������                  */
/*****************************************************************/
void ReadNextToken()
{
    if (fenxi.hasMoreTokens())
    {
        int i=1;
	String stok=fenxi.nextToken();
        StringTokenizer fenxi1=new StringTokenizer(stok,":,");  //�ã���,���ֽ��ַ����ı��,����������ַ�������:��,��������ĸ
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
 *********�����Ǵ����﷨�����õĸ���ڵ������***********
 ********************************************************/

/********************************************************/

/* ������ newNode									*/	
/* ��  �� �����﷨���ڵ㺯��			        		*/
/* ˵  �� �ú���Ϊ�﷨������һ���µĽ��      		*/
/********************************************************/
TreeNode newNode(String s)
{
    TreeNode t=new TreeNode();
    t.nodekind = s;
    t.lineno = lineno;
    return t;
}

/********************************************************/
/* ������ newStmtNode									*/	
/* ��  �� ������������﷨���ڵ㺯��					*/
/* ˵  �� �ú���Ϊ�﷨������һ���µ�������ͽ��		*/
/*        �����﷨���ڵ��Ա��ʼ��						*/
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
/* ������ newExpNode									*/
/* ��  �� ���ʽ�����﷨���ڵ㴴������					*/
/* ˵  �� �ú���Ϊ�﷨������һ���µı��ʽ���ͽ��		*/
/*        �����﷨���ڵ�ĳ�Ա��ʼ��					*/
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
/*д�ַ�������*/
/********************************************************/
void writeStr(String s)                   /*д�ַ���*/
{
    stree=stree+s;
} 

/********************************************************/
/*д�ո���*/
/********************************************************/
void writeSpace()                            /*д�ո�*/
{
    stree=stree+"  ";
}

/********************************************************/
/*д���з���4���ո���*/
/********************************************************/
void writeTab(int x)              /*д���з���1���ո�*/
{
    stree=stree+"\n";    
    while (x!=0)
    {  stree=stree+"    ";   x--;  }
}

/******************************************************/
/* ������ printTree                                   */
/* ��  �� ���﷨�����           */
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
            
         /* ���﷨�����tree�ĸ��ӽ��ݹ����printTree���� */
         for (int i=0;i<3;i++)
              printTree(tree.child[i],l+1);
         /* ���﷨�����tree�ĸ��ֵܽ��ݹ����printTree���� */
         tree=tree.sibling;       
    }             
}



/*������״Ŀ¼*/
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
