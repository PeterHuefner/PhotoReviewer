package PhotoReviewer;

import PhotoReviewer.Core.BaseView;

import javax.swing.*;
import java.awt.*;

public class WindowManager {

	public static JFrame mainFrame;
	public static JFrame childFrame;

	public static JPanel mainPanel;

	public static void initialize(BaseView mainView) {

		if (WindowManager.mainPanel == null) {
			WindowManager.mainPanel = mainPanel;
			WindowManager.mainFrame = new JFrame(Application.name + " - " + Application.version);

			WindowManager.mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			//maximizeFrame(WindowManager.mainFrame);
			showPanel(mainView);
		}

	}

	public static void showPanel(BaseView panel) {
		showPanelInFrame(panel, WindowManager.mainFrame);
	}

	public static void showPanelInFrame(BaseView panel, JFrame frame) {
		showPanelInFrame(panel.getPanel(), frame);
		panel.panelShowed();
	}

	public static void showPanelInFrame(Container panel, JFrame frame) {
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	public static void maximizeFrame(JFrame frame) {
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	public static void createChildFrame(String title) {
		disposeChildFrame();

		childFrame = new JFrame(title);
		childFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public static void disposeChildFrame() {
		if (childFrame != null) {
			childFrame.dispose();
		}
		childFrame = null;
	}
}
