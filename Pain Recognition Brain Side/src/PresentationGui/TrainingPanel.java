package PresentationGui;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableCellRenderer;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;

import dataLayer.ProjectConfig;
import businessLogic.ProjectUtils;
import businessLogic.training.NeuralTrainDesciptor;
import businessLogic.training.TrainingSession;
import businessLogic.training.TrainingSession.ConfKeys;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Font;
import java.awt.Image;


@SuppressWarnings("serial")

/**
 * GUI panel for easy neural networks creation by configurable trainings
 * @author Eliran Arbeli , Arie Gaon
 *
 */
public class TrainingPanel extends BackgroundPanel {

	
	/*
	 * Instance Variables
	 */
	private MouseListener tableClickListener;
	private TrainingSession currentTrainSession;
	private File dataSetFileToUse;
	private Thread trainWorker;


	/*
	 * Constructors
	 */
	/**
	 * Create new TrainingPanel with a background image
	 * @param image
	 */
	public TrainingPanel(Image image) {
		super(image);
		initialize();
	}
	
	
	private void initialize(){
		setBounds(100, 100, 580, 460);
		btnTrain = new JButton("Train");
		btnTrain.setFont(new Font("Arial", Font.BOLD, 14));
		btnTrain.setBounds(310, 403, 100, 34);
		lblDataSet = new JLabel("Data Set:");
		lblDataSet.setFont(new Font("Arial", Font.BOLD, 14));
		lblDataSet.setBounds(10, 29, 140, 14);
		lblActivationFunction = new JLabel("Activation Function:");
		lblActivationFunction.setFont(new Font("Arial", Font.BOLD, 14));
		lblActivationFunction.setBounds(10, 60, 140, 14);
		lblTrainingType = new JLabel("Training Type:");
		lblTrainingType.setFont(new Font("Arial", Font.BOLD, 14));
		lblTrainingType.setBounds(10, 122, 140, 14);
		lblNetworkType = new JLabel("Network Type:");
		lblNetworkType.setFont(new Font("Arial", Font.BOLD, 14));
		lblNetworkType.setBounds(10, 91, 140, 14);
		lblMaxEpochs = new JLabel("Max Epochs:");
		lblMaxEpochs.setFont(new Font("Arial", Font.BOLD, 14));
		lblMaxEpochs.setBounds(10, 153, 140, 14);
		lbStripLength = new JLabel("Strip Length:");
		lbStripLength.setFont(new Font("Arial", Font.BOLD, 14));
		lbStripLength.setBounds(10, 184, 140, 14);
		lblAlpha = new JLabel("Alpha:");
		lblAlpha.setFont(new Font("Arial", Font.BOLD, 14));
		lblAlpha.setBounds(10, 246, 140, 14);
		lblMinEfficiency = new JLabel("Min Efficiency:");
		lblMinEfficiency.setFont(new Font("Arial", Font.BOLD, 14));
		lblMinEfficiency.setBounds(10, 215, 140, 14);
		lblInputNeurons = new JLabel("Input Neurons:");
		lblInputNeurons.setFont(new Font("Arial", Font.BOLD, 14));
		lblInputNeurons.setBounds(10, 370, 140, 14);
		lblOutputNeurons = new JLabel("Output Neurons:");
		lblOutputNeurons.setFont(new Font("Arial", Font.BOLD, 14));
		lblOutputNeurons.setBounds(10, 308, 140, 14);
		lblHiddenNeurons = new JLabel("Hidden Neurons:");
		lblHiddenNeurons.setFont(new Font("Arial", Font.BOLD, 14));
		lblHiddenNeurons.setBounds(10, 339, 140, 14);
		scrollPane = new JScrollPane();
		scrollPane.setBounds(216, 152, 343, 236);
		chckbxCSVHeaders = new JCheckBox("CSVHeaders",true);
		chckbxCSVHeaders.setFont(new Font("Arial", Font.BOLD, 14));
		chckbxCSVHeaders.setBounds(332, 56, 126, 23);

		
		textFieldDataSet = new JTextField();
		textFieldDataSet.setEditable(false);
		textFieldDataSet.setBounds(158, 27, 300, 20);
		textFieldDataSet.setColumns(10);

		btnBrowse = new JButton("Browse");
		btnBrowse.setFont(new Font("Arial", Font.BOLD, 14));
		btnBrowse.setBounds(468, 25, 91, 23);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickBrowse(e);
			}
		});
		comboBoxActivationFunction = new JComboBox<String>();
		comboBoxActivationFunction.setBounds(160, 58, 145, 20);
		comboBoxActivationFunction.addItem("Sigmoid");
		comboBoxActivationFunction.addItem("TANH");
		comboBoxNetworkType = new JComboBox<String>();
		comboBoxNetworkType.addItem("MLP");
		comboBoxNetworkType.addItem("RBF");
		comboBoxNetworkType.setBounds(160, 89, 145, 20);

		comboBoxTrainingType = new JComboBox<String>();
		comboBoxTrainingType.addItem("Back Propagation");
		comboBoxTrainingType.addItem("Reasilent Propagation");
		comboBoxTrainingType.setBounds(160, 120, 145, 20);

		lblKFolds = new JLabel("K-Folds:");
		lblKFolds.setFont(new Font("Arial", Font.BOLD, 14));
		lblKFolds.setBounds(10, 277, 140, 14);

		textFieldStripLength = new JTextField();
		textFieldStripLength.setFont(new Font("Arial", Font.PLAIN, 12));
		textFieldStripLength.setBounds(160, 182, 36, 20);
		textFieldStripLength.setColumns(10);
		textFieldStripLength.setToolTipText("Typical value: 1");

		textFieldMaxEpochs = new JTextField();
		textFieldMaxEpochs.setFont(new Font("Arial", Font.PLAIN, 12));
		textFieldMaxEpochs.setBounds(160, 151, 36, 20);
		textFieldMaxEpochs.setColumns(10);
		textFieldMaxEpochs.setToolTipText("Typical value: 15,000");

		textFieldMinEffiency = new JTextField();
		textFieldMinEffiency.setFont(new Font("Arial", Font.PLAIN, 12));
		textFieldMinEffiency.setBounds(160, 211, 36, 20);
		textFieldMinEffiency.setColumns(10);
		textFieldMinEffiency.setToolTipText("Typical value: 0.1");

		textFieldAlpha = new JTextField();
		textFieldAlpha.setFont(new Font("Arial", Font.PLAIN, 12));
		textFieldAlpha.setBounds(160, 242, 36, 20);
		textFieldAlpha.setColumns(10);
		textFieldAlpha.setToolTipText("Typical value: ");

		textFieldKFolds = new JTextField();
		textFieldKFolds.setFont(new Font("Arial", Font.PLAIN, 12));
		textFieldKFolds.setBounds(160, 273, 36, 20);
		textFieldKFolds.setColumns(10);

		textFieldOutputNeurons = new JTextField();
		textFieldOutputNeurons.setFont(new Font("Arial", Font.PLAIN, 12));
		textFieldOutputNeurons.setBounds(160, 305, 36, 20);
		textFieldOutputNeurons.setColumns(10);

		textFieldHiddenNeurons = new JTextField();
		textFieldHiddenNeurons.setFont(new Font("Arial", Font.PLAIN, 12));
		textFieldHiddenNeurons.setBounds(160, 336, 36, 20);
		textFieldHiddenNeurons.setColumns(10);

		textFieldInputNeurons = new JTextField();
		textFieldInputNeurons.setFont(new Font("Arial", Font.PLAIN, 12));
		textFieldInputNeurons.setBounds(160, 367, 36, 20);
		textFieldInputNeurons.setColumns(10);


		btnTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickTrain(e);
			}
		});
		

		tableTrainedNetworks = new JTable();
		tableTrainedNetworks.setFillsViewportHeight(true);
		tableTrainedNetworks.setRowHeight(25);
		
		tableClickListener = new MouseListener() {		
			@Override
			public void mousePressed(MouseEvent e) {
				onClickTable(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				onClickTable(e);
			}
			
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		};
		scrollPane.setViewportView(tableTrainedNetworks);
		
		
		ClassLoader loader = this.getClass().getClassLoader();
		ImageIcon image = new ImageIcon(loader.getResource("resources/loader.gif"));
		iconLabel = new JLabel();
		iconLabel.setBounds(350, 220, 60, 60);
		iconLabel.setIcon(image);
		image.setImageObserver(iconLabel);
		iconLabel.setVisible(false);
		
		this.add(iconLabel);
		this.setLayout(null);
		this.add(lblAlpha);
		this.add(lblMinEfficiency);
		this.add(lbStripLength);
		this.add(lblActivationFunction);
		this.add(lblNetworkType);
		this.add(lblTrainingType);
		this.add(lblMaxEpochs);
		this.add(comboBoxActivationFunction);
		this.add(textFieldMaxEpochs);
		this.add(textFieldStripLength);
		this.add(textFieldMinEffiency);
		this.add(textFieldAlpha);
		this.add(comboBoxNetworkType);
		this.add(comboBoxTrainingType);
		this.add(lblOutputNeurons);
		this.add(lblInputNeurons);
		this.add(lblDataSet);
		this.add(lblHiddenNeurons);
		this.add(lblKFolds);
		this.add(textFieldKFolds);
		this.add(textFieldOutputNeurons);
		this.add(textFieldHiddenNeurons);
		this.add(textFieldInputNeurons);
		this.add(btnTrain);
		this.add(textFieldDataSet);
		this.add(btnBrowse);
		this.add(chckbxCSVHeaders);
		this.add(scrollPane);
	}

	/*
	 * Components callback's
	 */
	protected void onClickBrowse(ActionEvent e) {
		JFileChooser jfc = new JFileChooser(ProjectConfig.DATASETS_PATH);
		jfc.setFileFilter(new CSVFileFilter());
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int retval = jfc.showOpenDialog(this);
		if(retval == JOptionPane.YES_OPTION){
			textFieldDataSet.setText(jfc.getSelectedFile().getAbsolutePath());
			tableTrainedNetworks.removeAll();
		}
	}
	
	protected void onClickTrain(ActionEvent e) {
		
		if(btnTrain.getText().equals("Stop")){
			trainWorker.stop();
			btnTrain.setText("Train");
			iconLabel.setVisible(false);
			return;
		}
		
		if(! isFieldsOK()){
			return;
		}
		final int kFolds;
		HashMap<ConfKeys,Object> configurations;
		try
		{
			kFolds 			= Integer.parseInt(textFieldKFolds.getText());
			configurations 	= getEnteredConfigurations();
		}
		catch(NumberFormatException ex)
		{
			JOptionPane.showMessageDialog(this, "Wrong number Format", "Operation couldn't be completed", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		try{
			repaint();
			final int inputCount 		= (int)configurations.get(ConfKeys.inputCount);
			final int outputCount		= (int)configurations.get(ConfKeys.outputCount);
			final boolean headers		= chckbxCSVHeaders.isSelected();
			currentTrainSession 		= new TrainingSession(configurations);
			Runnable run = new Runnable(){
				@Override
				public void run() {
					ArrayList<NeuralTrainDesciptor> trainedNets;
					iconLabel.setVisible(true);
					try {
						File noDupsCSVSet		= ProjectUtils.removeDuplicateLines(new File(textFieldDataSet.getText()), inputCount, outputCount, headers);
						dataSetFileToUse		= ProjectUtils.normalizeCSVFile(noDupsCSVSet, inputCount, outputCount, headers);
						trainedNets = currentTrainSession.kFoldsCrossValidationTrain(dataSetFileToUse, kFolds, headers);
						tableTrainedNetworks.setModel(new TrainingTableModel(trainedNets));
						DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
						centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
						tableTrainedNetworks.getColumn("Training Error");
						for(int i = 0 ; i < tableTrainedNetworks.getModel().getColumnCount(); i++){
							String columnName = tableTrainedNetworks.getModel().getColumnName(i);
							tableTrainedNetworks.getColumn(columnName).setCellRenderer(centerRenderer);;
						}
						if(ProjectConfig.getOptBool("DEBUG_MODE") == false){
							noDupsCSVSet.delete();
						}
						tableTrainedNetworks.addMouseListener(tableClickListener);
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					iconLabel.setVisible(false);
				}
				
			};
			btnTrain.setText("Stop");
			trainWorker = new Thread(run);
			trainWorker.start();
		} 
		catch (Exception ex) 
		{
			JOptionPane.showMessageDialog(this, ex.toString(), "Operation couldn't be completed", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	protected void onClickTable(MouseEvent e) {
		if(e.getClickCount() == 2 && ! e.isConsumed()){
			e.consume();
			int rowPressed = ((JTable) e.getSource()).rowAtPoint(e.getPoint());
			if (rowPressed != ((JTable) e.getSource()).getSelectedRow()) {
				return;
			}
			String tagName = JOptionPane.showInputDialog(this, "Please Enter Training Tag Name","New Tag",JOptionPane.OK_CANCEL_OPTION);
			if(tagName == null){
				return;
			}
			if(! isTagValid(tagName)){
				JOptionPane.showMessageDialog(this, "Invalid file name or it already exists", "Invalid Tag Name", JOptionPane.ERROR_MESSAGE);
				return ;
			}
			try
			{
				createTag(tagName);
			}
			catch(IOException ex)
			{
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Couldn't Be Completed", JOptionPane.ERROR_MESSAGE);
			}
			JOptionPane.showMessageDialog(this, " Tag Successfuly Created!", "Training", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	
	private boolean isFieldsOK()
	{
		
		for (java.awt.Component cmp : this.getComponents()) {
			if(cmp instanceof JTextField){
		        if(((JTextField)cmp).getText().isEmpty()){
		        	JOptionPane.showMessageDialog(this, "Please fill empty fields", "Operation couldn't be completed", JOptionPane.WARNING_MESSAGE);
					return false;
		        }
			}
	    }
		return true;	
	}

	private void createTag(String trainingTag) throws IOException{
		File tagDir = null;
		NeuralTrainDesciptor selectedNet = ((TrainingTableModel)tableTrainedNetworks.getModel()).getItemAtRow(tableTrainedNetworks.convertRowIndexToModel(tableTrainedNetworks.getSelectedRow()));
		tagDir = new File(ProjectUtils.combine(ProjectConfig.TRAINING_TAGS_PATH, trainingTag));
		tagDir.mkdir();
		selectedNet.saveAsEncogFile(tagDir.getAbsolutePath());
		File dataSetFileDest 	= new File(ProjectUtils.combine(tagDir.getAbsolutePath(),dataSetFileToUse.getName()));
		Files.copy(dataSetFileToUse.toPath(),dataSetFileDest.toPath());
		dataSetFileToUse.delete(); // Delete after copy to tag dir
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
		


	private HashMap<ConfKeys, Object> getEnteredConfigurations() throws NumberFormatException {
		
		HashMap<ConfKeys,Object> confs = new HashMap<ConfKeys,Object>();
		confs.put(ConfKeys.alpha, Double.parseDouble(textFieldAlpha.getText()));
		confs.put(ConfKeys.stripLength, Integer.parseInt(textFieldStripLength.getText()));
		confs.put(ConfKeys.inputCount, Integer.parseInt(textFieldInputNeurons.getText()));
		confs.put(ConfKeys.maxEpochs, Integer.parseInt(textFieldMaxEpochs.getText()));
		confs.put(ConfKeys.hiddenCount, Integer.parseInt(textFieldHiddenNeurons.getText()));
		confs.put(ConfKeys.outputCount, Integer.parseInt(textFieldOutputNeurons.getText()));
		confs.put(ConfKeys.minEffiency, Double.parseDouble(textFieldMinEffiency.getText()));
		confs.put(ConfKeys.activationFunction, comboBoxActivationFunction.getSelectedItem().equals("TANH")? new ActivationTANH() : new ActivationSigmoid());
		confs.put(ConfKeys.neyType, comboBoxNetworkType.getSelectedItem().equals("MLP") ? TrainingSession.MLP_TYPE : TrainingSession.RBF_TYPE);
		confs.put(ConfKeys.trainType, comboBoxTrainingType.getSelectedItem().equals("Back Propagation") ? TrainingSession.BACK_PROP : TrainingSession.REAS_PROP);
		return confs;
	}


	/*
	 * Gui Components
	 */
	private JComboBox<String> comboBoxActivationFunction;	
	private JComboBox<String> comboBoxNetworkType;
	private JComboBox<String> comboBoxTrainingType;
	private JTextField textFieldDataSet;
	private JTextField textFieldStripLength;
	private JTextField textFieldMaxEpochs;
	private JTextField textFieldMinEffiency;
	private JTextField textFieldAlpha;
	private JTextField textFieldKFolds;
	private JTextField textFieldOutputNeurons;
	private JTextField textFieldHiddenNeurons;
	private JTextField textFieldInputNeurons;
	private JButton btnBrowse;
	private JButton btnTrain;
	private JLabel lblDataSet;
	private JLabel lblActivationFunction;
	private JLabel lblTrainingType;
	private JLabel lblNetworkType;
	private JLabel lblMaxEpochs;
	private JLabel lbStripLength;
	private JLabel lblAlpha;
	private JLabel lblKFolds;
	private JLabel lblMinEfficiency;
	private JLabel lblInputNeurons;
	private JLabel lblOutputNeurons;
	private JLabel lblHiddenNeurons;
	private JCheckBox chckbxCSVHeaders;
	private JScrollPane scrollPane;
	private JTable tableTrainedNetworks;
	private JLabel iconLabel;

}
