package yuyi;
import scanner.Scanner;
import parse.Parse;
public class test {

	/**
	 * ������
	 * �����﷨��������
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �Զ����ɷ������
		String s = "program sd \n   type  i=integer; \n  sarray=array [0..5] of integer;\n var  i x;\n sarray y;\n  integer ss;\n procedure sd(integer a);\n begin \n   write(a)\n end  \n begin \n   ss:=0;\n   while ss<6 do\n     y[ss]:=ss;\n     x:=y[ss];\n    sd(x);\n     ss:=ss+1\n   endwh\n end.";
		Scanner scan = new Scanner(s);
		s = scan.tokenlist;
		s = s.trim();
		Parse p = new Parse(s);
		if(p.Error)
		{
			System.out.print(p.serror);
		}
		else
		{
			System.out.print(p.stree);
		}
	//	AnalYuyi a = new AnalYuyi(s);
		//System.out.println(s);
		/*
		  if (a.Error1)
          {
			  System.out.print(a.serror);
          } 
          else if (a.Error)
          {
        	  System.out.print(a.yerror);
          }
          else
          {
        	  System.out.print(a.ytable);
          } 
		*/
		
		
	}

}
