// Main.cpp: implementation of the CMain class.
// CMain类实现文件
// 作者：刘志良
// 最后修改日期：2003年12月8日
////////////////////////////////////////////////

#include "stdafx.h"
#include "AI3.h"
#include "Main.h"
#include "math.h"

//////////////
// 构造函数
//////////////
CMain::CMain()
{

}
//////////////
// 析构函数
//////////////
CMain::~CMain()
{
	if(this->m_DispList.GetCount() >0) 
		this->m_DispList.RemoveAll();
}

/////////////////////////////////////////
//    LoadData:载入数据
//    生成当前操作状态 
//    并将当状态加入搜索树中
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
// GenerateMoveFlag : 穷举空白位的移动方式
// 同时记录共几种移动方式
///////////////////////////////////
void CMain::GenerateMoveFlag()
{
	Position BlankPosition = m_CurOpItem->GetBlankPosition();
	/* 这里本来要加入空白位是否存在的判断，但在本程序的
	   中便用的数据，空白位始终存在
	*/
	m_iMoveFlagCount = 0;
	if(BlankPosition == Position(0,0))
	{//空白位在(0,0)
	 m_iMoveFlag[MoveUp] = FALSE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = FALSE;
	 m_iMoveFlagCount = 2;
	}
	else if(BlankPosition == Position(0,MaxItem-1))
	{//空白位在(0,MaxItem-1)
	 m_iMoveFlag[MoveUp] = FALSE;m_iMoveFlag[MoveRight] = FALSE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 2;
	}
	else if(BlankPosition == Position(MaxItem-1,0))
	{//空白位在(MaxItem-1,0)
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = FALSE;m_iMoveFlag[MoveLeft] = FALSE;
	 m_iMoveFlagCount = 2;
	}
	else if(BlankPosition == Position(MaxItem-1,MaxItem-1))
	{//空白位在(MaxItem-1,MaxItem-1)
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = FALSE;
	 m_iMoveFlag[MoveDown] = FALSE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 2;
	}
	else if(BlankPosition.x>0 && BlankPosition.x<MaxItem-1 && BlankPosition.y==0)
	{//空白位在第一列除(0,0)与(MaxItem-1,0)
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = FALSE;
	 m_iMoveFlagCount = 3;
	}
	else if(BlankPosition.x>0 && BlankPosition.x<MaxItem-1 && BlankPosition.y==MaxItem-1)
	{//空白位在第MaxItem列除(0,MaxItem-1)与(MaxItem-1,MaxItem-1)
	 m_iMoveFlag[MoveUp] = TRUE;m_iMoveFlag[MoveRight] = FALSE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 3;
	}
	else if(BlankPosition.y>0 && BlankPosition.y<MaxItem-1 && BlankPosition.x==0)
	{//空白位在第一行除(0,0)与(0,MaxItem-1)
	 m_iMoveFlag[MoveUp] = FALSE;m_iMoveFlag[MoveRight] = TRUE;
	 m_iMoveFlag[MoveDown] = TRUE;m_iMoveFlag[MoveLeft] = TRUE;
	 m_iMoveFlagCount = 3;
	}
	else if(BlankPosition.y>0 && BlankPosition.y<MaxItem-1 && BlankPosition.x==MaxItem-1)
	{//空白位在第MaxItem行 除(MaxItem-1,0)与(MaxItem-1,MaxItem-1)
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
//GenerateChild: 生成子节点
// 返回值：生成子节点过程中的错误代码
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
// FindBestMoveFlag:寻找最佳移动方式 并移动之
// 入口: GenerateChild();
// 参数:子节点集
// 返回值:错误代码
//////////////////////////////////////////
UINT CMain::FindBestMoveFlag(List *pList)
{
	//计算移动后是否有重复项 重复项不参加最佳选择运算
	IsReadyExist(pList);
	//在没有出现过移动结果中选择最佳解
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
	 //没有找到最佳方法 表示这个结点所有子项都已扩展。要回溯
	 //但在本程序中 由于输入值一定有解，可以不要回溯
	 for(int CurrentG = this->m_CurrentG;CurrentG>0;CurrentG--)
	  if(FindOtherNote(CurrentG)) return NoError;//找到了另一个未扩展的结点返回
	 */
		return ErrorCode;//没有未扩展的结点了 本题无解
	}	

	//标记最佳解
	Pos = pList->GetHeadPosition();
	while(Pos)
	{
	 POSITION CurPos = Pos;
	 CDisplay *Item = (CDisplay *)m_DispList.GetNext(Pos);
	 if(CurPos == MinPos) 
	 {
	  Item->SetThisIsAAnswer();//是解
	  Item->SetNoteType(AlReady);//被扩展
	  m_CurOpItem = Item;
	 }
	 m_DispList.AddTail(Item);
	}
	return NoError;
}

//////////////////////////////////////
// IsReadyExist: 是否已经存在
// 如果已经存在则设标记为已存在
// 入口: FindBestMoveFlag()
// 参数：子节点集起始位置指针
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
	  {//已经存在于第i个结果上
	   ItemSrc->SetNoteType(Cannot);
	   m_DispList.AddTail(ItemSrc);
	   pList->RemoveAt(CurPos);
	   break;
	  }
	 }
	}
}

////////////////////////////////////
// CaculateH:计算当前H值
// 参数:当前节点
// 返回值:H值
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
// Scan: 扫描 计算H值的辅助函数
// 入口:CaculateH()
// 参数:i的位置,与i的值
// 返回值:H(i)的值
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
// FindOtherNote: 用于回溯 寻找另一个节点 
// 参数:当前G值
// 返回值:是否找到新节点
///////////////////////////////////////
BOOL CMain::FindOtherNote(int CurrentG)
{
	POSITION pos = m_DispList.GetTailPosition();
	while(pos)
	{
		CDisplay *Item = (CDisplay *)m_DispList.GetPrev(pos);
		if(Item->GetCurrentG() != CurrentG) continue;//没有到当前层
		if(Item->GetNoteType() != NotYet) continue;//当前结点不是没有被扩展的结点
		m_CurOpItem = Item;
		return TRUE;
	}
	return FALSE;
}

///////////////////////////////////////////
// GetResultListPoint: 得到搜索树树根的指针
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
