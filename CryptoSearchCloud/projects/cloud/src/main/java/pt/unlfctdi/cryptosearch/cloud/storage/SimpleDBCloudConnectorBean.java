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

package pt.unlfctdi.cryptosearch.cloud.storage;
//package pt.unlfctdi.cryptosearch.core.cloud;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.annotation.PostConstruct;
//import javax.ejb.Local;
//
//import org.jboss.ejb3.annotation.Service;
//
//import pt.unlfctdi.cryptosearch.core.data.PDocument;
//import pt.unlfctdi.cryptosearch.core.data.PInvListEntry;
//import pt.unlfctdi.cryptosearch.core.data.Posting;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
//
//import com.amazonaws.auth.PropertiesCredentials;
//import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
//import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
//import com.amazonaws.services.simpledb.model.Item;
//import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
//import com.amazonaws.services.simpledb.model.ReplaceableItem;
//import com.amazonaws.services.simpledb.model.SelectRequest;
//import com.amazonaws.services.simpledb.model.SelectResult;
//
//@Service
//@Local(CloudConnectorLocal.class)
//public class SimpleDBCloudConnectorBean implements CloudConnectorLocal {
//
//	private AmazonSimpleDBClient aws;
//
//	private List<ReplaceableItem> cache;
//
//	@PostConstruct
//	public void start() {
//		try {
//			String credentials = new String("secretKey=MD/FGPCuKkq9EBCpV0qXbfTbaQByFdQYPiM9/BzA" +
//					"\n" + "accessKey=AKIAICG6X3TDB3GIBQIQ");
//			aws = new AmazonSimpleDBClient(new PropertiesCredentials(new ByteArrayInputStream(credentials.getBytes())));
//			cache = new ArrayList<ReplaceableItem>(25);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	//	@Override
//	//	public void remove(String folder, String key) {
//	//	}
//
//
//	@Override
//	public PInvListEntry[][] getPlainIndex(String[] keys) {
//		byte[][] obj = getFromCloud("DocsAES","doc",keys);
//		PInvListEntry[][] results = new PInvListEntry[obj.length][];
//		for (int i = 0; i < obj.length; i++)
//			results[i] = (PInvListEntry[]) CloudUtils.deserializeObject(obj[i]);
//		return results;
//	}
//
//	@Override
//	public PDocument getPlainDoc(String key) {
//		byte[][] obj = getFromCloud("DocsAES","doc",new String[]{key});
//		return (PDocument) CloudUtils.deserializeObject(obj[0]);
//	}
//
//	@Override
//	public byte[][] getAESIndex(String[] keys) {
//		return getFromCloud("IndexAES","postingsList",keys);
//	}
//
//	@Override
//	public byte[] getAESDoc(String key) {
//		return getFromCloud("DocsAES","doc",new String[]{key})[0];
//	}
//
//	@Override
//	public void putPlainIndex(String key, Posting[] data) {
//		writeToCloud ("IndexPlain", key, "postingsList", CloudUtils.serializeObject(data));
//	}
//
//	@Override
//	public void putPlainDoc(String key, PDocument data) {
//		writeToCloud ("DocsPlain", key, "doc", CloudUtils.serializeObject(data));
//	}
//
//	@Override
//	public void putAESIndex(String key, byte[] data) {
//		writeToCloud ("IndexAES", key, "postingsList", data);
//	}
//
//	@Override
//	public void putAESDoc(String key, byte[] data) {
//		writeToCloud ("DocsAES", key, "doc", data);
//	}
//
//	private void writeToCloud (String domain, String item, String attr, byte[] value) {
//		BASE64Encoder encoder = new BASE64Encoder();
//		List<ReplaceableAttribute> attrbs = new ArrayList<ReplaceableAttribute>(1);
//		attrbs.add(new ReplaceableAttribute(attr,encoder.encode(value), false));
//		cache.add(new ReplaceableItem(item, attrbs));
//		if (cache.size() == 25) {
//			aws.batchPutAttributes(new BatchPutAttributesRequest(domain, cache));
//			cache = new ArrayList<ReplaceableItem>(25);
//		}
//	}
//
//	private byte[][] getFromCloud (String domain, String attr, String[] keys) {
//		try {
//			String query = "select "+attr+" from "+domain+" where ";
//			for (int i = 0; i < keys.length-1; i++)
//				query +=  "itemName() = '"+ keys[i] +"' or ";
//			query +=  "itemName() = '"+ keys[keys.length-1] +"'";
//			SelectResult result = aws.select(new SelectRequest(query, true));
//			byte[][] results = new byte[result.getItems().size()][];
//			BASE64Decoder decoder = new BASE64Decoder();
//			for (int i = 0; i <	result.getItems().size(); i++) {
//				String value = result.getItems().get(i).getAttributes().get(0).getValue();
//				results[i] = decoder.decodeBuffer(value);
//			}
//			return results;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	public static void main (String[] args) throws Exception{
//		String credentials = new String("secretKey=MD/FGPCuKkq9EBCpV0qXbfTbaQByFdQYPiM9/BzA" +
//				"\n" + "accessKey=AKIAICG6X3TDB3GIBQIQ");
//
//		AmazonSimpleDBClient aws = new AmazonSimpleDBClient(new PropertiesCredentials(new ByteArrayInputStream(credentials.getBytes())));
//		//		List<ReplaceableAttribute> attrbs = new ArrayList<ReplaceableAttribute>();
//		//		attrbs.add(new ReplaceableAttribute("docId","rfc456.txt", false));
//		//		attrbs.add(new ReplaceableAttribute("score","0.855", false));
//		//		attrbs.add(new ReplaceableAttribute("docId","rfc123.txt", false));
//		//		attrbs.add(new ReplaceableAttribute("score","0.53", false));
//		//		aws.putAttributes(new PutAttributesRequest("aes", "cenas", attrbs));
//		//		GetAttributesResult result = aws.getAttributes(new GetAttributesRequest("aes", "payload"));
//		//		for (Attribute attr: result.getAttributes())
//		//			System.out.println(attr.getName() + " " + attr.getValue());
//		String query = "select docId from aes where itemName() = 'rtp'";
//		SelectResult result = aws.select(new SelectRequest(query, true));
//		for (Item i:result.getItems())
//			System.out.println(i.getAttributes().get(0).getValue());
//	}
//
//}