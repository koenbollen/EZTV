package it.koen.eztv;

import it.koen.eztv.data.Show;
import it.koen.eztv.net.BaseScraper.Callback;
import it.koen.eztv.net.ShowList;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class ShowListActivity extends Activity
{

	public static int i = 0;

	protected ListView list;
	protected ArrayAdapter<String> adapter;

	protected ArrayList<Show> showlist;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.showlist );

		if( savedInstanceState != null )
			this.showlist = savedInstanceState.getParcelableArrayList( "showlist" );

		this.list = (ListView) findViewById( R.id.showlist );
		this.list.setAdapter( this.adapter = new ArrayAdapter<String>( this, R.layout.showlist_item ) );
		this.list.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				Toast.makeText( getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT ).show();
			}
		} );

		if( this.showlist == null )
			fetchShowList();
		else
			fillList();
	}

	@Override
	protected void onSaveInstanceState( Bundle state )
	{
		super.onSaveInstanceState( state );
		state.putParcelableArrayList( "showlist", this.showlist );
	}

	private void fetchShowList()
	{
		ShowList s = new ShowList( this );
		s.setCallback( new Callback<Show>() {
			public void onResult( List<Show> result )
			{
				ShowListActivity.this.showlist = new ArrayList<Show>( result );
				ShowListActivity.this.fillList();
			}
		} );
		ShowListActivity.this.list.setVisibility( View.INVISIBLE );
		ShowListActivity.this.findViewById( R.id.showlist_status ).setVisibility( View.VISIBLE );
		setProgressBarIndeterminateVisibility( true );
		s.execute( ShowList.URL );
	}

	protected void fillList()
	{
		ShowListActivity.this.setProgressBarIndeterminateVisibility( false );
		ShowListActivity.this.findViewById( R.id.showlist_status ).setVisibility( View.GONE );

		ShowListActivity.this.adapter.clear();
		for( Show s : this.showlist )
			ShowListActivity.this.adapter.add( s.title );
		ShowListActivity.this.list.setVisibility( View.VISIBLE );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.showlist_menu, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
			case R.id.showlist_refresh:
				this.fetchShowList();
				return true;
			case R.id.showlist_sort:
				Toast.makeText( this, "not yet implemented", Toast.LENGTH_SHORT ).show();
				return true;
			default:
				return super.onOptionsItemSelected( item );
		}
	}
}
