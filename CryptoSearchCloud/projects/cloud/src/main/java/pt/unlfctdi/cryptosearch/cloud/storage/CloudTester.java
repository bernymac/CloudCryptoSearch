package pt.unlfctdi.cryptosearch.cloud.storage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class CloudTester {

	public static void main (String[] args) throws Exception {
		String credentials = new String("secretKey=MD/FGPCuKkq9EBCpV0qXbfTbaQByFdQYPiM9/BzA" +
				"\n" + "accessKey=AKIAICG6X3TDB3GIBQIQ");
		AmazonS3Client s3 = new AmazonS3Client(new PropertiesCredentials(new ByteArrayInputStream(credentials.getBytes())));
		File f = new File("C:\rfcs\rfc4912.txt");
		String s = readFileAsString(f);
		
		for (int i = 0; i < 1000; i++) {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(s.getBytes().length);
			s3.putObject("cryptosearch", "test/"+i, new ByteArrayInputStream(s.getBytes()), metadata);
		}
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

}