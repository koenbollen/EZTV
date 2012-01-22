package it.koen.eztv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class EZTVActivity extends Activity {

	protected boolean signedin = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		SharedPreferences ps = this.getSharedPreferences("it.koen.eztv", Context.MODE_PRIVATE);
		String username = ps.getString("username", null);
		String password = ps.getString("password", null);
		
		if( username != null && password != null )
		{
			this.signedin = true;
		}

        findViewById(R.id.button_showlist).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(EZTVActivity.this, ShowListActivity.class));
			}
		});
        findViewById(R.id.button_mypage).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( EZTVActivity.this.signedin )
					startActivity(new Intent(EZTVActivity.this, MyPageActivity.class));
			}
		});
    }

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.main_menu, menu );
		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu( Menu menu )
	{
		if( this.signedin )
			menu.findItem( R.id.sign ).setTitle( R.string.signout );
		else
			menu.findItem( R.id.sign ).setTitle( R.string.signin );
		return super.onPrepareOptionsMenu( menu );
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
			case R.id.about:
				Toast.makeText( this, "not yet implemented", Toast.LENGTH_SHORT ).show();
				return true;
			case R.id.sign:
				if( this.signedin )
				{
					SharedPreferences ps = this.getSharedPreferences("it.koen.eztv", Context.MODE_PRIVATE);
					Editor edit = ps.edit();
					edit.putString( "password", null );
					edit.commit();
					this.signedin = false;
				}
				else
				{
					startActivityForResult(new Intent(this, LoginActivity.class), 0xe21);
				}
				return true;
			default:
				return super.onOptionsItemSelected( item );
		}
	}
	
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		if( 0xe21 != requestCode || resultCode != Activity.RESULT_OK || data == null )
			return;
		SharedPreferences ps = this.getSharedPreferences("it.koen.eztv", Context.MODE_PRIVATE);
		Editor edit = ps.edit();
		edit.putString( "username", data.getStringExtra( "username" ) );
		edit.putString( "password", data.getStringExtra( "password" ) );
		edit.commit();
		this.signedin = true;
	}
}