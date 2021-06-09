package gr.aueb.tikatokaapp.Core;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    private static final long serialVersionUID = -7307678039687006933L;
    String type;
    Address sourceAddress;
    ArrayList<String> usedTopics = null;

    public Message(Address address, String type) {
        super();
        this.type = type;
        this.sourceAddress = address;
    }

    public Message(Address address, String type, ArrayList<String> topics) {
        super();
        this.type = type;
        this.sourceAddress = address;
        this.usedTopics = topics;

    }

    public ArrayList<String> getUsedTopics(){
        return usedTopics;
    }
}
