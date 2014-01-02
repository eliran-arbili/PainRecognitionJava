package PresentationGui;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import businessLogic.ProjectUtils;
import businessLogic.RunTimeCase;

import javax.swing.JLabel;

import dataLayer.ProjectConfig;

import java.awt.Font;

import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ReviseDialog extends JDialog {

	private double [] solution;
	private JTable table;
	DefaultTableModel model;
	String [] auNames;
	private JTextField txtcaseResult;
	private JTextField textSolutionsOutput;
	private JPanel backgroundPanel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ReviseDialog dialog = new ReviseDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ReviseDialog() {
		try {
			backgroundPanel = new BackgroundPanel(ImageIO.read(this.getClass().getClassLoader().getResource("resources/background.png")));
			setContentPane(backgroundPanel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setBounds(100, 100, 475, 390);
		getContentPane().setLayout(null);

		auNames= ProjectConfig.getOptArray("AUS");
		model=new DefaultTableModel();
		model.addColumn("Action Unit Name");
		model.addColumn("Action Unit Value");
		Object [] value=new Object[2];

		for(int i=0;i<auNames.length;i++)
		{
			value[0]=auNames[i];
			value[1]="";
			model.addRow(value);
		}
		table=new JTable();
		table.setModel(model);
		table.setBounds(22, 71, 225, 211);
		backgroundPanel.add(table);

		JLabel lblActionUnits = new JLabel("Action Units:");
		lblActionUnits.setFont(new Font("Arial", Font.BOLD, 14));
		lblActionUnits.setBounds(22, 44, 108, 23);
		backgroundPanel.add(lblActionUnits);

		JLabel lblCaseResult = new JLabel("Case Result");
		lblCaseResult.setFont(new Font("Arial", Font.BOLD, 14));
		lblCaseResult.setBounds(280, 104, 108, 23);
		backgroundPanel.add(lblCaseResult);


		txtcaseResult = new JTextField();
		txtcaseResult.setEnabled(false);
		txtcaseResult.setBounds(280, 138, 108, 20);
		backgroundPanel.add(txtcaseResult);
		txtcaseResult.setColumns(10);

		JLabel lblNewSolutionsOutput = new JLabel("New Solution Output");
		lblNewSolutionsOutput.setFont(new Font("Arial", Font.BOLD, 14));
		lblNewSolutionsOutput.setBounds(257, 169, 144, 23);
		backgroundPanel.add(lblNewSolutionsOutput);


		textSolutionsOutput = new JTextField();
		textSolutionsOutput.setBounds(280, 203, 108, 23);
		backgroundPanel.add(textSolutionsOutput);
		textSolutionsOutput.setColumns(10);

		JLabel lblRevise = new JLabel("Revise");
		lblRevise.setFont(new Font("Arial", Font.BOLD, 18));
		lblRevise.setBounds(214, 23, 108, 23);
		backgroundPanel.add(lblRevise);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onClickOK();
			}
		});
		btnOk.setBounds(128, 293, 65, 23);
		backgroundPanel.add(btnOk);
		
		JButton btnNewButton = new JButton("Cancel");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onClickCancle();
			}
		});
		btnNewButton.setBounds(280, 293, 65, 23);
		backgroundPanel.add(btnNewButton);


	}

	protected void onClickCancle() {
		this.dispose();
	}

	protected void onClickOK() {
		 
		String [] userSolutions= textSolutionsOutput.getText().split(",");
		if(userSolutions.length!=ProjectConfig.getOptInt("CASE_OUTPUT_COUNT"))
		{
			JOptionPane.showMessageDialog(this, "The number of your solutions not equal to CASE_OUTPUT_COUNT in properties file!", "Revise", JOptionPane.ERROR_MESSAGE);
			return;
		}
		solution = new double[userSolutions.length];
		for(int i=0;i<userSolutions.length;i++)
			solution[i]=Double.parseDouble(userSolutions[i]);
		
		this.dispose();
		
	}

	public double []  showReviseDialog(RunTimeCase rtCase)
	{
		solution=null;
		textSolutionsOutput.setText("");
		for(int i=0;i<auNames.length;i++)
		{
			String strAuValue=String.format("%.4f",rtCase.getActionUnit(i));
			model.setValueAt(strAuValue, i, 1);
		}

		txtcaseResult.setText(ProjectUtils.joinDoubles(",",rtCase.getSolutionOutput(), "%.4f"));

		this.setModal(true);
		this.setVisible(true);
		return solution;
	}
}
