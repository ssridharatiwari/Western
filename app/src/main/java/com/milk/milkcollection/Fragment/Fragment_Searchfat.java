package com.milk.milkcollection.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import java.util.ArrayList;

/**
 * Created by Alpha on 12-01-2016.
 */
public class Fragment_Searchfat extends Fragment {
    private ListView searchfat;
    private  String fatSnf;
    private int fatSnfId,fatSnfPosition;
    private  ArrayAdapter<String> dataAdapter;

    public Fragment_Searchfat() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_searchfat, container, false);
        searchfat = (ListView)rootView.findViewById(R.id.saved_listview);
        searchfat.setEmptyView(rootView.findViewById(R.id.empty_saved_list));
        searchfat.setOnCreateContextMenuListener(this);
        ArrayList<String> savedfatlist = new ArrayList<>();

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
        savedfatlist=milkDBHelpers.searchfatsnf();

        dataAdapter = new ArrayAdapter<String>(getActivity(),R.layout.blacktext, savedfatlist);
        searchfat.setAdapter(dataAdapter);


        searchfat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
                ArrayList<Integer> savedfatIdList = new ArrayList<>();
                savedfatIdList = milkDBHelpers.searchfatsnfId();
                fatSnfId = savedfatIdList.get(position);
                Log.e("-=-=-item=-=", String.valueOf(fatSnfId));
                fatSnfPosition = position;

                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setMessage("Are You sure want to delete this Fat,Snf ");
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
                        Log.e("-=-=-ddd=-=", "ddddd");
                        milkDBHelpers.fatSnf_delete(fatSnfId);
                        dataAdapter.remove((dataAdapter.getItem(fatSnfPosition)));
                        dataAdapter.notifyDataSetChanged();
                    }
                });
                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                final AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        return rootView;
    }
}