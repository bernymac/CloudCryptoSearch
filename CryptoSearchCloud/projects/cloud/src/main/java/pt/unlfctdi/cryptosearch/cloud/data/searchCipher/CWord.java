//package pt.unlfctdi.cryptosearch.cloud.data.searchCipher;
//
//
//public class CWord extends SearchCipher {
//
//	private static final long serialVersionUID = -2958437923556090201L;
//
//	public CWord (byte[] word) {
//		this.word = word;
//	}
//	
//	@Override
//	public boolean equals(Object o) {
//		if (o instanceof WordKey)
//			return crypto.matchSearch(this.word, ((WordKey)o).word);
//		else {
//			System.out.println("Cannot compare between two Cipher Texts!");
//			return false;
//		}
//	}
//	
//}
