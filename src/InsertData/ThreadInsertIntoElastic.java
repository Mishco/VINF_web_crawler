package InsertData;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;

import application.FXMLController;
import application.Lyrics;

public class ThreadInsertIntoElastic implements Runnable {
	private FXMLController controller;
	private TransportClient client;
	private List<Lyrics> listOfLyricsFromFile;
	public static String T = "Thread:InsertIntoElastic = ";

	public ThreadInsertIntoElastic(FXMLController controller, TransportClient client,
			List<Lyrics> listOfLyricsFromFile) {
		super();
		this.controller = controller;
		this.client = client;
		this.listOfLyricsFromFile = listOfLyricsFromFile;
	}

	public void run() {
		//
		// Add this things into ES
		int countOfcorrect = 0;
		try {

			// String indexName =
			// getIndexForType(listOfLyricsFromFile.get(0).getClass());
			// String typeName =
			// MappingBuilder.indexTypeFromClass(data.getClass());
			// client.prepareIndex(indexName,typeName).setOperationThreaded(false).setSource(json).setRefresh(true).execute().actionGet();
			System.out.println("Running thread to insert data into ES");
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			
			for (int i = 0; i < listOfLyricsFromFile.size(); i++) {
				// either use client#prepare, or use Requests# to directly build
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
				System.out.println("prepare index: " + indexName + " " + typeName + " " + i);
				bulkRequest.add(client.prepareIndex(indexName, typeName, "1").setSource(jsonBuilder().startObject()
						.field("artist", typeName).field("lyric_text", lyricText)
						.field("title", title).field("source url", sourceUrl)
						.field("original artist", originalArtist).field("original artist", originalTitle)
						.endObject()));
				
				BulkResponse bulkResponse = bulkRequest.get();
				countOfcorrect++;
				if (bulkResponse.hasFailures()) {
					// process failures by iterating through each bulk response
					controller.infoArea.appendText(T + bulkResponse.buildFailureMessage()+"\n");
					controller.infoArea.appendText(bulkResponse.toString()+"\n");
					// incorrect
					countOfcorrect--;
				}
			}
		} catch (UnknownHostException e) {
			controller.result.setText(T + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			controller.result.setText(T + e.getMessage());
			e.printStackTrace();
		}
		System.out.println(T + "Succesful append into ES\n");
		controller.infoArea.setText(T + "Succesful append\n");
		controller.infoArea.appendText("Count of mapping objects: " + countOfcorrect + '\n');
	}// end of run
	
	
}
