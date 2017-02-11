package InsertData;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;

import application.FXMLController;
import application.Lyrics;
import javafx.application.Platform;
import javafx.scene.text.Text;

public class ThreadReadFomFiles implements Runnable {
	private FXMLController controller;
	List<Lyrics> listOfLyricsFromFile;
	private TransportClient client;
	public static String T = "Thread:InsertIntoElastic = ";

	public ThreadReadFomFiles(FXMLController controller, TransportClient client) {
		this.controller = controller;
		this.client = client;
	}

	public void run() {
		// all files in folder
		// and connect them into elasticSearch
		// correct dataset is dataset2
		File folder = new File("dataset2/");
		File[] listOfFiles = folder.listFiles();
		// List<Lyrics> listOfLyricsFromFile;
		int j = 0;
		//for (File file : listOfFiles) {
		//	if (file.isFile()) {
		for (int k = 300; k < 514; k++) {
			// skoncil pri 446
				String file = "data_"+k+".json";
		
				BulkRequestBuilder bulkRequest = client.prepareBulk();
				System.out.println(file);
				listOfLyricsFromFile = InsertData.ReadJsonFromFile("dataset2/" + file);
				System.out.println("OK");
				System.out.println(listOfLyricsFromFile.size());
				for (int i = 0; i < listOfLyricsFromFile.size(); i++) {
					try {
						if(listOfLyricsFromFile.get(i).mFlag != 1)
							continue;
						controller.result.appendText(listOfLyricsFromFile.get(i).mArtist + "\n\n");
						controller.result.appendText(listOfLyricsFromFile.get(i).mLyrics + "\n");
						controller.result.appendText(controller.b + controller.b + '\n');

						controller.infoArea.appendText(listOfLyricsFromFile.get(i).mArtist + '\n');
						Text t1 = new Text(listOfLyricsFromFile.get(i).mArtist + "\n");
						controller.textFlow.getChildren().add(t1);

						// either use client#prepare, or use Requests# to
						// directly build
						// index/delete requests
						// indexName must be lowerCase
						String indexName = listOfLyricsFromFile.get(i).mSource.toLowerCase();
						String typeName = listOfLyricsFromFile.get(i).mArtist;
						String lyricText = listOfLyricsFromFile.get(i).mLyrics;
						String originalArtist = listOfLyricsFromFile.get(i).mOriginalArtist;
						String originalTitle = listOfLyricsFromFile.get(i).mOriginalTitle;
						String sourceUrl = listOfLyricsFromFile.get(i).mSourceUrl;
						String coverUrl = listOfLyricsFromFile.get(i).mCoverURL;
						String title = listOfLyricsFromFile.get(i).mTitle;

						// prepareIndex(genius , artist , id_document)
						//System.out.println("prepare index: " + indexName + " " + typeName + " " + i);
						bulkRequest.add(client.prepareIndex(indexName, typeName, "1")
								.setSource(jsonBuilder().startObject().field("artist", typeName)
										.field("lyric_text", lyricText).field("title", title)
										.field("source url", sourceUrl).field("original artist", originalArtist)
										.field("original artist", originalTitle).field("coverUrl",coverUrl).endObject()));

						BulkResponse bulkResponse = bulkRequest.get();
						if (bulkResponse.hasFailures()) {
							// process failures by iterating through each bulk
							// response
							controller.infoArea.appendText(T + bulkResponse.buildFailureMessage() + "\n");
							controller.infoArea.appendText(bulkResponse.toString() + "\n");
							// incorrect
						}
					} catch (UnknownHostException e) {
						controller.result.setText(T + e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						controller.result.setText(T + e.getMessage());
						e.printStackTrace();
					}
				}
			//}
			j++;
			System.out.println(j);
		}
	}

	public List<Lyrics> getLyrics() {
		return listOfLyricsFromFile;
	}

}
