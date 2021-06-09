package gr.aueb.tikatokaapp.Core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public interface Publisher extends Node {
    
    void addTopics(String hashtag) throws IOException;

    void removeTopics(String hashtag) throws IOException;

    void push(Value video, ObjectInputStream in, ObjectOutputStream out) ;

    void notifyEveryBroker(boolean deletion, ArrayList<String> hashtags);

    ArrayList<Value> generateChunks(Value video) throws IOException;

}
