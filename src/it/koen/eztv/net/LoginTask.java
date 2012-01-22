package it.koen.eztv.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public abstract class LoginTask extends AsyncTask<String, Void, Intent>
{

	@Override
	protected Intent doInBackground( String... params )
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost( "https://eztv.it/login/" );
		
		try {
			List<NameValuePair> data = new ArrayList<NameValuePair>(2);
			data.add(new BasicNameValuePair("loginname", params[0]));
			data.add(new BasicNameValuePair("password", params[1]));
			data.add(new BasicNameValuePair("submit", "Login"));
			post.setEntity(new UrlEncodedFormEntity(data));

			HttpResponse res = client.execute(post);
			res.getEntity().consumeContent();
			if( res.getStatusLine().getStatusCode() != 200 || !res.containsHeader( "Set-Cookie" ) )
			{
				Log.w( "EZTV", "failed to login, !200 or no cookie" );
				return null;
			}
			
			String username = null;
			String password = null;
			for( Header h : res.getHeaders( "Set-Cookie" ) )
			{
				String cookie = h.getValue();
				if( cookie.contains( ";" ) )
					cookie = cookie.split( ";" )[0];
				String[] split = cookie.split("=");

				if( "username".equals( split[0] ) )
					username = split[1];
				if( "password".equals( split[0] ) )
					password = split[1];
			}
			
			if( username != null && password != null )
			{
				Intent result = new Intent();
				result.putExtra( "username", username );
				result.putExtra( "password", password );
				return result;
			}
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	protected void onCancelled()
	{
		this.onFailed();
	}
	
	@Override
	protected void onPostExecute( Intent result )
	{
		if( result == null )
		{
			this.onFailed();
			return;
		}
		this.onSuccess( result.getStringExtra( "username" ), result.getStringExtra( "password" ) ); 
	}

	public abstract void onSuccess( String username, String password );

	public abstract void onFailed();

}
