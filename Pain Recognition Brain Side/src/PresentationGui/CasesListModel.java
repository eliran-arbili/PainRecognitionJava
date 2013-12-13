package PresentationGui;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractListModel;

import dataLayer.ProjectConfig;
import businessLogic.RunTimeCase;

@SuppressWarnings("serial")
public class CasesListModel extends AbstractListModel<String> {

	
	private CasesQueue<RunTimeCase> modelCases;
	public CasesListModel(){
		modelCases = new CasesQueue<RunTimeCase>(ProjectConfig.getOptInt("CASES_SAVE_HISTORY"));
	}
	
	@Override
	public String getElementAt(int arg0) {
		return Arrays.toString(modelCases.get(arg0).getSolutionOutput());
		
		//return String.valueOf(modelCases.size());
	}
	
	public RunTimeCase getCase(int arg0) {
		return modelCases.get(arg0);
	}

	@Override
	public int getSize() {
		return modelCases.size();
	}
	
	public void add (RunTimeCase rtCase){
		modelCases.offer(rtCase);
		fireIntervalAdded(this, this.getSize()-1, this.getSize()-1);
	}
	
	public void remove(int index) {
		modelCases.remove(index);
		fireIntervalRemoved(this, index, index);
	}
	
	
	
	public class CasesQueue<E> extends AbstractQueue<E>
	{
		private int maxCapacity;
		private LinkedList<E> queue;
		public CasesQueue(int maxCapacity){
			super();
			this.maxCapacity = maxCapacity;
			queue = new LinkedList<>();
		}
		
		public E get(int index){
			return queue.get(index);
		}
		
		@Override
		public boolean offer(E e) {
			if(e == null){
				return false;
			}
			if(this.size() >= this.maxCapacity){
				queue.poll();
			}
			queue.offer(e);
			return true;
		}
		@Override
		public E peek() {
			return queue.peek();
		}
		@Override
		public E poll() {
			return queue.poll();
		}
		@Override
		public Iterator<E> iterator() {
			return queue.iterator();
		}
		@Override
		public int size() {
			return queue.size();
		}
		
	}

}
