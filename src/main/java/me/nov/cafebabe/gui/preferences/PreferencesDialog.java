package me.nov.cafebabe.gui.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.alee.laf.window.WebDialog;
import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.translations.Translations;

/**
 * 偏好设置弹窗
 */
public class PreferencesDialog extends WebDialog {
	private static final long serialVersionUID = 1L;

	public PreferencesDialog() {
		super(Cafebabe.gui, true);
//		this.setRound(5);
//		this.setShadeWidth(20);
//		this.setShowResizeCorner(false);
		this.initBounds();
		this.setTitle(Translations.get("Preferences"));
		this.setIconImage(Cafebabe.gui.getIconImage());
		// 默认关闭事件
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		try {
			// 默认位置（Center） 选项卡切换和内容
			this.add(new PreferencesPane());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// 南部 close按钮
		JPanel buttons = new JPanel(new FlowLayout(4));
		this.add(buttons, BorderLayout.SOUTH);

		JButton close = new JButton(Translations.get("Close"));
		buttons.add(close);
		close.addActionListener(e -> {
			setVisible(false);
		});
		// 设置组件的位置，getParent使它的位置相对父亲窗口位置
		setLocationRelativeTo(getParent());
		this.setVisible(true);
	}

	private void initBounds() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (screenSize.width * 0.25);
		int height = (int) (screenSize.height * 0.5);

		setBounds(screenSize.width / 2 - width / 2, screenSize.height / 2 - height / 2, width, height);
	}
}
