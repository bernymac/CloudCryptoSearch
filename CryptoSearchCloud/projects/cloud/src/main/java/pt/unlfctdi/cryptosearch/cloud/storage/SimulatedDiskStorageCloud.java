package pt.unlfctdi.cryptosearch.cloud.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SimulatedDiskStorageCloud extends UnicastRemoteObject implements CloudStorageRemote {

	private static final long serialVersionUID = -1400457347896237916L;
	
	private String bucketPath;

	public SimulatedDiskStorageCloud(String path) throws RemoteException {
		bucketPath = path;
		File d = new File (bucketPath);
		if (!d.exists())
			d.mkdir();
		else
			for (File f: d.listFiles())
				f.delete();
	}

	@Override
	public byte[] getDoc(String key) throws RemoteException {
		try {
			File f = new File(bucketPath+key);
			FileInputStream bais = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(bais);
			byte[] result = new byte[(int)f.length()];
			bis.read(result);
			bis.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void putDoc(String key, byte[] data) {
		try {
			File f = new File(bucketPath+key);
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(data);
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeDoc(String key) throws RemoteException {
		File f = new File(bucketPath+key);
		f.delete();
	}
	
	
//	public static void main (String[] args) throws Exception {
//		try { // start rmiregistry
//			LocateRegistry.createRegistry( 1099);
//		} catch( RemoteException e) {
//			// do nothing – already started with “rmiregistry”
//		}
//		
//		CloudStorageRemote server = new SimulatedDiskStorageCloud("/home/bernardo/CloudStorage/");
//		Naming.rebind("StorageBean", server);
//		System.out.println("Cloud Storage Server Running...");
//	}
	
}