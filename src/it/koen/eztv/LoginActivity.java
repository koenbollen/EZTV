package it.koen.eztv;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.login );
		
		SharedPreferences ps = this.getSharedPreferences("it.koen.eztv", Context.MODE_PRIVATE);
		String username = ps.getString("username", null);
		
		if( !"".equals( username ) )
			((EditText)findViewById( R.id.username )).setText( username );
		
		((EditText)findViewById( R.id.password )).setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction( TextView v, int actionId, KeyEvent event )
			{
				Log.d( "EZTV", "password field 'done' pressed!" );
				LoginActivity.this.prepareResultAndFinish();
				return true;
			}
		});

		findViewById( R.id.button_login ).setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v )
			{
				LoginActivity.this.prepareResultAndFinish();
			}
		} );
		findViewById( R.id.button_cancel ).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v )
			{
				setResult( RESULT_CANCELED );
				LoginActivity.this.finish();
			}
		} );
	}


	protected void prepareResultAndFinish()
	{
		String username = ((EditText)LoginActivity.this.findViewById( R.id.username )).getText().toString();
		String password = ((EditText)LoginActivity.this.findViewById( R.id.password )).getText().toString();
		password = md5(password);
		Log.d( "EZTV", "new login pair: "+username );
		
		// TODO: Test credentials
		
		Intent data = new Intent();
		data.putExtra( "username", username );
		data.putExtra( "password", password );
		setResult( RESULT_OK, data );
		this.finish();
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
