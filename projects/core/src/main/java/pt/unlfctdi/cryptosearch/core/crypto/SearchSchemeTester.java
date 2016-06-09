/**
 *    Copyright 2013 Bernardo Luís da Silva Ferreira

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

package pt.unlfctdi.cryptosearch.core.crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;

import pt.unlfctdi.cryptosearch.cloud.data.document.PDocument;
import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public class SearchSchemeTester {

	public static void main (String[] args) throws Exception{
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(128);
		MessageDigest hash = MessageDigest.getInstance("SHA-256");
		SearchScheme cipher = new SearchScheme(generator.generateKey().getEncoded(), hash, Mac.getInstance("HmacSHA1"), new SecureRandom(), 16);

		WordKey c1 = new WordKey(cipher.transform("ola"));
		WordKey c2 = new WordKey(cipher.transform("ola"));
		System.out.println(c1.equals(c2));
		
//		PDocument[] docs = loadFiles();
//		byte[][][] cipheredDocs = new byte[docs.length][][];
//		ClassicAnalyzer analyzer = new ClassicAnalyzer(Version.LUCENE_35, new StopWords().getM_Words());
//		System.out.println(System.currentTimeMillis()+" Ciphering Documents...");
//		for (int i = 0; i < docs.length; i++) {
//			List<String> words = new LinkedList<String>();
//			TokenStream tokenizer = analyzer.tokenStream(null, new BufferedReader(new StringReader(docs[i].getContent())));
//			while (tokenizer.incrementToken()) {
//				String word = tokenizer.getAttribute(CharTermAttribute.class).toString();
//				if (word.length() > 0 ) {
//					Stemmer s = new Stemmer();
//					s.add(word.toCharArray(), word.length());
//					s.stem();
//					words.add(s.toString());
//				}
//			}
//			cipheredDocs[i] = cipher.transform(words.toArray(new String[words.size()]));
//		}
//		System.out.println(System.currentTimeMillis()+" ...Ciphering Finished");
//		
//		double sum = 0.0;
//		for (int i = 0; i < cipheredDocs.length; i++) {
//			sum += cipheredDocs[i].length;
//		}
//		
//		double df = 0.0;
//		Posting[] search = new Posting[cipheredDocs.length];
//		for (int i = 0; i < cipheredDocs.length; i++) {
//			double tf = cipher.count(cipheredDocs[i], cipher.wordkey("rtp"));
//			if (tf > 0)
//				df++;
//			search[i] = new Posting(i,tf);
//		}
//		for (int i = 0; i < search.length; i++) {
//			search[i].setScore(bm25(search[i].getScore(), df, (double)cipheredDocs.length, (double)cipheredDocs[i].length, sum/(double)cipheredDocs.length));
//		}
//		
//		Arrays.sort(search);
//		
//		for (int i = 0; i < search.length && i < 12; i++)
//			System.out.println(search[i].getDocId()+" "+search[i].getScore());
	}
	
	private static PDocument[] loadFiles() throws Exception {
//		File f = new File("C:/Users/Rafael/Dropbox/WorkspacePHD/CryptoSearch/docs");
		File f = new File("C:/rfcs");
		File[] docs = f.listFiles();
		PDocument[] documents = new PDocument[docs.length];
		for (int i = 0; i < docs.length; i++) {
			documents[i] = new PDocument(docs[i].getName(), readFileAsString(docs[i]));
		}
		return documents;
	}

	private static String readFileAsString(File f) throws java.io.IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}
	
	private static double bm25(double n, double df, double nDocs, double docLength, double avgDocLength) {
		final double k1 = 1.2;
		final double b = 0.75;
		final double tf = n / docLength;
		final double idf = Math.log10(nDocs / df);
		return idf * (((k1+1)*tf) / (k1*((1-b)+b*(docLength/avgDocLength))+tf));
	}
}
