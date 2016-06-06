package pt.unlfctdi.cryptosearch.cloud.storage;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class CloudUtils {

	public static Object readObjectFromStream (InputStream is) {
		try {
			BufferedInputStream bis = new BufferedInputStream(is);
			ObjectInputStream ois = new ObjectInputStream(bis);
			Object result = ois.readObject();
			ois.close();
			is.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
}