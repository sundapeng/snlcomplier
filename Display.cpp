// Display.cpp: implementation of the CDisplay class.
// CDisplay��ʵ���ļ�
// ���ߣ���־��
// ����޸����ڣ�2003��12��8��
////////////////////////////////////////////////

#include "stdafx.h"
#include "Display.h"

////////////////////
// ���캯��
////////////////////
CDisplay::CDisplay()
{
	this->m_bAnswer = false;
	this->m_NoteType = NotYet;
}

////////////////////
// ��������
////////////////////
CDisplay::~CDisplay()
{

}

//////////////////////////////////////////////////
// LoadData������״̬
// ���������ַ�ʽ��1.���룬2.��������״̬
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
// FindBlankPostion:Ѱ�ҿհ�λ��λ��
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
// MoveBlank:�ƶ��հ�λ
// ���ڸ�������״̬������֮�� �ƶ��հ�λʹ������״̬
// ������MoveDirection �ƶ���ʽ
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
// IsEuqal:����״̬�Ƿ���ͬ
// ����:Ŀ��ڵ�Item
// ����ֵ��TRUE ��� FALSE ����
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
// GetDispData:����ӿ� ȡ�õ�ǰλ�õ�״ֵ̬
// ����:λ�ñ���i,j
////////////////////////////////////////////
DataType CDisplay::GetDispData(int i, int j)
{
	return m_DispData[i][j];
}

////////////////////////////////////////////////
// SetThisIsAAnswer: ����ӿ� ���ýڵ�Ϊ����ڵ�
////////////////////////////////////////////////
void CDisplay::SetThisIsAAnswer()
{
	this->m_bAnswer = true;
}

////////////////////////////////////
// GetNoteType:����ӿ� ȡ�ýڵ�����
////////////////////////////////////
UINT CDisplay::GetNoteType()
{
	return this->m_NoteType;
}

/////////////////////////////////////////
// SetNoteType:����ӿ� ���ýڵ�����
/////////////////////////////////////////
void CDisplay::SetNoteType(UINT noteType)
{
	this->m_NoteType = noteType;
}

/////////////////////////////////////
// SetCurrentG:����ӿ� ���ýڵ��Gֵ
/////////////////////////////////////
void CDisplay::SetCurrentG(int CurG)
{
	this->m_CurrentG = CurG;
}

////////////////////////////////////
// GetCurrentG:����ӿ� ȡ�ýڵ�Gֵ
////////////////////////////////////
int CDisplay::GetCurrentG()
{
	return this->m_CurrentG;
}

///////////////////////////////////////////////
// GetIsAAnswer:����ӿ� ȡ�ýڵ��Ƿ�������ڵ�
///////////////////////////////////////////////
BOOL CDisplay::GetIsAAnswer()
{
	return this->m_bAnswer;
}

//////////////////////////////////////////
// SetCurrentCount:����ӿ� ���ýڵ����ֵ
//////////////////////////////////////////
void CDisplay::SetCurrentCount(int Count)
{
	this->m_CurrentCount = Count;
}

////////////////////////////////////////////
// GetCurrentCount:����ӿ� ��ȡ�ýڵ����ֵ
////////////////////////////////////////////
int CDisplay::GetCurrentCount()
{
	return this->m_CurrentCount;
}

////////////////////////////////////////////
// GetBlankPositin:����ӿ� �ύ�հ�λ��λ��
////////////////////////////////////////////
Position CDisplay::GetBlankPosition()
{
	FindBlankPosition();
	return m_BlankPosition;
}