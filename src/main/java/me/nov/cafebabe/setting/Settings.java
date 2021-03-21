package me.nov.cafebabe.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.decompiler.CFR;
import me.nov.cafebabe.gui.ClassTree;
import me.nov.cafebabe.translations.Translations;

/**
 * preferences -> edit preferences 的设置项
 */
public class Settings {
	public static File propertiesFile;
	public static Properties properties;

	public static final HashMap<String, List<Setting>> settings = new HashMap<>();

	public static void loadSettings() throws Exception {
		loadDefaultSettings();
		initProperties();
	}

	/**
	 * 载入默认的设置
	 * 配置名称：可配置项
	 * @throws NoSuchFieldException
	 */
	private static void loadDefaultSettings() throws NoSuchFieldException {

		// General Setting
		settings
				.put("General",
						Arrays.asList(new Setting("translate", "Translations",
								"Use translations stored in %userprofile%/.cafebabe/translations",
								Translations.class.getDeclaredField("translate"), false, (b) -> {
							restartGUI();
						})));
		settings.put("GUI", Arrays.asList(new Setting("decorated", "Decorate Frame",
				"Use WebLaF-styled frames and dialogues", Cafebabe.class.getDeclaredField("decorated"), true, (b) -> {
			restartGUI();
		})));

		// ASM  Setting
		settings.put("ASM",
				Arrays.asList(new Setting("frames", "Regenerate Frames", "Regenerate frames using known commons determined at loading",
						ClassTree.class.getDeclaredField("useFrameRegeneration"), true, null)));
		// CFR  Setting
		settings.put("CFR", Arrays.asList(
				new Setting("stringbuilders", "Decompile StringBuilder / Buffer",
						"Decompile StringBuilder and Buffer back to default concat", CFR.class.getDeclaredField("stringBuilders"),
						true, null),
				new Setting("stringswitches", "Decompile String switch", "Decompile hashCode switches back to original",
						CFR.class.getDeclaredField("stringSwitches"), true, null),
				new Setting("trywith", "Reconstruct try-with", "Reconstruct try-with-resources",
						CFR.class.getDeclaredField("tryWith"), true, null),
				new Setting("lambdas", "Decompile lambdas", "Decompile Java 8 lambdas", CFR.class.getDeclaredField("lambdas"),
						true, null),
				new Setting("finally", "Decompile finally", "Decompile finally blocks", CFR.class.getDeclaredField("finallies"),
						true, null),
				new Setting("hidelongstr", "Hide long strings", "Hide long strings",
						CFR.class.getDeclaredField("hideLongStrings"), true, null),
				new Setting("hideutf8", "Hide UTF-8", "Convert UTF-8 to \\u codes", CFR.class.getDeclaredField("hideUTF8"),
						true, null),
				new Setting("removesynth", "Remove synthetic methods", "Remove methods with synthetic access",
						CFR.class.getDeclaredField("removeSynthetic"), false, null),
				new Setting("commentmonitors", "Comment monitors", "Comment monitorenter and monitorexit",
						CFR.class.getDeclaredField("commentMonitors"), false, null),
				new Setting("topsort", "Force basic block sorting", "Force basic block sorting (for heavy obfuscation)",
						CFR.class.getDeclaredField("topsort"), true, null),
				new Setting("ignoreexcpetions", "Drop exception information", "Drop exception information (changes semantics)",
						CFR.class.getDeclaredField("ignoreExcpetions"), false, null)));
	}

	/**
	 * 设置变更后重新启动GUI Action
	 */
	private static void restartGUI() {
		if (JOptionPane.showConfirmDialog(Cafebabe.gui,
				Translations.get("Do you want to restart now? Everything unsaved will be lost!"), Translations.get("Confirm"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			Cafebabe.gui.dispose();// 释放所有资源
			try {
				Thread.sleep(200);
				Cafebabe.main(new String[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 保存修改的配置，只有修改过配置，才会在默认的文件夹下生产默认的配置文件
	 */
	public static void saveProperties() {
		try {
			for (String key : settings.keySet()) {
				List<Setting> values = settings.get(key);
				for (Setting s : values) {
					properties.setProperty(key.toLowerCase() + "_" + s.id, String.valueOf(s.get()));
				}
			}
			properties.store(new FileOutputStream(propertiesFile), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 尝试从配置文件中读取配置项，如果没有配置文件则按照默认配置进行设置
	 */
	private static void initProperties() {
		// init folders and stuff
		// 在默认的文件夹下创建cafebabe配置文件
		propertiesFile = new File(Cafebabe.folder, "cafebabe.properties");

		properties = new Properties();
		// 如果配置文件存在，则从配置文件中加载数据
		if (propertiesFile.exists()) {
			try {
				properties.load(new FileInputStream(propertiesFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (String key : settings.keySet()) {
			List<Setting> values = settings.get(key);
			for (Setting s : values) {
				s.setInitial((boolean) Boolean
						// for example:
						// general_translate
						// 如果配置文件中存在的时候，取得的是字符串类型的布尔值，如果不存在的情况下就是布尔值
						.parseBoolean(properties.getOrDefault(key.toLowerCase() + "_" + s.id, s.defaultValue).toString()));
			}
		}
	}
}
