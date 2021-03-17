package me.nov.cafebabe.gui.preferences;

import javax.swing.JEditorPane;
import javax.swing.border.EmptyBorder;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.utils.io.Scanning;

/**
 * preferences -> edit preference -> about
 * 关于
 */
public class AboutPanel extends JEditorPane {
	private static final long serialVersionUID = 1L;
	//  JEditorPane 只支持HTML3.2或者更早的格式
	public AboutPanel() {
		// 内容格式
		this.setContentType("text/html");
		// 不可编辑
		this.setEditable(false);
		String license = Scanning.readInputStream(AboutPanel.class.getResourceAsStream("/license.txt")).replace("\n", "<br>");
		this.setText(String.format(Scanning.readInputStream(AboutPanel.class.getResourceAsStream("/about.txt")), Cafebabe.title, Cafebabe.version) + license);
		this.setFocusable(false);
		this.setOpaque(true);
		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		setBorder(new EmptyBorder(16, 16, 16, 16));
	}
}
