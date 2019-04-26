package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class Reader {
	
	private static final String FEEDS_PROPERTY_FILEPATH = "feeds.properties";
	
	public static void run(){
		
			Map<String, String> feedMap = loadFeeds(FEEDS_PROPERTY_FILEPATH);
			Map<String, Boolean> receivedNews = new HashMap<String, Boolean>();
			
			while(true){
				
				try {
				
				for(Map.Entry<String, String> entry : feedMap.entrySet()){
					
					URL url = new URL(entry.getValue());
					HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
					
					// Reading the feed
					SyndFeedInput input = new SyndFeedInput();
					SyndFeed feed = input.build(new XmlReader(httpcon));
					List<SyndEntry> entries = feed.getEntries();
					Iterator<SyndEntry> itEntries = entries.iterator();
					
					while (itEntries.hasNext()) {
						SyndEntry article = itEntries.next();
						
						if(!receivedNews.containsKey(article.getTitle())){
							System.out.println();
							System.out.println("[" + entry.getKey() + "] " + article.getTitle());
							System.out.println("-----------------------------------");
							receivedNews.put(article.getTitle(), true);
						}	
					}
				}
				System.out.print("=");
				
				if(receivedNews.size() > 50*feedMap.size())
					receivedNews.clear();
				Thread.sleep(30*1000);
				
				} catch (FileNotFoundException e) {
					System.out.println(e.getMessage());
				} catch (IOException e) {
					System.out.println(e.getMessage());
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
				} catch (FeedException e) {
					System.out.println(e.getMessage());
				}
			}
	}

	
	private static Map<String, String> loadFeeds(String propertyFilePath){
		
		Properties properties = new Properties();
		Map<String, String> map = new HashMap<String, String>();
		
	    try {
			properties.load(new FileReader(new File(propertyFilePath)));

		    for (String key : properties.stringPropertyNames()){
		        map.put(key, properties.getProperty(key));
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    return map;
	}
	

	public static void main(String[] args) {
		Reader.run();
	}
}
