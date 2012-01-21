package it.koen.eztv.data;

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
	
	public Show( String url, String title, String status )
	{
		this.url = url;
		this.title = title;
		this.status = status;
	}
	
	protected Show( Parcel in )
	{
		this.url = in.readString();
		this.title = in.readString();
		this.status = in.readString();
	}

	@Override
	public String toString() {
		return "Show [url=" + this.url + ", title=" + this.title + ", status="
				+ this.status + "]";
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
	}
}
