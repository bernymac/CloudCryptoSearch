/**
 *    Copyright 2013 Bernardo Luís da Silva Ferreira

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
