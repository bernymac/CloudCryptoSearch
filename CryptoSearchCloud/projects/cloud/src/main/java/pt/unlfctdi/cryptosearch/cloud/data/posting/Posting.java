package pt.unlfctdi.cryptosearch.cloud.data.posting;

import java.io.Serializable;

public class Posting implements Cloneable, Serializable, Comparable<Posting>{

	private static final long serialVersionUID = 8880242050287844431L;

	private int docId;
	
	private double score;
	
	public Posting (int docId, double score) {
		this.docId = docId;
		this.score = score;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public int compareTo(Posting o) {
		if (score < o.score)
			return 1;
		if (score > o.score)
			return -1;
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		return docId == ((Posting)o).docId;
	}
	
	@Override
	public Posting clone() {
		return new Posting(docId, score);
	}
}
