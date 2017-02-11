package application;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.JSONObject;

import InsertData.ApacheHttpClientPost;
import InsertData.ThreadInsertIntoElastic;
import InsertData.ThreadReadFomFiles;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class FXMLController implements Initializable {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML
	private Button crawl;
	@FXML
	public TextArea result;
	@FXML
	private Button search;
	@FXML
	private CheckBox checkBoxConnectify;
	@FXML
	private TextField inputText;
	@FXML
	public TextArea infoArea;
	@FXML
	public TextFlow textFlow;
	@FXML
	public ProgressIndicator progress;

	public String b = "---------------------------------";
	private String crawlStorageFolder;
	private int maxDepthOfCrawling;
	private int politenessDelay;

	public TransportClient client;

	// starting method
	public void initialize(URL location, ResourceBundle resources) {

		// crawling data from genius.com is forbidden
		// if you want to crawl data must set another page or you rewrite old
		// data
		crawl.setDisable(false);
		infoArea.setEditable(false);

		result.setMaxWidth(900);
		result.setWrapText(true);
		
		// function to access information
		// every String which user input to TextField
		// is output to result text
		Platform.runLater(() -> inputText.requestFocus());
		inputText.setOnAction(event -> {
			result.setText("Search item: " + inputText.getText() + "\n");
			// if
				
			
			
			if (inputText.getText().length() == 1 && inputText.getText().endsWith("*")) {
				// get all document and
				// GET /genius/_search/?size=1000&pretty=1
				ApacheHttpClientPost.httpGet(this);
					
			} else {

				// start search in both indexes
				String searchIndex = inputText.getText();
				int maxSizeResult = 10;
				ThreadSearchString s = new ThreadSearchString(this, searchIndex, client, maxSizeResult);
				s.run();
				result.setText(s.getResulthits());
				infoArea.setText(s.getStatistic());
				
				//String highlihts = s.getResulthits().substring(s.getResulthits().indexOf("<em>"), s.getResulthits().indexOf("</em>"));
				String tmp = s.getResulthits();
				List<String> allMatches = new ArrayList<String>();
				 Matcher m = Pattern.compile("<em>.+?</em>")
				     .matcher(tmp);
				 while (m.find()) {
					 allMatches.add(m.group());
				 }
				 System.out.println(allMatches.toString());
				 infoArea.appendText("\ncount of search words: " + allMatches.size());
				
				/*
				 * infoArea.setText("time of search: " + s.getTimeOfResponse() +
				 * "\n"); infoArea.appendText("count of result: " +
				 * s.getnbHist() + "\n"); infoArea.appendText("document: " +
				 * s.getTotalShard() + "\n");
				 */
			}
		});

		// function to clicked action
		// on authors
		textFlow.setOnMouseClicked(ev -> {
			if (ev.getTarget() instanceof Text) {
				Text clicked = (Text) ev.getTarget();

				ObservableList<Node> children = textFlow.getChildren();
				for (Iterator<Node> iterator = children.iterator(); iterator.hasNext();) {
					Node node = (Node) iterator.next();
					node.setStyle("-fx-font-size: 12; -fx-fill: black;");
				}

				result.setText("Clicked on: " + clicked.getText());
				clicked.setStyle("-fx-font-size: 12; -fx-fill: darkred;");
				// Elastic Search
				// find author's title of songs
				findAuthorsTitle(clicked.getText());

			}
		});

		// start
		// client to communicated to server
		try {
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
			// .addTransportAddress(new
			// InetSocketTransportAddress(InetAddress.getByName("localhost"),
			// 9300));
		} catch (UnknownHostException e) {
			result.appendText(b + e.getMessage() + b);
			e.printStackTrace();
			System.exit(1);
		}

		// progress.setProgress(0.25F);

	}

	public void buttonSearch() {
		int maxSizeResult = 1000;
		String searchIndex = inputText.getText();
		ThreadSearchString s = new ThreadSearchString(this, searchIndex, client, maxSizeResult);
		s.run2();
		// result.setText(s.getResulthits());
		result.setText(s.getResponseResult().replaceAll("<br/>", ""));
		infoArea.setText("time of search: " + s.getTimeOfResponse() + "\n");
		infoArea.appendText("count of result: " + s.getnbHist() + "\n");
		infoArea.appendText("document: " + s.getTotalShard() + "\n");

		// System.out.println(s.getResponseResult());

	}

	public void checkBoxConnect() {
		// insert first data
		// every data from files
		if (checkBoxConnectify.isSelected()) {
			// result.setText(b + "connect" + b + "\n");
			// InsertData.InsertDataElastic("dataset2/data_1.json");
			// infoArea.setText("Correct add into elasticSearch");

			// ApacheHttpClientPost.httpPOSTandGET();
			// get inserted data from elasticSearch
			// get all data and connect that into

			ThreadFindAuthorsTitles t = new ThreadFindAuthorsTitles(this, client);
			t.run();
			// System.out.println(t.getResult());

			// Analyzer analyzer = new StandardAnalyzer();//
			// https://www.elastic.co/guide/en/elasticsearch/reference/2.0/analysis-standard-analyzer.html
			// String indexName = "genius";

			ReadFromEveryFile();

		} else {
			result.setText(b + "disconnect" + b + "\n");
			// on shutdown
			client.close();

		}

		// only one file
		// add into elasticSearch

		/*
		 * List<Lyrics> listOfLyricsFromFile =
		 * InsertData.ReadJsonFromFile("dataset/data_2.json"); for (int i = 0; i
		 * < listOfLyricsFromFile.size(); i++) {
		 * result.appendText(listOfLyricsFromFile.get(i).mArtist + "\n\n");
		 * result.appendText(listOfLyricsFromFile.get(i).mLyrics + "\n");
		 * result.appendText(b + b + '\n');
		 * 
		 * infoArea.appendText(listOfLyricsFromFile.get(i).mArtist + '\n'); Text
		 * t1 = new Text(listOfLyricsFromFile.get(i).mArtist + "\n");
		 * textFlow.getChildren().add(t1); progress.setProgress(i / 0.25F); }
		 */
		// List<Lyrics> alllistOfLyricsFromFile =

		//InsertFileIntoElastic(alllistOfLyricsFromFile);

		// set color on text flow artist black
		ObservableList<Node> children = textFlow.getChildren();
		for (Iterator<Node> iterator = children.iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			node.setStyle("-fx-font-size: 12; -fx-fill: black;");
		}
	}

	// insert into ES
	// from linked list from files
	private void InsertFileIntoElastic(List<Lyrics> list) {
		ThreadInsertIntoElastic t = new ThreadInsertIntoElastic(this, client, list);
		t.run();
	}

	// reading from files
	// saving into linked list
	private void ReadFromEveryFile() {
		ThreadReadFomFiles t = new ThreadReadFomFiles(this, client);
		t.run();
		// return t.getLyrics();
	}

	private void findAuthorsTitle(String artistName) {
		System.out.println(artistName);
		ThreadFindAuthorsTitles f = new ThreadFindAuthorsTitles(this, client, artistName);
		f.run();
	}

	// get data from web
	// using special api.genius.com
	@FXML
	public void buttonAction() throws Exception {
		// private SearchResponse response;
		// private String clustername;
		// int numberOfCrawlers;

		crawlStorageFolder = "D:\\JAVA\\projectECLIPSE\\VII_zadanie\\DataNew";
		int numberOfCrawlers = 5;
		maxDepthOfCrawling = 7;
		politenessDelay = 1000;

		// result.setText("Start crawling data\n");
		System.out.println("StartCrawling");
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setMaxDepthOfCrawling(maxDepthOfCrawling);
		config.setPolitenessDelay(politenessDelay);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		controller.addSeed("http://www.azlyrics.com/lyrics/");
		controller.addSeed("http://www.azlyrics.com/a/a.html");
		for (int i = 0; i < 25; i++) {
			controller.addSeed("http://www.azlyrics.com/" + 'a' + i + '/' + ('a' + i) + ".html");
		}
		controller.start(Music_Crawler.class, numberOfCrawlers);

		/*
		 * controller.addSeed("http://pop.genius.com/");
		 * controller.addSeed("http://www.supermusic.sk/");
		 * controller.addSeed("http://www.karaoketexty.cz/");
		 */
		// controller.addSeed("http://www.karaoketexty.cz/texty-pisni/johnny-cash-15978");
		// controller.addSeed("http://www.karaoketexty.cz/texty-pisni/cash-johnny/sixteen-tons-381739");

		// ArrayList<Lyrics> songs = Genius.search("Justin Bieber");
		// for (int i=0; i < songs.size(); i++) {
		// System.out.println(songs.get(i).mTitle);
		// }

		/*
		 * Thread t1 = new Thread(new Runnable() { public void run() { try { //
		 * starting crawling LyricsToJson.crawlAndSaveData(25000, 27000,
		 * "d_texty_27000.json" ,"a"); } catch (IOException e) {
		 * e.printStackTrace(); } } }); t1.start();
		 * 
		 * 
		 * Thread t2 = new Thread(new Runnable() { public void run() { try { //
		 * starting crawling LyricsToJson.crawlAndSaveData(27000, 29000,
		 * "d_texty_29000.json", "b"); } catch (IOException e) {
		 * e.printStackTrace(); } } }); t2.start();
		 * 
		 * Thread t3 = new Thread(new Runnable() { public void run() { try { //
		 * starting crawling LyricsToJson.crawlAndSaveData(29000, 31000,
		 * "d_texty_31000.json", "c"); } catch (IOException e) {
		 * e.printStackTrace(); } } }); t3.start();
		 */

		/*
		 * int from = 1814000; // int MaxTo = 100000; int to = 1824000; String
		 * fileName = "d_texty_"; for (int i = 0; i < 100; i++) { // starting
		 * crawling String sign = "a" + String.valueOf(i);
		 * 
		 * System.out.println("create thread: " + i); MyThread t = new
		 * MyThread(from, to, fileName, sign); t.run(); }
		 */
	}

}
