package RequestList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import SillyGoose.phonefiletransfer.R;
import SillyGoose.phonefiletransfer.ServerActivity;
import Utils.Utils;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestView> {

    private List<Utils.Tuple<String, String>> localDataSet;
    private final static int MAX_REQUESTS = 10;
    /**
     * Initialize the dataset of the Adapter.
     *
     */
    public RequestAdapter(){
        localDataSet = new ArrayList<>();
    }

    /**
     * Adds an item from the RecyclerViewList
     * @param hostname -
     * @param ip
     * @param activity - current activity
     */
    public void addItem(String hostname, String ip, Activity activity){
        localDataSet.add(new Utils.Tuple<>(hostname, ip));
        int currentPos = localDataSet.size()- 1;
        //this needs activity because otherwise it has no access to an ui thread to update
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemInserted(currentPos);
            }
        });

    }

    /**
     * Removes an item from the RecyclerViewList
     * @param pos
     * @param activity
     */
    public void removeItem(int pos, Activity activity){
        localDataSet.remove(pos);
        //this needs activity because otherwise it has no access to an ui thread
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(pos);
            }
        });
    }
    protected void removeItem(int pos){
        localDataSet.remove(pos);
        //this does not need activity since its invoked by RequestView, which has access to ui thread.
        notifyItemRemoved(pos);
    }



    // Create new views (invoked by the layout manager)
    @Override
    public RequestView onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.request_list_item, viewGroup, false);
        return new RequestView(view, this);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RequestView requestView, final int position) {
        requestView.prepareView(localDataSet.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class RequestView extends RecyclerView.ViewHolder {
        private Utils.Tuple<String,String> info;
        private final TextView textView;
        private final ImageButton denyConn;
        private final ImageButton acceptConnBt;
        private View mainView;
        private RequestAdapter adapter;
        private boolean acceptedConnection;
        public RequestView(View view, RequestAdapter adapter) {
            super(view);
            this.mainView = view;
            this.adapter = adapter;
            acceptedConnection = false;
            textView = (TextView) view.findViewById(R.id.textView);
            denyConn = (ImageButton) view.findViewById(R.id.denyConn);
            acceptConnBt = (ImageButton) view.findViewById(R.id.acceptConn);
            prepareButtons();
        }

        public TextView getTextView() {
            return textView;
        }

        public boolean getAcceptedConnection(){
            return acceptedConnection;
        }
        private void prepareButtons(){
            denyConn.setOnClickListener(listener -> removeFromList());
            acceptConnBt.setOnClickListener(listener -> acceptConnection());
        }

        private void acceptConnection() {
            acceptedConnection = true;
            ((ServerActivity) mainView.getContext()).notifyServer(info);
            removeFromList();
            // notifyServer(this);
        }

        private void removeFromList() {
            adapter.removeItem(this.getAdapterPosition());
        }

        public void prepareView(Utils.Tuple<String, String> tuple) {
            info = tuple;
            String text = tuple.getVal1() + "\n" + tuple.getVal2();
            textView.setText(text);

        }
    }
}
