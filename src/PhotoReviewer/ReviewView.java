package PhotoReviewer;

import PhotoReviewer.Core.BaseView;
import PhotoReviewer.Core.ImageInfo;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ContainerAdapter;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReviewView implements BaseView {


	private JPanel     panel;
	private JPanel     trashPanel;
	private JPanel     originPanel;
	private JPanel     favoritesPanel;
	private JPanel     controlPanel;
	private JPanel     topPanel;
	private JList      originFilesList;
	private JList      trashFilesList;
	private JList      favoritesFilesList;
	private JTextField favoritesTextField;
	private JTextField trashTextField;
	private JButton    copyButton;
	private JButton    moveButton;
	private JButton    deleteButton;
	private JButton    clearButton;
	private JButton    copyTrashButton;
	private JButton    moveTrashButton;
	private JButton    deleteTrashButton;
	private JButton    clearTrashButton;

	protected File directory;

	protected LinkedHashMap<Integer, ImageInfo> allFiles;
	protected int lastKey = 0;

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public void panelShowed() {



	}

	public ReviewView(File directory) {
		this.directory = directory;
		allFiles = new LinkedHashMap<>();
		readDirectory(directory);
		buildOriginList();

		originFilesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				originFilesList.getSelectedValue();
			}
		});
	}

	protected void readDirectory(File folder) {

		FilenameFilter filenameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String lowerCase = name.toLowerCase();
				if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png")) {
					return true;
				} else {
					return false;
				}
			}
		};

		File[] files = folder.listFiles(filenameFilter);

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {

					ImageInfo info = new ImageInfo(lastKey);
					info.fileName = files[i].getName();
					info.path     = files[i].getPath();

					allFiles.put(lastKey, info);
					lastKey++;

				} else if (files[i].isDirectory()) {
					readDirectory(files[i]);
				}
			}
		}
	}

	protected void buildOriginList() {
		originFilesList.clearSelection();
		if (allFiles != null) {
			ImageInfo[] images = new ImageInfo[allFiles.size()];
			int i = 0;
			for(ImageInfo info: allFiles.values()) {
				images[i] = info;
				i++;
			}
			originFilesList.setListData(images);
		}
	}
}
