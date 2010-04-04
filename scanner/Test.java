package scanner;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根
		String s = "sarray=array [0..5] of integer;";
		Scanner scan = new Scanner(s);
		s = scan.tokenlist;
		s = s.trim();
		System.out.print(s);
	}

}
