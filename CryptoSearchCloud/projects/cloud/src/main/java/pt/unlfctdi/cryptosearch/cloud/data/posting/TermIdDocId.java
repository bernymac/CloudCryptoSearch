package pt.unlfctdi.cryptosearch.cloud.data.posting;

public class TermIdDocId implements Comparable<TermIdDocId>{

	private int termId;
	
	private int docId;

	public TermIdDocId (int termId, int docId) {
		this.termId = termId;
		this.docId = docId;
	}
	
	public int getTermId() {
		return termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	@Override
	public int compareTo(TermIdDocId o) {
		if (termId < o.termId)
			return -1;
		if (termId > o.termId)
			return 1;
		// (termId == o.termId)
		if (docId < o.docId)
			return -1;
		if (docId > o.docId)
			return 1;
		// (termId == o.termId) && (docId == o.docId)
		return 0;
	}
		
	
}
