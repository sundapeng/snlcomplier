// Display.cpp: implementation of the CDisplay class.
// CDisplay类实现文件
// 作者：刘志良
// 最后修改日期：2003年12月8日
////////////////////////////////////////////////

#include "stdafx.h"
#include "Display.h"

////////////////////
// 构造函数
////////////////////
CDisplay::CDisplay()
{
	this->m_bAnswer = false;
	this->m_NoteType = NotYet;
}

////////////////////
// 析构函数
////////////////////
CDisplay::~CDisplay()
{

}

//////////////////////////////////////////////////
// LoadData：载入状态
// 载入有两种方式：1.输入，2.复制已有状态
//////////////////////////////////////////////////
void CDisplay::LoadData(DataType Data,int i,int j)
{
	m_DispData[i][j] = Data;
}

void CDisplay::LoadData(CDisplay *Item)
{
	DataType Src[MaxItem][MaxItem];
	for(int i=0; i<MaxItem; i++)
	 for(int j=0; j<MaxItem; j++)
	 {
	  Src[i][j] = Item->GetDispData(i,j);
	  m_DispData[i][j] = Src[i][j];
	 }
}

////////////////////////////////////
// FindBlankPostion:寻找空白位的位置
////////////////////////////////////
void CDisplay::FindBlankPosition()
{
	m_BlankPosition = Position(65535,65535);
	for(int i=0; i<MaxItem; i++)
	 for(int j=0; j<MaxItem; j++) 
	  if(m_DispData[i][j] == Blank) 
		  m_BlankPosition = Position(i,j);	
}

////////////////////////////////////////////////////
// MoveBlank:移动空白位
// 用于复制已有状态到本身之后 移动空白位使产生新状态
// 参数：MoveDirection 移动方式
////////////////////////////////////////////////////
void CDisplay::MoveBlank(UINT MoveDirection)
{
	FindBlankPosition();
	Position TmpPos = m_BlankPosition;
	switch(MoveDirection)
	{
	case MoveLeft : TmpPos = m_BlankPosition-Position(0,1);break;
	case MoveRight : TmpPos = m_BlankPosition+Position(0,1);break;
	case MoveUp : TmpPos = m_BlankPosition-Position(1,0);break;
	case MoveDown : TmpPos = m_BlankPosition+Position(1,0);break;
	default : AfxMessageBox("ERROR MoveBlank");break;
	}
	DataType DataTemp = m_DispData[TmpPos.x][TmpPos.y];
	m_DispData[TmpPos.x][TmpPos.y] = m_DispData[m_BlankPosition.x][m_BlankPosition.y];
	m_DispData[m_BlankPosition.x][m_BlankPosition.y] = DataTemp;
} 

//////////////////////////////////////
// IsEuqal:两个状态是否相同
// 参数:目标节点Item
// 返回值：TRUE 相等 FALSE 不等
//////////////////////////////////////
BOOL CDisplay::IsEqual(CDisplay *Item)
{
	int k=0;
	DataType Src[MaxItem][MaxItem];
	for(int i=0; i<MaxItem; i++)
	 for(int j=0; j<MaxItem; j++)
	 {
	  Src[i][j] = Item->GetDispData(i,j);
	  if(Src[i][j] == m_DispData[i][j]) k++;
	 }
	if(k == MaxItem*MaxItem) return true;
	else return false;
}

////////////////////////////////////////////
// GetDispData:对外接口 取得当前位置的状态值
// 参数:位置变量i,j
////////////////////////////////////////////
DataType CDisplay::GetDispData(int i, int j)
{
	return m_DispData[i][j];
}

////////////////////////////////////////////////
// SetThisIsAAnswer: 对外接口 设置节点为正解节点
////////////////////////////////////////////////
void CDisplay::SetThisIsAAnswer()
{
	this->m_bAnswer = true;
}

////////////////////////////////////
// GetNoteType:对外接口 取得节点类型
////////////////////////////////////
UINT CDisplay::GetNoteType()
{
	return this->m_NoteType;
}

/////////////////////////////////////////
// SetNoteType:对外接口 设置节点类型
/////////////////////////////////////////
void CDisplay::SetNoteType(UINT noteType)
{
	this->m_NoteType = noteType;
}

/////////////////////////////////////
// SetCurrentG:对外接口 设置节点的G值
/////////////////////////////////////
void CDisplay::SetCurrentG(int CurG)
{
	this->m_CurrentG = CurG;
}

////////////////////////////////////
// GetCurrentG:对外接口 取得节点G值
////////////////////////////////////
int CDisplay::GetCurrentG()
{
	return this->m_CurrentG;
}

///////////////////////////////////////////////
// GetIsAAnswer:对外接口 取得节点是否是正解节点
///////////////////////////////////////////////
BOOL CDisplay::GetIsAAnswer()
{
	return this->m_bAnswer;
}

//////////////////////////////////////////
// SetCurrentCount:对外接口 设置节点记数值
//////////////////////////////////////////
void CDisplay::SetCurrentCount(int Count)
{
	this->m_CurrentCount = Count;
}

////////////////////////////////////////////
// GetCurrentCount:对外接口 设取得节点记数值
////////////////////////////////////////////
int CDisplay::GetCurrentCount()
{
	return this->m_CurrentCount;
}

////////////////////////////////////////////
// GetBlankPositin:对外接口 提交空白位的位置
////////////////////////////////////////////
Position CDisplay::GetBlankPosition()
{
	FindBlankPosition();
	return m_BlankPosition;
}