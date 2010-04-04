package parse;
/**
 * 语法分析类
 * Parse.java
 * @author 张玉明
 *
 */
import java.util.StringTokenizer;
import scanner.Token;
public class Parse 
{
	Token token = new Token();
	int MAXTOKENLEN = 10;
	int lineno = 0;
	String temp_name;
	StringTokenizer analyse;
	
	
	public boolean Error = false;
	public String stree;
	public String serror;
	public TreeNode grammarTree;
	
	
	public Parse(String s)
	{
		grammarTree = program(s);
		printTree(grammarTree,0);
	}
	
	/********************************************************************/
	/* 功  能 总程序的处理函数								        	*/
	/* 产生式 < program > ::= programHead declarePart programBody .     */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/*        语法树的根节点的第一个子节点指向程序头部分programHead,    */
	/*        DeclaraPart为programHead的兄弟节点,程序体部分programBody  */
	/*        为declarePart的兄弟节点.                                  */
	/********************************************************************/
	TreeNode program(String ss)
	{
		analyse = new StringTokenizer(ss,"\n");
		readNextToken();
		TreeNode root = newNode("ProcK");
		TreeNode t = programHead();
		TreeNode q = declarePart();
		TreeNode s = programBody();
		if(t!=null)
		{
			root.child[0] = t;
			
		}
		else
		{
			syntaxError("a program head is expected!");
		}
		if(q!=null)
		{
			root.child[1] = q;
		}
		if(s!=null)
		{
			root.child[2] = s;
		}
		else
		{
			syntaxError("a program body is expected!");
		}
		match("DOT");
		if(!(token.Lex.equals("ENDFILE")))
		{
			syntaxError("Code ends before file\n");
		}
		if(Error = true)
		{
			return null;
		}
		return root;
		
	}
	
	/********************************************************************/
	/* 功  能 程序头的处理函数								        	*/
	/* 产生式 < programHead > ::= PROGRAM  ProgramName                  */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode programHead()
	{
		TreeNode t = newNode("PheadK");
		match("PROGRAM");
		if(token.Lex.equals("ID"))
		{
			t.name[0] = token.Sem;
			
		}
		match("ID");
		return t;
			
	}
	
	
	
 /*************************声明部分***********************************/
	
	/********************************************************************/
	/* 函数名 declarePart											    */
	/* 功  能 声明部分的处理函数								     	*/
	/* 产生式 < declarePart > ::= typeDec  varDec  procDec              */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode declarePart()
	{
		/*类型*/
		TreeNode typeP = newNode("TypeK");
		TreeNode tp1 = typeDec();
		if(tp1!=null)
		{
			typeP.child[0] = tp1;
		}
		else
		{
			typeP = null;
		}
		
		 /*变量*/
		TreeNode varP = newNode("VarK");
		TreeNode tp2 = varDec();
		if(tp2 != null)
		{
			varP.child[0] = tp2;
		}
		else
		{
			varP = null;
		}
		
		/*函数*/
		TreeNode procP = procDec();
		if(procP == null)
		{	
		}
		if(varP == null)
		{
			varP = procP;
		}
		if(typeP == null)
		{
			typeP = varP;
		}
		if(typeP != varP)
		{
			typeP.sibling = varP;
		}
		if(varP != procP)
		{
			varP.sibling = procP;
		}
		return typeP;
		
	}
	
	
	
	/**************************类型声明部分******************************/

