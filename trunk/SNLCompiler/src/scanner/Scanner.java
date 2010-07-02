package scanner;

import java.util.LinkedList;

/**
 *  �ʷ�������
 * Scanner.java
 * @author ������
 * 
 */
public class Scanner 
{
	int MAXTOKENLEN = 50;		// MAXTOKENLENΪ������󳤶ȶ���Ϊ50 
	
	
	int total = 0;			    //Դ�����ַ�����
	int char_num = 0;			//�ڴ��뻺�����еĵ�ǰ�ַ�λ��,��ʼΪ0
	int token_num = 0;			//���ʱ��¼token�����ı���
	int columnNumber = 0;       // ��ǰ�ַ�������
	int lineno = 1;				//Դ�����к�
	boolean is = true;			//������ַ��Ƿ�ΪСд��ĸ��TRUEΪСд��falseΪ��д
	public boolean Error = false;		//����׷�ٱ�־ 
	boolean EOF_flag = false;	//EOF_flag��Ϊ�ļ�βʱ,�ı亯��ungetNextChar����
	
	
	public LinkedList<Token> errorTokenList = new LinkedList<Token>();   //����������java���е�LinkedList��ʵ��
	public String tokenlist = null;   //����token����
	public String tokenlist17 = "";  //��������token����
	
	
	/*���캯��*/
	public Scanner(String s)
	{
		tokenlist=printTokenlist(getTokenlist(s));
	}
	
	
	/* ��  �� ȡ����һ�ǿ��ַ�����									   */	
	/* ˵  �� �ú��������뻺������ȡ����һ���ǿ��ַ�		       */
	/*       ����������е��ִ��Ѿ�����,���Դ�����ļ��ж���һ����   */
char getNextChar(char t[])
{
	char a = ' ';
	if (char_num < total)
	{
		if (t[char_num]=='\n')
		{
			lineno++;		// Դ�����к�lineno��1
			columnNumber = 0;
		}

		a = t[char_num];
		char_num++;

	}
	else
		EOF_flag = true;		//�Ѿ���Դ�����ļ�ĩβ,����EOF_flag��־ΪTRUE
	return a;
		
}

/*
 * ��  �� �ַ����˺���
 * ˵  �� �ù����������뻺��������һ���ַ�		
            ���ڳ�ǰ���ַ���ƥ��ʱ��Ļ���
 */
void ungetNextChar()
{
	/* ���EOF_flag��־ΪFALSE,���Ǵ���Դ�ļ�ĩβ ,�ڻ������е�ǰλ�ü�1 */
	if(!EOF_flag)
	{
		char_num--;
	}
}



/*
 * ��  �� �ж��Ƿ������ֺ���
 */
boolean isdigit(char c)
{
	if('0'<= c && c <= '9')
	{
		return true;
	}
	else
		return false;
	
}



/*
 * ��  �� �ж��Ƿ�����ĸ����
 */
boolean isalpha(char c)
{
	if('a'<= c && c <= 'z')
	{
		return true;
	}
	else if('A'<= c && c <= 'Z')
	{
		is = false;
		return true;
	}
	else
		return false;
}



/**************************************************************/							     
/* ��  �� �����ֲ��Һ���									  */
/* ˵  �� ʹ�����Բ���,�鿴һ����ʶ���Ƿ��Ǳ�����			  */
/*		  ��ʶ������Ǳ������򷵻���Ӧ����,���򷵻ص���ID */
/**************************************************************/
String reservedLookup(String s)
{
	/* �ַ���s�뱣���ֱ���ĳһ����ƥ��,�������ض�Ӧ�����ֵ��� */
	if(s.equals("program"))
		return "PROGRAM";
	else if(s.equals("procedure"))
		return "PROCEDURE";
	else if(s.equals("type"))
		return "TYPE";
	else if(s.equals("var"))
		return "VAR";
	else if(s.equals("begin"))
		return "BEGIN";
	else if(s.equals("end"))
		return "END";
	else if(s.equals("array"))
		return "ARRAY";
	else if(s.equals("of"))
		return "OF";
	else if(s.equals("record"))
		return "RECORD";
	else if(s.equals("if"))
		return "IF";
	else if(s.equals("then"))
		return "THEN";
	else if(s.equals("else"))
		return "ELSE";
	else if(s.equals("read"))
		return "READ";
	else if(s.equals("write"))
		return "WRITE";
	else if(s.equals("return"))
		return "RETURN";
	else if(s.equals("integer"))
		return "INTEGER";
	else if(s.equals("fi"))
		return "FI";
	else if(s.equals("while"))
		return "WHILE";
	else if(s.equals("do"))
		return "DO";
	else if(s.equals("endwh"))
		return "ENDWH";
	else if(s.equals("char"))
		return "CHAR";
	else
		return "ID";		// �ַ���sδ�ڱ����ֱ����ҵ�,�������ر�ʶ������ID 
}


/********************************************************/
/* ��  �� ��Token����Token��								*/
/********************************************************/
void copyString(ChainNode a,Token b)
{
	a.token.setLineshow(b.getLineshow());
	a.token.setLex(b.getLex());
	a.token.setSem(b.getSem());
}




/*****************************************************************/
/* ��  �� ���Token��                      */							    
/* ˵  �� ���شʷ��������                                   */
/*****************************************************************/
String printTokenlist(ChainNode n)
{
	String a = " ";
	ChainNode node = n;
	Token token = n.token;
	for(int i=1;i<=token_num;i++)
	{
		a = a+String.valueOf(token.getLineshow())+":"+token.getLex()+",";
		if(token.getSem() == null)
		{
			a = a+" ";
		}
		else
		{
			a = a+token.getSem();     //���Sem
		}
		a = a + "\n";
		node = node.nexttoken;
		token = node.token;
	}

	return a;
}

/*//���Ӵ����token����
public void addErrorList(Token new_token)
{
	errorTokenList.add(new_token);
}*/

//�������token����
public String printErrorList()
{
	String tokenlist18 = "�����Token����Ϊ:"+"\n"+tokenlist17;
	return tokenlist18;
}







/*                    �ʷ�ɨ������������ 					 */ 			
/* ��  �� ȡ�õ��ʺ���										*/
/* ˵  �� ������Դ�ļ��ַ��������л�ȡ����Token���� 		*/
/*        ʹ��ȷ���������Զ���DFA,����ֱ��ת��    		*/
/*        ��ǰ���ַ�,�Ա����ֲ��ò����ʽʶ��    			*/
/*        �����ʷ�����ʱ��,�����Թ�����������ַ�,���Ӹ���  */
ChainNode getTokenlist(String s)
{
	ChainNode chainHead = new ChainNode();		//�����ı�ͷ
	ChainNode preNode = chainHead;				//ָ��ǰ����ǰ�����
	Token currentToken = new Token();			//��ŵ�ǰ��Token
	
	s = s+" ";
	total = s.length();
	char t[] = s.toCharArray();
	
	do{
		char tokenString[] = new char[MAXTOKENLEN+1];  
		int tokenStringIndex = 0;			// tokenStringIndex���ڼ�¼��ǰ����ʶ�𵥴ʵĴ�Ԫ�洢��tokenString�еĵ�ǰ����ʶ���ַ�λ��,��ʼΪ0 
	
		/*****************�ʷ�������ȷ���������Զ���DFA��״̬����*************/				             
		/* START ��ʼ״̬; INASSIGN ��ֵ״̬; INRANGE �±귶Χ״̬;          */
		/* INNUM ����״̬; INID ��ʶ��״̬; DONE ���״̬;                   */
		/* INCHAR �ַ�״̬;INCOMMENT ע��״̬;                               */
	String state = "START";				//��ǰ״̬��־state,ʼ�ն�����START��Ϊ��ʼ 
	boolean save;						//������ǰʶ���ַ��Ƿ���뵱ǰʶ�𵥴ʴ�Ԫ�洢��tokenString
	is = true;
	
	while(!(state.equals("DONE")))
	{
		char c = getNextChar(t);
		save = true;
		if(EOF_flag)
		{
			state = "DONE";					//��ǰ�ַ�cΪEOF,��ǰDFA״̬state����Ϊ���״̬DONE,��ǰ����ʶ�����
			save = false;
			currentToken.setLex("ENDFILE");			//��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�ļ���������ENDFILE
		}
		else if(state.equals("START"))
		{
			if(isdigit(c))
				state = "INNUM";
			else if(isalpha(c))
				state = "INID";
			else if (c== ':')
			{
				state = "INASSIGN";
				save = false;
			}
			else if(c == '.')
			{
				state = "INRANGE";
				save = false;
				
			}
			else if(c == '\'')
			{
				state = "INCHAR";
				save = false;
			}
			else if((c == ' ') || (c == '\t') || (c == '\n') ||(c == '\r'))
			{
				save = false;
			}
			else if(c == '{')
			{
				state = "INCOMMENT";
				save = false;
			}
			else
			{
				state = "DONE";
				save = false;
				switch(c)
				{
				/* ��ǰ�ַ�cΪ"=",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�Ⱥŵ���EQ */ 
					   case '=':
					   currentToken.setLex("EQ");
	                   break;

				      /* ��ǰ�ַ�cΪ"<",��ǰʶ�𵥴ʷ���ֵcurrentToken����ΪС�ڵ���LT */
	                   case '<':
	                   currentToken.setLex("LT");
	                   break;

				      /* ��ǰ�ַ�cΪ"+",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�Ӻŵ���PLUS */
	                   case '+':
	                   currentToken.setLex("PLUS");
	                   break;

				      /* ��ǰ�ַ�cΪ"-",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ŵ���MINUS */
	                   case '-':
	                   currentToken.setLex("MINUS");
	                   break;

			       	 /* ��ǰ�ַ�cΪ"*",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�˺ŵ���TIMES */
	                   case '*':
	                   currentToken.setLex("TIMES");
	                   break;
	   
			     	 /* ��ǰ�ַ�cΪ"/",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ŵ���OVER */
	                   case '/':
	                   currentToken.setLex("OVER");
	                   break;
	  
				     /* ��ǰ�ַ�cΪ"(",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�����ŵ���LPAREN */
	                   case '(':
	                   currentToken.setLex("LPAREN");
	                   break;

				     /* ��ǰ�ַ�cΪ")",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�����ŵ���RPAREN */
	                   case ')':
	                   currentToken.setLex("RPAREN");
	                   break;

				    /* ��ǰ�ַ�cΪ";",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�ֺŵ���SEMI */
	                   case ';':
	                   currentToken.setLex("SEMI");
	                   break;
	   	    	    /* ��ǰ�ַ�cΪ",",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ŵ���COMMA */
	 			       case ',':
				       currentToken.setLex("COMMA");
	                   break;     		 
			        /* ��ǰ�ַ�cΪ"[",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�������ŵ���LMIDPAREN */
			           case '[':
				       currentToken.setLex("LMIDPAREN");
				       break;
	    		 
				    /* ��ǰ�ַ�cΪ"]",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�������ŵ���RMIDPAREN */
				       case ']':
				       currentToken.setLex("RMIDPAREN");
				       break;
	    
				    /* ��ǰ�ַ�cΪ�����ַ�,��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���󵥴�ERROR */
	                   default:
	                   currentToken.setLex("ERROR");
					   Error = true;
	                   break;
				}
			}
		}
		else if(state.equals("INCOMMENT"))			//��ǰDFA״̬stateΪע��״̬INCOMMENT,ȷ���������Զ���DFA����ע��λ�� 
		{
			save = false;							//��ǰ�ַ��洢״̬save����ΪFALSE,ע�������ݲ����ɵ���,����洢
			if(c == '}')
			{
				state = "START";
			}
		}
		else if(state.equals("INASSIGN"))		//��ǰDFA״̬stateΪ��ֵ״̬INASSIGN,ȷ���������Զ���DFA���ڸ�ֵ����λ��
		{
			save = false;
			state = "DONE";							//��ǰDFA״̬state����Ϊ���״̬DONE,��ֵ���ʽ���
			if(c == '=')
			{
				currentToken.setLex("ASSIGN");
			}
			else								//��ǰ�ַ�cΪ�����ַ�,��":"����"=",�������л������л���һ���ַ�
			{
					ungetNextChar();
					currentToken.setLex("ERROR");
					Error = true;
			}
		}
		else if(state.equals("INRANGE"))		// ��ǰDFA״̬state����Ϊ���״̬DONE,��ֵ���ʽ��� 
		{
				state = "DONE";
				save = false;
				if(c== '.')						//��ǰ�ַ�cΪ".",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�±��UNDERANGE
				{
					currentToken.setLex("UNDERANGE");
				}
				else
				{
					ungetNextChar();
					currentToken.setLex("DOT");
				}
		}
		else if(state.equals("INNUM"))   //��ǰDFA״̬stateΪ����״̬INNUM,ȷ���������Զ����������ֵ���λ��
		{
				if(!isdigit(c))
				{
					ungetNextChar();		//��ǰ�ַ�c��������,���������л�����Դ�л���һ���ַ�	
					save = false;			//�ַ��洢��־����ΪFALSE,��ǰDFA״̬state����ΪDONE,���ֵ���ʶ�����
					state = "DONE";
					currentToken.setLex("INTC");    //��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ֵ���NUM
				}
		}
		else if(state.equals("INCHAR"))			//��ǰDFA״̬stateΪ�ַ���־״̬INCHAR,ȷ�������Զ��������ַ���־״̬
		{
			if(isalpha(c)||isdigit(c))
			{
				char c1 = getNextChar(t);
				if(c1 == '\'')
				{
					save = true;
					state = "DONE";
					currentToken.setLex("ID");
				}
				else
				{
					ungetNextChar();
					ungetNextChar();
					state = "DONE";
					currentToken.setLex("ERROR");
					Error = true;
				}
			}
			else
			{
				ungetNextChar();
				state = "DONE";
				currentToken.setLex("ERROR");
				Error = true;
			}	
		}
		else if(state.equals("INID"))			//��ǰDFA״̬stateΪ��ʶ��״̬INID,ȷ���������Զ���DFA���ڱ�ʶ������λ��
		{
			if((!isdigit(c))&&(!isalpha(c)))
			{
				ungetNextChar();
				save = false;					//�ַ��洢��־����ΪFALSE,��ǰDFA״̬state����ΪDONE,��ʶ������ʶ�����
				state = "DONE";
				currentToken.setLex("ID");
			}
		}
		else if(state.equals("DONE"))		//��ǰDFA״̬stateΪ���״̬DONE,ȷ���������Զ���DFA���ڵ��ʽ���λ��
		{		
		}
		else
		{
			Error = true;
			state = "DONE";
			currentToken.setLex("ERROR");
		}
	/*************** �����жϴ������� *******************/
		
		
		if((save)&&(tokenStringIndex<=MAXTOKENLEN))			//��ǰ�ַ��洢״̬saveΪTRUE,�ҵ�ǰ��ʶ�𵥴��Ѿ�ʶ�𲿷�δ����������󳤶�
		{
			tokenString[tokenStringIndex] = c;				//����ǰ�ַ�cд�뵱ǰ��ʶ�𵥴ʴ�Ԫ�洢��tokenString
			tokenStringIndex = tokenStringIndex+1;
		}
		if(state.equals("DONE"))				//��ǰDFA״̬stateΪ���״̬DONE,����ʶ�����
		{
			String st = (new String(tokenString)).trim();		//��ǰʶ�𵥴ʴ�Ԫ�洢��tokenString���Ͻ�����־
			if(currentToken.getLex().equals("ID"))			//��ǰ����currentTokenΪ��ʶ����������,�鿴���Ƿ�Ϊ�����ֵ���
			{
				if(is)								//����Token���д�д��ĸ����������ұ����ֱ���ֱ�ӽ�����Ϊ�Ǳ�����
				{
					currentToken.setLex(reservedLookup(st));
				}
				if(currentToken.getLex().equals("ID"))
				{
					currentToken.setSem(st);
				}
				else
				{
					currentToken.setSem(" ");
				}
			}
			else if(currentToken.getLex().equals("INTC"))		//��ʱTokenΪ���ֵ���
			{
				currentToken.setSem(st);
			}
			else
			{
				currentToken.setSem(" ");
			}
		}
	}
	/**************** ѭ���������� ********************/
	
	currentToken.setLineshow(lineno);				//���к���Ϣ����Token
	columnNumber++;
	currentToken.setColumn(columnNumber);			//���к���Ϣ����Token
	token_num++;								    //Token����Ŀ��1
	if(currentToken.getLex().equals("ERROR"))
	{
		
		tokenlist17 = tokenlist17 + "��"+currentToken.getLineshow()+ "��: ���ڴʷ�����."+"\n";
		
	}
	copyString(preNode,currentToken);			//���Ѵ�����ĵ�ǰToken����������Token����
	preNode.nexttoken = new ChainNode();		//����һ���µĽ�㣬�Լ�¼��һ��Token����Ϣ
	preNode = preNode.nexttoken;				//��ʼ����������ָ����һ������ָ��
	}
	while(!(currentToken.getLex().equals("ENDFILE")));
	return chainHead;
}


}