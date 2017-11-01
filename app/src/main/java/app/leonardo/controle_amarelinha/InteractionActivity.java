package app.leonardo.controle_amarelinha;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.json.JSONException;

import java.nio.charset.Charset;
import java.util.UUID;

public class InteractionActivity extends AppCompatActivity {
    public Button button3;

    public ImageButton btnAgita;
    public ImageButton btnRockRelease;
    public ImageButton btnSimple;
    BluetoothConnectionService mBluetoothConnection;
    BluetoothDevice mBTDevice;
    BluetoothAdapter mBluetoothAdapter;
    public JsonControl jsonControl;
    byte[] data_send;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//

        bundle = getIntent().getExtras();
        jsonControl = new JsonControl();

        LocalBroadcastManager.getInstance(InteractionActivity.this).registerReceiver(mReceiver, new IntentFilter("SendMessage"));

        mBluetoothConnection = new BluetoothConnectionService(InteractionActivity.this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTDevice = mBluetoothAdapter.getRemoteDevice(bundle.getString("address"));
        startBTConnection(mBTDevice, UUID.fromString(bundle.getString("device")));

        Log.d("k6","DEVICE UUID: " + bundle.getString("device"));
        Log.d("k6", "Device Address: " + bundle.getString("address"));
        Log.d("K6", bundle.getString("quant_users"));


        btnAgita = (ImageButton)findViewById(R.id.imgAgita);
        btnRockRelease = (ImageButton)findViewById(R.id.imgRockRelease);
        btnSimple = (ImageButton)findViewById(R.id.imgSimpleButton);
        btnAgita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnRockRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d("Lenovo K6", "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection.startClient(device,uuid);
    }

    public void onResume() {
        super.onResume();
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                jsonControl.add_data("device", bundle.getString("device"));
                jsonControl.add_data("address",bundle.getString("address"));
                jsonControl.add_data("quant_users",bundle.getString("quant_users"));
                String tmp = jsonControl.json_prepare();
                data_send = tmp.getBytes(Charset.defaultCharset());
                Log.d("Data send", tmp);
                Log.d("InteractionActivity","Dados enviados");
                mBluetoothConnection.write(data_send);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    protected void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        mBluetoothConnection.close_conn();

    }
}
