package me.nov.cafebabe.translations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alee.laf.WebLookAndFeel;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.gui.smalleditor.ChangelogPanel;
import me.nov.cafebabe.utils.io.Scanning;
import me.nov.cafebabe.utils.ui.WebLaF;

public class Translations {
	public static final HashMap<Integer, String> translations = new HashMap<>();
	public static final String language = System.getProperty("user.language");

	public static boolean translate = false;

	private static File translationsFile;

	public static String get(String i) {
		if (!translate) {
			return i;
		}
		if (translations.containsKey(i.hashCode())) {
			return translations.get(i.hashCode());
		}

		translations.put(i.hashCode(), i);
		return i;
	}

	public static void saveTranslations() {
		Properties properties = new Properties();
		for (Integer key : translations.keySet()) {
			properties.setProperty(String.valueOf(key), translations.get(key));
		}
		try {
			properties.store(new FileOutputStream(translationsFile), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadTranslations() {
		boolean needFontUpdate = false;
		String fileName = language + ".translation";
		translationsFile = new File(new File(Cafebabe.folder, "translations"), fileName);
		// TODO warn about old translation?
		Properties properties = new Properties();
		if (translationsFile.exists()) {
			try {
				properties.load(new FileInputStream(translationsFile));

				for (Object key : properties.keySet()) {
					String translation = String.valueOf(properties.get(key));
					if(!needFontUpdate && WebLookAndFeel.globalControlFont.canDisplayUpTo(translation) != -1) {
						needFontUpdate = true; //check for chinese or similar characters
					}
					translations.put(Integer.valueOf(String.valueOf(key)), translation);
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Files.write(translationsFile.toPath(),
						Scanning
								.readInputStream(
										ChangelogPanel.class.getResourceAsStream("/default_translations/" + fileName))
								.getBytes());
				loadTranslations();
			} catch (Exception e) {
				try {
					translationsFile.getParentFile().mkdirs();
					translationsFile.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		if(needFontUpdate) {
			WebLaF.fixUnicodeSupport();
		}
	}

	static {
		loadTranslations();
	}

}
