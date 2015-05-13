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
    private ArrayList<ArrayList<String>> serverList;
    /**
     * the ArrayAdapter of the spinner_serverSection
     */
    private ArrayAdapter<String> arrayAdapterSection;

    public ServerListAsyncTask(Context parentContext, ArrayList<ArrayList<String>> serverList, ArrayAdapter<String> arrayAdapterSection) {
        this.parentContext = parentContext;
        this.serverList = serverList;
        this.arrayAdapterSection = arrayAdapterSection;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> serverDataList = new ArrayList<>();
        URL serverListURL;
        try {
            serverListURL = new URL("http://jx3gc.autoupdate.kingsoft.com/jx3gc/zhcn/serverlist/serverlist.ini");
            HttpURLConnection httpURLConnection = (HttpURLConnection) serverListURL.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                serverDataList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverDataList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> serverDataList) {
        serverList.clear();
        arrayAdapterSection.clear();
        ArrayList<String> serverSectionList = new ArrayList<>();
        for (String serverData : serverDataList) {
            String[] serverStrings = serverData.split("\t");
            int position = serverSectionList.indexOf(serverStrings[0]);
            if (position == -1) {
                serverSectionList.add(serverStrings[0]);
                arrayAdapterSection.add(serverStrings[0]);
                ArrayList<String> serverSubList = new ArrayList<>();
                serverSubList.add(serverStrings[1] + "--" + serverStrings[3] + "--" + serverStrings[4]);
                serverList.add(serverSubList);
            } else
                serverList.get(position).add(serverStrings[1] + "--" + serverStrings[3] + "--" + serverStrings[4]);
        }
        arrayAdapterSection.notifyDataSetChanged();
        Toast.makeText(parentContext, "Server List Updated!", Toast.LENGTH_SHORT).show();
    }
}
