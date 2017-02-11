package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.JSONObject;

import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.google.gson.Gson;

import InsertData.ApacheHttpClientPost;

public class ThreadSearchString implements Runnable {
	private FXMLController controller;
	private String searchString;
	private TransportClient client;
	private int maxResultSize;
	private SearchResponse response;
	private long nbHits = 0;
	private StringBuilder stringResponse;
	private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
	private String resultHits;
	private String statistis;
	
	public ThreadSearchString(FXMLController controller, String search, TransportClient client, int maxResultSize) {
		this.controller = controller;
		this.searchString = search;
		this.client = client;
		this.maxResultSize = maxResultSize;
	}
	
	public String getStatistic() {
		return statistis;
	}
	
	
	public static String removeTags(String string) {
	    if (string == null || string.length() == 0) {
	        return string;
	    }

	    Matcher m = REMOVE_TAGS.matcher(string);
	    return m.replaceAll("");
	}

	
	@Override
	public void run() {
	        //String path="D:\\Tools\\javadoc.txt", 
			String filecontent="";
	        //String path="D:\\Tools\\data_161.json";
	        
	        ApacheHttpClientPost apacheHttpClientPost = new ApacheHttpClientPost();
	        try {
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpPost postRequest = new HttpPost("http://localhost:9200/genius/_search");
	            //filecontent=apacheHttpClientPost.readFileContent(path);
	            // removing html tags
	            
	            filecontent = "{ \"query\" : { \"match\" : { \"lyric_text\": \""+ searchString + "\" } }, \"highlight\": { \"fields\": { \"lyric_text\": {} }  } } ";
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
	            StringBuilder sb = new StringBuilder();
	            while ((output = br.readLine()) != null) {

	                System.out.println(output);
	                sb.append(output);
	            }
	            statistis = sb.substring(sb.indexOf("\"took\":"), sb.indexOf("\"_index\":"));
	            System.out.println(statistis);
	            
	            resultHits = sb.substring(sb.indexOf("\"lyric_text\":" ), sb.length());
	            System.out.println(resultHits.replaceAll("<br/>", ""));
	            
	            
	            
	            System.out.println("----------");    
	            String url = "http://localhost:9200/genius/_search";
	            		

	            HttpClient client = HttpClientBuilder.create().build();
	            HttpGet request = new HttpGet(url);

	            // add request header
	            // request.addHeader("User-Agent", USER_AGENT);
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
	            //System.out.println(result.toString());
	            
	            
	            httpClient.getConnectionManager().shutdown();
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	}
	

	
	public void run2() {
		stringResponse = new StringBuilder();
		Gson g = new Gson();
		List<Lyrics> listLyricsResult = new LinkedList<Lyrics>();
		
		SearchRequestBuilder srb1 = client.prepareSearch().setQuery(QueryBuilders.queryStringQuery("genius"))
				.setSize(maxResultSize);
		SearchRequestBuilder srb2 = client.prepareSearch()
				.setQuery(QueryBuilders.matchQuery("lyric_text", searchString)).setSize(maxResultSize);

		
		MultiSearchResponse sr = client.prepareMultiSearch()
				//.add(srb1)
				.add(srb2)
				.execute().actionGet();

		// You will get all individual responses from
		// MultiSearchResponse#getResponses()
		// nbHist = 0;
		for (MultiSearchResponse.Item item : sr.getResponses()) {
			response = item.getResponse();
			nbHits += response.getHits().getTotalHits();
			SearchHit[] searchResponse = response.getHits().getHits();

			for (SearchHit hit : searchResponse) {
				//System.out.println(hit.getSource());
				stringResponse.append((hit.getSource()));

				//listLyricsResult = searchResponse.toString();
				//JSONObject jsonObj = new JSONObject(hit.getSourceAsString());
				//System.out.println(result);			
				System.out.println(hit.getSourceAsString());
			}
		}
		
		

	}

	public String getResponseResult() {
		return stringResponse.toString();
		//return response.toString();
	}

	public long getTimeOfResponse() {
		return response.getTookInMillis();
	}

	public long getnbHist() {
		return nbHits;
	}
	public int getTotalShard() {
		return response.getTotalShards();
	}
	
	
	public String getResulthits() {
		return resultHits.replaceAll("<br/>", "").replaceAll("More on Genius      Mafia III Soundtrack: A Genius Liner Notes Experience   Genius", "");
	}

	private static String transformTextSong(String text) {
		String pattern = "<br/>";
		if (text == null)
			return null;

		return text.replaceAll(pattern, "");
	}

	private static String removeGarbage(String text) {
		String garbage = "More on Genius";
		if (text == null)
			return null;

		return text.substring(0, text.indexOf(garbage));
		
	}
}
