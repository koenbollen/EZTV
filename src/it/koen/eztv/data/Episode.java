package it.koen.eztv.data;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class Episode implements Parcelable
{
	private static final ThreadLocal<HttpClient> tlClient = new ThreadLocal<HttpClient>();

	private static HttpClient getHttpClient()
	{
		if( Episode.tlClient.get() == null )
		{
			HttpClient c = new DefaultHttpClient();
			Episode.tlClient.set( c );
		}
		return Episode.tlClient.get();
	}

    public static final Parcelable.Creator<Episode> CREATOR
            = new Parcelable.Creator<Episode>() {
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };
	
	public final String name;
	public final String released;
	public final String url;
	public final String watched_url;

	private List<String> downloads;

	public Episode( String name, String released, String url, String watched )
	{
		super();
		this.name = name;
		this.released = released;
		this.url = url;
		this.watched_url = watched;

		this.downloads = new LinkedList<String>();
	}

	protected Episode( Parcel in )
	{
		this.name = in.readString();
		this.url = in.readString();
		this.released = in.readString();
		this.watched_url = in.readString();

		int size = in.readInt();
		this.downloads = new LinkedList<String>();
		for( int i = 0; i < size; i++ )
			this.downloads.add( in.readString() );
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

	public boolean onWatchedClick( View v )
	{
		SharedPreferences ps = v.getContext().getSharedPreferences( "it.koen.eztv", Context.MODE_PRIVATE );
		String u = ps.getString( "username", null );
		String p = ps.getString( "password", null );

		HttpClient client = Episode.getHttpClient();
		HttpGet get = new HttpGet( "https://eztv.it" + this.watched_url );
		get.setHeader( "Cookie", "username=" + u + "; password=" + p );

		try
		{
			HttpResponse res = client.execute( get );
			res.getEntity().consumeContent();
			return (res.getStatusLine().getStatusCode() == 200);
		} catch( ClientProtocolException ball )
		{
			ball.printStackTrace();
		} catch( IOException ball )
		{
			ball.printStackTrace();
		}
		return false;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel( Parcel dest, int flags )
	{
		dest.writeString( this.name );
		dest.writeString( this.url );
		dest.writeString( this.released );
		dest.writeString( this.watched_url );
		dest.writeInt( this.downloads.size() );
		for( String d : this.downloads )
			dest.writeString( d );
	}

}
