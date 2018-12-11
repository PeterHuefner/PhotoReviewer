package PhotoReviewer.Core;

import PhotoReviewer.WindowManager;
import jdk.nashorn.internal.scripts.JD;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileWorker extends SwingWorker<Boolean, String> {

	protected JList  sourceList;
	protected String action;
	protected File   destination;
	protected JDialog dialog;

	protected long allFilesSize = 0;
	protected long handledFilesSize = 0;

	public FileWorker(JList sourceList, String action, JDialog dialog, File destination) {
		this.sourceList = sourceList;
		this.action = action;
		this.destination = destination;
		this.dialog = dialog;
	}

	protected void collectAllFileSize() {
		for(int i = 0; i < sourceList.getModel().getSize(); i++) {
			ImageInfo imageInfo = (ImageInfo) sourceList.getModel().getElementAt(i);
			File file = new File(imageInfo.path);

			allFilesSize += file.length();
		}
	}

	@Override
	protected Boolean doInBackground() {

		Boolean overAllStatus = true;

		publish("collecting files");
		collectAllFileSize();

		String allFilesSizesInMB = String.format("%.2f", (double) allFilesSize / 1024 / 1024) + "MB";
		String actionName = "handled";

		switch (action) {
			case "copy" :
				actionName = "copied";
				break;
			case "move" :
				actionName = "moved";
				break;
			case "delete" :
				actionName = "deleted";
				break;
		}

		ArrayList<Integer> handledIndexes = new ArrayList<Integer>();

		for(int i = 0; i < sourceList.getModel().getSize(); i++) {
			ImageInfo imageInfo = (ImageInfo) sourceList.getModel().getElementAt(i);
			File file = new File(imageInfo.path);
			long thisFileSize = file.length();

			/*try {
				Thread.sleep(2000);
			}
			catch (Exception ex) {
				System.err.println(ex);
			}*/

			Boolean thisProcessStatus = true;

			switch (action) {
				case "copy" :

					try {
						FileChannel sourceChannel      = new FileInputStream(file).getChannel();
						FileChannel destinationChannel = new FileOutputStream(new File(destination.getPath() + File.separator + imageInfo.fileName)).getChannel();

						long sourceSize = sourceChannel.size();
						long transferredBytes = destinationChannel.transferFrom(sourceChannel, 0, sourceSize);

						sourceChannel.close();
						destinationChannel.close();

						if (transferredBytes != sourceSize) {
							thisProcessStatus = false;
						}

					} catch (Exception e) {
						thisProcessStatus = false;
					}

					break;
				case "move" :

					thisProcessStatus = file.renameTo(new File(destination.getPath() + File.separator + imageInfo.fileName));

					break;
				case "delete" :

					thisProcessStatus = file.delete();

					break;
			}

			handledFilesSize += thisFileSize;

			publish(actionName + " " + String.format("%.2f", (double) handledFilesSize / 1024 / 1024) + "MB of " + allFilesSizesInMB);

			if (!thisProcessStatus) {
				overAllStatus = false;
			} else {
				handledIndexes.add(i);
			}
		}

		if (action.equals("delete") || action.equals("move")) {
			DefaultListModel sourceModel = (DefaultListModel) sourceList.getModel();
			for (int j = handledIndexes.size() - 1; j >= 0; j--) {
				try {
					sourceModel.remove(j);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}

		return overAllStatus;
	}

	@Override
	protected void process(List<String> chunks) {
		super.process(chunks);

		if (dialog != null) {
			try {
				JOptionPane optionPane  = (JOptionPane) dialog.getContentPane();

				for (final String string : chunks) {
					optionPane.setMessage(string);
					dialog.pack();
				}

			} catch (Exception e) {

			}
		}


	}

	@Override
	protected void done() {
		super.done();

		try {
			dialog.dispose();
			Boolean status = get();

			String message;

			if (status) {
				message = "operation successful";
			} else {
				message = "operation not successful";
			}

			JOptionPane.showMessageDialog(WindowManager.mainFrame, message, "operation completed", JOptionPane.PLAIN_MESSAGE);

		} catch (Exception e) {

		}
	}
}
