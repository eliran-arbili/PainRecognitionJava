package businessLogic.painServer;
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
	 * Create new Pain Recognition Sever instance
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
	
	/**
	 * Get the CBRController instance
	 * @return CBRController
	 */
	public CBRController getPainCBR() {
		return painCBR;
	}
	
	/**
	 * Set CBRController
	 * @param painCBR
	 */
	public void setPainCBR(CBRController painCBR) {
		this.painCBR = painCBR;
	}
	
	/*
	 * (non-Javadoc)
	 * @see businessLogic.casesServer.ObservableServer#handleMessageFromClient(java.lang.Object, businessLogic.casesServer.ConnectionToClient)
	 * 
	 * Extreact a given RunTine case from the message Object and activate the CBR cycle
	 * Notify all observers for the pain measure results
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
	
	synchronized protected void serverStarted() {
		super.serverStarted();
		System.out.println("Server Started...");
	}

	synchronized protected void serverStopped() {
		super.serverStopped();
		painCBR.handleShutDown();
		System.out.println("Server Stopped...");
	}
	
	synchronized protected void serverClosed() {
		super.serverClosed();
		painCBR.handleShutDown();
		System.out.println("Server Closed...");
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
