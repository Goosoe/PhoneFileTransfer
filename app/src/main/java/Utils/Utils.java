package Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Utils {
    //TODO: PUT IN CONFIG FILE (MILLISECONDS)
    public static final long WAIT_CONFIRMATION_TIMEOUT = 10000;
    //MAX SIMULTANEOUS REQUESTS OF DIFFERENT DEVICES
    public static final int MAX_REQUESTS = 5;

    //TODO: magic strings on this function
    public static void askForPermissions(Context context){
        Dexter.withContext(context)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(context.getApplicationContext(),"Please allow access permissions, otherwise the app won't work", Toast.LENGTH_LONG).show();

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(context.getApplicationContext(),"Please allow access permissions, otherwise the app won't work", Toast.LENGTH_LONG).show();}
                }).check();
    }

    public static boolean isConnectedToWifi(Activity activity){
        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(activity.getApplicationContext().CONNECTIVITY_SERVICE);
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public static void closeApp(Activity activity, String message){
        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        activity.finish();
    }


}
