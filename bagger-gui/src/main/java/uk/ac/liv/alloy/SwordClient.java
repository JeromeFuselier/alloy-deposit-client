package uk.ac.liv.alloy;

import gov.loc.repository.bagger.BaggerApplication;
import gov.loc.repository.bagger.ui.ConsoleView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SwordClient {

	public static ArrayList<String> getServiceDocument() {
		String request = BaggerApplication.server_url + "/api/deposit/sd";
		String username = "sword";
		String password = "sword";
		String result = "<?xml version=\"1.0\" ?>";
		
		try {
			URL url = new URL(request);
			HttpURLConnection conn;
		    BufferedReader rd;
		    String line;
		      
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty ("Authorization", userNamePasswordBase64(username, password));
			conn.setRequestProperty("On-Behalf-Of", "obo");
			conn.setRequestMethod("GET"); 
			
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
			conn.disconnect(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return XmlParse.parseSD(result);
	}
	
	


	public static String userNamePasswordBase64(String username, String password) {
		return "Basic " + base64Encode (username + ":" + password);
	 }

	private final static char base64Array [] = {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
      'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
      'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', '0', '1', '2', '3',
      '4', '5', '6', '7', '8', '9', '+', '/'
	};

	private static String base64Encode (String string) {
		String encodedString = "";
		byte bytes [] = string.getBytes ();
		int i = 0;
		int pad = 0;
		while (i < bytes.length) {
			byte b1 = bytes [i++];
			byte b2;
			byte b3;
			if (i >= bytes.length) {
				b2 = 0;
				b3 = 0;
				pad = 2;
			}
			else {
				b2 = bytes [i++];
				if (i >= bytes.length) {
					b3 = 0;
					pad = 1;
				}
				else
					b3 = bytes [i++];
			}
			byte c1 = (byte)(b1 >> 2);
			byte c2 = (byte)(((b1 & 0x3) << 4) | (b2 >> 4));
			byte c3 = (byte)(((b2 & 0xf) << 2) | (b3 >> 6));
			byte c4 = (byte)(b3 & 0x3f);
			encodedString += base64Array [c1];
			encodedString += base64Array [c2];
			switch (pad) {
			case 0:
				encodedString += base64Array [c3];
				encodedString += base64Array [c4];
				break;
			case 1:
				encodedString += base64Array [c3];
				encodedString += "=";
				break;
			case 2:
				encodedString += "==";
				break;
			}
		}
		return encodedString;
	}




	public static String sendBag(String coll_id, File file) {
		String request = BaggerApplication.server_url + "/api/deposit/col/" + coll_id;
		String username = "sword";
		String password = "sword";
		URL url;
		String response = "";
		
		try {
			url = new URL(request);
			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty ("Authorization", userNamePasswordBase64(username, password));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false); 
			conn.setRequestMethod("POST"); 
			conn.setRequestProperty("Content-Type", "application/zip"); 
			conn.setRequestProperty("Content-Disposition", "test.zip");
			conn.setRequestProperty("On-Behalf-Of", "obo");
			conn.setRequestProperty("Packaging", "http://purl.org/net/sword/package/BagIt");
			
			conn.setUseCaches (false);		
		
			byte[] result = new byte[(int)file.length()];		
		
			InputStream input = null;
			int totalBytesRead = 0;	
		
			input = new BufferedInputStream(new FileInputStream(file));
			
			while(totalBytesRead < result.length){
				int bytesRemaining = result.length - totalBytesRead;
				int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
				if (bytesRead > 0){
					totalBytesRead = totalBytesRead + bytesRead;
				}
			}


			DataOutputStream wr = new DataOutputStream(conn.getOutputStream ());
			wr.write(result, 0, result.length);
			wr.flush();
			wr.close();
			
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					response += line;
				}
				wr.close();
				rd.close();
			} catch (FileNotFoundException e) {
			}
				
				
			conn.disconnect();
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		return response;
	}
	
	
	

}
