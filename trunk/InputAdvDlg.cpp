// InputAdvDlg.cpp : implementation file
//

#include "stdafx.h"
#include "AI3.h"
#include "InputAdvDlg.h"

/////////////////////////////////////////////////////////////////////////////
// CInputAdvDlg dialog


CInputAdvDlg::CInputAdvDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CInputAdvDlg::IDD, pParent)
{
	m_ED1 = 1;m_ED2 = 2;m_ED3 = 3;
	m_ED4 = 4;m_ED5 = 5;m_ED6 = 6;
	m_ED7 = 7;m_ED8 = 8;m_ED9 = 0;
	m_ST1 = 1;m_ST2 = 2;m_ST3 = 3;
	m_ST4 = 4;m_ST5 = 5;m_ST6 = 6;
	m_ST7 = 7;m_ST8 = 8;m_ST9 = 0;
}


void CInputAdvDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Text(pDX, IDC_TED1, m_ED1);
	DDX_Text(pDX, IDC_TED2, m_ED2);
	DDX_Text(pDX, IDC_TED3, m_ED3);
	DDX_Text(pDX, IDC_TED4, m_ED4);
	DDX_Text(pDX, IDC_TED5, m_ED5);
	DDX_Text(pDX, IDC_TED6, m_ED6);
	DDX_Text(pDX, IDC_TED7, m_ED7);
	DDX_Text(pDX, IDC_TED8, m_ED8);
	DDX_Text(pDX, IDC_TED9, m_ED9);

	DDX_Text(pDX, IDC_TST1, m_ST1);
	DDX_Text(pDX, IDC_TST2, m_ST2);
	DDX_Text(pDX, IDC_TST3, m_ST3);
	DDX_Text(pDX, IDC_TST4, m_ST4);
	DDX_Text(pDX, IDC_TST5, m_ST5);
	DDX_Text(pDX, IDC_TST6, m_ST6);
	DDX_Text(pDX, IDC_TST7, m_ST7);
	DDX_Text(pDX, IDC_TST8, m_ST8);
	DDX_Text(pDX, IDC_TST9, m_ST9);
}


BEGIN_MESSAGE_MAP(CInputAdvDlg, CDialog)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CInputAdvDlg message handlers

void CInputAdvDlg::OnOK() 
{
	CWnd *pWnd = ::AfxGetApp()->GetMainWnd();
	AllStatusSet();
	if(IsHaveBlank())
		if(IsEqual())
			CDialog::OnOK();
		else 
			pWnd->PostMessage(WM_ERROR,0,ErrorCode2);
	else
		pWnd->PostMessage(WM_ERROR,0,ErrorCode3);
}

BOOL CInputAdvDlg::IsHaveBlank()
{
	for(int i=0;i<MaxItem;i++)
	 for(int j=0; j<MaxItem; j++)
	  if(this->m_Src[i][j] == 0) return TRUE;
	return FALSE;
}

BOOL CInputAdvDlg::IsEqual()
{
	for(int i=0; i<MaxItem; i++)
	 for(int j=0; j<MaxItem; j++)
	  if(!IsEqual(m_Src[i][j])) return FALSE;
	return TRUE;
}

BOOL CInputAdvDlg::IsEqual(DataType Desc)
{
	for(int i=0; i<MaxItem; i++)
	 for(int j=0; j<MaxItem; j++)
	  if(m_Desc[i][j] == Desc) return TRUE;
	return FALSE;
}

void CInputAdvDlg::AllStatusSet()
{
	UpdateData(true);

	m_Desc[0][0] = m_ED1;m_Desc[0][1] = m_ED2;m_Desc[0][2] = m_ED3;
	m_Desc[1][0] = m_ED4;m_Desc[1][1] = m_ED5;m_Desc[1][2] = m_ED6;
	m_Desc[2][0] = m_ED7;m_Desc[2][1] = m_ED8;m_Desc[2][2] = m_ED9;
	
	m_Src[0][0] = m_ST1;m_Src[0][1] = m_ST2;m_Src[0][2] = m_ST3;
	m_Src[1][0] = m_ST4;m_Src[1][1] = m_ST5;m_Src[1][2] = m_ST6;
	m_Src[2][0] = m_ST7;m_Src[2][1] = m_ST8;m_Src[2][2] = m_ST9;
}


DataType CInputAdvDlg::GetSrcData(int i, int j)
{
	return this->m_Src[i][j];
}

DataType CInputAdvDlg::GetDescData(int i, int j)
{
	return this->m_Desc[i][j];
}
