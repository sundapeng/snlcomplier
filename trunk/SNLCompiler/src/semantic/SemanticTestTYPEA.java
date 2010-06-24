package semantic;
import junit.framework.TestCase;

import scanner.Scanner;
import semantic.Semantic;



public class SemanticTestTYPEA extends TestCase {
	String chengxu;
	Scanner scan;
	Semantic sem;
	TreeNode t;

	protected void setUp() throws Exception {
		chengxu = "program sd var integer s; integer t; "
				+ "procedure factor(integer n;var integer m);"
				+ " var integer w; begin if n=0 then m:=1 else  "
				+ "factor(n-1,w); m:=n*w fi end begin read(s); "
				+ "factor(s,t); write(t) end. ";
		scan = new Scanner(chengxu);
		chengxu = scan.tokenlist;
		chengxu = chengxu.trim();
		sem = new Semantic(chengxu);
		t = sem.yuyiTree;
		
		
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTYPEA() {
		assertNotNull("",sem.TYPEA(t, "IntegerK"));
		
	}

}
