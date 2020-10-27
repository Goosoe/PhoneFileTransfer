package SillyGoose.phonefiletransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.LinkedList;

import FileNavigator.FileRecyclerAdapter;
import FileNavigator.ListElementData;
import Server.StartServerActivity;
import Utils.UriUtils;

public class MainActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ask permissions
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getApplicationContext(),"Lets get started", Toast.LENGTH_LONG).show();

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(),"Bruh", Toast.LENGTH_LONG).show();

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();



        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        LinkedList<String> filesPaths = new LinkedList<>();
        switch(action){
            case Intent.ACTION_SEND:
                Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

                if (uri != null) {

                    filesPaths.add(UriUtils.getPathFromUri(this,uri));

                    String[] itemsArray = new String[filesPaths.size()];
                    itemsArray = filesPaths.toArray(itemsArray);
                    startServer(itemsArray, this);

                    // Update UI to reflect multiple images being shared
                }
                break;

            case Intent.ACTION_SEND_MULTIPLE:
                ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if (uris != null) {
                    for (Uri fileUri : uris){
                        filesPaths.add(UriUtils.getPathFromUri(this, fileUri));
                    }

                    String[] itemsArray = new String[filesPaths.size()];
                    itemsArray = filesPaths.toArray(itemsArray);
                    startServer(itemsArray, this);
                    // Update UI to reflect multiple images being shared
                }
                break;

//            case Intent.ACTION_DEFAULT:
//                break;
        }
        Button start = findViewById(R.id.startButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                findViewById(R.id.navFrag).findViewById(R.id.nab)

                //get fragment's selected files Strings

//                String[] filesToUpload = ((NavigatorFragment) getFragmentManager().findFragmentById(R.id.navFrag)).getSelectedFiles();
                startServer(FileRecyclerAdapter.getSelectedIcons(), v.getContext());
//                Fragment f = getFragmentManager().findFragmentById(R.id.navFrag).;

                //.getSelectedFiles();
                //getSelected
                //startServer

            }
        });

    }

    private void startServer(String[] filesToUpload, Context c) {
        if(filesToUpload.length > 0 ) {
            Intent serverStart = new Intent(c, StartServerActivity.class);
            serverStart.putExtra("filePaths", filesToUpload);
            startActivity(serverStart);
        }
        else {
            Toast.makeText(getApplicationContext(),"You don't have any files chosen to send", Toast.LENGTH_LONG).show();
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            System.out.println("Got text");
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            System.out.println("Got image");
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            System.out.println("Got images");
            // Update UI to reflect multiple images being shared
        }
    }
//    public String[] getSelectedFiles(){
//        ArrayList<String> paths = new ArrayList<>();
//        for(FileRecyclerAdapter.ViewHolder v : FileRecyclerAdapter.viewHolders){
//            if(v.checkBox.isChecked()){
//                paths.add(v.mItem.filePath);
//            }
//        }
//        String[] result = new String[paths.size()];
//        return paths.toArray(result);
//

    //    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
    @Override
    protected void onResume() {
        super.onResume();

    }

}