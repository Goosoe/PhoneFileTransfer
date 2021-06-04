package Server;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.zip.ZipFile;

import RequestList.RequestInfo;
import SillyGoose.phonefiletransfer.R;
import SillyGoose.phonefiletransfer.ServerActivity;
import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    private final Context context;
    private String pathOfFileToSend;
    //Has the ip's of the open sessions
    private final HashSet<RequestInfo> requestedConnections;
    private final String ip;
    private int fileNumber;
//    private static File zippedFile;

    public HttpServer(String ip , int port, Context context, String pathOfFileToSend) {
        super(ip,port);
        this.context = context;
        this.pathOfFileToSend = pathOfFileToSend;
        this.ip = ip;
        this.requestedConnections = new HashSet<>();
        this.fileNumber = 0;
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public HttpServer(String ip , int port, Context context, String pathOfFileToSend, int fileSize) {
        this(ip, port, context, pathOfFileToSend);
        this.fileNumber = fileSize;
    }

    @Override
    public Response serve(IHTTPSession session) {
//        if(openSessions.contains(session.getRemoteIpAddress())){
//            final String html = "<html><p> There is already an ongoing connection from this device to the server </p></html>";
//            return newFixedLengthResponse(Response.Status.OK, "text/html", html);
//        }
//        openSessions.add(session.getRemoteIpAddress());
        switch (session.getMethod()){
            case GET:
                if(downloadButtonPressed(session)) {
                    //TODO: if this request already exists in http server, it can drop the connection
                    newRequest(session.getRemoteHostName(), session.getRemoteIpAddress());
                    //if request accepted
                    if(waitForConfirmation(session.getRemoteIpAddress())){
                        try {

                            File zippedFile = new File(pathOfFileToSend);
                            if (zippedFile == null) {
                                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error sending the selected files or no files were selected for transfer");
                            }
                            FileInputStream fis = new FileInputStream(zippedFile);
                            NanoHTTPD.Response res = newFixedLengthResponse(Response.Status.OK, "application/zip", fis, fileNumber);
                            res.addHeader("Content-Disposition", "attachment; filename=\"PhoneFileTransfer.zip\"");
                            removeRequest(session.getRemoteIpAddress());
                            return res;

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        removeRequest(session.getRemoteIpAddress());
                        final String html = "<html><p> Request denied by the server/phone user :(</p></html>";
                        return newFixedLengthResponse(Response.Status.OK, "text/html", html);
                    }

                    //else  return timeout
                }
                else{
                    //TODO: interesting string here. Should I make a html creator or just leave this beast here?
                    int size = 0;
//                    try {
////                        size = new ZipFile(pathOfFileToSend).size();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    final String html = "<html> <p> Connection from local device: " + android.os.Build.MODEL + "</p>\n" +
                            "<p>Number of files to download: " + size + "</p>\n" +
                            "<form action=\"\" method=\"get\"><button name=\"" + context.getString(R.string.download_button_val) + "\">Get Files</button></form>" +
                            "<p>After pressing the button, the phone user needs to accept your request. Please wait or notify the said" +
                            " user to accept your request if it is taking too long </p>" +
                            "</html>";
                    return newFixedLengthResponse(Response.Status.OK, "text/html", html);
                }
            case POST:
                break;
            default:
                return newFixedLengthResponse("Whoops, something went wrong :(");
        }
//            if(session.getMethod()  == Method.GET){
//
//            }
//
//            //default response
//            NanoHTTPD.Response res = newFixedLengthResponse (Response.Status.OK, "application/zip", fis, zippedFile.length());
//            res.addHeader("Content-Disposition", "attachment; filename=\"" + outputName+ "\"");
//            return res;

        return  newFixedLengthResponse("Something went wrong :(");
    }

    public void notifyConnectionRequest(RequestInfo info) {
        if(!requestedConnections.contains(info))
            requestedConnections.add(info);
    }

    public void onResume(){
        requestedConnections.clear();
    }

    private void removeRequest(String ip) {
        for (RequestInfo info : requestedConnections)
            if (info.getIp().equals(ip))
                requestedConnections.remove(info);
    }

    /**
     * @param hostname
     * @param ip
     * @return id of the request
     */
    private void newRequest(String hostname, String ip) {
        ((ServerActivity)context).newRequest(hostname, ip);
    }

    private boolean downloadButtonPressed(IHTTPSession session){
        return session.getParameters().containsKey(context.getString(R.string.download_button_val));
    }



    private boolean waitForConfirmation(String ip) {
        while (true) {
            for (RequestInfo info : requestedConnections)
                if (info.getIp().equals(ip))
                    return info.getAccepted();
        }

    }
}
