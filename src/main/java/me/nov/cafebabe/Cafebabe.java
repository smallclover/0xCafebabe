package me.nov.cafebabe;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.window.WebFrame;
import me.nov.cafebabe.gui.*;
import me.nov.cafebabe.gui.editor.Editor;
import me.nov.cafebabe.gui.preferences.PreferencesDialog;
import me.nov.cafebabe.gui.smalleditor.ChangelogPanel;
import me.nov.cafebabe.gui.translations.TranslationEditor;
import me.nov.cafebabe.gui.ui.MethodListCellRenderer;
import me.nov.cafebabe.setting.Settings;
import me.nov.cafebabe.translations.Translations;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

/**
 * cafebabe主窗口(JFrame)，程序入口
 */
public class Cafebabe extends WebFrame {
	private static final long serialVersionUID = 1L;
	public static final String title = "Cafebabe Editor Lite";
	public static final String version = "0.1.2";
	// 当前类的对象
	public static Cafebabe gui;
	public static File folder;

	private ClassTree tree;
	private ClassMemberList methods;
	public JScrollPane smallEditorPanel;
	public Editor editorFrame;

	public Cafebabe() {
		gui = this;
		setTitle(title + " " + version);
		initBounds();
		// 默认的关闭操作
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// 关闭操作绑定的事件
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				// 关闭窗口操作提示
				// 默认的父窗口即使Cafebabe
				if (JOptionPane.showConfirmDialog(Cafebabe.this, Translations.get("Do you really want to exit?"),
						Translations.get("Confirm"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					// 选择【是】的场合关闭程序
					Runtime.getRuntime().exit(0);
				}
			}
		});
		// 是否总是在其他程序的窗口之前（没有必要一直都在之前）
		this.setAlwaysOnTop(false);
		// 构造一个新的边框布局
		setLayout(new BorderLayout());
		this.setJMenuBar(initMenuBar());
		// 变更日志面板
		this.smallEditorPanel = new JScrollPane(new ChangelogPanel());
		this.smallEditorPanel.getVerticalScrollBar().setUnitIncrement(16);
		this.methods = new ClassMemberList();
		this.tree = new ClassTree(methods);
		// 左: tree 右上: methods 右下: smallEditorPanel
		this.add(new OuterSplitPane(new JScrollPane(tree), new InnerSplitPane(new JScrollPane(methods), smallEditorPanel)),
				BorderLayout.CENTER);
		// 底栏
		HelpBar hb = new HelpBar();
		this.add(hb, BorderLayout.SOUTH);
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent e) {
				if (e instanceof MouseEvent) {
					MouseEvent me = (MouseEvent) e;
					if (me.getComponent() instanceof JComponent) {
						JComponent jc = (JComponent) me.getComponent();
						String ttt = jc.getToolTipText();
						if (ttt != null && ttt.trim().length() > 0) {
							hb.setText(jc.getToolTipText());
						} else {
							hb.resetText();
						}
					}
				}
			}
		}, AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK);

//		this.setRound(5);
//		this.setShadeWidth(20);
//		this.setShowResizeCorner(false);
		// 图标
		this.setIconImage(
				Toolkit.getDefaultToolkit().getImage(MethodListCellRenderer.class.getResource("/icon.png")));
	}

	/**
	 * 初始化菜单栏
	 * @return
	 */
	private JMenuBar initMenuBar() {
		WebMenuBar bar = new WebMenuBar();
		// 暂时看不出有什么作用，至少视觉效果是一致的（注释前后）
//		bar.setUndecorated(false);
//		bar.setMenuBarStyle(MenuBarStyle.attached);

		// 一级菜单File
		WebMenu file = new WebMenu(Translations.get("File"));
		// 二级菜单Open jar file
		WebMenuItem load = new WebMenuItem(Translations.get("Open jar file"));
		load.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		// 文件选择窗口
		load.addActionListener(l -> {
			// 使用指定的的File作为路径来创建JFileChooser
			JFileChooser jfc = new JFileChooser(new File(System.getProperty("user.home") + File.separator + "Desktop"));
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setFileFilter(new FileNameExtensionFilter("Java Package (*.jar)", "jar"));
			// 打开文件对话框，同意按钮是打开(Open)
			int result = jfc.showOpenDialog(this);
			// 打开的情况下
			if (result == JFileChooser.APPROVE_OPTION) {
				File input = jfc.getSelectedFile();// 当前用户选择的文件
				tree.onJarLoad(-1, input);
			}
		});


		// 二级菜单Save jar file
		WebMenuItem save = new WebMenuItem(Translations.get("Save jar file"));
		save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		save.addActionListener(l -> {
			if (tree.inputFile == null)
				return;
			JFileChooser jfc = new JFileChooser(tree.inputFile.getParentFile());
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setSelectedFile(tree.inputFile);
			jfc.setDialogTitle(Translations.get("Save jar file"));

			// 设置文件过滤器，过滤jar
			jfc.setFileFilter(new FileNameExtensionFilter("Java Package (*.jar)", "jar"));
			// 打开文件对话框，同意按钮是打开(Save)
			int result = jfc.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File output = jfc.getSelectedFile();
				tree.saveJar(output);
			}
		});

		file.add(load);
		file.add(save);
		bar.add(file);

		// 一级菜单Preferences
		WebMenu preferences = new WebMenu(Translations.get("Preferences"));

		// 二级菜单Edit preferences...
		WebMenuItem editPrefs = new WebMenuItem(Translations.get("Edit preferences..."));
		editPrefs.addActionListener(l -> {
			new PreferencesDialog();
		});
		preferences.add(editPrefs);

		// 二级菜单Translation editor...
		WebMenuItem tranlations = new WebMenuItem(Translations.get("Translation editor..."));
		tranlations.addActionListener(l -> {
			new TranslationEditor();
		});
		preferences.add(tranlations);
		bar.add(preferences);


		return bar;
	}

	/**
	 * 初始化主窗口
	 */
	private void initBounds() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (screenSize.width * 0.3515625); // 675
		int height = (int) (screenSize.height * 0.833333333); // 900

		int x = (int) (screenSize.width * 0.013); // 25
		setBounds(x, screenSize.height / 2 - height / 2, width, height);
	}

	public void openEditor(Component c, String title, Color color, Icon icon) {
		if (editorFrame == null) {
			editorFrame = new Editor();
		}
		if (!editorFrame.isShowing()) {
			editorFrame.setVisible(true);
		}
		editorFrame.open(c, title, icon, color);
	}

	public static boolean decorated = true;
	public static void main(String[] args) throws Exception {
		try {
			// C:\Users\xxxx 当前用户目录
			folder = new File(System.getProperty("user.home"), ".cafebabe");
			if (!folder.exists()) {
				folder.mkdir();
			}
			for (LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) {
				if (lafi.getName().equals("Nimbus")) {
					UIManager.setLookAndFeel(lafi.getClassName());
					break;
				}
			}
			new Translations(); //load translations
			// 1.2.8字体是硬编码，所以需要替换设置
			// WebLookAndFeel.globalControlFont = new FontUIResource("隶书", 0, 12);
			WebLookAndFeel.install();
			System.setProperty("file.encoding", "UTF-8");
			Field charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null, null);
			Settings.loadSettings();
			// Swing的核心是LookAndFeel Laf
//			WebLookAndFeel.setDecorateFrames(decorated);
//			WebLookAndFeel.setDecorateDialogs(decorated);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		new Cafebabe();
		gui.setVisible(true);
	}
}
