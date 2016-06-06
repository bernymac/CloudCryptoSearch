package pt.unlfctdi.cryptosearch.cloud.storage;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface CloudStorageRemote extends Remote {

	public byte[] getDoc (String key) throws RemoteException;
	
	public void putDoc (String key, byte[] data) throws RemoteException;
	
	public void removeDoc (String key) throws RemoteException;
	
}
