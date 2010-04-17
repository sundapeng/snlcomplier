// InitDialog.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CInitDialog dialog
#include "Predefine.h"

class CInitDialog : public CDialog
{
// Construction
public:
	CInitDialog(CWnd* pParent = NULL);   // standard constructor
private:
	DataType m_Src[MaxItem][MaxItem];
	DataType m_Desc[MaxItem][MaxItem];
private:
	enum { IDD = IDD_InitDIALOG };
	CButton		m_ED1;	CButton		m_ED2;	CButton		m_ED3;
	CButton		m_ED4;	CButton		m_ED5;	CButton		m_ED6;
	CButton		m_ED7;	CButton		m_ED8;	CButton		m_ED9;
	CButton		m_ST1;	CButton		m_ST2;	CButton		m_ST3;
	CButton		m_ST4;	CButton		m_ST5;	CButton		m_ST6;
	CButton		m_ST7;	CButton		m_ST8;	CButton		m_ST9;
// Overrides
public:
	DataType GetDescData(int i,int j);//取得用户计算目标值
	DataType GetSrcData(int i,int j);//取得用户计算开始值
protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
// Implementation
protected:
	void UpdateDescRadio();
	void UpdateSrcRadio();
	virtual BOOL OnInitDialog();
	afx_msg void OnSrcButton1();
	afx_msg void OnSrcButton2();
	afx_msg void OnSrcButton3();
	afx_msg void OnSrcButton4();
	afx_msg void OnSrcButton5();
	afx_msg void OnSrcButton6();
	afx_msg void OnSrcButton7();
	afx_msg void OnSrcButton8();
	afx_msg void OnSrcButton9();
	afx_msg void OnDescButton1();
	afx_msg void OnDescButton2();
	afx_msg void OnDescButton3();
	afx_msg void OnDescButton4();
	afx_msg void OnDescButton5();
	afx_msg void OnDescButton6();
	afx_msg void OnDescButton7();
	afx_msg void OnDescButton8();
	afx_msg void OnDescButton9();
	DECLARE_MESSAGE_MAP()
};