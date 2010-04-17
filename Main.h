/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 
@@ Main.h: interface for the CMain class.
@@ CMain类定义文件
@@ CMain类在本程序中主要用于问题的实现
@@ 作者：刘志良
@@ 最后修改日期：2003年12月8日
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
#include "Predefine.h"
#include "InitDialog.h"
#include "Display.h"
#include "InputAdvDlg.h"

class CMain  
{
public:
	CMain();//构造函数
	virtual ~CMain();//析构函数
public:
	List *		GetResultListPoint();//得到搜索树树根的指针
	BOOL		LoadData(BOOL Adv = false);//载入数据
private:
	DataType	m_Data[MaxItem][MaxItem];//源状态保存
	DataType	m_Desc[MaxItem][MaxItem];//目标状态保存
	List		m_DispList;//用于存放搜索树的链表
protected:
	BOOL		FindOtherNote(int CurrentG);//回溯时在其兄弟节点中找其它的节点
	void		IsReadyExist(List *pList);//是否已经存在 如果已经存在则设标记为已存在
	UINT		FindBestMoveFlag(List *pList);//寻找最佳移动方式 并移动之
	int			Scan(DataType Desc,Position pos);//扫描 计算H值的辅助函数
	int			CaculateH(CDisplay *Item);//计算H值
	void		GenerateMoveFlag();//穷举空白位的移动方式
	UINT		GenerateChild();//生成子节点
	int			m_iMoveFlagCount;//当前移动空白位的方式计数
	int			m_CurrentG;//当前G值
	BOOL		m_iMoveFlag[4];//移动方式表示
	CDisplay	*m_CurOpItem;//当前操作节点对象
};
