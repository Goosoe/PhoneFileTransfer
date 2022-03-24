package Server;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import RequestList.RequestInfo;

public class ServerUtils {

    public static File zipFiles(Context context, List<Uri> uris, String outputZipPath){
        ArrayList<File> auxFilesToDelete = new ArrayList<>();

        if(uris.isEmpty())
            return null;

        //If we want to send a single .zip file, there is no need to re-zip it
        if(uris.size() == 1){
            Cursor returnCursor =
                    context.getContentResolver().query(uris.get(0), null, null, null, null);
            returnCursor.moveToFirst();
            String filename = returnCursor.getString(returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            if (filename.contains(".zip")) {
                return createCopiedFile(context, uris.get(0));
            }
        }

        ZipArchiveOutputStream outputStream;
        ParallelScatterZipCreator zipCreator = new ParallelScatterZipCreator();
        for(Uri uri : uris) {
            File copied = createCopiedFile(context, uri);
            auxFilesToDelete.add(copied);

            InputStreamSupplier supp = () -> {
                try {
                    return new FileInputStream(copied);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            };

            zipCreator.addArchiveEntry(() -> {
                ZipArchiveEntry entry = new ZipArchiveEntry(copied, copied.getName());
                entry.setMethod(ZipArchiveOutputStream.STORED);
                return ZipArchiveEntryRequest.createZipArchiveEntryRequest(entry, supp);
            });
        }
        try {
            File outputFile = new File(outputZipPath);
            outputStream = new ZipArchiveOutputStream(outputFile);
            zipCreator.writeTo(outputStream);
            outputStream.finish();
            outputStream.close();

            Executors.newSingleThreadExecutor().submit(() ->{
                for (File f: auxFilesToDelete) {
                    f.delete();
                }}
            );

            return outputFile;
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a file and copies the content from the uri from a uri
     * @param uri
     * @return the copied file
     */
    private static File createCopiedFile(Context ctx, Uri uri) {
        //get file name
        Cursor returnCursor =
                ctx.getContentResolver().query(uri, null, null, null, null);
        returnCursor.moveToFirst();
        String filenameToCopy = returnCursor.getString(returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        //Make a new empty file in cachedir with var name as title
        File f = new File(ctx.getCacheDir().getAbsolutePath() + File.separator + filenameToCopy);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            //copy the stuff from the existing file found in uri
            FileInputStream fis = new FileInputStream(ctx.getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());
            IOUtils.copy(fis, fos);
        }catch (IOException e){
            return null;
        }
        return f;
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    /**
     * Removes all files from context.getCacheDir()
     * @param activity
     */
    public static void cleanStorage(Activity activity) {
        if(activity == null)
            return;

        File cacheDir = activity.getCacheDir();
        //TODO: MT too?
        if(cacheDir.listFiles() != null) {
            for (File f : cacheDir.listFiles()) {
                String[] nameArray = f.getName().split("\\.");
                if (f.getName().contains(".zip")) {
                    f.delete();
                }
            }
        }
        //usually has nothing, but doesnt hurt to check (yes it does)
        if(activity.getFilesDir().listFiles() != null) {
            for (File f : activity.getFilesDir().listFiles()) {
                for (File file : f.listFiles()) {
                    file.delete();
                }
            }
        }
    }
    public static void answerRequest(RequestInfo request, REQUEST_RESPONSE_TYPE value) {
        if (request.getServeThread().isAlive()) {
            request.setResponseType(value);
            request.getServeThread().interrupt();
        }
    }
}