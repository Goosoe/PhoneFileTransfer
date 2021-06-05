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
        if(openSessions.size() > Utils.MAX_REQUESTS) {
            final String html = "<html><p> The device is already serving the maximum number of requests defined. Please try again in a moment</p></html>";
            return newFixedLengthResponse(Response.Status.OK, "text/html", html);
        }

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

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;

                        case DENIED:
                            //TODO: magic string
                            html = "<html><p> Request denied by the server/phone user :(</p></html>";
                            res = newFixedLengthResponse(Response.Status.OK, "text/html", html);
                            break;
                        case TIMEOUT:
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


    public void onResume(){
    }

    /**
     * @param hostname
     * @param ip
     * @return id of the request
     */
    private RequestInfo newRequest(String hostname, String ip) {
        RequestInfo request = new RequestInfo(ip, hostname, Thread.currentThread());
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
