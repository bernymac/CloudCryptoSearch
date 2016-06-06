package pt.unlfctdi.cryptosearch.cloud.storage;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

public class SimulatedMemoryStorageCloud implements CloudStorageRemote {

	private Map<String,byte[]> docsBucket;
	
	
	@PostConstruct
	public void start() {
		docsBucket = new HashMap<String, byte[]>();
	}
	

	@Override
	public byte[] getDoc(String key) throws RemoteException {
		return docsBucket.get(key);
	}


	@Override
	public void putDoc(String key, byte[] data) throws RemoteException {
		docsBucket.put(key, data);
	}

	@Override
	public void removeDoc(String key) throws RemoteException {
		docsBucket.remove(key);
	}
}