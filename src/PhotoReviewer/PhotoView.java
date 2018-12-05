package PhotoReviewer;

import PhotoReviewer.Core.BaseView;
import PhotoReviewer.Core.ImageInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class PhotoView {
	protected JLayeredPane panel;
	protected JLabel       imageLabel;
	public    JLabel       textLabel;

	protected JFrame    frame;
	protected ImageInfo imageInfo;
	protected ImageIcon imageIcon;

	protected ImageIcon resizedIcon;

	public PhotoView() {
		/*panel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
			}
		});*/

		panel = new JLayeredPane();
		imageLabel = new JLabel();
		textLabel = new JLabel("IMAGE MOVED TEXT HERE");

		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		imageLabel.setVerticalAlignment(JLabel.CENTER);

		textLabel.setHorizontalAlignment(JLabel.CENTER);
		textLabel.setVerticalAlignment(JLabel.CENTER);
		textLabel.setOpaque(true);
		textLabel.setBackground(new Color(255, 255, 255, 175));
		textLabel.setForeground(new Color(0,0,0, 255));
		textLabel.setVisible(false);


		panel.add(imageLabel, 1);
		panel.add(textLabel, 0);
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;

		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				showImage();
			}
		});
	}

	public JLayeredPane getPanel() {
		return panel;
	}

	public void panelShowed() {

	}

	public void setImage(ImageInfo info) {
		imageInfo = info;
		imageIcon = new ImageIcon(imageInfo.path);
		showImage();
	}

	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	protected void showImage() {
		imageLabel.setSize(frame.getWidth(), frame.getHeight());

		int labelWidth  = 400;
		int labelHeight = 100;

		textLabel.setBounds((frame.getWidth() / 2) - (labelWidth / 2),frame.getHeight() - ((int) (labelHeight * 1.5)), labelWidth,labelHeight);

		if (imageIcon != null) {
			int maxWidth  = panel.getWidth();
			int maxHeight = panel.getHeight();

			textLabel.setVisible(false);

			if (imageIcon.getIconWidth() < maxWidth || imageIcon.getIconHeight() < maxHeight) {
				imageLabel.setIcon(imageIcon);
				resizedIcon = null;
			} else {
				resizedIcon = new ImageIcon(scaleImage(imageIcon, frame.getWidth(), frame.getHeight()));
				imageLabel.setIcon(resizedIcon);
			}
		}
	}

	protected Image scaleImage(ImageIcon icon, int maxWidth, int maxHeight) {
		Image image = icon.getImage();

		double imageHeight       = icon.getIconHeight();
		double imageWidth        = icon.getIconWidth();

		double targetWidth       = maxWidth;
		double targetHeight      = (imageHeight / imageWidth) * maxWidth;

		if (targetHeight > maxHeight) {
			targetHeight = maxHeight;
			targetWidth = (imageWidth / imageHeight) * maxHeight;
		}

		if (targetHeight == 0) {
			targetHeight = imageHeight;
		}

		if (targetWidth == 0) {
			targetWidth = imageWidth;
		}

		BufferedImage resizedImg = new BufferedImage((int) targetWidth, (int) targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D    g2         = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(image, 0, 0, (int) targetWidth, (int) targetHeight, null);
		g2.dispose();

		return resizedImg;
	}
}
