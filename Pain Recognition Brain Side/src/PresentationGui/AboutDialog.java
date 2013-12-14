package PresentationGui;

import java.awt.EventQueue;

import javax.swing.JDialog;
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
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 414, 240);
		getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

	}
}
