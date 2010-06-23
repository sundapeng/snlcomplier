package midcode;
import java.util.*;

/*Դ�����Ӧ���м�������б�ʾ*/
class CodeFile
{   
    CodeR codeR=new CodeR();
    CodeFile former=null;
    CodeFile next=null;
}
/*�м����Ľṹ*/
class CodeR
{    
    String codekind;
    ArgRecord arg1;  
    ArgRecord arg2;
    ArgRecord arg3;
}  
class ArgRecord  
{  
    String form;
    MidAttr midAttr=new MidAttr();  /*������ARG�ṹ��Ҫ��¼����Ϣ*/
}  
class MidAttr		
{  
    int value;  /*��¼����ֵ*/
    int label;  /*��¼��ŵ�ֵ*/
    Addr addr;
}
class Addr
{ 
    String name;    /*ע�����������Ѿ�û�ã����ﱣ��ֻ��Ϊ����ʾ�������*/
    int dataLevel;
    int dataOff;
    String access;  /*����AccessKind��ǰ�涨��*/
}

/********************************************************************/
/* ��  �� Midcode	                                            */
/* ��  �� �ܳ���Ĵ���					            */
/* ˵  �� ����һ���࣬�����ܳ���                                    */
/********************************************************************/
public class MidCode
{
/*��ʱ������ţ�ȫ�ֱ���,ÿ�����̿�ʼ����TempOffset����
  ��ʼ����ע�����ԣ���ͬ�����У������б����ͬ����ʱ����������
  �������ǻ�����ɣ����Բ��������⣻�����Ż������ǶԻ���������Ż���
  ÿ�������������һ�����̣�Ҳ���������� */
int TempOffset=0;

/*���ֵ��ȫ�ֱ���*/
int Label=0;

/*ָ���һ���м����*/
CodeFile firstCode;

/*ָ��ǰ���һ���м����*/
CodeFile lastCode;

/*��¼������display���ƫ����*/
int StoreNoff;

public boolean Error=false;
public boolean Error1=false;
public String yerror;
public String serror;
public String midcode;
public CodeFile mid;

public MidCode(String s)
{
    AnalYuyi a=new AnalYuyi(s);
    StoreNoff=a.StoreNoff;
    if (a.Error1)
    {
        Error1=true;
        serror=a.serror;
    }
    else if (a.Error)
    {
        Error=true;
        yerror=a.yerror;
    }
    else
    {
        mid=GenMidCode(a.yuyiTree);
        PrintMidCode(mid);
    }
}
 
/********************************************************/
/* ������  GenMidCode 	  				*/
/* ��  ��  �м��������������				*/
/* ˵  ��  ���й������������ù��������Ĵ�������������   */
/*         ���ó�����Ĵ������ɺ���			*/
/********************************************************/
CodeFile GenMidCode(TreeNode t)
{  
    /*���й���������������Ӧ���������������������м����*/
    TreeNode t1=t.child[1];
    while (t1!=null)
    { 
        if (t1.nodekind.equals("ProcDecK"))
            GenProcDec(t1);
        t1=t1.sibling; 
    }
  
    /*display�������sp��ƫ��*/
    ArgRecord Noff = ARGValue(StoreNoff);

    /*���������������������*/
    CodeFile code = GenCode("MENTRY",null,null,Noff);

    /*��ʼ����ʱ�����Ŀ�ʼ���,Ϊ��ʱ�������ĵ�һ����ַ*/
    TempOffset = StoreNoff + 1;

    /*����������еĴ������ɺ���*/
    GenBody(t.child[2]);

    /*�����������AR�Ĵ�С������������м����*/
    int size = TempOffset;
    ArgRecord sizeArg = ARGValue(size);
    code.codeR.arg2= sizeArg;

    return firstCode;
}
/****************************************************/
/* ������  GenProcDec				    */
/* ��  ��  ���������м�������ɺ���		    */
/* ˵  ��  ���ɹ�������м���룬���ɹ�������м�   */
/*	   ���룬���ɹ��̳��ڵ��м����		    */	
/****************************************************/
void GenProcDec(TreeNode t)
{  
    /*�õ����̵���ڱ��*/
    int ProcEntry = NewLabel();
  
    /*�������ڷ��ű��еĵ�ַ*/
    SymbTable Entry = t.table[0];
    /*������ڱ�ţ�������ڵ���*/
    Entry.attrIR.proc.codeEntry = ProcEntry;

    /*���̵�display���ƫ����*/
    int noff = Entry.attrIR.proc.nOff;
 
    /*�õ����̵Ĳ�������ARG�ṹ*/
    int procLevel = Entry.attrIR.proc.level;
    ArgRecord levelArg = ARGValue(procLevel);
  
    /*�������ڲ����й���������������Ӧ���������������������м����*/
    TreeNode t1=t.child[1];
    while (t1!=null)
    { 
        if (t1.nodekind.equals("ProcDecK"))
            GenProcDec(t1);
        t1=t1.sibling; 
    }

    /*������������м����*/ 
    ArgRecord arg1 = ARGLabel(ProcEntry);
    CodeFile code = GenCode("PENTRY",arg1,null,levelArg);
  
    /*��ʼ����ʱ�����Ŀ�ʼ���,Ϊ������ʱ�������ĵ�һ����ַ*/
    TempOffset =  noff + procLevel+1;

    /*����������еĴ������ɺ������������*/
    GenBody(t.child[2]);

    /*�õ����̵�AR�Ĵ�С,�������������м����*/
    int size = TempOffset;
    ArgRecord sizeArg = ARGValue(size);
    code.codeR.arg2 = sizeArg;

    /*�������̳����м����*/
    GenCode("ENDPROC",null,null,null);
}
/****************************************************/
/* ������  GenBody				    */
/* ��  ��  ��������м�������ɺ���		    */
/* ˵  ��  ���ڴ����������߳����壬		    */
/*	   ѭ������������			    */	
/****************************************************/
void GenBody(TreeNode t)
{  
    TreeNode t1 = t;
    /*��ָ��ָ���һ�����*/
    if (t1.nodekind.equals("StmLK"))
	t1=t1.child[0];

   while (t1!=null)
   { 
       /*������䴦����*/
       GenStatement(t1);
       t1= t1.sibling;
   }
}
/****************************************************/
/* ������  GenStatement				    */
/* ��  ��  ��䴦����	        		    */
/* ˵  ��  �������ľ������ͣ��ֱ������Ӧ��	    */
/*	   ��䴦����				    */
/****************************************************/
void GenStatement(TreeNode t) 
{  
    if (t.kind.equals("AssignK"))
	GenAssignS(t);  
    else if (t.kind.equals("CallK"))
	GenCallS(t);   
    else if (t.kind.equals("ReadK"))     
        GenReadS(t);   
    else if (t.kind.equals("WriteK"))     
        GenWriteS(t);	
    else if (t.kind.equals("IfK"))       
        GenIfS (t);	   
    else if (t.kind.equals("WhileK"))  	
        GenWhileS(t);		
    else if (t.kind.equals("ReturnK"))    /*ֱ�������м����*/  	
        GenCode("RETURNC",null,null,null);
}
/****************************************************/
/* ������  GenAssignS				    */
/* ��  ��  ��ֵ��䴦����        	            */
/* ˵  ��  �����󲿱����������Ҳ����ʽ������       */
/*         ��ֵ����м���� 			    */
/****************************************************/
void GenAssignS(TreeNode t)
{   
    /*���ø�ֵ�󲿱����Ĵ�����*/
    ArgRecord Larg = GenVar(t.child[0]);
    /*���ø�ֵ�Ҳ����ʽ�Ĵ�����*/
    ArgRecord Rarg = GenExpr(t.child[1]);
    /*���ɸ�ֵ����м����*/
    GenCode("ASSIG",Rarg,Larg,null);
}
/****************************************************/
/* ������  GenVar				    */
/* ��  ��  ����������        		            */
/* ˵  ��					    */
/****************************************************/
ArgRecord GenVar(TreeNode t)
{ 
    int low,size;
    FieldChain head;

    /*���ɱ�������ARG�ṹ, EntryΪ��ʶ���ڷ��ű��еĵ�ַ*/
    SymbTable Entry = t.table[0];
    ArgRecord V1arg = ARGAddr(t.name[0],Entry.attrIR.var.level,
Entry.attrIR.var.off,Entry.attrIR.var.access);
    
    /*���ص�ARG�ṹ*/
    ArgRecord Varg=null;
    if (t.attr.expAttr.varkind.equals("IdV"))
        /*��ʶ����������*/
 	Varg = V1arg; 
    else if (t.attr.expAttr.varkind.equals("ArrayMembV"))
    {    
	/*�����Ա��������*/
	/*���������½�������С��ARG�ṹ*/
	low = Entry.attrIR.idtype.array.low;
	size = Entry.attrIR.idtype.array.elementTy.size;
        Varg = GenArray(V1arg,t,low,size);
    }
    else if (t.attr.expAttr.varkind.equals("FieldMembV"))
    {
        /*���������*/    
	head = Entry.attrIR.idtype.body;
	Varg = GenField(V1arg,t,head);
    }
    return Varg;
}
/****************************************************/
/* ������  GenArray				    */
/* ��  ��  �����Ա����������    		    */
/* ˵  ��  �ɺ���GenVar����GenField����	    */
/****************************************************/      	  
ArgRecord GenArray(ArgRecord V1arg,TreeNode t,int low,int size)  		  
{   
    /*�����±���ʽ*/
    ArgRecord Earg= GenExpr(t.child[0]);

    ArgRecord lowArg = ARGValue(low);
    ArgRecord sizeArg= ARGValue(size);
    /*����������ʱ����*/
    ArgRecord temp1= NewTemp("dir");
    ArgRecord temp2= NewTemp("dir");
    /*ע����ʾ���ӱ�������ʱ�������ڼ�ӷ���*/
    ArgRecord temp3= NewTemp("indir"); 
	      
    /*�����м����*/
    GenCode("SUB", Earg, lowArg ,temp1);
    GenCode("MULT",temp1,sizeArg,temp2);
    GenCode("AADD",V1arg,temp2, temp3);

    return temp3;
}		  		  		  
/****************************************************/
/* ������  GenField				    */
/* ��  ��  �����������    			    */
/* ˵  ��  �ɺ���GenVar����			    */
/****************************************************/		  
ArgRecord GenField(ArgRecord V1arg,TreeNode t,FieldChain head)		  
{   
    ArgRecord FieldV;
    /*t1ָ��ǰ���Ա*/
    TreeNode t1 = t.child[0];
    FieldChain Entry2=new FieldChain();

    FindField(t1.name[0],head,Entry2);
    /*����������е�ƫ��*/
    int off = Entry2.off;
    ArgRecord offArg = ARGValue(off);
    /*ע����ʾ���ӱ�������ʱ�������ڼ�ӷ���*/
    ArgRecord temp1 = NewTemp("indir");
    GenCode("AADD",V1arg,offArg,temp1);
    /*�����������*/
    if (t1.attr.expAttr.varkind.equals("ArrayMembV"))
    {  
        int low = Entry2.unitType.array.low;
 	int size= Entry2.unitType.array.elementTy.size;
	FieldV = GenArray(temp1,t1,low,size);
    }
    else  /*���Ǳ�ʶ������*/
   	FieldV = temp1;

    return FieldV;
}
/****************************************************/
/* ������  GenExpr				    */
/* ��  ��  ���ʽ������        		    */
/* ˵  ��					    */
/****************************************************/
ArgRecord GenExpr(TreeNode t)
{ 
    ArgRecord arg=null;
    ArgRecord Larg;
    ArgRecord Rarg;
    ArgRecord temp;

    if (t.kind.equals("VariK"))
        arg = GenVar(t);
    else if (t.kind.equals("ConstK"))
	/*�õ�ֵ��ARG�ṹ*/
	arg = ARGValue(t.attr.expAttr.val);
    else if (t.kind.equals("OpK"))
    {
	/*�����󲿺��Ҳ�*/
	Larg = GenExpr(t.child[0]);
	Rarg = GenExpr(t.child[1]);

	/*���ݲ�������𣬵õ��м��������*/
	String op=null;
	if (t.attr.expAttr.op.equals("LT"))
	    op = "LTC"; 
	else if (t.attr.expAttr.op.equals("EQ"))	
            op = "EQC"; 
	else if (t.attr.expAttr.op.equals("PLUS"))		
            op = "ADD";
	else if (t.attr.expAttr.op.equals("MINUS"))   
            op = "SUB"; 
	else if (t.attr.expAttr.op.equals("TIMES"))    
            op = "MULT"; 
	else if (t.attr.expAttr.op.equals("OVER"))    
            op = "DIV"; 
	/*����һ���µ���ʱ����*/	
        temp = NewTemp("dir");
	/*�����м����*/
	GenCode(op,Larg,Rarg,temp);
	arg = temp ;
    }
    return arg;
}
/****************************************************/
/* ������  GenCall				    */
/* ��  ��  ���̵��ô�����        		    */
/* ˵  ��  �ֱ���ñ��ʽ�������������ʵ�Σ���   */
/*	   ������Ӧ����ʵ�ν���м���룻�ӷ��ű��� */
/*	   ���̱�ʶ�������У��鵽��ڱ�ţ��������� */
/*	   �����м����				    */
/****************************************************/
void GenCallS(TreeNode t)
{
    /*ȡ�ù��̱�־���ڷ��ű��еĵ�ַ*/
    SymbTable Entry = t.child[0].table[0];
    ParamTable param = Entry.attrIR.proc.param;
    /*���ñ��ʽ�������������ʵ�Σ�
      ��������Ӧ����ʵ�ν���м����*/
    TreeNode t1 = t.child[1];
    ArgRecord Earg;
    while (t1!=null)
    { 
        Earg = GenExpr(t1);

        /*��¼������ƫ��*/
        int paramOff = param.entry.attrIR.var.off;
	ArgRecord OffArg = ARGValue(paramOff);
        /*��ʵ�ν���м����*/
	if (param.entry.attrIR.var.access.equals("dir")) 
	    /*ֵ�ν���м����*/
            GenCode("VALACT",Earg,OffArg,null);
	else  /*��ν���м����*/
	    GenCode("VARACT",Earg,OffArg,null);
	
	t1 = t1.sibling;
        param = param.next;
    } 
    /*������ڱ�ż���ARG�ṹ*/
    int label = Entry.attrIR.proc.codeEntry;
    ArgRecord labelarg = ARGLabel(label);
   
    /*���̵�display���ƫ����*/
    int Noff = Entry.attrIR.proc.nOff;
    ArgRecord Noffarg = ARGValue(Noff);

    /*���ɹ��̵����м����*/
    GenCode ("CALL",labelarg,null,Noffarg);
}
/****************************************************/
/* ������  GenReadS				    */
/* ��  ��  ����䴦����        		    */
/* ˵  ��  �õ����������ARG�ṹ�����ɶ�����м����*/
/****************************************************/
void GenReadS(TreeNode t)
{ 
    SymbTable Entry = t.table[0];
    ArgRecord Varg = ARGAddr(t.name[0],Entry.attrIR.var.level,
Entry.attrIR.var.off,Entry.attrIR.var.access);
    /*���ɶ�����м����*/
    GenCode("READC",Varg,null,null);
}
/****************************************************/
/* ������  GenWrite				    */
/* ��  ��  д��䴦����        		    */
/* ˵  ��  ���ñ��ʽ���м�������ɺ�����������д   */
/*	   �����м����			    */
/****************************************************/
void GenWriteS(TreeNode t)
{   
    /*���ñ��ʽ�Ĵ���*/
    ArgRecord Earg = GenExpr(t.child[0]);
    /*����д����м����*/
    GenCode("WRITEC",Earg,null,null);
}
/****************************************************/
/* ������  GenIfs				    */
/* ��  ��  ������䴦����        	            */
/* ˵  ��					    */
/****************************************************/
void GenIfS(TreeNode t)
{   
    /*����else������ڱ�ţ�����ARG�ṹ*/
    int elseL = NewLabel();
    ArgRecord ElseLarg=ARGLabel(elseL);

    /*����if�����ڱ�ţ�����ARG�ṹ*/
    int outL = NewLabel();
    ArgRecord OutLarg = ARGLabel(outL);

    /*�������ʽ���м��������*/
    ArgRecord Earg = GenExpr(t.child[0]);

    /*�����ʽΪ�٣���ת��else��ڱ��*/
    GenCode("JUMP0",Earg,ElseLarg,null);
    
    /*then�����м��������*/
    GenBody(t.child[1]);
    
    /*����if����*/
    GenCode("JUMP",OutLarg,null,null);

    /*else������ڱ������*/
    GenCode("LABEL",ElseLarg,null,null);

    /*else�����м��������*/
    GenBody(t.child[2]);

    /*if�����ڱ������*/
    GenCode("LABEL",OutLarg,null,null);
}
/****************************************************/
/* ������  GenWhileS				    */
/* ��  ��  ѭ����䴦����        		    */
/* ˵  ��  ��ѭ����ںͳ����ò�ͬ���м�����־���� */
/*	   Ϊ��ѭ������ʽ�������Ҫ		    */
/****************************************************/
void GenWhileS(TreeNode t)
{   
    /*����while�����ڱ�ţ�����ARG�ṹ*/
    int inL = NewLabel() ;
    ArgRecord InLarg = ARGLabel(inL);

    /*����while�����ڱ�ţ�����ARG�ṹ*/
    int outL = NewLabel();
    ArgRecord OutLarg = ARGLabel(outL);

    /*while�����ڱ������*/
    GenCode("WHILESTART",InLarg,null,null);
    
    /*�������ʽ���м��������*/
    ArgRecord Earg = GenExpr(t.child[0]);

    /*�����ʽΪ�٣���ת��while������*/
    GenCode("JUMP0",Earg,OutLarg,null);
    
    /*ѭ�����м��������*/
    GenBody(t.child[1]);
    
    /*����while���*/
    GenCode("JUMP",InLarg,null,null);

    /*while���ڱ������*/
    GenCode("ENDWHILE",OutLarg,null,null);
}
/********************************************************/
/* ������  NewTemp		  			*/
/* ��  ��  ����һ���µ���ʱ������ARG�ṹ		*/
/* ˵  ��  ��ʱ�����Ĳ���Ϊ-1��ƫ��Ϊ���ֵ�����ʷ�ʽ�� */
/*	   ����ȷ��					*/
/********************************************************/
ArgRecord NewTemp(String access)
{  
    ArgRecord newTemp=new ArgRecord();
    /*��д��ʱ������ARG����*/
   
    newTemp.form="AddrForm";
    newTemp.midAttr.addr=new Addr();
    newTemp.midAttr.addr.dataLevel=-1 ;
    newTemp.midAttr.addr.dataOff=TempOffset ;
    newTemp.midAttr.addr.access=access;
    /*��ʱ������ż�1*/   
    TempOffset++;
      
    return newTemp;
}
/********************************************************/
/* ������  NewLabel		  			*/
/* ��  ��  ����һ���µı��ֵ				*/
/* ˵  ��  ͨ��ȫ�ֱ���Label��1�������µı��ֵ		*/
/********************************************************/
int NewLabel()
{  
    Label++;  
    return Label;
}
/********************************************************/
/* ������  ARGAddr		  			*/
/* ��  ��  ���ڸ����ı���������Ӧ��ARG�ṹ		*/
/* ˵  ��  						*/
/********************************************************/
ArgRecord ARGAddr(String id,int level,int off,String access)
{   
    ArgRecord arg = new ArgRecord();
    /*��д����ARG�ṹ������*/
    arg.form = "AddrForm";
    arg.midAttr.addr=new Addr();
    arg.midAttr.addr.name=id;
    arg.midAttr.addr.dataLevel=level;
    arg.midAttr.addr.dataOff=off;
    arg.midAttr.addr.access=access;
		  
    return arg;
}
/********************************************************/
/* ������  ARGLabel		  			*/
/* ��  ��  ���ڸ����ı�Ų�����Ӧ��ARG�ṹ		*/
/* ˵  ��  						*/
/********************************************************/
ArgRecord ARGLabel(int label)
{  
    ArgRecord arg = new ArgRecord();
    arg.form = "LabelForm";
    arg.midAttr.label = label;

    return arg;
}
/********************************************************/
/* ������  ARGValue		  			*/
/* ��  ��  ���ڸ����ĳ���ֵ������Ӧ��ARG�ṹ	        */
/* ˵  ��  						*/
/********************************************************/
ArgRecord ARGValue(int value)
{ 
    ArgRecord arg = new ArgRecord();
    arg.form = "ValueForm";
    arg.midAttr.value = value;

    return arg;
}
/********************************************************/
/* ������  GenCode 		  			*/
/* ��  ��  ���ݸ�������������һ���м����		*/
/* ˵  ��						*/
/********************************************************/
CodeFile GenCode(String codekind,ArgRecord Arg1,ArgRecord Arg2,ArgRecord Arg3)
{ 
    CodeFile newCode = new CodeFile();
    /*��д���������*/	
    newCode.codeR.codekind = codekind;
    newCode.codeR.arg1 = Arg1;  
    newCode.codeR.arg2 = Arg2;
    newCode.codeR.arg3 = Arg3;
    /*�����м�������*/
    if (firstCode==null)
	firstCode = newCode;
    else
    {	   
        lastCode.next = newCode;
	newCode.former = lastCode;
    }
    lastCode = newCode;

    return newCode;
}
/********************************************************/
/* ������  FindField	  				*/
/* ��  ��  ���Ҽ�¼������				*/
/* ˵  ��  ����ֵΪ�Ƿ��ҵ���־������Entry���ش�������  */
/*	   ��¼������е�λ��.			        */
/********************************************************/
boolean FindField(String Id,FieldChain head,FieldChain Entry)
{ 
    boolean  present=false;
    /*��¼��ǰ�ڵ�*/
    FieldChain currentItem = head;
    /*�ӱ�ͷ��ʼ���������ʶ����ֱ���ҵ��򵽴��β*/
    while ((currentItem!=null)&&(!present))
    { 
        if  (currentItem.id.equals(Id)) 
	{ 
            present=true;
	    if (Entry!=null)
	    {
	        Entry.id=currentItem.id;
                Entry.off=currentItem.off;
                Entry.unitType=currentItem.unitType;
                Entry.next=currentItem.next;
            }
        }
        else  
            currentItem=currentItem.next;
    }
    return present;
}
/********************************************************/
/* ������  PrintCodeName  				*/
/* ��  ��  ��ӡ��������				*/
/* ˵  ��  �ɺ���PrintOneCode����			*/
/********************************************************/
void PrintCodeName(String kind)
{
             if (kind.equals("ADD"))                 
                 midcode=midcode+"ADD";
             else if (kind.equals("SUB"))
                 midcode=midcode+"SUB";  
             else if (kind.equals("MULT"))
                 midcode=midcode+"MULT";  
             else if (kind.equals("DIV"))
                 midcode=midcode+"DIV";  
             else if (kind.equals("EQC"))
                 midcode=midcode+"EQ";  
             else if (kind.equals("LTC"))
                 midcode=midcode+"LT";  
             else if (kind.equals("READC"))
                 midcode=midcode+"READ";  
             else if (kind.equals("WRITEC"))
                 midcode=midcode+"WRITE";  
             else if (kind.equals("RETURNC"))
                 midcode=midcode+"RETURN";  
             else if (kind.equals("ASSIG"))
                 midcode=midcode+"ASSIG";  
             else if (kind.equals("AADD"))
                 midcode=midcode+"AADD";  
             else if (kind.equals("LABEL"))
                 midcode=midcode+"LABEL";  
             else if (kind.equals("JUMP0"))
                 midcode=midcode+"JUMP0";  
             else if (kind.equals("JUMP"))
                 midcode=midcode+"JUMP";  
             else if (kind.equals("CALL"))
                 midcode=midcode+"CALL";  
             else if (kind.equals("VARACT"))
                 midcode=midcode+"VARACT";  
             else if (kind.equals("VALACT"))
                 midcode=midcode+"VALACT";  
             else if (kind.equals("PENTRY"))
                 midcode=midcode+"PENTRY";  
             else if (kind.equals("ENDPROC"))
                 midcode=midcode+"ENDPROC";  
             else if (kind.equals("MENTRY"))
                 midcode=midcode+"MENTRY";  
             else if (kind.equals("ENDWHILE"))
                 midcode=midcode+"ENDWHILE";  
             else if (kind.equals("WHILESTART"))
                 midcode=midcode+"WHILESTART";
}
/********************************************************/
/* ������  PrintCotent  				*/
/* ��  ��  ��ӡARG�ṹ������				*/
/* ˵  ��  �ɺ���PrintOneCode����			*/
/********************************************************/
void PrintContent(ArgRecord arg)
{
             if (arg.form.equals("LabelForm"))
                 midcode=midcode+String.valueOf(arg.midAttr.label);
             else if (arg.form.equals("ValueForm")) 
                 midcode=midcode+String.valueOf(arg.midAttr.value);
             else if (arg.form.equals("AddrForm")) 
	     {   
		 if (arg.midAttr.addr.dataLevel!=-1)
		     midcode=midcode+arg.midAttr.addr.name;
		 else  
		     midcode=midcode+"temp"+String.valueOf(arg.midAttr.addr.dataOff);
	     }
}
/********************************************************/
/* ������  PrintOneCode 				*/
/* ��  ��  ��ӡһ���м����				*/
/* ˵  ��  �ɺ���PrintMidCode����			*/
/********************************************************/
void PrintOneCode(CodeFile code)
{ 
             PrintCodeName(code.codeR.codekind);
             midcode=midcode+"    ";
             if (code.codeR.arg1!=null)
	         PrintContent(code.codeR.arg1);
             else  
                 midcode=midcode+"    ";
             midcode=midcode+"    ";
             if (code.codeR.arg2!=null)
	         PrintContent(code.codeR.arg2);
             else  
                 midcode=midcode+"    ";
             midcode=midcode+"    ";
             if (code.codeR.arg3!=null)
	         PrintContent(code.codeR.arg3);
             else                   
                 midcode=midcode+"    ";  
}
/********************************************************/
/* ������  PrintMidCode 				*/
/* ��  ��  ��ӡ�м��������				*/
/* ˵  ��						*/
/********************************************************/
void PrintMidCode(CodeFile firstCode)
{   
    int i = 0;
    CodeFile code = firstCode;
    midcode="";
    while (code!=null)
    { 
         midcode=midcode+String.valueOf(i)+":  ";
         PrintOneCode(code);
	 midcode=midcode+"\n";
	 code = code.next;
	 i++;
    }
}
}

