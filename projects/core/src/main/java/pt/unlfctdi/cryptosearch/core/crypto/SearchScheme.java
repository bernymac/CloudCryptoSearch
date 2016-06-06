package pt.unlfctdi.cryptosearch.core.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SearchScheme {

	private MessageDigest hash;
	private Mac mac;
	private Random random;
	private Key masterKey;
	private int csize;
	
	
	public SearchScheme (byte[] keyBytes, MessageDigest sha256Hash, Mac sha1Hmac, Random secureRand, int cipherSize) {
		hash = sha256Hash;
		mac = sha1Hmac;
		masterKey = new SecretKeySpec(keyBytes, "HmacSHA1");
		random = secureRand;
		csize = cipherSize;
	}
	
	public boolean match(byte[][] ctexts, byte[] word_key) {
	    for (byte[] c: ctexts)
	        if (match(c, word_key))
	            return true;
	    return false;
	}
	
	public int count(byte[][] ctexts, byte[] word_key) {
		int count = 0;
	    for (byte[] c: ctexts)
	        if (match(c, word_key))
	            count++;
	    return count;
	}
	
	public byte[][] transform(String[] words) {
	    byte[][] res = new byte[words.length][];
	    for (int i = 0; i < words.length; i++)
	        res[i] = transform(words[i]);
	    
		Arrays.sort(res, new vector_compare());
	    return res;
	}
	
	public boolean match(byte[] ctext, byte[] word_key) {
	    assert(ctext.length == csize);

	    byte[] xorpad = xor_pad(word_key);
	    for (int i = 0; i < csize; i++) 
	    	xorpad[i] ^= ctext[i];

	    byte[] salt = Arrays.copyOfRange(xorpad, 0, csize/2);
	    byte[] cf = Arrays.copyOfRange(xorpad, csize/2, csize);
	    
	    try {
			mac.init(new SecretKeySpec(word_key, "HMAC_SHA1_ALGORITHM"));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	    byte[] f = mac.doFinal(salt);

	    return new vector_compare().compare(Arrays.copyOf(f,csize/2), cf) == 0;
	}

	public byte[] transform(String word) {
	    byte[] word_key = wordkey(word);

	    byte[] salt = new byte[csize / 2];
		random.nextBytes(salt);

		try {
			mac.init(new SecretKeySpec(word_key, "HMAC_SHA1_ALGORITHM"));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	    byte[] f = mac.doFinal(salt);
	    //Resize f to csize/2

	    byte[] x = new byte[csize];
	    for (int i = 0; i < csize/2; i++) {
	    	x[i] = salt[i];
	    	x[i+csize/2] = f[i];
	    }

	    byte[] xorpad = xor_pad(word_key);
	    for (int i = 0; i < csize; i++)
	        x[i] ^= xorpad[i];

	    return x;
	}

	public byte[] wordkey(String word)	{
	    try {
			mac.init(masterKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	    return mac.doFinal(word.getBytes());
	}

	public byte[] xor_pad(byte[] word_key) {
	    byte[] v = hash.digest(word_key);
	    assert(v.length >= csize);
	    return Arrays.copyOf(v, csize);
	}
	
	private class vector_compare implements Comparator<byte[]> {
		@Override
		public int compare(byte[] a, byte[] b) {
			if (a.length != b.length)
		        return a.length - b.length;
		    for (int i = 0; i < a.length; i++)
		        if (a[i] != b[i])
		            return a[i] - b[i];
		    return 0;
		}
	};
	
}
