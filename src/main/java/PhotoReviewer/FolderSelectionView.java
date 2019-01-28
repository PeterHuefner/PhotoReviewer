package PhotoReviewer;

import PhotoReviewer.Core.BaseView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FolderSelectionView implements BaseView {
	private JPanel  panel;
	private JButton chooseFolderButton;
	private JLabel  welcomeLabel;
	private JButton showHelp;

	public JPanel getPanel() {
		return panel;
	}

	public FolderSelectionView() {

		chooseFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("Choose a Folder");

				if (chooser.showOpenDialog(WindowManager.mainFrame) == JFileChooser.APPROVE_OPTION) {
					File directory = chooser.getSelectedFile();

					ReviewView reviewView = new ReviewView(directory, WindowManager.mainFrame);

					WindowManager.maximizeFrame(WindowManager.mainFrame);
					WindowManager.showPanelInFrame(reviewView, WindowManager.mainFrame);
				}
			}
		});

		showHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpView helpView = new HelpView();
				WindowManager.createChildFrame("Help");
				WindowManager.showPanelInFrame(helpView, WindowManager.childFrame);
			}
		});
	}

	public void panelShowed() {

		/*ImageView imageView = new ImageView();

		WindowManager.createChildFrame("Image");
		WindowManager.maximizeFrame(WindowManager.childFrame);
		WindowManager.showPanelInFrame(imageView, WindowManager.childFrame);

		imageView.setFrame(WindowManager.childFrame);
		imageView.setImage("/Users/peterhufner/Pictures/Backgrounds/IMG_4126.JPG");*/

	}
}
