package scanner;

/**
 * 
 * ���嵥�ʵĽṹ�������ʷ���Ϣ��������Ϣ
 * Token.java
 * @author ������
 *    
 */
public class Token {
	private int Lineshow;	//��¼������Դ�����е�����
	private String Lex;   	//��¼�ʷ���Ϣ
	private String Sem;		//��¼������Ϣ
	private  int Column;       //�����еĵڼ�������
	
	
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