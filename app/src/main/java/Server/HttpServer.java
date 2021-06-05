package Server;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import RequestList.RequestInfo;
import SillyGoose.phonefiletransfer.R;
import SillyGoose.phonefiletransfer.ServerActivity;
import Utils.Utils;
import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    private final Context context;
    private String pathOfFileToSend;
    private HashSet<String> openSessions;
    //Has the info of the open sessions
//    private final HashSet<RequestInfo> requestedConnections;
    private final String ip;
    private int numberOfFiles;

    private HttpServer(String ip , int port, Context context, String pathOfFileToSend) {
        super(ip,port);
        this.context = context;
        this.pathOfFileToSend = pathOfFileToSend;
        this.ip = ip;
        this.openSessions = new HashSet<>();
        this.numberOfFiles = 0;
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * @param ip
     * @param port
     * @param context
     * @param pathOfFileToSend
     * @param numberOfFiles
     */
    public HttpServer(String ip , int port, Context context, String pathOfFileToSend, int numberOfFiles) {
        this(ip, port, context, pathOfFileToSend);
        this.numberOfFiles = numberOfFiles;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if(openSessions.contains(session.getRemoteIpAddress())){
            final String html = "<html><p> There is already an ongoing request from this device to the server. Please try again in a minute </p></html>";
            return newFixedLengthResponse(Response.Status.OK, "text/html", html);
        }
        openSessions.add(session.getRemoteIpAddress());
        NanoHTTPD.Response res = null;
        String html = null;
        switch (session.getMethod()){
            case GET:
                if(downloadButtonPressed(session)) {
                    //TODO: if this request already exists in http server, it can drop the connection

                    //if request accepted
                    switch (waitForConfirmation(newRequest(session.getRemoteHostName(), session.getRemoteIpAddress()))) {
                        case ACCEPTED:
                            try {

                                File zippedFile = new File(pathOfFileToSend);
                                if (zippedFile == null) {
                                    res = newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error sending the selected files or no files were selected for transfer");
                                }
                                FileInputStream fis = new FileInputStream(zippedFile);
                                res = newFixedLengthResponse(Response.Status.OK, "application/zip", fis, zippedFile.length());
                                res.addHeader("Content-Disposition", "attachment; filename=\"PhoneFileTransfer.zip\"");
//                                removeRequest(session.getRemoteIpAddress());

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;

                        case DENIED:
//                            removeRequest(session.getRemoteIpAddress());
                            //TODO: magic string
                            html = "<html><p> Request denied by the server/phone user :(</p></html>";
                            res = newFixedLengthResponse(Response.Status.OK, "text/html", html);
                            break;
                        case TIMEOUT:
//                            removeRequest(session.getRemoteIpAddress());
                            //TODO: magic string
                            html = "<html><p> Request timed out, please request again </p>" +
                                    "<form action=\"\" method=\"get\"><button name=\"" + context.getString(R.string.download_button_val) + "\">Get Files</button></form>" +
                                    "</html>";
                            res = newFixedLengthResponse(Response.Status.OK, "text/html", html);
                            break;
                    }
                }
                else{
                    //TODO: interesting string here. Should I make a html creator or just leave this beast here?
                    html = "<html> <p> Connection from local device: " + android.os.Build.MODEL + "</p>\n" +
                            "<p>Number of files to download: " + numberOfFiles + "</p>\n" +
                            "<form action=\"\" method=\"get\"><button name=\"" + context.getString(R.string.download_button_val) + "\">Get Files</button></form>" +
                            "<p>After pressing the button, the phone user needs to accept your request. Please wait or notify the said" +
                            " user to accept your request if it is taking too long </p>" +
                            "</html>";
                    res = newFixedLengthResponse(Response.Status.OK, "text/html", html);
                }
                break;
            case POST:
            default:
                //TODO: magic string
                res = newFixedLengthResponse("Something went wrong :(");
                break;
        }
        openSessions.remove(session.getRemoteIpAddress());
        return res;
    }

//    public void notifyConnectionRequest(RequestInfo info) {
//        if(!requestedConnections.contains(info))
//            requestedConnections.add(info);
//    }

    public void onResume(){
//        requestedConnections.clear();
    }

//    private void removeRequest(String ip) {
//        for (RequestInfo info : requestedConnections)
//            if (info.getIp().equals(ip))
//                requestedConnections.remove(info);
//    }

    /**
     * @param hostname
     * @param ip
     * @return id of the request
     */
    private RequestInfo newRequest(String hostname, String ip) {
        RequestInfo request = new RequestInfo(ip, hostname, Thread.currentThread(),this);
        ((ServerActivity)context).newRequest(request);
        return request;
    }

    private boolean downloadButtonPressed(IHTTPSession session){
        return session.getParameters().containsKey(context.getString(R.string.download_button_val));
    }



    private REQUEST_RESPONSE_TYPE waitForConfirmation(RequestInfo request) {
        try {
            //if im feeling cheeky use Long.MAX_VALUE
            Thread.sleep(Utils.WAIT_CONFIRMATION_TIMEOUT * 2);
//            request.setResponseType(REQUEST_RESPONSE_TYPE.TIMEOUT);
            //NOTIFY TIMEOUt to the adapter?
        } catch (InterruptedException e) {
            //does nothing
        }

        return request.getResponseType();

    }
}
