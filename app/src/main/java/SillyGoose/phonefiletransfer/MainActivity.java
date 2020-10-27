package SillyGoose.phonefiletransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import FileNavigator.FileRecyclerAdapter;
import FileNavigator.IconData;
import Server.StartServerActivity;

public class MainActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = (Button) findViewById(R.id.startButton);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                findViewById(R.id.navFrag).findViewById(R.id.nab)

               //get fragment's selected files Strings

//                String[] filesToUpload = ((NavigatorFragment) getFragmentManager().findFragmentById(R.id.navFrag)).getSelectedFiles();
                IconData[] filesToUpload = FileRecyclerAdapter.getSelectedIcons();
//                Fragment f = getFragmentManager().findFragmentById(R.id.navFrag).;
           
                //.getSelectedFiles();
                //getSelected
                //startServer
                if(filesToUpload.length > 0 ) {
                    Intent serverStart = new Intent(v.getContext(), StartServerActivity.class);
                    serverStart.putExtra("IconData", filesToUpload);
                    startActivity(serverStart);
                }
                else {
                    Toast warning = Toast.makeText(getApplicationContext(),"You don't have any files chosen to send", Toast.LENGTH_LONG);
                    warning.show();
                }
            }
        });

        //Ask permissions
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) { }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();


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
//    }
public String bruh(){
    return "coconut";
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

}