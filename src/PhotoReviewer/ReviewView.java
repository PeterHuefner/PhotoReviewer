package PhotoReviewer;

import PhotoReviewer.Core.BaseView;
import PhotoReviewer.Core.FileWorker;
import PhotoReviewer.Core.ImageInfo;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.*;
import java.util.List;

public class ReviewView implements BaseView {


	private JPanel     panel;
	private JPanel     trashPanel;
	private JPanel     originPanel;
	private JPanel     favoritesPanel;
	private JPanel     controlPanel;
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
	private JButton    upButton;
	private JButton    downButton;
	private JButton    rightButton;
	private JButton    leftButton;
	private JLabel     moveToRightLabel;
	private JLabel     moveToLeftLabel;
	private JButton    helpButton;

	protected File directory;
	protected JFrame frame;

	protected PhotoView imageView = null;

	protected JList focusedList;

	protected LinkedHashMap<Integer, ImageInfo> allFiles;
	protected DefaultListModel                  trashModel;
	protected DefaultListModel                  originModel;
	protected DefaultListModel                  favoritesModel;

	protected int lastKey = 0;

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public void panelShowed() {
		if (allFiles.size() == 0) {
			JOptionPane.showMessageDialog(WindowManager.mainFrame, "No comatible images found in folder. Please choose another.", "folder seems empty", JOptionPane.PLAIN_MESSAGE);

			WindowManager.mainFrame.dispose();
			WindowManager.mainFrame.setExtendedState(JFrame.NORMAL);
			WindowManager.showPanel(new FolderSelectionView());
		}
	}

