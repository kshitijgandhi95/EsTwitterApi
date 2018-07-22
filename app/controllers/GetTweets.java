package controllers;

import play.mvc.*;
import play.mvc.Http.RequestBody;
import views.html.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.api.mvc.MultipartFormData;
import play.data.DynamicForm;
import play.data.Form;


//GET /twitter/_search?q=guide
public class GetTweets extends Controller{

	@SuppressWarnings("deprecation")
	public Result tweets() throws IOException {
		JsonNode body = request().body().asJson();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode allTweets = mapper.createObjectNode();
		ObjectNode returnVal = mapper.createObjectNode();
		System.out.println("*******************************");
		for(JsonNode json: body) {
			JsonNode vals = null;
			vals = getTweets(json.toString());
			
			Iterator<String> itr = vals.fieldNames();
			
			while(itr.hasNext()) {
				String str = itr.next();
				System.out.println(vals.get(str));
				allTweets.set(str, vals.get(str));
			}
		}
		returnVal.put("count", allTweets.size());
		returnVal.set("Tweets", allTweets);
		return ok(returnVal);
		
		
		
	}
	
	public static JsonNode getTweets(String str) throws IOException {
		String url = "http://localhost:9200/twitter/_search?q="+str;
		String[] response = null;
		HttpURLConnection con = null;
		StringBuilder content;
		try {

            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();

            con.setRequestMethod("GET");

            

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            System.out.println(content.toString());

        } finally {
            
            con.disconnect();
        }
		
		return convertToJsonNode(content.toString());
		
		
	}
	
	public static JsonNode convertToJsonNode(String jsonString) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
	    JsonNode actualObj = mapper.readTree(jsonString);
	    ArrayNode sourceNode = (ArrayNode) actualObj.get("hits").get("hits");
	    ObjectNode returnNode = mapper.createObjectNode();
	    for(JsonNode json:sourceNode) {
	    	JsonNode item = json.get("_source");
	    	returnNode.put(item.get("id").toString(),item);
	    }
		return returnNode;
	}
	
}
