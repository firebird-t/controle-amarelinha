package app.leonardo.controle_amarelinha;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    protected float[] linear_acceleration = new float[3];
    protected boolean lock_;
    private TextView editText1;
    private TextView editText2;
    private TextView editText3;
    private Bundle bundle;
    private long lastUpdate;
    private Boolean toast_bool;
    private Boolean jogada;
    private Boolean vibra_bool;
    Boolean first_start;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        //Inicialização
        bundle = getIntent().getExtras();
        lock_ = true;
        toast_bool = false;
        jogada = false;
        first_start = true;
        vibra_bool = false;

        progress = new ProgressDialog(this);
        progress.setTitle("Preparando o celular");
        progress.setMessage("Por favor, mantenha o celular apontado para baixo...");
        progress.setIndeterminate(true);
        progress.setCancelable(true);

        //Textviews
        editText1 = (TextView) findViewById(R.id.editText3);
        editText2 = (TextView) findViewById(R.id.editText);
        editText3 = (TextView) findViewById(R.id.editText4);

        //Inicilização dos Sensores
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


    }

    private void iniciaJogo() {
        progress.show();
        Contador w = new Contador();
        w.start();

    }


    class Contador extends Thread {

        private int test;

        public Contador() {}

        public void run() {
            Boolean exibe_progress = false;

            while (!jogada) {
                if (testPosition()) {
                    close_progress();
                    Log.d("Thread", "Terminou execução");
                    jogada = true;
                    this.interrupt();
                }
            }
        }
    }

    //Exemplo da Página de ajuda do Android
    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

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


            editText1.setText(String.valueOf(linear_acceleration[0]));
            editText2.setText(String.valueOf(linear_acceleration[1]));
            editText3.setText(String.valueOf(linear_acceleration[2]));

            if (first_start) {
                //Testa posição do celular
                iniciaJogo();
                first_start = false;
            }


            if (jogada) {
                //Gravar Posições de tempo para mudança
                if (linear_acceleration[1] > 0 && linear_acceleration[1] < 4.09) {
                    Log.d("Linear 1", String.valueOf(linear_acceleration[1]));
                    jogada =  false;

                }
                if (linear_acceleration[1] >= 4.1 && linear_acceleration[1] < 8) {
                    Log.d("Linear 2", String.valueOf(linear_acceleration[1]));
                    jogada = false;
                }
            }

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 300) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                Log.d("Tempo", String.valueOf(diffTime));
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private boolean testPosition() {
        Boolean test_pos = false;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (linear_acceleration[1] > 0) {
            if (!vibra_bool) {
                v.vibrate(1000);
                vibra_bool = true;
            }
            return false;
        } else {
            v.vibrate(200);
            //lock_ = false;
            //progress.dismiss();
            //v.vibrate(200);
            return true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void close_progress() {
        progress.dismiss();
    }


    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
