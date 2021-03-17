package me.nov.cafebabe.gui.preferences;

import javax.swing.JScrollPane;

import org.objectweb.asm.Opcodes;

import com.alee.laf.tabbedpane.WebTabbedPane;

import me.nov.cafebabe.gui.preferences.list.SettingList;
import me.nov.cafebabe.setting.Setting;
import me.nov.cafebabe.setting.Settings;
import me.nov.cafebabe.translations.Translations;

public class PreferencesPane extends WebTabbedPane implements Opcodes {
	private static final long serialVersionUID = 1L;

	public PreferencesPane() throws Exception {
		// 设置选项卡模式
		this.setTabPlacement(WebTabbedPane.LEFT);
		for (String key : Settings.settings.keySet()) {
			// 选项卡标题
			// 该选项卡关联的组件
			this.addTab(Translations.get(key + " Settings"),
					new JScrollPane(new SettingList(Settings.settings.get(key))));
		}
		// About选项卡
		this.addTab(Translations.get("About"), new JScrollPane(new AboutPanel()));
	}
}