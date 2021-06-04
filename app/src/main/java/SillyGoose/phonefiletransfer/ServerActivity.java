package SillyGoose.phonefiletransfer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import RequestList.RequestAdapter;
import RequestList.RequestInfo;
import Server.HttpServer;
import Server.ZipUtils;
import Utils.UriUtils;
import Utils.Utils;

public class ServerActivity extends Activity {

    private static String ip = "localhost";
    private static final int PORT = 8080;
    private HttpServer server = null;
    private RecyclerView requestRecyclerView;
    private ExecutorService executorService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.server_layout);
        requestRecyclerView = this.findViewById(R.id.requestListView);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestRecyclerView.setAdapter(new RequestAdapter());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.askForPermissions(this);
        String[] uris = checkReceivedIntent();

        if(uris != null) {
            long time = System.currentTimeMillis();
            startServer(uris);
            System.out.println(System.currentTimeMillis() - time);
        }
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
        if(executorService != null && !executorService.isTerminated())
            executorService.shutdownNow();

        if (server != null) {
            server.onResume();
            if (!server.isAlive()) {
                try {
                    server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void newRequest(String hostname, String ip){
        ((RequestAdapter) requestRecyclerView.getAdapter()).addItem(hostname, ip,this);
    }

    public void notifyServer(RequestInfo info){
        server.notifyConnectionRequest(info);
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
        ImageButton b = this.findViewById(R.id.powerButton);
        b.setOnClickListener(listener -> super.finish());

        //TODO: make this happen in a thread, so the zipping can happen. While that thread is
        // running make a secondary thread to rotate an image button
        if(server == null) {
            server = new HttpServer(ip, PORT, this, Arrays.asList(filesToUpload));
        }
    }

    /**
     * This function checks for received intents from navigator apps which have the URI's of the desired files to send
     * @return a String[] of the URI's to send or null if error/none
     */
    private String[] checkReceivedIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        LinkedList<String> filesPaths = new LinkedList<>();
        ArrayList<Uri> uris = new ArrayList<>();
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
        //TODO: Overkill much?
       executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
        if (uris != null) {
            long time = System.currentTimeMillis();
            for (Uri fileUri : uris) {
                //TODO: ITS HERE MF
                executorService.submit(() -> {
                    filesPaths.add(uriUtils.getPath(fileUri));
                });
            }
            String[] itemsArray = new String[filesPaths.size()];
            executorService.shutdown();
            while (!executorService.isTerminated()){
                //do nothing
            }
            System.out.println(System.currentTimeMillis() - time);
            return filesPaths.toArray(itemsArray);

        }
        return null;
    }
}
