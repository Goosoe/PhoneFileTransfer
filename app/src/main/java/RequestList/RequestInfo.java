package RequestList;

import java.util.Objects;

public class RequestInfo {
    private String ip;
    private String hostname;
    private boolean accepted;

    /**
     * Accepted param is initialized as false when invoking this constructor
     * @param ip
     * @param hostname
     */
    public RequestInfo(String ip, String hostname){
        this.ip = ip;
        this.hostname = hostname;
        this.accepted = false;
    }
    public RequestInfo(String ip, String hostname, boolean accepted){
        this(ip, hostname);
        this.accepted = accepted;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIp() {
        return ip;
    }

    public boolean getAccepted(){
        return accepted;
    }

    public void setAccepted(boolean accepted){
        this.accepted = accepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestInfo that = (RequestInfo) o;
        return accepted == that.accepted &&
                ip.equals(that.ip) &&
                hostname.equals(that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, hostname, accepted);
    }
}
