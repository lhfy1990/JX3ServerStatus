package qi.muxi.jx3serverstatus;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private static final String LOG_TAG = "MainActivity";
    private AlarmManager alarmManager;
    private Intent serviceIntent;
    private ArrayList<String> serverList;
    private String serverHost;
    private int serverPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }

        serviceIntent = new Intent(getApplicationContext(), ServerConnectivityService.class);

        Button button_start = (Button) findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverHost != null) {
                    serviceIntent.putExtra("ServerHost", serverHost);
                    serviceIntent.putExtra("ServerPort", serverPort);
                    PendingIntent serverConnectivityIntent = PendingIntent.getService(getApplicationContext(), 0,
                            serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
                            10000, serverConnectivityIntent);
                    Toast.makeText(getApplicationContext(), "Alarm task set!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Please select a server!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Alarm task cancelled!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] data = serverList.get(position).split("--");
                serverHost = data[0];
                serverPort = Integer.valueOf(data[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please select a server!", Toast.LENGTH_SHORT).show();
            }
        });

        serverList = new ArrayList<>();
        serverList.clear();
        ServerListAsyncTask serverListAsyncTask = new ServerListAsyncTask(getApplicationContext(), serverList, arrayAdapter);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}