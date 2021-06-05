package RequestList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import Server.REQUEST_RESPONSE_TYPE;
import SillyGoose.phonefiletransfer.R;
import SillyGoose.phonefiletransfer.ServerActivity;
import Utils.Utils;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestView> {

    private List<RequestInfo> localDataSet;
    private Context context;
    /**
     * Initialize the dataset of the Adapter.
     *
     */
    public RequestAdapter(Context context){
        this.context = context;
        this.localDataSet = new ArrayList<>();
    }


    public void addItem(RequestInfo request, Activity activity){
        localDataSet.add(request);
        int currentPos = localDataSet.size()- 1;

        //TODO: visual timeout of the card needs a different solution
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(Utils.WAIT_CONFIRMATION_TIMEOUT);
                if (request.getServeThread().isAlive()) {
                    request.setResponseType(REQUEST_RESPONSE_TYPE.TIMEOUT);
                    request.getServeThread().interrupt();
                }
                removeItem(currentPos);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            removeItem(currentPos);
        });
        activity.runOnUiThread(() -> notifyItemInserted(currentPos));
    }

    /**
     * Removes an item from the RecyclerViewList
     * @param pos
     */
    protected void removeItem(int pos){
        localDataSet.remove(pos);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(pos);
            }
        });
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
        private RequestInfo request;
        private final TextView textView;
        private final ImageButton denyConn;
        private final ImageButton acceptConnBt;
        private View mainView;
        private RequestAdapter adapter;

        public RequestView(View view, RequestAdapter adapter) {
            super(view);
            this.mainView = view;
            this.adapter = adapter;
            textView = (TextView) view.findViewById(R.id.textView);
            denyConn = (ImageButton) view.findViewById(R.id.denyConn);
            acceptConnBt = (ImageButton) view.findViewById(R.id.acceptConn);
            prepareButtons();
        }

        public TextView getTextView() {
            return textView;
        }

        private void prepareButtons(){
            denyConn.setOnClickListener(listener -> denyConnection());
            acceptConnBt.setOnClickListener(listener -> acceptConnection());
        }

        private void acceptConnection() {
            answerRequest(REQUEST_RESPONSE_TYPE.ACCEPTED);
        }

        private void denyConnection(){
            answerRequest(REQUEST_RESPONSE_TYPE.DENIED);
        }

        public void prepareView(RequestInfo request) {
            this.request = request;
            String text = request.getHostname() + "\n" + request.getIp();
            textView.setText(text);
        }

        private void answerRequest(REQUEST_RESPONSE_TYPE value) {
            if (request.getServeThread().isAlive()) {
                request.setResponseType(value);
                request.getServeThread().interrupt();
            }
            adapter.removeItem(this.getBindingAdapterPosition());
        }
    }
}
