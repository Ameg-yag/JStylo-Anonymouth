package edu.drexel.psal.GUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.drexel.psal.JSANConstants;

/**
 *	JStylo / Anonymouth GUI Chooser.
 *
 * @author Ariel Stolerman
 */
public class JSANGUIChooser extends javax.swing.JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6938223250770015159L;
	private JLabel jstyloLogo;
	private JLabel anonLogo;
	private JLabel anonJLabel;
	private JButton anonJButton;
	private JButton jstyloJButton;
	private JLabel jstyloJLabel;
	
	private static JSANGUIChooser guiChooser;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e) {
					System.err.println("Look-and-Feel error! (Not on Windows)");
				}
				guiChooser = new JSANGUIChooser();
				guiChooser.setLocationRelativeTo(null);
				guiChooser.setDefaultCloseOperation(EXIT_ON_CLOSE);
				guiChooser.setVisible(true);
			}
		});
	}
	
	public JSANGUIChooser() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setResizable(false);
			setTitle("JStylo / Anonymouth GUI Chooser");
			getContentPane().setLayout(null);
			// JStylo
			ImageIcon jstyloImage = new ImageIcon(Thread.currentThread().getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"jstylo_gui_chooser.png"));
			{
				jstyloLogo = new JLabel(jstyloImage);
				getContentPane().add(jstyloLogo);
				jstyloLogo.setBounds(10, 10, 80, 80);
			}
			
			// Anonymouth
			ImageIcon anonImage = new ImageIcon(Thread.currentThread().getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"anonymouth_gui_chooser_v2.png"));
			{
				anonLogo = new JLabel();
				getContentPane().add(anonLogo);
				anonLogo.setIcon(anonImage);
				anonLogo.setBounds(10, 101, 80, 80);
			}
			{
				jstyloJLabel = new JLabel();
				getContentPane().add(jstyloJLabel);
				jstyloJLabel.setText("<html><b>JStylo</b><br>Authorship Recognition Analysis Tool</html>");
				jstyloJLabel.setBounds(100, 11, 202, 47);
			}
			{
				jstyloJButton = new JButton();
				getContentPane().add(jstyloJButton);
				jstyloJButton.setText("Launch JStylo");
				jstyloJButton.setBounds(100, 58, 202, 32);
				jstyloJButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// launch JStylo
						edu.drexel.psal.jstylo.GUI.GUIMain.main(null);
						guiChooser.dispose();
					}
				});
			}
			{
				anonJLabel = new JLabel();
				getContentPane().add(anonJLabel);
				anonJLabel.setText("<html><b>Anonymouth</b><br>Authorship Recognition Evasion Tool</html>");
				anonJLabel.setBounds(100, 101, 202, 47);
			}
			{
				anonJButton = new JButton();
				getContentPane().add(anonJButton);
				anonJButton.setText("Launch Anonymouth");
				anonJButton.setBounds(100, 148, 202, 33);
				anonJButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// launch JStylo
						edu.drexel.psal.anonymouth.gooie.ThePresident.main(null);
						guiChooser.dispose();
					}
				});
			}
			this.setSize(330, 230);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
