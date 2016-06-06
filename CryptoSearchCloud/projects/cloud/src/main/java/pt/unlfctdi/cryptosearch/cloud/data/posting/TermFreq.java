package pt.unlfctdi.cryptosearch.cloud.data.posting;

import java.io.Serializable;

public class TermFreq implements Cloneable, Serializable, Comparable<TermFreq>{

	private static final long serialVersionUID = 8880242050287844431L;

	private int docId;
	
	private int frequency;
	
	public TermFreq (int docId, int frequency) {
		this.docId = docId;
		this.frequency = frequency;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getFreq() {
		return frequency;
	}

	public void setFreq(int frequency) {
		this.frequency = frequency;
	}

	@Override
	public int compareTo(TermFreq o) {
		if (frequency < o.frequency)
			return 1;
		if (frequency > o.frequency)
			return -1;
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		return docId == ((TermFreq)o).docId;
	}
	
	@Override
	public TermFreq clone() {
		return new TermFreq(docId, frequency);
	}
}
