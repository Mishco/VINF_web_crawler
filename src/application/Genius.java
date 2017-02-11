package application;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.uci.ics.crawler4j.util.Net;


/**
 * This file is part of QuickLyric 
 * Created by geecko 
 * <p/> 
 * QuickLyric is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * <p/> 
 * QuickLyric is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License 
 * along with QuickLyric.  If not, see <http://www.gnu.org/licenses/>. 
 */ 

public class Genius {
    public static final String domain = "genius.com"; 
    public static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"; 
 
    
    public static ArrayList<Lyrics> search(String query) { 
        ArrayList<Lyrics> results = new ArrayList<>(); 
        query = Normalizer.normalize(query, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); 
        JsonObject response = null; 
        try { 
            URL queryURL = new URL(String.format("http://api.genius.com/search?q=%s", URLEncoder.encode(query, "UTF-8"))); 
            Connection connection = Jsoup.connect(queryURL.toExternalForm()) 
                    .header("Authorization", "Bearer " + Keys.GENIUS) 
                    .ignoreContentType(true); 
            Document document = connection.userAgent(USER_AGENT).get(); 
            response = new JsonParser().parse(document.text()).getAsJsonObject(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
 
        if (response == null || response.getAsJsonObject("meta").get("status").getAsInt() != 200) 
            return results; 
        JsonArray hits = response.getAsJsonObject("response").getAsJsonArray("hits"); 
 
        int processed = 0; 
        while (processed < hits.size()) { 
            JsonObject song = hits.get(processed).getAsJsonObject().getAsJsonObject("result"); 
            String artist = song.getAsJsonObject("primary_artist").get("name").getAsString(); 
            String title = song.get("title").getAsString(); 
            String url = "http://genius.com/songs/" + song.get("id").getAsString(); 
            Lyrics l = new Lyrics(); 
            l.mArtist = artist; 
            l.mTitle = title; 
            l.mSourceUrl = url; 
            l.mSource = "Genius"; 
            results.add(l); 
            processed++; 
        } 
        return results; 
    }
    
    
    
    public static Lyrics fromURLclear(String url) { 
        Document lyricsPage; 
        String text;
        String artist = null;
        String title = null;
        try { 
            lyricsPage = Jsoup.connect(url).userAgent(USER_AGENT).get(); 
            Elements lyricsDiv = lyricsPage.select("div.song_body-lyrics"); 
            if (lyricsDiv.isEmpty()) 
                throw new StringIndexOutOfBoundsException(); 
            else 
                text = Jsoup.clean(lyricsDiv.html(), Whitelist.none().addTags("br")).trim(); 
        } catch (HttpStatusException e) { 
            return new Lyrics(Lyrics.NO_RESULT); 
        } catch (IOException | StringIndexOutOfBoundsException e) { 
            e.printStackTrace(); 
            return new Lyrics(Lyrics.ERROR); 
        } 
        if (artist == null) { 
            title = lyricsPage.getElementsByClass("song_header-primary_info-title").get(0).text(); 
            artist = lyricsPage.getElementsByClass("song_header-primary_info-primary_artist").get(0).text(); 
        } 
        Lyrics result = new Lyrics(Lyrics.POSITIVE_RESULT); 
        if ("[Instrumental]".equals(text)) 
            result = new Lyrics(Lyrics.NEGATIVE_RESULT); 
        Pattern pattern = Pattern.compile("\\[.+\\]"); 
        StringBuilder builder = new StringBuilder(); 
        for (String line : text.split("<br> ")) { 
            String strippedLine = line.replaceAll("\\s",""); 
            if (!pattern.matcher(strippedLine).matches() && !(strippedLine.isEmpty() && builder.length() == 0)) 
                builder.append(line).append("<br/>"); 
        } 
        builder.delete(builder.length() - 5, builder.length()); 
        result.mArtist = artist; 
        result.mTitle = title; 
        result.mLyrics = builder.toString(); 
        result.mSourceUrl = url; 
        result.mSource = "Genius"; 
        return result; 
    } 
    
    
    public static Lyrics fromURL(String url, String artist, String title) { 
        Document lyricsPage; 
        String text; 
        try { 
            lyricsPage = Jsoup.connect(url).userAgent(USER_AGENT).get(); 
	            Elements lyricsDiv = lyricsPage.select("div.song_body-lyrics"); 
            if (lyricsDiv.isEmpty()) 
                throw new StringIndexOutOfBoundsException(); 
            else 
                text = Jsoup.clean(lyricsDiv.html(), Whitelist.none().addTags("br")).trim(); 
        } catch (HttpStatusException e) { 
            return new Lyrics(Lyrics.NO_RESULT); 
        } catch (IOException | StringIndexOutOfBoundsException e) { 
            e.printStackTrace(); 
            return new Lyrics(Lyrics.ERROR); 
        } 
        if (artist == null) { 
            title = lyricsPage.getElementsByClass("text_title").get(0).text(); 
            artist = lyricsPage.getElementsByClass("text_artist").get(0).text(); 
        } 
        Lyrics result = new Lyrics(Lyrics.POSITIVE_RESULT); 
        if ("[Instrumental]".equals(text)) 
            result = new Lyrics(Lyrics.NEGATIVE_RESULT); 
        Pattern pattern = Pattern.compile("\\[.+\\]"); 
        StringBuilder builder = new StringBuilder(); 
        for (String line : text.split("<br> ")) { 
            String strippedLine = line.replaceAll("\\s",""); 
            if (!pattern.matcher(strippedLine).matches() && !(strippedLine.isEmpty() && builder.length() == 0)) 
                builder.append(line).append("<br/>"); 
        } 
        builder.delete(builder.length() - 5, builder.length()); 
        result.mArtist = artist; 
        result.mTitle = title; 
        result.mLyrics = builder.toString(); 
        result.mSourceUrl = url; 
        result.mSource = "Genius"; 
        return result; 
    } 
    
    
}
