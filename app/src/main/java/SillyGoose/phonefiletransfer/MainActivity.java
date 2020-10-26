package SillyGoose.phonefiletransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import FileNavigator.FileRecyclerAdapter;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private FileRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
//    private List<ListIcon.IconData> iconDataList;
//    private String currentPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        LinearLayout ll = (LinearLayout)  findViewById(R.id.fileNav);
//        iconDataList = new ArrayList<>();
//        currentPath = null;

        //Ask permissions
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) { }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

//        Bundle args = new Bundle();
//        args.putParcelable("my_custom_object", ListIcon.IconData);
//        NavigatorFragment fragment = NavigatorFragment.newInstance(1);
////        fragment.setArguments(args);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.navFrag, fragment);
//        transaction.commit();


//        recyclerView = (RecyclerView) findViewById(R.id.listView);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        recyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        // specify an adapter (see also next example)
//        updateListToGetFile(null);
//        mAdapter = new FileRecyclerAdapter(iconDataList);
//        mAdapter.setOnItemClickListener(new FileRecyclerAdapter.ClickListener() {
//            @Override
//            public void onItemClick(int position, View v) {
//                updateRecyclerView(position, mAdapter);
//            }
////
////            @Override
////            public void onItemLongClick(int position, View v) {
////                getFolder(position);
////            }
//
//        });
//        recyclerView.setAdapter(mAdapter);
//



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