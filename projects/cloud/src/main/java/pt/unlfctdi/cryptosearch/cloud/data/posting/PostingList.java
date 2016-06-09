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

package pt.unlfctdi.cryptosearch.cloud.data.posting;

import java.io.Serializable;
import java.util.Collection;

import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public abstract class PostingList implements Serializable {

	private static final long serialVersionUID = 6311641806075374063L;
	
	private WordKey term;

	
	public WordKey getTerm() {
		return term;
	}

	public void setTerm(WordKey term) {
		this.term = term;
	}
	
	public void addPosting(int docId, int sum) {
		addPosting(new TermFreq(docId, sum));
	}
	
	public abstract void addPosting(TermFreq posting);

	public abstract void removePosting(int docId);
	
	public abstract Collection<TermFreq> getPostings();

	public abstract int getDf();

}