/******************************************/
class SymbTable  /* ���������ʱ�õ� */
{
    String idName;
    AttributeIR  attrIR=new AttributeIR();
    SymbTable next=null;
}
class AttributeIR
{
    TypeIR  idtype=new TypeIR();
    String kind;	
    Var var;
    Proc proc;
}
class Var
{
    String access;
    int level;
    int off;
    boolean isParam;
}
class Proc
{
    int level;
    ParamTable param;
    int mOff;
    int nOff;
    int procEntry;
    int codeEntry;
}
class ParamTable
{
    SymbTable entry=new SymbTable();
    ParamTable next=null;
}
class TypeIR
{
    int size;
    String kind;
    Array array;
    FieldChain body;
}
class Array
{
    TypeIR indexTy=new TypeIR();
    TypeIR elementTy=new TypeIR();
    int low;
    int up;
}
class FieldChain
{
    String id;
    int off;
    TypeIR unitType=new TypeIR();
    FieldChain next=null;
}
/*******************************************/
class TreeNode   /* �﷨�����Ķ��� */
{
    TreeNode child[]=new TreeNode[3];
    TreeNode sibling=null;
    int lineno;
    String nodekind;
    String kind;
    int idnum;
    String name[]=new String[10];
    SymbTable table[]=new SymbTable[10];
    Attr attr=new Attr();
}   
class Attr
{
    ArrayAttr arrayAttr=null;  /* ֻ�õ�����һ�����õ�ʱ�ٷ����ڴ� */
    ProcAttr procAttr=null;
    ExpAttr expAttr=null;
    String type_name;
}
class ArrayAttr
{
    int low;
    int up;
    String childtype;
}
class ProcAttr
{
    String paramt;
}
class ExpAttr
{
    String op;
    int val;
    String varkind;
    String type;
}
/************************************************/
class TokenType       /****Token���еĶ���*******/
{
    int lineshow;
    String Lex;
    String Sem;
} 

