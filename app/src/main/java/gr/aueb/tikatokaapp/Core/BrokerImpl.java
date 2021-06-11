package gr.aueb.tikatokaapp.Core;

import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class BrokerImpl implements Broker {

    private Address address;
    private BigInteger hashNum;
    private ServerSocket server;
    private String srcDir;
    private final Map<String, ArrayList<Address>> topicToPublishers = Collections.synchronizedMap(new HashMap<String, ArrayList<Address>>());                   //publishers per topic
    private final Map<String, Address> topicToBroker = Collections.synchronizedMap(new HashMap<String, Address>());                                  //key:topic , value:broker who manages this topic
    private final Map<String, ArrayList<Address>> topicToConsumers = Collections.synchronizedMap(new HashMap<String, ArrayList<Address>>());                    //subscribers per topic
    private final ArrayList<Address> brokers = new ArrayList<Address>();
    private final HashMap<BigInteger, Address> BROKERS_HASH_MAP = new HashMap<BigInteger, Address>();
    private final List<BigInteger> sortedHashValues = new ArrayList<BigInteger>();


    public static void main(String[] args) throws IOException {
        
        
        // User enters the IP of his/her machine, and a Port from terminal.
        String IP = args[0];
        int PORT = Integer.parseInt(args[1]);
        

		// We start the brokers.
        BrokerImpl broker = new BrokerImpl(IP, PORT);
        broker.init();
    }


    public BrokerImpl(String ip, int port) {
        this.address = new Address(ip, port);
        this.hashNum = getMd5(ip + port);
        this.srcDir = getProjectDir(System.getProperty("user.dir")) + File.separator + "src";
        saveBrokers();
        calculateKeys();
    }

    public String getProjectDir(String dir) {
        File f = new File(dir + File.separator + "src");
        if (!f.isDirectory())
            dir = dir.substring(0, dir.lastIndexOf(File.separator));
        return dir;
    }


    @Override
    public void init() {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Address> getBrokers() {
        return brokers;
    }

    public static BigInteger getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(messageDigest);
            return no;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void connect() throws IOException {
        server = new ServerSocket(address.getPort(), 20);
        System.out.println("[BROKER] >>> SERVER OPENED AT : " + address);
        Socket node;

        while (true) {
            node = server.accept();
            HandlePublisher hb = new HandlePublisher(node, address);
            hb.start();
            System.out.println("[BROKER] >>> A NEW NODE WAS CONNECTED: " + node.getRemoteSocketAddress());
        }
    }

    @Override
    public void disconnect()  {
        try {
            this.server.close();
            System.out.println("[BROKER] >>> Server closed!");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveBrokers() {
        try {
            Scanner scanner = new Scanner(new FileReader(srcDir + File.separator + "brokers.txt"));
            while (scanner.hasNextLine()) {
                String[] IP_PORT = scanner.nextLine().split(":");
                brokers.add(new Address(IP_PORT[0], Integer.parseInt(IP_PORT[1])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getResponsibleBroker(String topic, BigInteger hashValue) {
        for (BigInteger brokerHash : sortedHashValues) {
            int compareValue = hashValue.compareTo(brokerHash);                     // compare hashTopicValue with hashBrokersValues
            if (compareValue < 0)   {                                               // if hashTopic is greater than or equal with currentBroker's hashNum
                synchronized (topicToBroker) {                                     // continue to the next hashBroker value.
                    topicToBroker.put(topic, BROKERS_HASH_MAP.get(brokerHash));
                }
                return;
            }
        }
        synchronized (topicToBroker) {
            topicToBroker.put(topic, BROKERS_HASH_MAP.get(sortedHashValues.get(0)));
        }
    }

    public void storeTopic(String topic) {
        if (topicToBroker.get(topic) != null)         //if the topic is already managed by a broker
            return;
        BigInteger hashTopic = getMd5(topic);
        getResponsibleBroker(topic, hashTopic);
    }

    @Override
    public void calculateKeys() {
        for (Address address : brokers) {
            String IP = address.getIp();
            int PORT = address.getPort();
            BROKERS_HASH_MAP.put(getMd5(IP + PORT), address);
        }
        Set<BigInteger> keys = BROKERS_HASH_MAP.keySet();
        sortedHashValues.addAll(keys);
        Collections.sort(sortedHashValues);
    }

    @Override
    public void pull(ObjectInputStream inputStreamPub, ObjectOutputStream outputStreamPub, ObjectInputStream inputStreamCons, ObjectOutputStream outputStreamCons) throws IOException, ClassNotFoundException {
        System.out.println("[BROKER] >>> PULLING VIDEOS FROM PUBLISHERS");
        String str = (String) inputStreamPub.readObject();
        String[] dataRead = str.split(":");
        int chunksNumber = Integer.parseInt(dataRead[2]);
        outputStreamCons.writeObject(str);
        outputStreamCons.flush();
        while (chunksNumber != -1) {
            if (!((Message) inputStreamCons.readObject()).type.equals("SKIP")) {
                outputStreamPub.writeObject(new Message(address, "OK"));
                outputStreamCons.flush();
                for (int i = 0; i < chunksNumber; i++) {
                    Value currentChunk = (Value) inputStreamPub.readObject();
                    outputStreamCons.writeObject(currentChunk);
                    outputStreamCons.flush();
                }
            }else {
                outputStreamPub.writeObject(new Message(address, "SKIP"));
                outputStreamPub.flush();
            }
            str = (String) inputStreamPub.readObject();
            dataRead = str.split(":");
            chunksNumber = Integer.parseInt(dataRead[2]);
            outputStreamCons.writeObject(str);
            outputStreamCons.flush();
        }
    }


    public void transferVideos(ObjectInputStream inputStreamSub,ObjectOutputStream outputStreamSub,String requestedTopic) throws IOException, ClassNotFoundException {
        synchronized (topicToPublishers) {
            if( !topicToPublishers.containsKey(requestedTopic)){
                outputStreamSub.writeObject("END:END:-1");
                outputStreamSub.flush();
                return;
            }

            ArrayList<Address> pubs = topicToPublishers.get(requestedTopic);
            for (Address address : pubs) {
                outputStreamSub.writeObject("MORE_PUBS");
                outputStreamSub.flush();
                try {
                    Socket newSocket = new Socket(address.getIp(), address.getPort());
                    request(newSocket, requestedTopic);
                    ObjectInputStream inputStreamPub = new ObjectInputStream(newSocket.getInputStream());
                    ObjectOutputStream outputStreamPub = new ObjectOutputStream(newSocket.getOutputStream());
                    Message msg = (Message) inputStreamPub.readObject();
                    if (msg.type.equals("PUSH_VIDEOS"))
                        pull(inputStreamPub, outputStreamPub, inputStreamSub, outputStreamSub);

                    inputStreamPub.close();
                    outputStreamPub.close();
                    newSocket.close();
                }catch(ConnectException e){
                    outputStreamSub.writeObject("END:END:-1");
                    outputStreamSub.flush();
                    System.out.println("PUBLISHER WITH ADDRESS: " + address + " CANNOT BE FOUND");
                }
            }
            outputStreamSub.writeObject("DONE_PUBS");
            outputStreamSub.flush();
        }
    }




    public void request(Socket socket, String topic) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("[BROKER] >>> SEND REQUEST TO [PUBLISHER].");
            ArrayList<String> topicRequest = new ArrayList<>();
            topicRequest.add(topic);
            Message req = new Message(address, "TOPIC_REQUEST", topicRequest);
            out.writeObject(req);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteTopics(ArrayList<String> deletedTopics, Address pubAddress) {
        for (String topic : deletedTopics) {
            synchronized (topicToPublishers) {
                ArrayList<Address> pubs = topicToPublishers.get(topic);
                Iterator it1 = pubs.iterator();
                while (it1.hasNext()) {
                    Address pubAdd = (Address) it1.next();
                    if (pubAdd.equals(pubAddress)) {
                        it1.remove();
                        if (pubs.isEmpty()) {
                            topicToPublishers.remove(topic);
//                            synchronized (topicToBroker) {
//                                topicToBroker.remove(topic);
//                            }
                        }
                    }
                }
            }
        }
    }

    public void addTopics(ArrayList<String> addedTopics, Address pubAddress) {
        synchronized (topicToPublishers) {
            for (String topic : addedTopics) {

                ArrayList<Address> existing = topicToPublishers.get(topic);
                if (existing == null) {
                    existing = new ArrayList<Address>();
                    topicToPublishers.put(topic, existing);
                }
                if (!existing.contains(pubAddress))
                    existing.add(pubAddress);
            }
            System.out.println("[BROKER] >>> TOPICS UPDATED!");
            for (String topic : topicToPublishers.keySet()) {
                storeTopic(topic);
            }
        }
    }

    public void removeSub(String topic, Address address){
        synchronized (topicToConsumers) {
            ArrayList<Address> consumersList = topicToConsumers.get(topic);
            Iterator it1 = consumersList.iterator();

            while (it1.hasNext()) {
                Address consumerAdd = (Address) it1.next();
                if (consumerAdd.equals(address)) {
                    it1.remove();
                    if (consumersList.isEmpty()) {
                        topicToConsumers.remove(topic);
                    }
                }
            }
        }
    }


    public class HandlePublisher extends Thread {

        Socket connection;
        Address addr;

        public HandlePublisher(Socket connection, Address address) {
            this.connection = connection;
            this.addr = address;
        }


        public void run() {

            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try {

                inputStream = new ObjectInputStream(connection.getInputStream());
                outputStream = new ObjectOutputStream(connection.getOutputStream());
                Message message = (Message) inputStream.readObject();
                System.out.println("[BROKER] >>> CURRENT MESSAGE TYPE : " + message.type);

                if (message.type.equals("NEW_TOPICS")) {
                    addTopics(message.getUsedTopics(), message.sourceAddress);
                    System.out.println("[BROKER] >>> TOPICS ADDED");

                } else if (message.type.equals("DELETE_TOPICS")) {
                    System.out.println("[BROKER] >>> DELETING HASHTAG OF [APPNODE]");
                    deleteTopics(message.getUsedTopics(), message.sourceAddress);
                    System.out.println("[BROKER] >>> TOPICS DELETED");
                    System.out.println(topicToPublishers);

                } else if (message.type.equals("TOPIC_TO_BROKER_MAP")) {
                    System.out.println("[BROKER] >>> SENDS TOPIC TO BROKER MAP");
                    System.out.println(topicToBroker);
                    outputStream.writeObject(topicToBroker);
                    outputStream.flush();

                } else if (message.type.equals("LOAD_TOPIC_VIDEOS")) {
                    System.out.println("[BROKER] >>> SOMEONE REQUESTED VIDEOS!");
                    String topic = message.getUsedTopics().get(0);
                    synchronized (topicToConsumers) {
                        ArrayList<Address> existing = topicToConsumers.get(topic);
                        if (existing == null) {
                            existing = new ArrayList<Address>();
                            topicToConsumers.put(topic, existing);
                        }
                        if (!existing.contains(message.sourceAddress)) {
                            existing.add(message.sourceAddress);
                        }
                    }

                    System.out.println("[BROKER] >>> I WILL TRY TO FIND THE PUBLISHER OF " + topic);
                    transferVideos(inputStream , outputStream , topic);


                } else if (message.type.equals("UNSUBSCRIBE")) {
                    String topic = message.getUsedTopics().get(0);
                    Address subAddress = message.sourceAddress;
                    removeSub(topic, subAddress);
                }


            } catch (ClassNotFoundException |
                    IOException e) {

                e.printStackTrace();

            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                    if (connection != null) connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


}
