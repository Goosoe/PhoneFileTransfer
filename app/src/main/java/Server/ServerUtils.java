package Server;

import android.app.Activity;

import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import RequestList.RequestInfo;

public class ServerUtils {

    public static File zipFiles(List<String> paths, String outputZipPath){
        if(paths.isEmpty())
            return null;
        
        if(paths.size() == 1 && paths.get(0).contains(".zip")) {
            File dest = new File(outputZipPath);
            try {
                FileUtils.copyFile(new File(paths.get(0)), dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dest;
        }

        ZipArchiveOutputStream outputStream;
        ParallelScatterZipCreator zipCreator = new ParallelScatterZipCreator();

        for(String path : paths) {
            File f = new File(path);
            InputStreamSupplier supp = () -> {
                try {
                    return new FileInputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return  null;
                }
            };

            zipCreator.addArchiveEntry(() -> {
                ZipArchiveEntry entry = new ZipArchiveEntry(f, f.getName());
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
            return outputFile;
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
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
     * Removes all zip files from context.getCacheDir() and files copied into getFilesDir()
     * @param activity
     */
    public static void cleanStorage(Activity activity) {
        if(activity == null)
            return;

        File cacheDir = activity.getCacheDir();
        //TODO: MT too?
        System.out.println("cached size " + cacheDir.listFiles().length);
        if(cacheDir.listFiles() != null) {
            for (File f : cacheDir.listFiles()) {

                String[] nameArray = f.getName().split("\\.");
                if (f.getName().contains("parallelscatter") || (nameArray.length > 0 && nameArray[nameArray.length - 1].equals("zip"))) {
                    f.delete();
                }
            }
        }
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