package it.koen.eztv.net;

import it.koen.eztv.data.Episode;
import it.koen.eztv.data.Show;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

public class MyShows extends BaseScraper<Show>
{
	public static final String URL = "https://eztv.it/myshows/";

	public MyShows(Context context) {
		super(context);
	}

	@Override
	protected List<Show> process( Document doc )
	{
		List<Show> shows = new LinkedList<Show>();
		
		for( Element show : doc.select( "tr.table" ) )
		{
			Elements tds = show.getElementsByTag( "td" );
			if( tds.size() != 4 )
			{
				Log.w( "EZTV", "Found show row (tr.table) with !=4 td's" );
				continue;
			}
			String url = tds.get( 1 ).child( 0 ).attr( "href" );
			String title = tds.get( 1 ).text();
			String status = tds.get( 2 ).text();
			
			Show s = new Show( url, title, status );
			
			Log.d( "EZTV", s.toString() );
			
			Element next = show;
			do
			{
				next = next.nextElementSibling();
				if( next == null || !"hover".equals(next.attr( "name" )) )
					break;
				
				tds = next.getElementsByTag( "td" );
				if( tds.size() != 4 )
					continue;

				String w = tds.get(0).child( 0 ).attr( "href" );
				String u = tds.get(1).child( 0 ).attr( "href" );
				String n = tds.get(1).text();
				String r = tds.get(3).text();
				Episode e = new Episode( n, r, u, w );
				for( Element a : tds.get(2).getElementsByTag( "a" ) )
					e.addDownload( a.attr("href") );
						
				s.addEpisode( e );
				Log.d( "EZTV", "   "+e.toString() );
				
			} while( next != null );
			
			shows.add( s );
		}
		
		return shows;
	}


}