/********************************************************************/
/* ��  �� AnalYuyi	                                            */
/* ��  �� �ܳ���Ĵ���					            */
/* ˵  �� ����һ���࣬�����ܳ���                                    */
/********************************************************************/
class AnalYuyi
{
/* SCOPESIZEΪ���ű�scopeջ�Ĵ�С*/
int SCOPESIZE = 1000;

/*scopeջ*/
SymbTable scope[]=new SymbTable[SCOPESIZE];

/*��¼��ǰ����*/
int  Level=-1;
/*��¼��ǰƫ�ƣ�*/
int  Off;
/*��¼�������displayOff*/
int mainOff;
/*��¼��ǰ���displayOff*/
int savedOff;

/*ע�����Ա��ƫ������0��ʼ��*/
int fieldOff = 0;

/*��¼������display���ƫ����*/
int StoreNoff;

/*����Ŀ�����������Ҫ��initOffӦΪAR�׵�ַsp���βα�������ƫ��7*/	
int initOff=7;

/*�ֱ�ָ�����ͣ��ַ��ͣ�bool���͵��ڲ���ʾ*/
TypeIR  intptr = new TypeIR();
TypeIR  charptr = new TypeIR();
TypeIR  boolptr = new TypeIR();

/*����׷�ٱ�־*/
boolean Error=false;
boolean Error1=false;
String yerror;
String serror;
TreeNode yuyiTree;

AnalYuyi(String s)
{
    Parse r=new Parse(s);
    Error1=r.Error;
    if (Error1)
        serror=r.serror;
    else
    {
        yuyiTree=r.yufaTree;
        Analyze(yuyiTree);
    }
}
/****************************************************/
/****************************************************/
/****************************************************/
/* ������  Analyze				    */
/* ��  ��  �������������        		    */
/* ˵  ��  ���﷨���ĸ��ڵ㿪ʼ�������������       */	
/****************************************************/
void Analyze(TreeNode t)	
{ 
    TreeNode p = null;

    /*����һ���µķ��ű���ʼ�������*/
    CreatSymbTable();

    /*���������ڲ���ʾ��ʼ������*/
    initiate();

    /*�﷨���������ڵ�*/
    p=t.child[1];
    while (p!=null) 
    {
        if(p.nodekind.equals("TypeK") ) 
	    TypeDecPart(p.child[0]); 
        else if(p.nodekind.equals("VarK") )  
	    VarDecPart(p.child[0]);  
        else if(p.nodekind.equals("ProcDecK") )  
            procDecPart(p);       
	else
	    AnalyzeError(t,"no this node kind in syntax tree!",null);
        p = p.sibling ;/*ѭ������*/
     }
	
    /*������*/
    t = t.child[2];
    if(t.nodekind.equals("StmLK"))
	BodyA(t);
	
    /*�������ű�*/
    if (Level!=-1)
        DestroySymbTable();
	
    /*����������*/
    if(Error)
	AnalyzeError(null," Analyze Error ",null);
} 

/****************************************************/
/* ������  TypeDecPart				    */
/* ��  ��  ����һ����������     		    */
/* ˵  ��  �����﷨���е����������ڵ㣬ȡ��Ӧ���ݣ� */
/*	   �����ͱ�ʶ��������ű�.	            */
/****************************************************/            
void TypeDecPart(TreeNode t)
{ 
    boolean present=false;
    AttributeIR Attrib=new AttributeIR();  /*�洢��ǰ��ʶ��������*/
    SymbTable entry = new SymbTable();

    Attrib.kind="typekind";  
    while (t!=null)   
    {
	/*���ü�¼���Ժ����������Ƿ��ظ����������ڵ�ַ*/
	present = Enter(t.name[0],Attrib,entry);	
	
	if (present)
	{
	    AnalyzeError(t," id repeat declaration ",t.name[0]); 
	    entry = null;
	}
	else 
	    entry.attrIR.idtype = TYPEA(t,t.kind);
	t = t.sibling;
    }  
}
/****************************************************/
/* ������  TYPEA 				    */
/* ��  ��  �������͵��ڲ���ʾ     		    */
/* ˵  ��  ���þ������ʹ�����������ڲ���ʾ�Ĺ���   */
/*	   ����ָ�������ڲ���ʾ��ָ��.	            */
/****************************************************/   
TypeIR TYPEA(TreeNode t,String kind)
{ 
    TypeIR typeptr=null;
	
    /*���ݲ�ͬ������Ϣ��������Ӧ�����ʹ�����*/
    if (kind.equals("IdK")) 
	typeptr= NameTYPEA(t);
    else if (kind.equals("IntegerK"))  
        typeptr= intptr;                                        
    else if (kind.equals("CharK"))  
	typeptr= charptr;                                      
    else if (kind.equals("ArrayK"))    
        typeptr= ArrayTYPEA(t); 
    else if (kind.equals("RecordK"))  
        typeptr= RecordTYPEA(t); 
    else
    { 
        AnalyzeError(t,"bug: no this type in syntax tree ",null);
        return null;
    }	
    return typeptr;
}
/****************************************************/
/* ������  NameTYPEA				    */
/* ��  ��  ��������Ϊ���ͱ�ʶ��ʱ������	            */
/* ˵  ��  �������µ����ͣ����ش����ͱ�ʶ�������ͣ� */
/*	   ������������                           */
/****************************************************/  
TypeIR NameTYPEA(TreeNode t)
{  
    SymbTable Entry=new SymbTable();                     
    TypeIR temp=null;
    boolean present;

    present= FindEntry(t.attr.type_name,Entry);
    /*������ͱ�ʶ��δ������*/
    if (!present)
	AnalyzeError(t," id use before declaration ",t.attr.type_name);
    /*�������ͱ�ʶ����*/  
    else if (!(Entry.attrIR.kind.equals("typekind")))
	AnalyzeError(t," id is not type id ",t.attr.type_name);
    /*���ر�ʶ�������͵��ڲ���ʾ*/
    else
    {  
        temp= Entry.attrIR.idtype;
        return temp;
    }
    return temp;
}	 
/****************************************************/
/* ������  ArrayTypeA				    */
/* ��  ��  �����������͵��ڲ���ʾ   		    */
/* ˵  ��  �����±����ͣ���Ա���ͣ����������С��   */
/*	   ������±곬�����		            */
/****************************************************/   
TypeIR ArrayTYPEA(TreeNode t)
{ 
    TypeIR tempforchild;

    /*����һ���µ��������͵��ڲ���ʾ*/
    TypeIR typeptr=new TypeIR();
    typeptr.array=new Array();
    typeptr.kind="arrayTy";
    /*�±���������������*/
    typeptr.array.indexTy=intptr;                         
    /*��Ա����*/
    tempforchild=TYPEA(t,t.attr.arrayAttr.childtype);
    typeptr.array.elementTy=tempforchild;

    /*��������±�������*/
    int up=t.attr.arrayAttr.up;
    int low=t.attr.arrayAttr.low;
    if (up < low)
	AnalyzeError(t," array up smaller than under ",null);
    else  /*���½�������������ڲ���ʾ��*/
    {       
        typeptr.array.low = low;
        typeptr.array.up = up;
    }
    /*��������Ĵ�С*/
    typeptr.size= (up-low+1)*(tempforchild.size);
    /*����������ڲ���ʾ*/
    return typeptr;
}
/****************************************************/
/* ������  RecordTYPEA				    */
/* ��  ��  �����¼���͵��ڲ���ʾ   		    */
/* ˵  ��  �������ָ��洢�ڼ�¼���ڲ���ʾ�У�   */
/*	   �������¼�Ĵ�С                         */  
/****************************************************/
TypeIR RecordTYPEA(TreeNode t)
{ 
    TypeIR Ptr=new TypeIR();  /*�½���¼���͵Ľڵ�*/
    Ptr.body=new FieldChain();
    Ptr.kind="recordTy";
	
    t = t.child[0];                /*���﷨���Ķ��ӽڵ��ȡ����Ϣ*/

    FieldChain Ptr2=null;
    FieldChain Ptr1=null;
    FieldChain body=null;

    while (t != null)				/*ѭ������*/
    {
	/*��дptr2ָ������ݽڵ�*
	 *�˴�ѭ���Ǵ���������int a,b; */
	for(int i=0 ; i < t.idnum ; i++)
	{     
	    /*�����µ������͵�Ԫ�ṹPtr2*/  
	    Ptr2 = new FieldChain();            
	    if(body == null)
            {
		body = Ptr2; 
                Ptr1 = Ptr2;
	    }
	    /*��дPtr2�ĸ�����Ա����*/
	    Ptr2.id=t.name[i];
	    Ptr2.unitType = TYPEA(t,t.kind);			 
	    
	    /*���Ptr1!=Ptr2����ô��ָ�����*/
	    if(Ptr2 != Ptr1)          
	    {
		/*����������ĵ�Ԫoff*/
		Ptr2.off = (Ptr1.off) + (Ptr1.unitType.size);
		Ptr1.next = Ptr2;
		Ptr1 = Ptr2;
	    }
	}
	/*������ͬ���͵ı�����ȡ�﷨�����ֵܽڵ�*/
	t = t.sibling;
    }	
    /*�����¼�����ڲ��ṹ*/
	
    /*ȡPtr2��offΪ���������¼��size*/
    Ptr.size = Ptr2.off + (Ptr2.unitType.size);
    /*�����������¼���͵�body����*/   
    Fcopy(Ptr.body,body);   

    return Ptr;
}
/****************************************************/
/* ������  VarDecPart				    */
/* ��  ��  �����������з�������    		    */
/* ˵  ��  �������еı�������			    */
/****************************************************/
void VarDecPart(TreeNode t) 
{  
    varDecList(t);
}   
/****************************************************/
/* ������  varDecList 				    */
/* ��  ��  ����һ�������������β�����		    */
/* ˵  ��  ����һ�������ڵ������������б�ʶ����	    */	
/*	   �������Ϣ������ű��У������βΣ���Ҫ   */
/*	   ����һ��������Ϣ��������ʶ���ڷ��ű�� */
/*	   λ�ô洢�ڱ��У����ز�����ı�ͷָ��     */					
/****************************************************/
void varDecList(TreeNode t)
{ 
    boolean present = false;
    SymbTable  Entry=new SymbTable();
    /*��¼����������*/
    AttributeIR Attrib=new AttributeIR();

    while(t!=null)	/*ѭ������*/
    {
	Attrib.kind="varkind";  
	for(int i=0;i<(t.idnum);i++)
	{
	    Attrib.idtype=TYPEA(t,t.kind);
			
	    /*�ж�ʶֵ�λ��Ǳ��acess(dir,indir)*/	
	    if((t.attr.procAttr!=null)&&(t.attr.procAttr.paramt.equals("varparamType")))
	    {
                Attrib.var = new Var();
		Attrib.var.access = "indir";
		Attrib.var.level = Level;
		/*�����βε�ƫ��*/
				
		Attrib.var.off = Off;
		Off = Off+1;
	    }/*����Ǳ�Σ���ƫ�Ƽ�1*/
	    else
	    {
                Attrib.var = new Var();
		Attrib.var.access = "dir";
		Attrib.var.level = Level;
		/*����ֵ�ε�ƫ��*/
		if(Attrib.idtype.size!=0)				
		{
		    Attrib.var.off = Off;
		    Off = Off + (Attrib.idtype.size);
		}
	    }/*���������Ϊֵ�Σ�ƫ�Ƽӱ������͵�size*/
			
	    /*�ǼǸñ��������Լ�����,�������������ڲ�ָ��*/
	    present = Enter(t.name[i],Attrib,Entry);	
	    if(present)
	        AnalyzeError(t," id repeat  declaration ",t.name[0]);
	    else
	        t.table[i] = Entry;
	}
	if(t!=null)
	    t = t.sibling;
    }
	
    /*��������������¼��ʱƫ�ƣ�����Ŀ���������ʱ��displayOff*/
    if(Level==0)
    {
	mainOff = Off;
	/*�洢������AR��display���ƫ�Ƶ�ȫ�ֱ���*/
	StoreNoff = Off;
    }
    /*����������������¼��ʱƫ�ƣ�����������д������Ϣ���noff��Ϣ*/ 
    else 
	savedOff = Off;
} 
/****************************************************/
/* ������  procDecPart				    */
/* ��  ��  һ�������������������  		    */
/* ˵  ��  �������ͷ��������������		    */	
/****************************************************/
void procDecPart(TreeNode t)
{ 
    TreeNode p =t;
    SymbTable entry = HeadProcess(t);   /*�������ͷ*/
		
    t = t.child[1];
    /*��������ڲ������������֣�������������*/	
    while (t!=null) 
    {
	if ( t.nodekind.equals("TypeK") ) 
	    TypeDecPart(t.child[0]); 
        else if ( t.nodekind.equals("VarK") )  
            VarDecPart(t.child[0]);  

	/*������������к���������������ѭ��������дnoff��moff����Ϣ��*
	*�ٴ�����������ѭ�����������޷�����noff��moff��ֵ��      */
	else if ( t.nodekind.equals("ProcDecK") )  {}
	else
	    AnalyzeError(t,"no this node kind in syntax tree!",null);
				
	if(t.nodekind.equals("ProcDecK"))
            break;
	else
            t=t.sibling ;
    }
    entry.attrIR.proc.nOff = savedOff;
    entry.attrIR.proc.mOff = entry.attrIR.proc.nOff + entry.attrIR.proc.level+1;
    /*���̻��¼�ĳ��ȵ���nOff����display��ĳ���*
    *diplay��ĳ��ȵ��ڹ������ڲ�����һ           */

    /*����������������*/
    while(t!=null)
    {
	procDecPart(t);
	t = t.sibling;
    }
    t = p;
    BodyA(t.child[2]);/*����Block*/

    /*�������ֽ�����ɾ�������β�ʱ���½����ķ��ű�*/
    if ( Level!=-1)
	DestroySymbTable();/*������ǰscope*/
}
/****************************************************/
/* ������  HeadProcess				    */
/* ��  ��  �βδ�����         		    */
/* ˵  ��  ѭ����������ڵ㣬��������ÿ���ڵ�õ�   */
/*	   �Ĳ�����������������������β��������� */
/*         ������ָ��				    */	
/****************************************************/
SymbTable HeadProcess(TreeNode t)
{ 
    AttributeIR attrIr = new AttributeIR();
    boolean present = false;
    SymbTable entry = new SymbTable();
		
    /*������*/
    attrIr.kind = "prockind";
    attrIr.idtype = null; 
    attrIr.proc = new Proc();
    attrIr.proc.param = new ParamTable();
    attrIr.proc.level = Level+1;	
	
    if(t!=null)
    {
	/*�ǼǺ����ķ��ű���*/		
	present = Enter(t.name[0],attrIr,entry);
	t.table[0] = entry;
	/*�����β�������*/
    }
    entry.attrIR.proc.param = ParaDecList(t);

    return entry;
}   
/****************************************************/
/* ������  ParaDecList				    */
/* ��  ��  ����һ���βνڵ�        		    */
/* ˵  ��  ���ݲ������βλ��Ǳ�Σ��ֱ���ñ������� */
/*	   �ڵ�Ĵ���������һ��ʵ����Add����ʾ����*/
/*	   ���Ǻ������βΡ�			    */	
/****************************************************/
ParamTable ParaDecList(TreeNode t)
{ 
    TreeNode p=null;
    ParamTable Ptr1=null; 
    ParamTable Ptr2=null;
    ParamTable head=null;
	
    if(t!=null)
    {
	if(t.child[0]!=null)
	    p = t.child[0];   	/*���������ڵ�ĵ�һ�����ӽڵ�*/
	
	CreatSymbTable();		/*�����µľֲ�����*/
	Off = 7;                /*�ӳ����еı�����ʼƫ����Ϊ8*/

	VarDecPart(p);			/*������������*/

	SymbTable Ptr0 = scope[Level];      		 
                                    
	while(Ptr0 != null)         /*ֻҪ��Ϊ�գ��ͷ������ֵܽڵ�*/
	{
	    /*�����βη��ű���ʹ�����������ű��param��*/
	    Ptr2 = new ParamTable();
	    if(head == null)
            {
		head = Ptr2;
                Ptr1 = Ptr2;
            }
	    //Ptr0.attrIR.var.isParam = true;
	    copy(Ptr2.entry,Ptr0);
			
	    if(Ptr2 != Ptr1)          
	    {
		Ptr1.next = Ptr2;
		Ptr1 = Ptr2;
	    }
	    Ptr0 = Ptr0.next;
	}
    }
    return head;   /*�����βη��ű��ͷָ��*/
}
/****************************************************/
/* ������  BodyA				    */
/* ��  ��  ������д�����        		    */
/* ˵  ��  ���ڴ����������߳����壬		    */
/*	  ѭ������������			    */	
/****************************************************/
void BodyA(TreeNode t)
{  
    /*��ָ��ָ���һ�����*/
    if (t.nodekind.equals("StmLK"))
	t=t.child[0];

    /*�����������*/
    while (t!=null)
    { 
        /*������䴦����*/
	StatementA(t);
        t= t.sibling;
    }
}
/****************************************************/
/* ������  StatementA				    */
/* ��  ��  ��䴦����	        		    */
/* ˵  ��  �������ľ������ͣ��ֱ������Ӧ��       */
/*	   ��䴦����				    */
/****************************************************/
void StatementA(TreeNode t) 
{  
    if (t.kind.equals("AssignK"))
	AssignSA(t);  
    else if (t.kind.equals("CallK"))      
        CallSA(t);   
    else if (t.kind.equals("ReadK"))     
        ReadSA(t);    
    else if (t.kind.equals("WriteK"))     
        WriteSA(t);	 
    else if (t.kind.equals("IfK"))     
        IfSA(t);	  
    else if (t.kind.equals("WhileK"))	
        WhileSA(t);	  
    else if (t.kind.equals("ReturnK")) 	
        ReturnSA(t);  
    else
        AnalyzeError(t," bug:no this statement in syntax tree ",null);	
}
/****************************************************/
/* ������  AssignSA				    */
/* ��  ��  ��ֵ��䴦����	       		    */
/* ˵  ��  ����󲿱�ʶ�������ñ��ʽ��������	    */	
/*	   ������ʶ��δ��������������ʶ����   */
/*	   ��ֵ�����ݴ�				    */
/****************************************************/
void AssignSA(TreeNode t)
{ 
    SymbTable entry = new SymbTable();
	
    boolean present = false;
    TypeIR ptr = null;
    TypeIR Eptr = null;
	
    TreeNode child1;
    TreeNode child2;

    child1 = t.child[0];
    child2 = t.child[1];

    if(child1.child[0]==null)
    {	
	/*�ڷ��ű��в��Ҵ˱�ʶ��*/
	present = FindEntry(child1.name[0],entry);
		
	if(present)
	{   /*id���Ǳ���*/
            if (!(entry.attrIR.kind.equals("varkind")))
	    {				
                AnalyzeError(t," left and right is not compatible in assign ",null);				                      Eptr = null;
	    }
	    else
            {
	        Eptr = entry.attrIR.idtype;
		child1.table[0] = entry;
            }
	} 
	else /*��ʶ��������*/
	    AnalyzeError(t,"is not declarations!",child1.name[0]);
	}
	else/*Var0[E]������*/
	{	
            if(child1.attr.expAttr.varkind.equals("ArrayMembV"))
		Eptr = arrayVar(child1);	
	    else /*Var0.id������*/
		if(child1.attr.expAttr.varkind.equals("FieldMembV"))
		    Eptr = recordVar(child1);
	}
	if(Eptr != null)
	{	
	    if((t.nodekind.equals("StmtK"))&&(t.kind.equals("AssignK")))
	    {
		/*����ǲ��Ǹ�ֵ������ ���͵ȼ�*/
		ptr = Expr(child2,null);
		if (!Compat(ptr,Eptr)) 
		    AnalyzeError(t,"ass_expression error!",child2.name[0]);
	    }
	    /*��ֵ����в��ܳ��ֺ�������*/
	}
}
/***********************************************************/
/* ������ Compat                                           */
/* ��  �� �ж������Ƿ�����                                 */
/* ˵  �� ����TINY������ֻ���������͡��ַ����͡��������ͺ� */
/*        ��¼���ͣ����������ݵ������͵ȼۣ�ֻ���ж�ÿ���� */
/*        �����͵��ڲ���ʾ������ָ��ֵ�Ƿ���ͬ���ɡ�       */
/***********************************************************/
boolean Compat(TypeIR tp1,TypeIR tp2)
{
    boolean  present; 
    if (tp1!=tp2)
	present = false;  /*���Ͳ���*/
    else
	present = true;   /*���͵ȼ�*/
    return present;
}

/************************************************************/
/* ������  Expr                                             */
/* ��  ��  �ú���������ʽ�ķ���                           */
/* ˵  ��  ���ʽ����������ص��Ǽ��������������������ԣ� */
/*         ����ʽ�����͡����в���Ekind������ʾʵ���Ǳ��  */
/*         ����ֵ�Ρ�    	                            */
/************************************************************/
TypeIR Expr(TreeNode t,String Ekind)
{
    boolean present = false;
    SymbTable entry = new SymbTable();

    TypeIR Eptr0=null;
    TypeIR Eptr1=null;
    TypeIR Eptr = null;
    if(t!=null)
    {
        if(t.kind.equals("ConstK"))
        { 
	    Eptr = intptr;
	    Eptr.kind = "intTy";
	    if(Ekind!=null)
	        Ekind = "dir";   /*ֱ�ӱ���*/ 
        }
        else if(t.kind.equals("VariK"))
        {
	    /*Var = id������*/
	    if(t.child[0]==null)
	    {	
		/*�ڷ��ű��в��Ҵ˱�ʶ��*/
		present = FindEntry(t.name[0],entry);				
		t.table[0] = entry;

		if(present)
		{   /*id���Ǳ���*/
		    if (!(entry.attrIR.kind.equals("varkind")))
		    {
			AnalyzeError(t," syntax bug: no this kind of exp ",t.name[0]);				                              Eptr = null;
		    }
		    else
		    {
			Eptr = entry.attrIR.idtype;	
			if (Ekind!=null)
			    Ekind = "indir";  /*��ӱ���*/						
		    }
		} 
		else /*��ʶ��������*/
		    AnalyzeError(t,"is not declarations!",t.name[0]);				
	    }
	    else/*Var = Var0[E]������*/
	    {	
                if(t.attr.expAttr.varkind.equals("ArrayMembV"))
		    Eptr = arrayVar(t);	
		/*Var = Var0.id������*/
		else if(t.attr.expAttr.varkind.equals("FieldMembV"))
		    Eptr = recordVar(t);
	    }
	}
        else if(t.kind.equals("OpK"))
        {
	    /*�ݹ���ö��ӽڵ�*/
	    Eptr0 = Expr(t.child[0],null);
	    if(Eptr0==null)
	        return null;
	    Eptr1 = Expr(t.child[1],null);
	    if(Eptr1==null)
		return null;
							
	    /*�����б�*/
	    present = Compat(Eptr0,Eptr1);
	    if (present)
	    {
		if((t.attr.expAttr.op.equals("LT"))||(t.attr.expAttr.op.equals("EQ")))
		    Eptr = boolptr;
                else if((t.attr.expAttr.op.equals("PLUS"))||(t.attr.expAttr.op.equals("MINUS"))||(t.attr.expAttr.op.equals("TIMES"))||(t.attr.expAttr.op.equals("OVER")))  
		    Eptr = intptr;
                                /*�������ʽ*/
		if(Ekind != null)
	            Ekind = "dir"; /*ֱ�ӱ���*/
	    }
	    else 
		AnalyzeError(t,"operator is not compat!",null);
	}
    }
    return Eptr;
}			

/************************************************************/
/* ������  arrayVar                                         */
/* ��  ��  �ú�����������������±����                     */
/* ˵  ��  ���var := var0[E]��var0�ǲ����������ͱ�����E�ǲ�*/
/*         �Ǻ�������±��������ƥ�䡣                     */
/************************************************************/
TypeIR arrayVar(TreeNode t)
{
    boolean present = false;
    SymbTable entry = new SymbTable();

    TypeIR Eptr0=null;
    TypeIR Eptr1=null;
    TypeIR Eptr = null;
	
	
    /*�ڷ��ű��в��Ҵ˱�ʶ��*/

    present = FindEntry(t.name[0],entry);				
    t.table[0] = entry;	
    /*�ҵ�*/
    if(present)
    {
	/*Var0���Ǳ���*/
	if (!(entry.attrIR.kind.equals("varkind")))
	{
	    AnalyzeError(t,"is not variable error!",t.name[0]);			
	    Eptr = null;
	}
	/*Var0�����������ͱ���*/
	else if(entry.attrIR.idtype!=null)
        {
	    if(!(entry.attrIR.idtype.kind.equals("arrayTy")))
	    {
		AnalyzeError(t,"is not array variable error !",t.name[0]);
		Eptr = null;
	    }
	    else
	    {	
		/*���E�������Ƿ����±��������*/
		Eptr0 = entry.attrIR.idtype.array.indexTy;
		if(Eptr0==null)
		    return null;
		Eptr1 = Expr(t.child[0],null);//intPtr;
		if(Eptr1==null)
		    return null;
		present = Compat(Eptr0,Eptr1);
		if(!present)
		{
		    AnalyzeError(t,"type is not matched with the array member error !",null);
		    Eptr = null;
		}
		else
		    Eptr = entry.attrIR.idtype.array.elementTy;
	    }
        }
    }
    else/*��ʶ��������*/
	AnalyzeError(t,"is not declarations!",t.name[0]);
    return Eptr;
}

/************************************************************/
/* ������  recordVar                                        */
/* ��  ��  �ú��������¼��������ķ���                     */
/* ˵  ��  ���var:=var0.id�е�var0�ǲ��Ǽ�¼���ͱ�����id�� */
/*         ���Ǹü�¼�����е����Ա��                       */
/************************************************************/
TypeIR recordVar(TreeNode t)
{
	boolean present = false;
	boolean result = false;
	SymbTable entry = new SymbTable();

	TypeIR Eptr0=null;
	TypeIR Eptr = null;
	FieldChain currentP = new FieldChain();
	
	
	/*�ڷ��ű��в��Ҵ˱�ʶ��*/
	present = FindEntry(t.name[0],entry);				
	t.table[0] = entry;	
	/*�ҵ�*/
	if(present)
	{
	    /*Var0���Ǳ���*/
	    if (!(entry.attrIR.kind.equals("varkind")))
	    {
		AnalyzeError(t,"is not variable error!",t.name[0]);				
		Eptr = null;
	    }
	    /*Var0���Ǽ�¼���ͱ���*/
	    else if(!(entry.attrIR.idtype.kind.equals("recordTy")))
	    {
		AnalyzeError(t,"is not record variable error!",t.name[0]);
		Eptr = null;
	    }
	    else/*���id�Ƿ��ǺϷ�����*/
	    {
		Eptr0 = entry.attrIR.idtype;
		currentP = Eptr0.body;
		while((currentP!=null)&&(!result))
		{  
        	    result = t.child[0].name[0].equals(currentP.id);
		    /*������*/
		    if(result)
			Eptr = currentP.unitType;
		    else
			currentP = currentP.next;
		}	 
		if(currentP==null)
		    if(!result)
		    {
		        AnalyzeError(t,"is not field type!",t.child[0].name[0]);    				                        Eptr = null;
		    }
	            /*���id���������*/
		    else if(t.child[0].child[0]!=null)
			Eptr = arrayVar(t.child[0]);
	    }
	}
	else/*��ʶ��������*/
	    AnalyzeError(t,"is not declarations!",t.name[0]);
	return Eptr;
}
		
/****************************************************/
/* ������  CallSA				    */
/* ��  ��  ����������䴦����    		    */
/* ˵  ��  ���Ǻ�����ʶ�������ü����ʵ���Ƿ�   */	
/*		   ���ݺ���			    */
/****************************************************/
void CallSA(TreeNode t)
{ 
	String Ekind=" ";
	boolean present = false;
	SymbTable entry=new SymbTable();
	TreeNode p = null;

	/*��id����������ű�*/
	present = FindEntry(t.child[0].name[0],entry);		
        t.child[0].table[0] = entry;

	/*δ�鵽��ʾ����������*/
	if (!present)                     
	    AnalyzeError(t,"function is not declarationed!",t.child[0].name[0]);  
        else 
	    /*id���Ǻ�����*/
	    if (!(entry.attrIR.kind.equals("prockind")))     
		AnalyzeError(t,"is not function name!",t.child[0].name[0]);
	    else/*��ʵ��ƥ��*/
	    {
		p = t.child[1];
		/*paramPָ���βη��ű�ı�ͷ*/
		ParamTable paramP = entry.attrIR.proc.param;	
		while((p!=null)&&(paramP!=null))
		{
		    SymbTable paraEntry = paramP.entry;
		    TypeIR Etp = Expr(p,Ekind);/*ʵ��*/
		    /*�������ƥ��*/
		    if ((paraEntry.attrIR.var.access.equals("indir"))&&(Ekind.equals("dir")))
			AnalyzeError(t,"param kind is not match!",null);  
			/*�������Ͳ�ƥ��*/
                    else if((paraEntry.attrIR.idtype)!=Etp)
			AnalyzeError(t,"param type is not match!",null);
		    p = p.sibling;
		    paramP = paramP.next;
		}
		/*����������ƥ��*/
		if ((p!=null)||(paramP!=null))
		    AnalyzeError(t,"param num is not match!",null); 
	    }
}
/****************************************************/
/* ������  ReadSA				    */
/* ��  ��  ����䴦����	    		    */
/* ˵  ��  ����ʶ��δ�������Ǳ�����ʶ����	    */
/****************************************************/
void ReadSA(TreeNode t)
{ 
    SymbTable Entry=new SymbTable();
    boolean present=false;
    /*���ұ�����ʶ��*/
    present = FindEntry(t.name[0],Entry);
    /*�����ڷ��ű��еĵ�ַд���﷨��*/
    t.table[0] = Entry;

    if (!present)   /*����ʶ��δ������*/
	AnalyzeError(t," id no declaration in read ",t.name[0]);
    else if (!(Entry.attrIR.kind.equals("varkind")))   /*���Ǳ�����ʶ����*/ 
        AnalyzeError(t," not var id in read statement ", null);
}
/****************************************************/
/* ������  WriteSA				    */
/* ��  ��  д��䴦����	    		    */
/* ˵  ��  ���ñ��ʽ������������������	    */
/****************************************************/
void WriteSA(TreeNode t)  
{ 
    TypeIR Etp = Expr(t.child[0],null);	
    if(Etp!=null)
	/*������ʽ����Ϊbool���ͣ�����*/
	if (Etp.kind.equals("boolTy"))
		AnalyzeError(t,"exprssion type error!",null);
}
/****************************************************/
/* ������  IfSA					    */
/* ��  ��  ������䴦����	    		    */
/* ˵  ��  ���ǲ������ʽ��������������к���   */
/*	   ����then���ֺ� else ����	            */	
/****************************************************/
void IfSA(TreeNode t)
{ 
    String Ekind=null;
    TypeIR Etp;
    Etp=Expr(t.child[0],Ekind);
    
    if (Etp!=null)   /*���ʽû�д���*/
        if (!(Etp.kind.equals("boolTy")))   /*���ǲ������ʽ��*/
	    AnalyzeError(t," not bool expression in if statement ",null);
	else
	{
	    TreeNode p = t.child[1];
	    /*����then������в���*/
	    while(p!=null)
	    {
		StatementA(p);
		p=p.sibling;
	    }
	    t = t.child[2];		/*����������*/
	    /*����else��䲿��*/
	    while(t!=null)
	    {
		StatementA(t);	
		t=t.sibling;
	    }
	}
}
/****************************************************/
/* ������  WhileSA				    */
/* ��  ��  ѭ����䴦����	    		    */
/* ˵  ��  ���ǲ������ʽ��������������к���   */
/*	   ����ѭ����			            */	
/****************************************************/
void  WhileSA(TreeNode t)
{ 
    TypeIR Etp;
    Etp=Expr(t.child[0],null);
   
    if (Etp!=null)  /*���ʽû�д�*/	  
        if (!(Etp.kind.equals("boolTy")))   /*���ǲ������ʽ��*/
	    AnalyzeError(t," not bool expression in if statement ",null);
    /*����ѭ����*/
    else
    {
	t = t.child[1];
	/*����ѭ������*/
	while(t!=null)
	{ 
	    StatementA(t);
	    t=t.sibling;
	}
    }
}
/****************************************************/
/* ������  ReturnSA				    */
/* ��  ��  ������䴦����	    		    */
/* ˵  ��  ���������������У����������		    */	
/****************************************************/
void  ReturnSA(TreeNode t)
{
    if (Level == 0)
	AnalyzeError(t," return statement cannot in main program ",null);
}

/****************************************************/
/*****************���ܺ���***************************/
/****************************************************/
/* ������  AnalyzeError				    */
/* ��  ��  �������������ʾ��Ϣ			    */
/* ˵  ��  Error����Ϊtrue,��ֹ����Ĵ���	    */
/****************************************************/
void AnalyzeError(TreeNode t,String message,String s)
{   
    if (t==null)
        yerror=yerror+"\n>>> ERROR:"+"Analyze error "+":"+message+s+"\n"; 
    else
        yerror=yerror+"\n>>> ERROR :"+"Analyze error at "+String.valueOf(t.lineno)+": "+message+s+"\n";  

    /* ���ô���׷�ٱ�־ErrorΪTRUE,��ֹ�����һ������ */
    Error = true;
}
/****************************************************/
/* ������  initiate				    */
/* ��  ��  �������ͣ��ַ��ͣ��������͵��ڲ���ʾ	    */
/* ˵  ��  �⼸�����͵��ڲ���ʾʽ�̶��ģ�ֻ�轨��   */
/*	   һ�Σ��Ժ�������Ӧ�����ü���	            */	
/****************************************************/
void initiate()
{   
    /*�������͵��ڲ���ʾ*/
    intptr.kind="intTy";
    intptr.size=1;
    /*�ַ����͵��ڲ���ʾ*/
    charptr.kind="charTy";
    charptr.size=1;
    /*�������͵��ڲ���ʾ*/
    boolptr.kind="boolTy";
    boolptr.size=1;
}

/********************************************************/
/*************���ű���غ���*****************************/
/********************************************************/
/* ������  CreatSymbTable			        */
/* ��  ��  ����һ�����ű�				*/
/* ˵  ��  ��û�����������µķ��ű�ֻ�ǲ�����һ	*/
/********************************************************/
void  CreatSymbTable()
{ 	
    Level = Level +1; 
    scope[Level] = null;	
    Off = initOff;  /* ����Ŀ�����������Ҫ��initOffӦΪAR�׵�ַsp
		       ���βα�������ƫ��7 */	
}
/********************************************************/
/* ������  DestroySymbTable				*/
/* ��  ��  ɾ��һ�����ű�				*/
/* ˵  ��  ���������ͷ�������ű�ռ䣬ֻ�Ǹı�scopeջ  */
/********************************************************/
void  DestroySymbTable()
{
    /*�ò�����1������ʾɾ����ǰ���ű�*/
    Level = Level - 1;
}
/**********************************************************/
/* ������  Enter					  */
/* ��  ��  ��һ����ʶ���������ԵǼǵ����ű�		  */
/* ˵  ��  ����ֵ������ʶ���Ƿ��ظ�����Entry���ش˱�ʶ��  */
/*         �ڷ��ű��е�λ�ã����ظ����򲻵Ǽǣ�EntryΪ��  */
/*	   �����Ǹ���ʶ����λ��				  */
/**********************************************************/
boolean Enter(String id,AttributeIR attribP,SymbTable entry)
{ 
    boolean present = false;
    boolean result = false;
    SymbTable curentry=null;
    SymbTable prentry=null;

    if(scope[Level]==null)
    {
	scope[Level] = new SymbTable();
	curentry = scope[Level];
    }
    else
    {
        curentry = scope[Level];
	while (curentry != null)
	{
	    prentry = curentry;
	    result = id.equals(curentry.idName);
	    if(result)
	    {
		AnalyzeError(null," Enter , id repeat declaration ",null);
		present = true;
	    }
	    else
		curentry = prentry.next;
	}   /*�ڸò���ű��ڼ���Ƿ����ظ��������*/
    
	if(!present)
	{
	    prentry.next = new SymbTable();
	    curentry = prentry.next;
	}
    }
		
    /*����ʶ���������ԵǼǵ�����*/
    curentry.idName = id;

    curentry.attrIR.idtype = attribP.idtype;
    curentry.attrIR.kind = attribP.kind;
    if (attribP.kind.equals("typekind"))
	{}
    else if (attribP.kind.equals("varkind")) 
    {
        curentry.attrIR.var=new Var();
	curentry.attrIR.var.level=attribP.var.level;
	curentry.attrIR.var.off=attribP.var.off;
	curentry.attrIR.var.access=attribP.var.access;
    }
    else if (attribP.kind.equals("prockind")) 
    {
        curentry.attrIR.proc=new Proc();
	curentry.attrIR.proc.level=attribP.proc.level;
	curentry.attrIR.proc.param=attribP.proc.param;
    }
    copy(entry,curentry);
	
    return present;
}
/********************************************************/
/* ������  FindEntry    				*/
/* ��  ��  ����һ����ʶ���Ƿ��ڷ��ű���			*/
/* ˵  ��  ����flag�����ǲ��ҵ�ǰ���ű��������з��ű� */
/*	   ����ֵ�����Ƿ��ҵ�������Entry���ش˱�ʶ����  */
/*	   ���ű��е�λ��				*/
/********************************************************/
boolean FindEntry(String id,SymbTable entry)
{ 
	boolean  present=false;  /*����ֵ*/
	boolean result = false;         /*��ʶ�����ֱȽϽ��*/
	int lev = Level;	 /*��ʱ��¼�����ı���*/

	SymbTable findentry = scope[lev];

	while((lev!=-1)&&(!present))
	{
	    while ((findentry!=null)&&(!present))
	    {
		result = id.equals(findentry.idName);
		if (result)
		    present = true;    
		/*�����ʶ��������ͬ���򷵻�TRUE*/
		else 
		    findentry = findentry.next;
		/*���û�ҵ�������������еĲ���*/
	    }
	    if(!present)
	    {
		lev = lev-1;
                if(lev != -1)
		    findentry = scope[lev];			
	    }
	}/*����ڱ�����û�в鵽����ת����һ���ֲ��������м�������*/
        if (!present)
	    entry = null;
	else 
	    copy(entry,findentry);

	return present;
}
/********************************************************/
/* ������  copy	  				        */
/* ��  ��  ���ƺ���    				        */
/* ˵  ��  ��b�е����ݸ��Ƹ�a			        */
/********************************************************/
void copy(SymbTable a,SymbTable b)
{
    a.idName=b.idName;
    a.attrIR=b.attrIR;
    a.next=b.next;
}
/********************************************************/
/* ������  Fcopy	  				*/
/* ��  ��  ���ƺ���    				        */
/* ˵  ��  ��b�е����ݸ��Ƹ�a			        */
/********************************************************/
void Fcopy(FieldChain a,FieldChain b)
{
    a.id=b.id;
    a.off=b.off;
    a.unitType=b.unitType;
    a.next=b.next;
}

}


