package qi.muxi.jx3serverstatus;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * a class defining the a list fetching AsyncTask.
 * Created by Muxi on 5/10/2015.
 *
 * @author Muxi
 */
public class ServerListAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
    private static final String LOG_TAG = "SLAsyncTask";
    /**
     * the context of parent thread
     */
    private Context parentContext;
    /**
     * the ArrayList of server data strings
     */
    private ArrayList<String> serverList;
    /**
     * the ArrayAdapter of the spinner
     */
    private ArrayAdapter<String> arrayAdapter;

    public ServerListAsyncTask(Context parentContext, ArrayList<String> serverList, ArrayAdapter<String> arrayAdapter) {
        this.parentContext = parentContext;
        this.serverList = serverList;
        this.arrayAdapter = arrayAdapter;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> serverList = new ArrayList<>();
        URL serverListURL;
        try {
            serverListURL = new URL("http://jx3gc.autoupdate.kingsoft.com/jx3gc/zhcn/serverlist/serverlist.ini");
            HttpURLConnection httpURLConnection = (HttpURLConnection) serverListURL.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                serverList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> serverList) {
        this.serverList.clear();
        arrayAdapter.clear();
        for (String server : serverList) {
            String[] serverStrings = server.split("\t");
            this.serverList.add(serverStrings[3] + "--" + serverStrings[4]);
            arrayAdapter.add(serverStrings[0] + "--" + serverStrings[1]);
        }
        arrayAdapter.notifyDataSetChanged();
        Toast.makeText(parentContext, "Server List Updated!", Toast.LENGTH_SHORT).show();
    }
}
