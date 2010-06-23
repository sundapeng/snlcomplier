package test;

import static org.junit.Assert.*;

import org.junit.Test;

import parse.Parse;
import scanner.Scanner;

public class ParseTest_match {

	@Test
	public void testMatch() {
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
