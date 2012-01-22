package it.koen.eztv.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Show implements Parcelable
{
    public static final Parcelable.Creator<Show> CREATOR
            = new Parcelable.Creator<Show>() {
        public Show createFromParcel(Parcel in) {
            return new Show(in);
        }

        public Show[] newArray(int size) {
            return new Show[size];
        }
    };
	
	public final String url;
	public final String title;
	public final String status;
	
	private List<Episode> episodes;
	
	public Show( String url, String title, String status )
	{
		this.url = url;
		this.title = title;
		this.status = status;
		
		this.episodes = new LinkedList<Episode>();
	}
	
	protected Show( Parcel in )
	{
		this.url = in.readString();
		this.title = in.readString();
		this.status = in.readString();


		int size = in.readInt();
		this.episodes = new LinkedList<Episode>();
		for( int i = 0; i < size; i++ )
			this.episodes.add( (Episode)in.readParcelable( Episode.class.getClassLoader() ) );
	}

	@Override
	public String toString() {
		return this.title + " (" + this.status + ")";
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel( Parcel dest, int flags )
	{
		dest.writeString( this.url );
		dest.writeString( this.title );
		dest.writeString( this.status );

		dest.writeInt( this.episodes.size() );
		for( Episode e : this.episodes )
			dest.writeParcelable( e, 0 );
	}

	public void addEpisode( Episode e )
	{
		this.episodes.add( e );
	}

	public List<Episode> getEpisodes()
	{
		return Collections.unmodifiableList( this.episodes );
	}
}
