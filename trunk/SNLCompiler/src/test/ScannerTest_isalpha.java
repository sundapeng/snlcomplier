package test;

import static org.junit.Assert.*;
import scanner.Scanner;
import org.junit.Test;

public class ScannerTest_isalpha {

	@Test
	public void testIsalpha() {
		String t = "a";
		Scanner sc = new Scanner(t);
		t = sc.tokenlist;
		t = t.trim();
		String sr = "1:ID,a"+"\n"+"1:ENDFILE,";
		assertEquals(t,sr);
	}

}
