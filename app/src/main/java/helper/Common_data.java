package helper;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Common_data
{
	/*********** Method for getting the status of internet ********/
	public static final boolean isInternetOn(Context con) {
		InternetStatus internetstatus = new InternetStatus(con);
		boolean isOnline = internetstatus.isOnline();
		return isOnline;
	}
	





	/************************************************/
	/***************************************************** set shared preferences **************************************************/
	public static void setPreference(Context con, String key, String value)
	{
		SharedPreferences preferences = con.getSharedPreferences("sylvestermwambeke@gmail.com", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**************************************************** get shared preferences ***************************************************/
	public static String getPreferences(Context con, String key)
	{
		SharedPreferences sharedPreferences = con.getSharedPreferences("sylvestermwambeke@gmail.com", 0);
		String value = sharedPreferences.getString(key, "0");
		return value;
	}

	public static void Preferencesclear(Context con)
	{
		SharedPreferences preferences = con.getSharedPreferences("sylvestermwmabeke@gmail.com", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}

	public static String formatDate(int year, int month, int day) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day);
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy");
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return sdf.format(date);
	}

	public static String formatDate1(int year, int month, int day) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day);
		Date date = cal.getTime();
		//SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return sdf.format(date);
	}

	public static void closeKeyboard(Context c, IBinder windowToken) {
		InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(windowToken, 0);
	}




	public static boolean isSimInserted(Activity act) {
		/*boolean f=false;
		try {
			TelephonyManager telMgr = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);

			if (telMgr.getSimState() != TelephonyManager.SIM_STATE_ABSENT)
				f= true;
			else
				f=false;



		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;*/

		return true;
	}

	public static String getMCCMNC(Activity act) {
		String MCCMNC="";
		int mcc=0;
		int mnc=0;
		try {
			TelephonyManager telMgr = (TelephonyManager)act.getSystemService(Context.TELEPHONY_SERVICE);

			//if (isSimInserted(act)){
			String mccmnc1 = telMgr.getNetworkOperator();
			Log.i("MCCMNC", "" + mccmnc1);

			if (mccmnc1 != null) {
				mcc = Integer.parseInt(mccmnc1.substring(0, 3));
				mnc = Integer.parseInt(mccmnc1.substring(3));
				Log.i("MCC", "" + mcc);
				Log.i("MNC", "" + mnc);

				MCCMNC = mcc + "" + mnc;
			}

			Log.i("SimState", "" + "sim present"+telMgr.getSimState()+"\nmccmnc="+MCCMNC);
			/*} else{
				Toast.makeText(act, "Please Insert Sim For M-PESA Transfer", Toast.LENGTH_LONG).show();
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		return MCCMNC;
	}

	public static String getDateAndTime(){

		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

		//DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.getDefault()).format(new Date());
		return currentDateTimeString;
	}
}