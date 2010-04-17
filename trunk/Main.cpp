// Main.cpp: implementation of the CMain class.
// CMain��ʵ���ļ�
// ���ߣ���־��
// ����޸����ڣ�2003��12��8��
////////////////////////////////////////////////

#include "stdafx.h"
#include "AI3.h"
#include "Main.h"
#include "math.h"

//////////////
// ���캯��
//////////////
CMain::CMain()
{

}
//////////////
// ��������
//////////////
CMain::~CMain()
{
	if(this->m_DispList.GetCount() >0) 
		this->m_DispList.RemoveAll();
}

/////////////////////////////////////////
//    LoadData:��������
//    ���ɵ�ǰ����״̬ 
//    ������״̬������������
/////////////////////////////////////////
BOOL CMain::LoadData(BOOL Adv)
{
	if(Adv == false)
	{
	 CInitDialog InitDlg;
	 if(InitDlg.DoModal()==IDOK)
	 {
		CDisplay *DispItem;DispItem = new CDisplay;
		m_CurrentG = 0;
		for(int i=0; i<MaxItem; i++)
		 for(int j=0; j<MaxItem; j++)
		 {
			this->m_Desc[i][j] = InitDlg.GetDescData(i,j);
			this->m_Data[i][j] = InitDlg.GetSrcData(i,j);
			DispItem->LoadData(m_Data[i][j],i,j);
		 }
		 DispItem->SetNoteType(AlReady);
		 DispItem->SetThisIsAAnswer();
		 DispItem->SetCurrentG(m_CurrentG);
		 DispItem->SetCurrentCount(0);
		 m_CurOpItem = DispItem;
		 m_DispList.AddTail(DispItem);
		return TRUE;
	 }
	 else return FALSE;
	}
	else
	{
	 CInputAdvDlg InitDlg;
	 if(InitDlg.DoModal()==IDOK)
	 {
		CDisplay *DispItem;DispItem = new CDisplay;
		m_CurrentG = 0;
		for(int i=0; i<MaxItem; i++)
		 for(int j=0; j<MaxItem; j++)
		 {
			this->m_Desc[i][j] = InitDlg.GetDescData(i,j);
			this->m_Data[i][j] = InitDlg.GetSrcData(i,j);
			DispItem->LoadData(m_Data[i][j],i,j);
		 }
		 DispItem->SetNoteType(AlReady);
		 DispItem->SetThisIsAAnswer();
		 DispItem->SetCurrentG(m_CurrentG);
		 DispItem->SetCurrentCount(0);
		 m_CurOpItem = DispItem;
		 m_DispList.AddTail(DispItem);
		return TRUE;
	 }
	 else return FALSE;
	}
}

////////////////////////////////////
// GenerateMoveFlag : ��ٿհ�λ���ƶ���ʽ
// ͬʱ��¼�������ƶ���ʽ
///////////////////////////////////
void CMain::GenerateMoveFlag()
{
	Position BlankPosition = m_CurOpItem->GetBlankPosition();
	/* ���ﱾ��Ҫ����հ�λ�Ƿ���ڵ��жϣ����ڱ������
	   �б��õ����ݣ��հ�λʼ�մ���
	*/
	m_iMoveFlagCount = 0;
	if(BlankPosition == Position(0,0))
	{//�հ�λ��(0,0)
	 m_iMoveFlag[MoveUp] = FALSE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = FALSE;
	 m_iMoveFlagCount = 2;
	}
	else if(BlankPosition == Position(0,MaxItem-1))
	{//�հ�λ��(0,MaxItem-1)
	 m_iMoveFlag[MoveUp] = FALSE;m_iMoveFlag[MoveRight] = FALSE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 2;
	}
	else if(BlankPosition == Position(MaxItem-1,0))
	{//�հ�λ��(MaxItem-1,0)
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = FALSE;m_iMoveFlag[MoveLeft] = FALSE;
	 m_iMoveFlagCount = 2;
	}
	else if(BlankPosition == Position(MaxItem-1,MaxItem-1))
	{//�հ�λ��(MaxItem-1,MaxItem-1)
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = FALSE;
	 m_iMoveFlag[MoveDown] = FALSE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 2;
	}
	else if(BlankPosition.x>0 && BlankPosition.x<MaxItem-1 && BlankPosition.y==0)
	{//�հ�λ�ڵ�һ�г�(0,0)��(MaxItem-1,0)
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = FALSE;
	 m_iMoveFlagCount = 3;
	}
	else if(BlankPosition.x>0 && BlankPosition.x<MaxItem-1 && BlankPosition.y==MaxItem-1)
	{//�հ�λ�ڵ�MaxItem�г�(0,MaxItem-1)��(MaxItem-1,MaxItem-1)
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = FALSE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 3;
	}
	else if(BlankPosition.y>0 && BlankPosition.y<MaxItem-1 && BlankPosition.x==0)
	{//�հ�λ�ڵ�һ�г�(0,0)��(0,MaxItem-1)
	 m_iMoveFlag[MoveUp] = FALSE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 3;
	}
	else if(BlankPosition.y>0 && BlankPosition.y<MaxItem-1 && BlankPosition.x==MaxItem-1)
	{//�հ�λ�ڵ�MaxItem�� ��(MaxItem-1,0)��(MaxItem-1,MaxItem-1)
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = FALSE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 3;
	}
	else
	{
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 4;
	}
}

