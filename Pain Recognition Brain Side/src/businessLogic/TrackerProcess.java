package businessLogic;

import java.io.File;
import java.io.IOException;

/**
 * Wrapper for the tracking process program
 * This class can represent the client side process. 
 * Activating the client process is done in a different thread.
 * @author Eliran Arbili , Arie Gaon
 */
public class TrackerProcess {
	
	/*
	 * Instnace variables
	 */
	private Thread processWrapper;
	private Process vendorTracker;
	private File trackerProgram;
	private boolean alive;

	/**
	 * Create new Tracker process
	 * @param trackerProgram- executable program file
	 */
	public TrackerProcess(File trackerProgram){
		this.trackerProgram = trackerProgram;
		alive = false;
	}
	
	/*
	 * Member functions
	 */
	
	/**
	 * Start the tacker program in different thread
	 */
	public void start(){
		processWrapper = new Thread() {	
			@Override
			public void run() {
				runProcess();				
			}
		};
		processWrapper.start();
	}
	
	/**
	 * kill the tracker process
	 */
	public void killTracker(){
		if(vendorTracker != null){
			vendorTracker.destroy();
		}

	}

	/**
	 *  check if program still runs
	 * @return true if program still run, else false
	 */
	public boolean isAlive() {
		return alive;
	}
	
	/*
	 * Auxiliary Methods
	 */
	private void runProcess() {
		ProcessBuilder pb = new ProcessBuilder(trackerProgram.getAbsolutePath());
		try {
			vendorTracker = pb.start();
			alive = true;
			vendorTracker.waitFor();
			alive = false;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	

}
