package SillyGoose.phonefiletransfer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Server.ServerUtils;
import Utils.Utils;

public class PrepareServerActivity extends Activity {

    private String outputZipPath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setContentView(R.layout.prepare_server_layout);
        if(!Utils.isConnectedToWifi(this)){
            Utils.closeApp(this, "You are not connected to any local network. Please connect and try again");
        }
        TextView progressText = findViewById(R.id.progressText);
        //TODO: magic string
        progressText.setText("Zipping Files");
        ServerUtils.cleanStorage(this);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            outputZipPath = prepareZip(checkReceivedIntent());
            notifyServerReady();
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void notifyServerReady() {
        Intent intent = new Intent(this, ServerActivity.class);
        //TODO: magic string
        intent.putExtra("outputZipPath", outputZipPath);
        startActivity(intent);
        finish();
    }

    private List<Uri> checkReceivedIntent() {
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
        return uris;
    }

    /**
     *
     * @param filesToSend
     * @return the path of the created Zip File
     */
    private String prepareZip(List<Uri> filesToSend) {
        String outputName = UUID.randomUUID().toString().concat(".zip");
        String outputZipPath = this.getCacheDir() + File.separator + outputName;
        return ServerUtils.zipFiles(this, filesToSend, outputZipPath) == null ? null : outputZipPath;

    }
}
