package businessLogic.casesServer;
import java.io.IOException;
import java.util.Arrays;
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
		double [] actionUnits 	= 	Arrays.copyOfRange(message, 0, message.length-1);
		int mode 				= 	Double.valueOf(message[message.length-1]).intValue();
		if(mode == 0) // run time case
		{
			RunTimeCase rtCase = new RunTimeCase(actionUnits);
			double painMeasure = painCBR.doCycle(rtCase);
			sendMsgToClient(client, painMeasure);
			//System.out.println(rtCase);
		}
		/**
		 * TO-DO: complete implementation
		 */
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
}
