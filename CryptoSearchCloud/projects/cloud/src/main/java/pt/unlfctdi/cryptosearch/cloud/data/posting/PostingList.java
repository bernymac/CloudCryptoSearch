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