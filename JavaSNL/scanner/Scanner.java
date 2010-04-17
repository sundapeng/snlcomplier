package scanner;

/**
 *  词法分析类
 * Scanner.java
 * @author 张玉明
 * 
 */
public class Scanner 
{
	int MAXTOKENLEN = 50;		// MAXTOKENLEN为单词最大长度定义为50 
	
	
	int total = 0;			    //源代码字符总数
	int char_num = 0;			//在代码缓冲区中的当前字符位置,初始为0
	int token_num = 0;			//输出时记录token个数的变量
	int lineno = 1;				//源代码行号
	boolean is = true;			//读入的字符是否为小写字母，TRUE为小写，false为大写
	boolean Error = false;		//错误追踪标志 
	boolean EOF_flag = false;	//EOF_flag当为文件尾时,改变函数ungetNextChar功能
	
	public String tokenlist = null;
	
	/*构造函数*/
	public Scanner(String s)
	{
		tokenlist=printTokenlist(getTokenlist(s));
	}
	
	
	
	

	
	/* 功  能 取得下一非空字符函数									   */	
	/* 说  明 该函数从输入缓冲区中取得下一个非空字符		       */
	/*       如果缓冲区中的字串已经读完,则从源代码文件中读入一新行   */
char getNextChar(char t[])
{
	char a = ' ';
	if (char_num < total)
	{
		if (t[char_num]=='\n')
			lineno++;		// 源代码行号lineno加1
		a = t[char_num];
		char_num++;
	}
	else
		EOF_flag = true;		//已经到源代码文件末尾,设置EOF_flag标志为TRUE
	return a;
		
}

/*
 * 功  能 字符回退函数
 * 说  明 该过程在行输入缓冲区回退一个字符		
            用于超前读字符后不匹配时候的回退
 */
void ungetNextChar()
{
	/* 如果EOF_flag标志为FALSE,不是处于源文件末尾 ,在缓冲区中当前位置减1 */
	if(!EOF_flag)
		char_num--;
}



/*
 * 功  能 判断是否是数字函数
 */
boolean isdigit(char c)
{
	if((c == '0')||(c == '1')||(c == '2')||(c == '3')||(c=='4')||(c=='5')||(c=='6')||(c=='7')||(c=='8')||(c=='9'))
	{
		return true;
	}
	else
		return false;
	
}



/*
 * 功  能 判断是否是字母函数
 */
boolean isalpha(char c)
{
	if((c == 'a')||(c == 'b')||(c == 'c')||(c == 'd')||(c == 'e')||(c == 'f')||(c == 'g')||(c == 'h')||(c == 'i')||(c == 'j')||(c == 'k')||(c == 'l')||(c == 'm')||(c == 'n')||(c == 'o')||(c == 'p')||(c == 'q')||(c == 'r')||(c == 's')||(c == 't')||(c == 'u')||(c == 'v')||(c == 'w')||(c == 'x')||(c == 'y')||(c == 'z'))
	{
		return true;
	}
	else if((c == 'A')||(c == 'B')||(c == 'C')||(c == 'D')||(c == 'E')||(c == 'F')||(c == 'G')||(c == 'H')||(c == 'I')||(c == 'J')||(c == 'K')||(c == 'L')||(c == 'M')||(c == 'N')||(c == 'O')||(c == 'P')||(c == 'Q')||(c == 'R')||(c == 'S')||(c == 'T')||(c == 'U')||(c == 'V')||(c == 'W')||(c == 'X')||(c == 'Y')||(c == 'Z'))
	{
		is = false;
		return true;
	}
	else
		return false;
}



/**************************************************************/							     
/* 功  能 保留字查找函数									  */
/* 说  明 使用线性查找,查看一个标识符是否是保留字			  */
/*		  标识符如果是保留字则返回相应单词,否则返回单词ID */
/**************************************************************/
String reservedLookup(String s)
{
	/* 字符串s与保留字表中某一表项匹配,函数返回对应保留字单词 */
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
		return "IETEGER";
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
		return "ID";		// 字符串s未在保留字表中找到,函数返回标识符单词ID 
}


/********************************************************/
/* 功  能 将Token插入Token链								*/
/********************************************************/
void copyString(ChainNode a,Token b)
{
	a.token.Lineshow = b.Lineshow;
	a.token.Lex = b.Lex;
	a.token.Sem = b.Sem;
}




/*****************************************************************/
/* 功  能 输出Token链                      */							    
/* 说  明 返回词法分析结果                                   */
/*****************************************************************/
String printTokenlist(ChainNode n)
{
	String a = " ";
	ChainNode node = n;
	Token token = n.token;
	for(int i=1;i<=token_num;i++)
	{
		a = a+String.valueOf(token.Lineshow)+":"+token.Lex+",";
		if(token.Sem == null)
		{
			a = a+" ";
		}
		else
		{
			a = a+token.Sem;     //输出Sem
		}
		a = a + "\n";
		node = node.nexttoken;
		token = node.token;
	}
	return a;
}








/*                    词法扫描器基本函数 					 */ 			
/* 功  能 取得单词函数										*/
/* 说  明 函数从源文件字符串序列中获取所有Token序列 		*/
/*        使用确定性有限自动机DFA,采用直接转向法    		*/
/*        超前读字符,对保留字采用查表方式识别    			*/
/*        产生词法错误时候,仅仅略过产生错误的字符,不加改正  */
ChainNode getTokenlist(String s)
{
	ChainNode chainHead = new ChainNode();		//链表的表头
	ChainNode preNode = chainHead;				//指向当前结点的前驱结点
	Token currentToken = new Token();			//存放当前的Token
	
	s = s+" ";
	total = s.length();
	char t[] = s.toCharArray();
	
	do{
		char tokenString[] = new char[MAXTOKENLEN+1];  
		int tokenStringIndex = 0;			// tokenStringIndex用于记录当前正在识别单词的词元存储区tokenString中的当前正在识别字符位置,初始为0 
	
		/*****************词法分析器确定性有限自动机DFA的状态类型*************/				             
		/* START 开始状态; INASSIGN 赋值状态; INRANGE 下标范围状态;          */
		/* INNUM 数字状态; INID 标识符状态; DONE 完成状态;                   */
		/* INCHAR 字符状态;INCOMMENT 注释状态;                               */
	String state = "START";				//当前状态标志state,始终都是以START作为开始 
	boolean save;						//决定当前识别字符是否存入当前识别单词词元存储区tokenString
	is = true;
	
	while(!(state.equals("DONE")))
	{
		char c = getNextChar(t);
		save = true;
		if(EOF_flag)
		{
			state = "DONE";					//当前字符c为EOF,当前DFA状态state设置为完成状态DONE,当前单词识别结束
			save = false;
			currentToken.Lex = "ENDFILE";			//当前识别单词返回值currentToken设置为文件结束单词ENDFILE
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
				/* 当前字符c为"=",当前识别单词返回值currentToken设置为等号单词EQ */ 
					   case '=':
					   currentToken.Lex = "EQ";
	                   break;

				      /* 当前字符c为"<",当前识别单词返回值currentToken设置为小于单词LT */
	                   case '<':
	                   currentToken.Lex = "LT";
	                   break;

				      /* 当前字符c为"+",当前识别单词返回值currentToken设置为加号单词PLUS */
	                   case '+':
	                   currentToken.Lex = "PLUS";
	                   break;

				      /* 当前字符c为"-",当前识别单词返回值currentToken设置为减号单词MINUS */
	                   case '-':
	                   currentToken.Lex = "MINUS";
	                   break;

			       	 /* 当前字符c为"*",当前识别单词返回值currentToken设置为乘号单词TIMES */
	                   case '*':
	                   currentToken.Lex = "TIMES";
	                   break;
	   
			     	 /* 当前字符c为"/",当前识别单词返回值currentToken设置为除号单词OVER */
	                   case '/':
	                   currentToken.Lex = "OVER";
	                   break;
	  
				     /* 当前字符c为"(",当前识别单词返回值currentToken设置为左括号单词LPAREN */
	                   case '(':
	                   currentToken.Lex = "LPAREN";
	                   break;

				     /* 当前字符c为")",当前识别单词返回值currentToken设置为右括号单词RPAREN */
	                   case ')':
	                   currentToken.Lex = "RPAREN";
	                   break;

				    /* 当前字符c为";",当前识别单词返回值currentToken设置为分号单词SEMI */
	                   case ';':
	                   currentToken.Lex = "SEMI";
	                   break;
	   	    	    /* 当前字符c为",",当前识别单词返回值currentToken设置为逗号单词COMMA */
	 			       case ',':
				       currentToken.Lex = "COMMA";
	                   break;     		 
			        /* 当前字符c为"[",当前识别单词返回值currentToken设置为左中括号单词LMIDPAREN */
			           case '[':
				       currentToken.Lex = "LMIDPAREN";
				       break;
	    		 
				    /* 当前字符c为"]",当前识别单词返回值currentToken设置为右中括号单词RMIDPAREN */
				       case ']':
				       currentToken.Lex = "RMIDPAREN";
				       break;
	    
				    /* 当前字符c为其它字符,当前识别单词返回值currentToken设置为错误单词ERROR */
	                   default:
	                   currentToken.Lex = "ERROR";
					   Error = true;
	                   break;
				}
			}
		}
		else if(state.equals("INCOMMENT"))			//当前DFA状态state为注释状态INCOMMENT,确定性有限自动机DFA处于注释位置 
		{
			save = false;							//当前字符存储状态save设置为FALSE,注释中内容不生成单词,无需存储
			if(c == '}')
			{
				state = "START";
			}
		}
		else if(state.equals("INASSIGN"))		//当前DFA状态state为赋值状态INASSIGN,确定性有限自动机DFA处于赋值单词位置
		{
			save = false;
			state = "DONE";							//当前DFA状态state设置为完成状态DONE,赋值单词结束
			if(c == '=')
			{
				currentToken.Lex = "ASSIGN";
			}
			else								//当前字符c为其它字符,即":"后不是"=",在输入行缓冲区中回退一个字符
			{
					ungetNextChar();
					currentToken.Lex = "ERROR";
					Error = true;
			}
		}
		else if(state.equals("INRANGE"))		// 当前DFA状态state设置为完成状态DONE,赋值单词结束 
		{
				state = "DONE";
				save = false;
				if(c== '.')						//当前字符c为".",当前识别单词返回值currentToken设置为下标界UNDERANGE
				{
					currentToken.Lex = "UNDERANGE";
				}
				else
				{
					ungetNextChar();
					currentToken.Lex = "DOT";
				}
		}
		else if(state.equals("INNUM"))   //当前DFA状态state为数字状态INNUM,确定性有限自动机处于数字单词位置
		{
				if(!isdigit(c))
				{
					ungetNextChar();		//当前字符c不是数字,则在输入行缓冲区源中回退一个字符	
					save = false;			//字符存储标志设置为FALSE,当前DFA状态state设置为DONE,数字单词识别完成
					state = "DONE";
					currentToken.Lex = "INTC";    //当前识别单词返回值currentToken设置为数字单词NUM
				}
		}
		else if(state.equals("INCHAR"))			//当前DFA状态state为字符标志状态INCHAR,确定有限自动机处于字符标志状态
		{
			if(isalpha(c)||isdigit(c))
			{
				char c1 = getNextChar(t);
				if(c1 == '\'')
				{
					save = true;
					state = "DONE";
					currentToken.Lex = "ID";
				}
				else
				{
					ungetNextChar();
					ungetNextChar();
					state = "DONE";
					currentToken.Lex = "ERROR";
					Error = true;
				}
			}
			else
			{
				ungetNextChar();
				state = "DONE";
				currentToken.Lex = "ERROR";
				Error = true;
			}	
		}
		else if(state.equals("INID"))			//当前DFA状态state为标识符状态INID,确定性有限自动机DFA处于标识符单词位置
		{
			if((!isdigit(c))&&(!isalpha(c)))
			{
				ungetNextChar();
				save = false;					//字符存储标志设置为FALSE,当前DFA状态state设置为DONE,标识符单词识别完成
				state = "DONE";
				currentToken.Lex = "ID";
			}
		}
		else if(state.equals("DONE"))		//当前DFA状态state为完成状态DONE,确定性有限自动机DFA处于单词结束位置
		{		
		}
		else
		{
			Error = true;
			state = "DONE";
			currentToken.Lex = "ERROR";
		}
	/*************** 分类判断处理结束 *******************/
		
		
		if((save)&&(tokenStringIndex<=MAXTOKENLEN))			//当前字符存储状态save为TRUE,且当前正识别单词已经识别部分未超过单词最大长度
		{
			tokenString[tokenStringIndex] = c;				//将当前字符c写入当前正识别单词词元存储区tokenString
			tokenStringIndex = tokenStringIndex+1;
		}
		if(state.equals("DONE"))				//当前DFA状态state为完成状态DONE,单词识别完成
		{
			String st = (new String(tokenString)).trim();		//当前识别单词词元存储区tokenString加上结束标志
			if(currentToken.Lex.equals("ID"))			//当前单词currentToken为标识符单词类型,查看其是否为保留字单词
			{
				if(is)								//若此Token中有大写字母，则无需查找保留字表，直接将其作为非保留字
				{
					currentToken.Lex = reservedLookup(st);
				}
				if(currentToken.Lex.equals("ID"))
				{
					currentToken.Sem = st;
				}
				else
				{
					currentToken.Sem = " ";
				}
			}
			else if(currentToken.Lex.equals("INTC"))		//此时Token为数字单词
			{
				currentToken.Sem = st;
			}
			else
			{
				currentToken.Sem = " ";
			}
		}
	}
	/**************** 循环处理结束 ********************/
	
	currentToken.Lineshow = lineno;				//将行号信息存入Token
	token_num++;								//Token总数目加1
	copyString(preNode,currentToken);			//将已处理完的当前Token存入链表的Token部分
	preNode.nexttoken = new ChainNode();		//申请一个新的结点，以记录下一个Token的信息
	preNode = preNode.nexttoken;				//初始化这个结点中指向下一个结点的指针
	}
	while(!(currentToken.Lex.equals("ENDFILE")));
	return chainHead;
}


}
