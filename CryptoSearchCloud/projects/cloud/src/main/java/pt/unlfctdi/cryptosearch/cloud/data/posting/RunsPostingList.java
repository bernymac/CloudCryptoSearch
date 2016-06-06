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

package pt.unlfctdi.cryptosearch.cloud.data.posting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public class RunsPostingList extends PostingList{

	private static final long serialVersionUID = -2674224848248079640L;

	private Map<Integer,TermFreq> postings;
	
	public RunsPostingList(WordKey term) {
		this(term, new HashMap<Integer,TermFreq>());
	}
	
	public RunsPostingList(WordKey term, Map<Integer,TermFreq> postings) {
		this.setTerm(term);
		this.setPostings(postings);
	}

	@Override
	public Collection<TermFreq> getPostings() {
		return postings.values();
	}

	public void setPostings(Map<Integer,TermFreq> postings) {
		this.postings = postings;
	}
	
	@Override
	public void removePosting(int docId) {
		this.postings.remove(docId);
	}
	
	@Override
	public void addPosting(TermFreq posting) {
		TermFreq j = postings.get(posting.getDocId());
		if (j == null)
			postings.put(posting.getDocId(), posting);
		else 
			j.setFreq(j.getFreq()+posting.getFreq());
	}
	
	@Override
	public int getDf() {
		return postings.size();
	}

}
