// AI3Doc.cpp : implementation of the CAI3Doc class
//

#include "stdafx.h"
#include "AI3.h"

#include "AI3Doc.h"
#include "InputAdvDlg.h"
#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAI3Doc

IMPLEMENT_DYNCREATE(CAI3Doc, CDocument)

BEGIN_MESSAGE_MAP(CAI3Doc, CDocument)
ON_COMMAND(IDM_Custom,OnFileNew2)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CAI3Doc construction/destruction

CAI3Doc::CAI3Doc()
{
}

CAI3Doc::~CAI3Doc()
{
}

BOOL CAI3Doc::OnNewDocument()
{
	if (!CDocument::OnNewDocument())
		return FALSE;
	static BOOL Ready = FALSE;
	if(!Ready) return Ready = TRUE;
	
	CMain MainProcess;
	if(MainProcess.LoadData()) 
	{
		List *pList = MainProcess.GetResultListPoint();
		this->GenerateDispList(pList);//生成显示链表
	}
	return TRUE;
}

/////////////////////////////////////////////////////////////////////////////
// CAI3Doc serialization

void CAI3Doc::Serialize(CArchive& ar)
{
	if (ar.IsStoring())
	{
	}
	else
	{
	}
}

/////////////////////////////////////////////////////////////////////////////
// CAI3Doc diagnostics

#ifdef _DEBUG
void CAI3Doc::AssertValid() const
{
	CDocument::AssertValid();
}

void CAI3Doc::Dump(CDumpContext& dc) const
{
	CDocument::Dump(dc);
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CAI3Doc commands

///////////////////////////////////////////////
// 产生结果显示链表
///////////////////////////////////////////////
void CAI3Doc::GenerateDispList(List *pScrList)
{
	if(m_DispList.GetCount() > 0) m_DispList.RemoveAll();
	POSITION pos = pScrList->GetHeadPosition();
	while(pos)
	{
		CDisplay * SrcItem = (CDisplay *)pScrList->GetNext(pos);
		this->m_DispList.AddTail(SrcItem);
	}
}

void CAI3Doc::OnFileNew2()
{
	CMain MainProcess;
	if(MainProcess.LoadData(true)) 
	{
		List *pList = MainProcess.GetResultListPoint();
		this->GenerateDispList(pList);//生成显示链表
	}
	this->UpdateAllViews(NULL);
}