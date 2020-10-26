package FileNavigator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import Server.StartServerActivity;
import SillyGoose.phonefiletransfer.R;
//import FileNavigator.ListIcon.ListIcon;

/**
 * A fragment representing a list of Items.
 */
public class NavigatorFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private List<IconData> iconDataList;
    private String currentPath;
    private FileRecyclerAdapter rAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NavigatorFragment() {
        iconDataList = new ArrayList<>();
        currentPath = null;
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NavigatorFragment newInstance(int columnCount) {
        NavigatorFragment fragment = new NavigatorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nav_item_list, container, false);
        updateListToGetFile(currentPath);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            rAdapter = new FileRecyclerAdapter(iconDataList);
                      recyclerView.setAdapter(rAdapter);

            rAdapter.setOnItemClickListener(new FileRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                updateRecyclerView(position, rAdapter);
            }
//
//            @Override
//            public void onItemLongClick(int position, View v) {
//                getFolder(position);
//            }

        });
        }
        return view;
    }

    private void getFolder(int position) {
        if(updateListToGetFile(iconDataList.get(position).id)) {
            Intent serverStart = new Intent(getActivity(), StartServerActivity.class);
            serverStart.putExtra("FilePath", currentPath.concat(File.separator).concat(iconDataList.get(position).id));
            startActivity(serverStart);
        }
    }


    private void updateRecyclerView(int position, FileRecyclerAdapter adapter) {
        if(updateListToGetFile(iconDataList.get(position).id)) {
            Intent serverStart = new Intent(getActivity(), StartServerActivity.class);
            serverStart.putExtra("FilePath", currentPath);
            startActivity(serverStart);
        }
        else{
            adapter.notifyDataSetChanged();
        }

    }


    private boolean updateListToGetFile(String fileName){

        if(currentPath == null)
            currentPath = Environment.getExternalStorageDirectory().toString();

        if(fileName != null){
            if(fileName.equals("...")) {
                currentPath = this.currentPath.replaceAll("^(.*)/.*?$", "$1");
            }
            else{
                currentPath += File.separator + fileName;
            }
//            System.out.println("DEBUG?: " + currentPath.concat(File.separator).concat(fileName));


        }

//                getExternalStoragePublicDirectory(Environment.).toString();
//        if (getIntent().hasExtra("path")) {
//            currentPath = getIntent().getStringExtra("path");
//        }
        getActivity().setTitle(currentPath);
        iconDataList.clear();
        if(currentPath.length() > Environment.getExternalStorageDirectory().toString().length())
            iconDataList.add(new IconData("...","...",""));
        // Read all files sorted into the values-array
        List<String> values = new LinkedList<>();

        File dir = new File(currentPath);
        if(dir.isFile()){
            return true;
        }
        if (!dir.canRead()) {
            getActivity().setTitle(getActivity().getTitle() + " (inaccessible)");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(file);
                }
            }
        }
        Collections.sort(values);
        for(String name : values)
            iconDataList.add(new IconData(name, name, currentPath.concat(File.separator).concat(name)));
        return false;
    }
}