package Server;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

import SillyGoose.phonefiletransfer.R;
import SillyGoose.phonefiletransfer.ServerActivity;
import Utils.Utils;
import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    private final Context context;
    private final List<String> filesToSend;
    //Has the ip's of the open sessions
    private HashSet<String> acceptedConnections;
    private final String ip;
//    private static final String DOWNLOAD_BUTTON_VAL = "download";
    private static File zippedFile;
    private static String outputName = null;

    public HttpServer(String ip , int port, Context context, List<String> filesToSend) {
        super(ip,port);
        this.context = context;
        this.filesToSend = filesToSend;
        this.ip = ip;
        acceptedConnections = new HashSet<>();
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ZipUtils.cleanCachedZips(context.getCacheDir());
        prepareZip();

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
                   newRequest(session.getRemoteHostName(), session.getRemoteIpAddress());
                    //if request accepted
                    if(waitForConfirmation(session.getRemoteIpAddress())){
                        try {
                            if (zippedFile == null) {
                                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error sending the selected files or no files were selected for transfer");
                            }
                            FileInputStream fis = new FileInputStream(zippedFile);
                            NanoHTTPD.Response res = newFixedLengthResponse(Response.Status.OK, "application/zip", fis, zippedFile.length());
                            res.addHeader("Content-Disposition", "attachment; filename=\"" + outputName + "\"");
                            acceptedConnections.remove(session.getRemoteIpAddress());
                            return res;

                        } catch (IOException e) {
                            //does nothing
                        }
                    }
//                    else{
//                        removeRequest(requestId);
//                    }

                    //else  return timeout
                }
                else{
                        //TODO: interesting string here. Should I make a html creator or just leave this beast here?
                        final String html = "<html> <p> Connection from local device: " + android.os.Build.MODEL + "</p>\n" +
                                "<p>Number of files to download: " + filesToSend.size() + "</p>\n" +
                                "<form action=\"\" method=\"get\"><button name=\"" + context.getString(R.string.download_button_val) + "\">Get Files</button></form>" +
//                                info +
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

    private void prepareZip() {
//        if(zippedFile == null) {
            outputName = UUID.randomUUID().toString().concat(".zip");
            String outputZipPath = context.getCacheDir() + File.separator + outputName;
            zippedFile = ZipUtils.zipFiles(filesToSend, outputZipPath);
//        }
    }

    private boolean waitForConfirmation(String ip){
        while (true){
            if(acceptedConnections.contains(ip))
                return true;
        }

    }

    public void notifyAcceptedConnection(Utils.Tuple<String, String> info) {
        if(!acceptedConnections.contains(info.getVal2()))
            acceptedConnections.add(info.getVal2());
    }
}
