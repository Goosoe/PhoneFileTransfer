package SillyGoose.phonefiletransfer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import RequestList.RequestAdapter;
import Server.HttpServer;
import Server.ZipUtils;
import Utils.UriUtils;
import Utils.Utils;

public class ServerActivity extends Activity {

    private static String ip = "localhost";
    private static final int PORT = 8080;
    private HttpServer server = null;
    private RecyclerView requestRecyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_layout);

        requestRecyclerView = this.findViewById(R.id.requestListView);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestRecyclerView.setAdapter(new RequestAdapter());

        Utils.askForPermissions(this);
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
        ip = ZipUtils.getIPAddress(true);
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
        b.setOnClickListener(listener -> super.finish());
    }

    public void newRequest(String hostname, String ip){
       ((RequestAdapter) requestRecyclerView.getAdapter()).addItem(hostname, ip,this);
    }

    public void notifyServer(Utils.Tuple<String,String> info){
        server.notifyAcceptedConnection(info);

    }


}
