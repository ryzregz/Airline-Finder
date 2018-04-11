package search.flight.com.flightsearch;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import helper.Common_data;
import helper.RestHttpClient;

/**
 * Created by mac on 2/27/18.
 */

public class Route extends AppCompatActivity {
    Toolbar toolbar;
    Polyline polyline1;
    Double origin_lat,origin_long,destination_lat,destination_long;
    SupportMapFragment mapFragment;
    protected GoogleMap map;
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.route);
        //set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpMapIfNeeded();


        System.out.println("Origin Code: "+AirLineSchedule.origin+" Dest code: "+AirLineSchedule.destination);
    }

    private void setUpMapIfNeeded() {
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // Retrieve the Airport Data data from the web service
                    getOriginLatLongFromAPI(AirLineSchedule.origin);
                    getLatLongFromAPI(AirLineSchedule.destination);


    }

    private void getOriginLatLongFromAPI(String origin) {
        RestHttpClient.get("references/airports/"+origin, new OriginAirportHandler(origin),  Common_data.getPreferences(Route.this,"token"));
    }

    public class OriginAirportHandler extends AsyncHttpResponseHandler {
        ProgressDialog pdialog;
        String airport_code;
        String type;

        public OriginAirportHandler(String code) {
            this.airport_code =code;
        }

        @Override
        public void onStart() {
            super.onStart();

            pdialog = ProgressDialog.show(Route.this, "", "Loading...");

        }
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);
                    JSONObject airportResource = json.getJSONObject("AirportResource");
                    JSONObject airportsArray = airportResource.getJSONObject("Airports");
                    JSONObject airport = airportsArray.getJSONObject("Airport");
                    Log.d("AirportData", airport.toString());

                    String airportcode=airport.getString("AirportCode");

                    JSONObject position = airport.getJSONObject("Position");
                    JSONObject coordinates = position.getJSONObject("Coordinate");

                    origin_lat = coordinates.getDouble("Latitude");
                    origin_long = coordinates.getDouble("Longitude");


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

        }


    }

    public void getLatLongFromAPI(String code) {
        RestHttpClient.get("references/airports/"+code, new AirportHandler(code),  Common_data.getPreferences(Route.this,"token"));

    }
    public class AirportHandler extends AsyncHttpResponseHandler {
        ProgressDialog pd;
        String airport_code;
        String type;

        public AirportHandler(String code) {
            this.airport_code =code;
        }

        @Override
        public void onStart() {
            super.onStart();

            pd = ProgressDialog.show(Route.this, "", "Loading...");

        }
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);
                    JSONObject airportResource = json.getJSONObject("AirportResource");
                    JSONObject airportsArray = airportResource.getJSONObject("Airports");
                    JSONObject airport = airportsArray.getJSONObject("Airport");
                    Log.d("AirportData", airport.toString());

                        String airportcode=airport.getString("AirportCode");

                            JSONObject position = airport.getJSONObject("Position");
                            JSONObject coordinates = position.getJSONObject("Coordinate");

                                destination_lat = coordinates.getDouble("Latitude");
                                destination_long = coordinates.getDouble("Longitude");



                    runOnUiThread(new Runnable() {
                        public void run() {

                                createPolyLines();

                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (pd.isShowing())
                pd.dismiss();



        }
        @Override
        @Deprecated
        public void onFailure(Throwable error) {
            super.onFailure(error);
            Log.i("Error", error.getLocalizedMessage());

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void createPolyLines() {
        System.out.println("Origin Lat: "+origin_lat+"Origin Long: "+origin_long+" Dest Lat: "+destination_lat+" Dest Long: "+destination_long);
        if(String.valueOf(origin_lat) == null || String.valueOf(origin_long) == null || String.valueOf(destination_lat) == null || String.valueOf(destination_long)==null){
            Toast.makeText(this, "Route Data not Found", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng startLatLng = new LatLng(origin_lat, origin_long);
        LatLng endLatLng = new LatLng(destination_lat, destination_long);
        //set markers at the start and end point
        map.addMarker(new MarkerOptions().position(endLatLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.landing_airplane)));
       map.addMarker(new MarkerOptions().position(startLatLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.airplane_symbol)));
        // Add polylines and polygons to the map.
      polyline1 = map.addPolyline(new PolylineOptions() .add(
           new LatLng(origin_lat, origin_long), new LatLng(destination_lat, destination_long)).width(5)
              .color(Color.RED)
              .geodesic(true));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(origin_lat, origin_long), 10));


    }





}
