package parse;
import scanner.Scanner;
public class Test {

	/**
	 * 张玉明
	 * 测试语法分析程序
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根
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
