// InitDialog.cpp : implementation file
//

#include "stdafx.h"
#include "AI3.h"
#include "InitDialog.h"

/////////////////////////////////////////////////////////////////////////////
// CInitDialog dialog


CInitDialog::CInitDialog(CWnd* pParent /*=NULL*/)
	: CDialog(CInitDialog::IDD, pParent)
{
}


void CInitDialog::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);

	DDX_Control(pDX, IDC_ED1, m_ED1);
	DDX_Control(pDX, IDC_ED2, m_ED2);
	DDX_Control(pDX, IDC_ED3, m_ED3);
	DDX_Control(pDX, IDC_ED4, m_ED4);
	DDX_Control(pDX, IDC_ED5, m_ED5);
	DDX_Control(pDX, IDC_ED6, m_ED6);
	DDX_Control(pDX, IDC_ED7, m_ED7);
	DDX_Control(pDX, IDC_ED8, m_ED8);
	DDX_Control(pDX, IDC_ED9, m_ED9);
	DDX_Control(pDX, IDC_ST1, m_ST1);
	DDX_Control(pDX, IDC_ST2, m_ST2);
	DDX_Control(pDX, IDC_ST3, m_ST3);
	DDX_Control(pDX, IDC_ST4, m_ST4);
	DDX_Control(pDX, IDC_ST5, m_ST5);
	DDX_Control(pDX, IDC_ST6, m_ST6);
	DDX_Control(pDX, IDC_ST7, m_ST7);
	DDX_Control(pDX, IDC_ST8, m_ST8);
	DDX_Control(pDX, IDC_ST9, m_ST9);
}


BEGIN_MESSAGE_MAP(CInitDialog, CDialog)
	ON_BN_CLICKED(IDC_ST1, OnSrcButton1)
	ON_BN_CLICKED(IDC_ST2, OnSrcButton2)
	ON_BN_CLICKED(IDC_ST3, OnSrcButton3)
	ON_BN_CLICKED(IDC_ST4, OnSrcButton4)
	ON_BN_CLICKED(IDC_ST5, OnSrcButton5)
	ON_BN_CLICKED(IDC_ST6, OnSrcButton6)
	ON_BN_CLICKED(IDC_ST7, OnSrcButton7)
	ON_BN_CLICKED(IDC_ST8, OnSrcButton8)
	ON_BN_CLICKED(IDC_ST9, OnSrcButton9)
	ON_BN_CLICKED(IDC_ED1, OnDescButton1)
	ON_BN_CLICKED(IDC_ED2, OnDescButton2)
	ON_BN_CLICKED(IDC_ED3, OnDescButton3)
	ON_BN_CLICKED(IDC_ED4, OnDescButton4)
	ON_BN_CLICKED(IDC_ED5, OnDescButton5)
	ON_BN_CLICKED(IDC_ED6, OnDescButton6)
	ON_BN_CLICKED(IDC_ED7, OnDescButton7)
	ON_BN_CLICKED(IDC_ED8, OnDescButton8)
	ON_BN_CLICKED(IDC_ED9, OnDescButton9)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CInitDialog message handlers
BOOL CInitDialog::OnInitDialog() 
{
	CDialog::OnInitDialog();	
	int k=1;
	for(int i=0;i<MaxItem;i++)
	 for(int j=0;j<MaxItem;j++)
	 {
		 m_Src[i][j] = k;
		 m_Desc[i][j] = k++;
	 }
	m_Src[MaxItem-1][MaxItem-1] = 0;
	m_Desc[MaxItem-1][MaxItem-1] = 0;
	UpdateSrcRadio();
	UpdateDescRadio();
	return TRUE;  // return TRUE unless you set the focus to a control
	              // EXCEPTION: OCX Property Pages should return FALSE
}

int CInitDialog::GetSrcData(int i,int j)
{
	return m_Src[i][j];
}

DataType CInitDialog::GetDescData(int i,int j)
{
	return m_Desc[i][j];
}

void CInitDialog::OnSrcButton1()
{
	if(m_Src[0][1] ==0 )
	{
		m_Src[0][1] = m_Src[0][0];
		m_Src[0][0] = 0;
	}
	else if(m_Src[1][0] == 0)
	{
		m_Src[1][0] = m_Src[0][0];
		m_Src[0][0] = 0;
	}
	else m_ST1.SetCheck(false);
	UpdateSrcRadio();
}

