package semantic;
import scanner.*;
import junit.framework.TestCase;

public class SemanticTestCompat extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCompat() {
		String s="program bubble {殻會遊 殻會兆炎紛憲} " +
				"　　var integer i,j,num; 　" +
				"　array[1..20] of integer a; 　" +
				"　procedure q(integer num); 　" +
				"　var integer i,j,k; 　　" +
				"integer t; 　" +
				"　begin 　" +
				"　i:=1; 　" +
				"　j:=1; 　　" +
				"while i < num do " +
				"　　j:=num-i+1;" +
				" 　　k:=1; 　" +
				"　while k<j do " +
				"　　if a[k+1] < a[k] " +
				"　　then t:=a[k]; " +
				"　　a[k]:=a[k+1]; " +
				"　　a[k+1]:=t 　" +
				"　else t:=0 　" +
				"　fi; 　" +
				"　k:=k+1 　　endwh; 　" +
				"　i:=i+1 　　endwh " +
				"　　end 　　" +
				"begin 　　" +
				"read(num); 　" +
				"　i:=1; 　" +
				"　while i<(num+1)" +
				" do　read(j); " +
				"　　a:=j; 　" +
				"　i:=i+1 　　endwh;" +
				" 　　q(num); 　　i:=1; " +
				"　　while i<(num+1)" +
				" do 　　" +
				"write(a); " +
				"　　i:=i+1 　" +
				"　endwh 　" +
				"　end.";
	
		
		Scanner scan = new Scanner(s);
		s = scan.tokenlist;
		s = s.trim();
	   
		Semantic ss= new Semantic(s);
		TypeIR a=new TypeIR();
		TypeIR b=new TypeIR();
		 a=null;
		 b=null;
		
		boolean aresult = true;
		boolean result = ss.Compat(a,b);
		assertEquals(aresult,result);
	
	}

}
