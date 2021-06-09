package gr.aueb.tikatokaapp.Core;

import java.io.IOException;
import java.util.ArrayList;

public interface Node {

    void init() throws IOException;

    ArrayList<Address> getBrokers();

    void connect() throws IOException;

    void disconnect() throws IOException;


}