/********************************************************************/
/************************�� �� �� ��*********************************/
/********************************************************************/
/********************************************************************/
/* ��  �� Parse	                                            */
/* ��  �� �ܳ���Ĵ���					            */
/* ˵  �� ����һ���࣬�����ܳ���                                    */
/********************************************************************/
class Parse
{       
TokenType token=new TokenType();

int MAXTOKENLEN=10;
int  lineno=0;
String temp_name;
StringTokenizer fenxi;

boolean Error=false;
String serror;
TreeNode yufaTree;

Parse(String s)
{
    yufaTree=Program(s);
}

/********************************************************************/
/********************************************************************/
/********************************************************************/
/* ������ Program					            */
/* ��  �� �ܳ���Ĵ�����					    */
/* ����ʽ < Program > ::= ProgramHead DeclarePart ProgramBody .     */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/*        �﷨���ĸ��ڵ�ĵ�һ���ӽڵ�ָ�����ͷ����ProgramHead,    */
/*        DeclaraPartΪProgramHead���ֵܽڵ�,�����岿��ProgramBody  */
/*        ΪDeclarePart���ֵܽڵ�.                                  */
/********************************************************************/
TreeNode Program(String ss)
{
        fenxi=new StringTokenizer(ss,"\n");
        ReadNextToken();

        TreeNode root = newNode("ProcK");
	TreeNode t=ProgramHead();
	TreeNode q=DeclarePart();
	TreeNode s=ProgramBody();		
       
	if (t!=null) 
            root.child[0] = t;
	else 
            syntaxError("a program head is expected!");
	if (q!=null) 
            root.child[1] = q;
	if (s!=null) 
            root.child[2] = s;
	else syntaxError("a program body is expected!");

	match("DOT");
        if (!(token.Lex.equals("ENDFILE")))
	    syntaxError("Code ends before file\n");

	return root;
}

/**************************����ͷ����********************************/
/********************************************************************/
/********************************************************************/
/* ������ ProgramHead						    */
/* ��  �� ����ͷ�Ĵ�����					    */
/* ����ʽ < ProgramHead > ::= PROGRAM  ProgramName                  */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ProgramHead()
{
    TreeNode t = newNode("PheadK");
    match("PROGRAM");
    if (token.Lex.equals("ID"))
        t.name[0]=token.Sem;
    match("ID");
    return t;
}	
    
/**************************��������**********************************/
/********************************************************************/	
/********************************************************************/
/* ������ DeclarePart						    */
/* ��  �� �������ֵĴ���					    */
/* ����ʽ < DeclarePart > ::= TypeDec  VarDec  ProcDec              */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode DeclarePart()
{
    /*����*/
    TreeNode typeP = newNode("TypeK");    	 
    TreeNode tp1 = TypeDec();
    if (tp1!=null)
        typeP.child[0] = tp1;
    else
	typeP=null;

    /*����*/
    TreeNode varP = newNode("VarK");
    TreeNode tp2 = VarDec();
    if (tp2 != null)
        varP.child[0] = tp2;
    else 
        varP=null;
		 
    /*����*/
    TreeNode procP = ProcDec();
    if (procP==null)  {}
    if (varP==null)   { varP=procP; }	 
    if (typeP==null)  { typeP=varP; }
    if (typeP!=varP)
	typeP.sibling = varP;
    if (varP!=procP)
        varP.sibling = procP;
    return typeP;
}

/**************************������������******************************/
/********************************************************************/
/* ������ TypeDec					            */
/* ��  �� �����������ֵĴ���    				    */
/* ����ʽ < TypeDec > ::= �� | TypeDeclaration                      */
/* ˵  �� �����ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�      */
/********************************************************************/
TreeNode TypeDec()
{
    TreeNode t = null;
    if (token.Lex.equals("TYPE"))
        t = TypeDeclaration();
    else if ((token.Lex.equals("VAR"))||(token.Lex.equals("PROCEDURE"))      
            ||(token.Lex.equals("BEGIN"))) {}
         else      
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* ������ TypeDeclaration					    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < TypeDeclaration > ::= TYPE  TypeDecList                 */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode TypeDeclaration()
{
    match("TYPE");
    TreeNode t = TypeDecList();
    if (t==null)
        syntaxError("a type declaration is expected!");
    return t;
}

/********************************************************************/
/* ������ TypeDecList		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < TypeDecList > ::= TypeId = TypeName ; TypeDecMore       */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode TypeDecList()
{
    TreeNode t = newNode("DecK");
    if (t != null)
    {
	TypeId(t);                               
	match("EQ");  
	TypeName(t); 
	match("SEMI");                           
        TreeNode p = TypeDecMore();
	if (p!=null)
	    t.sibling = p;
    }
    return t;
}

/********************************************************************/
/* ������ TypeDecMore		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < TypeDecMore > ::=    �� | TypeDecList                   */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode TypeDecMore()
{
    TreeNode t=null;
    if (token.Lex.equals("ID"))
        t = TypeDecList();
    else if ((token.Lex.equals("VAR"))||(token.Lex.equals("PROCEDURE"))                       ||(token.Lex.equals("BEGIN"))) {}       
         else
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* ������ TypeId		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < TypeId > ::= id                                         */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void TypeId(TreeNode t)
{
    if ((token.Lex.equals("ID"))&&(t!=null))
    {
	t.name[(t.idnum)]=token.Sem;
	t.idnum = t.idnum+1;
    }
    match("ID");
}

/********************************************************************/
/* ������ TypeName		 				    */
/* ��  �� �����������ֵĴ���				            */
/* ����ʽ < TypeName > ::= BaseType | StructureType | id            */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void TypeName(TreeNode t)
{
   if (t !=null)
   {
      if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR")))    
          BaseType(t);
      else if ((token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD")))   
              StructureType(t);
      else if (token.Lex.equals("ID")) 
           {
                 t.kind = "IdK";
	         t.attr.type_name = token.Sem;    
                 match("ID");  
           }
	   else
	       ReadNextToken();
   }
}
/********************************************************************/
/* ������ BaseType		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < BaseType > ::=  INTEGER | CHAR                          */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void BaseType(TreeNode t)
{
       if (token.Lex.equals("INTEGER"))
       { 
             match("INTEGER");
             t.kind = "IntegerK";
       }
       else if (token.Lex.equals("CHAR"))     
             {
                 match("CHAR");
                 t.kind = "CharK";
             }
             else
                ReadNextToken();   
}

/********************************************************************/
/* ������ StructureType		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < StructureType > ::=  ArrayType | RecType                */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void StructureType(TreeNode t)
{
       if (token.Lex.equals("ARRAY"))
       {
           ArrayType(t); 
       }          
       else if (token.Lex.equals("RECORD"))     
            {
                 t.kind = "RecordK";
                 RecType(t);
            }
            else
                ReadNextToken();   
}
/********************************************************************/
/* ������ ArrayType                                                 */
/* ��  �� �����������ֵĴ�����			            */
/* ����ʽ < ArrayType > ::=  ARRAY [low..top] OF BaseType           */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void ArrayType(TreeNode t)
{
     t.attr.arrayAttr = new ArrayAttr();
     match("ARRAY");
     match("LMIDPAREN");
     if (token.Lex.equals("INTC"))
	 t.attr.arrayAttr.low = Integer.parseInt(token.Sem);
     match("INTC");
     match("UNDERANGE");
     if (token.Lex.equals("INTC"))
	 t.attr.arrayAttr.up = Integer.parseInt(token.Sem);
     match("INTC");
     match("RMIDPAREN");
     match("OF");
     BaseType(t);
     t.attr.arrayAttr.childtype = t.kind;
     t.kind = "ArrayK";
}

/********************************************************************/
/* ������ RecType		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < RecType > ::=  RECORD FieldDecList END                  */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void RecType(TreeNode t)
{
    TreeNode p = null;
    match("RECORD");
    p = FieldDecList();
    if (p!=null)
        t.child[0] = p;
    else
        syntaxError("a record body is requested!");         
    match("END");
}
/********************************************************************/
/* ������ FieldDecList		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < FieldDecList > ::=   BaseType IdList ; FieldDecMore     */
/*                             | ArrayType IdList; FieldDecMore     */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode  FieldDecList()
{
    TreeNode t = newNode("DecK");
    TreeNode p = null;
    if (t != null)
    {
        if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR")))
        {
                    BaseType(t);
	            IdList(t);
	            match("SEMI");
	            p = FieldDecMore();
        }
	else if (token.Lex.equals("ARRAY")) 
             {
	            ArrayType(t);
	            IdList(t);
	            match("SEMI");
	            p = FieldDecMore();
             }
             else
             {
		    ReadNextToken();
		    syntaxError("type name is expected");
             }
        t.sibling = p;
    }	    
    return t;	
}
/********************************************************************/
/* ������ FieldDecMore		 				    */
/* ��  �� �����������ֵĴ�����			            */
/* ����ʽ < FieldDecMore > ::=  �� | FieldDecList                   */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode FieldDecMore()
{
    TreeNode t = null;   
    if (token.Lex.equals("INTEGER")||token.Lex.equals("CHAR")                          ||token.Lex.equals("ARRAY"))
	t = FieldDecList();
    else if (token.Lex.equals("END")) {}
	 else
             ReadNextToken();
    return t;	
}
/********************************************************************/
/* ������ IdList		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < IdList > ::=  id  IdMore                                */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void IdList(TreeNode  t)
{
    if (token.Lex.equals("ID"))
    {
	t.name[(t.idnum)] = token.Sem;
	t.idnum = t.idnum + 1;
        match("ID");
    }
    IdMore(t);
}

/********************************************************************/
/* ������ IdMore		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < IdMore > ::=  �� |  , IdList                            */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void IdMore(TreeNode t)
{
    if (token.Lex.equals("COMMA"))
    {
        match("COMMA");
        IdList(t);
    }
    else if (token.Lex.equals("SEMI")) {}
         else
	     ReadNextToken();	
}

/**************************������������******************************/
/********************************************************************/
/* ������ VarDec		 				    */
/* ��  �� �����������ֵĴ���				            */
/* ����ʽ < VarDec > ::=  �� |  VarDeclaration                      */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode VarDec()
{
    TreeNode t = null;
    if (token.Lex.equals("VAR"))
        t = VarDeclaration();
    else if ((token.Lex.equals("PROCEDURE"))||(token.Lex.equals("BEGIN")))                    {}
	 else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* ������ VarDeclaration		 			    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < VarDeclaration > ::=  VAR  VarDecList                   */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode VarDeclaration()
{
    match("VAR");
    TreeNode t = VarDecList();
    if (t==null)
	syntaxError("a var declaration is expected!");
    return t;
}

/********************************************************************/
/* ������ VarDecList		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < VarDecList > ::=  TypeName VarIdList; VarDecMore        */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode VarDecList()
{
    TreeNode t = newNode("DecK");
    TreeNode p = null;
    if (t != null)
    {
	TypeName(t);
	VarIdList(t);
	match("SEMI");
        p = VarDecMore();
	t.sibling = p;
    }
    return t;
}

/********************************************************************/
/* ������ VarDecMore		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < VarDecMore > ::=  �� |  VarDecList                      */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode VarDecMore()
{
    TreeNode t =null;
    if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR"))                        ||(token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD"))                       ||(token.Lex.equals("ID")))
	t = VarDecList();
    else if ((token.Lex.equals("PROCEDURE"))||(token.Lex.equals("BEGIN")))
	     {}
	 else
             ReadNextToken();
    return t;
}

/********************************************************************/
/* ������ VarIdList		 				    */
/* ��  �� �����������ֵĴ�����			            */
/* ����ʽ < VarIdList > ::=  id  VarIdMore                          */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void VarIdList(TreeNode t)
{
    if (token.Lex.equals("ID"))
    {
        t.name[(t.idnum)] = token.Sem;
	t.idnum = t.idnum + 1;
        match("ID");
    }
    else 
    {
	syntaxError("a varid is expected here!");
	ReadNextToken();
    }
    VarIdMore(t);
}

/********************************************************************/
/* ������ VarIdMore		 				    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < VarIdMore > ::=  �� |  , VarIdList                      */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void VarIdMore(TreeNode t)
{
    if (token.Lex.equals("COMMA"))
    {   
        match("COMMA");
        VarIdList(t);
    }
    else if (token.Lex.equals("SEMI"))  {}
         else
             ReadNextToken();	
}
/****************************������������****************************/
/********************************************************************/
/* ������ ProcDec		 		                    */
/* ��  �� �����������ֵĴ���					    */
/* ����ʽ < ProcDec > ::=  �� |  ProcDeclaration                    */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ProcDec()
{
    TreeNode t = null;
    if (token.Lex.equals("PROCEDURE"))
        t = ProcDeclaration();
    else if (token.Lex.equals("BEGIN")) {}
         else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* ������ ProcDeclaration		 			    */
/* ��  �� �����������ֵĴ�����				    */
/* ����ʽ < ProcDeclaration > ::=  PROCEDURE ProcName(ParamList);   */
/*                                 ProcDecPart                      */
/*                                 ProcBody                         */
/*                                 ProcDecMore                      *
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ProcDeclaration()
{
    TreeNode t = newNode("ProcDecK");
    match("PROCEDURE");
    if (token.Lex.equals("ID"))
    {
        t.name[0] = token.Sem;
	t.idnum = t.idnum+1;
	match("ID");
    }
    match("LPAREN");
    ParamList(t);
    match("RPAREN");
    match("SEMI");
    t.child[1] = ProcDecPart();
    t.child[2] = ProcBody();
    t.sibling = ProcDecMore();
    return t;
}
/********************************************************************/
/* ������ ProcDecMore    				            */
/* ��  �� ���ຯ�������д�����        	        	    */
/* ����ʽ < ProcDecMore > ::=  �� |  ProcDeclaration                */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ProcDecMore()
{
    TreeNode t = null;
    if (token.Lex.equals("PROCEDURE"))
        t = ProcDeclaration();
    else if (token.Lex.equals("BEGIN"))  {}
	 else
             ReadNextToken();
    return t;
}
/********************************************************************/
/* ������ ParamList		 				    */
/* ��  �� ���������в����������ֵĴ�����	        	    */
/* ����ʽ < ParamList > ::=  �� |  ParamDecList                     */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void ParamList(TreeNode t)     
{
    TreeNode p = null;
    if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR"))||                      (token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD"))||                       (token.Lex.equals("ID"))||(token.Lex.equals("VAR")))
    {
        p = ParamDecList();
        t.child[0] = p;
    } 
    else if (token.Lex.equals("RPAREN")) {} 
	 else
	     ReadNextToken();
}

/********************************************************************/
/* ������ ParamDecList		 			    	    */
/* ��  �� ���������в����������ֵĴ�����	        	    */
/* ����ʽ < ParamDecList > ::=  Param  ParamMore                    */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ParamDecList()
{
    TreeNode t = Param();
    TreeNode p = ParamMore();
    if (p!=null)
        t.sibling = p;
    return t;
}

/********************************************************************/
/* ������ ParamMore		 			    	    */
/* ��  �� ���������в����������ֵĴ�����	        	    */
/* ����ʽ < ParamMore > ::=  �� | ; ParamDecList                    */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ParamMore()
{
    TreeNode t = null;
    if (token.Lex.equals("SEMI"))
    {
        match("SEMI");
        t = ParamDecList();
	if (t==null)
           syntaxError("a param declaration is request!");
    }
    else if (token.Lex.equals("RPAREN"))  {} 
	 else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* ������ Param		 			    	            */
/* ��  �� ���������в����������ֵĴ�����	        	    */
/* ����ʽ < Param > ::=  TypeName FormList | VAR TypeName FormList  */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode Param()
{
    TreeNode t = newNode("DecK");
    if ((token.Lex.equals("INTEGER"))||(token.Lex.equals("CHAR"))                        ||(token.Lex.equals("ARRAY"))||(token.Lex.equals("RECORD"))
       || (token.Lex.equals("ID")))
    {
         t.attr.procAttr = new ProcAttr();
         t.attr.procAttr.paramt = "valparamType";
	 TypeName(t);
	 FormList(t);
    }
    else if (token.Lex.equals("VAR"))
         {
             match("VAR");
             t.attr.procAttr = new ProcAttr();
             t.attr.procAttr.paramt = "varparamType";
	     TypeName(t);
	     FormList(t);
	 }
         else
             ReadNextToken();
    return t;
}

/********************************************************************/
/* ������ FormList		 			    	    */
/* ��  �� ���������в����������ֵĴ�����	        	    */
/* ����ʽ < FormList > ::=  id  FidMore                             */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void FormList(TreeNode t)
{
    if (token.Lex.equals("ID"))
    {
	t.name[(t.idnum)] = token.Sem;
	t.idnum = t.idnum + 1;
	match("ID");
    }
    FidMore(t);   
}

/********************************************************************/
/* ������ FidMore		 			    	    */
/* ��  �� ���������в����������ֵĴ�����	        	    */
/* ����ʽ < FidMore > ::=   �� |  , FormList                        */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
void FidMore(TreeNode t)
{      
    if (token.Lex.equals("COMMA"))
    {
        match("COMMA");
	FormList(t);
    }
    else if ((token.Lex.equals("SEMI"))||(token.Lex.equals("RPAREN")))  
             {}
         else
	     ReadNextToken();	  
}
/********************************************************************/
/* ������ ProcDecPart		 			  	    */
/* ��  �� �����е��������ֵĴ�����	             	            */
/* ����ʽ < ProcDecPart > ::=  DeclarePart                          */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ProcDecPart()
{
    TreeNode t = DeclarePart();
    return t;
}

/********************************************************************/
/* ������ ProcBody		 			  	    */
/* ��  �� �����岿�ֵĴ�����	                    	            */
/* ����ʽ < ProcBody > ::=  ProgramBody                             */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ProcBody()
{
    TreeNode t = ProgramBody();
    if (t==null)
	syntaxError("a program body is requested!");
    return t;
}

/****************************�����岿��******************************/
/********************************************************************/
/********************************************************************/
/* ������ ProgramBody		 			  	    */
/* ��  �� �����岿�ֵĴ���	                    	            */
/* ����ʽ < ProgramBody > ::=  BEGIN  StmList   END                 */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ProgramBody()
{
    TreeNode t = newNode("StmLK");
    match("BEGIN");
    t.child[0] = StmList();
    match("END");
    return t;
}

/********************************************************************/
/* ������ StmList		 			  	    */
/* ��  �� ��䲿�ֵĴ�����	                    	            */
/* ����ʽ < StmList > ::=  Stm    StmMore                           */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode StmList()
{
    TreeNode t = Stm();
    TreeNode p = StmMore();
    if (t!=null)
    {
       if (p!=null)
	   t.sibling = p;
    }
    return t;
}

/********************************************************************/
/* ������ StmMore		 			  	    */
/* ��  �� ��䲿�ֵĴ�����	                    	            */
/* ����ʽ < StmMore > ::=   �� |  ; StmList                         */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode StmMore()
{
    TreeNode t = null;
    if ((token.Lex.equals("ELSE"))||(token.Lex.equals("FI"))||                        (token.Lex.equals("END"))||(token.Lex.equals("ENDWH"))) {}
    else if (token.Lex.equals("SEMI"))
	 {
             match("SEMI");
	     t = StmList();
	 }
	 else
	     ReadNextToken();
    return t;
}
/********************************************************************/
/* ������ Stm   		 			  	    */
/* ��  �� ��䲿�ֵĴ�����	                    	            */
/* ����ʽ < Stm > ::=   ConditionalStm   {IF}                       */
/*                    | LoopStm          {WHILE}                    */
/*                    | InputStm         {READ}                     */
/*                    | OutputStm        {WRITE}                    */
/*                    | ReturnStm        {RETURN}                   */
/*                    | id  AssCall      {id}                       */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode Stm()
{
    TreeNode t = null;
    if (token.Lex.equals("IF"))
        t = ConditionalStm();
    else if (token.Lex.equals("WHILE"))         
	     t = LoopStm();
    else if (token.Lex.equals("READ"))  
	     t = InputStm();	 
    else if (token.Lex.equals("WRITE"))   
	     t = OutputStm();	 
    else if (token.Lex.equals("RETURN"))  
	     t = ReturnStm();	 
    else if (token.Lex.equals("ID"))
         {
             temp_name = token.Sem;
	     match("ID");              
             t = AssCall();
         }
	 else
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* ������ AssCall		 			  	    */
/* ��  �� ��䲿�ֵĴ�����	                    	            */
/* ����ʽ < AssCall > ::=   AssignmentRest   {:=,LMIDPAREN,DOT}     */
/*                        | CallStmRest      {(}                    */  
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode AssCall()
{
    TreeNode t = null;
    if ((token.Lex.equals("ASSIGN"))||(token.Lex.equals("LMIDPAREN"))||                  (token.Lex.equals("DOT")))
	t = AssignmentRest();
    else if (token.Lex.equals("LPAREN"))
	     t = CallStmRest();
	 else 
	     ReadNextToken();
    return t;
}

/********************************************************************/
/* ������ AssignmentRest		 			    */
/* ��  �� ��ֵ��䲿�ֵĴ�����	                    	    */
/* ����ʽ < AssignmentRest > ::=  VariMore : = Exp                  */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode AssignmentRest()
{
    TreeNode t = newStmtNode("AssignK");
	
    /* ��ֵ���ڵ�ĵ�һ�����ӽڵ��¼��ֵ��������������
    /* �ڶ������ӽ���¼��ֵ�����Ҳ���ʽ*/

    /*�����һ�����ӽ�㣬Ϊ�������ʽ���ͽڵ�*/
    TreeNode c = newExpNode("VariK");
    c.name[0] = temp_name;
    c.idnum = c.idnum+1;
    VariMore(c);
    t.child[0] = c;
		
    match("ASSIGN");
	  
    /*����ڶ������ӽڵ�*/
    t.child[1] = Exp(); 
				
    return t;
}

/********************************************************************/
/* ������ ConditionalStm		 			    */
/* ��  �� ������䲿�ֵĴ�����	                    	    */
/* ����ʽ <ConditionalStm>::=IF RelExp THEN StmList ELSE StmList FI */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ConditionalStm()
{
    TreeNode t = newStmtNode("IfK");
    match("IF");
    t.child[0] = Exp();
    match("THEN");
    if (t!=null)  
        t.child[1] = StmList();
    if(token.Lex.equals("ELSE"))
    {
	match("ELSE");   
	t.child[2] = StmList();
    }
    match("FI");
    return t;
}

/********************************************************************/
/* ������ LoopStm          		 			    */
/* ��  �� ѭ����䲿�ֵĴ�����	                    	    */
/* ����ʽ < LoopStm > ::=   WHILE RelExp DO StmList ENDWH           */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode LoopStm()
{
    TreeNode t = newStmtNode("WhileK");
    match("WHILE");
    t.child[0] = Exp();
    match("DO");
    t.child[1] = StmList();
    match("ENDWH");
    return t;
}

/********************************************************************/
/* ������ InputStm          		     	                    */
/* ��  �� ������䲿�ֵĴ�����	                    	    */
/* ����ʽ < InputStm > ::=  READ(id)                                */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode InputStm()
{
    TreeNode t = newStmtNode("ReadK");
    match("READ");
    match("LPAREN");
    if (token.Lex.equals("ID"))	
    {
	t.name[0] = token.Sem;
        t.idnum = t.idnum+1;
    }
    match("ID");
    match("RPAREN");
    return t;
}

/********************************************************************/
/* ������ OutputStm          		     	                    */
/* ��  �� �����䲿�ֵĴ�����	                    	    */
/* ����ʽ < OutputStm > ::=   WRITE(Exp)                            */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode OutputStm()
{
    TreeNode t = newStmtNode("WriteK");
    match("WRITE");
    match("LPAREN");
    t.child[0] = Exp();
    match("RPAREN");
    return t;
}

/********************************************************************/
/* ������ ReturnStm          		     	                    */
/* ��  �� ������䲿�ֵĴ�����	                    	    */
/* ����ʽ < ReturnStm > ::=   RETURN(Exp)                           */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ReturnStm()
{
    TreeNode t = newStmtNode("ReturnK");
    match("RETURN");
    return t;
}

/********************************************************************/
/* ������ CallStmRest          		     	                    */
/* ��  �� ����������䲿�ֵĴ�����	                  	    */
/* ����ʽ < CallStmRest > ::=  (ActParamList)                       */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode CallStmRest()
{
    TreeNode t=newStmtNode("CallK");
    match("LPAREN");
    /*��������ʱ�����ӽڵ�ָ��ʵ��*/
    /*�������Ľ��Ҳ�ñ��ʽ���ͽ��*/
    TreeNode c = newExpNode("VariK"); 
    c.name[0] = temp_name;
    c.idnum = c.idnum+1;
    t.child[0] = c;
    t.child[1] = ActParamList();
    match("RPAREN");
    return t;
}

/********************************************************************/
/* ������ ActParamList          		   	            */
/* ��  �� ��������ʵ�β��ֵĴ�����	                	    */
/* ����ʽ < ActParamList > ::=     �� |  Exp ActParamMore           */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ActParamList()
{
    TreeNode t = null;
    if (token.Lex.equals("RPAREN"))  {}
    else if ((token.Lex.equals("ID"))||(token.Lex.equals("INTC")))
	 {
	     t = Exp();
             if (t!=null)
	         t.sibling = ActParamMore();
         }
	 else
             ReadNextToken();
    return t;
}

/********************************************************************/
/* ������ ActParamMore          		   	            */
/* ��  �� ��������ʵ�β��ֵĴ�����	                	    */
/* ����ʽ < ActParamMore > ::=     �� |  , ActParamList             */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�  */
/********************************************************************/
TreeNode ActParamMore()
{
    TreeNode t = null;
    if (token.Lex.equals("RPAREN"))  {}
    else if (token.Lex.equals("COMMA"))
         {
	     match("COMMA");
	     t = ActParamList();
	 }
	 else	
	     ReadNextToken();
    return t;
}

/*************************���ʽ����********************************/
/*******************************************************************/
/* ������ Exp							   */
/* ��  �� ���ʽ������					   */
/* ����ʽ Exp ::= simple_exp | ��ϵ�����  simple_exp              */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ� */
/*******************************************************************/
TreeNode Exp()
{
    TreeNode t = simple_exp();

    /* ��ǰ����tokenΪ�߼����㵥��LT����EQ */
    if ((token.Lex.equals("LT"))||(token.Lex.equals("EQ"))) 
    {
        TreeNode p = newExpNode("OpK");

	/* ����ǰ����token(ΪEQ����LT)�����﷨���ڵ�p���������Աattr.op*/
	p.child[0] = t;
        p.attr.expAttr.op = token.Lex;
        t = p;
 
        /* ��ǰ����token��ָ���߼����������(ΪEQ����LT)ƥ�� */ 
        match(token.Lex);

        /* �﷨���ڵ�t�ǿ�,���ü򵥱��ʽ������simple_exp()	   
           ���������﷨���ڵ��t�ĵڶ��ӽڵ��Աchild[1]  */ 
        if (t!=null)
            t.child[1] = simple_exp();
    }
    return t;
}

/*******************************************************************/
/* ������ simple_exp						   */
/* ��  �� ���ʽ����						   */
/* ����ʽ simple_exp ::=   term  |  �ӷ������  term               */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ� */
/*******************************************************************/
TreeNode simple_exp()
{
    TreeNode t = term();

    /* ��ǰ����tokenΪ�ӷ����������PLUS��MINUS */
    while ((token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS")))
    {
	TreeNode p = newExpNode("OpK");
	p.child[0] = t;
        p.attr.expAttr.op = token.Lex;
        t = p;

        match(token.Lex);

	/* ����Ԫ������term(),���������﷨���ڵ��t�ĵڶ��ӽڵ��Աchild[1] */
        t.child[1] = term();
    }
    return t;
}

/********************************************************************/
/* ������ term						            */
/* ��  �� �����						    */
/* ����ʽ < �� > ::=  factor | �˷������  factor		    */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ� */
/********************************************************************/
TreeNode term()
{
    TreeNode t = factor();

    /* ��ǰ����tokenΪ�˷����������TIMES��OVER */
    while ((token.Lex.equals("TIMES"))||(token.Lex.equals("OVER")))
    {
	TreeNode p = newExpNode("OpK");
	p.child[0] = t;
        p.attr.expAttr.op = token.Lex;
        t = p;	
        match(token.Lex);
        p.child[1] = factor();    
    }
    return t;
}

/*********************************************************************/
/* ������ factor						     */
/* ��  �� ���Ӵ�����						     */
/* ����ʽ factor ::= INTC | Variable | ( Exp )                       */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ� */
/*********************************************************************/
TreeNode factor()
{
    TreeNode t = null;
    if (token.Lex.equals("INTC")) 
    {
        t = newExpNode("ConstK");

	/* ����ǰ������tokenStringת��Ϊ��������t����ֵ��Աattr.val */
        t.attr.expAttr.val = Integer.parseInt(token.Sem);
        match("INTC");
    }
    else if (token.Lex.equals("ID"))  	  
	     t = Variable();
    else if (token.Lex.equals("LPAREN")) 
	 {
             match("LPAREN");					
             t = Exp();
             match("RPAREN");					
         }
         else 			
             ReadNextToken();	  
    return t;
}


/********************************************************************/
/* ������ Variable						    */
/* ��  �� ����������						    */
/* ����ʽ Variable   ::=   id VariMore                   	    */
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ� */
/********************************************************************/
TreeNode Variable()
{
    TreeNode t = newExpNode("VariK");
    if (token.Lex.equals("ID"))
    {
	t.name[0] = token.Sem;
        t.idnum = t.idnum+1;
    }
    match("ID");
    VariMore(t);
    return t;
}

/********************************************************************/
/* ������ VariMore						    */
/* ��  �� ��������						    */
/* ����ʽ VariMore   ::=  ��                             	    */
/*                       | [Exp]            {[}                     */
/*                       | . FieldVar       {DOT}                   */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ� */
/********************************************************************/	
void VariMore(TreeNode t)
{
        if ((token.Lex.equals("EQ"))||(token.Lex.equals("LT"))||                       (token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS"))||                           (token.Lex.equals("RPAREN"))||(token.Lex.equals("RMIDPAREN"))||                     (token.Lex.equals("SEMI"))||(token.Lex.equals("COMMA"))|| 
           (token.Lex.equals("THEN"))||(token.Lex.equals("ELSE"))||                       (token.Lex.equals("FI"))||(token.Lex.equals("DO"))||(token.Lex.equals           ("ENDWH"))||(token.Lex.equals("END"))||(token.Lex.equals("ASSIGN"))||           (token.Lex.equals("TIMES"))||(token.Lex.equals("OVER")))    {}
	else if (token.Lex.equals("LMIDPAREN"))
             {
	         match("LMIDPAREN");
	         t.child[0] = Exp();
                 t.attr.expAttr.varkind = "ArrayMembV";
	         match("RMIDPAREN");
	     }
	else if (token.Lex.equals("DOT"))
             {
	         match("DOT");
	         t.child[0] = FieldVar();
                 t.attr.expAttr.varkind = "FieldMembV";
	     }
	     else
	         ReadNextToken();
}
/********************************************************************/
/* ������ FieldVar						    */
/* ��  �� ����������				                    */
/* ����ʽ FieldVar   ::=  id  FieldVarMore                          */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ� */
/********************************************************************/
TreeNode FieldVar()
{
    TreeNode t = newExpNode("VariK");
    if (token.Lex.equals("ID"))
    {
	t.name[0] = token.Sem;
        t.idnum = t.idnum+1;
    }	
    match("ID");	
    FieldVarMore(t);
    return t;
}

/********************************************************************/
/* ������ FieldVarMore  			                    */
/* ��  �� ����������                                              */
/* ����ʽ FieldVarMore   ::=  ��| [Exp]            {[}              */ 
/* ˵  �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ� */
/********************************************************************/
void FieldVarMore(TreeNode t)
{
    if ((token.Lex.equals("ASSIGN"))||(token.Lex.equals("TIMES"))||               (token.Lex.equals("EQ"))||(token.Lex.equals("LT"))||                          (token.Lex.equals("PLUS"))||(token.Lex.equals("MINUS"))||                      (token.Lex.equals("OVER"))||(token.Lex.equals("RPAREN"))||               (token.Lex.equals("SEMI"))||(token.Lex.equals("COMMA"))||               (token.Lex.equals("THEN"))||(token.Lex.equals("ELSE"))||               (token.Lex.equals("FI"))||(token.Lex.equals("DO"))||
       (token.Lex.equals("ENDWH"))||(token.Lex.equals("END")))
       {}
    else if (token.Lex.equals("LMIDPAREN"))
         { 
	     match("LMIDPAREN");
	     t.child[0] = Exp();
             t.child[0].attr.expAttr.varkind = "ArrayMembV";
	     match("RMIDPAREN");
	 }
	 else
	     ReadNextToken();
}

/********************************************************************/
/********************************************************************/
/* ������ match							    */
/* ��  �� �ռ���ƥ�䴦����				            */
/* ˵  �� ��������expected�����������ʷ����뵱ǰ���ʷ���token��ƥ�� */
/*        �����ƥ��,�򱨷����������﷨����			    */
/********************************************************************/
void match(String expected)
{ 
      if (token.Lex.equals(expected))   
	  ReadNextToken();
      else 
      {
	  syntaxError("not match error ");
	  ReadNextToken();
      }     
}

/************************************************************/
/* ������ syntaxError                                       */
/* ��  �� �﷨��������		                    */
/* ˵  �� ����������messageָ���Ĵ�����Ϣ���               */	
/************************************************************/
void syntaxError(String s)     /*�������Ϣ.txt��д���ַ���*/
{
    serror=serror+"\n>>> ERROR :"+"Syntax error at "                              +String.valueOf(token.lineshow)+": "+s; 

    /* ���ô���׷�ٱ�־ErrorΪTRUE,��ֹ�����һ������ */
    Error = true;
}

/********************************************************************/
/* ������ ReadNextToken                                             */
/* ��  �� ��Token������ȡ��һ��Token				    */
/* ˵  �� ���ļ��д��Token����������ȡһ�����ʣ���Ϊ��ǰ����       */	
/********************************************************************/ 
void ReadNextToken()
{
    if (fenxi.hasMoreTokens())
    {
        int i=1;
	String stok=fenxi.nextToken();
        StringTokenizer fenxi1=new StringTokenizer(stok,":,");
        while (fenxi1.hasMoreTokens())
        {
            String fstok=fenxi1.nextToken();
            if (i==1)
            {
	        token.lineshow=Integer.parseInt(fstok);
	        lineno=token.lineshow;
            }
            if (i==2)
	        token.Lex=fstok;
            if (i==3)
                token.Sem=fstok;
            i++;
        }
    }    
}
/********************************************************
 *********�����Ǵ����﷨�����õĸ���ڵ������***********
 ********************************************************/
/********************************************************/
/* ������ newNode				        */	
/* ��  �� �����﷨���ڵ㺯��			        */
/* ˵  �� �ú���Ϊ�﷨������һ���µĽ��      	        */
/*        �����﷨���ڵ��Ա����ֵ�� sΪProcK, PheadK,  */
/*        DecK, TypeK, VarK, ProcDecK, StmLK	        */
/********************************************************/
TreeNode newNode(String s)
{
    TreeNode t=new TreeNode();
    t.nodekind = s;
    t.lineno = lineno;
    return t;
}
/********************************************************/
/* ������ newStmtNode					*/	
/* ��  �� ������������﷨���ڵ㺯��			*/
/* ˵  �� �ú���Ϊ�﷨������һ���µ�������ͽ��	*/
/*        �����﷨���ڵ��Ա��ʼ��			*/
/********************************************************/
TreeNode newStmtNode(String s)
{
    TreeNode t=new TreeNode();
    t.nodekind = "StmtK";
    t.lineno = lineno;
    t.kind = s;
    return t;
}
/********************************************************/
/* ������ newExpNode					*/
/* ��  �� ���ʽ�����﷨���ڵ㴴������			*/
/* ˵  �� �ú���Ϊ�﷨������һ���µı��ʽ���ͽ��	*/
/*        �����﷨���ڵ�ĳ�Ա����ֵ			*/
/********************************************************/
TreeNode newExpNode(String s)
{
    TreeNode t=new TreeNode();
    t.nodekind = "ExpK";
    t.kind = s;
    t.lineno = lineno;
    t.attr.expAttr = new ExpAttr();
    t.attr.expAttr.varkind = "IdV";
    t.attr.expAttr.type = "Void";
    return t;
}

}
