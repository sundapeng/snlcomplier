// AI3.h : main header file for the AI3 application
//
#pragma once

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"       // main symbols

/////////////////////////////////////////////////////////////////////////////
// CAI3App:
// See AI3.cpp for the implementation of this class
//

class CAI3App : public CWinApp
{
public:
	CAI3App();

// Overrides
	public:
	virtual BOOL InitInstance();
// Implementation
	afx_msg void OnAppAbout();
	DECLARE_MESSAGE_MAP()
};