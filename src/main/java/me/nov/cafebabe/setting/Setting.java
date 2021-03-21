package me.nov.cafebabe.setting;

import java.lang.reflect.Field;

import me.nov.cafebabe.translations.Translations;
import me.nov.cafebabe.utils.interfaces.BooleanAction;

/**
 * 设置项实体类
 */
public class Setting {
	public String id;
	public String title;
	public String description;
	public Field field;
	public boolean defaultValue;// 决定配置是否开启，一部分配置默认不开启
	private BooleanAction updateAction; // 修改配置后执行的操作，重启应用GUI

	public Setting(String id, String title, String description, Field field, boolean defaultValue,
			BooleanAction updateAction) {
		super();
		this.id = id;
		this.title = Translations.get(title);
		this.description = Translations.get(description);
		this.field = field;
		this.defaultValue = defaultValue;
		this.updateAction = updateAction;
	}

	/**
	 * 初始化的时候设置该属性是否配置
	 * true：配置
	 * false：不配置
	 * @param b
	 */
	public void setInitial(boolean b) {
		try {
			field.setBoolean(null, b);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 配置发生改变时更新配置文件，重启应用GUI
	 * @param b
	 */
	public void set(boolean b) {
		try {
			field.setBoolean(null, b);
			Settings.saveProperties();
			new Thread(() -> {
				try {
					// call later to update ui
					Thread.sleep(500);
					if (updateAction != null) {
						updateAction.action(b);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean get() {
		try {
			return field.getBoolean(null);
		} catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

}
