/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 
@@ Display.h: interface for the CDisplay class.
@@ CDisplay�ඨ���ļ�
@@ CDisplay���ڱ���������Ҫ����״̬������
@@ ���ߣ���־��
@@ ����޸����ڣ�2003��12��8��
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

#include "predefine.h"

class CDisplay  
{
public:
	CDisplay();//���캯��
	virtual ~CDisplay();//��������
private:
	void		FindBlankPosition();//Ѱ�ҿհ�λλ��
private:
	int			m_CurrentG;//�ڵ�Gֵ
	int			m_CurrentCount;//���ļ��� ������ʾ
	DataType	m_DispData[MaxItem][MaxItem];//�ڵ�״̬
	BOOL		m_bAnswer;//�Ƿ�����
	UINT		m_NoteType;//�ڵ�����
public:
	int			GetCurrentCount();//ȡ�ýڵ����ֵ
	void		SetCurrentCount(int Count);//���ýڵ����ֵ
	BOOL		GetIsAAnswer();//�ýڵ��Ƿ��ǽ�
	int			GetCurrentG();//ȡ�õ�ǰGֵ
	void		SetCurrentG(int CurG);//���õ�ǰ�ڵ�Gֵ
	void		SetNoteType(UINT noteType);//���ýڵ�����
	UINT		GetNoteType();//ȡ�ýڵ�����
	void		SetThisIsAAnswer();//���øýڵ�������
	BOOL		IsEqual(CDisplay *Item);//�ж�Item�ڵ��Ƿ��뵱ǰ�ڵ����
	DataType	GetDispData(int i,int j);//ȡ�ýڵ�״̬��ʾ
	Position	GetBlankPosition();//ȡ�ÿհ�λλ��
	void		MoveBlank(UINT MoveDirection);//����MoveDirection�ƶ���ǰ״̬�ڵĿհ�λ
	void		LoadData(CDisplay *Item);//��Item�ڵ���Copy״ֵ̬����ǰ�ڵ�
	void		LoadData(DataType Data, int i, int j);//���i,jλλ��״̬����ֵ
protected:
	Position	m_BlankPosition; //�հ�λλ��	
};