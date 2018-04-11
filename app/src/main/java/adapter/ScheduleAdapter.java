package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import model.Schedule;
import search.flight.com.flightsearch.AirLineSchedule;
import search.flight.com.flightsearch.R;
import search.flight.com.flightsearch.Route;

/**
 * Created by mac on 2/27/18.
 */

public class ScheduleAdapter extends BaseAdapter {
    private ArrayList<Schedule> scheduleList;
    Context context;





    public class MyViewHolder  {

        public TextView name, time, from,to;
        public TextView time_to;
        public TextView flighNo;
        public Button view_route;

    }
    public ScheduleAdapter(Context context, ArrayList<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
        this.context=context;

    }

    public View getView(final int position, View view, ViewGroup parent) {
        MyViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = mInflater.inflate(R.layout.flight_schedule_item, null);
            holder = new MyViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.time = (TextView) view.findViewById(R.id.time_from);
            holder.from = (TextView) view.findViewById(R.id.from);
            holder.to = (TextView) view.findViewById(R.id.to);
            holder.time_to = (TextView) view.findViewById(R.id.time_to);
            holder.flighNo = (TextView) view.findViewById(R.id.flight_no);
            holder.view_route = (Button)view.findViewById(R.id.view_route);

            view.setTag(holder);
        } else {
            holder = (MyViewHolder) view.getTag();
        }

        Schedule schedule = scheduleList.get(position);
        holder.name.setText(schedule.getName());
        holder.time.setText("Departure Time: "+schedule.getTime_from());
        holder.time_to.setText("Arrival Time: "+schedule.getTime_to());
        holder.from.setText(schedule.getFrom());
        holder.to.setText(schedule.getTo());
        holder.flighNo.setText("Flight No: "+schedule.getFlight_no());
        holder.view_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Schedule schedule1 = (Schedule) getItem(position);
                context.startActivity(new Intent(context, Route.class));
                AirLineSchedule.origin = schedule1.getFrom();
                AirLineSchedule.destination = schedule1.getTo();
            }
        });




        return view;

    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int position) {
        return scheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return scheduleList.indexOf(getItem(position));
    }


}
