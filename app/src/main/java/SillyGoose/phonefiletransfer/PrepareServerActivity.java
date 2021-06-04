package SillyGoose.phonefiletransfer;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import RequestList.RequestAdapter;
import Server.ServerUtils;
import Utils.UriUtils;

public class PrepareServerActivity extends Activity {

    private String outputZipPath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.prepare_server_layout);
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        executorService.submit(() -> {
            outputZipPath = prepareZip(Arrays.asList(checkReceivedIntent()));
            notifyServerReady();
        });

    }

    private void notifyServerReady() {
        Intent intent = new Intent(this, ServerActivity.class);
        intent.putExtra("outputZipPath", outputZipPath);
        startActivity(intent);
        finish();
    }

//    private class StartServerService extends JobIntentService {
//        static final int JOB_ID = 1000;
//        private String zipPath;
//        public StartServerService () {
//            super();
//        }
//
//        @Override
//        protected void onHandleWork(@NonNull Intent intent) {
//        }
//
//
//        void enqueueWork(Context context, Intent intent) {
//            enqueueWork(context, StartServerService.class, JOB_ID, intent);
//        }
//



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
        if (uris != null) {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
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
}