	public ReviewView(File directory, JFrame frame) {
		this.directory = directory;
		this.frame     = frame;

		allFiles = new LinkedHashMap<>();

		trashModel = new DefaultListModel();
		originModel = new DefaultListModel();
		favoritesModel = new DefaultListModel();

		trashFilesList.setModel(trashModel);
		originFilesList.setModel(originModel);
		favoritesFilesList.setModel(favoritesModel);

		readDirectory(directory);
		buildOriginList();

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {

					Component focusedComponent = frame.getMostRecentFocusOwner();
					if (focusedComponent != null) {
						if (focusedComponent instanceof JTextField) {
							return false;
						}
					}

					switch (e.getKeyCode()) {
						case 37 :
							if (imageView != null && !frame.isFocused()) {
								moveImageFromPhotoView("left");
							} else {
								move("left");
							}
							break;
						case 39 :
							if (imageView != null && !frame.isFocused()) {
								moveImageFromPhotoView("right");
							} else {
								move("right");
							}
							break;
						case 40 :
							if (!frame.isFocused()) {
								showNextImage();
							}
							break;
						case 38 :
							if (!frame.isFocused()) {
								showPreviousImage();
							}
							break;
						case 27 :
							WindowManager.disposeChildFrame();
							break;
						case 10 :
							if (focusedComponent instanceof JList) {
								showImage();
							}
							break;
					}
				}


				return false;
			}
		});

		//region events

		originFilesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				listSelectedValueChanged(originFilesList);
			}
		});

		trashFilesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				listSelectedValueChanged(trashFilesList);
			}
		});

		favoritesFilesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				listSelectedValueChanged(favoritesFilesList);
			}
		});

		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		rightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				move("right");
			}
		});

		leftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				move("left");
			}
		});

		WindowManager.mainFrame.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				if (imageView != null) {
					WindowManager.disposeChildFrame();
				}
			}
		});

		originFilesList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				focusedList = (JList) e.getComponent();
			}
		});
		originFilesList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				focusedList = (JList) e.getComponent();
			}
		});

		trashFilesList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				focusedList = (JList) e.getComponent();
			}
		});
		trashFilesList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				focusedList = (JList) e.getComponent();
			}
		});

		favoritesFilesList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				focusedList = (JList) e.getComponent();
			}
		});
		favoritesFilesList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				focusedList = (JList) e.getComponent();
			}
		});

		originFilesList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				if (e.getClickCount() == 2 && !e.isConsumed() && originFilesList.getSelectedValue() != null) {
					showImage();
				}
			}
		});

		trashFilesList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				if (e.getClickCount() == 2 && !e.isConsumed() && trashFilesList.getSelectedValue() != null) {
					showImage();
				}
			}
		});

		favoritesFilesList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				if (e.getClickCount() == 2 && !e.isConsumed() && favoritesFilesList.getSelectedValue() != null) {
					showImage();
				}
			}
		});

		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpView helpView = new HelpView();
				WindowManager.createChildFrame("Help");
				WindowManager.showPanelInFrame(helpView, WindowManager.childFrame);

			}
		});

		copyTrashButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyList(trashFilesList);
			}
		});

		moveTrashButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveList(trashFilesList);
			}
		});

		deleteTrashButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteList(trashFilesList);
			}
		});

		clearTrashButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearList(trashFilesList);
			}
		});

		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyList(favoritesFilesList);
			}
		});

		moveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveList(favoritesFilesList);
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteList(favoritesFilesList);
			}
		});

		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearList(favoritesFilesList);
			}
		});

		//endregion events
	}

	protected void listSelectedValueChanged(JList changedList) {
		/*moveToLeftLabel.setText("");
		moveToRightLabel.setText("");

		if (changedList == originFilesList) {
			moveToLeftLabel.setText("move to " + trashTextField.getText());
			moveToRightLabel.setText("move to " + favoritesTextField.getText());
		} else if (changedList == trashFilesList) {
			moveToRightLabel.setText("move to not reviewed");
		} else if (changedList == favoritesFilesList) {
			moveToLeftLabel.setText("move to not reviewed");
		}*/
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
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {

					ImageInfo info = new ImageInfo(lastKey);
					info.fileName = files[i].getName();
					info.path = files[i].getPath();

					allFiles.put(lastKey, info);
					lastKey++;

				} else if (files[i].isDirectory()) {
					//readDirectory(files[i]);
				}
			}
		}
	}

	protected void buildOriginList() {
		if (allFiles != null) {

			for(Map.Entry<Integer, ImageInfo> file: allFiles.entrySet()) {
				originModel.add(file.getKey(), file.getValue());
			}
		}
	}

	protected void determineFocusedList() {
		if (focusedList == null) {
			focusedList = originFilesList;
		}
	}

	protected Integer getSelectedIndexOfFocusedList() {
		determineFocusedList();

		if (focusedList.getModel().getSize() != 0) {
			if (focusedList.getSelectedValue() == null) {
				focusedList.setSelectedIndex(0);
			}

			return focusedList.getSelectedIndex();
		}

		return -1;
	}

	public boolean move(String direction) {
		boolean moved = false;
		determineFocusedList();
		if (focusedList.getModel().getSize() != 0) {

			if (focusedList.getSelectedValue() == null) {
				focusedList.setSelectedIndex(0);
			}

			moved = moveItem(focusedList, direction, focusedList.getSelectedIndex());
			//ImageInfo selectedInfo = (ImageInfo) focusedList.getSelectedValue();

		}

		return moved;
	}

	public boolean moveItem(ImageInfo info, String direction) {
		boolean moved = false;

		JList sourceList = getListForImageInfo(info);
		Integer index;

		if (sourceList != null) {
			index = ((DefaultListModel) sourceList.getModel()).indexOf(info);
			moved = moveItem(sourceList, direction, index);
		}

		return moved;
	}

	public boolean moveItem(JList sourceList, String direction, Integer listIndexToMove) {
		boolean moved = false;

		JList targetList = null;

		if (direction.equals("left")) {
			if (sourceList == originFilesList) {
				targetList = trashFilesList;
			} else if (sourceList == favoritesFilesList) {
				targetList = originFilesList;
			}
		} else {
			if (sourceList == trashFilesList) {
				targetList = originFilesList;
			} else if (sourceList == originFilesList) {
				targetList = favoritesFilesList;
			}
		}

		if (targetList != null && sourceList.getModel().getSize() > listIndexToMove) {

			DefaultListModel sourceModel = (DefaultListModel) sourceList.getModel();
			DefaultListModel targetModel = (DefaultListModel) targetList.getModel();

			try {
				ImageInfo info = (ImageInfo) sourceModel.remove(listIndexToMove);
				targetModel.addElement(info);
				moved = true;

				if (sourceModel.getSize() > listIndexToMove) {
					sourceList.setSelectedIndex(listIndexToMove);
				} else if (sourceModel.getSize() > 0) {
					sourceList.setSelectedIndex(0);
				}

				if (frame.isFocused()) {
					sourceList.requestFocusInWindow();
				}

			} catch (Exception e) {

			}
		}

		return moved;
	}

	protected JList getListForImageInfo (ImageInfo info) {
		JList sourceList = null;

		if (originModel.contains(info)) {
			sourceList = originFilesList;
		} else if (trashModel.contains(info)) {
			sourceList = trashFilesList;
		} else if (favoritesModel.contains(info)) {
			sourceList = favoritesFilesList;
		}

		return sourceList;
	}

	public void moveImageFromPhotoView(String direction) {
		ImageInfo currentImage = imageView.getImageInfo();
		JList currentList = getListForImageInfo(currentImage);
		Integer currentPosition = ((DefaultListModel) currentList.getModel()).indexOf(currentImage);

		moveItem(currentImage, direction);

		if (currentPosition >= 0) {
			currentList.setSelectedIndex(currentPosition);
			showImage();

			if (imageView != null) {

				JList targetList = getListForImageInfo(currentImage);
				String targetName = "not reviewed";

				if (targetList == trashFilesList) {
					targetName = trashTextField.getText();
				} else if (targetList == favoritesFilesList) {
					targetName = favoritesTextField.getText();
				}

				imageView.textLabel.setText(currentImage.fileName + " moved to " + targetName);
				imageView.textLabel.setVisible(true);
			}
		}
	}

	public void showImageFrame() {
		if (imageView == null) {
			imageView = new PhotoView();
		}

		if (WindowManager.childFrame == null) {
			WindowManager.createChildFrame("Image");
			imageView.setFrame(WindowManager.childFrame);
			WindowManager.maximizeFrame(WindowManager.childFrame);
			WindowManager.showPanelInFrame(imageView.getPanel(), WindowManager.childFrame);
			WindowManager.childFrame.requestFocusInWindow();
		} else if (!WindowManager.childFrame.isVisible()) {
			WindowManager.showPanelInFrame(imageView.getPanel(), WindowManager.childFrame);
			WindowManager.childFrame.requestFocusInWindow();
		} else if (!WindowManager.childFrame.isFocused()) {
			WindowManager.childFrame.requestFocusInWindow();
		}

	}

	public void showImage() {
		determineFocusedList();

		if (focusedList.getModel().getSize() > 0) {
			showImageFrame();
			getSelectedIndexOfFocusedList();

			ImageInfo info = (ImageInfo) focusedList.getSelectedValue();

			imageView.setImage(info);
			WindowManager.childFrame.setTitle(info.fileName);
		}
	}

	public void showNextImage() {
		determineFocusedList();

		if (focusedList.getModel().getSize() > 0) {
			showImageFrame();
			Integer selectedIndex = getSelectedIndexOfFocusedList();

			if (selectedIndex >= 0 && focusedList.getModel().getSize() > (selectedIndex + 1)) {
				focusedList.setSelectedIndex(selectedIndex + 1);
				showImage();
			}
		}
	}

	public void showPreviousImage() {
		determineFocusedList();

		if (focusedList.getModel().getSize() > 0) {
			showImageFrame();
			Integer selectedIndex = getSelectedIndexOfFocusedList();

			if (selectedIndex > 0) {
				focusedList.setSelectedIndex(selectedIndex - 1);
				showImage();
			}
		}
	}

	protected File getActionDestination() {
		File destination = null;

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("choose a target folder");

		if (chooser.showOpenDialog(WindowManager.mainFrame) == JFileChooser.APPROVE_OPTION) {
			destination = chooser.getSelectedFile();
		}

		return destination;
	}

	protected void handleListAction(JList sourceList, String action, String dialogTitle) {
		File destination = null;

		if (action.equals("copy") || action.equals("move")) {
			destination = getActionDestination();
		}

		if (destination != null || action.equals("delete")) {
			JOptionPane optionPane = new JOptionPane("collecting files", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
			JDialog dialog = new JDialog();

			dialog.setTitle(dialogTitle);
			dialog.setModal(true);
			dialog.setContentPane(optionPane);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.pack();


			FileWorker fileWorker = new FileWorker(sourceList, action, dialog, destination);
			fileWorker.execute();

			dialog.setVisible(true);
		}

	}

	public void copyList(JList sourceList) {
		handleListAction(sourceList, "copy", "copy files");
	}

	public void moveList(JList sourceList) {
		handleListAction(sourceList, "move", "move files");
	}

	public void deleteList(JList sourceList) {
		Integer userChoice = JOptionPane.showConfirmDialog(WindowManager.mainFrame, "Do you really want to delete all images in this list? This action can not be undone.", "confirm the removing of images", JOptionPane.YES_NO_OPTION);

		if (userChoice == JOptionPane.YES_OPTION) {
			handleListAction(sourceList, "delete", "delete files");
		}
	}

	public void clearList(JList sourceList) {

		int userChoice = JOptionPane.showOptionDialog(WindowManager.mainFrame, "Do you want entries moved back to 'not reviewed' or remove from list?\nRemoved entries are not accessible through application until you reopen and choose this folder again.", "move or remove entries", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"move", "remove", "cancel"}, "move");
		DefaultListModel sourceModel = (DefaultListModel) sourceList.getModel();

		switch (userChoice) {
			case 0 :
				String direction = "";

				if (sourceList == trashFilesList) {
					direction = "right";
				} else {
					direction = "left";
				}

				while (sourceModel.size() != 0) {
					moveItem(sourceList, direction, 0);
				}

				break;
			case 1 :
				sourceModel.removeAllElements();
				break;
			case 2 :

				break;
		}
	}
}