//////////////////////////////////
//GenerateChild: �����ӽڵ�
// ����ֵ�������ӽڵ�����еĴ������
//////////////////////////////////
UINT CMain::GenerateChild()
{
	List ChildList;
	int m=0;
	for(int k=0; k<4; k++)
	{
		if(m_iMoveFlag[k] == false) continue;
		CDisplay *Tmp;Tmp = new CDisplay;
		for(int i=0; i<MaxItem; i++)
		 for(int j=0; j<MaxItem; j++)
		  Tmp->LoadData(m_CurOpItem);
		 Tmp->SetCurrentG(m_CurrentG);
		 Tmp->SetCurrentCount(m++);
		 Tmp->SetNoteType(NotYet);
		 Tmp->MoveBlank(k);
		 ChildList.AddTail(Tmp);
	}
	return FindBestMoveFlag(&ChildList);
}

///////////////////////////////////////////
// FindBestMoveFlag:Ѱ������ƶ���ʽ ���ƶ�֮
// ���: GenerateChild();
// ����:�ӽڵ㼯
// ����ֵ:�������
//////////////////////////////////////////
UINT CMain::FindBestMoveFlag(List *pList)
{
	//�����ƶ����Ƿ����ظ��� �ظ���μ����ѡ������
	IsReadyExist(pList);
	//��û�г��ֹ��ƶ������ѡ����ѽ�
	POSITION Pos = pList->GetHeadPosition();
	int H[4]={65535,65535,65535,65535},i=0,Min = 65535;
	POSITION MinPos;
	while(Pos)
	{
	 POSITION CurPos = Pos;
	 CDisplay *Item = (CDisplay *)pList->GetNext(Pos);
	 if(Item->GetNoteType() == AlReady) {i++;continue;}
	 H[i] = CaculateH(Item);
	 if(Min>H[i]) {Min=H[i];MinPos = CurPos;}
	 i++;
	}
	
	if(Min == 65535)
	{/*
	 //û���ҵ���ѷ��� ��ʾ�����������������չ��Ҫ����
	 //���ڱ������� ��������ֵһ���н⣬���Բ�Ҫ����
	 for(int CurrentG = this->m_CurrentG;CurrentG>0;CurrentG--)
	  if(FindOtherNote(CurrentG)) return NoError;//�ҵ�����һ��δ��չ�Ľ�㷵��
	 */
		return ErrorCode;//û��δ��չ�Ľ���� �����޽�
	}	

	//�����ѽ�
	Pos = pList->GetHeadPosition();
	while(Pos)
	{
	 POSITION CurPos = Pos;
	 CDisplay *Item = (CDisplay *)m_DispList.GetNext(Pos);
	 if(CurPos == MinPos) 
	 {
	  Item->SetThisIsAAnswer();//�ǽ�
	  Item->SetNoteType(AlReady);//����չ
	  m_CurOpItem = Item;
	 }
	 m_DispList.AddTail(Item);
	}
	return NoError;
}

