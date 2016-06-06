package pt.unlfctdi.cryptosearch.cloud.data.searchCipher;

import java.io.Serializable;
import java.util.Arrays;

public class WordKey implements Serializable{

	private static final long serialVersionUID = 4591094848354398867L;

	private byte[] word;
	
	public WordKey (byte[] word) {
		this.word = word;
	}
	
	public byte[] getContents() {
		return word;
	}
	
	@Override
	public boolean equals(Object o) {
//		if (o instanceof WordKey)
			return Arrays.equals(this.word, ((WordKey)o).word);
//		else
//			return crypto.matchSearch(((CWord)o).word, this.word);
	}
	
	@Override
	public int hashCode()
	{
		return Arrays.hashCode(word);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(word);
	}

}
