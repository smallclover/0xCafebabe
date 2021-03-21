package me.nov.cafebabe.gui.node;

import me.nov.cafebabe.setting.Setting;
import me.nov.cafebabe.utils.formatting.Colors;
import me.nov.cafebabe.utils.formatting.Html;

/**
 * 具体配置的包装层
 */
public class SettingNode {
	public String title;
	public String description;
	private String text;

	private Setting setting;// 具体的配置

	public SettingNode(Setting s) {
		this.setting = s;
		this.title = s.title;
		this.description = s.description;
		this.text = "<html>" + Html.bold(title) + "<br><font size=2>"
				+ Html.italics(Html.color(Colors.debug_grey, description));
	}

	@Override
	public String toString() {
		return text;
	}

	/**
	 * 用户的选择，是否启用配置
	 * @param b
	 */
	public void setUserSelected(boolean b) {
		setting.set(b);
	}
}
