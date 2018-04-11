package search.flight.com.flightsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import helper.Common_data;
import helper.RestHttpClient;


public class MainActivity extends AppCompatActivity {
   AutoCompleteTextView dep,arrival;
    CalendarView mCalendarView;
    Button next;
    public static String token;
    String[] airport_names = {""};
    String [] airport_codes = {""};
    RelativeLayout internet_layout;
    NestedScrollView scrollView;
    public static String destination,origin,date,origin_code,destination_code;
    Button try_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders interface elements
        setContentView(R.layout.activity_main);
        internet_layout = (RelativeLayout)findViewById(R.id.internet_layout);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);

        //check internet status
        if(Common_data.isInternetOn(MainActivity.this)){
            // if internet is okay retrive data from the webservice
            try {
                getAirportName();
                //getAirLines();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            internet_layout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }


        //assign  layout elements
        init();

    }



    private void init() {


        dep = (AutoCompleteTextView)findViewById(R.id.departure);
        arrival = (AutoCompleteTextView)findViewById(R.id.arrival);
        mCalendarView = (CalendarView) findViewById(R.id.datePicker);
        next = (Button)findViewById(R.id.check);
        try_again =(Button)findViewById(R.id.retry);

       //Removing the first empty String int the aiportname before copying into a new array
        int n=airport_names.length-1;
        final String[] newArray1=new String[n];
        System.arraycopy(airport_names,1,newArray1,0,n);

        //create array adapter and add the airport names into it
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, newArray1);
    //Set the number of characters the user must type before the drop down list is shown
        dep.setThreshold(1);
        //Set the adapter for departure Airport with airport names from the API
        dep.setAdapter(adapter);
        arrival.setThreshold(1);
        //set adapter for arrival airport with Airport names from the API
        arrival.setAdapter(adapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });
        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                internet_layout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                //retry data retrival from the API
                try {
                    getAirportName();
                    //getAirLines();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void getData() {
        destination = arrival.getText().toString();
        origin = dep.getText().toString();
        date = getFormattedDate(new Date(mCalendarView.getSelectedDate().getTime().toString()));


        if(validate()){
            int index = Arrays.asList(airport_names).indexOf(destination);
            int index1 = Arrays.asList(airport_names).indexOf(origin);

            destination_code = airport_codes[index+1];
             origin_code = airport_codes[index1+1];
            System.out.println("Date: "+date+" Origin: "+origin_code+" Dest: "+destination_code);
            Intent i = new Intent(MainActivity.this, AirLineSchedule.class);
//
            startActivity(i);


        }

    }


    public void  getAirportName() throws JSONException {
        JSONArray all= new JSONArray(Common_data.getPreferences(this,"airports"));
        //emptying the arrays before appending new String data
        Arrays.fill(airport_names, null);
        Arrays.fill(airport_codes, null);
        for(int i=0; i<all.length(); i++) {
            JSONObject obj = all.getJSONObject(i);
            String airportcode=obj.getString("AirportCode");
            JSONObject names = obj.getJSONObject("Names");
            Log.d("Names array", names.toString());
            JSONArray name = names.getJSONArray("Name");
            String Airport = null;
            for (int j = 0; j< name.length(); j++){
                JSONObject name_obj = name.getJSONObject(j);
                Airport = name_obj.getString("$");
            }
            //String Airport = name.getString("$");

            System.out.println("Code "+airportcode);
            System.out.println("Name "+Airport);
            //add all airport names and codes into arrays
            airport_names = appendValue(airport_names, Airport);
            airport_codes = appendValue(airport_codes, airportcode);


        }





    }

    private String[] appendValue(String[] airport_names, String airport) {
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(airport_names));
        temp.add(airport);
        return temp.toArray(new String[]{});
    }

    public boolean validate(){
        boolean flag = false;
        if(destination.length() == 0){
           dep.setError("Please select a origin Airport");
        }
         else if (origin.length() == 0) {

            dep.setError("Please select a destination Airport");
        }else if(date.length() == 0){
            Toast.makeText(MainActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
        }

        else {
            flag = true;

        }

        return flag;
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    private void getAirLines() throws UnsupportedEncodingException {


        RestHttpClient.get("references/airlines", new AirLineHandler(),  Common_data.getPreferences(MainActivity.this,"token"));

    }


    private class AirLineHandler extends AsyncHttpResponseHandler {
        @Override
        public void onStart() {
            super.onStart();

        }
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);
                    JSONObject airlineResource = json.getJSONObject("AirlineResource");
                    JSONObject airlinesArray = airlineResource.getJSONObject("Airlines");
                    JSONArray airline = airlinesArray.getJSONArray("Airline");

                    Log.d("Airline Response array", airline.toString());
                    Common_data.setPreference(MainActivity.this,"airlines",airline.toString());


                } catch (Exception e) {
                e.printStackTrace();
                }
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();


        }
        @Override
        @Deprecated
        public void onFailure(Throwable error) {
            super.onFailure(error);
            Log.i("Error", error.getLocalizedMessage());
            Toast.makeText(MainActivity.this, "Error: "+ error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


    }



}
