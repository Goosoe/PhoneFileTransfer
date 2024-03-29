package RequestList;

import java.util.Objects;

import Server.HttpServer;
import Server.REQUEST_RESPONSE_TYPE;

public class RequestInfo {
    private String ip;
    private String hostname;
    private REQUEST_RESPONSE_TYPE responseType;
    private Thread serveThread;
    /**
     * Accepted param is initialized as false when invoking this constructor
     * @param ip
     * @param hostname
     */
    public RequestInfo(String ip, String hostname){
        this.ip = ip;
        this.hostname = hostname;
        this.responseType = REQUEST_RESPONSE_TYPE.DENIED;
    }
    public RequestInfo(String ip, String hostname, REQUEST_RESPONSE_TYPE responseType){
        this(ip, hostname);
        this.responseType = responseType;
    }
    public RequestInfo(String ip, String hostname, Thread serveThread){
        this(ip, hostname);
        this.serveThread = serveThread;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIp() {
        return ip;
    }

    public REQUEST_RESPONSE_TYPE getResponseType(){
        return responseType;
    }

    public void setResponseType(REQUEST_RESPONSE_TYPE type){
        this.responseType = type;
    }

    public Thread getServeThread() {
        return serveThread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestInfo that = (RequestInfo) o;
        return responseType == that.responseType &&
                ip.equals(that.ip) &&
                hostname.equals(that.hostname) &&
                serveThread.equals(that.serveThread);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, hostname, responseType, serveThread);
    }
}
