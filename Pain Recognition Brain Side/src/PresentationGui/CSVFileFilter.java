package PresentationGui;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * Used to Limit a user to choose files with csv suffix only with a file chooser
 * @author Eliran Arbeli , Arie Gaon
 */
public class CSVFileFilter extends FileFilter{

	/**
	 * accept only suffices defined here
	 */
	@Override
	public boolean accept(File file) {
		if(file.isDirectory()){
			return true;
		}
		String path = file.getAbsolutePath().toLowerCase();
		if(path.endsWith("csv") && (path.charAt(path.length()-4) == '.')){
			return true;
		}
		return false;
	}
	
	/**
	 * Get the description for the suffices
	 */
	@Override
	public String getDescription() {
		return "csv files .csv";
	}

}
