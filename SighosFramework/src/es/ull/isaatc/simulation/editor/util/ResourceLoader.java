package es.ull.isaatc.simulation.editor.util;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ResourceLoader {
	static final int MAX_IMAGE_SIZE = 131072;
	
	static final ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", new Locale("es", "ES"));

	public static JLabel getImageAsJLabel(String imageFile) {
		return new JLabel(getImageAsIcon(imageFile));
	}

	public static ImageIcon getImageAsIcon(String imageFile) {
		try {
			InputStream in = ResourceLoader.class
					.getResourceAsStream(imageFile);
			final byte[] imageByteBuffer = convertToByteArray(in);
			in.close();
			return new ImageIcon(imageByteBuffer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] convertToByteArray(final InputStream is) {
		int read = 0;
		int totalRead = 0;

		byte[] byteArray = new byte[MAX_IMAGE_SIZE];

		try {
			while ((read = is.read(byteArray, totalRead, MAX_IMAGE_SIZE
					- totalRead)) >= 0) {
				totalRead += read;
			}
		} catch (Exception e) {
			return null;
		}

		byte[] finalByteArray = new byte[totalRead];
		System.arraycopy(byteArray, 0, finalByteArray, 0, totalRead);
		return finalByteArray;
	}
	
	public static String getMessage(String key) {
		return messages.getString(key);
	}
}
