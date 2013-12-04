package PresentationGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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

import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.util.arrayutil.NormalizationAction;

import businessLogic.ProjectUtils;
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
		menuItemAnalyzeCSV	= new JMenuItem();
		btnStop 			= new JButton();
		btnStart 			= new JButton();
		txtFldPort			= new JTextField();
		txtFldKCases		= new JTextField();
		cmboxTags			= new JComboBox<File>();
		chkboxFuzzyMode		= new JCheckBox();
		lblTags				= new JLabel();
		lblPort				= new JLabel();
		lblKCases			= new JLabel();
		lblServerStatus 	= new JLabel();
		lblFuzzyMode		= new JLabel();
		
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
		txtFldKCases.setFont(generalTxtFont);
		lblTags.setFont(generalTxtFont);
		lblPort.setFont(generalTxtFont);
		lblKCases.setFont(generalTxtFont);
		lblFuzzyMode.setFont(generalTxtFont);
		btnStop.setFont(new Font("Arial",1,18));
		btnStart.setFont(new Font("Arial",1,18));
		
		menuFile.setText("File");
		menuTraining.setText("Training");
		menuItemExit.setText("Exit");
		menuItemNewTraining.setText("New");
		menuItemAnalyzeCSV.setText("Analyze CSV");
		menuFile.add(menuItemExit);
		menuTraining.add(menuItemNewTraining);
		menuTraining.add(menuItemAnalyzeCSV);
		menuBar.add(menuFile);
		menuBar.add(menuTraining);
		this.setJMenuBar(menuBar);
		
		menuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onApplicationExit();
			}
		});
		menuItemNewTraining.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onClickNewTraining(arg0);
			}
		});
		menuItemAnalyzeCSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onClickAnalyzeCSV();
			}
		});
		
		for(File tag: ProjectConfig.getTrainingTags()){
			cmboxTags.addItem(tag);
		}
		
		btnStop.setText("Stop");
		btnStop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				btnStopOnClick(arg0);
			}
		});
		btnStop.setEnabled(false);
		
		btnStart.setText("Start");
		btnStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				btnStartOnClick(arg0);
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
		
		
		GroupLayout layout = new GroupLayout(this.getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblPort)
								.addComponent(lblTags)
								.addComponent(lblKCases)
								.addComponent(lblFuzzyMode))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(txtFldPort,GroupLayout.PREFERRED_SIZE,80,GroupLayout.PREFERRED_SIZE)
								.addComponent(cmboxTags,GroupLayout.PREFERRED_SIZE,350,GroupLayout.PREFERRED_SIZE)
										.addGap(10)
								.addComponent(txtFldKCases,GroupLayout.PREFERRED_SIZE,30,GroupLayout.PREFERRED_SIZE)
								.addComponent(chkboxFuzzyMode,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE)))
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
						.addComponent(lblTags)
						.addComponent(cmboxTags,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblKCases)
						.addComponent(txtFldKCases,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblFuzzyMode)
						.addComponent(chkboxFuzzyMode,GroupLayout.PREFERRED_SIZE,20,GroupLayout.PREFERRED_SIZE))
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
	


	protected void onClickAnalyzeCSV() {
		JFileChooser jf = new JFileChooser(ProjectConfig.DATASETS_PATH);
		jf.setFileFilter(new CSVFileFilter());
		int retval = jf.showOpenDialog(this);
		if(retval == JFileChooser.APPROVE_OPTION){
			StringBuilder minValues = new StringBuilder("Minimum Values: ");
			StringBuilder maxValues = new StringBuilder("Maximum Values: ");
			StringBuilder headers 	= new StringBuilder("Analyzed Fields: ");
			StringBuilder message 	= new StringBuilder();

			java.util.List<AnalystField> aFields = ProjectUtils.getAnalystFieldsCSV(jf.getSelectedFile());
			for(AnalystField af: aFields){
				if(af.getAction() == NormalizationAction.Ignore){
					continue;
				}
				headers.append(af.getName() + ", ");
				minValues.append(af.getActualLow()  + ", ");
				maxValues.append(af.getActualHigh() + ", ");
			}
			headers.deleteCharAt(headers.length()-1);
			minValues.deleteCharAt(minValues.length()-1);
			maxValues.deleteCharAt(maxValues.length()-1);
			message.append(headers + "\n");
			message.append(minValues+ "\n");
			message.append(maxValues + "\n");
			JOptionPane.showMessageDialog(this, message + "\nPress OK To Continue", "Analyze Results", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/*
	 * Components callback's
	 */
	
	protected void onClickNewTraining(ActionEvent arg0) {
		String tagName = JOptionPane.showInputDialog(this,"Choose Training Tag Name");
		if(tagName == null){
			return;
		}
		if(! isTagValid(tagName)){
			JOptionPane.showMessageDialog(this, "Invalid file name or it already exists", "Invalid Tag Name", JOptionPane.INFORMATION_MESSAGE);
			return ;
		}
		TrainingGui trGui = new TrainingGui(tagName);
		trGui.addWindowListener(new WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				onTrainingGuiExit();
			}
            public void windowClosed(java.awt.event.WindowEvent e) {
            	onTrainingGuiExit();
            }
		});
		trGui.setVisible(true);
	}
	
	protected void onTrainingGuiExit() {
		System.out.println("Here");
		cmboxTags.removeAllItems();
		for(File tag: ProjectConfig.getTrainingTags()){
			cmboxTags.addItem(tag);
		}
	}

	private boolean isTagValid(String tag) {
		File tagDir = new File(ProjectUtils.combine(ProjectConfig.TRAINING_TAGS_PATH, tag));
		try{
			java.nio.file.Path tagPath = tagDir.toPath();
			if(Files.exists(tagPath)){
				return false;
			}
		}
		catch(java.nio.file.InvalidPathException ex)
		{
			return false;
		}
		return true;
	}

	protected void btnStartOnClick(ActionEvent arg0) {
	
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
		String annPath	= ProjectConfig.getPersistenceANNByTag(trainingTag).getAbsolutePath();
		String csvPath = ProjectConfig.getCSVByTag(trainingTag).getAbsolutePath();
		ProjectConfig.setOpt("SERVER_PORT", portTxt);
		ProjectConfig.setOpt("K_SIMILAR_CASES", kCasesTxt);
		ProjectConfig.setOpt("ANN_PARAMETERS_PATH", annPath);
		ProjectConfig.setOpt("CSV_CASES_PATH", csvPath);
		ProjectConfig.setOpt("FUZZY_MODE", String.valueOf(chkboxFuzzyMode.isSelected()));

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
		btnStart.setEnabled(false);
		btnStop.setEnabled(true);
	}
	
	protected void btnStopOnClick(ActionEvent arg0) {
		painRecognitionServer.stopListening();
		lblServerStatus.setText("OFF");
		lblServerStatus.setForeground(Color.RED);
		painGui.closeWindow();
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
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
	private JMenuBar 		 	menuBar;
	private JMenu			 	menuFile;
	private JMenu			 	menuTraining;
	private JMenuItem		 	menuItemExit;
	private JMenuItem		 	menuItemNewTraining;
	private JMenuItem			menuItemAnalyzeCSV;
	private JButton 		 	btnStop;
	private JButton 		 	btnStart;
	private JTextField 		 	txtFldPort;
	private JTextField 			txtFldKCases;
	private JComboBox<File> 	cmboxTags;
	private JCheckBox			chkboxFuzzyMode;
	private JLabel 			 	lblTags;
	private JLabel 				lblPort;
	private JLabel 				lblKCases;
	private JLabel 				lblServerStatus;
	private JLabel				lblFuzzyMode;
	
	/*
	 * Project Entry
	 */
	public static void main(String[] args) {
	    try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e) 
    {
    	e.printStackTrace();
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
