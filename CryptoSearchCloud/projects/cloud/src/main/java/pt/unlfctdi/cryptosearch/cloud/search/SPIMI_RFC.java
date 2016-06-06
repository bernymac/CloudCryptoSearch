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

//package pt.unlfctdi.cryptosearch.cloud.search;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.PriorityQueue;
//
//import org.apache.log4j.ConsoleAppender;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PatternLayout;
//import org.apache.lucene.analysis.standard.ClassicAnalyzer;
//import org.apache.lucene.util.Version;
//
//import pt.unlfctdi.cryptosearch.cloud.Utils;
//import pt.unlfctdi.cryptosearch.cloud.data.document.CDocument;
//import pt.unlfctdi.cryptosearch.cloud.data.document.PDocument;
//import pt.unlfctdi.cryptosearch.cloud.data.posting.Posting;
//import pt.unlfctdi.cryptosearch.cloud.data.posting.PostingList;
//import pt.unlfctdi.cryptosearch.cloud.data.posting.RunsPostingList;
//import pt.unlfctdi.cryptosearch.cloud.data.posting.TermFreq;
//import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;
//import pt.unlfctdi.cryptosearch.core.crypto.ClientCryptoBean;
//import pt.unlfctdi.cryptosearch.core.crypto.CryptoLocal;
//
//public class SPIMI_RFC {
//
//	private Map<WordKey,Integer> docsDict;
////	private Map<String,PostingList> index;
//	private Map<WordKey,byte[]> index;
//	private String currentIndexName;
//	private List<Integer> docLengths;
//	private int sumDocLengths;
//	private int docCount;
//	private ClassicAnalyzer analyzer;
//
//	private static Logger log = Logger.getLogger(SPIMI_RFC.class);
//	private static double maxMemory = 5;	//in GB
//	private static int k = 12;
//	private static String mainPath = "/home/bernardo/Dropbox/WorkspacePHD/CryptoSearchStandalone/";
//	
//	private CryptoLocal crypto = new ClientCryptoBean();
//
//	public SPIMI_RFC() {
//		log.addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %-5p [%c{1}] %m%n")));
//		log.setLevel(Level.ALL);
//	}
//	
//	@Override
//	public void buildIndex (CDocument[] documents) {
//		boolean hasToMerge = processDocs(documents);
//		if (hasToMerge)
//			mergeRuns();
//	}
//
//	@Override
//	public List<Posting> processQuery (List<WordKey> query) {
//		try {
//			List<Posting> finalScores = new ArrayList<Posting>(k);
//			for (WordKey term: query) {
//					//retrieve index entry if it exists or continue
////					PostingList tfs = index.get(term);
//					PostingList tfs = crypto.decryptPostingList(index.get(term));
////					if (tfs == null && currentIndexName != null) {
////						File[] indexParts = new File(mainPath+"index").listFiles();
////						Arrays.sort(indexParts);
////						for (File f: indexParts) 
////							if (term.compareTo(f.getName()) < 0) {
////								Utils.writeObjectToDisk(index, mainPath+"index/"+currentIndexName);
////								index = null;
////								System.gc();
////								index = (Map<String, PostingList>) Utils.readObjectFromDisk(f);
////								currentIndexName = f.getName();
////								break;
////							}
////						tfs = index.get(term);
////					}
//					if (tfs == null)
//						continue;
//					//calculate scores and sort them
//					PriorityQueue<Posting> postings = new PriorityQueue<Posting>(tfs.getPostings().size());
//					for (TermFreq tf: tfs.getPostings())
//						postings.add(new Posting(tf.getDocId(), Utils.bm25(tf.getFreq(), tfs.getPostings().size(),
//								docsDict.size(), docLengths.get(tf.getDocId()), sumDocLengths)));
//					//merge with final scores list
//					Posting posting;
//					while ((posting = postings.poll()) != null && tfs.getPostings().size()-postings.size() <= k) {
////						if (!removedDocs.containsKey(posting.getDocId())) {
//						int j = finalScores.indexOf(posting);
//						if (j == -1)
//							finalScores.add(posting);
//						else 
//							finalScores.get(j).setScore(finalScores.get(j).getScore() + posting.getScore());
//					}
//				}
//			Collections.sort(finalScores);
//			return finalScores.subList(0, k);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	private void mergeRuns() {
////		log.info("Processed runs. Merging...");
////		try {
////			File f = new File(mainPath+"runs");
////			File[] runs = f.listFiles();
////			if (runs.length <= 1)
////				return; //TODO copiar para outra pasta?
////			ObjectInputStream[] buffers = new ObjectInputStream[runs.length];
////			PostingList[] heads = new PostingList[runs.length];
////			for (int i = 0; i < runs.length; i++) {
////				buffers[i] = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f),134217728));
////				heads[i] = (PostingList) buffers[i].readObject();
////			}
////			while (heads[0] != null) {
////				for (int i = 1; i < heads.length; i++)
////					while (heads[i] != null && heads[i].compareTo(heads[0]) <= 0) 
////						writePosting(i, index, buffers, heads);
////				if (buffers[0].available() > 0) 
////					heads[0] = (PostingList) buffers[0].readObject();
////				else {
////					heads[0] = null;
////					buffers[0].close();
////					for (int i = 1; i < heads.length; i++)
////						while (heads[i] != null)
////							writePosting(i, index, buffers, heads);
////				}
////				//if memory full, write index partition to disk
////				if (heads[0] == null) {
////					currentIndexName = "z";
////					Utils.writeObjectToDisk(index, mainPath+"index/"+currentIndexName);
////				} else if (Runtime.getRuntime().totalMemory()/1073741824 >= maxMemory) {
////					currentIndexName = heads[0].getTerm();
////					Utils.writeObjectToDisk(index, mainPath+"index/"+currentIndexName);
////					index = new HashMap<String,PostingList>();
////					System.gc();
////				}
////			}
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
//	}
//
////	private void writePosting(int i, Map<String,PostingList> index, ObjectInputStream[] buffers, PostingList[] heads) {
////		try {
////			PostingList indexedPostings = index.get(heads[i].getTerm());
////			if (indexedPostings == null)
////				index.put(heads[i].getTerm(), heads[i]);
////			else
////				for (TermFreq tf: heads[i].getPostings().values()) 
////					indexedPostings.addPosting(tf);	
////			if (buffers[i].available() <= 0) {
////				heads[i] = null;
////				buffers[i].close();
////			} else
////				heads[i] = (PostingList) buffers[i].readObject();
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////	}
//
//
//	private boolean processDocs(CDocument[] docs) {
//		boolean hasToMerge = false;
//		docsDict = new HashMap<WordKey,Integer>(docs.length);
//		docLengths = new ArrayList<Integer>(docs.length);
//		Map<WordKey,PostingList> index = new HashMap<WordKey,PostingList>();
//		analyzer = new ClassicAnalyzer(Version.LUCENE_35, new StopWords().getM_Words());
//		for (int i = 0; i < docs.length; i++) {
//			docsDict.put(docs[i].getTitle(), i);
//			//TODO cloud.putDoc(""+i, crypto.encryptDocAES(docs[i]));
//			int nTerms = 0;
//			for (WordKey word: docs[i].getContent()) {
//				//get posting list
//				PostingList postings = index.get(word);
//				if (postings == null) {
//					postings = new RunsPostingList(word);
//					index.put(word, postings);
//				}
//				//add posting
//				postings.addPosting(i, 1);
//				nTerms++;
//				if (Runtime.getRuntime().totalMemory()/1073741824 >= maxMemory) {
//					//sort and store to disk
//					PostingList[] tfs = new PostingList[index.size()];
//					index.values().toArray(tfs);
//					Arrays.sort(tfs);
//					writePostingListsToDisk(i, tfs);
//					index = new HashMap<WordKey,PostingList>();
//					System.gc();
//					hasToMerge = true;
//				}
//			}
//			docLengths.add(nTerms);
//			sumDocLengths += nTerms;
//		}
//		if (index.size() > 0 && hasToMerge) {
//			PostingList[] tfs = new PostingList[index.size()];
//			index.values().toArray(tfs);
//			Arrays.sort(tfs);
//			writePostingListsToDisk(-1, tfs);
//			index = new HashMap<WordKey,PostingList>();
//			System.gc();
//		}
//		this.index = new HashMap<WordKey,byte[]>();
//		for (PostingList postings: index.values()) 
//			this.index.put(postings.getTerm(), crypto.encryptPostingList(postings));
//		docCount = docsDict.size();
//		return hasToMerge;
//	}
//
//	private void writePostingListsToDisk(int runId, PostingList[] tfs) {
//		try {
//			//escrever numero de caracteres e numero de posting lists
//			File f = new File(mainPath+"runs/"+runId);
//			f.createNewFile();
//			FileOutputStream fos = new FileOutputStream(f);
//			BufferedOutputStream bos = new BufferedOutputStream(fos,134217728);
//			ObjectOutputStream oos = new ObjectOutputStream(bos);
//			for (PostingList postings: tfs)
//				oos.writeObject(postings);
//			oos.flush();
//			oos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public int getDocumentId(String title) {
//		return docsDict.get(title);
//	}
//
//	public boolean addDocumentToIndex(String docPath) {
//		//check if document exists
////		File f = new File(docPath);
////		if (!f.exists())
////			return false;
////		PDocument doc = new PDocument(f.getName(), Utils.readFileAsString(f));
////		Integer docId = docsDict.get(doc.getTitle());
////		if (docId != null) 
////			return false;
////		else {
////			docId = docCount;
////			docCount++;
////			docsDict.put(doc.getTitle(), docId);
//////			cloud.putDoc(""+docId, crypto.encryptDocAES(doc));
////		}
////		
////		Map<String,PostingList> indexRef;
////		if (currentIndexName != null)
////			indexRef = new HashMap<String,PostingList>();
////		else
////			indexRef = index;
////		TokenStream tokenizer = analyzer.tokenStream(null, new BufferedReader(new StringReader(doc.getContent())));
////		int nTerms = 0;
////		try {
////			while (tokenizer.incrementToken()) {
////				String word = tokenizer.getAttribute(CharTermAttribute.class).toString();
////				if (word.length() > 0) {
////					Stemmer s = new Stemmer();
////					s.add(word.toCharArray(), word.length());
////					s.stem();
////					String term = s.toString();
////					//get posting list
////					PostingList postings = indexRef.get(term);
////					if (postings == null) {
////						postings = new PostingList(term);
////						indexRef.put(term, postings);
////					}
////					//add posting
////					postings.addPosting(docId, 1);
////					nTerms++;
////				}
////			}
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
////		docLengths.add(nTerms);
////		sumDocLengths += nTerms;
////		if (currentIndexName != null) {
////			;//TODO merge das novas entradas com os indices em disco
////		}
//		return true;
//	}
//
//	@Override
//	public void rebuildIndex() {
//		// TODO Shouldn't be necessary unless auxiliary index is kept in memory
//		//may be necessary anyways if too many inserts increase size of index partitions beyond available memory
//		//in that case, copy files to runs folder and do normal runs merge 
//		//(maybe different method can be faster because there's less requirements than in mergeRuns)
//		//probably something like: open index partition, read to memory until full, write to new partition file; continue
//	}
//
//	public boolean removeDocument(String title) {
////		Integer docId = docsDict.remove(title);
////		if (docId == null)
////			return false;
////		sumDocLengths -= docLengths.get(docId);
////		docLengths.set(docId, null);
////		for (PostingList postings: index.values())
////			postings.removePosting(docId);
////		if (currentIndexName != null) {
////			//TODO remove postings from index in disk
////		}
//		return true;
//	}
//
//	@Override
//	public boolean removeDocumentById(int docId) {
////		String title = null;
////		for (Map.Entry<String, Integer> entry: docsDict.entrySet())
////			if (entry.getValue().equals(docId)) {
////				title = entry.getKey();
////				break;
////			}
////		if (title == null)
////			return false;
////		return removeDocument(title);
//		return true;
//	}
//	
//	public void encryptDocs(String filePath) {
//		File f = new File(filePath);
//		File[] docs = f.listFiles();
//		for (int i = 0; i < docs.length; i++) {
//			crypto.encryptDocAES(new PDocument(docs[i].getName(), Utils.readFileAsString(docs[i])));
//		}
//	}
//	
//	public static void main(String[] args) throws Exception {
//		SPIMI_RFC indexer = new SPIMI_RFC();
//		
//		log.info("Setup done. Building index...");
//		indexer.encryptDocs("/home/bernardo/Desktop/rfcs");
////		indexer.buildIndex("/home/bernardo/Desktop/rfcs");
////		indexer.buildIndex("C:/rfcs");
////		indexer.buildIndex("C:/Users/Rafael/Dropbox/WorkspacePHD/CryptoSearchStandalone/docs");
//		log.info("...Index Finished.");
////		query(indexer, "RTP payload optimize retransmit threshold XMPP protocol  trusted network address");
//		
////		log.info("Adding new document...");
////		indexer.addDocumentToIndex("C:/rfcs/rfc783.txt");
////		log.info("...Document added.");
////		query(indexer, "retransmit");
//	}
//
//	@Override
//	public Integer addDocumentToIndex(CDocument document) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
////	@Override
//	public boolean removeDocument(byte[] title) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	
////	private static void query(SPIMI_RFC indexer, String q) {
////		List<Posting> postings = indexer.processQuery(q);
////		if (postings == null || postings.size() == 0)
////			log.info("No match found for the query!");
////		else
////			for (int i = 0; i < postings.size(); i++) 
////				log.info(postings.get(i).getDocId() + " " + postings.get(i).getScore());
////	}
//
//}
