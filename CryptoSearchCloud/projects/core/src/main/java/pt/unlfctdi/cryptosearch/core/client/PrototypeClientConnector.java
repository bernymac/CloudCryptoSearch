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

package pt.unlfctdi.cryptosearch.core.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import pt.unlfctdi.cryptosearch.cloud.Utils;
import pt.unlfctdi.cryptosearch.cloud.data.document.PDocument;
import pt.unlfctdi.cryptosearch.cloud.data.posting.CipheredPostingList;
import pt.unlfctdi.cryptosearch.cloud.data.posting.Posting;
import pt.unlfctdi.cryptosearch.cloud.data.posting.PostingList;
import pt.unlfctdi.cryptosearch.cloud.data.posting.TermFreq;
import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;
import pt.unlfctdi.cryptosearch.cloud.search.SearchRemote;
import pt.unlfctdi.cryptosearch.cloud.storage.CloudStorageRemote;
import pt.unlfctdi.cryptosearch.core.crypto.ClientCryptoBean;


public class PrototypeClientConnector {

	private CloudStorageRemote storage;
	private ClientCryptoBean crypto;
	private SearchRemote search;
	private Analyzer analyzer;
	
	private static Logger log = Logger.getLogger(PrototypeClientConnector.class);
	private static String host = "176.111.104.31";
//	private static String host = "127.0.0.1";

	public PrototypeClientConnector() throws RemoteException, MalformedURLException, NotBoundException{
		search = (SearchRemote)Naming.lookup("//"+host+"/SearchBean");
		storage = (CloudStorageRemote)Naming.lookup("//"+host+"/StorageBean");
		crypto = new ClientCryptoBean();
		analyzer = new EnglishAnalyzer(Version.LUCENE_40);
	}
	
	public List<Posting> query(String query) {
		try {
			List<Posting> finalScores = new ArrayList<Posting>(12);

			List<WordKey> cipheredWords = new LinkedList<WordKey>();
			TokenStream ts = analyzer.tokenStream(null, new BufferedReader(new StringReader(query)));
			try {
				ts.reset();
				while (ts.incrementToken()) {
					String word = ts.getAttribute(CharTermAttribute.class).toString();
					if (word.length() > 0 ) 
						cipheredWords.add(new WordKey(crypto.encryptWordKey(word)));
				}
				ts.end();
			} finally {
				ts.close();
			}
			List<CipheredPostingList> cipheredPostings = search.processQuery(cipheredWords);
			for (CipheredPostingList cipherPostings: cipheredPostings) {
				PostingList tfs = crypto.decryptPostingList(cipherPostings.getCipheredPostings());

				PriorityQueue<Posting> postings = new PriorityQueue<Posting>(tfs.getPostings().size());
				for (TermFreq tf: tfs.getPostings())
					postings.add(new Posting(tf.getDocId(), tf.getFreq()));
					//postings.add(new Posting(tf.getDocId(), Utils.bm25(tf.getFreq(), tfs.getDf(),
					//	docsDict.size(), docLengths.get(tf.getDocId()), sumDocLengths)));
			
				Posting posting;
				while ((posting = postings.poll()) != null) {
					//if (!removedDocs.containsKey(posting.getDocId())) {
					int j = finalScores.indexOf(posting);
					if (j == -1)
						finalScores.add(posting);
					else 
						finalScores.get(j).setScore(finalScores.get(j).getScore() + posting.getScore());
				}
			}
			Collections.sort(finalScores);
			if (finalScores.size() > 12)
				return finalScores.subList(0, 12);
			else
				return finalScores;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void addFirstDocuments (File[] docs) {
		try {
//			File f = new File(path);
//			File[] docs = f.listFiles();
			for (int i = 0; i < docs.length; i++) {
				String content = Utils.readFileAsString(docs[i]);
				List<WordKey> cipheredWords = new ArrayList<WordKey>();
				TokenStream ts = analyzer.tokenStream(null, new BufferedReader(new StringReader(content)));
				try {
					ts.reset();
					while (ts.incrementToken()) {
						String word = ts.getAttribute(CharTermAttribute.class).toString();
						if (word.length() > 0 )
							cipheredWords.add(new WordKey(crypto.encryptWordKey(word)));
					}
					ts.end();
				} finally {
					ts.close();
				}
				search.addFirstDocuments(crypto.encryptAES(docs[i].getName().getBytes()), cipheredWords);
				storage.putDoc(""+i, crypto.encryptAES(Utils.serializeObject(new PDocument(docs[i].getName(), content))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public PDocument getDocument(String title) {
//		try {
//			return getDocumentById(search.getDocumentId(title));
//		} catch (RemoteException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	public PDocument getDocumentById(String id) {
		try {
			return crypto.decryptDocAES(storage.getDoc(id));
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean removeDocument(String title) {
//		return search.removeDocument(crypto.digest(title.getBytes()));
		return false;
	}
	
	public boolean removeDocumentById(int id) {
		try {
			return search.removeDocumentById(id);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void addDocument(PDocument document) {
//		ClassicAnalyzer analyzer = new ClassicAnalyzer(Version.LUCENE_35, new StopWords().getM_Words());
//		TokenStream tokenizer = analyzer.tokenStream(null, new BufferedReader(new StringReader(document.getContent())));
//		List<WordKey> cipheredWords = new LinkedList<WordKey>();
//		try {
//			while (tokenizer.incrementToken()) {
//				String word = tokenizer.getAttribute(CharTermAttribute.class).toString();
//				if (word.length() > 0 ) {
//					Stemmer s = new Stemmer();
//					s.add(word.toCharArray(), word.length());
//					s.stem();
//					cipheredWords.add(new WordKey(crypto.encryptSearch(s.toString())));
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		CDocument cDoc = new CDocument(new WordKey(crypto.digest(document.getTitle().getBytes())),
//				cipheredWords.toArray(new WordKey[cipheredWords.size()]));
//		
//		Integer i = search.addDocumentToIndex(cDoc);
//		if (i != null)
//			cloud.putDoc(""+i, crypto.encryptDocAES(document));
	}

	public void rebuildIndex() {
		try {
			search.buildIndex();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public String getDocumentName(int id) {
		try {
			return new String(crypto.decryptAES(search.getDocumentName(id)));
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static void main (String[] args) throws Exception{
		log.addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %-5p [%c{1}] %m%n")));
		log.setLevel(Level.INFO);
		PrototypeClientConnector client = new PrototypeClientConnector();
		
		log.info("Setup done. Building index...");
		File f = new File("/Users/bernardo/Dropbox/WorkspacePHD/CryptoSearchCloud/docs");
		client.addFirstDocuments(f.listFiles());
		log.info("Index Finished!");
		client.rebuildIndex();
		log.info("Starting query...");
//		query(client, "IETF payload optimize retransmit threshold RFC protocol  trusted network address");
//		query(client, "IETF payload optimize retransmit threshold");
//		query(client, "IETF payload");
		query(client, "rTp");
		log.info("Query finished!");
	}
	
	public static void query(PrototypeClientConnector client, String q) {
		List<Posting> postings = client.query(q);
		if (postings == null || postings.size() == 0)
			log.info("No match found for the query!");
		else
			for (int i = 0; i < postings.size(); i++) 
				log.info(postings.get(i).getDocId() + " " + client.getDocumentName(postings.get(i).getDocId()) + " " + postings.get(i).getScore());
	}
}