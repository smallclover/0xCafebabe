package me.nov.cafebabe.gui.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import me.nov.cafebabe.gui.node.SortedTreeClassNode;
import me.nov.cafebabe.utils.asm.Access;
import me.nov.cafebabe.utils.ui.Images;

/**
 * 扩展默认的树单元的渲染器
 * 窗口左侧jar包展开后的图标文字渲染
 */
public class ClassTreeCellRenderer extends DefaultTreeCellRenderer implements Opcodes {
	private static final long serialVersionUID = 1L;

	private ImageIcon pack, clazz, enu, itf;

	public ClassTreeCellRenderer() {
		// 图标

		// 包
		this.pack = new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/tree/package.png")));
		// 类
		this.clazz = new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/classtype/class.png")));
		// 枚举类
		this.enu = Images.combine(clazz, new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/classtype/enum.png"))));
		// 接口
		this.itf = Images.combine(clazz, new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/classtype/interface.png"))));
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		// 执行父类的默认的节点绘制操作
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		// 这里转化如果不是DefaultMutableTreeNode的父类或者子类就会报错，应该是WebLaf的继承关系变更导致
//		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (value instanceof SortedTreeClassNode) {
			SortedTreeClassNode stn = (SortedTreeClassNode) value;
			ClassNode cn = stn.getClazz();
			if (cn != null) {
				if (Access.isInterface(cn.access)) {
					this.setIcon(this.itf);
				} else if (Access.isEnum(cn.access)) {
					this.setIcon(this.enu);
				} else {
					this.setIcon(this.clazz);
				}
			} else {
				this.setIcon(this.pack);
			}
		}
		return this;
	}

	@Override
	public Font getFont() {
		return new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	}
}
