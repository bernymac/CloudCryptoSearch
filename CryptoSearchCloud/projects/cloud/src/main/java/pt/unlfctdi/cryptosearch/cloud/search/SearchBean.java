package pt.unlfctdi.cryptosearch.cloud.search;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.PriorityQueue;
//
//import javax.annotation.PostConstruct;
//import javax.ejb.Local;
//
//import org.jboss.ejb3.annotation.Service;
//
//import pt.unlfctdi.cryptosearch.cloud.data.document.CDocument;
//import pt.unlfctdi.cryptosearch.cloud.data.posting.Posting;
//import pt.unlfctdi.cryptosearch.cloud.data.posting.TermIdDocId;
//import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;
//
//public class SearchBean implements SearchLocal {
//
////	private String indexPath;
//
//	private Map<byte[],Integer> docsDict;
//
//	private Map<WordKey,Integer> termsDict;
//
////	private List<List<HomPosting>> postings;
//	private List<List<Posting>> postings;
//	
//	private List<List<Posting>> tf;
//	
//	private List<Double> docLengths;
//	
//	private int docIdCount;
//	
//	private int k;
//	
//	private int indexThreshold;
//	
//	private Map<Integer, byte[]> removedDocs;
//
//
////	@SuppressWarnings("unchecked")
//	@PostConstruct
//	public void start() {
//		k = 12;
//		indexThreshold = 0;
//		removedDocs = new HashMap<Integer,byte[]>();
////		indexPath = "C:/Users/Bernardo/Dropbox/WorkspacePHD/CryptoSearch/index/";
////		postings = (HomPosting[][]) Utils.readObjectFromDisk(indexPath+"postings");
////		termsDict = (Map<String,Integer>) Utils.readObjectFromDisk(indexPath+"termsDict");
////		docsDict = (Map<String,Integer>) Utils.readObjectFromDisk(indexPath+"docsDict");
//	}
//
//
//	@Override
//	public Posting[] processQuery(List<WordKey> query) {
//		try {
//			if (termsDict == null)
//				return null;
//			//			List<HomPosting> eScores = new LinkedList<HomPosting>();
//			List<Posting> eScores = new LinkedList<Posting>();
//			for (WordKey term: query) {
//				Integer termId = termsDict.get(term);
//				if (termId != null) {
//					//						List<HomPosting> postingList = postings.get(termId);
//					List<Posting> postingList = postings.get(termId);
//					for (int i = 0; /*i < k &&*/ i < postingList.size(); i++) {
//						//							HomPosting posting = postingList.get(i);
//						Posting posting = postingList.get(i);
//						if (!removedDocs.containsKey(posting.getDocId())) {
//							int j = eScores.indexOf(posting);
//							if (j == -1)
//								eScores.add(posting.clone());
//							else {
//								//									HomPosting eScore = eScores.get(j);
//								//									eScore.setScore(Paillier.eAdd(eScore.getScore(), posting.getScore()));
//								eScores.get(j).setScore(eScores.get(j).getScore() + posting.getScore());
//							}
//						}
//					}
//				}
//			}
//			Posting[] finalScores = new Posting[eScores.size()];
//			finalScores = eScores.toArray(finalScores);
//			Arrays.sort(finalScores);
//			return finalScores;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Override
//	public int getDocumentId(String title) {
//		return docsDict.get(title);
//	}
//	
//	@Override
//	public void buildIndex(CDocument[] docs) {
//		buildPostingLists(buildTermIdsDocIds(docs));
//		finalScores();
//	}
//
//	//Extract all (TermId,DocId) pairs and build term and doc dicts
//	private List<TermIdDocId> buildTermIdsDocIds(CDocument[] docs) {
//		try{
//			docsDict = new HashMap<byte[],Integer>(docs.length);
//			termsDict = new HashMap<WordKey,Integer>();
//			List<TermIdDocId> termDocPairs = new LinkedList<TermIdDocId>();
//			int nTerms = 0;
//			for (int i = 0; i < docs.length; i++) {
//				docsDict.put(docs[i].getTitle(), i);
//				WordKey[] words = docs[i].getContent();
//				for (WordKey word: words) {
//					Integer termId = termsDict.get(word);
//						if (termId == null) {
//							termId = nTerms;
//							termsDict.put(word, termId);
//							nTerms++;
//						}
//						termDocPairs.add(new TermIdDocId(termId, i));		
//				}
//			}
//			docIdCount = docsDict.size();
//			return termDocPairs;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	private void buildPostingLists(List<TermIdDocId> termDocPairs) {
//		docLengths = new ArrayList<Double>(docsDict.size());
//		tf = new ArrayList<List<Posting>>(termsDict.size());
////		postings = new ArrayList<List<HomPosting>>(termsDict.size());
//		postings = new ArrayList<List<Posting>>(termsDict.size());
//
//		//sort (TermId,DocId) pairs and build postings list
//		PriorityQueue<TermIdDocId> minHeap = new PriorityQueue<TermIdDocId>(termDocPairs);
//		List<Posting> postingList = new LinkedList<Posting>();
//		double n = 1.0;
//		TermIdDocId oldPair = minHeap.poll();
//		TermIdDocId newPair = null;
//		while((newPair = minHeap.poll()) != null) {
//			if (newPair.getTermId() == oldPair.getTermId() && newPair.getDocId() == oldPair.getDocId())
//				n++;
//			else {
//				try {
//					docLengths.set(oldPair.getDocId(), docLengths.get(oldPair.getDocId()) + n);
//				} catch(IndexOutOfBoundsException e) {
//					docLengths.add(n);
//				}
//				postingList.add(new Posting(oldPair.getDocId(), n));
//				n = 1;
//				if (newPair.getTermId() != oldPair.getTermId()) {
//					tf.add(postingList);
//					postingList = new LinkedList<Posting>();
//				}
//			}
//			oldPair = newPair;
//		}
//		//Last pair processing
//		try {
//			docLengths.set(oldPair.getDocId(), docLengths.get(oldPair.getDocId()) + n);
//		} catch(IndexOutOfBoundsException e) {
//			docLengths.add(n);
//		}
//		postingList.add(new Posting(oldPair.getDocId(), n));
//		tf.add(postingList);
//		minHeap = null;
//		termDocPairs = null;
//	}
//		
//	private void finalScores() {		
//		double avgDocLength = 0.0, nDocs = 0.0;
//		for (Double x: docLengths)
//			if (x != null) {
//				avgDocLength += x;
//				nDocs++;
//			}
//		avgDocLength /= nDocs;
//		//calculate and store final scores
//		for (int i = 0; i < tf.size(); i++) {
//			List<Posting> tfList = tf.get(i);
//			PriorityQueue<Posting> orderedPostingList = new PriorityQueue<Posting>(tfList.size());
//			for (int j = 0; j < tfList.size(); j++) {
//				Posting tfPosting = tfList.get(j);
//				orderedPostingList.add(new Posting(tfPosting.getDocId(), bm25(tfPosting.getScore(), (double)tfList.size(), nDocs, docLengths.get(tfPosting.getDocId()), avgDocLength)));
//			}
//			//Cipher ordered postings with paillier
//			List<Posting> homPostings = new ArrayList<Posting>(k);
////			List<HomPosting> homPostings = new ArrayList<HomPosting>(k);
//			for (int j = 0; j < k && j < tfList.size(); j++) {
//				Posting posting = orderedPostingList.poll();
////				BigInteger score = crypto.encryptHom(new BigInteger(""+(int)(posting.getScore()*Math.pow(10, 9))));
////				homPostings.add(new HomPosting(posting.getDocId(), score));
//				homPostings.add(posting);
//			}
//			postings.add(homPostings);
//		}
////		writeIndexToDisk();
//	}
//
//	private double bm25(double n, double df, double nDocs, double docLength, double avgDocLength) {
//		double k1 = 1.2;
//		double b = 0.75;
//		double tf = n / docLength;
//		double idf = Math.log10(nDocs / df);
//		return idf * (((k1+1)*tf) / (k1*((1-b)+b*(docLength/avgDocLength))+tf));
//	}
//
////	private void writeIndexToDisk() {
////	Utils.writeObjectToDisk(postings, indexPath+"postings");
////	Utils.writeObjectToDisk(docsDict, indexPath+"docsDict");
////	Utils.writeObjectToDisk(termsDict, indexPath+"termsDict");
////	crypto.save();
////}
//
//	@Override
//	public Integer addDocumentToIndex(CDocument doc) {
//		Integer docId = docsDict.get(doc.getTitle());
//		if (docId != null) {
//			if (removedDocs.containsKey(docId))
//				removedDocs.remove(docId);
//			return null;
//		} else {
//			docId = docIdCount;
//			docIdCount++;
//			docsDict.put(doc.getTitle(), docId);
//		}
//		int nTerms = termsDict.size();
//		List<Integer> terms = new LinkedList<Integer>();
//		WordKey[] words = doc.getContent();
//		for (WordKey word: words) {
//			Integer termId = termsDict.get(word);
//				if (termId == null) {
//					termId = nTerms;
//					termsDict.put(word, termId);
//					nTerms++;
//				}
//				terms.add(termId);		
//		}
//		
//		List<Posting> newTF = new LinkedList<Posting>();
//		PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(terms);
//		Integer oldTerm = minHeap.poll();
//		Integer newTerm = null;
//		double n = 1.0;
//		while ((newTerm = minHeap.poll()) != null) {
//			if (newTerm == oldTerm)
//				n++;
//			else {
//				//This is a TermId stored as a DocId
//				newTF.add(new Posting(oldTerm, n));
//				n = 1;
//			}
//			oldTerm = newTerm;
//		}
//		newTF.add(new Posting(oldTerm, n));
//		
//		double docLength = terms.size();
//		docLengths.add(docLength);
//		double avgDocLength = 0.0, nDocs = 0.0;
//		for (Double x: docLengths)
//			if (x != null) {
//				avgDocLength += x;
//				nDocs++;
//			}
//		avgDocLength /= nDocs;
//		
//		for (Posting p: newTF) {
//			//This is a TermId stored as a DocId
//			if (p.getDocId() < postings.size()) {
//				List<Posting> tfList = tf.get(p.getDocId());
//				tfList.add(new Posting(docId, p.getScore()));
//				
////				List<HomPosting> postingList = postings.get(p.getDocId());
//				List<Posting> postingList = postings.get(p.getDocId());
//				double score = bm25(p.getScore(), (double)tfList.size(), docsDict.size(), docLength, avgDocLength);
////				BigInteger eScore = crypto.encryptHom(new BigInteger(""+(int)(score*Math.pow(10, 9))));
////				postingList.add(new HomPosting(docId, eScore));
//				postingList.add(new Posting(docId, score));
//			} else {
//				List<Posting> tfList = new LinkedList<Posting>();
//				tfList.add(new Posting(docId, p.getScore()));
//				tf.add(tfList);
//				
////				List<HomPosting> postingList = new ArrayList<HomPosting>(k);
//				List<Posting> postingList = new ArrayList<Posting>(k);
//				double score = bm25(p.getScore(), (double)tfList.size(), docsDict.size(), docLength, avgDocLength);
////				BigInteger eScore = crypto.encryptHom(new BigInteger(""+(int)(score*Math.pow(10, 9))));
////				postingList.add(new HomPosting(docId, eScore));
//				postingList.add(new Posting(docId, score));
//				postings.add(postingList);
//			}
//		}
//		indexThreshold++;
//		if (indexThreshold == 50)
//			rebuildIndex();
//		return docId;
//	}
//	
//	@Override
//	public void rebuildIndex() {
//		for (Map.Entry<Integer, byte[]> doc: removedDocs.entrySet()) {
//			docsDict.remove(doc.getValue());
////			cloud.removeDoc(""+doc.getKey());
//			docLengths.set(doc.getKey(), null);
//		}
//		indexThreshold = 0;
////		postings = new ArrayList<List<HomPosting>>(termsDict.size());
//		postings = new ArrayList<List<Posting>>(termsDict.size());
//		double avgDocLength = 0.0, nDocs = 0.0;
//		for (Double x: docLengths)
//			if (x != null) {
//				avgDocLength += x;
//				nDocs++;
//			}
//		avgDocLength /= nDocs;
//		
//		for (int i = 0; i < tf.size(); i++) {
//			List<Posting> tfList = tf.get(i);
//			//remove old docs from posting lists
//			for (int j = 0; j < tfList.size(); j++)
//				if (removedDocs.containsKey(tfList.get(j).getDocId())) {
//					tfList.remove(j);
//					j--;
//				}
//			if (tfList.size() == 0)
//				continue;
//			//build scores
//			PriorityQueue<Posting> orderedPostingList = new PriorityQueue<Posting>(tfList.size());
//			for (int j = 0; j < tfList.size(); j++) {
//				Posting tfPosting = tfList.get(j);
//				double score = bm25(tfPosting.getScore(), (double)tfList.size(), nDocs, docLengths.get(tfPosting.getDocId()), avgDocLength);
//				orderedPostingList.add(new Posting(tfPosting.getDocId(), score));
//			}
//			//Cipher ordered postings with paillier
////			List<HomPosting> homPostings = new ArrayList<HomPosting>(k);
//			List<Posting> homPostings = new ArrayList<Posting>(k);
//			for (int j = 0; j < k && j < tfList.size(); j++) {
//				Posting posting = orderedPostingList.poll();
////				BigInteger score = crypto.encryptHom(new BigInteger(""+(int)(posting.getScore()*Math.pow(10, 9))));
////				homPostings.add(new HomPosting(posting.getDocId(), score));
//				homPostings.add(posting);
//			}
//			postings.add(homPostings);
//		}
//		
//		removedDocs = new HashMap<Integer, byte[]>();
//	}
//
//	@Override
//	public boolean removeDocument(byte[] title) {
//		Integer docId = docsDict.get(title);
//		if (docId == null)
//			return false;
//		removedDocs.put(docId, title);
//		return true;
//	}
//	
//	@Override
//	public boolean removeDocumentById(int docId) {
//		byte[] title = null;
//		for (Map.Entry<byte[], Integer> entry: docsDict.entrySet())
//			if (entry.getValue().equals(docId)) {
//				title = entry.getKey();
//				break;
//			}
//		if (title == null)
//			return false;
//		
//		removedDocs.put(docId, title);
//		return true;
//	}
//	
//}