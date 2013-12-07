package PresentationGui;

import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

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
import java.awt.Color;


@SuppressWarnings("serial")
public class TrainingGui extends JDialog {

	
	private MouseListener tableClickListener;
	private TrainingSession currentTrainSession;
	private String trainingTag;
	private File dataSetFileToUse;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TrainingGui dialog = new TrainingGui("sample");
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/*
	 * Constructors
	 */

	public TrainingGui(String trainingTag) {
		this.trainingTag = trainingTag;
		initialize();
	}

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
	
	private boolean isFieldsOK()
	{
		
		for (java.awt.Component cmp : this.getContentPane().getComponents()) {
			if(cmp instanceof JTextField){
		        if(((JTextField)cmp).getText().isEmpty()){
		        	JOptionPane.showMessageDialog(this, "Please fill empty fields", "Operation couldn't be completed", JOptionPane.INFORMATION_MESSAGE);
					return false;
		        }
			}
	    }
		return true;	
	}
	
	private void initialize(){
		setBounds(100, 100, 575, 500);
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(120, 398, 100, 34);
		btnTrain = new JButton("Train");
		btnTrain.setBounds(310, 398, 100, 34);
		lblDataSet = new JLabel("Data Set:");
		lblDataSet.setBounds(60, 24, 100, 14);
		lblActivationFunction = new JLabel("Activation Function:");
		lblActivationFunction.setBounds(10, 52, 130, 14);
		lblTrainingType = new JLabel("Training Type:");
		lblTrainingType.setBounds(37, 116, 120, 14);
		lblNetworkType = new JLabel("Network Type:");
		lblNetworkType.setBounds(35, 85, 120, 14);
		lblMaxEpochs = new JLabel("Max Epochs:");
		lblMaxEpochs.setBounds(45, 147, 120, 14);
		lbStripLength = new JLabel("Strip Length:");
		lbStripLength.setBounds(44, 178, 120, 14);
		lblAlpha = new JLabel("Alpha:");
		lblAlpha.setBounds(75, 240, 100, 14);
		lblMinEfficiency = new JLabel("Min Efficiency:");
		lblMinEfficiency.setBounds(37, 209, 110, 14);
		lblInputNeurons = new JLabel("Input Neurons:");
		lblInputNeurons.setBounds(33, 365, 120, 14);
		lblOutputNeurons = new JLabel("Output Neurons:");
		lblOutputNeurons.setBounds(25, 303, 120, 14);
		lblHiddenNeurons = new JLabel("Hidden Neurons:");
		lblHiddenNeurons.setBounds(26, 334, 120, 14);
		scrollPane = new JScrollPane();
		scrollPane.setBounds(216, 147, 343, 236);
		chckbxCSVHeaders = new JCheckBox("CSVHeaders",true);
		chckbxCSVHeaders.setBounds(348, 48, 95, 23);

		
		textFieldDataSet = new JTextField();
		textFieldDataSet.setEditable(false);
		textFieldDataSet.setBounds(140, 21, 300, 20);
		textFieldDataSet.setColumns(10);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(468, 20, 81, 23);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickBrowse(e);
			}
		});
		comboBoxActivationFunction = new JComboBox<String>();
		comboBoxActivationFunction.setBounds(137, 49, 145, 20);
		comboBoxActivationFunction.addItem("Sigmoid");
		comboBoxActivationFunction.addItem("TANH");
		comboBoxNetworkType = new JComboBox<String>();
		comboBoxNetworkType.addItem("MLP");
		comboBoxNetworkType.addItem("RBF");
		comboBoxNetworkType.setBounds(137, 82, 145, 20);

		comboBoxTrainingType = new JComboBox<String>();
		comboBoxTrainingType.addItem("Back Propagation");
		comboBoxTrainingType.addItem("Reasilent Propagation");
		comboBoxTrainingType.setBounds(137, 113, 145, 20);

		lblKFolds = new JLabel("K-Folds:");
		lblKFolds.setBounds(67, 271, 100, 14);

		textFieldStripLength = new JTextField();
		textFieldStripLength.setBounds(137, 175, 36, 20);
		textFieldStripLength.setColumns(10);
		textFieldStripLength.setToolTipText("Typical value: 1");

		textFieldMaxEpochs = new JTextField();
		textFieldMaxEpochs.setBounds(137, 144, 36, 20);
		textFieldMaxEpochs.setColumns(10);
		textFieldMaxEpochs.setToolTipText("Typical value: 15,000");

		textFieldMinEffiency = new JTextField();
		textFieldMinEffiency.setBounds(137, 206, 36, 20);
		textFieldMinEffiency.setColumns(10);
		textFieldMinEffiency.setToolTipText("Typical value: 0.1");

		textFieldAlpha = new JTextField();
		textFieldAlpha.setBounds(137, 237, 36, 20);
		textFieldAlpha.setColumns(10);
		textFieldAlpha.setToolTipText("Typical value: ");

		textFieldKFolds = new JTextField();
		textFieldKFolds.setBounds(137, 268, 36, 20);
		textFieldKFolds.setColumns(10);

		textFieldOutputNeurons = new JTextField();
		textFieldOutputNeurons.setBounds(137, 300, 36, 20);
		textFieldOutputNeurons.setColumns(10);

		textFieldHiddenNeurons = new JTextField();
		textFieldHiddenNeurons.setBounds(137, 331, 36, 20);
		textFieldHiddenNeurons.setColumns(10);

		textFieldInputNeurons = new JTextField();
		textFieldInputNeurons.setBounds(137, 362, 36, 20);
		textFieldInputNeurons.setColumns(10);


		btnTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickTrain(e);
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onClickCancel();
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
		
		getContentPane().add(iconLabel);
		getContentPane().setLayout(null);
		getContentPane().add(btnCancel);
		getContentPane().add(lblAlpha);
		getContentPane().add(lblMinEfficiency);
		getContentPane().add(lbStripLength);
		getContentPane().add(lblActivationFunction);
		getContentPane().add(lblNetworkType);
		getContentPane().add(lblTrainingType);
		getContentPane().add(lblMaxEpochs);
		getContentPane().add(comboBoxActivationFunction);
		getContentPane().add(textFieldMaxEpochs);
		getContentPane().add(textFieldStripLength);
		getContentPane().add(textFieldMinEffiency);
		getContentPane().add(textFieldAlpha);
		getContentPane().add(comboBoxNetworkType);
		getContentPane().add(comboBoxTrainingType);
		getContentPane().add(lblOutputNeurons);
		getContentPane().add(lblInputNeurons);
		getContentPane().add(lblDataSet);
		getContentPane().add(lblHiddenNeurons);
		getContentPane().add(lblKFolds);
		getContentPane().add(textFieldKFolds);
		getContentPane().add(textFieldOutputNeurons);
		getContentPane().add(textFieldHiddenNeurons);
		getContentPane().add(textFieldInputNeurons);
		getContentPane().add(btnTrain);
		getContentPane().add(textFieldDataSet);
		getContentPane().add(btnBrowse);
		getContentPane().add(chckbxCSVHeaders);
		getContentPane().add(scrollPane);
		getContentPane().setBackground(Color.CYAN);
		this.setResizable(false);
	}

	protected void onClickCancel() {
		dispose();
	}

	private void createTag() throws IOException{
		File tagDir = null;
		NeuralTrainDesciptor selectedNet = ((TrainingTableModel)tableTrainedNetworks.getModel()).getItemAtRow(tableTrainedNetworks.convertRowIndexToModel(tableTrainedNetworks.getSelectedRow()));
		tagDir = new File(ProjectUtils.combine(ProjectConfig.TRAINING_TAGS_PATH, trainingTag));
		tagDir.mkdir();
		selectedNet.saveAsEncogFile(tagDir.getAbsolutePath());
		File dataSetFileDest 	= new File(ProjectUtils.combine(tagDir.getAbsolutePath(),dataSetFileToUse.getName()));
		Files.copy(dataSetFileToUse.toPath(),dataSetFileDest.toPath());
		dataSetFileToUse.delete(); // Delete after copy to tag dir
	}

	protected void onClickTrain(ActionEvent e) {
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
			iconLabel.setVisible(true);
			repaint();
			final int inputCount 		= (int)configurations.get(ConfKeys.inputCount);
			final int outputCount		= (int)configurations.get(ConfKeys.outputCount);
			final boolean headers		= chckbxCSVHeaders.isSelected();
			currentTrainSession 		= new TrainingSession(configurations);
			Runnable run = new Runnable(){
				@Override
				public void run() {
					ArrayList<NeuralTrainDesciptor> trainedNets;
					try {
						File noDupsCSVSet		= ProjectUtils.removeDuplicateLines(new File(textFieldDataSet.getText()), inputCount, outputCount, headers);
						dataSetFileToUse		= ProjectUtils.normalizeCSVFile(noDupsCSVSet, inputCount, outputCount, headers);
						trainedNets = currentTrainSession.kFoldsCrossValidationTrain(dataSetFileToUse, kFolds, headers);
						tableTrainedNetworks.setModel(new TrainingTableModel(trainedNets));
						if(ProjectConfig.getOptBool("DEBUG_MODE") == false){
							noDupsCSVSet.delete();
						}
						iconLabel.setVisible(false);
						tableTrainedNetworks.addMouseListener(tableClickListener);
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
				
			};
			Thread trainWorker = new Thread(run);
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
			if (rowPressed == ((JTable) e.getSource()).getSelectedRow()) {
				int retval = JOptionPane.showConfirmDialog(this, "This Will Create New Neural Network tag\nPress OK To Continue","New Tag",JOptionPane.OK_CANCEL_OPTION);
				if(retval == JOptionPane.OK_OPTION){
					try
					{
						createTag();
						dispose();
					}
					catch(IOException ex)
					{
						JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Couldn't Be Completed", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
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
	private JButton btnCancel;
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
