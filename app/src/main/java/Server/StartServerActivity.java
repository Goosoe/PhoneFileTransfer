package Server;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;

import FileNavigator.ListElementData;
import SillyGoose.phonefiletransfer.R;

public class StartServerActivity extends AppCompatActivity {

    private static String ip = "localhost";
    private static final int PORT = 8080;
    private HttpServer server = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Starting server");
        setContentView(R.layout.activity_start_server);


        ListElementData[] files = (ListElementData[]) getIntent().getSerializableExtra("IconData");
//        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

//        // Capture the layout's TextView and set the string as its text
//        TextView textView = findViewById(R.id.);
//        textView.setText(message);

        ip = Utils.getIPAddress(true);
        TextView ipText = (TextView) findViewById(R.id.ipText);
        ipText.setText(ip + ":" + PORT);
        if(server == null) {
            server = new HttpServer(ip, PORT, this, Arrays.asList(files));
        }
//        try {
//            server.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        server.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!server.isAlive()) {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}