void CInitDialog::OnSrcButton2()
{
	if(m_Src[0][0] ==0 )
	{
		m_Src[0][0] = m_Src[0][1];
		m_Src[0][1] = 0;
	}
	else if(m_Src[1][1] == 0)
	{
		m_Src[1][1] = m_Src[0][1];
		m_Src[0][1] = 0;
	}
	else if(m_Src[0][2] == 0)
	{
		m_Src[0][2] = m_Src[0][1];
		m_Src[0][1] = 0;
	}
	else m_ST2.SetCheck(false);
	UpdateSrcRadio();
}
void CInitDialog::OnSrcButton3()
{
	if(m_Src[0][1] ==0 )
	{
		m_Src[0][1] = m_Src[0][2];
		m_Src[0][2] = 0;
	}
	else if(m_Src[1][2] == 0)
	{
		m_Src[1][2] = m_Src[0][2];
		m_Src[0][2] = 0;
	}
	else m_ST3.SetCheck(false);
	UpdateSrcRadio();
}

void CInitDialog::OnSrcButton4()
{
	if(m_Src[1][1] == 0)
	{
		m_Src[1][1] = m_Src[1][0];
		m_Src[1][0] = 0;
	}
	else if(m_Src[0][0] == 0)
	{
		m_Src[0][0] = m_Src[1][0];
		m_Src[1][0] = 0;
	}
	else if(m_Src[2][0] == 0)
	{
		m_Src[2][0] = m_Src[1][0];
		m_Src[1][0] = 0;
	}
	else m_ST4.SetCheck(false);
	UpdateSrcRadio();
}

void CInitDialog::OnSrcButton5()
{
	if(m_Src[1][2] ==0 )
	{
		m_Src[1][2] = m_Src[1][1];
		m_Src[1][1] = 0;
	}
	else if(m_Src[1][0] == 0)
	{
		m_Src[1][0] = m_Src[1][1];
		m_Src[1][1] = 0;
	}
	else if(m_Src[0][1] == 0)
	{
		m_Src[0][1] = m_Src[1][1];
		m_Src[1][1] = 0;
	}
	else if(m_Src[2][1] == 0)
	{
		m_Src[2][1] = m_Src[1][1];
		m_Src[1][1] = 0;
	}
	else m_ST5.SetCheck(false);
	UpdateSrcRadio();
}

void CInitDialog::OnSrcButton6()
{
	if(m_Src[2][2] ==0 )
	{
		m_Src[2][2] = m_Src[1][2];
		m_Src[1][2] = 0;
	}
	else if(m_Src[0][2] == 0)
	{
		m_Src[0][2] = m_Src[1][2];
		m_Src[1][2] = 0;
	}
	else if(m_Src[1][1] == 0)
	{
		m_Src[1][1] = m_Src[1][2];
		m_Src[1][2] = 0;
	}
	else m_ST6.SetCheck(false);
	UpdateSrcRadio();
}

void CInitDialog::OnSrcButton7()
{
	if(m_Src[1][0] ==0 )
	{
		m_Src[1][0] = m_Src[2][0];
		m_Src[2][0] = 0;
	}
	else if(m_Src[2][1] == 0)
	{
		m_Src[2][1] = m_Src[2][0];
		m_Src[2][0] = 0;
	}
	else m_ST7.SetCheck(false);
	UpdateSrcRadio();
}
void CInitDialog::OnSrcButton8()
{
	if(m_Src[2][0] ==0 )
	{
		m_Src[2][0] = m_Src[2][1];
		m_Src[2][1] = 0;
	}
	else if(m_Src[2][2] == 0)
	{
		m_Src[2][2] = m_Src[2][1];
		m_Src[2][1] = 0;
	}
	else if(m_Src[1][1] == 0)
	{
		m_Src[1][1] = m_Src[2][1];
		m_Src[2][1] = 0;
	}
	else m_ST8.SetCheck(false);
	UpdateSrcRadio();
}

void CInitDialog::OnSrcButton9()
{
	if(m_Src[2][1] ==0 )
	{
		m_Src[2][1] = m_Src[2][2];
		m_Src[2][2] = 0;
	}
	else if(m_Src[1][2] == 0)
	{
		m_Src[1][2] = m_Src[2][2];
		m_Src[2][2] = 0;
	}
	else m_ST9.SetCheck(false);
	UpdateSrcRadio();
}


