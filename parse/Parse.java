package parse;
/**
 * �﷨������
 * Parse.java
 * @author ������
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
	/* ��  �� �ܳ���Ĵ�����								        	*/
	/* ����ʽ < program > ::= programHead declarePart programBody .     */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
	/*        �﷨���ĸ��ڵ�ĵ�һ���ӽڵ�ָ�����ͷ����programHead,    */
	/*        DeclaraPartΪprogramHead���ֵܽڵ�,�����岿��programBody  */
	/*        ΪdeclarePart���ֵܽڵ�.                                  */
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
	/* ��  �� ����ͷ�Ĵ�����								        	*/
	/* ����ʽ < programHead > ::= PROGRAM  ProgramName                  */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	
	
	
 /*************************��������***********************************/
	
	/********************************************************************/
	/* ������ declarePart											    */
	/* ��  �� �������ֵĴ�����								     	*/
	/* ����ʽ < declarePart > ::= typeDec  varDec  procDec              */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
	/********************************************************************/
	TreeNode declarePart()
	{
		/*����*/
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
		
		 /*����*/
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
		
		/*����*/
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
	
	
	
	/**************************������������******************************/

	/********************************************************************/
	/* ������ typeDec									     		    */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < typeDec > ::= �� | TypeDeclaration                      */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������typeDeclaration									  	    */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < TypeDeclaration > ::= TYPE  TypeDecList                 */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ typeDecList		 							  	        */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < TypeDecList > ::= typeId = typeName ; typeDecMore       */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ typeDecMore		 							            */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < typeDecMore > ::=    �� | TypeDecList                   */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ typeId		 							  	            */
	/* ��  �� �����������ֵĴ�����	�������ͱ�ʶ��			        	*/
	/* ����ʽ < typeId > ::= id                                         */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ typeName		 							  	            */
	/* ��  �� �����������ֵĴ�����		����������				        	*/
	/* ����ʽ < typeName > ::= baseType | structureType | id            */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ baseType		 							  	            */
	/* ��  �� �����������ֵĴ�����			�����������	        	*/
	/* ����ʽ < baseType > ::=  INTEGER | CHAR                          */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ structureType		 							            */
	/* ��  �� �����������ֵĴ�����			����ṹ����			        	*/
	/* ����ʽ < structureType > ::=  arrayType | recType                */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ arrayType		 							                */
	/* ��  �� �����������ֵĴ�����				������������		        	*/
	/* ����ʽ < arrayType > ::=  ARRAY [low..top] OF baseType           */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ recType		 							                */
	/* ��  �� �����������ֵĴ�����				�����¼����		        	*/
	/* ����ʽ < recType > ::=  RECORD fieldDecList END                  */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ fieldDecList		 							            */
	/* ��  �� �����������ֵĴ�����			������			        	*/
	/* ����ʽ < fieldDecList > ::=   baseType idList ; fieldDecMore     */
	/*                             | arrayType idList; fieldDecMore     */ 
	/*˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�   */
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
	/* ������ fieldDecMore		 							            */
	/* ��  �� �����������ֵĴ�����		������				        	*/
	/* ����ʽ < fieldDecMore > ::=  �� | fieldDecList                   */ 
	/*˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�   */
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
	/* ������ idList		 						     	            */
	/* ��  �� �����������ֵĴ�����			�����ʶ��			        	*/
	/* ����ʽ < idList > ::=  id  idMore                                */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ idMore		 						     	            */
	/* ��  �� �����������ֵĴ�����		�����ʶ��				        	*/
	/* ����ʽ < idMore > ::=  �� |  , idList                            */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	
	
	
	/**************************������������******************************/

	/********************************************************************/
	/* ������ varDec		 						     	            */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < varDec > ::=  �� |  varDeclaration                      */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ varDeclaration		 						            */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < varDeclaration > ::=  VAR  varDecList                   */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ varDecList		 						                */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < varDecList > ::=  typeName varIdList; varDecMore        */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ varDecMore		 						                */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < varDecMore > ::=  �� |  varDecList                      */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ varIdList		 						                    */
	/* ��  �� �����������ֵĴ�����		 �����ʶ��				        	*/
	/* ����ʽ < varIdList > ::=  id  varIdMore                          */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ varIdMore		 						                    */
	/* ��  �� �����������ֵĴ�����			�����ʶ��			        	*/
	/* ����ʽ < varIdMore > ::=  �� |  , varIdList                      */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	
	
	/****************************������������****************************/

	/********************************************************************/
	/* ������ procDec		 						                    */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < procDec > ::=  �� |  procDeclaration                    */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ procDeclaration		 						            */
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
	/* ������ procDec	More	 						                    */
	/* ��  �� �����������ֵĴ�����						        	*/
	/* ����ʽ < procDec > ::=  �� |  procDeclaration                    */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ paramList		 						                    */
	/* ��  �� ���������в����������ֵĴ�����	        	        	*/
	/* ����ʽ < paramList > ::=  �� |  paramDecList                     */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ paramDecList		 			    	                    */
	/* ��  �� ���������в����������ֵĴ�����	        	        	*/
	/* ����ʽ < paramDecList > ::=  param  paramMore                    */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ paramMore		 			    	                        */
	/* ��  �� ���������в����������ֵĴ�����	        	        	*/
	/* ����ʽ < paramMore > ::=  �� | ; paramDecList                     */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ param		 			    	                            */
	/* ��  �� ���������в����������ֵĴ�����	        	        	*/
	/* ����ʽ < param > ::=  typeName formList | VAR typeName formList  */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ formList		 			    	                        */
	/* ��  �� ���������в����������ֵĴ�����	        	        	*/
	/* ����ʽ < formList > ::=  id  fidMore                             */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ fidMore		 			    	                        */
	/* ��  �� ���������в����������ֵĴ�����	        	        	*/
	/* ����ʽ < fidMore > ::=   �� |  , formList                        */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ procDecPart		 			  	                        */
	/* ��  �� �����е��������ֵĴ�����	             	        	*/
	/* ����ʽ < procDecPart > ::=  declarePart                          */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
	/********************************************************************/
	TreeNode procDecPart()
	{
		TreeNode t = declarePart();
		return t;
	}
	
	
	/********************************************************************/
	/* ������ procBody		 			  	                            */
	/* ��  �� �����岿�ֵĴ�����	                    	        	*/
	/* ����ʽ < procBody > ::=  programBody                             */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ programBody		 			  	                        */
	/* ��  �� �����岿�ֵĴ�����	                    	        	*/
	/* ����ʽ < programBody > ::=  BEGIN  stmList   END                 */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ stmList		 			  	                            */
	/* ��  �� ��䲿�ֵĴ�����	                    	        	*/
	/* ����ʽ < stmList > ::=  stm    stmMore                           */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ stmMore		 			  	                            */
	/* ��  �� ��䲿�ֵĴ�����	                    	        	*/
	/* ����ʽ < stmMore > ::=   �� |  ; stmList                         */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ stm   		 			  	                            */
	/* ��  �� ��䲿�ֵĴ�����	                    	        	*/
	/* ����ʽ < stm > ::=   conditionalStm   {IF}                       */
	/*                    | loopStm          {WHILE}                    */
	/*                    | inputStm         {READ}                     */
	/*                    | outputStm        {WRITE}                    */
	/*                    | returnStm        {RETURN}                   */
	/*                    | id  assCall      {id}                       */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ assCall		 			  	                            */
	/* ��  �� ��䲿�ֵĴ�����	               ����ֵ�͵���      	        	*/
	/* ����ʽ < assCall > ::=   assignmentRest   {:=,LMIDPAREN,DOT}     */
	/*                        | callStmRest      {(}                    */  
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ assignmentRest		 			                        */
	/* ��  �� ��ֵ��䲿�ֵĴ�����	                    	        */
	/* ����ʽ < assignmentRest > ::=  variMore : = exp                  */ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
	/********************************************************************/
	TreeNode assignmentRest()
	{
		TreeNode t = newStmtNode("AssignK");
		 /* ��ֵ���ڵ�ĵ�һ�����ӽڵ��¼��ֵ��������������
	    /* �ڶ������ӽ���¼��ֵ�����Ҳ���ʽ*/
	    /*�����һ�����ӽ�㣬Ϊ�������ʽ���ͽڵ�*/
		TreeNode c = newExpNode("VariK");
		c.name[0] = temp_name;
		c.idnum = c.idnum + 1;
		variMore(c);
		t.child[0] = c;
		match("ASSIGN");			//��ֵ��ƥ��
		t.child[1] = exp();   		//����ڶ������ӽڵ�
		return t;
	}
	
	
	/********************************************************************/
	/* ������ conditionalStm		 			                        */
	/* ��  �� ������䲿�ֵĴ�����	                    	        */
	/* ����ʽ < conditionalStm > ::= IF exp THEN stmList ELSE stmList FI*/ 
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ loopStm          		 			                        */
	/* ��  �� ѭ����䲿�ֵĴ�����	                    	        */
	/* ����ʽ < loopStm > ::=      WHILE exp DO stmList ENDWH           */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ inputStm          		     	                        */
	/* ��  �� ������䲿�ֵĴ�����	                    	        */
	/* ����ʽ < inputStm > ::=    READ(id)                              */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ outputStm          		     	                        */
	/* ��  �� �����䲿�ֵĴ�����	                    	        */
	/* ����ʽ < outputStm > ::=   WRITE(exp)                            */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ returnStm          		     	                        */
	/* ��  �� ������䲿�ֵĴ�����	                    	        */
	/* ����ʽ < returnStm > ::=   RETURN                                */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
	/********************************************************************/
	TreeNode returnStm()
	{
		TreeNode t = newStmtNode("ReturnK");
		match("RETURN");
		return t;
	}
	
	
	/********************************************************************/
	/* ������ callStmRest          		     	                        */
	/* ��  �� ����������䲿�ֵĴ�����	                  	        */
	/* ����ʽ < callStmRest > ::=  (actParamList)                       */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ actParamList          		   	                        */
	/* ��  �� ��������ʵ�β��ֵĴ�����	                	        */
	/* ����ʽ < actParamList > ::=     �� |  exp actParamMore           */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	/* ������ actParamMore          		   	                        */
	/* ��  �� ��������ʵ�β��ֵĴ�����	                	        */
	/* ����ʽ < actParamMore > ::=     �� |  , actParamList             */
	/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
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
	
	
	/*************************���ʽ����********************************/
	
	/****************************************************************************/
	/* ������ exp																*/
	/* ��  �� ���ʽ������													*/
	/* ����ʽ < ���ʽ > ::= < �򵥱��ʽ > [< ��ϵ����� > < �򵥱��ʽ > ]	*/
	/* ˵  �� �ú������ݲ���ʽ������Ӧ�ݹ鴦����,���ɱ��ʽ�����﷨���ڵ�		*/
	/****************************************************************************/
	TreeNode exp()
	{
		TreeNode t = simple_exp();			//���ü򵥱��ʽ������simple_exp(),�����﷨���ڵ�ָ���t
		if((token.Lex.equals("LT"))||(token.Lex.equals("EQ")))		//��ǰ����tokenΪ�߼����㵥��LT����EQ
		{
			TreeNode p = newExpNode("OpK");				//���﷨���ڵ�p�����ɹ�,��ʼ��p��һ���ӽڵ��Աchild[0]
			p.child[0] = t;								
			p.attr.expAttr.op = token.Lex;			// ������ǰ����token(ΪEQ����LT)�����﷨���ڵ�p���������Աattr.op
			t = p;									//���µı��ʽ�����﷨���ڵ�p��Ϊ��������ֵt
			match(token.Lex);						//��ǰ����token��ָ���߼����������(ΪEQ����LT)ƥ��
			
/* �﷨���ڵ�t�ǿ�,���ü򵥱��ʽ������simple_exp()	 ���������﷨���ڵ�ָ���t�ĵڶ��ӽڵ��Աchild[1]	*/
			if(t!=null)
			{
				t.child[1] = simple_exp();
			}
			
		}
		return t;
	}
	
	
	/************************************************************************/
	/* ������ simple_exp													*/
	/* ��  �� �򵥱��ʽ������											*/
	/* ����ʽ < �򵥱��ʽ >::=	< �� > { < �ӷ������ > < �� > }			*/
	/* ˵  �� �ú������ݲ���ʽ������Ӧ�ݹ鴦����,���ɱ��ʽ�����﷨���ڵ�	*/
	/************************************************************************/
	TreeNode simple_exp()
	{
		TreeNode t = term();		//����Ԫ������term(),���������﷨���ڵ�ָ���t
		while((token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS")))		// ��ǰ����tokenΪ�ӷ����������PLUS��MINUS
		{
			TreeNode p = newExpNode("OpK");			//������OpK���ʽ�����﷨���ڵ㣬���﷨���ڵ�ָ�븳��p
			p.child[0] = t;							//�﷨���ڵ�p�����ɹ�,��ʼ��p��һ�ӽڵ��Աchild[0]	
			p.attr.expAttr.op = token.Lex;			//�����﷨���ڵ�ָ���p���������Աattr.op
			t = p;									//����������ֵt�����﷨���ڵ�p
			match(token.Lex);						//��ǰ����token��ָ���ӷ����㵥��(ΪPLUS��MINUS)ƥ��
			t.child[1] = term();					//����Ԫ������term(),���������﷨���ڵ�ָ���t�ĵڶ��ӽڵ��Աchild[1]
		}
		return t;
	}
	
	/****************************************************************************/
	/* ������ term																*/
	/* ��  �� �����														*/
	/* ����ʽ < �� > ::= < ���� > { < �˷������ > < ���� > }					*/
	/* ˵  �� �ú������ݲ���ʽ������Ӧ�ݹ鴦����,���ɱ��ʽ�����﷨���ڵ�		*/
	/****************************************************************************/
	TreeNode term()
	{
		TreeNode t = factor();		//�������Ӵ�����factor(),���������﷨���ڵ�ָ���t 
		while((token.Lex.equals("TIMES"))||(token.Lex.equals("OVER")))  	//��ǰ����tokenΪ�˷����������TIMES��OVER
		{
			TreeNode p = newExpNode("OpK");			//�����µ�OpK���ʽ�����﷨���ڵ�,�½ڵ�ָ�븳��p
			p.child[0] = t;						//���﷨���ڵ�p�����ɹ�,��ʼ����һ���ӽڵ��Աchild[0]Ϊt
			p.attr.expAttr.op = token.Lex;		//����ǰ����token��ֵ���﷨���ڵ�p���������Աattr.op
			t = p;
			match(token.Lex);					//��ǰ����token��ָ���˷����������(ΪTIMES��OVER)ƥ�� 
			p.child[1] = factor();				//�������Ӵ�����factor(),���������﷨���ڵ�ָ�븳��p�ڶ����ӽڵ��Աchild[1]
		}
		return t;
	}
	
	
	/****************************************************************************/
	/* ������ factor															*/
	/* ��  �� ���Ӵ�����														*/
	/* ����ʽ factor ::= ( exp ) | INTC | variable                  			*/
	/* ˵  �� �ú������ݲ���ʽ������Ӧ�ĵݹ鴦����,���ɱ��ʽ�����﷨���ڵ�	*/
	/****************************************************************************/
	TreeNode factor()
	{
		TreeNode t = null;
		if(token.Lex.equals("INTC"))
		{
			t = newExpNode("ConstK");			//�����µ�ConstK���ʽ�����﷨���ڵ�,��ֵ��t
			t.attr.expAttr.val = Integer.parseInt(token.Sem);  //���﷨���ڵ�t�����ɹ�,��ǰ����tokenΪ���ֵ���NUM	����ǰ������tokenStringת��Ϊ�����������﷨���ڵ�t����ֵ��Աattr.val
			match("INTC");				//��ǰ����token�����ֵ���NUMƥ��
		}
		else if(token.Lex.equals("ID"))
		{
			t = variable();				//�����µ�IdK���ʽ�����﷨���ڵ�t
		}
		else if(token.Lex.equals("LPAREN"))
		{
			match("LPAREN");		//��ǰ����token�������ŵ���LPARENƥ��
			t = exp();				//���ñ��ʽ������exp(),���������﷨���ڵ�ָ���t
			match("RPAREN");
		}
		else 					//��ǰ����tokenΪ��������
		{
			readNextToken();
		}
		return t;
	}
	
	
	
	/********************************************************************/
	/* ������ variable													*/
	/* ��  �� ����������												*/
	/* ����ʽ variable   ::=   id variMore                   			*/
	/* ˵  �� �ú������ݲ���ʽ,	����������������﷨���ڵ�              */
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
	/* ������ variMore													*/
	/* ��  �� ����������												*/
	/* ����ʽ variMore   ::=  ��                             			*/
	/*                       | [exp]            {[}                     */
	/*                       | . fieldvar       {DOT}                   */ 
	/* ˵  �� �ú������ݲ���ʽ������Ӧ�ĵݹ鴦������еļ��ֲ�ͬ����	*/
	/********************************************************************/		
	void variMore(TreeNode t)
	{
		if((token.Lex.equals("EQ"))||(token.Lex.equalsIgnoreCase("LT"))||(token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS"))||(token.Lex.equals("RPAREN"))||(token.Lex.equals("RMIDPAREN"))||(token.Lex.equals("SEMI"))||(token.Lex.equals("COMMA"))||(token.Lex.equals("THEN"))||(token.Lex.equals("ELSE"))||(token.Lex.equals("FI"))||(token.Lex.equals("DO"))||(token.Lex.equals("END"))||(token.Lex.equals("ENDWH"))||(token.Lex.equals("ASSIGN"))||(token.Lex.equals("TIMES"))||(token.Lex.equals("OVER")))
				{}
		else if(token.Lex.equals("LMIDPAREN"))
		{
			match("LMIDPAREN");
			t.child[0] = exp();				//�����Ժ��������ʽ��ֵ���������������±����
			t.attr.expAttr.varkind = "ArrayMembV";		//�˱��ʽΪ�����Ա��������
			match("RMIDPAREN");
		}
		else if(token.Lex.equals("DOT"))
		{
			match("DOT");
			t.child[0] = fieldVar();			//��һ������ָ�����Ա�������
			t.attr.expAttr.varkind = "FieldMembV";
		}
		else
		{
			readNextToken();
		}
		
	}
	
	
	/********************************************************************/
	/* ������ fieldVar													*/
	/* ��  �� ����������				���������								*/
	/* ����ʽ fieldvar   ::=  id  fieldvarMore                          */ 
	/* ˵  �� �ú������ݲ���ʽ����������������������﷨���ڵ�       	*/
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
	/* ������ fieldVarMore  											*/
	/* ��  �� ����������			���������									*/
	/* ����ʽ fieldvarMore   ::=  ��                             		*/
	/*                           | [exp]            {[}                 */ 
	/* ˵  �� �ú������ݲ���ʽ������Ӧ�ĵݹ鴦�������Ϊ�������͵����	*/
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
	
	
	/************************��ظ�������************************************/
	/********************************************************************/
	/* ������ match														*/
	/* ��  �� �ռ���ƥ�䴦����										*/
	/* ˵  �� ��������expected�����������ʷ����뵱ǰ���ʷ���token��ƥ��	*/
	/*        �����ƥ��,�򱨷����������﷨����							*/
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
	/* ������ syntaxError												*/
	/* ��  �� �﷨��������											*/
	/* ˵  �� ����������messageָ���Ĵ�����Ϣ��ʽ��д���б��ļ�listing	*/
	/*		  ���ô���׷�ٱ�־ErrorΪTRUE								*/
	/********************************************************************/
	void syntaxError(String s)
	{
		serror = serror+"\n>>> ERROR:" + "Syntax error at "+String.valueOf(token.Lineshow)+" line: "+s;
		Error = true;
	}
	
	/*****************************************************************/
	/* ������ readNextToken							        		 */	
	/* ��  �� ���ļ�tokenlist�е���Ϣ��Ϊ����ֵ                      */							    
	/*        һ�㣬listingָ���׼�����                            */
	/* ˵  �� ����ֵΪToken���ͣ������﷨������                  */
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
	 *********�����Ǵ����﷨�����õĸ���ڵ������***********
	 ********************************************************/

	/********************************************************/
	
	/* ������ newNode									*/	
	/* ��  �� �����﷨���ڵ㺯��			        		*/
	/* ˵  �� �ú���Ϊ�﷨������һ���µĽ��      		*/
	/********************************************************/
	TreeNode newNode(String s)
	{
		TreeNode t = new TreeNode();
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
		TreeNode t = new TreeNode();
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
	 /*д�ַ�������*/
	/********************************************************/
	void writeStr(String s)
	{
		stree = stree + s;
		
	}
	
	/********************************************************/
	 /*д�ո���*/
	/********************************************************/
	void writeSpace()
	{
		stree = stree + " ";
	}
	
	
	/********************************************************/
	 /*д���з���4���ո���*/
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
	/* ������ printTree                                   */
	/* ��  �� ���﷨���������ʾ���ļ���           */
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
			else if(tree.nodekind.equals("StmLK"))		//�����������
			{
				writeTab(1);
				writeStr("StmLK");
			}
			else if(tree.nodekind.equals("StmtK"))		//�������
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
				else if(tree.kind.equals("AssignK"))		//����ֵ
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
				else if(tree.kind.equals("VariK"))		//�������
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
					else if(tree.attr.expAttr.varkind.equals("FieldMembV"))		//���������
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

			for(int i=0;i<3;i++)			//���﷨�����tree�ĸ��ӽ��ݹ����printTree����
			{
				printTree(tree.child[i],l+1);
			}
			tree = tree.sibling;			//���﷨�����tree�ĸ��ֵܽ��ݹ����printTree����		
		}
	}
}
