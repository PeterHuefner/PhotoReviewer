package PhotoReviewer;

import javax.swing.*;

public class Application {

	final public static String name = "PhotoReviewer";
	final public static String version = "1.0.1";

	protected static FolderSelectionView folderSelectionView;

	public static void main(String[] args) {

		Application.folderSelectionView = new FolderSelectionView();
		WindowManager.initialize(Application.folderSelectionView);
	}
}
