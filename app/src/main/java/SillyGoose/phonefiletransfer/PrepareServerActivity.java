package SillyGoose.phonefiletransfer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Server.ServerUtils;
import Utils.UriUtils;
import Utils.Utils;

public class PrepareServerActivity extends Activity {

    private String outputZipPath;
    private TextView progressText;
    private int filesZipped;
    private int totalFilesToZip;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.prepare_server_layout);

        if(!Utils.isConnectedToWifi(this)){
            Utils.closeApp(this, "You are not connected to any local network. Please connect and try again");
        }
        progressText = findViewById(R.id.progressText);
        updateText();
        ServerUtils.cleanStorage(this);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            outputZipPath = prepareZip(Arrays.asList(checkReceivedIntent()));
            notifyServerReady();
        });

    }

    private void notifyServerReady() {
        Intent intent = new Intent(this, ServerActivity.class);
        //TODO: magic string
        intent.putExtra("outputZipPath", outputZipPath);
        startActivity(intent);
        finish();
    }

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
        totalFilesToZip = uris.size();
        UriUtils uriUtils = new UriUtils(this.getBaseContext());
        //TODO: Overkill much? - what about 1 core processors? Do they still exist?
        if (uris != null) {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (Uri fileUri : uris) {
                executorService.submit(() -> {
                    filesPaths.add(uriUtils.getPath(fileUri));
                    filesZipped++;
                    updateText();
                });
            }
            String[] itemsArray = new String[filesPaths.size()];
            executorService.shutdown();
            while (!executorService.isTerminated()){
                //does nothing
            }
            updateText();
            return filesPaths.toArray(itemsArray);

        }
        updateText();
        return null;
    }

    /**
     *
     * @param filesToSend
     * @return the path of the created Zip File
     */
    private String prepareZip(List<String> filesToSend) {
//        if(zippedFile == null) {
        String outputName = UUID.randomUUID().toString().concat(".zip");
        String outputZipPath = this.getCacheDir() + File.separator + outputName;
        ServerUtils.zipFiles(filesToSend, outputZipPath);
        return outputZipPath;
//        }
    }

    private void updateText(){
        this.runOnUiThread(() ->{
            String text = "Reading files: ";
            //TODO: magic string
            progressText.setText(text + filesZipped + "/" + totalFilesToZip);
            if(filesZipped == totalFilesToZip)
                progressText.setText("Zipping Files");
        });
    }
}
