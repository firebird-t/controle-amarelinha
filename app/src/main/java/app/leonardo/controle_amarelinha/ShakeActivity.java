package app.leonardo.controle_amarelinha;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.Random;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    protected float[] linear_acceleration = new float[3];
    protected boolean lock_;
    private TextView textView;
    private Bundle bundle;
    private long lastUpdate;
    private Boolean toast_bool;
    private Boolean jogada;
    private Boolean vibra_bool;
    Boolean first_start;
    ProgressDialog progress;
    private String caminho;
    private int control_users;
    private int quant_users;
    private int int_valor_jogada;
    private int anterior;
    private String modo_jogo;
    private String resp_arduino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shake);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Fixa em modo Retrato

        //Inicialização
        bundle = getIntent().getExtras();
        lock_ = true;
        toast_bool = false;
        jogada = false;
        first_start = true;
        vibra_bool = false;
        resp_arduino = null;

        progress = new ProgressDialog(this);
        progress.setTitle("Preparando o celular");
        progress.setMessage("Por favor, mantenha o celular apontado para baixo...");
        progress.setIndeterminate(true);
       // progress.setCancelable(true);

        //
        anterior = 0;
        caminho = "ida";
        bundle = getIntent().getExtras();
        modo_jogo = bundle.getString("game_mode");
        quant_users = Integer.parseInt(bundle.getString("quant_users"));
        control_users = 1;

        //Textviews
        textView = (TextView) findViewById(R.id.editText3);
        textView.setText("Jogador: " + control_users);

        //Inicilização dos Sensores
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("data_receive"));

    }

    private void iniciaJogo() {
        Progress w = new Progress();
        showProgress();
        w.start();
    }

    private void showProgress(){
        progress.show();
    }


    class Progress extends Thread {

        public Progress() {}

        public void run() {
            while (!jogada) {
                if (testPosition()) {
                    close_progress();
                    jogada = true;
                    Log.d("Thread", "Terminou execução");
                    this.interrupt();
                }
            }
        }
    }


    class Sorteio extends Thread{
       private Context context;

        public Sorteio(Context context){
            this.context = context;
        }

        public void run() {
            int valor = 0;

            if (modo_jogo.equals("Normal")) {
                Boolean check = false;
                while (!check) {
                    Random random = new Random();
                    int max = 10;
                    int min = 1;
                    valor = random.nextInt((max - min) + 1) + min;

                    if (caminho.equals("ida")) {
                        if (valor != anterior && valor > anterior && valor <= anterior + 3) {
                            anterior = valor;
                            check = true;
                            int_valor_jogada = valor;
                            if (valor >= 10) {
                                caminho = "volta";
                            }
                        }
                    } else if (caminho.equals("volta")) {
                        if (valor != anterior && valor < anterior && valor > anterior - 3) {
                            anterior = valor;
                            int_valor_jogada = valor;
                            check = true;
                        }
                    }

                }
            } else {
                valor++;
            }

            JsonControl jsonControl = new JsonControl();

            try {
                jsonControl.add_data("valor_botao", String.valueOf(valor));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String tmp = jsonControl.json_prepare();
            byte[] data_send = tmp.getBytes(Charset.defaultCharset());
            Intent intent = new Intent("data_send");
            intent.putExtra("data_to_send", data_send);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            Progress p = new Progress();
            progress.setMessage("Mantenha o telefone apontado para baixo...");
            showProgress();
            p.start();
            this.interrupt();
        }
    }

    class Resposta extends Thread{
        public Resposta(){}

        public void run(){
            Boolean answer = false;
            while(!answer){
                if(resp_arduino == "ok"){
                    answer = true;
                    Log.d("Thread_Resposta", "Finalizada");
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


            if (first_start) {
                iniciaJogo();
                first_start = false;
            }


            if (jogada) {
                //Gravar Posições de tempo para mudança
                if (linear_acceleration[1] > 5 && linear_acceleration[1] < 8) {
                    Log.d("Linear 2", String.valueOf(linear_acceleration[1]));
                    Sorteio sorteio = new Sorteio(ShakeActivity.this );
                    sorteio.start();
                    jogada = false;
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

    private boolean testPosition() {
        Boolean test_pos = false;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (linear_acceleration[1] > -4) {
            if (!vibra_bool) {
                v.vibrate(1000);
                vibra_bool = true;
            }
            return false;
        } else {
            v.vibrate(200);
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            JSONObject json = null;
            String btn_pedra_state = null;

            try {
                json = new JSONObject(bundle.getString("data_rec"));
                btn_pedra_state = json.getString("pedra_ok").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(btn_pedra_state != null){
                if(!btn_pedra_state.isEmpty() && btn_pedra_state.equalsIgnoreCase("ok")) {
                    resp_arduino = "ok";
                    Log.d("Caminho",caminho);

                    if(caminho == "volta" && int_valor_jogada <= 1){
                        caminho = "ida";
                        control_users++;
                        textView.setText("Jogador: " + control_users);
                    }


                    if(control_users > quant_users){
                        control_users = 1;
                        textView.setText("Jogador: " + control_users);
                    }
                }
            }
        }
    };
}
