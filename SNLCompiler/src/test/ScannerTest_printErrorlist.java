package test;

import static org.junit.Assert.*;
import scanner.Scanner;
import org.junit.Test;

public class ScannerTest_printErrorlist {

	@Test
	public void testPrintErrorList() {
		String st = "V1:V1+10;";
		Scanner sc = new Scanner(st);
		st = sc.printErrorList();
		st = st.trim();
		String a = "错误的Token序列为:"+"\n"+"第1行: 存在词法错误.";
		assertEquals(st,a);
	}

}
