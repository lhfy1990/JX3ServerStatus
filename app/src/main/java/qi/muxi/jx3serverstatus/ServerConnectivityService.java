package qi.muxi.jx3serverstatus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * a class defining a server connectivity checking service.
 * Created by Muxi on 5/11/2015.
 *
 * @author Muxi
 */
public class ServerConnectivityService extends Service {
    private static final String LOG_TAG = "SCService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String serverHost = intent.getStringExtra("ServerHost");
        int serverPort = intent.getIntExtra("ServerPort", -1);
        if (serverPort != -1 && serverHost != null) {
            ServerConnectivityAsyncTask serverConnectivityAsyncTask = new ServerConnectivityAsyncTask(this);
            serverConnectivityAsyncTask.execute(serverHost, serverPort);
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
