package compilerUI;

/**
 *@author: 张玉明 2010.4
 */


import java.io.*;
import java.net.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.*;
import org.eclipse.jface.window.*;

import scanner.Scanner;
import parse.Parse;
import semantic.Semantic;
import midcode.MidCode;

public class Window extends ApplicationWindow {
	private StyledText text;

	private StyledText text1;
	
	private Action newCreate;

	private Action openFile;

	private Action saveFile;

	private Action saveAsFile;

	private Action exit;

	private Action copyFile;

	private Action pasteFile;

	private Action cutFile;

	private Action setFont;

	private Action setColor;

	private Action selectAll;

	private Action formate;
	
	private Action scanner;
	
	private Action parse;
	
	private Action semantic;
	
	private Action midcode;

	private Action about;

	private Font font;

	private File file;

	private Color color;

	private StyleRange style, range;

	boolean changes;;

	Window() {
		// 部署窗口
		super(null);
		newCreate = new NewCreateAction();
		openFile = new OpenFileAction();
		saveFile = new SaveFileAction();
		saveAsFile = new SaveAsFileAction();
		exit = new ExitAction();
		copyFile = new CopyFileAction();
		pasteFile = new PasteFileAction();
		cutFile = new CutFileAction();
		setFont = new SetFontAction();
		setColor = new SetColorAction();
		selectAll = new SelectAllAction();
		formate = new FormateAction();
		scanner = new ScannerAction();
		parse = new ParseAction();
		semantic = new SemanticAction();
		midcode = new MidCodeAction();
		about = new AboutAction();

		addMenuBar();
		addToolBar(SWT.FLAT);
	}

	public void run() {
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}

