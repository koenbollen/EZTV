package it.koen.eztv.net;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class WatchedTask extends AsyncTask<String, Void, Void>
{
	private static ThreadLocal<HttpClient> tlClient = new ThreadLocal<HttpClient>();

	protected Context context;
	protected SharedPreferences prefs;

	protected HttpClient client;

	public WatchedTask( Context context )
	{
		this.context = context;
		this.prefs = context.getSharedPreferences( "it.koen.eztv", Context.MODE_PRIVATE );
		if( WatchedTask.tlClient.get() == null )
		{
			HttpClient client = new DefaultHttpClient();
			WatchedTask.tlClient.set( client );
		}
		this.client = WatchedTask.tlClient.get();
	}

	@Override
	protected Void doInBackground( String... params )
	{
		String u = this.prefs.getString( "username", null );
		String p = this.prefs.getString( "password", null );

		for( String watched_url : params )
		{
			HttpGet get = new HttpGet( "https://eztv.it" + watched_url );
			get.setHeader( "Cookie", "username=" + u + "; password=" + p );

			try
			{
				HttpResponse res = this.client.execute( get );
				res.getEntity().consumeContent();
			} catch( ClientProtocolException ball )
			{
				ball.printStackTrace();
			} catch( IOException ball )
			{
				ball.printStackTrace();
			}
		}
		return null;
	}

}
