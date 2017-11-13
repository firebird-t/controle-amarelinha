package app.leonardo.controle_amarelinha;

import android.content.Context;
import android.content.Loader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener {

    //Acelerômetro
    private SensorManager mSensorManager;
    private Sensor mSensor;
    protected float[] linear_acceleration = new float[3];
    protected boolean lock_;
    private TextView editText1;
    private TextView editText2;
    private TextView editText3;
    private Bundle bundle;
    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        bundle = getIntent().getExtras();
       editText1 = (TextView)findViewById(R.id.editText3);
       editText2 = (TextView)findViewById(R.id.editText);
       editText3 = (TextView)findViewById(R.id.editText4);

        //Iniciliazação dos Sensores

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    //Exemplo da Página de ajuda do Android
    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){

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

            if(lock_ == true){
               testPosition();
            }else{
                //Gravar Posições de tempo para mudança
                if(linear_acceleration[1] > 0 && linear_acceleration[1] < 4){

                }

                //
                if(linear_acceleration[1] > 4 && linear_acceleration[1] < 7){

                }

            }



            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 300) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    void testPosition(){
        if(linear_acceleration[1] > 0){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(1000);
            Toast.makeText(getApplicationContext(),"aponte o celular para baixo",Toast.LENGTH_LONG);
        }else{
            lock_ = false;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }


    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    protected void onDestroy(){
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }
}
