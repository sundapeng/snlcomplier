package test;
/**
 *  测试判断是否为整数方法
 * @author 张玉明
 * 
 */
import static org.junit.Assert.*;
import scanner.Scanner;
import org.junit.Test;

public class ScannerTest_isdigit {

	@Test
	public void testIsdigit() {
		String t = "12";
		Scanner sc = new Scanner(t);
		t = sc.tokenlist;
		t = t.trim();
		String sr = "1:INTC,12"+"\n"+"1:ENDFILE,";
		assertEquals(t,sr);
	}

}
