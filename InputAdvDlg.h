#pragma once
// InputAdvDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CInputAdvDlg dialog
#include "predefine.h"
class CInputAdvDlg : public CDialog
{
// Construction
public:
	DataType	 GetDescData(int i,int j);
	DataType     GetSrcData(int i, int j);
	CInputAdvDlg(CWnd* pParent = NULL);   // standard constructor

	enum { IDD = IDD_InitTestDlg };
private:
	int		m_ED1; int		m_ED2; int		m_ED3;
	int		m_ED4; int		m_ED5; int		m_ED6;
	int		m_ED7; int		m_ED8; int		m_ED9;
	int		m_ST1; int		m_ST2; int		m_ST3;
	int		m_ST4; int		m_ST5; int		m_ST6;
	int		m_ST7; int		m_ST8; int		m_ST9;
private:
	void AllStatusSet();
	BOOL IsEqual();
	BOOL IsHaveBlank();
	DataType m_Src[MaxItem][MaxItem];
	DataType m_Desc[MaxItem][MaxItem];
// Overrides
protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
// Implementation
protected:
	BOOL IsEqual(DataType Desc);
	virtual void OnOK();
	DECLARE_MESSAGE_MAP()
};
