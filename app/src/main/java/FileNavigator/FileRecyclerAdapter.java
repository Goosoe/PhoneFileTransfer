package FileNavigator;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

//import FileNavigator.ListIcon.ListIcon.IconData;
import SillyGoose.phonefiletransfer.R;

import java.io.File;
import java.util.HashSet;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ListElementData}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FileRecyclerAdapter extends RecyclerView.Adapter<FileRecyclerAdapter.ViewHolder>{

    private final List<ListElementData> mValues;
    private static ClickListener clickListener;
    private static HashSet<String> selectedIcon;
    private View view;

    public interface ClickListener {
        void onItemClick(int position, View v);
//        void onItemLongClick(int position, View v);
    }


    public FileRecyclerAdapter(List<ListElementData> items) {
        mValues = items;
        selectedIcon = new HashSet<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        System.out.println("parent " + ((MainActivity) parent.getRootView().getContext());
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nav_item, parent, false);
//        viewHolders = new ArrayList<>();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        viewHolders.add(holder);
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).fileName);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    selectedIcon.add(holder.mItem.filePath);
                }
                else{
                    selectedIcon.remove(holder.mItem.filePath);
                }
            }
        });
        holder.checkBox.setChecked(selectedIcon.contains(holder.mItem.filePath));

        if(mValues.get(position).fileName.equals(NavigatorFragment.BACK_SYMBOL)) {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }
        else{
            holder.checkBox.setVisibility(View.VISIBLE);

        }

        File f = new File(mValues.get(position).filePath);
        if(f != null) {
           float elemSize = view.getResources().getDimension(R.dimen.list_element_height);
           float size = elemSize - view.getResources().getDimension(R.dimen.list_element_image_margin);
            Picasso.get().load(f).resize((int)size, (int)size).centerCrop().into(holder.imageView);
        }
        else
            Picasso.get().invalidate(f);
    }

    public void clearSelectedFiles(){
        selectedIcon.clear();
    }

    public static String[] getSelectedIcons() {
        String[] array = new String[selectedIcon.size()];
        selectedIcon.toArray(array);
        selectedIcon.clear();
        return array;
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
//        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView imageView;
        public final CheckBox checkBox;
        public ListElementData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
//            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            imageView = (ImageView) view.findViewById(R.id.NavItemImageView);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        }

        @Override
        public String toString() {
            return super.toString() + "" + mContentView.getText() + "";
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