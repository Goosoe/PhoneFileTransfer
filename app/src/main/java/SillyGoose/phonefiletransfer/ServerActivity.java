package SillyGoose.phonefiletransfer;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CheckBox;
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
import Server.REQUEST_RESPONSE_TYPE;
import Server.ServerUtils;
import Utils.Utils;

public class ServerActivity extends Activity {

    private static String ip = "localhost";
    private static  final int DEFAULT_PORT = 8080;
    private HttpServer server = null;
    private RecyclerView requestRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        if(!Utils.isConnectedToWifi(this)){
            Toast.makeText(getApplicationContext(), "You are not connected to any local network. Please connect and try again", Toast.LENGTH_LONG).show();
            super.finish();
        }
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
        if (server != null) {
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
    protected void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerUtils.cleanStorage(this);
    }

    public void newRequest(RequestInfo request){
        CheckBox cBox = findViewById(R.id.request_checkBox);
        //if the checkbox is ticked, there is no need to create a request card
        if(cBox.isChecked()){
            ServerUtils.answerRequest(request,REQUEST_RESPONSE_TYPE.ACCEPTED);
        }
        else {
            ((RequestAdapter) requestRecyclerView.getAdapter()).addItem(request, this);
        }
    }

    private void startServer() {
        //TODO: Null checks
        int fileNumber = -1;
        //TODO: magic string
        String output = getIntent().getStringExtra("outputZipPath");
        if(output == null)
            Utils.closeApp(this, "Error creating the .zip file, please try again.");
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(output);
            fileNumber = zipFile.size();
            zipFile.close();
            if(fileNumber < 1) {
                //TODO: Magic string
                Utils.closeApp(this, "You don't have any files chosen to send");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*Updating server text*/
        ip = ServerUtils.getIPAddress(true);

        TextView serverDirections = findViewById(R.id.serverDirections);
        serverDirections.setText(R.string.connect);

        TextView ipText = findViewById(R.id.serverIp);
        ipText.setText(ip.concat(":").concat(String.valueOf(DEFAULT_PORT)));

        TextView fileInfo = findViewById(R.id.fileInfo);
        fileInfo.setText(getString(R.string.files_info, String.valueOf(fileNumber)));

        CheckBox cBox = findViewById(R.id.request_checkBox);
        cBox.setText(R.string.requests_checkBox);

        if(server == null) {
            try {
                server = new HttpServer(this, DEFAULT_PORT, output, fileNumber);
            }catch (IOException e){
                //TODO: bruh...
                //if port is being used try on the next one
                try {
                    server = new HttpServer(this, DEFAULT_PORT + 1, output, fileNumber);
                }catch (IOException ex){
                    Utils.closeApp(this, "The default ports of this service are already being used." +
                            " Please try again later after closing the services using that port");
                }
                ipText.setText(ip.concat(":").concat(String.valueOf(DEFAULT_PORT + 1)));
            }
        }
    }
}
