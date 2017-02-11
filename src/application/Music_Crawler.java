package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Music_Crawler extends WebCrawler {
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz))$");

	protected FXMLController controller;
	
	private static File storageFolder;

	public static void configure(String storageFolderName) 
	{
		storageFolder = new File(storageFolderName);
		if (!storageFolder.exists()) 
		{
			storageFolder.mkdirs();
		}
	}

	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		String href = url.getURL().toLowerCase();
		// Ignore the url if it has an extension that matches our defined set of
		// image extensions.
		if (FILTERS.matcher(href).matches()) {
			return false;
		}

		// Only accept the url if it is in the "www.ics.uci.edu" domain and
		// protocol is "http".
		return href.startsWith("http://www.azlyrics.com/lyrics/");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();

		System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);
		//System.out.println("Domain: " + domain);
		//System.out.println("Sub-domain: " + subDomain);
		//System.out.println("Path: " + path);
		//System.out.println("Parent page: " + parentUrl);
		//System.out.println("Anchor text: " + anchor);

		// POZOR NA TOTO
		// pouzit az v pripade viacerych vlaken
		// controller.result.appendText("Docid : " + docid);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			
			/*String filename = "D:\\JAVA\\projectECLIPSE\\VII_zadanie\\Data\\data5.txt";
			// store image
			try {
			    Document doc = Jsoup.connect("http://genius.com/Johny-cash-i-walk-the-line-lyrics").get();
			    Elements body = doc.select("body");
			    Elements lyric = doc.select("lyrics");
			    
			    System.out.println(body.toString());
			    
			    
				FileWriter fw = new FileWriter(filename,true); //the true will append the new data
			    fw.write(body.toString());	//appends the string to the file
			    fw.close();
		
			    logger.info("Stored: {}", url);
			} catch (IOException iox) {
				logger.error("Failed to write file: " + filename, iox);
			}*/
			
			/**
			 * Document document = Jsoup.parse(text); Element table =
			 * document.select("table").first(); String arrayName =
			 * table.select("th").first().text(); JSONObject jsonObj = new
			 * JSONObject(); JSONArray jsonArr = new JSONArray();
			 * 
			 * Elements ttls = table.getElementsByClass("ttl"); Elements nfos =
			 * table.getElementsByClass("nfo"); JSONObject jo = new
			 * JSONObject(); for (int i = 0, l = ttls.size(); i < l; i++) {
			 * String key = ttls.get(i).text(); String value =
			 * nfos.get(i).text(); jo.put(key, value); } jsonArr.add(jo);
			 * jsonObj.put(arrayName, jsonArr);
			 * 
			 * try { FileWriter fw1 = new FileWriter(filename, true);
			 * fw1.write(jsonObj.toJSONString());// appends the string to the
			 * fw1.close();
			 * System.out.println("Successfully Copied JSON Object to File...");
			 * System.out.println("\nJSON Object: " + jsonObj); } catch
			 * (IOException e) { System.err.println("IOException: " +
			 * e.getMessage()); }
			 */

		//	System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
		//	System.out.println("Number of outgoing links: " + links.size());
		}

		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
		//	System.out.printf("Response headers:");
			for (Header header : responseHeaders) {
			//	System.out.println("\t : " + header.getName() + header.getValue());
			}
		}
		System.out.println("=============");

	}

}
