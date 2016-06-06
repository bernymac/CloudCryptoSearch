package pt.unlfctdi.cryptosearch.cloud.storage;
//package pt.unlfctdi.cryptosearch.core.cloud;
//
//import java.io.BufferedInputStream;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//
//import javax.annotation.PostConstruct;
//import javax.ejb.Local;
//
//import org.jboss.ejb3.annotation.Service;
//
//import pt.unlfctdi.cryptosearch.core.data.PDocument;
//import pt.unlfctdi.cryptosearch.core.data.PInvListEntry;
//import pt.unlfctdi.cryptosearch.core.data.Posting;
//
//import com.amazonaws.auth.PropertiesCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.S3Object;
//
//@Service
//@Local(CloudConnectorLocal.class)
//public class S3CloudConnectorBean implements CloudConnectorLocal {
//
//	private AmazonS3 s3;
//
//	private String bucket;
//
//	@PostConstruct
//	public void start() {
//		try {
//			String credentials = new String("secretKey=MD/FGPCuKkq9EBCpV0qXbfTbaQByFdQYPiM9/BzA" +
//					"\n" + "accessKey=AKIAICG6X3TDB3GIBQIQ");
//			s3 = new AmazonS3Client(new PropertiesCredentials(new ByteArrayInputStream(credentials.getBytes())));
//			bucket = "cryptosearch";
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	//	@Override
//	//	public void remove(String folder, String key) {
//	//	}
//
//	@Override
//	public PInvListEntry[][] getPlainIndex(String[] keys) {
//		PInvListEntry[][] results = new PInvListEntry[keys.length][];
//		for (int i = 0; i < keys.length; i++)
//			results[i] = (PInvListEntry[]) getFromCloud("IndexPlain",keys[i]);
//		return results;
//	}
//
//	@Override
//	public PDocument getPlainDoc(String key) {
//		return (PDocument) getFromCloud("DocsPlain",key);
//	}
//
//	@Override
//	public byte[][] getAESIndex(String[] keys) {
//		byte[][] results = new byte[keys.length][];
//		for (int i = 0; i < keys.length; i++)
//			results[i] = (byte[]) getFromCloud("IndexAES",keys[i]);
//		return results;
//	}
//
//	@Override
//	public byte[] getAESDoc(String key) {
//		return (byte[]) getFromCloud("DocsAES",key);
//	}
//
//	@Override
//	public void putPlainIndex(String key, Posting[] data) {
//		writeToCloud("IndexPlain", key, CloudUtils.serializeObject(data));
//	}
//
//	@Override
//	public void putPlainDoc(String key, PDocument data) {
//		writeToCloud("DocsPlain", key, CloudUtils.serializeObject(data));
//	}
//
//	@Override
//	public void putAESIndex(String key, byte[] data) {
//		writeToCloud("IndexAES", key, data);
//	}
//
//	@Override
//	public void putAESDoc(String key, byte[] data) {
//		writeToCloud("DocsAES", key, data);
//	}
//
//	private Object getFromCloud(String folder, String key) {
//		S3Object s3Object = s3.getObject(bucket, folder+"/"+key);
//		return CloudUtils.readObjectFromStream(s3Object.getObjectContent());
//	}
//
//	private void writeToCloud(String folder, String key, byte[] data) {
//		ObjectMetadata metadata = new ObjectMetadata();
//		metadata.setContentLength(data.length);
//		ByteArrayInputStream bais = new ByteArrayInputStream(data);
//		BufferedInputStream bis = new BufferedInputStream(bais);
//		s3.putObject(bucket, folder+"/"+key, bis, metadata);
//	}
//
//
//	public static void main (String[] args) throws Exception{
//		String credentials = new String("secretKey=MD/FGPCuKkq9EBCpV0qXbfTbaQByFdQYPiM9/BzA" +
//				"\n" + "accessKey=AKIAICG6X3TDB3GIBQIQ");
//		AmazonS3Client s3 = new AmazonS3Client(new PropertiesCredentials(new ByteArrayInputStream(credentials.getBytes())));
//		String s = "ola!";
//		ObjectMetadata metadata = new ObjectMetadata();
//		metadata.setContentLength(s.getBytes().length);
//		s3.putObject("cryptosearch", "plain/"+s, new ByteArrayInputStream(s.getBytes()), metadata);
//
//	}
//}