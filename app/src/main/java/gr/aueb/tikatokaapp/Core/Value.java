package gr.aueb.tikatokaapp.Core;

import java.io.Serializable;

public class Value implements Serializable {

    private static final long serialVersionUID = -2845096289187611411L;
    public VideoFile videoFile;


    public Value(VideoFile file) {
        super();
        videoFile = file;
    }

    public Value(Value old, byte[] FileChunk) {
        super();
        videoFile = new VideoFile(old.videoFile, FileChunk);
    }

    public Value(String name, String channelName) {
        super();
        videoFile = new VideoFile(name, channelName);
    }

    public String getName() {
        return videoFile.videoName;
    }

    public String getChannelName() {
        return videoFile.channelName;
    }

    public VideoFile getVideoFile() {
        return videoFile;
    }

    public String toString() {
        return videoFile.toString();
    }

    @Override
    public boolean equals(Object object) {
        boolean same = false;
        if (object != null && object instanceof Value) {
            same = getName().equals(((Value) object).getName())
                    && getChannelName().equals(((Value) object).getChannelName());
        }
        return same;
    }


}
