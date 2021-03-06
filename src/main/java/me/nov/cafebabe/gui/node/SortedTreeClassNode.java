package me.nov.cafebabe.gui.node;

import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.objectweb.asm.tree.ClassNode;

import me.nov.cafebabe.utils.formatting.EscapedString;

public class SortedTreeClassNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	private ClassNode cn;
	private EscapedString text;

	public SortedTreeClassNode(ClassNode cn) {
		this.cn = cn;
		setClassName();
	}

	public SortedTreeClassNode(String path) {
		this.cn = null;
		this.text = new EscapedString(path);
	}

	private void setClassName() {
		if (cn != null) {
			String[] split = cn.name.split("/");
			this.text = new EscapedString(split[split.length - 1]);
		}
	}

	public ClassNode getClazz() {
		return cn;
	}

	public void setClazz(ClassNode c) {
		this.cn = c;
	}

	@SuppressWarnings("unchecked")
	public void sort() {
		if (children != null){
			children.sort(comparator());
		}
	}

	private Comparator<TreeNode> comparator() {
		return new Comparator<>() {
			@Override
			public int compare(TreeNode node1, TreeNode node2) {
				SortedTreeClassNode sortedNode1 = (SortedTreeClassNode)node1;
				SortedTreeClassNode sortedNode2 = (SortedTreeClassNode)node2;
				boolean leaf1 = sortedNode1.cn != null;
				boolean leaf2 = sortedNode2.cn != null;

				if (leaf1 && !leaf2) {
					return 1;
				}
				if (!leaf1 && leaf2) {
					return -1;
				}
				return sortedNode1.getText().compareTo(sortedNode2.getText());
			}
		};
	}

	public String getText() {
		return text.getText();
	}

	@Override
	public String toString() {
		return text.getEscapedText();
	}
}