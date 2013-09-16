package businessLogic;

public class ProjectUtils {
	public static void assertFalse(boolean statement, String description){
		if(statement == true){
			return;
		}
		else{
			Exception ex = new Exception("Assertion:" + description);
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
