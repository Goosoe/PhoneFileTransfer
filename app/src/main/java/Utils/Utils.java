package Utils;

import android.Manifest;
import android.content.Context;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Utils {
    //TODO: static strings on this function
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

    public static class Tuple<A,B>{
        private A val1;
        private B val2;
        public Tuple(A val1, B val2){
            this.val1 = val1;
            this.val2 = val2;
        }

        public A getVal1(){
            return val1;
        }

        public B getVal2(){
            return val2;
        }
    }
}
