package helper;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class RestHttpClient 
{
	private static final String APPID = "1";
	private static final String ACCESSKEY = "18101987QWERTY";
	private static String VERSIONNAME = "1.0.0";



	public static String GG ="https://api.lufthansa.com/v1/";


	public static AsyncHttpClient client = new AsyncHttpClient();


    public static void postParams(String url, RequestParams params, AsyncHttpResponseHandler handler, String token)
    {
        Log.i("url_12", getAbsoluteUrl(url));
        Log.i("params", ""+params);
		client.addHeader("Authorization", "Bearer "+token);
		client.addHeader("Accept","application/json");
		client.addHeader("Content-Type","application/json");
        client.post(getAbsoluteUrl(url), params, handler);
        client.setTimeout(30000);
    }


	public static void get(String url, AsyncHttpResponseHandler handler, String token)
	{
		client.addHeader("Authorization", "Bearer "+token);
		client.addHeader("Accept","application/json");
		client.addHeader("Content-Type","application/json");
		client.get(getAbsoluteUrl(url), handler);
		client.setTimeout(30000);
	}

	public static void generateToken(String url, RequestParams params, AsyncHttpResponseHandler handler)
	{
		client.addHeader("Content-Type","application/x-www-form-urlencoded");
		client.post(getAbsoluteUrl(url), params, handler);
	}

	public static String getAbsoluteUrl(String SubUrl)
	{
			Log.d("url", GG + SubUrl);
			Log.d("url", "here");
			return GG + SubUrl;



	}

	public static void cancelRequestMethods(Context con, boolean bolean)
	{
		client.cancelRequests(con, bolean);
	}


}
