package pt.unlfctdi.cryptosearch.cloud.data.document;

import java.io.Serializable;

import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public class CDocument implements Serializable {
	
	private static final long serialVersionUID = -2229654345571204593L;

	private WordKey title;
	
	private WordKey[] content;
	
	public CDocument () { }
	
	public CDocument (WordKey title, WordKey[] content) {
		setTitle(title);
		setContent(content);
	}

	public void setTitle(WordKey title) {
		this.title = title;
	}

	public WordKey getTitle() {
		return title;
	}

	public void setContent(WordKey[] content) {
		this.content = content;
	}

	public WordKey[] getContent() {
		return content;
	}
	
}