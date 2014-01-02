package PresentationGui;

import java.awt.EventQueue;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Color;

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
			e.printStackTrace();
		}
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 20, 370, 221);
		getContentPane().add(scrollPane);
		
		JTextArea txtrPainRecognitionSystem = new JTextArea();
		txtrPainRecognitionSystem.setForeground(new Color(102, 0, 0));
		txtrPainRecognitionSystem.setFont(new Font("Consolas", Font.BOLD, 12));
		txtrPainRecognitionSystem.setText("Pain Recognition System\r\nVersion 1.0\r\n\r\nFinal Project Ort Braude College Of Engineering\r\nBy Arbili Eliran & Gaon Arie\r\n\r\n\r\n");
		txtrPainRecognitionSystem.setEditable(false);
		scrollPane.setViewportView(txtrPainRecognitionSystem);

	}
}
