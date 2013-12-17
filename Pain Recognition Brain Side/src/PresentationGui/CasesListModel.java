package PresentationGui;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractListModel;

import dataLayer.ProjectConfig;
import businessLogic.RunTimeCase;

@SuppressWarnings("serial")

/**
 * Describe the model of listing a RunTimeCase objects in a list component
 * @author Eliran Arbeli , Arie Gaon
 */
public class CasesListModel extends AbstractListModel<String> {

	/*
	 * Instance variables
	 */
	private CasesQueue<RunTimeCase> modelCases;
	
	/*
	 * Constructors
	 */
	
	/**
	 * Create new CasesListModel that can be used in a JList to show RunTimeCase objects
	 */
	public CasesListModel(){
		modelCases = new CasesQueue<RunTimeCase>(ProjectConfig.getOptInt("CASES_SAVE_HISTORY"));
	}
	
	/**
	 * Get the RunTimeCase solution value from list of cases
	 */
	@Override
	public String getElementAt(int arg0) {
		return Arrays.toString(modelCases.get(arg0).getSolutionOutput());
		
		//return String.valueOf(modelCases.size());
	}
	
	/**
	 * Get the RunTimeCase object corresponds to the location in list
	 * @param arg0- the index of the case in list
	 * @return RunTimeCase
	 */
	public RunTimeCase getCase(int arg0) {
		return modelCases.get(arg0);
	}

	/**
	 * Get the size of list
	 */
	@Override
	public int getSize() {
		return modelCases.size();
	}
	
	/**
	 * Add new RunTimeCase to list
	 * The case will be seen in JList after this call
	 * @param rtCase
	 */
	public void add (RunTimeCase rtCase){
		modelCases.offer(rtCase);
		fireIntervalAdded(this, this.getSize()-1, this.getSize()-1);
	}
	
	/**
	 * Remove case from the list
	 * @param index of case to remove
	 */
	public void remove(int index) {
		modelCases.remove(index);
		fireIntervalRemoved(this, index, index);
	}
	
	
	/**
	 * A veriant for the regular queue just with a limitation of queue size 
	 * and automatic poll of first-in objects when adding to full queue
	 * @author Eliran Arbeli , Arie Gaon
	 *
	 * @param <E>
	 */
	public class CasesQueue<E> extends AbstractQueue<E>
	{
		/*
		 * Instance variables
		 */
		private int maxCapacity;
		private LinkedList<E> queue;
		
		/**
		 * Create new queue with specified max size
		 * @param maxCapacity
		 */
		public CasesQueue(int maxCapacity){
			super();
			this.maxCapacity = maxCapacity;
			queue = new LinkedList<>();
		}
		
		/**
		 * Get the element in a given index
		 * @param index of element
		 * @return the element
		 */
		public E get(int index){
			return queue.get(index);
		}
		
		/**
		 * Insert new element to the list
		 * This will remove first element if list is full
		 */
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
		
		/**
		 * Get the head of the queue
		 */
		@Override
		public E peek() {
			return queue.peek();
		}
		
		/**
		 * Get the next element using the FIFO rule
		 */
		@Override
		public E poll() {
			return queue.poll();
		}
		
		/**
		 * Get iterator for queue
		 */
		@Override
		public Iterator<E> iterator() {
			return queue.iterator();
		}
		
		/**
		 * Get the size of queue
		 */
		@Override
		public int size() {
			return queue.size();
		}
		
	}

}
