package me.nov.cafebabe.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;

import me.nov.cafebabe.Cafebabe;

/**
 * cafebabe最外层水平方向组件分割
 */
public class OuterSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;

	public OuterSplitPane(Component left, Component right) {
		// 方向,左组件,右组件
		super(JSplitPane.HORIZONTAL_SPLIT, left, right);
		// 斜角边框
		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setPreferredSize(new Dimension(this.getWidth(), 24));
		// 设置分割条的位置
		this.setDividerLocation((int) (Cafebabe.gui.getSize().getWidth() / 3.5));
	}
}
