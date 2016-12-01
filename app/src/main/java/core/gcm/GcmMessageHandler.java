package core.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import core.utils.ConfigUtils;
import core.utils.NotificationUtils;

/**
 * This class defines what to do with the received message. The received message will contain in its
 * data part “as we will see later in GCM server step” two attributes title & message, we will extract “title”
 * value from the intent extras & display it on a Toast.
 */
public class GcmMessageHandler extends IntentService {

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //parse push message
        Log.e("GCM extras", intent.getExtras().toString());
        String commandName = intent.getExtras().getString("message");
        if (commandName != null && !commandName.isEmpty()) {
            try {
                Log.e("GCM command", commandName);
                createInfoNotification(commandName, ConfigUtils.PUSH_HUB_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private int createInfoNotification(String message, String pushType){
        NotificationUtils n = NotificationUtils.getInstance(getApplicationContext());
        return n.createInfoNotification(message, pushType);
    }
}