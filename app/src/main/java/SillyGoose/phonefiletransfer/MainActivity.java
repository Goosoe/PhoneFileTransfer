package SillyGoose.phonefiletransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import FileNavigator.FileRecyclerAdapter;
import FileNavigator.dummy.FileContent;
import Server.StartServerActivity;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private FileRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<FileContent.FileData> fileDataList;
    private String currentPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        LinearLayout ll = (LinearLayout)  findViewById(R.id.fileNav);
        fileDataList = new ArrayList<>();
        currentPath = null;

        //Ask permissions
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) { }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

        recyclerView = (RecyclerView) findViewById(R.id.listView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        updateListToGetFile(null);
        mAdapter = new FileRecyclerAdapter(fileDataList);
        mAdapter.setOnItemClickListener(new FileRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                updateRecyclerView(position, mAdapter);
            }
//
//            @Override
//            public void onItemLongClick(int position, View v) {
//                getFolder(position);
//            }

        });
        recyclerView.setAdapter(mAdapter);




    }

    private void getFolder(int position) {
        if(updateListToGetFile(fileDataList.get(position).id)) {
            Intent serverStart = new Intent(this, StartServerActivity.class);
            serverStart.putExtra("FilePath", currentPath.concat(File.separator).concat(fileDataList.get(position).id));
            startActivity(serverStart);
        }
    }


    private void updateRecyclerView(int position, FileRecyclerAdapter adapter) {
        if(updateListToGetFile(fileDataList.get(position).id)) {
            Intent serverStart = new Intent(this, StartServerActivity.class);
            serverStart.putExtra("FilePath", currentPath);
            startActivity(serverStart);
        }
        else{
            adapter.notifyDataSetChanged();
        }

    }


    private boolean updateListToGetFile(String fileName){

        if(currentPath == null)
            currentPath = Environment.getExternalStorageDirectory().toString();

        if(fileName != null){
            if(fileName.equals("...")) {
                currentPath = this.currentPath.replaceAll("^(.*)/.*?$", "$1");
            }
            else{
                currentPath += File.separator + fileName;
            }
//            System.out.println("DEBUG?: " + currentPath.concat(File.separator).concat(fileName));


        }

//                getExternalStoragePublicDirectory(Environment.).toString();
//        if (getIntent().hasExtra("path")) {
//            currentPath = getIntent().getStringExtra("path");
//        }
        setTitle(currentPath);
        fileDataList.clear();
        if(currentPath.length() > Environment.getExternalStorageDirectory().toString().length())
            fileDataList.add(new FileContent.FileData("...","...","..."));
        // Read all files sorted into the values-array
        List<String> values = new LinkedList<>();

        File dir = new File(currentPath);
        if(dir.isFile()){
            return true;
        }
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(file);
                }
            }
        }
        Collections.sort(values);
        for(String name : values)
            fileDataList.add(new FileContent.FileData(name, name, name));
        return false;
    }


    //    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
    @Override
    protected void onResume() {
        super.onResume();
    }

//    private class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
//        public RecyclerItemClickListener(Object p0, RecyclerView recyclerView, RecyclerItemClickListener.OnItemClickListener onItemClickListener) {
//        }
//    }
}