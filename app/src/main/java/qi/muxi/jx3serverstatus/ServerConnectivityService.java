package qi.muxi.jx3serverstatus;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
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
        Bundle serviceBundle = intent.getExtras();
        String serverHost = serviceBundle.getString(getString(R.string.serverHost), null);
        int serverPort = serviceBundle.getInt(getString(R.string.serverPort), -1);
        Configuration configuration = (Configuration) serviceBundle.getSerializable(getString(R.string.configuration));
        if (serverPort != -1 && serverHost != null) {
            ServerConnectivityAsyncTask serverConnectivityAsyncTask = new ServerConnectivityAsyncTask(this, configuration);
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
