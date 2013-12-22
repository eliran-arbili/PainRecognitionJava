package businessLogic;

import java.io.File;
import java.io.IOException;

public class TrackerProcess {
	
	private Thread processWrapper;
	private Process vendorTracker;
	private File trackerProgram;
	private boolean alive;

	public TrackerProcess(File trackerProgram){
		this.trackerProgram = trackerProgram;
		alive = false;
	}
	
	public void start(){
		processWrapper = new Thread() {	
			@Override
			public void run() {
				runProcess();				
			}
		};
		processWrapper.start();
	}
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
	
	public void killTracker(){
		if(vendorTracker != null){
			vendorTracker.destroy();
		}

	}

	
	public boolean isAlive() {
		return alive;
	}

}
