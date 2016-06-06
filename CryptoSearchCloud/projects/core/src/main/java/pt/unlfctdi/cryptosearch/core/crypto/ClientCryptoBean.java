package pt.unlfctdi.cryptosearch.core.crypto;

import java.io.EOFException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import pt.unlfctdi.cryptosearch.cloud.Utils;
import pt.unlfctdi.cryptosearch.cloud.crypto.Paillier;
import pt.unlfctdi.cryptosearch.cloud.data.document.PDocument;
import pt.unlfctdi.cryptosearch.cloud.data.posting.ChampionList;
import pt.unlfctdi.cryptosearch.cloud.data.posting.PostingList;
import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public class ClientCryptoBean {

	SecureRandom random;
	SecretKeySpec AESKey;
	Cipher AESCipher; 			//AES CBC => Guardar IV em claro juntamente com o ciphertext, depois de gerado
	
//	List<byte[]> docsIVs;
	
//	private String indexPath;

	private MessageDigest hash;
	
	private Mac mac;

	private Paillier homCipher;
	
	private SearchScheme search;

	public ClientCryptoBean() {
		try {
			byte[] raw = new byte[] { 
                    0x73, 0x2f, 0x2d, 0x33, (byte)0xc8, 0x01, 0x73, 
                    0x2b, 0x72, 0x06, 0x75, 0x6c, (byte)0xbd, 0x44, 
                    (byte)0xf9, (byte)0xc1};
			
			random = new SecureRandom();
			hash = MessageDigest.getInstance("SHA-256");
			mac = Mac.getInstance("HmacSHA1");
			
			AESKey = new SecretKeySpec(raw, "AES");
			AESCipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
//			docsIVs = new LinkedList<byte[]>();
			
			search =  new SearchScheme(raw, hash, mac, random, 16);
			homCipher = new Paillier();
			
//			indexPath = "C:/Users/Rafael/Dropbox/WorkspacePHD/CryptoSearch/index/";
//			docsIVs = (byte[][]) Utils.readCiphersFromDisk(indexPath+"docsIVs");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public void save() {
//		Utils.writeObjectToDisk(docsIVs, indexPath+"docsIVs");
//	}
	
	public byte[] digest(byte[] data) {
		return hash.digest(data);
	}

	public BigInteger encryptHom(BigInteger data) {
		return homCipher.Encryption(data);
	}

	public BigInteger decryptHom(BigInteger data) {
		return homCipher.Decryption(data);
	}
	
	public byte[] encryptDocAES (PDocument obj) {
//		byte[] iv = new byte[16];
//		random.nextBytes(iv);
//		docsIVs.add(iv);
		return encryptAES(Utils.serializeObject(obj) /*,iv*/);
	}

	public PDocument decryptDocAES (byte[] obj/*, int id*/) {
//		byte[] iv = docsIVs.get(id);
//		if (iv == null)
//			return null;
		try {
			return (PDocument) Utils.deserializeObject(decryptAES(obj/*, iv*/));
		} catch (EOFException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] encryptAES(byte[] data/*, byte[] ivBytes*/) {
		try {
//			IvParameterSpec iv = new IvParameterSpec(ivBytes);
			AESCipher.init(Cipher.ENCRYPT_MODE, AESKey/*, iv*/);
			return AESCipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] decryptAES(byte[] data/*, byte[] ivBytes*/) {
		try {
//			IvParameterSpec iv = new IvParameterSpec(ivBytes);
			AESCipher.init(Cipher.DECRYPT_MODE, AESKey/*, iv*/);
			return AESCipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] encryptWordKey(String word) {
		return search.xor_pad(search.wordkey(word));
	}
	
	public byte[] encryptSearch(String word) {
		return search.transform(word);
	}

	public boolean matchSearch(byte[] ctext, byte[] word_key) {
		return search.match(ctext, word_key);
	}
	
	public PostingList decryptPostingList (byte[] cipher) {
		try {
			AESCipher.init(Cipher.DECRYPT_MODE, AESKey);
			byte[] plaintext = AESCipher.doFinal(cipher);
			WordKey wordkey = new WordKey(Arrays.copyOfRange(plaintext, 0, 16));
			int df = Utils.byteArrayToInt(Arrays.copyOfRange(plaintext, 16, 20));
			PostingList postings = new ChampionList(wordkey, df);
			for (int i=0; i<df && i<12; i++) {
				int docId = Utils.byteArrayToInt(Arrays.copyOfRange(plaintext, 20+i*8, 24+i*8));
				int score = Utils.byteArrayToInt(Arrays.copyOfRange(plaintext, 24+i*8, 28+i*8));
				postings.addPosting(docId, score);
			}
			return postings;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}