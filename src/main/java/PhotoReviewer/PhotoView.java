package PhotoReviewer;

import PhotoReviewer.Core.BaseView;
import PhotoReviewer.Core.ImageInfo;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.rmi.server.ExportException;

public class PhotoView {
	public    JLabel       textLabel;
	protected JLayeredPane panel;
	protected JLabel       imageLabel;
	protected JFrame    frame;
	protected ImageInfo imageInfo;
	protected ImageIcon imageIcon;

	protected ImageIcon resizedIcon;

	public PhotoView() {

		panel = new JLayeredPane();
		imageLabel = new JLabel();
		textLabel = new JLabel("IMAGE MOVED TEXT HERE");

		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		imageLabel.setVerticalAlignment(JLabel.CENTER);

		textLabel.setHorizontalAlignment(JLabel.CENTER);
		textLabel.setVerticalAlignment(JLabel.CENTER);
		textLabel.setOpaque(true);
		textLabel.setBackground(new Color(255, 255, 255, 175));
		textLabel.setForeground(new Color(0, 0, 0, 255));
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

		textLabel.setBounds((frame.getWidth() / 2) - (labelWidth / 2), frame.getHeight() - ((int) (labelHeight * 1.5)), labelWidth, labelHeight);

		if (imageIcon != null) {
			int maxWidth  = panel.getWidth();
			int maxHeight = panel.getHeight();

			textLabel.setVisible(false);

			int degrees = readExifAndGetRotation();
			resizedIcon = new ImageIcon(rotateImage(imageIcon, degrees));

			resizedIcon = new ImageIcon(scaleImage(resizedIcon, frame.getWidth(), frame.getHeight()));
			imageLabel.setIcon(resizedIcon);
		}
	}

	protected int readExifAndGetRotation() {
		int degrees = 0;

		try {
			File     jpegFile = new File(imageInfo.path);
			Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

			Directory directory   = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			int       orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);

			switch (orientation) {
				case 6:
					degrees = 90;
					break;
				case 3:
					degrees = 180;
					break;
				case 8:
					degrees = -90;
					break;
			}
		} catch (Exception e) {

		}

		return degrees;
	}

	protected Image rotateImage(ImageIcon icon, int degrees) {
		int targetWidth = icon.getIconWidth(), targetHeight = icon.getIconHeight();

		switch (degrees) {
			case 90:
				targetWidth  = icon.getIconHeight();
				targetHeight = icon.getIconWidth();
				break;
			case -90:
				targetWidth  = icon.getIconHeight();
				targetHeight = icon.getIconWidth();
				break;
		}

		if (degrees != 0) {
			BufferedImage rotatedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);

			Graphics2D g2 = rotatedImage.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.rotate(Math.toRadians(degrees));

			g2.drawImage(icon.getImage(), 0, 0, targetWidth, targetHeight, null);
			g2.dispose();

			return rotatedImage;
		} else {
			return icon.getImage();
		}
	}

	protected Image scaleImage(ImageIcon icon, int maxWidth, int maxHeight) {
		Image image = icon.getImage();

		double imageHeight = icon.getIconHeight();
		double imageWidth  = icon.getIconWidth();

		double targetWidth  = maxWidth;
		double targetHeight = (imageHeight / imageWidth) * maxWidth;

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
