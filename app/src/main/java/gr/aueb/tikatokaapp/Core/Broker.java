package gr.aueb.tikatokaapp.Core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Broker extends Node {

    void calculateKeys();

    void pull(ObjectInputStream inputStreamPub, ObjectOutputStream outputStreamPub, ObjectInputStream inputStreamCons, ObjectOutputStream outputStreamCons) throws IOException, ClassNotFoundException;

}
