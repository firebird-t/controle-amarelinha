package app.leonardo.controle_amarelinha;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener {

    //Acelerômetro
    private SensorManager mSensorManager;
    private Sensor mSensor;
    protected float[] linear_acceleration = new float[3];
    protected boolean lock_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        //Iniciliazação dos Sensores
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
        //   testPosition();
        //}

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    void testPosition(){

    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener((SensorEventListener) this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener((SensorEventListener) this);
    }
}
