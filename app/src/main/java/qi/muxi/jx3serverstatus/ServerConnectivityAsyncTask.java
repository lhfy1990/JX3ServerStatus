package qi.muxi.jx3serverstatus;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * <p>a class defining a server connectivity checking AsyncTask.</p>
 * <p>This class should only be used in {@link ServerConnectivityService}</p>
 * Created by Muxi on 5/11/2015.
 *
 * @author Muxi
 */
public class ServerConnectivityAsyncTask extends AsyncTask<Object, Void, Boolean> {
    private static final String LOG_TAG = "SCAsyncTask";
    /**
     * a Service referring parent Service.
     */
    private Service parentService;
    /**
     * a Configuration storing notification configuration.
     */
    private Configuration configuration;

    /**
     * Called by construction.
     *
     * @param parentService the Service referring parent Service.
     * @param configuration the Configuration storing notification configuration.
     */
    public ServerConnectivityAsyncTask(Service parentService, Configuration configuration) {
        this.parentService = parentService;
        this.configuration = configuration;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        String serverHost = (String) params[0];
        int serverPort = (int) params[1];
        Log.i(LOG_TAG, String.valueOf(serverHost) + " " + String.valueOf(serverPort));
        int timeout = 5000;
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(serverHost, serverPort);
            socket.connect(socketAddress, timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean connectivity) {
        super.onPostExecute(connectivity);
        Log.i(LOG_TAG, String.valueOf(connectivity));
        if (connectivity) {
            Intent callBackActivityIntent = new Intent(parentService, MainActivity.class);
            callBackActivityIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            Notification notification = new NotificationCompat.Builder(parentService)
                    .setTicker(parentService.getText(R.string.notification_ticker))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(parentService.getText(R.string.notification_contentTitle))
                    .setContentText(parentService.getText(R.string.notification_contentText))
                    .setContentIntent(PendingIntent.getActivity(parentService, 0, callBackActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            if (configuration.isLight()) {
                notification.flags |= Notification.FLAG_SHOW_LIGHTS;
                notification.defaults |= Notification.DEFAULT_LIGHTS; // TODO May not corrent on different devices
                notification.ledOnMS = 300;
                notification.ledOffMS = 1000;
            }
            if (configuration.isVibrate())
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            if (configuration.isSound())
                notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationManager notificationManager = (NotificationManager) parentService.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
            AlarmManager alarmManager = (AlarmManager) parentService.getSystemService(Context.ALARM_SERVICE);
            Intent serviceIntent = new Intent(parentService, ServerConnectivityService.class);
            PendingIntent serverConnectivityIntent = PendingIntent.getService(parentService, 0,
                    serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(serverConnectivityIntent);
        }
        parentService.stopSelf();
    }
}
