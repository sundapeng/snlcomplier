// AI1View.cpp : implementation of the CAI3View class
//

#include "stdafx.h"
#include "AI3.h"

#include "AI3Doc.h"
#include "AI3View.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAI3View

IMPLEMENT_DYNCREATE(CAI3View, CScrollView)

BEGIN_MESSAGE_MAP(CAI3View, CScrollView)
	ON_COMMAND(IDM_ViewStepOnly, OnViewStepOnly)
	ON_UPDATE_COMMAND_UI(IDM_ViewStepOnly, OnUpdateViewStepOnly)
	ON_COMMAND(IDM_ViewTreeOnly, OnViewTreeOnly)
	ON_UPDATE_COMMAND_UI(IDM_ViewTreeOnly, OnUpdateViewTreeOnly)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CAI3View construction/destruction

CAI3View::CAI3View()
{
	m_bIsDispAll = true;
}

CAI3View::~CAI3View()
{
}

BOOL CAI3View::PreCreateWindow(CREATESTRUCT& cs)
{
	return CScrollView::PreCreateWindow(cs);
}

/////////////////////////////////////////////////////////////////////////////
// CAI3View drawing

void CAI3View::OnDraw(CDC* pDC)
{
	CAI3Doc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	POSITION pos = pDoc->m_DispList.GetHeadPosition();
	if(pos == NULL) return;
	//显示
	int DispX,DispY;
	while(pos)
	{
		CDisplay *Item = (CDisplay *)pDoc->m_DispList.GetNext(pos);
		DispY = (Item->GetCurrentG())*70+10;
		if(m_bIsDispAll) 
		{
		 for(int i=0; i<3; i++)
		 {
		  DispX = (Item->GetCurrentCount())*70+10; 
		  if(Item->GetIsAAnswer()) pDC->SetTextColor(RGB(255,0,0));
		  else pDC->SetTextColor(RGB(0,0,0));
		  for(int j=0; j<3; j++)
		  {
			pDC->Rectangle(DispX-1,DispY-1,DispX+20,DispY+20);
			DataType DataItem = Item->GetDispData(i,j);
			CString DispItem;
			if(DataItem != 0) DispItem.Format("%d ",DataItem);
			else DispItem ="  ";
			pDC->TextOut(DispX, DispY, DispItem);
			DispX += 20;
		  } 
		  DispY += 20;
		 }
		 DispY += 10;
		}
		else
		{
			if(Item->GetIsAAnswer() == false) continue;
			for(int i=0; i<3; i++)
			{
		     DispX = 10; 
		     pDC->SetTextColor(RGB(0,0,0));
     	     for(int j=0; j<3; j++)
			 {
			  pDC->Rectangle(DispX-1,DispY-1,DispX+20,DispY+20);
			  DataType DataItem = Item->GetDispData(i,j);
			  CString DispItem;
			  if(DataItem != 0) DispItem.Format("%d ",DataItem);
			  else DispItem ="  ";
			  pDC->TextOut(DispX, DispY, DispItem);
			  DispX += 20;
			 } 
		     DispY += 20;
			}
		    DispY += 10;
		}
	}
	//判断是否生成滚动条
	CSize sizeTotal;
	sizeTotal.cx = DispX+80;
	sizeTotal.cy = DispY+10;
	SetScrollSizes(MM_TEXT, sizeTotal);
}

void CAI3View::OnInitialUpdate()
{
	CScrollView::OnInitialUpdate();

	CSize sizeTotal;
	sizeTotal.cx = sizeTotal.cy = 100;
	SetScrollSizes(MM_TEXT, sizeTotal);
}

/////////////////////////////////////////////////////////////////////////////
// CAI3View diagnostics
void CAI3View::AssertValid() const
{
	CScrollView::AssertValid();
}

void CAI3View::Dump(CDumpContext& dc) const
{
	CScrollView::Dump(dc);
}

CAI3Doc* CAI3View::GetDocument() // non-debug version is inline
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CAI3Doc)));
	return (CAI3Doc*)m_pDocument;
}
/////////////////////////////////////////////////////////////////////////////
// CAI3View message handlers

void CAI3View::OnViewStepOnly() 
{
	this->m_bIsDispAll = FALSE;
	Invalidate();
}

void CAI3View::OnUpdateViewStepOnly(CCmdUI* pCmdUI) 
{
	pCmdUI->SetCheck(!m_bIsDispAll);
}

void CAI3View::OnViewTreeOnly() 
{
	this->m_bIsDispAll = TRUE;
	Invalidate();
}

void CAI3View::OnUpdateViewTreeOnly(CCmdUI* pCmdUI) 
{
	pCmdUI->SetCheck(m_bIsDispAll);
}
