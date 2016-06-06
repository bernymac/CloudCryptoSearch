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

package pt.unlfctdi.cryptosearch.cloud.search;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import pt.unlfctdi.cryptosearch.cloud.data.document.CDocument;
import pt.unlfctdi.cryptosearch.cloud.data.posting.CipheredPostingList;
import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public interface SearchRemote extends Remote {

	public List<CipheredPostingList> processQuery(List<WordKey> query) throws RemoteException;

	public void buildIndex() throws RemoteException;

	public void addFirstDocuments(byte[] name, List<WordKey> doc) throws RemoteException;

	public Integer addDocumentToIndex(CDocument docPath) throws RemoteException;

	public void rebuildIndex() throws RemoteException;

	public boolean removeDocument(WordKey title) throws RemoteException;

	public boolean removeDocumentById(int docId) throws RemoteException;

	byte[] getDocumentName(int id) throws RemoteException;

}