package it.koen.eztv.net;

import java.io.IOException;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public abstract class BaseScraper<T> extends AsyncTask<String, Void, List<T>> {

	public interface Callback<T>
	{
		public void onResult( List<T> result );
	}
	
	protected final Context context;
	private Callback<T> callback;
	
	public BaseScraper( Context context )
	{
		this.context = context;
	}
	
	public void setCallback( Callback<T> callback )
	{
		this.callback = callback;
	}
	
	protected abstract List<T> process( Document doc );
	
	@Override
	protected List<T> doInBackground(String... urls) {
		
		SharedPreferences ps = this.context.getSharedPreferences("it.koen.eztv", Context.MODE_PRIVATE);
		String username = ps.getString("username", null);
		String password = ps.getString("password", null);
		
		for( String url : urls )
		{

			Log.d( "EZTV", "Downloading " + url );
			Connection con = Jsoup.connect( url );
			
			if( username != null && password != null )
				con.cookie("username", username).cookie("password", password);
			
			Document doc = null;
			try {
				doc = con.get();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			if( doc == null )
				continue;
			
			return this.process( doc );
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(List<T> result) {
		if( result != null && this.callback != null )
			this.callback.onResult(result);
	}

}