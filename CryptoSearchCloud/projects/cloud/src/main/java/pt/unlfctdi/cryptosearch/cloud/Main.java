package pt.unlfctdi.cryptosearch.cloud;

import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import pt.unlfctdi.cryptosearch.cloud.search.PrototypeSearchBean;
import pt.unlfctdi.cryptosearch.cloud.search.SearchRemote;
import pt.unlfctdi.cryptosearch.cloud.storage.CloudStorageRemote;
import pt.unlfctdi.cryptosearch.cloud.storage.SimulatedDiskStorageCloud;

public class Main {
	
	private static String host = "176.111.104.31";
//	private static String host = "127.0.0.1";
	
	public static void main (String[] args) throws Exception{
		System.setProperty("java.rmi.server.hostname", host);
		System.getProperties().put("java.security.policy", "policy.all");
		if( System.getSecurityManager() == null ) 
			System.setSecurityManager( new RMISecurityManager()) ;
		try {
			LocateRegistry.createRegistry( 1099);
		} catch (RemoteException e) {
			//do nothing; already registered
		}
		try {
			SearchRemote search = new PrototypeSearchBean();
			Naming.bind("SearchBean", search);
			System.out.println("Cloud Search Running...");

			CloudStorageRemote storage = new SimulatedDiskStorageCloud("/home/bf/CloudStorage/");
//			CloudStorageRemote storage = new SimulatedDiskStorageCloud("/Users/bernardo/CloudStorage/");
			Naming.bind("StorageBean", storage);
			System.out.println("Cloud Storage Running...");
		} catch (AlreadyBoundException e) {
			System.out.println("ERROR: CloudCryptoSearch Server Already Running!");
			System.exit(0);
		}
	}
}
