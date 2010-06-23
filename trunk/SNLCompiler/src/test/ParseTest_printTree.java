package test;

import static org.junit.Assert.*;
import parse.*;
import scanner.*;
import org.junit.Test;

public class ParseTest_printTree {

	@Test
	public void testPrintTree() {
	String st = "program p";
	Scanner sc = new Scanner(st);
	st = sc.tokenlist;
	st = st.trim();
	Parse p = new Parse(st);
	String error = p.serror;
	String errorlist = ">>> ERROR :错误发生在第 1 行: 终极符不匹配!"+"\n"+">>> ERROR :错误发生在第 1 行: 终极符不匹配!"+"\n"+">>> ERROR :错误发生在第 1 行: 终极符不匹配!\n";
	assertEquals(error,errorlist);
	}

}
