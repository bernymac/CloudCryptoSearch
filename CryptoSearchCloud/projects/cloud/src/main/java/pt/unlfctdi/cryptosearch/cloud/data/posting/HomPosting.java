package pt.unlfctdi.cryptosearch.cloud.data.posting;

import java.io.Serializable;
import java.math.BigInteger;

public class HomPosting implements Cloneable, Serializable {

	private static final long serialVersionUID = 8880242050287844431L;

	private int docId;
	
	private BigInteger score;
	
	public HomPosting (int docId, BigInteger score) {
		this.docId = docId;
		this.score = score;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public BigInteger getScore() {
		return score;
	}
	
	public void setScore(BigInteger score) {
		this.score = score;
	}
	
	@Override
	public boolean equals(Object o) {
		return docId == ((HomPosting)o).docId;
	}
	
	@Override
	public HomPosting clone() {
		return new HomPosting(docId, new BigInteger(score.toByteArray()));
	}
}
