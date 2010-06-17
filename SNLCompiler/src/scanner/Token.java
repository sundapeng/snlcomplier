package scanner;

/**
 * 
 * 定义单词的结构，包括词法信息和语义信息
 * Token.java
 * @author 张玉明
 *    
 */
public class Token {
	private int Lineshow;	//记录单词在源程序中的行数
	private String Lex;   	//记录词法信息
	private String Sem;		//记录语义信息
	private  int Column;       //所在行的第几个单词
	
	
	public int getColumn() {
		return Column;
	}
	public void setColumn(int column) {
		Column = column;
	}
	public String getLex() {
		return Lex;
	}
	public void setLex(String lex) {
		Lex = lex;
	}
	public int getLineshow() {
		return Lineshow;
	}
	public void setLineshow(int lineshow) {
		Lineshow = lineshow;
	}
	public String getSem() {
		return Sem;
	}
	public void setSem(String sem) {
		Sem = sem;
	}

}