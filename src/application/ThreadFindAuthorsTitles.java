package application;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class ThreadFindAuthorsTitles implements Runnable {
	private FXMLController controller;
	private TransportClient client;
	private String artistName; 
	
	public ThreadFindAuthorsTitles(FXMLController controller, TransportClient client, String artistName) {
		this.controller = controller;
		this.client = client;
		this.artistName = artistName;
	}
	public ThreadFindAuthorsTitles(FXMLController controller, TransportClient client) {
		this.controller = controller;
		this.client = client;
	}
	
	public SearchResponse getResult() {
		SearchResponse result = client.prepareSearch("genius").execute().actionGet();
		return result;
	}
	
	@Override
	public void run() {
		// get all 
		//SearchResponse response = client.prepareSearch().execute().actionGet();
		
		// Elastic Search
		// find all titles from artist 
		/*SearchResponse response = client.prepareSearch("index1", "index2")
		        .setTypes("type1", "type2")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
		        .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
		        .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();
		*/
		
		/*
		
		bulkRequest.add(client.prepareIndex(indexName, typeName, "1").setSource(jsonBuilder().startObject()
				.field("artist", typeName).field("lyric_text", lyricText)
				.field("title", title).field("source url", sourceUrl)
				.field("original artist", originalArtist).field("original artist", originalTitle)
				.endObject()));
		
		*/
		
		/*
		SearchResponse artistResponse = client.prepareSearch("index")
				.setTypes("genius")
			    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
			    .setQuery(QueryBuilders.termQuery("artist", artistName))
			    .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();
        SearchHit[] searchResponse = artistResponse.getHits().getHits();*/
		/*
		POST /genius/_search
		{
		    "query" : {
		        "match": {
		           "lyric_text": "some"
		        }
		    },
		    "highlight": {
		      "fields": {
		         "lyric_text": {}
		      }      
		    }
		}
        */
       /* for(SearchHit hit : searchResponse){
            System.out.println(hit.getSource());
        }*/
		
	}

}
