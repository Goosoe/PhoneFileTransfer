package FileNavigator;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

//import FileNavigator.ListIcon.ListIcon.IconData;
import SillyGoose.phonefiletransfer.R;

import java.io.File;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link IconData}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FileRecyclerAdapter extends RecyclerView.Adapter<FileRecyclerAdapter.ViewHolder> {

    private final List<IconData> mValues;
    private static ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(int position, View v);
//        void onItemLongClick(int position, View v);
    }


    public FileRecyclerAdapter(List<IconData> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nav_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
        System.out.println("bruh " + mValues.get(position).filePath);
        File f = new File(mValues.get(position).filePath);
        if(f != null)
            Picasso.get().load(f).resize(200,200).into(holder.imageView);
        else
            Picasso.get().invalidate(f);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }





    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView imageView;
        public IconData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            imageView = (ImageView) view.findViewById(R.id.imageView);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        public ImageView getImageView() {
            return imageView;
        }


        //        @Override
//        public boolean onLongClick(View v) {
//            clickListener.onItemLongClick(getAdapterPosition(), v);
//            return true;
//        }
    }

//    public class onClickListener interface View.OnClickListener {
//        @Override
//        void onClick(View v){}
//    }

}