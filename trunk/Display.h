/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 
@@ Display.h: interface for the CDisplay class.
@@ CDisplay类定义文件
@@ CDisplay类在本程序中主要用于状态的描述
@@ 作者：刘志良
@@ 最后修改日期：2003年12月8日
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

#include "predefine.h"

class CDisplay  
{
public:
	CDisplay();//构造函数
	virtual ~CDisplay();//析构函数
private:
	void		FindBlankPosition();//寻找空白位位置
private:
	int			m_CurrentG;//节点G值
	int			m_CurrentCount;//结点的计数 便于显示
	DataType	m_DispData[MaxItem][MaxItem];//节点状态
	BOOL		m_bAnswer;//是否正解
	UINT		m_NoteType;//节点类型
public:
	int			GetCurrentCount();//取得节点计数值
	void		SetCurrentCount(int Count);//设置节点计数值
	BOOL		GetIsAAnswer();//该节点是否是解
	int			GetCurrentG();//取得当前G值
	void		SetCurrentG(int CurG);//设置当前节点G值
	void		SetNoteType(UINT noteType);//设置节点类型
	UINT		GetNoteType();//取得节点类型
	void		SetThisIsAAnswer();//设置该节点是正解
	BOOL		IsEqual(CDisplay *Item);//判断Item节点是否与当前节点相等
	DataType	GetDispData(int i,int j);//取得节点状态表示
	Position	GetBlankPosition();//取得空白位位置
	void		MoveBlank(UINT MoveDirection);//根据MoveDirection移动当前状态在的空白位
	void		LoadData(CDisplay *Item);//从Item节点中Copy状态值到当前节点
	void		LoadData(DataType Data, int i, int j);//获得i,j位位的状态具体值
protected:
	Position	m_BlankPosition; //空白位位置	
};