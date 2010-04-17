package scanner;

import static org.junit.Assert.*;

import org.junit.Test;

public class ScannerTest {

	@Test
	public void testReservedLookup() {
		java.lang.String s = "begin";
		Scanner sc = new Scanner(s);
		s = sc.tokenlist;
		s = s.trim();
		java.lang.String r = "1:BEGIN, "+"\n"+"1:ENDFILE,";
		assertEquals(r,s);
	}

}
