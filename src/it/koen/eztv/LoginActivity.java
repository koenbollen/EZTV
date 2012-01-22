package it.koen.eztv;

import it.koen.eztv.net.LoginTask;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.login );
		
		SharedPreferences ps = this.getSharedPreferences("it.koen.eztv", Context.MODE_PRIVATE);
		String username = ps.getString("username", null);
		
		if( !"".equals( username ) )
			((EditText)findViewById( R.id.username )).setText( username );
		
		((EditText)findViewById( R.id.password )).setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction( TextView v, int actionId, KeyEvent event )
			{
				LoginActivity.this.executeLogin();
				return true;
			}
		});

		findViewById( R.id.button_login ).setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v )
			{
				LoginActivity.this.executeLogin();
			}
		} );
	}


	protected void executeLogin()
	{
		Log.d("EZTV", "Trying to login..." );
		setProgressBarIndeterminateVisibility( true );
		LoginActivity.this.findViewById( R.id.error ).setVisibility( View.INVISIBLE );
		String username = ((EditText)LoginActivity.this.findViewById( R.id.username )).getText().toString();
		String password = ((EditText)LoginActivity.this.findViewById( R.id.password )).getText().toString();
		new LoginTask() {
			
			@Override
			public void onSuccess( String username, String password )
			{
				Log.i("EZTV", "Login "+username+" complete." );
				setProgressBarIndeterminateVisibility( false );
				Intent data = new Intent();
				data.putExtra( "username", username );
				data.putExtra( "password", password );
				LoginActivity.this.setResult( RESULT_OK, data );
				LoginActivity.this.finish();
			}
			
			@Override
			public void onFailed()
			{
				Log.w("EZTV", "Login failed." );
				((EditText)LoginActivity.this.findViewById( R.id.password )).setText( "" );
				LoginActivity.this.findViewById( R.id.error ).setVisibility( View.VISIBLE );
				setProgressBarIndeterminateVisibility( false );
			}
		}.execute( username, password );
	}

	public static String md5( String data )
	{
		try
		{
			byte[] bytes = data.getBytes();
			MessageDigest md;
			md = MessageDigest.getInstance( "MD5" );
			byte[] digest = md.digest( bytes );
			BigInteger hash = new BigInteger( 1, digest );
			return hash.toString(16);

		} catch( NoSuchAlgorithmException ball )
		{
			throw new RuntimeException( ball );
		}
	}

}
