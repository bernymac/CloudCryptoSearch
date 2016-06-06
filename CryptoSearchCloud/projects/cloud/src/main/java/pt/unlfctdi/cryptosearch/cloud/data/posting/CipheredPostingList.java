package pt.unlfctdi.cryptosearch.cloud.data.posting;

import java.util.Collection;

public class CipheredPostingList extends PostingList {
	
	private static final long serialVersionUID = -949917116295606684L;
	
	private byte[] postings;
	
	
	public CipheredPostingList (byte[] postings) {
		this.postings = postings;
	}

	@Override
	public void addPosting(TermFreq posting) {
	}

	@Override
	public void removePosting(int docId) {
	}

	@Override
	public Collection<TermFreq> getPostings() {
		return null;
	}

	@Override
	public int getDf() {
		return 0;
	}
	
	public byte[] getCipheredPostings() {
		return postings;
	}

}
