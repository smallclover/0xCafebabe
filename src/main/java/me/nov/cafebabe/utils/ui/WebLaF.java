package me.nov.cafebabe.utils.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import com.alee.api.data.BoxOrientation;
import com.alee.extended.image.WebImage;
import com.alee.extended.overlay.AlignedOverlay;
import com.alee.extended.overlay.WebOverlay;
import com.alee.extended.panel.GroupPanel;
//import com.alee.extended.panel.WebOverlay;
import com.alee.laf.WebLookAndFeel;
//import com.alee.managers.language.data.TooltipWay;
import com.alee.laf.label.WebLabel;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;

public class WebLaF {

	private static ImageIcon info;
	static {
		info = new ImageIcon(Toolkit.getDefaultToolkit().getImage(WebLaF.class.getResource("/overlay/info.png")));
	}

	public static GroupPanel createInfoLabel(JLabel nameLabel, String overlayText) {
		// overlay
		WebOverlay overlayPanel = new WebOverlay();
		overlayPanel.setContent(nameLabel);

		// overlay tooltip
		WebLabel overlay = new WebLabel(info);
		overlay.setToolTip(overlayText, TooltipWay.trailing, 0);
		overlayPanel.addOverlay(
				new AlignedOverlay(overlay, BoxOrientation.right, BoxOrientation.top,
						new Insets(0,0,0,overlay.getPreferredSize().width)));
		return new GroupPanel(overlayPanel);
	}

	public static JSeparator createSeparator() {
		JSeparator sep = new JSeparator();
		sep.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		sep.setPreferredSize(new Dimension(5, 2));
		return sep;
	}
	
  public static void fixUnicodeSupport() {
    WebLookAndFeel.globalControlFont = fixFont(WebLookAndFeel.globalControlFont);
    WebLookAndFeel.globalTooltipFont = fixFont(WebLookAndFeel.globalTooltipFont);
//    WebLookAndFeel.globalAlertFont = fixFont(WebLookAndFeel.globalAlertFont);
    WebLookAndFeel.globalMenuFont = fixFont(WebLookAndFeel.globalMenuFont);
//    WebLookAndFeel.globalAcceleratorFont = fixFont(WebLookAndFeel.globalAcceleratorFont);
//    WebLookAndFeel.globalTitleFont = fixFont(WebLookAndFeel.globalTitleFont);
    WebLookAndFeel.globalTextFont = fixFont(WebLookAndFeel.globalTextFont);
  }

  private static Font fixFont(Font font) {
    return new Font(Font.SANS_SERIF, font.getStyle(), font.getSize());
  }
}
