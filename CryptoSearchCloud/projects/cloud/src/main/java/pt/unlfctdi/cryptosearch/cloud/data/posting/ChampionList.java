package pt.unlfctdi.cryptosearch.cloud.data.posting;

import java.util.PriorityQueue;

import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public class ChampionList extends PostingList{

	private static final long serialVersionUID = -2674224848248079640L;
	
	private PriorityQueue<TermFreq> postings;
	private int df;
	
	public ChampionList(PostingList clone) {
		this(clone.getTerm(), clone.getDf());
		for (TermFreq tf: clone.getPostings())
			this.addPosting(tf);
	}
	
	public ChampionList(WordKey term, int df) {
		this(term, df, new PriorityQueue<TermFreq>(12));
	}
	
	public ChampionList(WordKey term,int df,  PriorityQueue<TermFreq> postings) {
		this.setTerm(term);
		this.setDf(df);
		this.setPostings(postings);
	}

	@Override
	public PriorityQueue<TermFreq> getPostings() {
		return postings;
	}

	public void setPostings(PriorityQueue<TermFreq> postings) {
		this.postings = postings;
	}
	
	@Override
	public void removePosting(int docId) {
		this.postings.remove(docId);
	}
	
	@Override
	public void addPosting(int docId, int sum) {
		addPosting(new TermFreq(docId, sum));
	}
	
	@Override
	public void addPosting(TermFreq posting) {
		if (postings.size() < 12)
			postings.add(posting);
		else if (posting.getFreq() > postings.peek().getFreq()) { 
			postings.poll();
			postings.add(posting);
		}
	}

	@Override
	public int getDf() {
		return df;
	}
	
	public void setDf(int df) {
		this.df = df;
	}

}
