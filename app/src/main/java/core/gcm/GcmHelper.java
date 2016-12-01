package core.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;
import core.helpers.ConstantsHelper;
import il.co.yomanim.mobileyomanim.activities.MainActivity;

public class GcmHelper {

    String PROJECT_NUMBER = "930399098474";
    String TAG = "GCM";
    Context _context;


    public String init(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                ConstantsHelper.PREF_NAME,
                Activity.MODE_PRIVATE);
        String gcmToken = sharedPreferences.getString("gcmToken", null);
        if (gcmToken == null) {
            try {
                InstanceID instanceID = InstanceID.getInstance(MainActivity.instance);
//                    // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
//                    // See https://developers.google.com/cloud-messaging/android/start for details on this file.
                gcmToken = instanceID.getToken(PROJECT_NUMBER, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                Log.e("GCM", gcmToken != null ? gcmToken : "null");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("gcmToken",gcmToken);
                editor.apply();
            } catch (IOException ex) {
                Log.e("GCM Error", ex.getMessage());
            }
        }

        return gcmToken;
    }


    public void init(final String token, final Context context){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                _context = context;
                try {
                    InstanceID instanceID = InstanceID.getInstance(MainActivity.instance);
//                    // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
//                    // See https://developers.google.com/cloud-messaging/android/start for details on this file.
                    String gcmToken = instanceID.getToken(PROJECT_NUMBER, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    // [END get_token]
                    msg = "Device registered, registration ID=" + token;
                    Log.i("GCM", msg);

//                    sendRegistrationToServer(token, gcmToken);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.e("Error", ex.getMessage());
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //TODO: there is nothing to do
                Log.d(TAG, msg);
            }
        }.execute(null, null, null);
    }


    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, String gcmToken) {
        // Add custom implementation, as needed.
        try {
//            TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
//
//
//            String deviceIMEI = new StringBuilder("IMEI:").append(telephonyManager.getDeviceId()).toString();
//            SoapHelper soapHelper = new SoapHelper((Foda.getInstance()).getFodsNamespace(),
//                    (Foda.getInstance()).getFodsWsdlURL(),
//                    (Foda.getInstance()).getFodsWebServiceUser(),
//                    (Foda.getInstance()).getFodsWebServicePassword());

//            //TODO: get rid, test purposes only
//            String url = "http://192.168.1.50:8080/FODSWebService/FODSWebServiceWSDL.wsdl";

//            boolean isRegistered = soapHelper.registerDeviceToGcm(deviceIMEI, token);

            Log.e("Token", token);
            Log.e("GCM-TOKEN", gcmToken);

//            String gcmContent = HttpManager.registerDeviceToGcm(_context, token, gcmToken);
//            if (gcmContent != null) {
//                Log.d(TAG, "Web Service call 'messagingDeviceRegisterRequest' succeeded");
//            }
        } catch (Exception e) {
            Log.e(TAG, "Error calling 'messagingDeviceRegisterRequest' web service : \n" + e.getMessage());
        }
    }
}