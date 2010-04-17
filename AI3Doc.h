// AI3Doc.h : interface of the CAI3Doc class
//
/////////////////////////////////////////////////////////////////////////////

#pragma once

#include "Main.h"

class CAI3Doc : public CDocument
{
protected:
	CAI3Doc();
	DECLARE_DYNCREATE(CAI3Doc)

// Attributes
public:
// Operations
public:
// Overrides
public:
	virtual BOOL OnNewDocument();
	virtual void Serialize(CArchive& ar);
// Implementation
public:
	List m_DispList;
	virtual ~CAI3Doc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:
	void GenerateDispList(List *pSrcList);// 产生结果显示链表
protected:
	void OnFileNew2();
	DECLARE_MESSAGE_MAP()
};
