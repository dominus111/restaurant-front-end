package comp1206.sushi;
import javax.swing.*;

import comp1206.sushi.mock.MockServer;
import comp1206.sushi.server.ServerWindow;

public class ServerApplication {

	public static void main(String[] argv) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> new ServerWindow(new MockServer()));
	}
}

