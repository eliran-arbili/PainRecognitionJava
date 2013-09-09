package businessLogic.casesServer;
import java.io.IOException;
import java.util.Arrays;

import dataLayer.ProjectConfig;
import businessLogic.*;


public class Server extends AbstractServer{
	
	public Server(int port) {
		super(port);
	}

	public static void main(String[] args) {
		Server s = new Server(ProjectConfig.SERVER_PORT);
		try {
			s.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		double[] message = (double[])msg;
		double [] actionUnits = Arrays.copyOfRange(message, 0, message.length-1);
		int mode = Double.valueOf(message[message.length-1]).intValue();
		if(mode == 0) // run time case
		{
			RunTimeCase rtCase = new RunTimeCase(actionUnits);
			System.out.println(rtCase);
		}
		/**
		 * TO-DO: complete implementation
		 */
	}
	protected void serverStarted() {
		System.out.println("Server Started...");
	}

	protected void serverStopped() {
		System.out.println("Server Stopped...");
	}

	protected void listeningException(Throwable exception) {
		exception.printStackTrace();
	}
	  synchronized protected void clientException(ConnectionToClient client, Throwable exception){
		  exception.printStackTrace();
	  }


}
