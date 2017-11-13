package app.leonardo.controle_amarelinha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;

import java.util.Random;

public class RockReleaseActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private Button btn_pedra;
    private Bundle bundle;
    private BluetoothConnectionService mBluetoothConnection;
    private String modo_jogo;
    private int anterior;
    private String caminho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rock_release);

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
        //mDetector.setOnDoubleTapListener(this);

        anterior = 0;
        caminho = "ida";
        bundle = getIntent().getExtras();
        modo_jogo = bundle.getString("game_mode");

        //btn_pedra = (Button)findViewById(R.id.bt_pedra);

        //btn_pedra.setOnClickListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("data_rec"));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        //Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());

        int valor = 0;

        if(modo_jogo.equals("normal")){
            Boolean check = false;
            while(!check){
                Random random = new Random();
                int max = 10;
                int min = 1;
                valor = random.nextInt((max - min) + 1) + min;

                if(caminho.equals("ida")){
                    if(valor != anterior && valor > anterior && valor <= anterior + 2){
                        anterior = valor;
                        check = true;
                    }
                }else if(caminho.equals("volta")){
                    if(valor != anterior && valor < anterior && valor <= anterior - 2){
                        anterior = valor;
                        check = true;
                    }
                }

            }
            //btn_pedra.setEnabled(false);
        }else{
            valor++;
        }

        Intent intent = new Intent("data_send");
        intent.putExtra("valor_botao",valor);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(!bundle.getString("caminho").isEmpty())
                caminho = bundle.getString("caminho");

            if(!bundle.getString("pedra_ok").isEmpty()){
                btn_pedra.setEnabled(true);
            }
        }
    };

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }



}
