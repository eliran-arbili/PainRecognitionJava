package PresentationGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import businessLogic.casesServer.Server;
import dataLayer.ProjectConfig;

@SuppressWarnings("serial")
public class ServerGui extends JFrame{
	
	/*
	 * Instance variables
	 */
	private Server painRecognitionServer;
	private PainMeasureGui painGui;
	public ServerGui(){
		initComponents();
		painGui = new PainMeasureGui();
	}
	
	/*
	 * initialize frame shape and components 
	 */
	private void initComponents(){
		menuBar				= new JMenuBar();
		menuFile			= new JMenu();
		menuTraining		= new JMenu();
		menuItemExit		= new JMenuItem();
		menuItemNewTraining = new JMenuItem();
		menuItemExisting 	= new JMenuItem();
		btnStop 			= new JButton();
		btnStart 			= new JButton();
		btnBrowsAnnFile		= new JButton();
		txtFldAnnFile		= new JTextField();
		txtFldPort			= new JTextField();
		txtFldKCases		= new JTextField();
		txtFldDBAddress		= new JTextField();
		lblAnnFile			= new JLabel();
		lblPort				= new JLabel();
		lblKCases			= new JLabel();
		lblServerStatus 	= new JLabel();
		lblDBAddress		= new JLabel();
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {	
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				onApplicationExit();
			}
		});
		setPreferredSize(new Dimension(550, 300));
		setResizable(false);
		
		Font generalTxtFont = new Font("Arial", 0, 14);
		txtFldPort.setFont(generalTxtFont);
		txtFldAnnFile.setFont(generalTxtFont);
		txtFldKCases.setFont(generalTxtFont);
		txtFldDBAddress.setFont(generalTxtFont);
		lblAnnFile.setFont(generalTxtFont);
		lblPort.setFont(generalTxtFont);
		lblKCases.setFont(generalTxtFont);
		lblDBAddress.setFont(generalTxtFont);
		btnBrowsAnnFile.setFont(generalTxtFont);
		btnStop.setFont(new Font("Arial",1,18));
		btnStart.setFont(new Font("Arial",1,18));
		
		menuFile.setText("File");
		menuTraining.setText("Training");
		menuItemExit.setText("Exit");
		menuItemNewTraining.setText("New");
		menuItemExisting.setText("Existing");
		menuFile.add(menuItemExit);
		menuTraining.add(menuItemNewTraining);
		menuTraining.add(menuItemExisting);
		menuBar.add(menuFile);
		menuBar.add(menuTraining);
		this.setJMenuBar(menuBar);
		
		menuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onApplicationExit();
			}
		});
		
		btnStop.setText("Stop");
		btnStop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				btnStopOnClick(arg0);
			}
		});
		btnStart.setText("Start");
		btnStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				btnStartOnClick(arg0);
			}
		});
		btnBrowsAnnFile.setText("Browse");
		btnBrowsAnnFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				btnBrowsAnnFileOnClick(arg0);
			}
		});
		
		lblAnnFile.setText("Neural Network:");
		lblPort.setText("Port:");
		lblKCases.setText("K Similar Cases:");
		lblDBAddress.setText("Data Base Address:");
		lblServerStatus.setForeground(Color.RED);
		lblServerStatus.setText("OFF");
		lblServerStatus.setFont(new Font("Arial",1,18));
		txtFldAnnFile.setEnabled(false);
		txtFldPort.setText(ProjectConfig.getOpt("SERVER_PORT"));
		txtFldKCases.setText(ProjectConfig.getOpt("K_SIMILAR_CASES"));
		//txtFldAnnFile.setText(ProjectConfig.ANN_PARAMETERS_PATH);
		txtFldDBAddress.setText(ProjectConfig.getOpt("DB_ADDRESS"));
		
		
		GroupLayout layout = new GroupLayout(this.getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblPort)
								.addComponent(lblDBAddress)
								.addComponent(lblAnnFile)
								.addComponent(lblKCases))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(txtFldPort,GroupLayout.PREFERRED_SIZE,80,GroupLayout.PREFERRED_SIZE)
								.addComponent(txtFldDBAddress,GroupLayout.PREFERRED_SIZE,80,GroupLayout.PREFERRED_SIZE)
								.addGroup(layout.createSequentialGroup()
										.addComponent(txtFldAnnFile,GroupLayout.PREFERRED_SIZE,300,GroupLayout.PREFERRED_SIZE)
										.addGap(10)
										.addComponent(btnBrowsAnnFile,GroupLayout.PREFERRED_SIZE,90,GroupLayout.PREFERRED_SIZE))
								.addComponent(txtFldKCases,GroupLayout.PREFERRED_SIZE,30,GroupLayout.PREFERRED_SIZE)))
				.addGroup(layout.createSequentialGroup()
						.addComponent(btnStart,GroupLayout.PREFERRED_SIZE,100,GroupLayout.PREFERRED_SIZE)
						.addContainerGap(30,60)
						.addComponent(btnStop,GroupLayout.PREFERRED_SIZE,100,GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createSequentialGroup()
						.addComponent(lblServerStatus,GroupLayout.PREFERRED_SIZE,50,GroupLayout.PREFERRED_SIZE))
		);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblPort)
						.addComponent(txtFldPort,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblDBAddress)
						.addComponent(txtFldDBAddress,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAnnFile)
						.addComponent(txtFldAnnFile,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowsAnnFile,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblKCases)
						.addComponent(txtFldKCases,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE))
				.addContainerGap(30,50)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnStart,GroupLayout.PREFERRED_SIZE,50,GroupLayout.PREFERRED_SIZE)
						.addComponent(btnStop,GroupLayout.PREFERRED_SIZE,50,GroupLayout.PREFERRED_SIZE))
				.addContainerGap(10,20)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblServerStatus,GroupLayout.PREFERRED_SIZE,40,GroupLayout.PREFERRED_SIZE))
				);
		setTitle("Pain Recognition Server");
		pack();
		
	}
	
	/*
	 * Components callback's
	 */
	protected void btnBrowsAnnFileOnClick(ActionEvent arg0) {
		JFileChooser jfl = new JFileChooser();
		jfl.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfl.setFileFilter(new EncogFileFilter());
		int retVal = jfl.showOpenDialog(this);
		if(retVal == JFileChooser.APPROVE_OPTION){
			txtFldAnnFile.setText(jfl.getSelectedFile().getAbsolutePath());
		}
	}
	protected void btnStartOnClick(ActionEvent arg0) {
	
		String portTxt		= txtFldPort.getText();
		String kCasesTxt 	= txtFldKCases.getText();
		String annPath		= txtFldAnnFile.getText();
		String dbAddress	= txtFldDBAddress.getText();
		if(! isFieldsOK(portTxt,kCasesTxt,annPath,dbAddress)){
			return;
		}
		
		ProjectConfig.setOpt("SERVER_PORT", portTxt);
		ProjectConfig.setOpt("K_SIMILAR_CASES", kCasesTxt);
		ProjectConfig.setOpt("DB_ADDRESS", dbAddress);
		ProjectConfig.setOpt("ANN_PARAMETERS_PATH", annPath);

		if(painRecognitionServer == null){
			painRecognitionServer = new Server(ProjectConfig.getOptInt("SERVER_PORT"));
			painRecognitionServer.addObserver(painGui);
		}
		try {
			painGui.openWindow();
			painRecognitionServer.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lblServerStatus.setText("ON");
		lblServerStatus.setForeground(Color.GREEN);
		
	}
	
	protected void btnStopOnClick(ActionEvent arg0) {
		painRecognitionServer.stopListening();
		lblServerStatus.setText("OFF");
		lblServerStatus.setForeground(Color.RED);
		painGui.closeWindow();
	}
	
	protected void onApplicationExit(){
        int confirm = JOptionPane.showOptionDialog(this,
                "Are You Sure You Want To Exit?",
                "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        
        if (confirm == JOptionPane.YES_OPTION) {
    		if(painRecognitionServer != null){
    			try 
    			{
    				painRecognitionServer.close();
    			} 
    			catch (IOException e) 
    			{
    				e.printStackTrace();
    			}
    		}
    		System.exit(0);
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
		/*
		try{
			
		}catch(NumberFormatException ex){
			JOptionPane.showMessageDialog(this, "Wrong number Format", "Operation couldn't be completed", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		*/	
	}

	/*
	 * Gui components
	 */
	private JMenuBar 		menuBar;
	private JMenu			menuFile;
	private JMenu			menuTraining;
	private JMenuItem		menuItemExit;
	private JMenuItem		menuItemNewTraining;
	private JMenuItem		menuItemExisting;
	private JButton 		btnStop;
	private JButton 		btnStart;
	private JButton 		btnBrowsAnnFile;
	private JTextField 		txtFldAnnFile;
	private JTextField 		txtFldPort;
	private JTextField 		txtFldKCases;
	private JTextField 		txtFldDBAddress;
	private JLabel 			lblAnnFile;
	private JLabel 			lblPort;
	private JLabel 			lblKCases;
	private JLabel 			lblServerStatus;
	private JLabel 			lblDBAddress;
	
	
	/*
	 * Project Entry
	 */
	public static void main(String[] args) {
	    try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } 
    catch (Exception e) {
    	e.printStackTrace();
    }
	    Double auWeights[] = ProjectConfig.getOptDoubleArray("SIMILARITY_WEIGHTS");
		for(int i = 0; i < ProjectConfig.getOptInt("NUMBER_OF_ACTION_UNITS"); i++)
		{
			auWeights[i]=  1.0;
		}
		ServerGui sg = new ServerGui();
		sg.setVisible(true);
	}
	
	
	/*
	 * Inner class for Encog file filtering in dialog
	 */
	public class EncogFileFilter extends FileFilter{

		@Override
		public boolean accept(File file) {
			if(file.isDirectory()){
				return true;
			}
			String path = file.getAbsolutePath().toLowerCase();
			if(path.endsWith("eg") && (path.charAt(path.length()-3) == '.')){
				return true;
			}
			return false;
		}
		@Override
		public String getDescription() {
			return "Encog files .eg";
		}
		
	}

}
