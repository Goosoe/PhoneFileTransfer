package SillyGoose.phonefiletransfer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.zip.ZipFile;

import RequestList.RequestAdapter;
import RequestList.RequestInfo;
import Server.HttpServer;
import Server.ServerUtils;
import Utils.Utils;

public class ServerActivity extends Activity {

    private static String ip = "localhost";
    private static final int PORT = 8080;
    private HttpServer server = null;
    private RecyclerView requestRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.server_layout);
        requestRecyclerView = this.findViewById(R.id.requestListView);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestRecyclerView.setAdapter(new RequestAdapter(this));
        ImageButton b = this.findViewById(R.id.powerButton);
        b.setOnClickListener(listener -> super.finish());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.askForPermissions(this);
        startServer();
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
//        if(executorService != null && !executorService.isTerminated())
//            executorService.shutdownNow();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerUtils.cleanCachedZips(this.getCacheDir());
    }

    public void newRequest(RequestInfo request){
        ((RequestAdapter) requestRecyclerView.getAdapter()).addItem(request, this);
    }

    private void startServer() {
        //TODO: Null checks
        int fileNumber = -1;
        //TODO: magic string
        String output = getIntent().getStringExtra("outputZipPath");
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(output);
            fileNumber = zipFile.size();
            zipFile.close();
            if(fileNumber < 1) {
                Toast.makeText(getApplicationContext(), "You don't have any files chosen to send", Toast.LENGTH_LONG).show();
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        ip = ServerUtils.getIPAddress(true);

        TextView serverDirections = findViewById(R.id.serverDirections);
        serverDirections.setText(R.string.connect);
        TextView ipText = findViewById(R.id.serverIp);
        ipText.setText(ip.concat(":").concat(String.valueOf(PORT)));
        TextView fileInfo = findViewById(R.id.fileInfo);
        fileInfo.setText(getString(R.string.files_info, String.valueOf(fileNumber)));


        //TODO: make this happen in a thread, so the zipping can happen. While that thread is
        // running make a secondary thread to rotate an image button
        if(server == null) {
            server = new HttpServer(ip, PORT, this, output, fileNumber);
        }
    }
}