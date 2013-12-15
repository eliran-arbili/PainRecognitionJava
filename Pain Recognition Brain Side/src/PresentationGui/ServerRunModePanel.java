package PresentationGui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import businessLogic.CBRController;
import businessLogic.casesServer.Server;
import dataLayer.ProjectConfig;

@SuppressWarnings("serial")
public class ServerRunModePanel extends BackgroundPanel {

	
	/*
	 * Instance variables
	 */
	private Server painRecognitionServer;
	private PainMeasureGui painGui;
	
	/**
	 * Create the panel.
	 */
	public ServerRunModePanel(Image image) {
		super(image);
		initialize();
	}
	
	private void initialize()
	{
		btnStartStop 			= new JButton();
		btnStartStop.setFont(new Font("Arial", Font.BOLD, 14));
		btnStartStop.setBounds(310, 403, 100, 34);
		txtFldPort			= new JTextField();
		txtFldPort.setFont(new Font("Arial", Font.PLAIN, 12));
		txtFldPort.setBounds(160, 80, 36, 20);
		txtFldKCases		= new JTextField();
		txtFldKCases.setFont(new Font("Arial", Font.PLAIN, 12));
		txtFldKCases.setBounds(160, 194, 36, 20);
		cmboxTags			= new JComboBox<File>();
		cmboxTags.setFont(new Font("Arial", Font.PLAIN, 11));
		cmboxTags.setBounds(160, 132, 399, 30);
		chkboxFuzzyMode		= new JCheckBox();
		chkboxFuzzyMode.setBounds(164, 254, 20, 20);
		lblTags				= new JLabel();
		lblTags.setFont(new Font("Arial", Font.BOLD, 14));
		lblTags.setBounds(24, 139, 134, 17);
		lblPort				= new JLabel();
		lblPort.setFont(new Font("Arial", Font.BOLD, 14));
		lblPort.setBounds(24, 75, 134, 30);
		lblKCases			= new JLabel();
		lblKCases.setFont(new Font("Arial", Font.BOLD, 14));
		lblKCases.setBounds(24, 196, 134, 17);
		lblServerStatus 	= new JLabel();
		lblServerStatus.setBounds(250, 399, 50, 40);
		lblFuzzyMode		= new JLabel();
		lblFuzzyMode.setFont(new Font("Arial", Font.BOLD, 14));
		lblFuzzyMode.setBounds(24, 257, 134, 17);
			

		for(File tag: ProjectConfig.getTrainingTags()){
			cmboxTags.addItem(tag);
		}
		
		btnStartStop.setText("Start");
		btnStartStop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				btnStartStopOnClick(arg0);
			}
		});
		

		
		lblTags.setText("Tag:");
		lblPort.setText("Port:");
		lblKCases.setText("K Similar Cases:");
		lblServerStatus.setForeground(Color.RED);
		lblServerStatus.setText("OFF");
		lblFuzzyMode.setText("Fuzzy Mode:");
		lblServerStatus.setFont(new Font("Arial",1,18));
		txtFldPort.setText(ProjectConfig.getOpt("SERVER_PORT"));
		txtFldKCases.setText(ProjectConfig.getOpt("K_SIMILAR_CASES"));
		setLayout(null);
		add(lblPort);
		add(lblTags);
		add(lblKCases);
		add(lblFuzzyMode);
		add(txtFldPort);
		add(cmboxTags);
		add(txtFldKCases);
		add(chkboxFuzzyMode);
		add(btnStartStop);
		add(lblServerStatus);
	}
	
	/*
	 * Member functions
	 */
	
	public void refreshTags() {
		cmboxTags.removeAllItems();
		for(File tag: ProjectConfig.getTrainingTags()){
			cmboxTags.addItem(tag);
		}
	}
	
	public void closeServer() throws IOException{
		if(painRecognitionServer != null){
			painRecognitionServer.close();
		}
	}
	
	/*
	 * Components callback's
	 */

	protected void btnStartStopOnClick(ActionEvent arg0) {
		if(btnStartStop.getText().equalsIgnoreCase("Start")){
			String portTxt		= txtFldPort.getText();
			String kCasesTxt 	= txtFldKCases.getText();
			if(! isFieldsOK(portTxt,kCasesTxt)){
				return;
			}
			File trainingTag = (File)cmboxTags.getSelectedItem();
			if(trainingTag == null){
				JOptionPane.showMessageDialog(this, "Must Choose Tag, Please Do Training","Operation Couldn't Be Completed", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			String annPath	= ProjectConfig.getANNFileByTag(trainingTag).getAbsolutePath();
			String csvPath = ProjectConfig.getCSVByTag(trainingTag).getAbsolutePath();
			ProjectConfig.setOpt("SERVER_PORT", portTxt);
			ProjectConfig.setOpt("K_SIMILAR_CASES", kCasesTxt);
			ProjectConfig.setOpt("ANN_PARAMETERS_PATH", annPath);
			ProjectConfig.setOpt("CSV_CASES_PATH", csvPath);
			ProjectConfig.setOpt("FUZZY_MODE", String.valueOf(chkboxFuzzyMode.isSelected()));
			
			if(painRecognitionServer == null){
				painRecognitionServer = new Server(ProjectConfig.getOptInt("SERVER_PORT"));
				painGui = new PainMeasureGui(painRecognitionServer);
				painRecognitionServer.addObserver(painGui);
			}
			else{
				if(! ProjectConfig.getCurrentTag().equals(trainingTag)) {
					painRecognitionServer.setPainCBR(new CBRController());
				}
			}
			try {
				painRecognitionServer.listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ProjectConfig.setCurrentTag(trainingTag);
			painGui.openWindow();
			lblServerStatus.setText("ON");
			lblServerStatus.setForeground(Color.GREEN);
			btnStartStop.setText("Stop");
		}

		else{
			lblServerStatus.setText("OFF");
			lblServerStatus.setForeground(Color.RED);
			btnStartStop.setText("Start");
			painGui.closeWindow();
		}
	}
	
	
	/*
	 * Auxiliary methods
	 */
	private boolean isFieldsOK(String... args)
	{
		
		for (String arg : args) {
	        if(arg.isEmpty()){
	        	JOptionPane.showMessageDialog(this, "Please fill empty fields", "Operation couldn't be completed", JOptionPane.INFORMATION_MESSAGE);
				return false;
	        }
	    }
		return true;
	}
	private JButton 		 	btnStartStop;
	private JTextField 		 	txtFldPort;
	private JTextField 			txtFldKCases;
	private JComboBox<File> 	cmboxTags;
	private JCheckBox			chkboxFuzzyMode;
	private JLabel 			 	lblTags;
	private JLabel 				lblPort;
	private JLabel 				lblKCases;
	private JLabel 				lblServerStatus;
	private JLabel				lblFuzzyMode;

}
