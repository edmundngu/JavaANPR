package javaanpr;
import javax.swing.UIManager;

import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;
import javaanpr.gui.windows.FrameComponentInit;
import javaanpr.gui.windows.FrameMain;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		FrameComponentInit frameComponentInit = new FrameComponentInit(); // show wait
		frameComponentInit.dispose();
		FrameMain mainFrame = new FrameMain();
	}

}
