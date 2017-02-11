package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

public class LyricsToJson {

	public LyricsToJson() {

	}

	public static String crawlAndSaveData(int from, int to, String fileName, String sign) throws IOException {

		ArrayList<Lyrics> songs = Genius.search("Johny cash");

		// other famous people are in file Data/artist.txt
		/*
		 * BufferedReader br = null; try { br = new BufferedReader(new
		 * FileReader("D:\\JAVA\\projectECLIPSE\\VII_zadanie\\Data\\artist.txt")
		 * ); } catch (FileNotFoundException e1) { e1.printStackTrace(); } try {
		 * StringBuilder sb = new StringBuilder(); String line = br.readLine();
		 * 
		 * for (int j = 550; j < 730; j++) {
		 * 
		 * 
		 * //while (line != null) { sb.append(line); sb.append("\n");
		 * songs.addAll(Genius.search(line)); line = br.readLine();
		 * 
		 * //for (int i = 0; i < songs.size(); i++) { //
		 * System.out.println(songs.get(i).mTitle); //} System.out.print("*");
		 * 
		 * } } finally { br.close(); } // System.out.println(song.mLyrics);
		 */
		String jsonInString = null;
		Gson g = new Gson();
		Gson gson = new Gson();

		//FileWriter fw = new FileWriter("D:\\JAVA\\projectECLIPSE\\VII_zadanie\\Data\\d_texty_25000.json");
		FileWriter fw = new FileWriter("D:\\JAVA\\projectECLIPSE\\VII_zadanie\\Data\\"+fileName);
		//for (int idx = 5000; idx < 25000; idx++) {
		for (int idx = from; idx < to; idx++ ) {
		//Lyrics obj = songs.get(idx);
			try {
				// 1. Java object to JSON, and save into a file, append to this

				//Lyrics t = Genius.fromURL(obj.mSourceUrl, obj.mArtist, obj.mTitle);
				Lyrics t = Genius.fromURLclear("http://genius.com/songs/"+idx);
				gson.toJson(t, fw);

				// 2. Java object to JSON, and assign to a String
				jsonInString = gson.toJson(t);
				// System.out.println(jsonInString);
			} catch (JsonIOException e) {

				e.printStackTrace();
			}
			System.out.print(sign);
			if (idx % 1000 == 0) 
			{
				System.out.println(idx);
			}
			
		}
		System.out.println("DONE");
		// sound of end
		java.awt.Toolkit.getDefaultToolkit().beep();

		return jsonInString;

		// return jsonInString;

	}

}
