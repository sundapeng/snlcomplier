package scanner;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �Զ����ɷ������
		String s = "sarray=array [0..5] of integer;";
		Scanner scan = new Scanner(s);
		s = scan.tokenlist;
		s = s.trim();
		System.out.print(s);
	}

}