	/********************************************************************/
	/* 函数名 typeDec									     		    */
	/* 功  能 类型声明部分的处理函数						        	*/
	/* 产生式 < typeDec > ::= ε | TypeDeclaration                      */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode typeDec()
	{
		TreeNode t = null;
		if(token.Lex.equals("TYPE"))
		{
			t = typeDeclaration();
		}
		else if((token.Lex.equals("VAR"))||(token.Lex.equals("PROCEDURE"))||(token.Lex.equals("BEGIN")))
		{
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名typeDeclaration									  	    */
	/* 功  能 类型声明部分的处理函数						        	*/
	/* 产生式 < TypeDeclaration > ::= TYPE  TypeDecList                 */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode typeDeclaration()
	{
		match("TYPE");
		TreeNode t = typeDecList();
		if(t==null)
		{
			syntaxError("A type declaration is expected!");
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名 typeDecList		 							  	        */
	/* 功  能 类型声明部分的处理函数						        	*/
	/* 产生式 < TypeDecList > ::= typeId = typeName ; typeDecMore       */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode typeDecList()
	{
		TreeNode t = newNode("DecK");
		if(t != null)
		{
			typeId(t);
			match("EQ");
			typeName(t);
			match("SEMI");
			TreeNode p = typeDecMore();
			if(p!=null)
			{
				t.sibling = p;
			}
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名 typeDecMore		 							            */
	/* 功  能 类型声明部分的处理函数						        	*/
	/* 产生式 < typeDecMore > ::=    ε | TypeDecList                   */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode typeDecMore()
	{
		TreeNode t = null;
		if(token.Lex.equals("ID"))
		{
			t  = typeDecList();
		}
		else if((token.Lex.equals("VAR"))||(token.Lex.equals("PROCEDURE"))||(token.Lex.equals("BEGIN")))
		{
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名 typeId		 							  	            */
	/* 功  能 类型声明部分的处理函数	处理类型标识符			        	*/
	/* 产生式 < typeId > ::= id                                         */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void typeId(TreeNode t)
	{
		if((token.Lex.equals("ID"))&&(t!=null))
		{
			t.name[(t.idnum)]=token.Sem;
			t.idnum = t.idnum + 1;
		}
		match("ID");
	}
	
	/********************************************************************/
	/* 函数名 typeName		 							  	            */
	/* 功  能 类型声明部分的处理函数		处理类型名				        	*/
	/* 产生式 < typeName > ::= baseType | structureType | id            */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void typeName(TreeNode t)
	{
		if(t!=null)
		{
			if((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR")))
			{
				baseType(t);
			}
			else if((token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD")))
			{
				structureType(t);
			}
			else if(token.Lex.equals("ID"))
			{
				t.kind = "IdK";
				t.attr.type_name = token.Sem;
				match("ID");
			}
			else
			{
				readNextToken();
			}
		}
	}
	
	
	/********************************************************************/
	/* 函数名 baseType		 							  	            */
	/* 功  能 类型声明部分的处理函数			处理基本类型	        	*/
	/* 产生式 < baseType > ::=  INTEGER | CHAR                          */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void baseType(TreeNode t)
	{
		if(token.Lex.equals("INTEGER"))
		{
			match("INTEGER");
			t.kind = "IntegerK";   
		}
		else if(token.Lex.equals("CHAR"))
		{
			match("CHAR");
			t.kind = "CharK";
		}
		else 
		{
			readNextToken();
		}
	}
	
	
	/********************************************************************/
	/* 函数名 structureType		 							            */
	/* 功  能 类型声明部分的处理函数			处理结构类型			        	*/
	/* 产生式 < structureType > ::=  arrayType | recType                */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void structureType(TreeNode t)
	{
		if(token.Lex.equals("ARRAY"))
		{
			arrayType(t);
		}
		else if(token.Lex.equals("RECORD"))
		{
			t.kind = "RecordK";
			recType(t);
		}
		else 
		{
			readNextToken();
		}
	}
	
	
	/********************************************************************/
	/* 函数名 arrayType		 							                */
	/* 功  能 类型声明部分的处理函数				处理数组类型		        	*/
	/* 产生式 < arrayType > ::=  ARRAY [low..top] OF baseType           */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void arrayType(TreeNode t)
	{
		t.attr.arrayAttr = new ArrayAttr();
		match("ARRAY");
		match("LMIDPAREN");
		if(token.Lex.equals("INTC"))
		{
			t.attr.arrayAttr.low = Integer.parseInt(token.Sem);
		}
		match("INTC");
		match("UNDERANGE");
		if(token.Lex.equals("INTC"))
		{
			t.attr.arrayAttr.up = Integer.parseInt(token.Sem);
		}
		match("INTC");
		match("RMIDPAREN");
		match("OF");
		baseType(t);
		t.attr.arrayAttr.childtype = t.kind;
		t.kind = "ArrayK";
	}
	
	
	/********************************************************************/
	/* 函数名 recType		 							                */
	/* 功  能 类型声明部分的处理函数				处理记录类型		        	*/
	/* 产生式 < recType > ::=  RECORD fieldDecList END                  */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void recType(TreeNode t)
	{
		TreeNode p = null;
		match("RECORD");
		p = fieldDecList();
		if(p!=null)
		{
			t.child[0] = p;
		}
		else
		{
			syntaxError("A record body is requested!");
		}
		match("END");
	}
	
	
	/********************************************************************/
	/* 函数名 fieldDecList		 							            */
	/* 功  能 类型声明部分的处理函数			处理域			        	*/
	/* 产生式 < fieldDecList > ::=   baseType idList ; fieldDecMore     */
	/*                             | arrayType idList; fieldDecMore     */ 
	/*说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点   */
	/********************************************************************/
	TreeNode fieldDecList()
	{
		TreeNode t = newNode("DecK");
		TreeNode p = null;
		if(t!=null)
		{
			if((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR")))
			{
				baseType(t);
				idList(t);
				match("SEMI");
				p = fieldDecMore();
			}
			else if((token.Lex.equals("ARRAY")))
			{
				arrayType(t);
				idList(t);
				match("SEMI");
				p = fieldDecMore();
			}
			else
			{
				readNextToken();
				syntaxError("Type name is expected!");
			}
			t.sibling = p;
		}
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 fieldDecMore		 							            */
	/* 功  能 类型声明部分的处理函数		处理域				        	*/
	/* 产生式 < fieldDecMore > ::=  ε | fieldDecList                   */ 
	/*说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点   */
	/********************************************************************/
	TreeNode fieldDecMore()
	{
		TreeNode t =null;
		if(token.Lex.equals("INTEGER")||token.Lex.equals("CHAR"))
		{
			t  = fieldDecList();
		}
		else if(token.Lex.equals("END"))
		{	
		}
		else
		{
			readNextToken();
		}
		return t;
		
	}
	
	
	/********************************************************************/
	/* 函数名 idList		 						     	            */
	/* 功  能 类型声明部分的处理函数			处理标识符			        	*/
	/* 产生式 < idList > ::=  id  idMore                                */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void idList(TreeNode t)
	{
		if(token.Lex.equals("ID"))
		{
			t.name[(t.idnum)] = token.Sem;
			t.idnum = t.idnum + 1;
			match("ID");
		}
		idMore(t);
	}
	
	
	/********************************************************************/
	/* 函数名 idMore		 						     	            */
	/* 功  能 类型声明部分的处理函数		处理标识符				        	*/
	/* 产生式 < idMore > ::=  ε |  , idList                            */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void idMore(TreeNode t)
	{
		if(token.Lex.equals("COMMA"))
		{
			match("COMMA");
			idList(t);
		}
		else if(token.Lex.equals("SEMI"))
		{
		}
		else 
		{
			readNextToken();
		}
	}
	
	
	
	/**************************变量声明部分******************************/

	/********************************************************************/
	/* 函数名 varDec		 						     	            */
	/* 功  能 变量声明部分的处理函数						        	*/
	/* 产生式 < varDec > ::=  ε |  varDeclaration                      */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode varDec()
	{
		TreeNode t = null;
		if(token.Lex.equals("VAR"))
		{
			t = varDeclaration();
		}
		else if((token.Lex.equals("PROCEDURE"))||(token.Lex.equals("BEGIN")))
		{
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名 varDeclaration		 						            */
	/* 功  能 变量声明部分的处理函数						        	*/
	/* 产生式 < varDeclaration > ::=  VAR  varDecList                   */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode varDeclaration()
	{
		match("VAR");
		TreeNode t = varDecList();
		if(t==null)
		{
			syntaxError("A var declaration is expected!");
		}
		return t;
		
	}
	
	
	/********************************************************************/
	/* 函数名 varDecList		 						                */
	/* 功  能 变量声明部分的处理函数						        	*/
	/* 产生式 < varDecList > ::=  typeName varIdList; varDecMore        */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode varDecList()
	{
		TreeNode t = newNode("DecK");
		TreeNode p = null;
		if(t!=null)
		{
			typeName(t);
			varIdList(t);
			match("SEMI");
			p = varDecMore();
			t.sibling = p;
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名 varDecMore		 						                */
	/* 功  能 变量声明部分的处理函数						        	*/
	/* 产生式 < varDecMore > ::=  ε |  varDecList                      */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode varDecMore()
	{
		TreeNode t =null;
		if((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR"))||(token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD"))||(token.Lex.equals("ID")))
		{
			t = varDecList();
		}
		else if((token.Lex.equals("PROCEDURE"))||(token.Lex.equals("BEGIN")))
		{
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 varIdList		 						                    */
	/* 功  能 变量声明部分的处理函数		 处理标识符				        	*/
	/* 产生式 < varIdList > ::=  id  varIdMore                          */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void varIdList(TreeNode t)
	{
		if(token.Lex.equals("ID"))
		{
			t.name[(t.idnum)] = token.Sem;
			t.idnum = t.idnum + 1;
			match("ID");
		}
		else
		{
			syntaxError("A varid is expected here!");
			readNextToken();
			
		}
		varIdMore(t);
	}
	
	
	/********************************************************************/
	/* 函数名 varIdMore		 						                    */
	/* 功  能 变量声明部分的处理函数			处理标识符			        	*/
	/* 产生式 < varIdMore > ::=  ε |  , varIdList                      */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/    
	void varIdMore(TreeNode t)
	{
		if(token.Lex.equals("COMMA"))
		{
			match("COMMA");
			varIdList(t);
		}
		else if(token.Lex.equals("SEMI"))
		{
		}
		else 
		{
			readNextToken();
		}
	}
	
	
	/****************************过程声明部分****************************/

	/********************************************************************/
	/* 函数名 procDec		 						                    */
	/* 功  能 函数声明部分的处理函数						        	*/
	/* 产生式 < procDec > ::=  ε |  procDeclaration                    */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode procDec()
	{
		TreeNode t = null;
		if(token.Lex.equals("PROCEDURE"))
		{
			t = procDeclaration();
		}
		else if(token.Lex.equals("BEGIN"))
		{			
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名 procDeclaration		 						            */
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
	TreeNode procDeclaration()
	{
		TreeNode t = newNode("ProcDecK");
		match("PROCEDURE");
		if(token.Lex.equals("ID"))
		{
			t.name[0] = token.Sem;
			t.idnum = t.idnum + 1;
			match("ID");
		}
		match("LPAREN");
		paramList(t);
		match("RPAREN");
		match("SEMI");
		t.child[1] = procDecPart();
		t.child[2] = procBody();
		t.sibling = procDecMore();
		return t;
	}
	
	/********************************************************************/
	/* 函数名 procDec	More	 						                    */
	/* 功  能 函数声明部分的处理函数						        	*/
	/* 产生式 < procDec > ::=  ε |  procDeclaration                    */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode procDecMore()
	{
		TreeNode t = null;
		if(token.Lex.equals("PROCEDURE"))
		{
			t = procDeclaration();
		}
		else if(token.Lex.equals("BEGIN"))
		{
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名 paramList		 						                    */
	/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
	/* 产生式 < paramList > ::=  ε |  paramDecList                     */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void paramList(TreeNode t)
	{
		TreeNode p = null;
		if((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR"))||(token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD"))||(token.Lex.equals("ID"))||(token.Lex.equals("VAR")))
		{
			p = paramDecList();
			t.child[0] = p;
			
		}
		else if(token.Lex.equals("RPAREN"))
		{
		}
		else
		{
			readNextToken();
		}
	}
	
	
	/********************************************************************/
	/* 函数名 paramDecList		 			    	                    */
	/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
	/* 产生式 < paramDecList > ::=  param  paramMore                    */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode paramDecList()
	{
		TreeNode t = param();
		TreeNode p = paramMore();
		if(p!=null)
		{
			t.sibling = p;
		}
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 paramMore		 			    	                        */
	/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
	/* 产生式 < paramMore > ::=  ε | ; paramDecList                     */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode paramMore()
	{
		TreeNode t = null;
		if(token.Lex.equals("SEMI"))
		{
			match("SEMI");
			t = paramDecList();
			if(t==null)
			{
				syntaxError("A param declaration is request!");
			}
		}
		else if(token.Lex.equals("PRAREN"))
		{
		}
		else 
		{
			readNextToken();
		}
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 param		 			    	                            */
	/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
	/* 产生式 < param > ::=  typeName formList | VAR typeName formList  */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode param()
	{
		TreeNode t = newNode("DecK");
		if((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR"))||(token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD"))||(token.Lex.equals("ID")))
		{
			t.attr.procAttr = new ProcAttr();
			t.attr.procAttr.paramt = "valparamType";
			typeName(t);
			formList(t);
		}
		else if(token.Lex.equals("VAR"))
		{
			match("VAR");
			t.attr.procAttr = new ProcAttr();
			t.attr.procAttr.paramt = "varparamType";
			typeName(t);
			formList(t);
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 formList		 			    	                        */
	/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
	/* 产生式 < formList > ::=  id  fidMore                             */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void formList(TreeNode t)
	{
		if(token.Lex.equals("ID"))
		{
			t.name[(t.idnum)] = token.Sem;
			t.idnum = t.idnum + 1;
			match("ID");
		}
		fidMore(t);
	}
	
	
	/********************************************************************/
	/* 函数名 fidMore		 			    	                        */
	/* 功  能 函数声明中参数声明部分的处理函数	        	        	*/
	/* 产生式 < fidMore > ::=   ε |  , formList                        */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	void fidMore(TreeNode t)
	{
		if(token.Lex.equals("COMMA"))
		{
			match("COMMA");
			formList(t);
		}
		else if((token.Lex.equals("SEMI"))||(token.Lex.equals("RPAREN")))
		{
		}
		else
		{
			readNextToken();
		}
	}
	
	/********************************************************************/
	/* 函数名 procDecPart		 			  	                        */
	/* 功  能 函数中的声明部分的处理函数	             	        	*/
	/* 产生式 < procDecPart > ::=  declarePart                          */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode procDecPart()
	{
		TreeNode t = declarePart();
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 procBody		 			  	                            */
	/* 功  能 函数体部分的处理函数	                    	        	*/
	/* 产生式 < procBody > ::=  programBody                             */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode procBody()
	{
		TreeNode t = programBody();
		if(t==null)
		{
			syntaxError("A program body is requested!");
		}
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 programBody		 			  	                        */
	/* 功  能 程序体部分的处理函数	                    	        	*/
	/* 产生式 < programBody > ::=  BEGIN  stmList   END                 */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode programBody()
	{
		TreeNode t = newNode("StmLK");
		match("BEGIN");
		t.child[0] = stmList();
		match("END");
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 stmList		 			  	                            */
	/* 功  能 语句部分的处理函数	                    	        	*/
	/* 产生式 < stmList > ::=  stm    stmMore                           */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode stmList()
	{
		TreeNode t = stm();
		TreeNode p = stmMore();
		if(t!=null)
		{
			if(p!=null)
			{
				t.sibling = p;
			}
		}
		return t;
	}
	
	
	
	/********************************************************************/
	/* 函数名 stmMore		 			  	                            */
	/* 功  能 语句部分的处理函数	                    	        	*/
	/* 产生式 < stmMore > ::=   ε |  ; stmList                         */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode stmMore()
	{
		TreeNode t = null;
		if((token.Lex.equals("ELSE"))||(token.Lex.equals("FI"))||(token.Lex.equals("END"))||(token.Lex.equals("ENDWH")))
		{
		}
		else if(token.Lex.equals("SEMI"))
		{
			match("SEMI");
			t = stmList();
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名 stm   		 			  	                            */
	/* 功  能 语句部分的处理函数	                    	        	*/
	/* 产生式 < stm > ::=   conditionalStm   {IF}                       */
	/*                    | loopStm          {WHILE}                    */
	/*                    | inputStm         {READ}                     */
	/*                    | outputStm        {WRITE}                    */
	/*                    | returnStm        {RETURN}                   */
	/*                    | id  assCall      {id}                       */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode stm()
	{
		TreeNode t = null;
		if(token.Lex.equals("IF"))
		{
			t = conditionalStm();
		}
		else if(token.Lex.equals("WHILE"))
		{
			t = loopStm();
		}
		else if(token.Lex.equals("READ"))
		{
			t = inputStm();
		}
		else if(token.Lex.equals("WRITE"))
		{
			t = outputStm();
		}
		else if(token.Lex.equals("RETURN"))
		{
			t = returnStm();
		}
		else if(token.Lex.equals("ID"))
		{
			temp_name = token.Sem;
			match("ID");
			t = assCall();
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	/********************************************************************/
	/* 函数名 assCall		 			  	                            */
	/* 功  能 语句部分的处理函数	               处理赋值和调用      	        	*/
	/* 产生式 < assCall > ::=   assignmentRest   {:=,LMIDPAREN,DOT}     */
	/*                        | callStmRest      {(}                    */  
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode assCall()
	{
		TreeNode t = null;
		if((token.Lex.equals("ASSIGN"))||(token.Lex.equals("LMIDPAREN"))||(token.Lex.equals("DOT")))
		{
			t = assignmentRest();
		}
		else if(token.Lex.equals("LPAREN"))
		{
			t = callStmRest();
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 assignmentRest		 			                        */
	/* 功  能 赋值语句部分的处理函数	                    	        */
	/* 产生式 < assignmentRest > ::=  variMore : = exp                  */ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode assignmentRest()
	{
		TreeNode t = newStmtNode("AssignK");
		 /* 赋值语句节点的第一个儿子节点记录赋值语句的左侧变量名，
	    /* 第二个儿子结点记录赋值语句的右侧表达式*/
	    /*处理第一个儿子结点，为变量表达式类型节点*/
		TreeNode c = newExpNode("VariK");
		c.name[0] = temp_name;
		c.idnum = c.idnum + 1;
		variMore(c);
		t.child[0] = c;
		match("ASSIGN");			//赋值号匹配
		t.child[1] = exp();   		//处理第二个儿子节点
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 conditionalStm		 			                        */
	/* 功  能 条件语句部分的处理函数	                    	        */
	/* 产生式 < conditionalStm > ::= IF exp THEN stmList ELSE stmList FI*/ 
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode conditionalStm()
	{
		TreeNode t = newStmtNode("IfK");
		match("IF");
		t.child[0] = exp();
		match("THEN");
		if(t!=null)
		{
			t.child[1] = stmList();
		}
		if(token.Lex.equals("ELSE"))
		{
			match("ELSE");
			t.child[2] = stmList();
			
		}
		match("FI");
		return t;
	}
	

	/********************************************************************/
	/* 函数名 loopStm          		 			                        */
	/* 功  能 循环语句部分的处理函数	                    	        */
	/* 产生式 < loopStm > ::=      WHILE exp DO stmList ENDWH           */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode loopStm()
	{
		TreeNode t = newStmtNode("WhileK");
		match("WHILE");
		t.child[0] = exp();
		match("DO");
		t.child[1] = stmList();
		match("ENDWH");
		return  t;
	}
	
	
	/********************************************************************/
	/* 函数名 inputStm          		     	                        */
	/* 功  能 输入语句部分的处理函数	                    	        */
	/* 产生式 < inputStm > ::=    READ(id)                              */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode inputStm()
	{
		TreeNode t = newStmtNode("ReadK");
		match("READ");
		match("LPAREN");
		if(token.Lex.equals("ID"))
		{
			t.name[0] = token.Sem;
			t.idnum = t.idnum + 1;
			
		}
		match("ID");
		match("RPAREN");
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 outputStm          		     	                        */
	/* 功  能 输出语句部分的处理函数	                    	        */
	/* 产生式 < outputStm > ::=   WRITE(exp)                            */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode outputStm()
	{
		TreeNode t = newStmtNode("WriteK");
		match("WRITE");
		match("LPAREN");
		t.child[0] = exp();
		match("RPAREN");
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 returnStm          		     	                        */
	/* 功  能 返回语句部分的处理函数	                    	        */
	/* 产生式 < returnStm > ::=   RETURN                                */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode returnStm()
	{
		TreeNode t = newStmtNode("ReturnK");
		match("RETURN");
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 callStmRest          		     	                        */
	/* 功  能 函数调用语句部分的处理函数	                  	        */
	/* 产生式 < callStmRest > ::=  (actParamList)                       */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode callStmRest()
	{
		TreeNode t = newStmtNode("CallK");
		match("LPAREN");
		TreeNode c = newExpNode("VariK");
		c.name[0] =	temp_name;
		c.idnum = c.idnum + 1;
		t.child[0] = c;
		t.child[1] = actParamList();
		match("RPAREN");
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 actParamList          		   	                        */
	/* 功  能 函数调用实参部分的处理函数	                	        */
	/* 产生式 < actParamList > ::=     ε |  exp actParamMore           */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode actParamList()
	{
		TreeNode t = null;
		if(token.Lex.equals("RPAREN"))
		{
		}
		else if((token.Lex.equals("ID"))||(token.Lex.equals("INTC")))
		{
			t = exp();
			if(t!= null)
			{
				t.sibling = actParamMore();
			}
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 actParamMore          		   	                        */
	/* 功  能 函数调用实参部分的处理函数	                	        */
	/* 产生式 < actParamMore > ::=     ε |  , actParamList             */
	/* 说  明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点  */
	/********************************************************************/
	TreeNode actParamMore()
	{
		TreeNode t = null;
		if(token.Lex.equals("RPAREN"))
		{
		}
		else if(token.Lex.equals("COMMA"))
		{
			match("COMMA");
			t = actParamList();
		}
		else
		{
			readNextToken();
		}
		return t;
	}
	
	
	/*************************表达式部分********************************/
	
	/****************************************************************************/
	/* 函数名 exp																*/
	/* 功  能 表达式处理函数													*/
	/* 产生式 < 表达式 > ::= < 简单表达式 > [< 关系运算符 > < 简单表达式 > ]	*/
	/* 说  明 该函数根据产生式调用相应递归处理函数,生成表达式类型语法树节点		*/
	/****************************************************************************/
	TreeNode exp()
	{
		TreeNode t = simple_exp();			//调用简单表达式处理函数simple_exp(),返回语法树节点指针给t
		if((token.Lex.equals("LT"))||(token.Lex.equals("EQ")))		//当前单词token为逻辑运算单词LT或者EQ
		{
			TreeNode p = newExpNode("OpK");				//新语法树节点p创建成功,初始化p第一个子节点成员child[0]
			p.child[0] = t;								
			p.attr.expAttr.op = token.Lex;			// 并将当前单词token(为EQ或者LT)赋给语法树节点p的运算符成员attr.op
			t = p;									//将新的表达式类型语法树节点p作为函数返回值t
			match(token.Lex);						//当前单词token与指定逻辑运算符单词(为EQ或者LT)匹配
			
/* 语法树节点t非空,调用简单表达式处理函数simple_exp()	 函数返回语法树节点指针给t的第二子节点成员child[1]	*/
			if(t!=null)
			{
				t.child[1] = simple_exp();
			}
			
		}
		return t;
	}
	
	
	/************************************************************************/
	/* 函数名 simple_exp													*/
	/* 功  能 简单表达式处理函数											*/
	/* 产生式 < 简单表达式 >::=	< 项 > { < 加法运算符 > < 项 > }			*/
	/* 说  明 该函数根据产生式调用相应递归处理函数,生成表达式类型语法树节点	*/
	/************************************************************************/
	TreeNode simple_exp()
	{
		TreeNode t = term();		//调用元处理函数term(),函数返回语法树节点指针给t
		while((token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS")))		// 当前单词token为加法运算符单词PLUS或MINUS
		{
			TreeNode p = newExpNode("OpK");			//创建新OpK表达式类型语法树节点，新语法树节点指针赋给p
			p.child[0] = t;							//语法树节点p创建成功,初始化p第一子节点成员child[0]	
			p.attr.expAttr.op = token.Lex;			//返回语法树节点指针给p的运算符成员attr.op
			t = p;									//将函数返回值t赋成语法树节点p
			match(token.Lex);						//当前单词token与指定加法运算单词(为PLUS或MINUS)匹配
			t.child[1] = term();					//调用元处理函数term(),函数返回语法树节点指针给t的第二子节点成员child[1]
		}
		return t;
	}
	
	/****************************************************************************/
	/* 函数名 term																*/
	/* 功  能 项处理函数														*/
	/* 产生式 < 项 > ::= < 因子 > { < 乘法运算符 > < 因子 > }					*/
	/* 说  明 该函数根据产生式调用相应递归处理函数,生成表达式类型语法树节点		*/
	/****************************************************************************/
	TreeNode term()
	{
		TreeNode t = factor();		//调用因子处理函数factor(),函数返回语法树节点指针给t 
		while((token.Lex.equals("TIMES"))||(token.Lex.equals("OVER")))  	//当前单词token为乘法运算符单词TIMES或OVER
		{
			TreeNode p = newExpNode("OpK");			//创建新的OpK表达式类型语法树节点,新节点指针赋给p
			p.child[0] = t;						//新语法树节点p创建成功,初始化第一个子节点成员child[0]为t
			p.attr.expAttr.op = token.Lex;		//将当前单词token赋值给语法树节点p的运算符成员attr.op
			t = p;
			match(token.Lex);					//当前单词token与指定乘法运算符单词(为TIMES或OVER)匹配 
			p.child[1] = factor();				//调用因子处理函数factor(),函数返回语法树节点指针赋给p第二个子节点成员child[1]
		}
		return t;
	}
	
	
	/****************************************************************************/
	/* 函数名 factor															*/
	/* 功  能 因子处理函数														*/
	/* 产生式 factor ::= ( exp ) | INTC | variable                  			*/
	/* 说  明 该函数根据产生式调用相应的递归处理函数,生成表达式类型语法树节点	*/
	/****************************************************************************/
	TreeNode factor()
	{
		TreeNode t = null;
		if(token.Lex.equals("INTC"))
		{
			t = newExpNode("ConstK");			//创建新的ConstK表达式类型语法树节点,赋值给t
			t.attr.expAttr.val = Integer.parseInt(token.Sem);  //新语法树节点t创建成功,当前单词token为数字单词NUM	将当前单词名tokenString转换为整数并赋给语法树节点t的数值成员attr.val
			match("INTC");				//当前单词token与数字单词NUM匹配
		}
		else if(token.Lex.equals("ID"))
		{
			t = variable();				//创建新的IdK表达式类型语法树节点t
		}
		else if(token.Lex.equals("LPAREN"))
		{
			match("LPAREN");		//当前单词token与左括号单词LPAREN匹配
			t = exp();				//调用表达式处理函数exp(),函数返回语法树节点指针给t
			match("RPAREN");
		}
		else 					//当前单词token为其它单词
		{
			readNextToken();
		}
		return t;
	}
	
	
	
	/********************************************************************/
	/* 函数名 variable													*/
	/* 功  能 变量处理函数												*/
	/* 产生式 variable   ::=   id variMore                   			*/
	/* 说  明 该函数根据产生式,	处理变量，生成其语法树节点              */
	/********************************************************************/
	TreeNode variable()
	{
		TreeNode t = newExpNode("VariK");
		if(token.Lex.equals("ID"))
		{
			t.name[0] = token.Sem;
			t.idnum = t.idnum + 1;
		}
		match("ID");
		variMore(t);
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 variMore													*/
	/* 功  能 变量处理函数												*/
	/* 产生式 variMore   ::=  ε                             			*/
	/*                       | [exp]            {[}                     */
	/*                       | . fieldvar       {DOT}                   */ 
	/* 说  明 该函数根据产生式调用相应的递归处理变量中的几种不同类型	*/
	/********************************************************************/		
	void variMore(TreeNode t)
	{
		if((token.Lex.equals("EQ"))||(token.Lex.equalsIgnoreCase("LT"))||(token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS"))||(token.Lex.equals("RPAREN"))||(token.Lex.equals("RMIDPAREN"))||(token.Lex.equals("SEMI"))||(token.Lex.equals("COMMA"))||(token.Lex.equals("THEN"))||(token.Lex.equals("ELSE"))||(token.Lex.equals("FI"))||(token.Lex.equals("DO"))||(token.Lex.equals("END"))||(token.Lex.equals("ENDWH"))||(token.Lex.equals("ASSIGN"))||(token.Lex.equals("TIMES"))||(token.Lex.equals("OVER")))
				{}
		else if(token.Lex.equals("LMIDPAREN"))
		{
			match("LMIDPAREN");
			t.child[0] = exp();				//用来以后求出其表达式的值，送入用于数组下标计算
			t.attr.expAttr.varkind = "ArrayMembV";		//此表达式为数组成员变量类型
			match("RMIDPAREN");
		}
		else if(token.Lex.equals("DOT"))
		{
			match("DOT");
			t.child[0] = fieldVar();			//第一个儿子指向域成员变量结点
			t.attr.expAttr.varkind = "FieldMembV";
		}
		else
		{
			readNextToken();
		}
		
	}
	
	
	/********************************************************************/
	/* 函数名 fieldVar													*/
	/* 功  能 变量处理函数				处理域变量								*/
	/* 产生式 fieldvar   ::=  id  fieldvarMore                          */ 
	/* 说  明 该函数根据产生式，处理域变量，并生成其语法树节点       	*/
	/********************************************************************/
	TreeNode fieldVar()
	{
		TreeNode t = newExpNode("VariK");
		if(token.Lex.equals("ID"))
		{
			t.name[0] = token.Sem;
			t.idnum = t.idnum + 1;
			
		}
		match("ID");
		fieldVarMore(t);
		return t;
	}
	
	
	/********************************************************************/
	/* 函数名 fieldVarMore  											*/
	/* 功  能 变量处理函数			处理域变量									*/
	/* 产生式 fieldvarMore   ::=  ε                             		*/
	/*                           | [exp]            {[}                 */ 
	/* 说  明 该函数根据产生式调用相应的递归处理域变量为数组类型的情况	*/
	/********************************************************************/
	void fieldVarMore(TreeNode t)
	{
		if((token.Lex.equals("ASSIGN"))||(token.Lex.equals("TIMES"))||(token.Lex.equals("EQ"))||(token.Lex.equals("LT"))||(token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS"))||(token.Lex.equals("OVER"))||(token.Lex.equals("RPAREN"))||(token.Lex.equals("SEMI"))||(token.Lex.equals("COMMA"))||(token.Lex.equals("THEN"))||(token.Lex.equals("ELSE"))||(token.Lex.equals("FI"))||(token.Lex.equals("DO"))||(token.Lex.equals("ENDWH"))||(token.Lex.equals("END")))
		{
		}
		else if(token.Lex.equals("LMIDPAREN"))
		{
			match("LMIDPAREN");
			t.child[0] = exp();
			t.child[0].attr.expAttr.varkind = "ArrayMembV";
			match("RMIDPAREN");
		}
		else
		{
			readNextToken();
		}
		
	}
	
	
	/************************相关辅助函数************************************/
	/********************************************************************/
	/* 函数名 match														*/
	/* 功  能 终极符匹配处理函数										*/
	/* 说  明 函数参数expected给定期望单词符号与当前单词符号token相匹配	*/
	/*        如果不匹配,则报非期望单词语法错误							*/
	/********************************************************************/
	void match(String expected)
	{
		
		if(token.Lex.equals(expected))
		{
			readNextToken();
		}
		else
		{
			syntaxError("Not match Error!");
		}
		readNextToken();
	}
	
	/********************************************************************/
	/* 函数名 syntaxError												*/
	/* 功  能 语法错误处理函数											*/
	/* 说  明 将函数参数message指定的错误信息格式化写入列表文件listing	*/
	/*		  设置错误追踪标志Error为TRUE								*/
	/********************************************************************/
	void syntaxError(String s)
	{
		serror = serror+"\n>>> ERROR:" + "Syntax error at "+String.valueOf(token.Lineshow)+" line: "+s;
		Error = true;
	}
	
	/*****************************************************************/
	/* 函数名 readNextToken							        		 */	
	/* 功  能 将文件tokenlist中的信息作为返回值                      */							    
	/*        一般，listing指向标准输出。                            */
	/* 说  明 返回值为Token类型，用于语法分析中                  */
	/*****************************************************************/
	void readNextToken()
	{
		if(analyse.hasMoreTokens())
		{
			int i = 1;
			String stok = analyse.nextToken();
			StringTokenizer analyse1 = new StringTokenizer(stok,":,");
			while(analyse1.hasMoreTokens())
			{
				String fstok = analyse1.nextToken();
				if(i == 1)
				{
					token.Lineshow = Integer.parseInt(fstok);
					lineno = token.Lineshow;
				}
				if(i == 2)
				{
					token.Lex = fstok;
				}
				if(i == 3)
				{
					token.Sem = fstok;
				}
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
		TreeNode t = new TreeNode();
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
		TreeNode t = new TreeNode();
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
		TreeNode t = new TreeNode();
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
	void writeStr(String s)
	{
		stree = stree + s;
		
	}
	
	/********************************************************/
	 /*写空格函数*/
	/********************************************************/
	void writeSpace()
	{
		stree = stree + " ";
	}
	
	
	/********************************************************/
	 /*写换行符和4个空格函数*/
	/********************************************************/
	void writeTab(int x)
	{
		stree = stree + "\n";
		while(x!=0)
		{
			stree = stree + "\t";
			x--;
		}
	}
	
	
	/******************************************************/
	/* 函数名 printTree                                   */
	/* 功  能 把语法树输出，显示在文件中           */
	/******************************************************/
	void printTree(TreeNode t,int l)
	{
		TreeNode tree = t;
		while(tree != null)
		{
			if(tree.nodekind.equals("ProcK"))
			{
				stree = "ProcK";
				
			}
			else if(tree.nodekind.equals("PheadK"))
			{
				writeTab(1);
				writeStr("PheadK");
				writeSpace();
				writeStr(tree.name[0]);
			}
			else if(tree.nodekind.equals("DecK"))
			{
				writeTab(1);
				writeStr("DecK");
				writeSpace();
				if(tree.attr.procAttr!=null)
				{
					if(tree.attr.procAttr.paramt.equals("varparamType"))
					{
						writeStr("Var param:");
					}
					else if(tree.attr.procAttr.paramt.equals("valparamType"))
					{
						writeStr("Value param:");
					}
				}
				if(tree.kind.equals("ArrayK"))
				{
					writeStr("ArrayK");
					writeSpace();
					writeStr(String.valueOf(tree.attr.arrayAttr.low));
					writeSpace();
					writeStr(String.valueOf(tree.attr.arrayAttr.up));
					writeSpace();
					if(tree.attr.arrayAttr.childtype.equals("CharK"))
					{
						writeStr("CharK");
					}
					else if(tree.attr.arrayAttr.childtype.equals("IntegerK"))
					{
						writeStr("IntegerK");
					}
				}
				else if(tree.kind.equals("CharK"))
				{
					writeStr("CharK");
				}
				else if(tree.kind.equals("IntegerK"))
				{
					writeStr("IntegerK");
				}
				else if(tree.kind.equals("RecordK"))
				{
					writeStr("RecordK");
				}
				else if(tree.kind.equals("IdK"))
				{
					writeStr("IdK");
					writeStr(tree.attr.type_name);
				}
				else
				{
					syntaxError("error!");
				}
				if(tree.idnum!=0)
				{
					for(int i=0;i<(tree.idnum);i++)
					{
						writeSpace();
						writeStr(tree.name[i]);
					}
				}
				else
				{
					syntaxError("Wrong! No var!");
				}
			}
			else if(tree.nodekind.equals("TypeK"))
			{
				writeTab(1);
				writeStr("TypeK");
			}
			else if(tree.nodekind.equals("VarK"))
			{
				writeTab(1);
				writeStr("VarK");
			}
			else if(tree.nodekind.equals("ProcDecK"))
			{
				writeTab(1);
				writeStr("ProcDecK");
				writeSpace();
				writeStr(tree.name[0]);
			}
			else if(tree.nodekind.equals("StmLK"))		//处理语句序列
			{
				writeTab(1);
				writeStr("StmLK");
			}
			else if(tree.nodekind.equals("StmtK"))		//处理语句
			{
				writeTab(1);
				writeStr("StmtK");
				writeSpace();
				if(tree.kind.equals("IfK"))
				{
					writeStr("If");
				}
				else if (tree.kind.equals("WhileK"))
				{
					writeStr("While");
				}
				else if(tree.kind.equals("AssignK"))		//处理赋值
				{
					writeStr("AssignK");
				}
				else if(tree.kind.equals("ReadK"))
				{
					writeStr("Read");
					writeSpace();
					writeStr(tree.name[0]);
				}
				else if(tree.kind.equals("WriteK"))
				{
					writeStr("Write");
				}
				else if(tree.kind.equals("CallK"))
				{
					writeStr("Call");
				}
				else if(tree.kind.equals("ReturnK"))
				{
					writeStr("Return");
				}
				else 
				{
					syntaxError("error2!");
				}
			}
			else if(tree.nodekind.equals("ExpK"))
			{
				writeTab(1);
				writeStr("ExpK");
				if(tree.kind.equals("OpK"))
				{
					writeSpace();
					writeStr("Op");
					writeSpace();
					if(tree.attr.expAttr.op.equals("EQ"))
					{
						writeStr("=");
					}
					else if(tree.attr.expAttr.op.equals("LT"))
					{
						writeStr("<");
					}
					else if(tree.attr.expAttr.op.equals("PLUS"))
					{
						writeStr("+");
					}
					else if(tree.attr.expAttr.op.equals("MINUS"))
					{
						writeStr("-");
					}
					else if(tree.attr.expAttr.op.equals("TIMES"))
					{
						writeStr("*");
					}
					else if(tree.attr.expAttr.op.equals("OVER"))
					{
						writeStr("/");
					}
					else
					{
						syntaxError("error3!");
					}
				}
				else if(tree.kind.equals("ConstK"))
				{
					writeSpace();
					writeStr("Const");
					writeSpace();
					writeStr(String.valueOf(tree.attr.expAttr.val));
				}
				else if(tree.kind.equals("VariK"))		//处理变量
				{
					writeSpace();
					writeStr("Vari");
					writeSpace();
					if(tree.attr.expAttr.varkind.equals("IdV"))
					{
						writeStr("Id");
						writeSpace();
						writeStr(tree.name[0]);
					}
					else if(tree.attr.expAttr.varkind.equals("FieldMembV"))		//处理域变量
					{
						writeStr("FieldMember");
						writeSpace();
						writeStr(tree.name[0]);
					}
					else if(tree.attr.expAttr.varkind.equals("ArrayMembV"))
					{
						writeStr("ArrayMember");
						writeSpace();
						writeStr(tree.name[0]);
					}
					else
					{
						syntaxError("Var type error!");
					}
				}
				else
				{
					syntaxError("error4!");
				}
			}
			else
			{
				syntaxError("error5!");
			}

			for(int i=0;i<3;i++)			//对语法树结点tree的各子结点递归调用printTree过程
			{
				printTree(tree.child[i],l+1);
			}
			tree = tree.sibling;			//对语法树结点tree的各兄弟结点递归调用printTree过程		
		}
	}
}
