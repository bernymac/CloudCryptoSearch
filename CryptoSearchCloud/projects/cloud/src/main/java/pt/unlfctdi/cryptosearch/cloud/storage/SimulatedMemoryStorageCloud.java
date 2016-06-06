/**
 *    Copyright 2013 Bernardo Ferreira

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

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