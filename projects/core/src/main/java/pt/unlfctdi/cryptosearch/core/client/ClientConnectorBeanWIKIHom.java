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
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import pt.unlfctdi.cryptosearch.cloud.data.document.CDocument;
import pt.unlfctdi.cryptosearch.cloud.data.document.PDocument;
import pt.unlfctdi.cryptosearch.cloud.data.posting.HomPosting;
import pt.unlfctdi.cryptosearch.cloud.data.posting.Posting;
import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;
import pt.unlfctdi.cryptosearch.cloud.search.SPIMI_WIKI_HOM;
import pt.unlfctdi.cryptosearch.cloud.storage.CloudStorageRemote;
import pt.unlfctdi.cryptosearch.core.crypto.ClientCryptoBean;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;


public class ClientConnectorBeanWIKIHom implements ClientConnectorLocal {

	private CloudStorageRemote cloud;
	private ClientCryptoBean crypto;
	private SPIMI_WIKI_HOM search;
	private Analyzer analyzer;
	
	private static Logger log = Logger.getLogger(ClientConnectorBeanWIKIHom.class);

	public ClientConnectorBeanWIKIHom() {
		crypto = new ClientCryptoBean();
		search = new SPIMI_WIKI_HOM();
		analyzer = new EnglishAnalyzer(Version.LUCENE_40);
//		analyzer = new PortugueseAnalyzer(Version.LUCENE_40);
//		analyzer = new SpanishAnalyzer(Version.LUCENE_40);
	}
	
	@Override
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
			List<HomPosting> homScores = search.processQuery(cipheredWords);
			for (HomPosting posting: homScores) {
				finalScores.add(new Posting(posting.getDocId(), crypto.decryptHom(posting.getScore()).intValue()));
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

	@Override
	public void addFirstDocuments (String xmlFile) {
		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(xmlFile);
		try {	
			wxsp.setPageCallback(new PageCallbackHandler() { public void process(WikiPage page) {
				if (page.isDisambiguationPage() || page.isRedirect() || page.isSpecialPage())
					return;
				List<WordKey> cipheredWords = new ArrayList<WordKey>();
				try {
					TokenStream ts = analyzer.tokenStream(null, new BufferedReader(new StringReader(page.getText())));
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
				} catch (IOException e) {
					e.printStackTrace();
				}
				search.addFirstDocuments(new CDocument(new WordKey(crypto.digest(page.getTitle().getBytes())),
						cipheredWords.toArray(new WordKey[cipheredWords.size()])));
				//store doc in the cloud
//				cloud.putDoc(""+i, crypto.encryptDocAES(documents[i]));
			}});
			wxsp.parse();
			search.buildIndex();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PDocument getDocument(String title) {
		return getDocumentById(search.getDocumentId(title));
	}

	@Override
	public PDocument getDocumentById(int id) {
		try {
			return crypto.decryptDocAES(cloud.getDoc(""+id));
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean removeDocument(String title) {
//		return search.removeDocument(crypto.digest(title.getBytes()));
		return false;
	}
	
	@Override
	public boolean removeDocumentById(int id) {
		return search.removeDocumentById(id);
	}

	@Override
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

	@Override
	public void rebuildIndex() {
		search.buildIndex();
	}

	
	public static void main (String[] args) {
		log.addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %-5p [%c{1}] %m%n")));
		log.setLevel(Level.INFO);
		ClientConnectorLocal client = new ClientConnectorBeanWIKIHom();
		
		log.info("Setup done. Building index...");
//		client.addFirstDocuments("/home/bernardo/Desktop/enwiki-latest-pages-articles.xml");
//		client.addFirstDocuments("/home/bernardo/Desktop/ptwiki-20121027-pages-articles.xml");
//		client.addFirstDocuments("/home/bernardo/Desktop/eswiki-20121018-pages-articles.xml");
//		log.info("Index Finished!");
		client.rebuildIndex();
		log.info("Starting query...");
		query(client, "IETF payload optimize retransmit threshold RFC protocol  trusted network address");
//		query(client, "IETF payload optimize retransmit threshold");
//		query(client, "IETF payload");
		log.info("Query finished!");
	}
	
	private static void query(ClientConnectorLocal client, String q) {
		List<Posting> postings = client.query(q);
		if (postings == null || postings.size() == 0)
			log.info("No match found for the query!");
		else
			for (int i = 0; i < postings.size(); i++) 
				log.info(postings.get(i).getDocId() + " " + postings.get(i).getScore());
	}
}