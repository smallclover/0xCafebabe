package me.nov.cafebabe.gui.smalleditor;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.alee.extended.panel.GroupPanel;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import com.alee.laf.button.WebToggleButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.text.WebTextField;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.gui.ClassTree;
import me.nov.cafebabe.gui.decompiler.DecompilerPanel;
import me.nov.cafebabe.gui.node.SortedTreeClassNode;
import me.nov.cafebabe.gui.ui.MethodListCellRenderer;
import me.nov.cafebabe.translations.Translations;
import me.nov.cafebabe.utils.formatting.Colors;
import me.nov.cafebabe.utils.formatting.EscapedString;
import me.nov.cafebabe.utils.ui.Listeners;
import me.nov.cafebabe.utils.ui.WebLaF;

/**
 * 点击jar包中具体的类，在右下角显示该类可以编辑的信息
 * 位置：右下
 * 类编辑面板
 */
public class ClassEditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	// 当前选择的类对象
	private ClassNode clazz;
	private WebTextField name;
	private GroupPanel access;
	private WebComboBox version;
	private WebTextField superName;
	private WebTextField signature;
	private WebTextField sourceFile;
	private WebCheckBox hasSignature;
	private WebTextField interfaces;

	public ClassEditorPanel(ClassTree classTree) {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
		this.setFocusable(false);


		// name栏
		// name文本框
		name = new WebTextField(20);
		// 绑定修改事件
		Listeners.addChangeListener(name, l -> {
			String trim = name.getText().trim();
			if (!trim.isEmpty())
				// 反应修改的内容
				clazz.name = trim;
		});
		// name标签
		JLabel nameLabel = new JLabel(Translations.get("Name:"));
		nameLabel.setDisplayedMnemonic('N');
		nameLabel.setLabelFor(name);


		// access栏
		// access按钮组
		access = accessToggleGroup();
		Listeners.addMouseReleasedListener(access, () -> {
			int acc = clazz.access; // do not lose old access
			for (Component c : access.getComponents()) {
				WebToggleButton tb = (WebToggleButton) c;
				try {
					int accessInt = Opcodes.class.getField("ACC_" + tb.getToolTipText().toUpperCase()).getInt(null);
					if (tb.isSelected()) {
						acc |= accessInt;
					} else {
						acc &= ~accessInt;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			clazz.access = acc;
		}, true);
		// access标签
		JLabel accessLabel = new JLabel(Translations.get("Access:"));
		accessLabel.setDisplayedMnemonic('A');
		accessLabel.setLabelFor(access);


		// version栏
		// version选择框
		version = new WebComboBox(new Integer[] { 49, 50, 51, 52, 53, 54, 55, 56, 57, 58 });
		version.addActionListener(l -> {
			clazz.version = (int) version.getSelectedItem();
		});
		JLabel versionLabel = new JLabel(Translations.get("Version:"));
		versionLabel.setDisplayedMnemonic('V');
		versionLabel.setLabelFor(version);

		// parent栏
		superName = new WebTextField(20);
		Listeners.addChangeListener(superName, l -> {
			if (superName.getText().isEmpty()) {
				clazz.superName = null;
			} else {
				clazz.superName = superName.getText().trim();
			}
		});
		JLabel superNameLabel = new JLabel(Translations.get("Parent:"));
		superNameLabel.setDisplayedMnemonic('P');
		superNameLabel.setLabelFor(superName);


		// 文本框里放选择框
		signature = new WebTextField(20);
		signature.setMargin(0, 2, 0, 0);
		hasSignature = new WebCheckBox();
		hasSignature.setCursor(Cursor.getDefaultCursor());
		hasSignature.setSelected(false);
		hasSignature.setFocusable(false);
		signature.setLeadingComponent(hasSignature);

		Listeners.addChangeListener(signature, l -> {
			if (hasSignature.isSelected()) {
				clazz.signature = signature.getText().trim();
			} else {
				clazz.signature = null;
			}
		});
		hasSignature.addActionListener(l -> {
			if (hasSignature.isSelected()) {
				clazz.signature = signature.getText().trim();
			} else {
				clazz.signature = null;
			}
		});

		// signature栏
		JLabel signatureLabel = new JLabel(Translations.get("Signature:"));
		signatureLabel.setDisplayedMnemonic('S');
		signatureLabel.setLabelFor(signature);

		// 显示源代码文件名栏
		sourceFile = new WebTextField(20);
		sourceFile.setToolTipText(Translations.get("Original name"));
		Listeners.addChangeListener(sourceFile, l -> {
			String sf = sourceFile.getText();
			if (sf.isEmpty()) {
				clazz.sourceFile = null;
			} else {
				clazz.sourceFile = sf;
			}
		});

		JLabel sourceFileLabel = new JLabel(Translations.get("Source file:"));
		sourceFileLabel.setDisplayedMnemonic('S');
		sourceFileLabel.setLabelFor(sourceFile);


		// Interfaces 栏
		interfaces = new WebTextField(20);
		Listeners.addChangeListener(interfaces, l -> {
			String itfs = interfaces.getText();
			itfs = itfs.replace(" ", ""); // spaces don't matter
			String[] itf = itfs.split(",");

			clazz.interfaces = new ArrayList<>();
			for (String itfc : itf) {
				clazz.interfaces.add(itfc);
			}
		});

		JLabel itfLabel = new JLabel(Translations.get("Interfaces:"));
		itfLabel.setDisplayedMnemonic('I');
		itfLabel.setLabelFor(interfaces);

		JButton decompile = new JButton(Translations.get("Decompile"));
		decompile.addActionListener(l -> {
			SortedTreeClassNode treeNode = (SortedTreeClassNode) classTree.getLastSelectedPathComponent();
			Icon icon = ((JLabel) classTree.getCellRenderer().getTreeCellRendererComponent(classTree, treeNode, false, false,
					true, 0, false)).getIcon();
			Cafebabe.gui.openEditor(new DecompilerPanel(treeNode.getClazz(), null), "CFR: " + new EscapedString(treeNode.getClazz().name, 60, false).getEscapedText(),
					Colors.decompilerTabColor, icon);
		});
		decompile.setPreferredSize(new Dimension(100, (int) decompile.getPreferredSize().getHeight()));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 6, 0, 0);
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = 0;

		// name
		// name标签
		this.add(nameLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		// name文本框
		this.add(name, gbc);

		// 分割线
		gbc.gridy++;
		this.add(WebLaF.createSeparator(), gbc);

		//access
		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(accessLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(access, gbc);

		// 分割线
		gbc.gridy++;
		this.add(WebLaF.createSeparator(), gbc);

		// version
		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(versionLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(version, gbc);

		// parent
		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(superNameLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		this.add(superName, gbc);


		// 分割线
		gbc.gridy++;
		this.add(WebLaF.createSeparator(), gbc);


		// signature
		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(signatureLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		this.add(signature, gbc);


		// sourceFile
		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(sourceFileLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		this.add(sourceFile, gbc);

		// 分割线
		gbc.gridy++;
		this.add(WebLaF.createSeparator(), gbc);

		// interfaces
		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(WebLaF.createInfoLabel(itfLabel, Translations.get("Separated by a comma")), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		this.add(interfaces, gbc);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		gbc.gridwidth = 1;
		gbc.gridy = 0;
		JLabel placeholder = new JLabel();
		placeholder.setPreferredSize(new Dimension(100, (int) placeholder.getPreferredSize().getHeight()));
		JLabel placeholder2 = new JLabel();
		placeholder2.setPreferredSize(new Dimension(100, (int) placeholder.getPreferredSize().getHeight()));
		
		buttonPanel.add(placeholder, gbc);
		buttonPanel.add(placeholder2, gbc);
		buttonPanel.add(decompile, gbc);
		this.add(buttonPanel, new GridBagConstraints(1, 11, 4, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));

		this.add(Box.createGlue(), new GridBagConstraints(0, 12, 4, 1, 0, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

	}

	private GroupPanel accessToggleGroup() {
		// do not change tooltips
		WebToggleButton private_ = new WebToggleButton(MethodListCellRenderer.pri);
		private_.setToolTipText("private");

		WebToggleButton public_ = new WebToggleButton(MethodListCellRenderer.pub);
		public_.setToolTipText("public");

		WebToggleButton protected_ = new WebToggleButton(MethodListCellRenderer.pro);
		protected_.setToolTipText("protected");

		WebToggleButton abstract_ = new WebToggleButton(MethodListCellRenderer.abs);
		abstract_.setToolTipText("abstract");

		WebToggleButton final_ = new WebToggleButton(MethodListCellRenderer.fin);
		final_.setToolTipText("final");

		WebToggleButton synthetic_ = new WebToggleButton(MethodListCellRenderer.synth);
		synthetic_.setToolTipText("synthetic");

		private_.addActionListener(l -> {
			public_.setSelected(false);
			protected_.setSelected(false);
		});
		public_.addActionListener(l -> {
			protected_.setSelected(false);
			private_.setSelected(false);
		});
		protected_.addActionListener(l -> {
			public_.setSelected(false);
			private_.setSelected(false);
		});

		GroupPanel accessGroup = new GroupPanel(true, private_, public_, protected_, abstract_, final_,
				synthetic_);
		return accessGroup;
	}

	/**
	 * 向组件中填充Class的数据
	 * @param clazz
	 */
	public void editClass(ClassNode clazz) {
		// 当前选择的类对象
		this.clazz = clazz;
		// 当前类的名字
		this.name.setText(clazz.name);

		// 获取所有的ACC_开头的访问标识符
		List<Field> accessList = Arrays.stream(Opcodes.class.getDeclaredFields())
				.filter( field -> field.getName().startsWith("ACC_"))
				.collect(Collectors.toList());

		for (Field f : accessList) {
			try {
					int acc = f.getInt(null);
					String accName = f.getName().substring(4).toLowerCase();
					for (Component c : access.getComponents()) {
						WebToggleButton tb = (WebToggleButton) c;
						if (tb.getToolTipText().equals(accName)) {
							// 跟Opcodes的access做与运算，判断access中是否包含指定的访问标识符
							tb.setSelected((clazz.access & acc) != 0);
							break;
						}
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.superName.setText(clazz.superName);

		this.sourceFile.setText(clazz.sourceFile);

		this.version.setSelectedItem(clazz.version);

		if (clazz.signature != null) {
			hasSignature.setSelected(true);
			this.signature.setText(clazz.signature);
		} else {
			hasSignature.setSelected(false);
			this.signature.setText("");
		}

		this.interfaces.setText(String.join(",", clazz.interfaces));

	}

}
