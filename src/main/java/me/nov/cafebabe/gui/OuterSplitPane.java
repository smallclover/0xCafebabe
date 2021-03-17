package me.nov.cafebabe.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;

import me.nov.cafebabe.Cafebabe;

public class OuterSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;

	public OuterSplitPane(Component left, Component right) {
		super(JSplitPane.HORIZONTAL_SPLIT, left, right);

		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setPreferredSize(new Dimension(this.getWidth(), 24));
		this.setDividerLocation((int) (Cafebabe.gui.getSize().getWidth() / 3.5));
	}
}
