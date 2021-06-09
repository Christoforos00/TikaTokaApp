package gr.aueb.tikatokaapp.Core;

import java.io.Serializable;

public class Address implements Serializable {
    private String ip;
    private int port;

    public Address(String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String toString() {
        return "address: "+ ip + ":" + port;
    }

    @Override
    public boolean equals(Object object) {
        boolean same = false;

        if (object != null && object instanceof Address) {
            same = ip.equals(((Address) object).ip) && port == ((Address) object).port;
        }

        return same;
    }


}
