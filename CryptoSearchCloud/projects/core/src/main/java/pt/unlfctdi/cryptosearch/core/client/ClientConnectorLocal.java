package pt.unlfctdi.cryptosearch.core.client;

import java.util.List;

import pt.unlfctdi.cryptosearch.cloud.data.document.PDocument;
import pt.unlfctdi.cryptosearch.cloud.data.posting.Posting;

public interface ClientConnectorLocal {

	public List<Posting> query (String keyword);
	
	public PDocument getDocument (String title);
	
	public PDocument getDocumentById (int id);
	
	public void addDocument (PDocument document);
	
	public boolean removeDocument (String title);
	
	public void rebuildIndex();

	boolean removeDocumentById(int id);

	void addFirstDocuments(String filesFolder);

}
