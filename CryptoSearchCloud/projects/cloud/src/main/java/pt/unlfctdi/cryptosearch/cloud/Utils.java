package pt.unlfctdi.cryptosearch.cloud;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import pt.unlfctdi.cryptosearch.cloud.data.posting.PostingList;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Utils {

	public static void cipheredWritePostingsToDisk(Cipher cipher, Collection<PostingList> data, String filePath) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			BufferedOutputStream bos = new BufferedOutputStream(cos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			for (PostingList p: data)
				oos.writeObject(p);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public static Object cipheredReadObjectFromDisk(Cipher cipher, String filePath) {
//			FileInputStream fis = new FileInputStream(filePath);
//			CipherInputStream cis = new CipherInputStream(fis, cipher);
//			BufferedInputStream bis = new BufferedInputStream(cis);
//			ObjectInputStream ois = new ObjectInputStream(bis);
//			Object p = ois.readObject();
//			ois.close();
//			return p;
//		
//	}
	
	public static byte[] serializeObject(Object data) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(baos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(data);
			oos.flush();
			byte[] bytes = baos.toByteArray();
			oos.close();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object deserializeObject (byte[] data) throws EOFException{
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			BufferedInputStream bis = new BufferedInputStream(bais);
			ObjectInputStream ois = new ObjectInputStream(bis);
			Object result = ois.readObject();
			ois.close();
			return result;
		} catch (EOFException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeObjectToDisk (Object data, String filePath) {
		try {
			File f = new File(filePath);
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(data);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object readObjectFromDisk (String filePath) {
		File f = new File(filePath);
		return readObjectFromDisk(f);
	}
	
	public static Object readObjectFromDisk (File f) {
		try {
			FileInputStream bais = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(bais);
			ObjectInputStream ois = new ObjectInputStream(bis);
			Object result = ois.readObject();
			ois.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void writeCiphersToDisk(byte[][] ciphers, String filePath) {
		try {
			String[] encodedCiphers = new String[ciphers.length];
			BASE64Encoder encoder = new BASE64Encoder();
			for (int i = 0; i < ciphers.length; i++)
				encodedCiphers[i] = encoder.encode(ciphers[i]);
			File f = new File(filePath);
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(encodedCiphers);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[][] readCiphersFromDisk(String filePath) {
		try {
			File f = new File(filePath);
			FileInputStream bais = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(bais);
			ObjectInputStream ois = new ObjectInputStream(bis);
			String[] encodedCiphers = (String[]) ois.readObject();
			ois.close();
			byte[][] ciphers = new byte[encodedCiphers.length][];
			BASE64Decoder decoder = new BASE64Decoder();
			for (int i = 0; i < ciphers.length; i++)
				ciphers[i] = decoder.decodeBuffer(encodedCiphers[i]);
			return ciphers;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String readFileAsString(File f)  {
		try {
			StringBuffer fileData = new StringBuffer(1000);
			BufferedReader reader = new BufferedReader(new FileReader(f));
			char[] buf = new char[1024];
			int numRead=0;
			while((numRead=reader.read(buf)) != -1){
				fileData.append(buf, 0, numRead);
			}
			reader.close();
			return fileData.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] intToByteArray (int i) {
		return new byte[] {
				(byte)((i >> 24) & 0xff),
				(byte)((i >> 16) & 0xff),
				(byte)((i >> 8) & 0xff),
				(byte)((i >> 0) & 0xff)
		};
	}
	
	public static int byteArrayToInt (byte[] bytes) {
		if (bytes == null || bytes.length != 4) 
			return 0x0;
		return 	((0xff & bytes[0]) << 24 |
				(0xff & bytes[1]) << 16 |
				(0xff & bytes[2]) << 8 |
				(0xff & bytes[3]) << 0
				);
	}
	
	public static double bm25(int n, int df, int nDocs, int docLength, int sumDocLengths) {
		final double k1 = 1.2;
		final double b = 0.75;
		final double avgDocLength = (double) sumDocLengths / nDocs;
		final double tf = (double) n / docLength;
		final double idf = Math.log10((double) nDocs / df);
		return idf * (((k1+1)*tf) / (k1*((1-b)+b*(docLength/avgDocLength))+tf));
	}
	
}