//////////////////////////////////////
// IsReadyExist: �Ƿ��Ѿ�����
// ����Ѿ�����������Ϊ�Ѵ���
// ���: FindBestMoveFlag()
// �������ӽڵ㼯��ʼλ��ָ��
//////////////////////////////////////
void CMain::IsReadyExist(List *pList)
{
	POSITION PosSrc = pList->GetHeadPosition();
	while(PosSrc)
	{
	 POSITION CurPos = PosSrc;
	 CDisplay *ItemSrc = (CDisplay *)pList->GetNext(PosSrc);
	 POSITION PosDesc = m_DispList.GetHeadPosition();
	 while(PosDesc)
	 {
	  CDisplay *ItemDesc = (CDisplay *)m_DispList.GetNext(PosDesc);
	  if(ItemSrc->IsEqual(ItemDesc))
	  {//�Ѿ������ڵ�i�������
	   ItemSrc->SetNoteType(Cannot);
	   m_DispList.AddTail(ItemSrc);
	   pList->RemoveAt(CurPos);
	   break;
	  }
	 }
	}
}

////////////////////////////////////
// CaculateH:���㵱ǰHֵ
// ����:��ǰ�ڵ�
// ����ֵ:Hֵ
////////////////////////////////////
int CMain::CaculateH(CDisplay *Item)
{
	DataType Src[MaxItem][MaxItem];
	for(int i=0; i<MaxItem; i++)
	 for(int j=0; j<MaxItem; j++)
	  Src[i][j] = Item->GetDispData(i,j);
	int Hop = 0;
	for(i=0; i<MaxItem; i++)
	 for(int j=0; j<MaxItem; j++)
	 {
		if(Src[i][j] == m_Desc[i][j]) continue;
		if(Src[i][j] == 0 ) continue;
		else
		{
			int Hhop = Scan(Src[i][j], Position(i,j));
			if(Hhop == 65535) return 65535;
			Hop += Hhop;
		}
	 }
	return Hop;
}

//////////////////////////////////////////////
// Scan: ɨ�� ����Hֵ�ĸ�������
// ���:CaculateH()
// ����:i��λ��,��i��ֵ
// ����ֵ:H(i)��ֵ
//////////////////////////////////////////////
int CMain::Scan(DataType Desc,Position Srcpos)
{
	Position Descpos;
	for(int i=0;i<MaxItem;i++)
	 for(int j=0;j<MaxItem;j++)
	 {
		 if(this->m_Desc[i][j] == Desc) 
		 {
			Descpos = Position(i,j);
			return (int)fabs(Descpos.x - Srcpos.x)
				+(int)fabs(Descpos.y-Srcpos.y);
		 }
	 }
	 return 65535;
}

///////////////////////////////////////
// FindOtherNote: ���ڻ��� Ѱ����һ���ڵ� 
// ����:��ǰGֵ
// ����ֵ:�Ƿ��ҵ��½ڵ�
///////////////////////////////////////
BOOL CMain::FindOtherNote(int CurrentG)
{
	POSITION pos = m_DispList.GetTailPosition();
	while(pos)
	{
		CDisplay *Item = (CDisplay *)m_DispList.GetPrev(pos);
		if(Item->GetCurrentG() != CurrentG) continue;//û�е���ǰ��
		if(Item->GetNoteType() != NotYet) continue;//��ǰ��㲻��û�б���չ�Ľ��
		m_CurOpItem = Item;
		return TRUE;
	}
	return FALSE;
}

///////////////////////////////////////////
// GetResultListPoint: �õ�������������ָ��
// ////////////////////////////////////////
List * CMain::GetResultListPoint()
{
	while(1)
	{
		if(m_DispList.GetCount() >= MaxNote) 
		{
			HWND hWnd = AfxGetApp()->GetMainWnd()->GetSafeHwnd();
			PostMessage(hWnd,WM_ERROR,0,ErrorCode1);
			return &m_DispList;
		}
		if(CaculateH(m_CurOpItem) == 0) 
		{
			HWND hWnd = ::AfxGetApp()->GetMainWnd()->GetSafeHwnd();
			PostMessage(hWnd,WM_ERROR,0,NoError);
			return &m_DispList;
		}
		GenerateMoveFlag();
		m_CurrentG++;
		if(GenerateChild() == NoError) continue;
		HWND hWnd = ::AfxGetApp()->GetMainWnd()->GetSafeHwnd();
		PostMessage(hWnd,WM_ERROR,0,ErrorCode);
		break;
	}
	return NULL;
}
