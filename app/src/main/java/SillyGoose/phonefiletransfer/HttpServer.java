package SillyGoose.phonefiletransfer;

import android.content.Context;
import android.os.Environment;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {
    private final Context context;
    private static final String outputName = "out.zip";
    public HttpServer(String ip , int port, Context context) {
        super(ip,port);
        this.context = context;
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Response serve(IHTTPSession session) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String outputZipPath = context.getCacheDir() + File.separator + outputName;
//        context.getCacheDir()
        try {
            FileOutputStream fos = new FileOutputStream(outputZipPath);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            Utils.zipFile(dir, dir.getName(), zipOut);
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
