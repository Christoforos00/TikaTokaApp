package gr.aueb.tikatokaapp.Core;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AppNode implements Publisher, Consumer {
    private String srcDir, inDir, outDir;
    private Address address;
    private ServerSocket server;
    private ChannelName channelname;
    private boolean RUNNING = true;
    private Map<String, Address> topicToBroker = new HashMap<String, Address>();
    private final ArrayList<Address> brokers = new ArrayList<Address>();
    private final List<String> subscribedTopics = Collections.synchronizedList(new ArrayList<String>());

    public static void main(String args[]) throws InterruptedException, UnknownHostException {

        // User enters the IP of his/her machine, and a Port from terminal and the name of his/her channel.
        String IP = args[0];
        int PORT = Integer.parseInt(args[1]);
        String channelName = args[2];

        // We start the appNode.
        AppNode node = new AppNode(IP, PORT, channelName);
        node.init();
    }

    public AppNode(String ip, int port, String channel, InputStream brokersIn, String videoDir) throws InterruptedException {
        this.address = new Address(ip, port);
        String projectDir = videoDir;

        this.outDir = projectDir + File.separator + channel + "Files" + File.separator + "publishedVideos";
        this.inDir = projectDir + File.separator + channel + "Files" + File.separator + "consumedVideos";
        this.channelname = new ChannelName(channel);
        loadFolders();
        loadBrokersFile(brokersIn);
        HandleTopicUpdates updatesHandler = new HandleTopicUpdates();
        updatesHandler.start();
        loadPublisherVideos();
        loadSubscriptions();
        notifyEveryBroker(false, getPublishedTopics());
        TimeUnit.SECONDS.sleep(1);
    }

    public String getPubDir() {
        return outDir;
    }

    public String getSubDir(){ return inDir;}

    public AppNode(String ip, int port, String channel) throws InterruptedException {
        this.address = new Address(ip, port);
        String projectDir = getProjectDir(System.getProperty("user.dir"));
        this.srcDir = projectDir + File.separator + "src";
        this.outDir = projectDir + File.separator + channel + "Files" + File.separator + "publishedVideos";
        this.inDir = projectDir + File.separator + channel + "Files" + File.separator + "consumedVideos";
        this.channelname = new ChannelName(channel);
        loadFolders();
        loadBrokersFile();
        HandleTopicUpdates updatesHandler = new HandleTopicUpdates();
        updatesHandler.start();
        loadPublisherVideos();
        loadSubscriptions();
        notifyEveryBroker(false, getPublishedTopics());
        TimeUnit.SECONDS.sleep(1);
    }

    public String getName(){
        return channelname.getChannelName();
    }

    public String getProjectDir(String dir) {
        File f = new File(dir + File.separator + "src");
        if (!f.isDirectory())
            dir = dir.substring(0, dir.lastIndexOf(File.separator));
        return dir;
    }

    public void loadFolders() {
        File f = new File(inDir + File.separator + "videos");
        if (!f.exists())
            f.mkdirs();

        f = new File(outDir + File.separator + "videos");
        if (!f.exists())
            f.mkdirs();

        try {
//            f = new File(srcDir + File.separator + "brokers.txt");
//            f.createNewFile();
            f = new File(outDir + File.separator + "topics.txt");
            f.createNewFile();
            f = new File(inDir + File.separator + "topics.txt");
            f.createNewFile();
            f = new File(inDir + File.separator + "subscriptions.txt");
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> loadPublisherVideos() {
        return channelname.updateChannel(outDir);
    }

    public ArrayList<String> getPublishedTopics() {
        return channelname.getAllHashtags();
    }

    public ArrayList<String> getSubscribedTopics() {
        return new ArrayList<>(subscribedTopics);
    }

    public ArrayList<String> findAllTopics(){
        topicToBroker = requestTopicToBrokerMap(brokers.get(0));
        return new ArrayList<String>(topicToBroker.keySet());
    }

    public ArrayList<Value> getVideos(String hashtag) {
        return channelname.getValues(hashtag);
    }

    public void loadSubscriptions() {
        try {
            Scanner scanner = new Scanner(new FileReader(inDir + File.separator + "subscriptions.txt"));
            while (scanner.hasNextLine())
                synchronized (subscribedTopics) {
                    subscribedTopics.add(scanner.nextLine().trim());
                }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void init() {
        HandleMenu action = new HandleMenu();
        action.start();
        connect();
    }

    @Override
    public ArrayList<Address> getBrokers() {
        return brokers;
    }


    @Override
    public void connect() {
        try {
            server = new ServerSocket(address.getPort(), 10);
            System.out.println("[APPNODE] >>> SERVER OPENED AT : " + address);
            Socket broker;

            while (RUNNING) {
                broker = server.accept();
                HandleBroker handleBroker = new HandleBroker(broker);
                handleBroker.start();
            }
            notifyEveryBroker(true, getPublishedTopics());
            server.close();
            System.exit(0);

        } catch (SocketException e) {
            System.out.println("Server stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void disconnect() {
        RUNNING = false;
    }


    ///////////////////////////////////////////////     PUBLISHER       ///////////////////////////////////////////////////////////////////


    public void addTopics(String videoFileName) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.print("ENTER THE HASHTAGS OF YOUR VIDEO, SEPARATED BY COMMAS : ");
        String s = scan.next();
        String[] topics = s.split(",");
        appendInPubTopicsFile(videoFileName, topics);
        ArrayList<String> addedHashtags = loadPublisherVideos();
        notifyEveryBroker(false, addedHashtags);
    }

    public void addTopics(String videoFileName, String s) throws IOException {
        String[] topics = s.split(",");
        appendInPubTopicsFile(videoFileName, topics);
        ArrayList<String> addedHashtags = loadPublisherVideos();
        notifyEveryBroker(false, addedHashtags);
    }

    @Override
    public void removeTopics(String videoFileName) throws IOException {
        removeFromPubTopicsFile(videoFileName);
        ArrayList<String> deletetedHashtags = channelname.deleteVideo(videoFileName);
        notifyEveryBroker(true, deletetedHashtags);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteVideo(String videoFileName) {
        String PATH = outDir + File.separator + "videos" + File.separator + videoFileName;
        try {
            if (Files.deleteIfExists(Paths.get(PATH))) {
                removeTopics(videoFileName);
                System.out.println("[SYSTEM] >>> " + videoFileName + " is deleted!");
            } else {
                System.out.println("[SYSTEM] >>> This file can't be found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadVideo(String videoFileName) {
        String PATH = outDir + File.separator + "videos" + File.separator + videoFileName;
        try {
            boolean result = Files.exists(Paths.get(PATH));
            if (result) {
                Scanner scanner = new Scanner(new FileReader(outDir + File.separator + "topics.txt"));
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(":");
                    if (parts[0].equals(videoFileName)) {
                        System.out.println("[SYSTEM] >>> YOU HAVE ALREADY UPLOADED THIS VIDEO.");
                        return;
                    }
                }
                addTopics(videoFileName);
            } else {
                System.out.println("[SYSTEM] >>> Sorry, I didn't find any video file with name: " + videoFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void uploadVideo(String videoFileName, String hashtags) {
        String PATH = outDir + File.separator + "videos" + File.separator + videoFileName;
        try {
//            boolean result = Files.exists(Paths.get(PATH));
            boolean result = true;
            if (result) {
                Scanner scanner = new Scanner(new FileReader(outDir + File.separator + "topics.txt"));
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(":");
                    if (parts[0].equals(videoFileName)) {
                        System.out.println("[SYSTEM] >>> YOU HAVE ALREADY UPLOADED THIS VIDEO.");
                        return;
                    }
                }
                addTopics(videoFileName, hashtags);
            } else {
                System.out.println("[SYSTEM] >>> Sorry, I didn't find any video file with name: " + videoFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void appendInPubTopicsFile(String videoFileName, String[] topics) throws IOException {
//        FileWriter fw = new FileWriter(outDir + File.separator + "topics.txt", true); //the true will append the new data
//        byte[] bytes = Files.readAllBytes(Paths.get(outDir + File.separator + "topics.txt"));
//        String text = videoFileName + ":";
//        for (String topic : topics)
//            text += topic + ",";
//        text += channelname.getChannelName();
//        if (bytes.length == 0 || bytes[bytes.length - 1] == '\n')
//            fw.write(text);
//        else
//            fw.write("\n" + text);
//        fw.close();
//    }


    public void appendInPubTopicsFile(String videoFileName, String[] topics) throws IOException {
        FileWriter fw = new FileWriter(outDir + File.separator + "topics.txt", true); //the true will append the new data

        File file = new File(outDir + File.separator + "topics.txt");
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        String text = videoFileName + ":";
        for (String topic : topics)
            text += topic + ",";
        text += channelname.getChannelName();
        if (bytes.length == 0 || bytes[bytes.length - 1] == '\n')
            fw.write(text);
        else
            fw.write("\n" + text);
        fw.close();
    }



    public void removeFromPubTopicsFile(String videoFileName) throws IOException {
        File inputFile = new File(outDir + File.separator + "topics.txt");
        File tempFile = new File(outDir + File.separator + "topics2.txt");
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            String[] parts = currentLine.split(":");
            if (parts[0].equals(videoFileName)) continue;
            writer.write(currentLine + "\n");
        }
        writer.close();
        reader.close();
        tempFile.renameTo(inputFile);
    }

    public void loadBrokersFile() {
        try {
            Scanner scanner = new Scanner(new FileReader(srcDir + File.separator + "brokers.txt"));
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(":");
                Address readAddress = new Address(parts[0], Integer.parseInt(parts[1]));
                brokers.add(readAddress);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadBrokersFile(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split(":");
            Address readAddress = new Address(parts[0], Integer.parseInt(parts[1]));
            brokers.add(readAddress);
        }
    }


    @Override
    public void notifyEveryBroker(boolean deletion, ArrayList<String> topics) {
        if (topics.isEmpty())       //there aren't any videos that can be published
            return;
        for (Address brokerAddress : brokers)
            notifyBroker(brokerAddress, deletion, topics);
    }

    /*
    Notify a Broker about the current Hashtags in the channel
     */
    public void notifyBroker(Address brokerAddress, boolean deletion, ArrayList<String> hashtags) {
        System.out.println("[SYSTEM] >>> Publisher [" + address + "] notifies Broker [" + brokerAddress + "]");
        String text = "NEW_TOPICS";
        if (deletion)
            text = "DELETE_TOPICS";
        try {
            Socket socket = new Socket(brokerAddress.getIp(), brokerAddress.getPort());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(new Message(address, text, hashtags));
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Push every video relevant to a topic
     */
    public void pushTopic(String topic, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        System.out.println("[PUBLISHER] >>> PUSH TO BROKER: STARTED!");
        System.out.println("[PUBLISHER] >>> pushing videos with topic: " + topic);
        ArrayList<Value> videos = getVideos(topic);
        for (Value video : videos)                                  //send every relevant video
            push(video, inputStream, outputStream);
        try {
            outputStream.writeObject("END:END:-1");
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void push(Value video, ObjectInputStream in, ObjectOutputStream out) {
        try {
            ArrayList<Value> chunks = generateChunks(video);
            String videoData = video.getName() + ":" + video.getChannelName() + ":" + chunks.size();            // dog_video.mp4:GiorgosChannel:10
            out.writeObject(videoData);
            out.flush();
            if (((Message) in.readObject()).type.equals("SKIP"))
                return;
            for (Value chunk : chunks) {
                out.writeObject(chunk);
                out.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Value> generateChunks(Value video) throws IOException {
        ArrayList<Value> chunks = new ArrayList<Value>();
        int chunkSize = 500000;                     //0.5 mb
        try {
            File file = new File(outDir + "/videos/" + video.videoFile.videoName);
            int chunkNumber = ((int) file.length()) / chunkSize;
            FileInputStream inputStream = new FileInputStream(file);
            byte[] wholeVideo = new byte[(int) file.length()];
            inputStream.read(wholeVideo);

            for (int i = 0; i < chunkNumber; i++) {
                byte[] currentChunk = new byte[chunkSize];
                for (int j = 0; j < chunkSize; j++) {
                    currentChunk[j] = wholeVideo[j + i * chunkSize];
                }
                chunks.add(new Value(video, currentChunk));
            }

            int remaining = ((int) file.length()) % chunkSize;

            if (remaining > 0) {
                int start = chunkNumber * chunkSize;

                byte[] currentChunk = new byte[remaining];
                for (int i = 0; i < remaining; ++i) {
                    currentChunk[i] = wholeVideo[start + i];
                }
                chunks.add(new Value(video, currentChunk));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return chunks;
    }

    //////////////////////////////////////////  CONSUMER   ////////////////////////////////////////////////////


    public Address getResponsibleBroker(String topic) {
        if (!topicToBroker.containsKey(topic)) {
            System.out.println("[SYSTEM] >>>TOPIC INITIALLY NOT FOUND..REQUESTING TOPIC MAP!");   //update topicToBroker
            topicToBroker = requestTopicToBrokerMap(brokers.get(0));
        }
        return topicToBroker.get(topic);
    }

    @Override
    public void register(String topic) {
        System.out.println("REGISTER!!!!");
        if (subscribedTopics.contains(topic)) {
            System.out.println("ALREADY SUBSCRIBED TO: " + topic);
            return;
        }

        if (getResponsibleBroker(topic) != null) {
            playData(topic);
            System.out.println("PLAYDATA!!!!");
            if (!subscribedTopics.contains(topic)) {
                synchronized (subscribedTopics) {
                    subscribedTopics.add(topic);
                }
                appendSubscriptionsFile(topic);
            }
        } else {
            System.out.println("CANNOT FIND TOPIC: " + topic);
        }
    }

    public void registerAll(Set<String> topics){
        for (String topic : topics)
            register(topic);
    }



    public void appendSubTopicsFile(String name, String channel) {
        try {
            String filename = inDir + File.separator + "topics.txt";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(name + ":" + channel + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendSubscriptionsFile(String topic) {
        try {
            String filename = inDir + File.separator + "subscriptions.txt";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(topic + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeFromSubscriptionsFile(String topic) throws IOException {
        Scanner scanner = new Scanner(new FileReader(inDir + File.separator + "subscriptions.txt"));
        File inputFile = new File(inDir + File.separator + "subscriptions.txt");
        File tempFile = new File(inDir + File.separator + "subscriptions2.txt");
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            if (currentLine.equals(topic)) continue;
            writer.write(currentLine + "\n");
        }
        writer.close();
        reader.close();
        tempFile.renameTo(inputFile);

        File leftOvers = new File(inDir + File.separator + "subscriptions2.txt");
        leftOvers.delete();
    }


    public boolean alreadyHasVideo(String fileName) {
        boolean hasVideo = false;
        File receivedVideos = new File(inDir + File.separator + "topics.txt");
        String[] validate = fileName.split(":");

        // check if current video is from themselves.
        if (validate[1].equals(this.channelname.getChannelName()))
            return true;
        try {
            Scanner scan = new Scanner(new FileReader(receivedVideos));
            while (scan.hasNextLine()) {
                String[] parts = scan.nextLine().split(":");
                if (validate[0].equals(parts[0]) && validate[1].equals(parts[1]))
                    hasVideo = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return hasVideo;
    }

    public Map<String, Address> requestTopicToBrokerMap(Address brokerAddress) {
        Map<String, Address> map = null;
        try {
            Socket socket = new Socket(brokerAddress.getIp(), brokerAddress.getPort());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(new Message(address, "TOPIC_TO_BROKER_MAP"));
            outputStream.flush();
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            map = (Map<String, Address>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }


    @Override
    public void disconnect(String topic) {
        if (!subscribedTopics.contains(topic)) {     // return if not subscribed!
            System.out.println("YOU ARE NOT SUBSCRIBED TO: " + topic);
            return;
        }
        synchronized (subscribedTopics) {
            subscribedTopics.remove(topic);
        }
        Address brokerAddress = getResponsibleBroker(topic);
        if (brokerAddress == null) {
            System.out.println("CANNOT UNSUBRCRIBE FROM: " + topic);
            return;
        }

        Socket socket = null;
        ObjectOutputStream out = null;
        try {
            socket = new Socket(brokerAddress.getIp(), brokerAddress.getPort());
            out = new ObjectOutputStream(socket.getOutputStream());
            ArrayList<String> hashtag = new ArrayList<>();
            hashtag.add(topic);
            Message message = new Message(address, "UNSUBSCRIBE", hashtag);
            out.writeObject(message);
            out.flush();
            removeFromSubscriptionsFile(topic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void disconnectAll(Set<String> topics){
        for (String topic : topics)
            disconnect(topic);
    }


    public void downloadVideos(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException {
        String str = (String) inputStream.readObject();     // read videoName:chunksNumber
        String[] dataRead = str.split(":");
        int chunksNumber = Integer.parseInt(dataRead[2]);
        OutputStream outStream = null;
        while (chunksNumber != -1) {
            System.out.println("[SYSTEM] >>> " + dataRead[0] + " COMING FROM " + dataRead[1]);
            Boolean firstChunk = true;
            if (alreadyHasVideo(dataRead[0] + ":" + dataRead[1])) {         // check videoName:ChannelName
                outputStream.writeObject(new Message(address, "SKIP"));
                outputStream.flush();
                System.out.println("[SYSTEM] >>> ALREADY FOUND " + dataRead[0] + ":" + dataRead[1]);
            } else {
                outputStream.writeObject(new Message(address, "OK"));
                outputStream.flush();
                appendSubTopicsFile(dataRead[0], dataRead[1]);
                for (int current = 0; current < chunksNumber; current++) {
                    Value receivedChunk = (Value) inputStream.readObject();
                    if (firstChunk) {
                        firstChunk = false;
                        outStream = new FileOutputStream(inDir + File.separator + "videos" + File.separator + receivedChunk.getName());
                    }
                    outStream.write(receivedChunk.videoFile.videoFileChunk);
                }
                outStream.close();
            }
            str = (String) inputStream.readObject();     // read videoName:chunksNumber
            dataRead = str.split(":");
            chunksNumber = Integer.parseInt(dataRead[2]);
        }
    }


    @Override
    public void playData(String topic) {
        Address brokerAddress = getResponsibleBroker(topic);
        if (brokerAddress == null) {
            System.out.println("CANNOT ACCESS: " + topic);
            return;
        }
        Socket socket = null;
        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;
        try {
            socket = new Socket(brokerAddress.getIp(), brokerAddress.getPort());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<String> topics = new ArrayList<String>();
            topics.add(topic);
            System.out.println("[CONSUMER] >>> SENDING UPDATE MESSAGE TO BROKER!");
            outputStream.writeObject(new Message(address, "LOAD_TOPIC_VIDEOS", topics));
            outputStream.flush();

            String str = (String) inputStream.readObject();
            while (str.equals("MORE_PUBS")) {
                downloadVideos(inputStream, outputStream);
                str = (String) inputStream.readObject();
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /////////////////////////////////////////////////   HANDLERS    //////////////////////////////////////////////////////


    public class HandleBroker extends Thread {
        Socket connection;

        public HandleBroker(Socket connection) {
            this.connection = connection;
        }

        public void run() {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(connection.getOutputStream());
                Message message = (Message) inputStream.readObject();
                System.out.println("[APPNODE] >>>  RECEIVED MESSAGE WITH TYPE: " + message.type);

                if (message.type.equals("TOPIC_REQUEST")) {
                    Message msg = new Message(address, "PUSH_VIDEOS");
                    outputStream.writeObject(msg);
                    outputStream.flush();
                    inputStream = new ObjectInputStream(connection.getInputStream());
                    pushTopic(message.getUsedTopics().get(0), inputStream, outputStream);
                }

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class HandleMenu extends Thread {

        public HandleMenu() {
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {
            Scanner scan = new Scanner(System.in);
            String s;

            while (RUNNING) {
                System.out.println("MENU\n1| Upload new Video\n2| Delete a Video\n3| Subscribe to topic\n4| Unsubscribe from topic\n5| Display your subscriptions\n6| Display your published videos\n0| Exit");
                System.out.print("INPUT:");
                s = scan.next();
                try {
                    if (Integer.parseInt(s) == 1) {
                        System.out.print("ENTER THE NAME OF THE VIDEO:");
                        s = scan.next();
                        uploadVideo(s);
                    } else if (Integer.parseInt(s) == 2) {
                        System.out.print("ENTER THE NAME OF THE VIDEO:");
                        s = scan.next();
                        deleteVideo(s);
                    } else if (Integer.parseInt(s) == 3) {
                        System.out.print("ENTER THE TOPIC YOU WANT TO SUBSCRIBE TO :");
                        s = scan.next();
                        while ((s.charAt(0) == '#' && s.length() < 3) || (s.length() < 2)) {
                            System.out.print("WRONG INPUT,TRY AGAIN" + "\n");
                            System.out.print("ENTER THE TOPIC YOU WANT TO SUBSCRIBE TO :");
                            s = scan.next();
                        }
                        register(s);
                    } else if (Integer.parseInt(s) == 4) {
                        System.out.print("ENTER THE TOPIC YOU WANT TO UNSUBSCRIBE FROM :");
                        s = scan.next();
                        while ((s.charAt(0) == '#' && s.length() < 3) || (s.length() < 2)) {
                            System.out.print("WRONG INPUT,TRY AGAIN" + "\n");
                            System.out.print("ENTER THE TOPIC YOU WANT TO UNSUBSCRIBE FROM :");
                            s = scan.next();
                        }
                        disconnect(s);
                    } else if (Integer.parseInt(s) == 5) {
                        System.out.println("SUBSCRIBED TOPICS: " + subscribedTopics);
                    } else if (Integer.parseInt(s) == 6) {
                        System.out.println("PUBLISHED VIDEOS: " + channelname);
                    } else if (Integer.parseInt(s) == 0) {
                        disconnect();
                    } else {
                        System.out.print("WRONG INPUT,TRY AGAIN " + "\n" + "\n");
                    }
                } catch (NumberFormatException e) {
                    System.out.print("WRONG INPUT,TRY AGAIN " + "\n" + "\n");
                }
            }
            notifyEveryBroker(true, getPublishedTopics());
            System.exit(0);
        }
    }


    public class HandleTopicUpdates extends Thread {
        public HandleTopicUpdates() {
        }

        public void run() {
            while (RUNNING) {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Iterator it1;
                synchronized (subscribedTopics) {
                    it1 = subscribedTopics.iterator();
                }
                String topic;
                while (it1.hasNext()) {
                    synchronized (subscribedTopics) {
                        topic = (String) it1.next();
                    }
                    System.out.println("[APPNODE] >>> UPDATES FROM: " + topic);
                    playData(topic);
                }
            }
            notifyEveryBroker(true, getPublishedTopics());
            System.exit(0);
        }
    }


}
