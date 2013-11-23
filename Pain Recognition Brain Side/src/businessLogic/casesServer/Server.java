package businessLogic.casesServer;
import java.io.IOException;
import java.util.Arrays;

import dataLayer.ProjectConfig;
import businessLogic.*;


public class Server extends AbstractServer{
	
	/*
	 * Instance variables 
	 */
	
	/**
	 * Pain Recognition CBR controller
	 */
	private CBRController painCBR;
	
	/*
	 * Constructors
	 */
	public Server(int port) 
	{
		super(port);
		painCBR = new CBRController();
	}

	/*
	 * Member functions
	 */
	
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		double [] message 		= 	(double[])msg;
		double [] actionUnits 	= 	Arrays.copyOfRange(message, 0, message.length);

		RunTimeCase rtCase = new RunTimeCase(actionUnits);
		rtCase.normalize();
		double painMeasure = painCBR.doCycle(rtCase);
		String toSend = String.format("%.3f", painMeasure);
		System.out.println(toSend);
		if(painMeasure > ProjectConfig.PAIN_SENSITIVITY){
			System.out.println(rtCase);
		}
		
		
	}
	
	public void sendMsgToClient(ConnectionToClient client, Object msg){
		try 
		{
			client.sendToClient(msg);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	protected void serverStarted() {
		System.out.println("Server Started...");
	}

	protected void serverStopped() {
		painCBR.handleShutDown();
		System.out.println("Server Stopped...");
	}

	protected void listeningException(Throwable exception) {
		painCBR.handleShutDown();
		exception.printStackTrace();
	}
	synchronized protected void clientException(ConnectionToClient client, Throwable exception){
		exception.printStackTrace();
	}
	  protected void clientConnected(ConnectionToClient client) {
		  System.out.println("Client Connected");
	  }
}
