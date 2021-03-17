package me.nov.cafebabe.gui.smalleditor;

import javax.swing.JEditorPane;
import javax.swing.border.EmptyBorder;

import me.nov.cafebabe.utils.io.Scanning;

/**
 * 变更日志面板
 */
public class ChangelogPanel extends JEditorPane {
	private static final long serialVersionUID = 1L;

	public ChangelogPanel() {
		// 内容类型
		this.setContentType("text/html");
		// 不可编辑
		this.setEditable(false);
		// 文本内容
		this.setText(Scanning.readInputStream(ChangelogPanel.class.getResourceAsStream("/changelog.txt")));
		//
		this.setFocusable(false);
		//
		this.setOpaque(true);
		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		setBorder(new EmptyBorder(16, 16, 16, 16));
	}
}
