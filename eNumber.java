
/*
 * @author (�ֽ���) 
 * @ID 038054132
*/

import java.util.*;
import java.io.*;

class Node //����ڵ�
{  int id[][]; //һ���ڵ�������ľŸ���ֵ��λ�����  
   int p=-1;
   int value; 
   public Node()
   {
      id=new int[3][3];
   }
} 
public class eNumber
{
	int step=0; 
    int index;
    int b;
    Node First=new Node();// �洢��ʼ״̬�ڵ����ֵ˳��
    Node End=new Node();//Ŀ��״̬
    LinkedList closeList=new LinkedList();//�洢�Ѿ���չ�Ľڵ�
    LinkedList openList=new LinkedList();//�洢����չ�Ľڵ�
    LinkedList templeList=new LinkedList();//�����չ�������½ڵ�
//---------------------------------------------------------------------------------  
	public eNumber()
	{
		First.id[0][0]=1;   First.id[0][1]=0;    First.id[0][2]=3;
        First.id[1][0]=7;   First.id[1][1]=2;    First.id[1][2]=4;
        First.id[2][0]=6;   First.id[2][1]=8;    First.id[2][2]=5;
        End.id[0][0]=1;     End.id[0][1]=2;      End.id[0][2]=3;
        End.id[1][0]=8;     End.id[1][1]=0;      End.id[1][2]=4;
        End.id[2][0]=7;     End.id[2][1]=6;      End.id[2][2]=5;
	}

