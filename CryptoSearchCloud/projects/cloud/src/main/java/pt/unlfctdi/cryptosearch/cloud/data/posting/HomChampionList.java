package pt.unlfctdi.cryptosearch.cloud.data.posting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomChampionList extends PostingList {

	private static final long serialVersionUID = -2674224848248079640L;
	
	private List<HomPosting> postings;
	
	public HomChampionList() {
		postings = new ArrayList<HomPosting>(12);
	}
	
	public HomChampionList(List<HomPosting> postings) {
		this.postings = postings;
	}

	@Override
	public Collection<TermFreq> getPostings() {
		return null;
	}

	@Override
	public void removePosting(int docId) {
		this.postings.remove(docId);
	}
	
	@Override
	public void addPosting(TermFreq posting) {
	}

	@Override
	public int getDf() {
		return -1;
	}
	
	public void addPosting(HomPosting posting) {
		postings.add(posting);
	}
	
	public Collection<HomPosting> getHomPostings() {
		return postings;
	}

}
