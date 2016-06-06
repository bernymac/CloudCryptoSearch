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