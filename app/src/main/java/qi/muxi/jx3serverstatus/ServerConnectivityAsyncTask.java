package qi.muxi.jx3serverstatus;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * a class defining a server connectivity checking AsyncTask.
 * Created by Muxi on 5/11/2015.
 *
 * @author Muxi
 */
public class ServerConnectivityAsyncTask extends AsyncTask<Object, Void, Boolean> {
    private static final String LOG_TAG = "SCAsyncTask";
    private Service parentService;

    public ServerConnectivityAsyncTask(Service parentService) {
        this.parentService = parentService;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        String serverHost = (String) params[0];
        int serverPort = (int) params[1];
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
        if (connectivity) {
            Intent callBackActivityIntent = new Intent(parentService, MainActivity.class);
            callBackActivityIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            Notification notification = new NotificationCompat.Builder(parentService)
                    .setTicker("Congratulations! The server is ALIVE!")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Server Connectivity Notification")
                    .setContentText("Congratulations! The server is ALIVE! Enjoy your game!")
                    .setContentIntent(PendingIntent.getActivity(parentService, 0, callBackActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
            notification.defaults |= Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
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
