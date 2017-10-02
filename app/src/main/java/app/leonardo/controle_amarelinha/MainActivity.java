package app.leonardo.controle_amarelinha;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.Vibrator;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public BluetoothAdapter mBluetoothAdapter;
    protected float[] linear_acceleration = new float[3];
    protected boolean lock_;
    String TAG = "Lenovo K6";
    private Button button1;
    //Bluetooth
    private Button bt_conect;
    //Acelerômetro
    private SensorManager mSensorManager;
    private Sensor mSensor;

    //Gerenciamento de Conexão
    //private ConnectivityManager connManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicio de Atividade
        button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent escolhe_avatar = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(escolhe_avatar);
            }
        });

        //Conexão Bluetooth
        //bt_button = (Button) findViewById(R.id.button5);
        bt_conect = (Button) findViewById(R.id.bt_conect);
        bt_conect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent busca_dispositivos = new Intent(MainActivity.this, BluetoothSearchActivity.class);
                startActivityForResult(busca_dispositivos, 1);
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //lvNewDevices = (ListView)findViewById(R.id.listview_bt);

        //Iniciliazação dos Sensores
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Intents
        //Intent bt_discovery_intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //bt_discovery_intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        //startActivity(bt_discovery_intent);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.conectar, menu);
        inflater.inflate(R.menu.jogar, menu);
        inflater.inflate(R.menu.sair, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Exemplo da Página de ajuda do Android
    @Override
    public void onSensorChanged(SensorEvent event) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        final float alpha = (float) 0.8;

        // Isolate the force of gravity with the low-pass filter.
        float[] gravity = new float[3];

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        float accelationSquareRoot = (linear_acceleration[0] * linear_acceleration[0] +
                linear_acceleration[1] * linear_acceleration[1] + linear_acceleration[2] *
                linear_acceleration[2]) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);


        //if( lock_ == true){
        //   testaPosicao();
        //}

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener((SensorEventListener) this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener((SensorEventListener) this);
    }

    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mBroadcastReceiver);
    }


}
