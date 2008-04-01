/**
 * 
 */
package simkit.random;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Iván
 *
 */
public class FileResampleVariate extends RandomVariateBase {
	private ResampleVariate rVar;
	private String file;
	public FileResampleVariate() {
		
	}
	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	public double generate() {
		return rVar.generate();
	}
	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	public Object[] getParameters() {
		return new Object[] {file};
	}
	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	public void setParameters(Object... params) {
		if (params.length != 1) {
            throw new IllegalArgumentException("Should be two parameters for FileResample: " +
            params.length + " passed.");
        }
        if (!(params[0] instanceof String)) {
            throw new IllegalArgumentException("Parameters must be a String");
        }
        else {
            setFile(((String) params[0]));
        }
		
	}
	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
		try {
			BufferedReader f = new BufferedReader(new FileReader(file));
			// First line is a commentary
			f.readLine();
			// Second line is number of samples
			double []data = new double[new Integer(f.readLine())];
			// Rest of file
			for (int i = 0; i < data.length; i++)
				data[i] = new Double(f.readLine());
			rVar = new ResampleVariate();
			rVar.setParameters(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
	
}
