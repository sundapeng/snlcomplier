package test;

import static org.junit.Assert.*;
import parse.*;
import scanner.*;

import org.junit.Test;

public class ParseTest_writeStr {

	@Test
	public void testWriteStr() {
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
