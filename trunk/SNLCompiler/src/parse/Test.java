package parse;
import scanner.Scanner;
public class Test {

	/**
	 * ������
	 * �����﷨��������
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �Զ����ɷ������
		String s = "sarray=array [0..5] of integer;";
		Scanner scan = new Scanner(s);
		s = scan.tokenlist;
		s = s.trim();
		Parse p = new Parse(s);
		System.out.println(s);
		if(p.Error)
		{
			System.out.print(p.serror);
		}
		else
		{
			System.out.print(p.stree);
		}
		
		
	}

}
