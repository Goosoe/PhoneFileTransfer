package Server;

import android.content.Context;
import android.se.omapi.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {
//    private static File CACHE_FILE;
//    private static final String CACHE_FILE_NAME = "cache";
    private final Context context;
    private final List<String> filesToSend;
//    private static final String outputName = "out.zip";
    private final String ip;
    private String downloadButtonVal = "download";
    private String outputZipPath = null;
    private File lastZip = null;

    public HttpServer(String ip , int port, Context context, List<String> filesToSend) {
        super(ip,port);
        this.context = context;
        this.filesToSend = filesToSend;
        this.ip = ip;
//        CACHE_FILE = new File(context.getCacheDir().toString().concat(CACHE_FILE_NAME));
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Response serve(IHTTPSession session) {
        String outputName = UUID.randomUUID().toString().concat(".zip");
        outputZipPath = context.getCacheDir() + File.separator + outputName;

        switch (session.getMethod()){
            case GET:
                if(downloadButtonPressed(session)){
                    try {
                        //TODO: check if its only one file and its a .zip or .7z format -> No need to call the zip service
                        File zippedFile = Utils.zipFiles(filesToSend, outputZipPath);
                        if(zippedFile == null)
                            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error sending the selected files or no files were selected for transfer");

                        FileInputStream fis = new FileInputStream(zippedFile);
                        NanoHTTPD.Response res = newFixedLengthResponse(Response.Status.OK, "application/zip", fis, zippedFile.length());
                        res.addHeader("Content-Disposition", "attachment; filename=\"" + outputName + "\"");
                        return res;

                    } catch (IOException e) {
                        //does nothing
                    }
                }
                else {
//
                    //TODO: interesting string here. Should I make a html creator or just leave this beast here?
                    final String html = "<html> <p> Connection from local device: " + android.os.Build.MODEL + "</p>\n" +
                            "<p>Number of files to download: " + filesToSend.size() +"</p>\n" +
                            "<form action=\"\" method=\"get\"><button name=\"" + downloadButtonVal + "\">Get Files</button></form>" +
//                                info +
                            "</html>";
                    return newFixedLengthResponse(html);
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

    private boolean downloadButtonPressed(IHTTPSession session){
        return session.getParameters().containsKey(downloadButtonVal);
    }


}
