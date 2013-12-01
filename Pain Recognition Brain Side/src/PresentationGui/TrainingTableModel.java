package PresentationGui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.encog.util.Format;

import businessLogic.training.NeuralTrainDesciptor;

@SuppressWarnings("serial")
public class TrainingTableModel extends AbstractTableModel {

	private ArrayList<NeuralTrainDesciptor> trainedNets;

	private String[] 	headerList = {"Training Error","Validation Error", "Iterations"};
	@SuppressWarnings("rawtypes")
	private Class [] 	classes 	= {String.class, String.class, Integer.class};
	
	public TrainingTableModel(ArrayList<NeuralTrainDesciptor> trainedNets) {
		this.trainedNets = trainedNets;
	}
	
	@Override
	public int getColumnCount() {
		return headerList.length;
	}

	@Override
	public int getRowCount() {
		return trainedNets.size();
	}

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
	
	public NeuralTrainDesciptor getItemAtRow(int rowIndex){
		return trainedNets.get(rowIndex);
	}
	
	public void add(int index, NeuralTrainDesciptor ntd) {
		trainedNets.add(index, ntd);
		fireTableRowsInserted(index, index);
	}
	
	public void remove(int index) {
		trainedNets.remove(index);
		fireTableRowsDeleted(index, index);
	}
	
	@Override
	public Class<?> getColumnClass(int arg0) {
		return classes[arg0];
	}

	public String getColumnName(int col) {
		return headerList[col];
	}
		

}
