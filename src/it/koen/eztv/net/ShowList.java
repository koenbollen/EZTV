package it.koen.eztv.net;

import it.koen.eztv.data.Show;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;

public class ShowList extends BaseScraper<Show>
{
	public static final String URL = "https://eztv.it/showlist/";
	
	public ShowList(Context context) {
		super(context);
	}

	@Override
	protected List<Show> process(Document doc) {
		
		List<Show> result = new LinkedList<Show>();
        
		Element a = null, b = null;
		Iterator<Element> it = doc.select( "td.forum_thread_post" ).iterator();
		while( it.hasNext() )
		{
			try
			{
				a = it.next();
				b = it.next();
				it.next();
			} catch( NoSuchElementException e )
			{
				break;//ing bad
			}
			String url = null;
			try
			{
				url = "http://eztv.it"+a.select("a").first().attr("href");
			} catch( NullPointerException e )
			{
			}
			String title = a.text();
			String status = b.text();
			result.add( new Show( url, title, status ) );
		}
			
		
		return result;
	}
}
