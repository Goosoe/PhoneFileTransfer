package RequestList;

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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestView> {

    private List<String> localDataSet;
    /**
     * Initialize the dataset of the Adapter.
     *
     * @param localDataSet  List<String> containing the data to populate views to be used
     * by RecyclerView.
     */
    public RequestAdapter(){
        this(null);
    }

    public RequestAdapter(List<String> dataSet) {
        if(dataSet == null)
            localDataSet = new ArrayList<>();
        else
            localDataSet = dataSet;
    }

    public void addItem(String info){
        localDataSet.add(info);
        notifyDataSetChanged();

    }
    public void removeItem(int pos){
        localDataSet.remove(pos);
        notifyDataSetChanged();
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

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        requestView.getTextView().setText(localDataSet.get(position));
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
        private final TextView textView;
        private final ImageButton denyConn;
        private final ImageButton acceptConn;
        private View view;
        private RequestAdapter adapter;
        public RequestView(View view, RequestAdapter adapter) {
            super(view);
            this.view = view;
            this.adapter = adapter;
            textView = (TextView) view.findViewById(R.id.textView);
            denyConn = (ImageButton) view.findViewById(R.id.denyConn);
            acceptConn = (ImageButton) view.findViewById(R.id.acceptConn);

            prepareButtons();
        }

        public TextView getTextView() {
            return textView;
        }

        private void prepareButtons(){
            denyConn.setOnClickListener(listener -> removeFromList());
            acceptConn.setOnClickListener(listener -> notifyServer());
        }

        private void notifyServer() {
            Toast.makeText(view.getContext(), "accepted", Toast.LENGTH_SHORT).show();
            // notifyServer(this);
        }

        private void removeFromList() {
            adapter.removeItem(this.getAdapterPosition());
            Toast.makeText(view.getContext(), "denied " + this.getItemId(), Toast.LENGTH_SHORT).show();
        }
    }
}