	//-------------------------------------------------------------------------------
	public static void main(String[] args)
	{
		eNumber en=new eNumber();
		System.out.println("�밴����˳������'0-8'����α�ʾ����1 0 3 7 2 4 6 8 5��");
		BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
		String str;
		int n=0;
		char c;
		try
		{
		    //str=input.readLine();
		    for(int i=0;i<3;i++)
		    {
		        for(int j=0;j<3;j++)
		        {
		            //c=str.charAt(n);
		            System.out.print("��"+(n+1)+"����:");
		            str=input.readLine();
		            en.First.id[i][j]=Integer.parseInt(str);
		            //System.out.print(en.First.id[i][j]);
		            n++;
		        }
		     }
		  }
		  catch(IOException e){}
        en.begin(en.First,en.End);  
        en.end(en.closeList);
	}
	//  ------------------------�ж��Ƿ����-----------------------------------------------------
    public boolean isEqual(Node fnode,Node lnode)
    {
        int n=0; 
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                if(fnode.id[i][j]==lnode.id[i][j])
                n=n+1;
            }  
             //System.out.println(n);
        }
            if(n==9) 
            return true;
            else 
            return false;
    }
	//---------------------������-------------------------------------
	public void begin(Node First1,Node End1)   
     {   
       if(!isEqual(First1,End1))
           {
             int len=templeList.size();
             templeList.clear(); //�����ʱ��
             int n=0;
             int i=0,j=0;
             step=step+1;
             openList.remove(First1);
             closeList.addLast(First1);
             int k=0;
             int h=1;
             int gg;
             gg=0;
             l:while(gg==0)
             {
            for( k=0;k<3;k++)
             {
                 for( h=0;h<3;h++)
                 {
                     if(First1.id[k][h]==0)
                     {
                         gg=-1;
                         continue l;
                     }
                 }
             }
            }
             //System.out.print(k+" ");
             //System.out.println(h);
             left(copy(First1),k,h);
             right(copy(First1),k,h);  
             up(copy(First1),k,h);
             down(copy(First1),k,h);
              
             int Oh=openList.size();  
             if(Oh>0)  
             {     
                 Node minnode=new Node();
                 minnode=(Node)openList.getFirst();  
               
                 for(int l=1;l<Oh;l++) 
                 {  
                     Node compnode=new Node();
                     compnode=(Node)openList.get(l);
                     if(minnode.value>=compnode.value)      
                     minnode=compnode; //��open������С�Ľڵ�
                  } 
                  if(templeList.contains(minnode)) First1.p=1;
                  openList.remove(minnode);
                    
                    if(step<10)  
                    begin(minnode,End1);  
                }    
            }
            else  
            {
                First1.p=1;
                closeList.addLast(First1);
            } 
                
        }
  //******************���ƽڵ�****************
  public Node copy(Node nod)
  {   
      Node no=new Node();
      for(int i=0;i<3;i++)
      {
          for(int j=0;j<3;j++)
          {
              no.id[i][j]=nod.id[i][j];
          }
       }
       return no;
   }
   //---------------------------------------------------------------------------------------------------
   public void left(Node node,int x,int y)
   {    
        int n=0,sum=0;
        Node tnode=new Node();
        //int i,j,a,b;
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                tnode.id[i][j]=node.id[i][j];
            }
        }
        if(y!=0)
        {
            node.id[x][y]=node.id[x][y-1];
            node.id[x][y-1]=0;
            if((!closeList.contains(node))&&(!openList.contains(node))&&(!isEqual(tnode,node)))
            {   int y2;
                for(int x1=0;x1<3;x1++) 
                {
                    for(int y1=0;y1<3;y1++)
                    {
                        for(int x2=0;x2<3;x2++)
                        {
                                if(node.id[x1][y1]==End.id[x2][0])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-0));
                                    break;
                                }
                                if(node.id[x1][y1]==End.id[x2][y1])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-1));
                                    break;
                                }
                                if(node.id[x1][y1]==End.id[x2][2])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-2));
                                    break;
                                }
                            
                           
                        }
                    }
                }
                node.value=sum;
                //System.out.println(sum);
                openList.addLast(node);
            } 
            else node=null;
        }
        if(node!=null) templeList.addLast(node);
    }
    //-------------------------------------------------------------------------------------------------------
    public void right(Node node,int x,int y)
   {    
        int n=0,sum=0;
        Node tnode=new Node();
        int i,j,a;
        for(i=0;i<3;i++)
        {
            for(j=0;j<3;j++)
            {
                tnode.id[i][j]=node.id[i][j];
            }
        }
        if(y!=2)
        {
            node.id[x][y]=node.id[x][y+1];
            node.id[x][y+1]=0;
            if((!closeList.contains(node))&&(!openList.contains(node))&&(!isEqual(tnode,node)))
            {   int y2;
                for(int x1=0;x1<3;x1++) 
                {
                    for(int y1=0;y1<3;y1++)
                    {
                        for(int x2=0;x2<3;x2++)
                        {
                           if(node.id[x1][y1]==End.id[x2][0])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-0));
                                    break;
                                }
                                if(node.id[x1][y1]==End.id[x2][y1])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-1));
                                    break;
                                }
                                if(node.id[x1][y1]==End.id[x2][2])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-2));
                                    break;
                                }
                        }
                    }
                }
                node.value=sum;
                //System.out.println(sum);
                openList.addLast(node);
            } 
            else node=null;
        }
        if(node!=null) templeList.addLast(node);
    }
    //-------------------------------------------------------------------------------------------------------
    public void up(Node node,int x,int y)
   {    
        int n=0,sum=0;
        Node tnode=new Node();
        int i,j,a,b;
        for(i=0;i<3;i++)
        {
            for(j=0;j<3;j++)
            {
                tnode.id[i][j]=node.id[i][j];
            }
        }
        if(x!=0)
        {
            node.id[x][y]=node.id[x-1][y];
            node.id[x-1][y]=0;
            if((!closeList.contains(node))&&(!openList.contains(node))&&(!isEqual(tnode,node)))
            {   int y2;
                for(int x1=0;x1<3;x1++) 
                {
                    for(int y1=0;y1<3;y1++)
                    {
                        for(int x2=0;x2<3;x2++)
                        {
                           if(node.id[x1][y1]==End.id[x2][0])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-0));
                                    break;
                                }
                                if(node.id[x1][y1]==End.id[x2][y1])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-1));
                                    break;
                                }
                                if(node.id[x1][y1]==End.id[x2][2])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-2));
                                    break;
                                }
                        }
                    }
                }
                node.value=sum;
                //System.out.println(sum);
                openList.addLast(node);
            } 
            else node=null;
        }
        if(node!=null) templeList.addLast(node);
    }
    //------------------------------------------------------------------------------------------------
   public void down(Node node,int x,int y)
   {    
        int n=0,sum=0;
        Node tnode=new Node();
        int i,j,a,b;
        for(i=0;i<3;i++)
        {
            for(j=0;j<3;j++)
            {
                tnode.id[i][j]=node.id[i][j];
            }
        }
        if(x!=2)
        {
            node.id[x][y]=node.id[x+1][y];
            node.id[x+1][y]=0;
            if((!closeList.contains(node))&&(!openList.contains(node))&&(!isEqual(tnode,node)))
            {   int y2;
                for(int x1=0;x1<3;x1++) 
                {
                    for(int y1=0;y1<3;y1++)
                    {
                        for(int x2=0;x2<3;x2++)
                        {
                           if(node.id[x1][y1]==End.id[x2][0])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-0));
                                    break;
                                }
                                if(node.id[x1][y1]==End.id[x2][y1])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-1));
                                    break;
                                }
                                if(node.id[x1][y1]==End.id[x2][2])
                                {
                                    sum=sum+(Math.abs(x1-x2)+Math.abs(y1-2));
                                    break;
                                }
                        }
                    }
                }
                node.value=sum;
                //System.out.println(sum);
                openList.addLast(node);
            } 
            else node=null;
        }
        if(node!=null) templeList.addLast(node);
    }
    //***************�������*****************************************************
public void  end(LinkedList list)
 {     
     int count=0;
     int ppt=closeList.size();
     for(int v=0;v<ppt;v++)
     {    
         System.out.println();
         Node outnode=(Node)list.get(v); 
         if(outnode.p==1) 
         {   
             count=count+1;
             System.out.println("��"+count+"��:");
             for(int i=0;i<3;i++)
             {
                 for(int j=0;j<3;j++)
                 {
                     System.out.print(outnode.id[i][j]+" ");
                 }
                 System.out.println();
             }
         }
     }
     System.out.println("����ִ�н���....");
  }
}