package search.flight.com.flightsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import helper.AppController;
import helper.Common_data;
import helper.RestHttpClient;

/**
 * Created by mac on 2/26/18.
 */

public class SplashScreen extends AppCompatActivity {
    Button proceed;
    public static String token;
    boolean ready = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders interface elements
        setContentView(R.layout.splash_screen);


        if(Common_data.isInternetOn(SplashScreen.this)){
            //generate token that will be used to make api calls
            generateToken();
        }else{
            ready = false;
            Toast.makeText(this, "Please check your Internet connection", Toast.LENGTH_SHORT).show();
        }

        //initialize layout elements
        init();

    }

    private void init() {
        proceed = (Button)findViewById(R.id.proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ready ){
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                }else{
                        Toast.makeText(SplashScreen.this, "Please wait while we set up the application", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void generateToken() {
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                "https://api.lufthansa.com/v1/oauth/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("Volley Response", response);
                        try {
                            ready = true;
                            JSONObject json = new JSONObject(response);
                            token=json.getString("access_token");
                            //store the token in user preferences
                            Common_data.setPreference(SplashScreen.this,"token",token);

                            //retrieve airport names from the API
                            getAirports();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getLocalizedMessage());
                error.printStackTrace();

            }
        })

                 {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_id", "pd6gszjd624r3ze98nad3kb4");
                params.put("client_secret", "HKKbvT4nrT");
                params.put("grant_type", "client_credentials");
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    private void getAirports() throws UnsupportedEncodingException {

    // Making API Call for airport retrival
        RestHttpClient.get("references/airports", new AirportHandler(),  token);

    }


    private class AirportHandler extends AsyncHttpResponseHandler {
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
                    JSONObject airportResource = json.getJSONObject("AirportResource");
                    JSONObject airportsArray = airportResource.getJSONObject("Airports");
                    JSONArray airport = airportsArray.getJSONArray("Airport");

                    Log.d("Response array", airport.toString());
                    Common_data.setPreference(SplashScreen.this,"airports",airport.toString());


                } catch (Exception e) {

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
            Toast.makeText(SplashScreen.this, "Error: "+ error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


    }

}
