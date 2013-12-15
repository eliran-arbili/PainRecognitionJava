package businessLogic.casesServer;
import java.io.IOException;
import java.util.Arrays;
import businessLogic.*;


public class Server extends ObservableServer{
	
	/*
	 * Instance variables 
	 */
	
	
	private CBRController painCBR;
	/*
	 * Constructors
	 */
	

	/**
	 * CInitialize the server and instance of CBRController class
	 * @param port
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
		
		double [] painMeasure = painCBR.doCycle(rtCase);
		rtCase.setSolutionOutput(painMeasure);
		setChanged();
		notifyObservers(rtCase);
	}
	
	public boolean handleReviseRequest(RunTimeCase newCase, double [] newSol){
		return painCBR.revise(newCase, newSol);

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
	
	public CBRController getPainCBR() {
		return painCBR;
	}

	public void setPainCBR(CBRController painCBR) {
		this.painCBR = painCBR;
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
