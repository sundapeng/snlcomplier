package parse;

/**
 * �﷨�����Ķ���
 * TreeNode.java
 * @author ������
 *
 */
public class TreeNode 
{
	TreeNode child[] = new TreeNode[3];			//ָ�����﷨���ڵ�ָ�룬Ϊ�﷨�����ָ������
	TreeNode sibling = null;     				//ָ���ֵ��﷨���ڵ�ָ�룬Ϊ�﷨���ڵ�ָ������			
	int lineno;									//��¼Դ�����к�
	String nodekind;							//��¼�﷨���ڵ�����ͣ�ȡֵΪProK��PheadK,TypeK,VarK,ProcDecK,StmLK,DecK,StmtK,ExpK
	String kind;								//��¼�﷨���ڵ�ľ�������
	int idnum;								    //��¼һ���ڵ��еı�ʶ���ĸ���
	String name[] = new String[10];				//�ַ������飬�����Ա�ǽڵ��еı�ʶ��������
	Attr attr = new Attr();						//��¼�﷨�����������ԣ�Ϊ�ṹ������
}
