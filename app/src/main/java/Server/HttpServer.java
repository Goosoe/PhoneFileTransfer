package Server;

import android.content.Context;

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
    private final Context context;
    private List<String> filesToSend;
    private String outputName = "out.zip";
    private String ip;
    private String downloadButtonVal = "download";

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
    }
    @Override
    public Response serve(IHTTPSession session) {
        outputName = UUID.randomUUID().toString().concat(".zip");
        String outputZipPath = context.getCacheDir() + File.separator + outputName;

        //TODO: Do I want to zip the files instantly?
        try {
            FileOutputStream fos = new FileOutputStream(outputZipPath);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File f;
            for(String filePath : filesToSend){
                f = new File(filePath);
                Utils.zipFile(f, zipOut);
            }
            zipOut.close();

            File zippedFile = new File(outputZipPath);
            FileInputStream fis = new FileInputStream(zippedFile);
            //show html file

            switch (session.getMethod()){
                case GET:
                    if(session.getParameters().containsKey(downloadButtonVal)){
                        NanoHTTPD.Response res = newFixedLengthResponse (Response.Status.OK, "application/zip", fis, zippedFile.length());
                        res.addHeader("Content-Disposition", "attachment; filename=\"" + outputName+ "\"");
                    }
                    else {
//                        String info = "";
//                        for (String s : session.getParameters().keySet())
//                            info = info.concat(s).concat("\n");

                        final String html = "<html> <p> Connection found from local device: " + ip + "</p>\n" +
                                "<p>Number of files to download: " + filesToSend.size() +"</p>\n" +
                                "<form action=\"\" method=\"get\"><button name=\"" + downloadButtonVal + "\">Get Files</button></form>" +
//                                info +
                                "</html>";
                        return newFixedLengthResponse(Response.Status.OK, MIME_HTML, html);
                    }
                case POST:
                    break;
                default:
                    return newFixedLengthResponse("Whoops, something went wrong :(");
            }
            if(session.getMethod()  == Method.GET){

            }

            //default response
            NanoHTTPD.Response res = newFixedLengthResponse (Response.Status.OK, "application/zip", fis, zippedFile.length());
            res.addHeader("Content-Disposition", "attachment; filename=\"" + outputName+ "\"");
            return res;

        } catch (IOException e) {
            e.printStackTrace();
        }



        return  newFixedLengthResponse("Something went wrong :(");
    }


}
