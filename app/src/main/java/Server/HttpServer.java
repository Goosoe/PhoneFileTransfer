package Server;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import SillyGoose.phonefiletransfer.R;
import SillyGoose.phonefiletransfer.ServerActivity;
import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    private final Context context;
    private final List<String> filesToSend;

    private final String ip;
//    private static final String DOWNLOAD_BUTTON_VAL = "download";
    private static File zippedFile;
    private static String outputName = null;

    public HttpServer(String ip , int port, Context context, List<String> filesToSend) {
        super(ip,port);
        this.context = context;
        this.filesToSend = filesToSend;
        this.ip = ip;
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
        //TODO: check if this does not create extra stuff unnecessarily
        switch (session.getMethod()){
            case GET:
                if(downloadButtonPressed(session)) {
                    newRequest(session.getRemoteHostName(), session.getRemoteIpAddress());
                    try {
                        if (zippedFile == null) {
                            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error sending the selected files or no files were selected for transfer");
                        }
                        FileInputStream fis = new FileInputStream(zippedFile);
                        NanoHTTPD.Response res = newFixedLengthResponse(Response.Status.OK, "application/zip", fis, zippedFile.length());
                        res.addHeader("Content-Disposition", "attachment; filename=\"" + outputName + "\"");
                        return res;

                    } catch (IOException e) {
                        //does nothing
                    }
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

    private void newRequest(String hostname, String ip) {
        ((ServerActivity)context).newRequest(hostname + "\n" + ip);
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

}
