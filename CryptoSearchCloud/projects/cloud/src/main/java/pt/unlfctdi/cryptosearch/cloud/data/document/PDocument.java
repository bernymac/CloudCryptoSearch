package pt.unlfctdi.cryptosearch.cloud.data.document;

import java.io.Serializable;

public class PDocument implements Serializable {
	
	private static final long serialVersionUID = -2229654345571204593L;

	private String title;
	
	private String content;
	
	public PDocument () { }
	
	public PDocument (String title, String content) {
		setTitle(title);
		setContent(content);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	
}