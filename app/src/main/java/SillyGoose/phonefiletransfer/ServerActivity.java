package SillyGoose.phonefiletransfer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import Server.HttpServer;
import Server.Utils;
import Utils.UriUtils;

public class ServerActivity extends Activity {

    private static String ip = "localhost";
    private static final int PORT = 8080;
    private HttpServer server = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_layout);

        askForPermissions();
        String[] uris = checkReceivedIntent();
        if(uris != null)
            startServer(uris);
    }

    /**
     * This function checks for received intents from navigator apps which have the URI's of the desired files to send
     * @return a String[] of the URI's to send or null if error/none
     */
    public  String[] checkReceivedIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        LinkedList<String> filesPaths = new LinkedList<>();
        ArrayList<Uri> uris = null;

        switch (action) {
            case Intent.ACTION_SEND:
                uris = new ArrayList<>();
                uris.add(intent.getParcelableExtra(Intent.EXTRA_STREAM));
                break;
            case Intent.ACTION_SEND_MULTIPLE:
                uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                break;
        }
        UriUtils uriUtils = new UriUtils(this.getBaseContext());
        if (uris != null) {
            for (Uri fileUri : uris) {
                filesPaths.add(uriUtils.getPath(fileUri));
            }
            String[] itemsArray = new String[filesPaths.size()];
            return filesPaths.toArray(itemsArray);

        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(server != null && server.isAlive()) {
            server.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (server != null && !server.isAlive()) {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startServer(String[] filesToUpload) {
        if(filesToUpload.length < 1 ) {
            Toast.makeText(getApplicationContext(), "You don't have any files chosen to send", Toast.LENGTH_LONG).show();
            return;
        }
        ip = Utils.getIPAddress(true);
        TextView serverDirections = findViewById(R.id.serverDirections);
        serverDirections.setText(R.string.connect);
        TextView ipText = findViewById(R.id.serverIp);
        ipText.setText(ip.concat(":").concat(String.valueOf(PORT)));
        TextView fileInfo = findViewById(R.id.fileInfo);
        fileInfo.setText(getString(R.string.files_info, String.valueOf(filesToUpload.length)));

        if(server == null) {
            server = new HttpServer(ip, PORT, this, Arrays.asList(filesToUpload));
        }
        ImageButton b = this.findViewById(R.id.powerButton);
//        b.setVisibility(View.VISIBLE);
//        this.findViewById(R.id.textView2).setVisibility(View.VISIBLE);
        b.setOnClickListener(listener -> super.finish());

    }

    private void askForPermissions(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(),"Please allow access permissions, otherwise the app won't work", Toast.LENGTH_LONG).show();

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(getApplicationContext(),"Please allow access permissions, otherwise the app won't work", Toast.LENGTH_LONG).show();}
                }).check();
    }
}
