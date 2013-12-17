package PresentationGui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.encog.util.Format;

import businessLogic.training.NeuralTrainDesciptor;

@SuppressWarnings("serial")
/**
 * Describe the model of listing a NeuralTrainDescriptor objects in a table component
 * @author Eliran Arbeli , Arie Gaon
 *
 */
public class TrainingTableModel extends AbstractTableModel {

	/*
	 * Instance variables
	 */
	private ArrayList<NeuralTrainDesciptor> trainedNets;
	private String[] 	headerList = {"Training Error","Validation Error", "Iterations"};
	@SuppressWarnings("rawtypes")
	private Class [] 	classes 	= {String.class, String.class, Integer.class};
	
	/**
	 * Create new model with initial list of objects
	 * @param trainedNets- list of elements to show in the table
	 */
	public TrainingTableModel(ArrayList<NeuralTrainDesciptor> trainedNets) {
		this.trainedNets = trainedNets;
	}
	
	/**
	 * Get the number of columns in the model
	 */
	@Override
	public int getColumnCount() {
		return headerList.length;
	}

	/**
	 * Get the number of rows in the model
	 */
	@Override
	public int getRowCount() {
		return trainedNets.size();
	}

	/**
	 * Get the string representation that corresponds to a given row & column pair
	 */
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		NeuralTrainDesciptor entity = trainedNets.get(rowIndex);
		switch(colIndex){
			case 0:
				return Format.formatPercent(entity.getTrainingSetError());
			case 1:
				return Format.formatPercent(entity.getValidationSetError());
			case 2: 
				return entity.getTrainIterations();
			default: return null;
		}
	}
	
	/**
	 * Get the NeuralTrainDesciptor object that correspond to a row
	 * @param rowIndex of the NeuralTrainDesciptor object
	 * @return NeuralTrainDesciptor object
	 */
	public NeuralTrainDesciptor getItemAtRow(int rowIndex){
		return trainedNets.get(rowIndex);
	}
	
	/**
	 * Add new NeuralTrainDesciptor to the table in row index
	 * @param index of row to add eleemnt
	 * @param ntd- the element to add
	 */
	public void add(int index, NeuralTrainDesciptor ntd) {
		trainedNets.add(index, ntd);
		fireTableRowsInserted(index, index);
	}
	
	/**
	 * Removing element from a given row index
	 * @param index of element to remove
	 */
	public void remove(int index) {
		trainedNets.remove(index);
		fireTableRowsDeleted(index, index);
	}
	
	/**
	 * Get the Class type of a given column index
	 */
	@Override
	public Class<?> getColumnClass(int arg0) {
		return classes[arg0];
	}

	/**
	 * Get the field name of a given column index
	 */
	public String getColumnName(int col) {
		return headerList[col];
	}
		

}
