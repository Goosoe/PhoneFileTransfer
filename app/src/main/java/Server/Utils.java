package Server;



import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequestSupplier;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;

public class Utils {

    public static File zipFiles(List<String> uris, String outputZipPath){

        ZipArchiveOutputStream outputStream = null;
        ParallelScatterZipCreator zipCreator = new ParallelScatterZipCreator();

        for(String uri : uris) {
            File f = new File(uri);
            InputStreamSupplier supp = new InputStreamSupplier() {
                @Override
                public InputStream get() {
                    try {
                        return new FileInputStream(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return  null;
                }
            };

            zipCreator.addArchiveEntry(new ZipArchiveEntryRequestSupplier() {
                @Override
                public ZipArchiveEntryRequest get() {
                    ZipArchiveEntry entry = new ZipArchiveEntry(f, f.getName());
                    entry.setMethod(ZipArchiveOutputStream.DEFAULT_COMPRESSION);
                    return ZipArchiveEntryRequest.createZipArchiveEntryRequest(entry, supp);
                }
            });
        }
        try {
            File outputFile = new File(outputZipPath);
            outputStream = new ZipArchiveOutputStream(outputFile);
//            outputStream.setMethod(ZipArchiveOutputStream.DEFAULT_COMPRESSION);
            zipCreator.writeTo(outputStream);
            outputStream.finish();
            return outputFile;
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void zipFile(File fileToZip, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        String filePath = fileToZip.getName();
        if (fileToZip.isDirectory()) {
            if (filePath.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(filePath));
            }
            else {
                zipOut.putNextEntry(new ZipEntry(filePath + "/"));
            }
            zipOut.closeEntry();
            for (File childFile : fileToZip.listFiles()) {
                zipFile(childFile, zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(filePath);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
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

}