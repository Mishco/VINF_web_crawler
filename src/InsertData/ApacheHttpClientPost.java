package InsertData;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;

import application.FXMLController;

public class ApacheHttpClientPost {
	private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
	public static Client transportClient;
	
	public static String removeTags(String string) {
	    if (string == null || string.length() == 0) {
	        return string;
	    }

	    Matcher m = REMOVE_TAGS.matcher(string);
	    return m.replaceAll("");
	}
	
	public static void httpPOSTandGET() {
        //String path="D:\\Tools\\javadoc.txt", 
		String filecontent="";
        String path="D:\\Tools\\data_161.json";
        
        ApacheHttpClientPost apacheHttpClientPost = new ApacheHttpClientPost();
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost("http://localhost:9200/genius/songs/1");
            filecontent=apacheHttpClientPost.readFileContent(path);
            // removing html tags
            filecontent = removeTags(filecontent);
            
            System.out.println(filecontent);
            StringEntity input = new StringEntity(filecontent);
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            /*if (response.getStatusLine().getStatusCode() != 201) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }*/
            if(response.getStatusLine().getStatusCode() != 200){
                // Throw exception or something else
            	 throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            } 
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {

                System.out.println(output);
            }
            System.out.println("----------");    
            String url = "http://localhost:9200/genius/songs/1";
            		

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);

            // add request header
           //request.addHeader("User-Agent", USER_AGENT);
            HttpResponse response2 = client.execute(request);

            System.out.println("Response Code : "
                            + response2.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
            	new InputStreamReader(response2.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
            	result.append(line);
            }
            System.out.println(result.toString());
            
            
            httpClient.getConnectionManager().shutdown();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
          
        
    }
	
	
	public static void httpGet(FXMLController controller) {
		
		 ApacheHttpClientPost apacheHttpClientPost = new ApacheHttpClientPost();
	        try {
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            
	            HttpGet getRequest = new HttpGet("http://localhost:9200/genius/_search/?size=1000&pretty=1");
	            HttpResponse response = httpClient.execute(getRequest);
	            
	            if(response.getStatusLine().getStatusCode() != 200){
	                // Throw exception or something else
	            	 throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
	            } 
	            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
	            String output;
	            System.out.println("Output from Server .... \n");
	            StringBuilder outputGUI = new StringBuilder();
	            while ((output = br.readLine()) != null) {

	                System.out.println(output);
	                outputGUI.append(output);
	            }
	            //controller.result.appendText(outputGUI.toString());
	            System.out.println("----------");    
	            String url = "http://localhost:9200/genius/songs/1";
	            		

	            HttpClient client = HttpClientBuilder.create().build();
	            HttpGet request = new HttpGet(url);

	            // add request header
	           //request.addHeader("User-Agent", USER_AGENT);
	            HttpResponse response2 = client.execute(request);

	            System.out.println("Response Code : "
	                            + response2.getStatusLine().getStatusCode());

	            BufferedReader rd = new BufferedReader(
	            	new InputStreamReader(response2.getEntity().getContent()));

	            StringBuffer result = new StringBuffer();
	            String line = "";
	            while ((line = rd.readLine()) != null) {
	            	result.append(line);
	            	
	            }
	            System.out.println(result.toString());
	            
	            httpClient.getConnectionManager().shutdown();
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
	

    private String readFileContent(String pathname) throws IOException {


        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    public static void ReadFromSense() {
    	//Create Client
    	Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch").build();
    	transportClient = TransportClient.builder().settings(settings).build();   
    	
    	
    	//Get document
    	GetRequestBuilder getRequestBuilder = transportClient.prepareGet("/vi/songs", "_id", "_EmilyWells2");
    	getRequestBuilder.setFields(new String[]{"_source"});
    	GetResponse response = getRequestBuilder.execute().actionGet();
    	String name = response.getField("name").getValue().toString();
    	System.out.println(name);
    }

}