void CInitDialog::OnDescButton1()
{
	if(m_Desc[0][1] ==0 )
	{
		m_Desc[0][1] = m_Desc[0][0];
		m_Desc[0][0] = 0;
	}
	else if(m_Desc[1][0] == 0)
	{
		m_Desc[1][0] = m_Desc[0][0];
		m_Desc[0][0] = 0;
	}
	else m_ED1.SetCheck(false);
	UpdateDescRadio();
}

void CInitDialog::OnDescButton2()
{
	if(m_Desc[0][0] ==0 )
	{
		m_Desc[0][0] = m_Desc[0][1];
		m_Desc[0][1] = 0;
	}
	else if(m_Desc[1][1] == 0)
	{
		m_Desc[1][1] = m_Desc[0][1];
		m_Desc[0][1] = 0;
	}
	else if(m_Desc[0][2] == 0)
	{
		m_Desc[0][2] = m_Desc[0][1];
		m_Desc[0][1] = 0;
	}
	else m_ED2.SetCheck(false);
	UpdateDescRadio();
}
void CInitDialog::OnDescButton3()
{
	if(m_Desc[0][1] ==0 )
	{
		m_Desc[0][1] = m_Desc[0][2];
		m_Desc[0][2] = 0;
	}
	else if(m_Desc[1][2] == 0)
	{
		m_Desc[1][2] = m_Desc[0][2];
		m_Desc[0][2] = 0;
	}
	else m_ED3.SetCheck(false);
	UpdateDescRadio();
}

void CInitDialog::OnDescButton4()
{
	if(m_Desc[1][1] == 0)
	{
		m_Desc[1][1] = m_Desc[1][0];
		m_Desc[1][0] = 0;
	}
	else if(m_Desc[0][0] == 0)
	{
		m_Desc[0][0] = m_Desc[1][0];
		m_Desc[1][0] = 0;
	}
	else if(m_Desc[2][0] == 0)
	{
		m_Desc[2][0] = m_Desc[1][0];
		m_Desc[1][0] = 0;
	}
	else m_ED4.SetCheck(false);
	UpdateDescRadio();
}

void CInitDialog::OnDescButton5()
{
	if(m_Desc[1][2] ==0 )
	{
		m_Desc[1][2] = m_Desc[1][1];
		m_Desc[1][1] = 0;
	}
	else if(m_Desc[1][0] == 0)
	{
		m_Desc[1][0] = m_Desc[1][1];
		m_Desc[1][1] = 0;
	}
	else if(m_Desc[0][1] == 0)
	{
		m_Desc[0][1] = m_Desc[1][1];
		m_Desc[1][1] = 0;
	}
	else if(m_Desc[2][1] == 0)
	{
		m_Desc[2][1] = m_Desc[1][1];
		m_Desc[1][1] = 0;
	}
	else m_ED5.SetCheck(false);
	UpdateDescRadio();
}

void CInitDialog::OnDescButton6()
{
	if(m_Desc[2][2] ==0 )
	{
		m_Desc[2][2] = m_Desc[1][2];
		m_Desc[1][2] = 0;
	}
	else if(m_Desc[0][2] == 0)
	{
		m_Desc[0][2] = m_Desc[1][2];
		m_Desc[1][2] = 0;
	}
	else if(m_Desc[1][1] == 0)
	{
		m_Desc[1][1] = m_Desc[1][2];
		m_Desc[1][2] = 0;
	}
	else m_ED6.SetCheck(false);
	UpdateDescRadio();
}

void CInitDialog::OnDescButton7()
{
	if(m_Desc[1][0] ==0 )
	{
		m_Desc[1][0] = m_Desc[2][0];
		m_Desc[2][0] = 0;
	}
	else if(m_Desc[2][1] == 0)
	{
		m_Desc[2][1] = m_Desc[2][0];
		m_Desc[2][0] = 0;
	}
	else m_ED7.SetCheck(false);
	UpdateDescRadio();
}
void CInitDialog::OnDescButton8()
{
	if(m_Desc[2][0] ==0 )
	{
		m_Desc[2][0] = m_Desc[2][1];
		m_Desc[2][1] = 0;
	}
	else if(m_Desc[2][2] == 0)
	{
		m_Desc[2][2] = m_Desc[2][1];
		m_Desc[2][1] = 0;
	}
	else if(m_Desc[1][1] == 0)
	{
		m_Desc[1][1] = m_Desc[2][1];
		m_Desc[2][1] = 0;
	}
	else m_ED8.SetCheck(false);
	UpdateDescRadio();
}

