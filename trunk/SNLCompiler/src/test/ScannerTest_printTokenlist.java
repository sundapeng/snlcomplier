package test;

import static org.junit.Assert.*;
import scanner.Scanner;
import org.junit.Test;

public class ScannerTest_printTokenlist {

	@Test
	public void testPrintTokenlist() {
		String a = "begin";
		Scanner sc = new Scanner(a);
		a = sc.tokenlist;
		a = a.trim();
		String sr = "1:BEGIN, "+"\n"+"1:ENDFILE,";
		assertEquals(a,sr);
	}

}
