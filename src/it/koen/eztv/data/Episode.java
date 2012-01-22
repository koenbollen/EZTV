package it.koen.eztv.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Episode
{
	public final Show show;
	public final String name;
	public final String released;
	public final String url;
	public final String watched_url;

	private List<String> downloads;

	public Episode( Show show, String name, String released, String url, String watched )
	{
		super();
		this.show = show;
		this.name = name;
		this.released = released;
		this.url = url;
		this.watched_url = watched;
		
		this.downloads = new LinkedList<String>();
	}

	
	@Override
	public String toString()
	{
		return this.name;
	}



	public synchronized void addDownload( String download )
	{
		this.downloads.add( download );
	}
	
	public synchronized List<String> getDownloads()
	{
		return Collections.unmodifiableList( this.downloads );
	}
	
	public synchronized String getFirstDownloadUrl()
	{
		if( this.downloads.size() > 0 )
			return this.downloads.get( 0 );
		return null;
	}
	
	
}
