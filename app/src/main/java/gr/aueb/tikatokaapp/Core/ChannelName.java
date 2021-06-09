package gr.aueb.tikatokaapp.Core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp4.MP4Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

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
        try {

            Scanner scanner = new Scanner(new FileReader(dir + File.separator + "topics.txt"));
            String line;
            File f;

            while (scanner.hasNextLine()) {

                String[] parts = scanner.nextLine().split(":");
                f = new File(dir + File.separator + "videos" + File.separator + parts[0]);
                if ( containsVideo( parts[0],channelName) )
                    continue;

                BodyContentHandler handler = new BodyContentHandler();
                Metadata metadata = new Metadata();
                FileInputStream inputstream = new FileInputStream(f);
                ParseContext pcontext = new ParseContext();
                MP4Parser MP4Parser = new MP4Parser();
                MP4Parser.parse(inputstream, handler, metadata, pcontext);

                VideoFile videoFile = new VideoFile(f.getName(), channelName, metadata.get("date"), metadata.get("xmpDM:duration")
                        , metadata.get("framerate"), metadata.get("tiff:ImageLength"), metadata.get("tiff:ImageWidth"), null);

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
        } catch (IOException | SAXException | TikaException e) {
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

    public boolean containsVideo(String name , String channel){
        return videos.contains(new Value(name,channel));
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
