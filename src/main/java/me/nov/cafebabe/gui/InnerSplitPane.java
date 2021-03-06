package me.nov.cafebabe.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;

import me.nov.cafebabe.Cafebabe;

/**
 * cafebabe 右边竖直方向组件分割
 */
public class InnerSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;

	public InnerSplitPane(Component above, Component below) {
		super(JSplitPane.VERTICAL_SPLIT, above, below);
		// Border边框
		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setPreferredSize(new Dimension(this.getWidth(), 24));
		this.setDividerLocation((int) (Cafebabe.gui.getSize().getHeight() / 3));
	}
}
