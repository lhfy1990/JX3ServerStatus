package qi.muxi.jx3serverstatus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * a class defining the main Activity.
 * Created by Muxi on 5/10/2015.
 *
 * @author Muxi
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    /**
     * an ArrayAdapter for spinner_section.
     */
    private ArrayAdapter<String> arrayAdapterSection;
    /**
     * an ArrayAdapter for spinner_server.
     */
    private ArrayAdapter<String> arrayAdapterServer;
    /**
     * a Configuration storing configuration.
     */
    private Configuration configuration;
    /**
     * an AlarmManager setting the alarmed task.
     */
    private AlarmManager alarmManager;
    /**
     * an Intent for server connectivity service, used by {@link #alarmManager}.
     */
    private Intent serviceIntent;
    /**
     * an ArrayList storing server list.
     */
    private ArrayList<ArrayList<String>> serverList;
    /**
     * an int storing current selection position of spinner_section.
     */
    private int position_spinner_section;
    /**
     * a String storing current selected server host.
     */
    private String serverHost;
    /**
     * an int storing current selected server port.
     */
    private int serverPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (configuration == null)
            configuration = new Configuration(true, false, true);

        Switch switch_vibrate = (Switch) findViewById(R.id.switch_vibrate);
        switch_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configuration.setVibrate(isChecked);
            }
        });
        Switch switch_sound = (Switch) findViewById(R.id.switch_sound);
        switch_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configuration.setSound(isChecked);
            }
        });
        Switch switch_light = (Switch) findViewById(R.id.switch_light);
        switch_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configuration.setLight(isChecked);
            }
        });


        if (alarmManager == null) alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        serviceIntent = new Intent(getApplicationContext(), ServerConnectivityService.class);

        Button button_start = (Button) findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverHost != null) {
                    Bundle serviceBundle = new Bundle();
                    serviceBundle.putString(getString(R.string.serverHost), serverHost);
                    serviceBundle.putInt(getString(R.string.serverPort), serverPort);
                    serviceBundle.putSerializable(getString(R.string.configuration), configuration);
                    serviceIntent.putExtras(serviceBundle);
                    PendingIntent serverConnectivityIntent = PendingIntent.getService(getApplicationContext(), 0,
                            serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
                            10000, serverConnectivityIntent);
                    Toast.makeText(getApplicationContext(), getText(R.string.toast_alarmSet), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), getText(R.string.toast_serverUnselected), Toast.LENGTH_SHORT).show();
            }
        });
        Button button_end = (Button) findViewById(R.id.button_end);
        button_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarmManager != null) {
                    PendingIntent serverConnectivityIntent = PendingIntent.getService(getApplicationContext(), 0,
                            serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(serverConnectivityIntent);
                    Toast.makeText(getApplicationContext(), getText(R.string.toast_alarmCancelled), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (arrayAdapterSection == null)
            arrayAdapterSection = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item);
        Spinner spinner_section = (Spinner) findViewById(R.id.spinner_section);
        spinner_section.setAdapter(arrayAdapterSection);
        spinner_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position_spinner_section = position;
                arrayAdapterServer.clear();
                ArrayList<String> serverSubList = serverList.get(position);
                for (String serverData : serverSubList) {
                    String[] data = serverData.split(getString(R.string.splitter));
                    arrayAdapterServer.add(data[0]);
                }
                arrayAdapterServer.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), getText(R.string.toast_serverUnselected), Toast.LENGTH_SHORT).show();
            }
        });

        if (arrayAdapterServer == null)
            arrayAdapterServer = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item);
        Spinner spinner_server = (Spinner) findViewById(R.id.spinner_server);
        spinner_server.setAdapter(arrayAdapterServer);
        spinner_server.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] data = serverList.get(position_spinner_section).get(position).split(getString(R.string.splitter));
                serverHost = data[1];
                serverPort = Integer.valueOf(data[2]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), getText(R.string.toast_serverUnselected), Toast.LENGTH_SHORT).show();
            }
        });

        serverList = new ArrayList<>();
        serverList.clear();
        ServerListAsyncTask serverListAsyncTask = new ServerListAsyncTask(getApplicationContext(), serverList, arrayAdapterSection);
        serverListAsyncTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), getText(R.string.toast_action_settings), Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}