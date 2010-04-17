/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 
@@ Main.h: interface for the CMain class.
@@ CMain�ඨ���ļ�
@@ CMain���ڱ���������Ҫ���������ʵ��
@@ ���ߣ���־��
@@ ����޸����ڣ�2003��12��8��
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
#include "Predefine.h"
#include "InitDialog.h"
#include "Display.h"
#include "InputAdvDlg.h"

class CMain  
{
public:
	CMain();//���캯��
	virtual ~CMain();//��������
public:
	List *		GetResultListPoint();//�õ�������������ָ��
	BOOL		LoadData(BOOL Adv = false);//��������
private:
	DataType	m_Data[MaxItem][MaxItem];//Դ״̬����
	DataType	m_Desc[MaxItem][MaxItem];//Ŀ��״̬����
	List		m_DispList;//���ڴ��������������
protected:
	BOOL		FindOtherNote(int CurrentG);//����ʱ�����ֵܽڵ����������Ľڵ�
	void		IsReadyExist(List *pList);//�Ƿ��Ѿ����� ����Ѿ�����������Ϊ�Ѵ���
	UINT		FindBestMoveFlag(List *pList);//Ѱ������ƶ���ʽ ���ƶ�֮
	int			Scan(DataType Desc,Position pos);//ɨ�� ����Hֵ�ĸ�������
	int			CaculateH(CDisplay *Item);//����Hֵ
	void		GenerateMoveFlag();//��ٿհ�λ���ƶ���ʽ
	UINT		GenerateChild();//�����ӽڵ�
	int			m_iMoveFlagCount;//��ǰ�ƶ��հ�λ�ķ�ʽ����
	int			m_CurrentG;//��ǰGֵ
	BOOL		m_iMoveFlag[4];//�ƶ���ʽ��ʾ
	CDisplay	*m_CurOpItem;//��ǰ�����ڵ����
};
