package com.example.cleareyes.localoyeproject;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class PendingTaskFragment extends android.support.v4.app.Fragment {

    View v;
    ArrayList<Task> tasks = new ArrayList<>();
    ListAdapter listAdapter = null;

    public PendingTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = (ListView) getActivity().findViewById(R.id.listView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) v.findViewById(R.id.listView);
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity(), "LocalOye", null ,1);
        tasks = databaseHandler.getAllActiveTasks("pending", false, 0);
        listAdapter = new ListAdapter();

        if(tasks.size() > 0) {
            listView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), CreateTask.class);
                    intent.putExtra("id", tasks.get(position).getTaskId());
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ListView listView = (ListView) v.findViewById(R.id.listView);
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity(), "LocalOye", null ,1);
        listAdapter.notifyDataSetChanged();
    }

    public class ListAdapter extends BaseAdapter {

        ListAdapter() {

        }

        @Override
        public int getCount() {
            return tasks.size();
        }

        @Override
        public Object getItem(int position) {
            return tasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item, parent,false);
            }

            TextView noteTitleTextView = (TextView) convertView.findViewById(R.id.noteTitleTextView);
            TextView noteDateTextView = (TextView) convertView.findViewById(R.id.noteDateTextView);

            noteTitleTextView.setText(tasks.get(position).getTaskTitle());
            long millisecond = Long.parseLong(tasks.get(position).getEndDate().toString());
            Date date = new Date(millisecond);
            String dateString= DateFormat.format("MM/dd", new Date(millisecond)).toString() + "/"+date.getYear();
            noteDateTextView.setText(dateString);

            return convertView;
        }
    }


}
