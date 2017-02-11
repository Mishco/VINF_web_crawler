package application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Lyrics implements Serializable {
	public String mTitle;
	public String mArtist;
	public String mOriginalTitle;
	public String mOriginalArtist;
	public String mSourceUrl;
	public String mCoverURL;
	public String mLyrics;
	public String mSource;
    public int mFlag; 
	// chybove hlasky
	public static final int NO_RESULT = -2; 
    public static final int NEGATIVE_RESULT = -1; 
    public static final int POSITIVE_RESULT = 1; 
    public static final int ERROR = -3; 
    public static final int SEARCH_ITEM = 2; 
	
    public Lyrics() {

	}

	
    public Lyrics(int flag) { 
        this.mFlag = flag; 
    } 
 
    public byte[] toBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.close();
		} finally {
			bos.close();
		}
		return bos.toByteArray();
	}

	public static Lyrics fromBytes(byte[] data) throws IOException, ClassNotFoundException {
		if (data == null)
			return null;
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return (Lyrics) is.readObject();
	}
	
	
	
}
