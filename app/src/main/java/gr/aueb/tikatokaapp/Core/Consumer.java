package gr.aueb.tikatokaapp.Core;

public interface Consumer extends Node {

    void register(String topic);

    void disconnect(String topic);

    void playData(String topic);

}
