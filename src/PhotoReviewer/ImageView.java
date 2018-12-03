package PhotoReviewer;

import PhotoReviewer.Core.BaseView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

public class ImageView implements BaseView {
	private JPanel panel;
	private JLabel imageLabel;

	protected JFrame    frame;
	protected String    imagePath;
	protected File      imageFile;
	protected ImageIcon imageIcon;

	protected ImageIcon resizedIcon;

	public ImageView() {
		panel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
			}
		});
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

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public void panelShowed() {

	}

	public void setImage(String path) {
		imageIcon = new ImageIcon(path);
		showImage();
	}

	public void setImage(File file) {

	}

	protected void showImage() {
		if (imageIcon != null) {
			int maxWidth  = panel.getWidth();
			int maxHeight = panel.getHeight();

			if (imageIcon.getIconWidth() < maxWidth || imageIcon.getIconHeight() < maxHeight) {
				imageLabel.setIcon(imageIcon);
				resizedIcon = null;
			} else {
				resizedIcon = new ImageIcon(scaleImage(imageIcon, panel.getWidth(), panel.getHeight()));
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
