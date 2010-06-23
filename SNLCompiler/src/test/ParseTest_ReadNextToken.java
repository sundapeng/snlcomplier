package test;

import static org.junit.Assert.*;

import org.junit.Test;

import parse.Parse;
import scanner.Scanner;

public class ParseTest_ReadNextToken {

	@Test
	public void testReadNextToken() {
		String st = "program p";
		Scanner sc = new Scanner(st);
		st = sc.tokenlist;
		st = st.trim();
		Parse p = new Parse(st);
		String error = p.serror;
		String errorlist = ">>> ERROR :�������ڵ� 1 ��: �ռ�����ƥ��!"+"\n"+">>> ERROR :�������ڵ� 1 ��: �ռ�����ƥ��!"+"\n"+">>> ERROR :�������ڵ� 1 ��: �ռ�����ƥ��!\n";
		assertEquals(error,errorlist);
	}

}
