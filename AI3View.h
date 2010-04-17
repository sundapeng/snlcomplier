// AI3View.h : interface of the CAI3View class
//
/////////////////////////////////////////////////////////////////////////////
#pragma once

class CAI3View : public CScrollView
{
protected: // create from serialization only
	CAI3View();
	DECLARE_DYNCREATE(CAI3View)

// Attributes
public:
	CAI3Doc* GetDocument();

// Operations
public:

// Overrides
public:
	virtual void OnDraw(CDC* pDC);  // overridden to draw this view
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
	protected:
	virtual void OnInitialUpdate(); // called first time after construct
// Implementation
public:
	virtual ~CAI3View();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:
	afx_msg void OnViewStepOnly();
	afx_msg void OnUpdateViewStepOnly(CCmdUI* pCmdUI);
	afx_msg void OnViewTreeOnly();
	afx_msg void OnUpdateViewTreeOnly(CCmdUI* pCmdUI);
	DECLARE_MESSAGE_MAP()
private:
	BOOL m_bIsDispAll;
};

#ifndef _DEBUG  // debug version in AI3View.cpp
inline CAI3Doc* CAI3View::GetDocument()
   { return (CAI3Doc*)m_pDocument; }
#endif
