package search.flight.com.flightsearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import adapter.ScheduleAdapter;
import helper.Common_data;
import helper.RestHttpClient;
import model.Schedule;

/**
 * Created by mac on 2/26/18.
 */

public class AirLineSchedule  extends AppCompatActivity implements AdapterView.OnItemClickListener {
    RecyclerView recyclerView;
    ArrayList<Schedule> scheduleList = new ArrayList<>();
   ScheduleAdapter mAdapter;
  public static String origin,destination;
    ListView listView;
    Toolbar toolbar;
    Context context = AirLineSchedule.this;
    RelativeLayout internet_layout, recylerlayout;
    Button try_again;
    TextView title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders interface elements
        setContentView(R.layout.activity_schedule);
        //set up toolbar and enable navigation
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //assign  layout elements
        listView = (ListView) findViewById(R.id.list);
        internet_layout = (RelativeLayout)findViewById(R.id.internet_layout);
        recylerlayout = (RelativeLayout)findViewById(R.id.recycler_ly);
        try_again =(Button)findViewById(R.id.retry);
        //assigning API Response data to the Adapter
        mAdapter = new ScheduleAdapter(this, scheduleList);

        //check internet status
        if(Common_data.isInternetOn(AirLineSchedule.this)){
            // if internet is okay retrive data from the webservice
            getScheduleData();
        }else{
            internet_layout.setVisibility(View.VISIBLE);
            recylerlayout.setVisibility(View.GONE);
        }

        title = (TextView)findViewById(R.id.page_title);
        title.setText("Flight Schedules from "+MainActivity.origin+" to "+MainActivity.destination);

        listView.setOnItemClickListener(this);
        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                internet_layout.setVisibility(View.GONE);
                recylerlayout.setVisibility(View.VISIBLE);
                //retry data retrival
                getScheduleData();

            }
        });

    }


    private void getScheduleData() {

      //Rest API call
        RestHttpClient.get("operations/schedules/"+MainActivity.origin_code+"/"+MainActivity.destination_code+"/"+MainActivity.date, new ScheduleHandler(), Common_data.getPreferences(AirLineSchedule.this, "token"));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent=new Intent(AirLineSchedule.this, Route.class);
        startActivity(intent);
    }

    private class ScheduleHandler extends AsyncHttpResponseHandler {
        ProgressDialog pdialog;
        @Override
        public void onStart() {
            super.onStart();
            pdialog = ProgressDialog.show(AirLineSchedule.this, "", "Loading...");

        }
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);
            //Log.i("Schedule Response", content);

            if (content.length() > 0) {

                try {
                    //API Response Json Parsing
                    JSONObject json = new JSONObject(content);
                    JSONObject scheduleResource = json.getJSONObject("ScheduleResource");
                    JSONArray sched = scheduleResource.getJSONArray("Schedule");
                    Log.i("Schedule  ", sched.toString());

                   Log.i("Length of first array", String.valueOf(sched.length()));

                    for(int i=0; i<sched.length(); i++) {


                        JSONObject obj1 = sched.getJSONObject(i);
                        JSONArray flight = obj1.getJSONArray("Flight");
                        Log.i("Flight  ", flight.toString());
                        Log.i("Length of Second array", String.valueOf(flight.length()));

                        for (int j = 0; j < flight.length(); j++) {
                            Schedule sch = new Schedule();
                            JSONObject obj = flight.getJSONObject(j);
                            JSONObject departure = obj.getJSONObject("Departure");
                            String airportcode = departure.getString("AirportCode");
                            JSONObject departuretime = departure.getJSONObject("ScheduledTimeLocal");
                            String actualtime = departuretime.getString("DateTime");

                            JSONObject arrival = obj.getJSONObject("Arrival");
                            String arrival_airportcode = arrival.getString("AirportCode");
                            JSONObject arrivaltime = arrival.getJSONObject("ScheduledTimeLocal");
                            String actualarrivaltime = arrivaltime.getString("DateTime");

                            JSONObject carrier = obj.getJSONObject("MarketingCarrier");
                            String airlineID = carrier.getString("AirlineID");
                            String flightNo = carrier.getString("FlightNumber");
                            sch.setFrom(airportcode);
                            sch.setTo(arrival_airportcode);


                            System.out.println("Departure Airport: " + airportcode + "Arrival Airport: " + arrival_airportcode + "Depature Time: " + actualtime
                                    + "Arrival Time : " + actualarrivaltime + "Airline: " + airlineID + "Flight No: " + flightNo);


                            sch.setTime_from(actualtime+ " -");
                            sch.setTime_to(actualarrivaltime);
                            sch.setName("Airline: " +airlineID);
                            sch.setFlight_no(flightNo);

                            scheduleList.add(sch);
                        }



                    }
                    Log.i("No of items",String.valueOf(scheduleList.size()));
                    mAdapter.notifyDataSetChanged();
                    listView.setAdapter(mAdapter);


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (pdialog.isShowing())
                pdialog.dismiss();


        }
        @Override
        @Deprecated
        public void onFailure(Throwable error) {
            super.onFailure(error);
            Log.i("Error", error.getLocalizedMessage());
            //show API Error Response to user
            Toast.makeText(AirLineSchedule.this, "Error: "+ error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}
