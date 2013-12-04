package PresentationGui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CSVFileFilter extends FileFilter{

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
	@Override
	public String getDescription() {
		return "csv files .csv";
	}

}