void CInitDialog::OnDescButton9()
{
	if(m_Desc[2][1] ==0 )
	{
		m_Desc[2][1] = m_Desc[2][2];
		m_Desc[2][2] = 0;
	}
	else if(m_Desc[1][2] == 0)
	{
		m_Desc[1][2] = m_Desc[2][2];
		m_Desc[2][2] = 0;
	}
	else m_ED9.SetCheck(false);
	UpdateDescRadio();
}

void CInitDialog::UpdateSrcRadio()
{
	CString Msg;
	if(m_Src[0][0] == 0) {Msg = "";m_ST1.SetCheck(1);}
	else Msg.Format("%d",m_Src[0][0]);
	m_ST1.SetWindowText(Msg);
	if(m_Src[0][1] == 0) {Msg = "";m_ST2.SetCheck(1);}
	else Msg.Format("%d",m_Src[0][1]);
	m_ST2.SetWindowText(Msg);
	if(m_Src[0][2] == 0) {Msg = "";m_ST3.SetCheck(1);}
	else Msg.Format("%d",m_Src[0][2]);
	m_ST3.SetWindowText(Msg);

	if(m_Src[1][0] == 0) {Msg = "";m_ST4.SetCheck(1);}
	else Msg.Format("%d",m_Src[1][0]);
	m_ST4.SetWindowText(Msg);
	if(m_Src[1][1] == 0) {Msg = "";m_ST5.SetCheck(1);}
	else Msg.Format("%d",m_Src[1][1]);
	m_ST5.SetWindowText(Msg);
	if(m_Src[1][2] == 0) {Msg = "";m_ST6.SetCheck(1);}
	else Msg.Format("%d",m_Src[1][2]);
	m_ST6.SetWindowText(Msg);

	if(m_Src[2][0] == 0) {Msg = "";m_ST7.SetCheck(1);}
	else Msg.Format("%d",m_Src[2][0]);
	m_ST7.SetWindowText(Msg);
	if(m_Src[2][1] == 0) {Msg = "";m_ST8.SetCheck(1);}
	else Msg.Format("%d",m_Src[2][1]);
	m_ST8.SetWindowText(Msg);
	if(m_Src[2][2] == 0) {Msg = "";m_ST9.SetCheck(1);}
	else Msg.Format("%d",m_Src[2][2]);
	m_ST9.SetWindowText(Msg);
}

void CInitDialog::UpdateDescRadio()
{
	CString Msg;
	if(m_Desc[0][0] == 0) {Msg = "";m_ED1.SetCheck(1);}
	else Msg.Format("%d",m_Desc[0][0]);
	m_ED1.SetWindowText(Msg);
	if(m_Desc[0][1] == 0) {Msg = "";m_ED2.SetCheck(1);}
	else Msg.Format("%d",m_Desc[0][1]);
	m_ED2.SetWindowText(Msg);
	if(m_Desc[0][2] == 0) {Msg = "";m_ED3.SetCheck(1);}
	else Msg.Format("%d",m_Desc[0][2]);
	m_ED3.SetWindowText(Msg);

	if(m_Desc[1][0] == 0) {Msg = "";m_ED4.SetCheck(1);}
	else Msg.Format("%d",m_Desc[1][0]);
	m_ED4.SetWindowText(Msg);
	if(m_Desc[1][1] == 0) {Msg = "";m_ED5.SetCheck(1);}
	else Msg.Format("%d",m_Desc[1][1]);
	m_ED5.SetWindowText(Msg);
	if(m_Desc[1][2] == 0) {Msg = "";m_ED6.SetCheck(1);}
	else Msg.Format("%d",m_Desc[1][2]);
	m_ED6.SetWindowText(Msg);

	if(m_Desc[2][0] == 0) {Msg = "";m_ED7.SetCheck(1);}
	else Msg.Format("%d",m_Desc[2][0]);
	m_ED7.SetWindowText(Msg);
	if(m_Desc[2][1] == 0) {Msg = "";m_ED8.SetCheck(1);}
	else Msg.Format("%d",m_Desc[2][1]);
	m_ED8.SetWindowText(Msg);
	if(m_Desc[2][2] == 0) {Msg = "";m_ED9.SetCheck(1);}
	else Msg.Format("%d",m_Desc[2][2]);
	m_ED9.SetWindowText(Msg);
}
