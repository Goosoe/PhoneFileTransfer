package Server;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import FileNavigator.IconData;
import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {
    private final Context context;
    private List<IconData> filesToSend;
    private static final String outputName = "out.zip";
    public HttpServer(String ip , int port, Context context, List<IconData> filesToSend) {
        super(ip,port);
        this.context = context;
        this.filesToSend = filesToSend;
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Response serve(IHTTPSession session) {
//        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        File dir = new File(filesToSend.get(0));
        String outputZipPath = context.getCacheDir() + File.separator + outputName;

        try {
            FileOutputStream fos = new FileOutputStream(outputZipPath);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File f = null;
            for(IconData iconD : filesToSend){
                f = new File(iconD.filePath);
                Utils.zipFile(f, f.getName(), zipOut);
            }
            zipOut.close();

            File zippedFile = new File(outputZipPath);
            FileInputStream fis = new FileInputStream(zippedFile);
            NanoHTTPD.Response res = newFixedLengthResponse (Response.Status.OK, "application/zip", fis, zippedFile.length());
            res.addHeader("Content-Disposition", "attachment; filename=\"" + outputName+ "\"");
            return res;

        } catch (IOException e) {
            e.printStackTrace();
        }



        return  newFixedLengthResponse("Something went wrong :(");
    }


}
