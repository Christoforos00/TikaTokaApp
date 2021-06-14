package gr.aueb.tikatokaapp.Core;

import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;


public class ChannelName {

    String channelName, dir;

    ArrayList<String> hashtagsPublished;
    ArrayList<Value> videos;
    HashMap<String, ArrayList<Value>> topicToVideos;

    public ChannelName(String name) {
        channelName = name;
        videos = new ArrayList<Value>();
        topicToVideos = new HashMap<String, ArrayList<Value>>();
        hashtagsPublished = new ArrayList<String>();
    }

    public ArrayList<Value> getValues(String hashtag) {
        return topicToVideos.get(hashtag);
    }

    public ArrayList<String> updateChannel(String dir) {
        ArrayList<String> addedHashtags = new ArrayList<String>();
        MediaMetadataRetriever retriever;

        try {

            Scanner scanner = new Scanner(new FileReader(dir + File.separator + "topics.txt"));
            File f;

            while (scanner.hasNextLine()) {

                String[] parts = scanner.nextLine().split(":");
                f = new File(dir + File.separator + "videos" + File.separator + parts[0]);
                if (containsVideo(parts[0], channelName))
                    continue;

                retriever = new MediaMetadataRetriever();
                Log.wtf("PATH I READ", f.getAbsolutePath());
                retriever.setDataSource(f.getAbsolutePath());

                VideoFile videoFile = new VideoFile(f.getName(), channelName, String.valueOf(retriever.METADATA_KEY_DATE), String.valueOf(retriever.METADATA_KEY_DURATION)
                        , String.valueOf(retriever.METADATA_KEY_CAPTURE_FRAMERATE), String.valueOf(retriever.METADATA_KEY_IMAGE_HEIGHT), String.valueOf(retriever.METADATA_KEY_IMAGE_WIDTH), null);

                videos.add(new Value(videoFile));
                if (parts.length == 1) continue;            //if there are no topics for a video

                String[] hashtags = parts[1].split(",");
                for (String hashtag : hashtags) {
                    ArrayList<Value> existing = topicToVideos.get(hashtag);
                    if (existing == null) {
                        existing = new ArrayList<Value>();
                        topicToVideos.put(hashtag, existing);
                        addedHashtags.add(hashtag);
                    }
                    existing.add(videos.get(videos.size() - 1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addedHashtags;
    }


    public ArrayList<String> deleteVideo(String videoName) {
        ArrayList<String> deletedHashtags = new ArrayList<String>();

        Iterator it1 = videos.iterator();
        while (it1.hasNext()) {
            Value value = (Value) it1.next();
            if (value.videoFile.videoName.equals(videoName))
                it1.remove();
        }

        it1 = topicToVideos.entrySet().iterator();
        while (it1.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it1.next();
            ArrayList<Value> values = (ArrayList<Value>) pair.getValue();
            Iterator it2 = (values).iterator();
            while (it2.hasNext()) {
                Value value = (Value) it2.next();
                if (value.videoFile.videoName.equals(videoName))
                    it2.remove();
            }
            if (values.isEmpty()) {
                deletedHashtags.add((String) pair.getKey());
                it1.remove();
            }
        }
        return deletedHashtags;
    }

    public boolean containsVideo(String name, String channel) {
        return videos.contains(new Value(name, channel));
    }

    public ArrayList<String> getAllHashtags() {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(topicToVideos.keySet());
        return list;
    }

    public String getChannelName() {
        return channelName;
    }

    public String toString() {
        String str = "";
        for (Value value : videos)
            str += value.toString() + ", ";
        return str;
    }


}
