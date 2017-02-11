package InsertData;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import application.Lyrics;

public class InsertData {

	// Controller of main class
	public static String b = "-----------";
	public static String T = "Class:InsertData = ";

	public static void InsertDataElastic(String pathToFile) {
		// Add directly into into elastic from File
		File jsonFile = new File(pathToFile);
		HttpEntity entity = new FileEntity(jsonFile);
		HttpPost post = new HttpPost("http://localhost:9200/_bulk");
		post.setEntity(entity);

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		HttpClient client = clientBuilder.build();

		post.addHeader("content-type", "text/plain");
		post.addHeader("Accept", "text/plain");
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Response: " + response);
	}

	// read from file
	// get content of file to json object
	// save json object to JSONArray
	// return JSONarray
	public static List<Lyrics> ReadJsonFromFile(String pathToFile) {
		List<Lyrics> listLyricsResult = new LinkedList<Lyrics>();

		Gson gson = new Gson();
		try {
			// JSON to Java object, read it from a file.
			// lyrics = gson.fromJson(new FileReader(pathToFile), Lyrics.class);
			// System.out.println(lyrics.mLyrics);
			// System.out.println(lyrics.mArtist);

			TypeToken<List<Lyrics>> token = new TypeToken<List<Lyrics>>() {
			};
			// List<Lyrics> listLyrics = gson.fromJson(new
			// FileReader(pathToFile), token.getType());

			listLyricsResult = gson.fromJson(new FileReader(pathToFile), token.getType());
			List<Lyrics> listLyrics = listLyricsResult;
			// remove garbage
			// and remove html tags which stayed in json file
			if (!listLyrics.isEmpty())
				System.out.println(pathToFile+ " " + listLyrics.size());
			
				
			/*for (int i = 0; i < listLyrics.size(); i++) {
				if (null == listLyrics.get(i) || listLyrics.get(i).mFlag != 1) {
					//System.out.println(b + i + " error nema pesnicku");
					continue;// return null;
				}
				Lyrics e = new Lyrics();
				e.mArtist = listLyrics.get(i).mArtist;
				//e.mLyrics = removeGarbage(transformTextSong(listLyrics.get(i).mLyrics));
				e.mLyrics = transformTextSong(listLyrics.get(i).mLyrics);
				e.mCoverURL = listLyrics.get(i).mCoverURL;
				e.mFlag = listLyrics.get(i).mFlag;
				e.mOriginalArtist = listLyrics.get(i).mOriginalArtist;
				e.mOriginalTitle = listLyrics.get(i).mOriginalTitle;
				e.mSource = listLyrics.get(i).mSource;
				e.mSourceUrl = listLyrics.get(i).mSourceUrl;
				e.mTitle = listLyrics.get(i).mTitle;
				listLyricsResult.add(e);
			}
*/
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// return clean all lyrics from file
		return listLyricsResult;
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