	public Control createContents(Composite parent) {
		// 设置窗体大小
		parent.getShell().setSize(700, 500);
		// 设置窗体标题
		parent.getShell().setText("SNL-Compiler");
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));   //容器的列数设置为2列
		text = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				changes = true;
			}
		});
		text1 = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		text1.setLayoutData(new GridData(GridData.FILL_BOTH));
		text1.setEditable(false);		//不可编辑
		return composite;
	}

	protected MenuManager createMenuManager() {
		MenuManager menuBar = new MenuManager();
		MenuManager fileMenu = new MenuManager("文件(&F)");
		MenuManager editorMenu = new MenuManager("编辑(&E)");
		MenuManager singleMenu = new MenuManager("编译(&S)");
		MenuManager helpMenu = new MenuManager("帮助(&H)");
		// 在文件菜单项添加下拉菜单
		fileMenu.add(newCreate);
		fileMenu.add(openFile);
		fileMenu.add(new Separator());
		fileMenu.add(saveFile);
		fileMenu.add(saveAsFile);
		fileMenu.add(new Separator());
		fileMenu.add(exit);
		// 在编辑菜单下添加下拉菜单
		editorMenu.add(copyFile);
		editorMenu.add(pasteFile);
		editorMenu.add(cutFile);
		editorMenu.add(new Separator());
		editorMenu.add(setFont);
		editorMenu.add(setColor);
		editorMenu.add(new Separator());
		editorMenu.add(selectAll);
		editorMenu.add(formate);
		singleMenu.add(scanner);
		singleMenu.add(parse);
		singleMenu.add(semantic);
		singleMenu.add(midcode);
		helpMenu.add(about);
		// 在menuBar上添加文件菜单、编辑菜单和帮助菜单
		menuBar.add(fileMenu);
		menuBar.add(editorMenu);
		menuBar.add(singleMenu);
		menuBar.add(helpMenu);
		return menuBar;
	}

	// 在工具栏上添加工具栏按钮
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		toolBarManager.add(new NewCreateAction());
		toolBarManager.add(new OpenFileAction());
		toolBarManager.add(new SaveFileAction());
		toolBarManager.add(new Separator());
		toolBarManager.add(new CopyFileAction());
		toolBarManager.add(new PasteFileAction());
		toolBarManager.add(new CutFileAction());
		toolBarManager.add(new Separator());
		toolBarManager.add(new BlodAction());
		toolBarManager.add(new ItalicAction());
		toolBarManager.add(new UnderlineAction());
		toolBarManager.add(new ScannerAction());
		return toolBarManager;
	}

	class NewCreateAction extends Action {
		public NewCreateAction() {
			super("NewCreateAction@Ctrl+N", Action.AS_PUSH_BUTTON);
			setText("新建");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/new.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			// 在新建文本之前，判断当前文本是否需要保存
			if (judgeTextSave()) {
				text.setText("");
			}
		}
	}

	class OpenFileAction extends Action {
		public OpenFileAction() {
			super("OpenFileAction@Ctrl+O", Action.AS_PUSH_BUTTON);
			setText("打开");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/open.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			// 在打开新的文件之前，判断是否保存当前文件
			if (judgeTextSave())
				OpenTextFile();
		}
	}
	
	class ScannerAction extends Action {
		public ScannerAction() {
			super("ScannerAction@Ctrl+M", Action.AS_PUSH_BUTTON);
			setText("词法分析");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/run.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			String s = text.getText();
			Scanner scan = new Scanner(s);
			s = scan.tokenlist;
			s = s.trim();
			int len = s.length();
			len = len - 11;
			String p = s.substring(0,len);
			text1.setText(p);
		}
	}

	class ParseAction extends Action {
		public ParseAction() {
			super("ParseAction@Ctrl+P", Action.AS_PUSH_BUTTON);
			setText("语法分析");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/run.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			String s = text.getText();
			Scanner scan = new Scanner(s);
			s = scan.tokenlist;
			s = s.trim();
		    Parse p = new Parse(s);
		    if(p.Error)
		    {
		    	text1.setText(p.serror);
		    }
		    else
		    {
		    	
		    	text1.setText(p.stree);
		    	p.printTreeEgg(s);
		    }
		}
	}
	
	class SemanticAction extends Action {
		public SemanticAction() {
			super("SemanticAction@Ctrl+Q", Action.AS_PUSH_BUTTON);
			setText("语义分析");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/run.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			String s = text.getText();
			Scanner scan = new Scanner(s);
			s = scan.tokenlist;
			s = s.trim();
		    Semantic sem = new Semantic(s);
		    if(sem.Error1)
		    {
		    	text1.setText(sem.serror);
		    }
		    else if(sem.Error)
		    {
		    	text1.setText(sem.yerror);
		    }
		    else
		    {
		    	text1.setText(sem.ytable);
		    }
		}
	}
	
	class MidCodeAction extends Action {
		public MidCodeAction() {
			super("MidCodeAction@Ctrl+L", Action.AS_PUSH_BUTTON);
			setText("中间代码");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/run.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			String s = text.getText();
			Scanner scan = new Scanner(s);
			s = scan.tokenlist;
			s = s.trim();
		    MidCode m = new MidCode(s);
		    if(m.Error1)
		    {
		    	text1.setText(m.serror);
		    }
		    else if(m.Error)
		    {
		    	text1.setText(m.yerror);
		    }
		    else
		    {
		    	text1.setText(m.midcode);
		    }
		}
	}
	
	
	class SaveFileAction extends Action {
		public SaveFileAction() {
			super("SaveFileAction@Ctrl+S", Action.AS_PUSH_BUTTON);
			setText("保存");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/save.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			saveTextFile();
		}
	}

	class SaveAsFileAction extends Action {
		public SaveAsFileAction() {
			super("另存为@Ctrl+A", Action.AS_PUSH_BUTTON);
		}

		public void run() {
			saveFileAs();
		}
	}

	class ExitAction extends Action {
		public ExitAction() {
			super("退出@Ctrl+E", Action.AS_PUSH_BUTTON);
		}

		public void run() {
			getShell().dispose();
		}
	}

	class CopyFileAction extends Action {
		public CopyFileAction() {
			super("CopyFileAction@Ctrl+C", Action.AS_PUSH_BUTTON);
			setText("复制");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/copy.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			text.copy();
		}
	}

	class PasteFileAction extends Action {
		public PasteFileAction() {
			super("PasteFileAction@Ctrl+V", Action.AS_PUSH_BUTTON);
			setText("粘贴");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/paste.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			text.paste();
		}
	}

	class CutFileAction extends Action {
		public CutFileAction() {
			super("CutFileAction @Ctrl+X", Action.AS_PUSH_BUTTON);
			setText("剪切");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/cut.gif"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			 text.cut();
		 
		}
	}

	class SetFontAction extends Action {
		public SetFontAction() {
			super("设置字体@Alt+F", Action.AS_PUSH_BUTTON);
		}

		public void run() {
			FontDialog fontDialog = new FontDialog(getShell());
			fontDialog.setFontList((text.getFont()).getFontData());
			FontData fontData = fontDialog.open();

			if (fontData != null) {
				if (font != null) {
					font.dispose();
				}
				font = new Font(getShell().getDisplay(), fontData);
				text.setFont(font);

			}
		}
	}

	class SetColorAction extends Action {
		public SetColorAction() {
			super("设置颜色@Alt+C", Action.AS_PUSH_BUTTON);
		}

		public void run() {
			// 定义颜色选择对话框
			ColorDialog dlg = new ColorDialog(getShell());
			// 打开对话框
			RGB rgb = dlg.open();
			if (rgb != null) {
				// 定义color对象
				color = new Color(getShell().getDisplay(), rgb);
				// 定义point对象，获取选择范围。
				Point point = text.getSelectionRange();
				for (int i = point.x; i < point.x + point.y; i++) {
					// 获得选中的字体样式和范围
					range = text.getStyleRangeAtOffset(i);
					// 如果字体设置了其它样式(如加粗、斜体、加下划线)
					if (range != null) {
				 		/**
						 * 设置一个与原来StyleRange的值相同的StyleRange
						 */
						 style = (StyleRange) range.clone();
						 style.start = i;
						 style.length = 1;
						// 设置前景颜色
						 style.foreground = color;
					} else {
						
						style = new StyleRange(i, 1, color, null, SWT.NORMAL);
					}
					text.setStyleRange(style);
				}

			}
		}
	}

	class SelectAllAction extends Action {
		public SelectAllAction() {
			super("全选@Alt+A", Action.AS_PUSH_BUTTON);
		}

		public void run() {
			text.selectAll();
			
		}
	}

	class FormateAction extends Action {
		public FormateAction() {
			super("格式化@Ctrl+W", Action.AS_CHECK_BOX);
		}

		public void run() {
			text.setWordWrap(isChecked());
		}
	}

	class AboutAction extends Action {
		public AboutAction() {
			super("关于@Ctrl+H", Action.AS_PUSH_BUTTON);
		}

		public void run() {
			MessageBox messageBox = new MessageBox(getShell(),
					SWT.ICON_INFORMATION | SWT.OK);
			messageBox.setMessage("SNL编译器1.0版本！\n开发者： 张玉明 孙大鹏 陈小健 李京");
			messageBox.open();
		}
	}

	class BlodAction extends Action {
		public BlodAction() {
			setText("加粗");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/blod.bmp"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			Point point= text.getSelectionRange();
			for (int i = point.x; i < point.x + point.y; i++) {
				StyleRange range = text.getStyleRangeAtOffset(i);
				if (range != null) {
					style = (StyleRange) range.clone();
					style.start = i;
					style.length = 1;
				} else {
					style = new StyleRange(i, 1, null, null, SWT.NORMAL);
				}
				
				//加粗字体
				style.fontStyle ^= SWT.BOLD;

				text.setStyleRange(style);

			}
		}

	}

	class ItalicAction extends Action {
		public ItalicAction() {
			setText("斜体");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/italic.bmp"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			
			Point point = text.getSelectionRange();

			for (int i = point.x; i < point.x + point.y; i++) {
			   range = text.getStyleRangeAtOffset(i);
				if (range != null) {
				style = (StyleRange) range.clone();
					style.start = i;
					style.length = 1;
				} else {
					style = new StyleRange(i, 1, null, null, SWT.NORMAL);
				}
				//设置为斜体
				style.fontStyle ^= SWT.ITALIC;

				text.setStyleRange(style);

			}
		}

	}

	class UnderlineAction extends Action {
		public UnderlineAction() {
			setText("下划线");
			try {
				// 载入图像
				ImageDescriptor icon = ImageDescriptor.createFromURL(new URL(
						"file:icons/underline.bmp"));
				setImageDescriptor(icon);
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			Point point = text.getSelectionRange();
			for (int i = point.x; i < point.x + point.y; i++) {
				 range = text.getStyleRangeAtOffset(i);
				if (range != null) {
					style = (StyleRange) range.clone();
					style.start = i;
					style.length = 1;
				} else {
					style = new StyleRange(i, 1, null, null, SWT.NORMAL);
				}
                //设置下划线
				style.underline = !style.underline;

				text.setStyleRange(style);

			}
		}

	}

	boolean judgeTextSave() {
		if (!changes)
			return true;
		MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING
				| SWT.YES | SWT.NO | SWT.CANCEL);
		messageBox.setMessage("是否保存对文件的更改？");
		messageBox.setText("SNL-Compiler");
		int message = messageBox.open();
		if (message == SWT.YES) {
			return saveTextFile();
		} else if (message == SWT.NO) {
			return true;
		} else {
			return false;
		}
	}

	boolean OpenTextFile() {
		// 定义对话框，类型为打开型
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		// 设置对话框打开的限定类型
		dialog.setFilterExtensions(new String[] { "*.txt", "*.*" });
		// 打开对话框，并返回打开文件的路径
		String openFile = dialog.open();
		if (openFile == null) {
			return false;
		}
		/**
		 * java.io包的File类专门处理文件，并获取文件相关信息。
		 * File类的构造方法： public File(String pathnames)
		 * 例如 ：File file=new(D:\my.java)
		 * public File(File parent,String child)
		 * 例如： File file=new(parent,"my.java")
		 * parent指文件所在目录的文件对象 
		 * public File(String parent,String child) 
		 * 例如:File file=new(dir,"my.java")
		 * dir指文件所在目录的字符串
		 */
		file = new File(openFile);
		try {
			/**
			 * FileReader文件字符流 
			 * 构造方法： public FileReader(File file)
			 * 例如：FileReader fileReade=new FileReader(file) 
			 * public FileReader(String filenames)
             * filenames为包含文件名的字符串
			 */
			FileReader fileReader = new FileReader(file);
			/**
			 * BufferedReader类用来把字符流的字符读入缓冲区
			 */
			BufferedReader reader = new BufferedReader(fileReader);
			/**
			 * 对Stringbuffer字符串缓冲实例化
			 */
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				/**
				 * 通过append()方法实现将字符串添加到字符缓冲区。 
				 * 也可以通过insert()方法将字符串插入缓冲区中
				 */
				sb.append(line);
				sb.append("\r\n");
			}
			text.setText(sb.toString());
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	boolean saveTextFile() {
		if (file == null) {
			// 定义文件选择对话框，类型为保存型
			FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setText("保存");
			// 设置对话框保存的限定类型
			dialog.setFilterExtensions(new String[] { "*.txt", "*.doc",
					"*.xls", "*.*" });
			// 打开对话框，并返回保存文件的路径
			String saveFile = dialog.open();
			if (saveFile == null) {
				return false;
			}
			file = new File(saveFile);
		}
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(text.getText());
			writer.close();
			changes = false;
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	boolean saveFileAs() {
		SafeSaveDialog dlg = new SafeSaveDialog(getShell());
		String temp = dlg.open();
		if (temp == null) {
			return false;
		}
		file = new File(temp);
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(text.getText());
			writer.close();
		} catch (IOException e) {
		}
		return false;
	}

	class SafeSaveDialog {
		private FileDialog dlg;

		public SafeSaveDialog(Shell shell) {
			dlg = new FileDialog(shell, SWT.SAVE);
			dlg.setFilterExtensions(new String[] { "*.txt", "*.doc", "*.xls",
					"*.*" });
		}

		public String open() {
			String fileName = null;
			boolean done = false;
			while (!done) {
				// 打开另存为对话框，并返回保存路径
				fileName = dlg.open();
				if (fileName == null) {
					done = true;
				} else {
					// 判断保存的文件是否已经存在
					File file = new File(fileName);
					if (file.exists()) {
						// 若文件存在，则弹出提示性的对话框
						MessageBox mb = new MessageBox(dlg.getParent(),
								SWT.ICON_WARNING | SWT.YES | SWT.NO);
						// 提示性的信息
						mb.setMessage(fileName + "已经存在，是否将该文件替换?");
						/**
						 * 单击“yes”按钮这将磁盘上的文件替换 
						 * 否则重新填写文件名
						 */
						done = mb.open() == SWT.YES;
					} else {
						done = true;
					}
				}
			}
			return fileName;
		}
	}

	public static void main(String[] args) {
		Window window = new Window();
		window.run();
	}
}
