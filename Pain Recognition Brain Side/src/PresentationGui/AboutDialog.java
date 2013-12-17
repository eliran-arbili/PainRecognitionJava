package PresentationGui;

import java.awt.EventQueue;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AboutDialog dialog = new AboutDialog();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		JPanel backgroundPanel;
		try {
			backgroundPanel = new BackgroundPanel(ImageIO.read(this.getClass().getClassLoader().getResource("resources/background.png")));
			setContentPane(backgroundPanel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		ImageIcon iconBackground=new ImageIcon(this.getClass().getClassLoader().getResource("resources/aboutBG.jpg"));
		//paneBackground.setIcon(iconBackground);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 20, 370, 221);
		getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

	}
}
