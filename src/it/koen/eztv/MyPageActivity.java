package it.koen.eztv;

import it.koen.eztv.data.Episode;
import it.koen.eztv.data.Show;
import it.koen.eztv.net.BaseScraper.Callback;
import it.koen.eztv.net.MyShows;
import it.koen.eztv.net.WatchedTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyPageActivity extends Activity
{

	private ArrayList<Show> showlist;
	private LinearLayout list;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.mypage );

		
		this.list = (LinearLayout)findViewById( R.id.myshows );
		
		// easter egg #1:
		Calendar c = Calendar.getInstance();
		if( c.get(Calendar.MONTH) == Calendar.FEBRUARY && c.get(Calendar.DAY_OF_MONTH) == 21 )
			setTitle( "EZTV - Ellen Page" );

		if( savedInstanceState != null )
		{
			this.showlist = savedInstanceState.getParcelableArrayList( "showlist" );
			this.fill( this.showlist );
		}else		
		{
			this.fetch();
		}
	}

	@Override
	protected void onSaveInstanceState( Bundle state )
	{
		super.onSaveInstanceState( state );
		state.putParcelableArrayList( "showlist", this.showlist );
	}
	
	private void fetch()
	{
		MyShows s = new MyShows( this );
		s.setCallback( new Callback<Show>() {
			public void onResult( List<Show> result )
			{
				MyPageActivity.this.fill(result);
			}
		} );
		this.list.setVisibility( View.INVISIBLE );
		this.findViewById( R.id.loading ).setVisibility( View.VISIBLE );
		setProgressBarIndeterminateVisibility( true );
		s.execute( MyShows.URL );
	}

	protected void fill( List<Show> showlist )
	{
		setProgressBarIndeterminateVisibility( false );
		MyPageActivity.this.findViewById( R.id.loading ).setVisibility( View.GONE );
		this.list.removeAllViews();
		
		LayoutInflater inflater = LayoutInflater.from( this );
		
		this.showlist = new ArrayList<Show>();
		
		for( Show s : showlist )
		{
			this.showlist.add( s );
			View show = inflater.inflate( R.layout.show_item, null );
			TextView show_title = (TextView)show.findViewById( R.id.show_title );
			TextView show_status = (TextView)show.findViewById( R.id.show_status );
			final LinearLayout eplist = (LinearLayout)show.findViewById( R.id.show_episodes );
			
			show_title.setText( s.title );
			show_status.setText( s.status );
			
			for( final Episode e : s.getEpisodes() )
			{
				final View view = inflater.inflate( R.layout.episode_item, null );
				TextView episode = (TextView) view.findViewById( R.id.episode );
				TextView released = (TextView) view.findViewById( R.id.released );
		
				episode.setText( e.name );
				released.setText( e.released );
				view.setTag( e );

				view.findViewById( R.id.watched ).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick( View v )
					{
						new WatchedTask( MyPageActivity.this ).execute( e.watched_url );
						eplist.removeView( view );
					}
				});
				view.findViewById( R.id.download ).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick( View v )
					{
						ClipboardManager clipboard = (ClipboardManager)MyPageActivity.this.getSystemService( CLIPBOARD_SERVICE );
						clipboard.setText( e.getFirstDownloadUrl() );
						Toast.makeText( MyPageActivity.this, "url copied", Toast.LENGTH_SHORT ).show();
					}
				});
				
				eplist.addView( view );
			}
			
			show.setTag( s );
			this.list.addView( show );
		}
		
		this.list.setVisibility( View.VISIBLE );
	}
	
}
