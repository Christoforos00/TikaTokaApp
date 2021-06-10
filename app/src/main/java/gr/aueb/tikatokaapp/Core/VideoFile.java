package gr.aueb.tikatokaapp.Core;

import java.io.Serializable;
import java.util.ArrayList;

public class VideoFile implements Serializable {

	String videoName, channelName , dateCreated , length , framerate , frameWidth, frameHeight;
	ArrayList<String> associatedHashtags;
	byte[] videoFileChunk;


	public VideoFile(String nameVid, String channel, String date, String length, String framerate, String framewidth, String frameheight, byte[] chunk){
		super();
		this.videoName = nameVid;
		this.channelName = channel;
		this.dateCreated = date;
		this.length = length;
		this.framerate = framerate;
		this.frameWidth = framewidth;
		this.frameHeight = frameheight;
		this.videoFileChunk = chunk;
	}
	public VideoFile( VideoFile other , byte[] chunk){
		super();
		this.videoName = other.videoName;
		this.channelName = other.channelName;
		this.dateCreated = other.dateCreated;
		this.length = other.length;
		this.framerate = other.framerate;
		this.frameWidth = other.frameWidth;
		this.frameHeight = other.frameHeight;
		this.videoFileChunk = chunk;
	}

	public VideoFile(String name,String channelName){
		super();
		this.videoName = name;
		this.channelName = channelName;
	}

	public void setAssociatedHashtags(String[] names){
		for(String hashtag : names)
			this.associatedHashtags.add(hashtag);
	}


	public String getVideoName() {
		return videoName;
	}

	public String getChannelName() {
		return channelName;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public String getLength() {
		return length;
	}

	public String getFramerate() {
		return framerate;
	}

	public String getFrameWidth() {
		return frameWidth;
	}

	public String getFrameHeight() {
		return frameHeight;
	}

	public ArrayList<String> getAssociatedHashtags() {
		return associatedHashtags;
	}

	public byte[] getVideoFileChunk() {
		return videoFileChunk;
	}


	
	public String toString() {
		return videoName + " " + channelName;
	}
	
	
}
