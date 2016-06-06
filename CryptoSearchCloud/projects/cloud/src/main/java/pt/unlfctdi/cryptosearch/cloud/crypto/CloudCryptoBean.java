/**
 *    Copyright 2013 Bernardo Ferreira

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package pt.unlfctdi.cryptosearch.cloud.crypto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import pt.unlfctdi.cryptosearch.cloud.Utils;
import pt.unlfctdi.cryptosearch.cloud.data.posting.ChampionList;
import pt.unlfctdi.cryptosearch.cloud.data.posting.PostingList;
import pt.unlfctdi.cryptosearch.cloud.data.posting.TermFreq;
import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public class CloudCryptoBean {

	private SecretKeySpec AESKey;
	private Cipher AESCipher;
	private Paillier homCipher;
	
	public CloudCryptoBean() {
		try {
			byte[] raw = new byte[] { 
                    0x73, 0x2f, 0x2d, 0x33, (byte)0xc8, 0x01, 0x73, 
                    0x2b, 0x72, 0x06, 0x75, 0x6c, (byte)0xbd, 0x44, 
                    (byte)0xf9, (byte)0xc1};
//			File f = new File("/home/bernardo/Dropbox/WorkspacePHD/CryptoSearchCloud/key");
//			if (f.exists()) {
//				FileInputStream fis = new FileInputStream(f);
//				raw = new byte[16];
//				int read = fis.read(raw);
//				assert read == 16;
//				fis.close();
//			}	else {
//				KeyGenerator kgen = KeyGenerator.getInstance("AES");
//				kgen.init(128);
//				SecretKey skey = kgen.generateKey();
//				raw = skey.getEncoded();
//				FileOutputStream fos = new FileOutputStream(f);
//				fos.write(raw);
//				fos.flush();
//				fos.close();
//			}
			AESKey = new SecretKeySpec(raw, "AES");
			AESCipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
			homCipher = new Paillier();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public byte[] encryptPostingList (PostingList postings) {
//		try {
//			AESCipher.init(Cipher.ENCRYPT_MODE, AESKey);
//			return AESCipher.doFinal(Utils.serializeObject(postings));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	public PostingList decryptPostingList (byte[] postings) throws EOFException{
//		try {
//			AESCipher.init(Cipher.DECRYPT_MODE, AESKey);
//			return (PostingList) Utils.deserializeObject(AESCipher.doFinal(postings));
//		} catch (EOFException e) {
//			throw e;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public BigInteger encryptHom(BigInteger data) {
		return homCipher.Encryption(data);
	}

	public BigInteger decryptHom(BigInteger data) {
		return homCipher.Decryption(data);
	}
	
	public BigInteger homAdd (BigInteger em1, BigInteger em2) {
    	return Paillier.eAdd(em1, em2);
    }
	
	public byte[] encryptPostingList(PostingList p) {
		try {
			AESCipher.init(Cipher.ENCRYPT_MODE, AESKey);
			byte[] plaintext = new byte[16+4+p.getPostings().size()*8];
			for (int i = 0; i < 16; i++)
				plaintext[i] = p.getTerm().getContents()[i];

			byte[] df = Utils.intToByteArray(p.getDf());
			for (int i = 0; i < 4; i++)
				plaintext[16+i] = df[i];

			int i = 0;
			for (TermFreq tf: p.getPostings()) {
				byte[] docId = Utils.intToByteArray(tf.getDocId());
				byte[] score = Utils.intToByteArray(tf.getFreq());
				for (int j = 0; j < 4; j++) {
					plaintext[20+i*8+j] = docId[j];
					plaintext[24+i*8+j] = score[j];
				}
				i++;
			}
			return AESCipher.doFinal(plaintext);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PostingList decryptPostingListFromDisk (DataInputStream dis) {
		try {
			AESCipher.init(Cipher.DECRYPT_MODE, AESKey);
			int length = dis.readInt();
			byte[] cipher = new byte[length];
			dis.read(cipher, 0, length);
			byte[] plaintext = AESCipher.doFinal(cipher);
			
			WordKey wordkey = new WordKey(Arrays.copyOfRange(plaintext, 0, 16));
			int df = Utils.byteArrayToInt(Arrays.copyOfRange(plaintext, 16, 20));
			PostingList postings = new ChampionList(wordkey, df);
			for (int i=0; i<df; i++) {
				int docId = Utils.byteArrayToInt(Arrays.copyOfRange(plaintext, 20+i*8, 24+i*8));
				int score = Utils.byteArrayToInt(Arrays.copyOfRange(plaintext, 24+i*8, 28+i*8));
				postings.addPosting(docId, score);
			}
			
			return postings;
		} catch (EOFException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public DataInputStream getCipheredInputStream(File f) {
		try {
			AESCipher.init(Cipher.DECRYPT_MODE, AESKey);
			return new DataInputStream(new CipherInputStream(new BufferedInputStream(new FileInputStream(f)),AESCipher));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public DataOutputStream getCipheredOutputStream(File f) {
		try {
			AESCipher.init(Cipher.ENCRYPT_MODE, AESKey);
			return new DataOutputStream(new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(f)),AESCipher));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
