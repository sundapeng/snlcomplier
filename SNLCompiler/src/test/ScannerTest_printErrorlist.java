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
		String a = "�����Token����Ϊ:"+"\n"+"��1��: ���ڴʷ�����.";
		assertEquals(st,a);
	}

}
