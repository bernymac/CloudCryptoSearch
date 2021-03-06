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

package pt.unlfctdi.cryptosearch.cloud.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import pt.unlfctdi.cryptosearch.cloud.crypto.CloudCryptoBean;
import pt.unlfctdi.cryptosearch.cloud.data.document.CDocument;
import pt.unlfctdi.cryptosearch.cloud.data.posting.HomChampionList;
import pt.unlfctdi.cryptosearch.cloud.data.posting.HomPosting;
import pt.unlfctdi.cryptosearch.cloud.data.posting.PostingList;
import pt.unlfctdi.cryptosearch.cloud.data.posting.RunsPostingList;
import pt.unlfctdi.cryptosearch.cloud.data.posting.TermFreq;
import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public class SPIMI_WIKI_HOM {

	private CloudCryptoBean crypto;
	
//	private Map<WordKey,Integer> docsDict;
	private Map<WordKey,PostingList> index;
//	private String currentIndexName;
//	private List<Integer> docLengths;
//	private int sumDocLengths;
	private int docCount;

	private static Logger log = Logger.getLogger(SPIMI_WIKI_HOM.class);
	private static double minMemory = 1;	//in GB
	private static String mainPath = "/home/bernardo/Dropbox/WorkspacePHD/CryptoSearchCloud/";
	
	private boolean hasToMerge;

	public SPIMI_WIKI_HOM() {
		log.addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %-5p [%c{1}] %m%n")));
		log.setLevel(Level.INFO);
		
		crypto = new CloudCryptoBean();
		index = new HashMap<WordKey,PostingList>();
		docCount = 0;
//		docsDict = new HashMap<WordKey,Integer>();
//		sumDocLengths = 0;
//		docLengths = new ArrayList<Integer>();
		 hasToMerge = true;
	}
	
	public List<HomPosting> processQuery(List<WordKey> query) {
		List<HomPosting> homScores = new ArrayList<HomPosting>();
		for (WordKey term: query) {
			PostingList tfs = index.get(term);
			if (tfs == null) {
				if (!hasToMerge)
					continue;
				else {
//						for (File f: new File(mainPath+"index").listFiles()) {
//							index = null;
//							for (int i = 0; i < 12; i++)
//								System.gc();
//							index = new HashMap<WordKey,PostingList>();
//							readIndexfromDisk(f);
//							currentIndexName = f.getName();
//							tfs = index.get(term);
//							if (tfs != null)
//								break;
//						}
//						if (tfs == null)
//							continue;
				}
			} else
				for (HomPosting posting: ((HomChampionList)tfs).getHomPostings()) {
//						if (!removedDocs.containsKey(posting.getDocId())) {
					int j = homScores.indexOf(posting);
					if (j == -1)
						homScores.add(posting);
					else 
						homScores.get(j).setScore(crypto.homAdd(homScores.get(j).getScore(), posting.getScore()));
				}
		}
		return homScores;
	}
	
//	private void readIndexfromDisk(File f) {
//		try {
//			FileChannel fc = new RandomAccessFile(f, "r").getChannel();
//			long pos = 0;
//			long size = fc.size();
//			MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_ONLY, pos, size);
//			out.load();
//			while (out.position() +4 < size) {
//				int length = out.getInt();
//				byte[] cipheredPostings = new byte[length];
//				out.get(cipheredPostings, 0, length);
//				PostingList postings = crypto.decryptPostingList(cipheredPostings);
//				index.put(postings.getTerm(), postings);
//			}
//			fc.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	private void readIndexfromDisk(File f) {
//		try {
//			ObjectInputStream ois = crypto.getCipheredObjectInputStream(f);
//			while (ois.available() > 0) {
//				PostingList postings = (PostingList)ois.readObject();
//				index.put(postings.getTerm(), postings);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 
	 */
	public void buildIndex() {
		if (!hasToMerge)
			return;
		if (index.size() > 0) {
			writeIndexToDisk(mainPath+"runs/-1");
			index = null;
			for (int i = 0; i < 12; i++)
				System.gc();
			index = new HashMap<WordKey,PostingList>();
		}
		log.info("Processed runs. Merging...");
		
		try {
			File[] runs = new File(mainPath+"runs").listFiles();
			for (int i = 0; i < runs.length; i++) {
				DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(runs[i])));
				PostingList postings = readChampionList(dis);
				while (postings != null) {
					PostingList indexedPostings = index.get(postings.getTerm());
					if (indexedPostings == null)
						index.put(postings.getTerm(), postings);
					else
						for (TermFreq tf: postings.getPostings()) 
							indexedPostings.addPosting(tf);
//					checkRAM(mainPath+"index/"+i);
//						currentIndexName = ""+i;
					postings = readChampionList(dis);
				}
				dis.close();
			}
//			if (currentIndexName != null) {
//			currentIndexName = "z";
//			writeIndexToDisk(mainPath+"index/"+currentIndexName);	
//			}
			log.info("Finished Merging. Ciphering Posting Lists...");
			for (Map.Entry<WordKey, PostingList> entry: index.entrySet()) {
				HomChampionList homPostings = new HomChampionList();
				for (TermFreq tf: entry.getValue().getPostings())
					homPostings.addPosting(new HomPosting(tf.getDocId(), crypto.encryptHom(BigInteger.valueOf(tf.getFreq()))));
				entry.setValue(homPostings);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private PostingList readChampionList (DataInputStream dis) {
//		try {
//			byte[] term = new byte[16];
//			dis.read(term, 0, 16);
//			int df = dis.readInt();
//			PostingList postings = new ChampionList(new WordKey(term), df);
//			for (int i = 0; i < df; i++) {
//				int docId = dis.readInt();
//				int score = dis.readInt();
//				postings.addPosting(docId, score);
//			}
//			return postings;
//		} catch (EOFException e) {
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
//	private PostingList readChampionList (DataInputStream dis) {
//		try {
//			int length = dis.readInt();
//			byte[] cipheredPostings = new byte[length];
//			dis.read(cipheredPostings,0,length);
//			return new ChampionList(crypto.decryptPostingList(cipheredPostings));
//		} catch (EOFException e) {
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	private PostingList readChampionList (DataInputStream dis) {
		return crypto.decryptPostingListFromDisk(dis);
	}
	
	/**
	 * @param CDocument 
	 * @return
	 */
	public void addFirstDocuments(CDocument doc) {
		//docsDict.put(doc.getTitle(), docCount);
		for (WordKey word: doc.getContent()) {
			PostingList postings = index.get(word);
			if (postings == null) {
				postings = new RunsPostingList(word);
				index.put(word, postings);
			}
			postings.addPosting(docCount, 1);
		}
		boolean wroteToDisk = checkRAM(mainPath+"runs/"+docCount);
		if (!hasToMerge)
			hasToMerge = wroteToDisk;
		docCount++;
		//docLengths.add(doc.getContent().length);
		//sumDocLengths += doc.getContent().length;
	}

	private boolean checkRAM(String fileName) {
		if ((double)Runtime.getRuntime().freeMemory()/1073741824 <= minMemory) {
//			log.info("Writing index to disk...");
			writeIndexToDisk(fileName);
//			log.info("...wrote Index.");
			index = null;
			for (int i = 0; i < 12; i++)
				System.gc();
			index = new HashMap<WordKey,PostingList>();
			return true;
		}
		return false;
	}
	
	private void writeIndexToDisk(String fileName) {
		try {
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
			for (PostingList p: index.values()) {
				byte[] cipher = crypto.encryptPostingList(p);
				dos.writeInt(cipher.length);
				dos.write(cipher);
				dos.flush();
			}
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private void writeIndexToDisk(String fileName) {
//		try {
//			File f = new File(fileName);
//			f.createNewFile();
//			FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
//			long pos = 0;
//			MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_WRITE, pos, 0x8FFFFFF);
//			for (PostingList postings: index.values()) {
//				byte[] cipheredPostings = crypto.encryptPostingList(postings);
//				if (out.position() + 4 + cipheredPostings.length >= out.limit()) {
//					pos += out.position();
//					out.force();
//					out = fc.map(FileChannel.MapMode.READ_WRITE, pos, 0x8FFFFFF);
//				}
//				out.putInt(cipheredPostings.length).put(cipheredPostings);
//			}
//			out.force();
//			fc.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	private void writeIndexToDisk(String fileName) {
//		try {
//			DataOutputStream dos = crypto.getCipheredOutputStream(new File(fileName));
//			for (PostingList p: index.values()) {
//				dos.write(p.getTerm().getContents());
//				dos.writeInt(p.getDf());
//				for (TermFreq tf: p.getPostings()) {
//					dos.writeInt(tf.getDocId());
//					dos.writeInt(tf.getFreq());
//				}
//				dos.flush();
//			}
//			dos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	 
	public int getDocumentId(String title) {
		return -1;//docsDict.get(title);
	}

	 
	public Integer addDocumentToIndex(CDocument docPath) {
		//check if document exists
//		File f = new File(docPath);
//		if (!f.exists())
//			return false;
//		PDocument doc = new PDocument(f.getName(), Utils.readFileAsString(f));
//		Integer docId = docsDict.get(doc.getTitle());
//		if (docId != null) 
//			return false;
//		else {
//			docId = docCount;
//			docCount++;
//			docsDict.put(doc.getTitle(), docId);
////			cloud.putDoc(""+docId, crypto.encryptDocAES(doc));
//		}
//		
//		Map<String,PostingList> indexRef;
//		if (currentIndexName != null)
//			indexRef = new HashMap<String,PostingList>();
//		else
//			indexRef = index;
//		TokenStream tokenizer = analyzer.tokenStream(null, new BufferedReader(new StringReader(doc.getContent())));
//		int nTerms = 0;
//		try {
//			while (tokenizer.incrementToken()) {
//				String word = tokenizer.getAttribute(CharTermAttribute.class).toString();
//				if (word.length() > 0) {
//					Stemmer s = new Stemmer();
//					s.add(word.toCharArray(), word.length());
//					s.stem();
//					String term = s.toString();
//					//get posting list
//					PostingList postings = indexRef.get(term);
//					if (postings == null) {
//						postings = new RunsPostingList(term);
//						indexRef.put(term, postings);
//					}
//					//add posting
//					postings.addPosting(docId, 1);
//					nTerms++;
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		docLengths.add(nTerms);
//		sumDocLengths += nTerms;
//		if (currentIndexName != null) {
//			;//TODO merge das novas entradas com os indices em disco
//		}
		return 0;
	}

	 
	public void rebuildIndex() {
		// TODO Shouldn't be necessary unless auxiliary index is kept in memory
		//may be necessary anyways if too many inserts increase size of index partitions beyond available memory
		//in that case, copy files to runs folder and do normal runs merge 
		//(maybe different method can be faster because there's less requirements than in mergeRuns)
		//probably something like: open index partition, read to memory until full, write to new partition file; continue
	}

	 
	public boolean removeDocument(WordKey title) {
//		Integer docId = docsDict.remove(title);
//		if (docId == null)
//			return false;
//		sumDocLengths -= docLengths.get(docId);
//		docLengths.set(docId, null);
//		for (PostingList postings: index.values())
//			postings.removePosting(docId);
//		if (currentIndexName != null) {
//			//TODO remove postings from index in disk
//		}
		return true;
	}

	 
	public boolean removeDocumentById(int docId) {
//		WordKey title = null;
//		for (Map.Entry<WordKey, Integer> entry: docsDict.entrySet())
//			if (entry.getValue().equals(docId)) {
//				title = entry.getKey();
//				break;
//			}
//		if (title == null)
//			return false;
//		return removeDocument(title);
		return false;
	}
	
}
