package app.leonardo.controle_amarelinha;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.UUID;

import pl.droidsonroids.gif.GifTextView;

public class InteractionActivity extends AppCompatActivity{
    public Button button3;

    public GifTextView btnAgita;
    public GifTextView btnRockRelease;
    public GifTextView btnSimple;
    private Button btback9;
    BluetoothConnectionService mBluetoothConnection;
    BluetoothDevice mBTDevice;
    BluetoothAdapter mBluetoothAdapter;
    public JsonControl jsonControl;
    byte[] data_send;
    Bundle bundle;
    private int quant_users;
    int num1;
    int num2;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Fixa em modo Retrato

        bundle = getIntent().getExtras();
        jsonControl = new JsonControl();
        quant_users = Integer.parseInt(bundle.getString("quant_users"));

        btback9 = (Button)findViewById(R.id.btback9);
        btback9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(InteractionActivity.this,Main2Activity.class);
                startActivity(it);
            }
        });


        TextView textView = (TextView)findViewById(R.id.textView14);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/ff.ttf");
        textView.setTypeface(typeface);
        String modo_jogo = bundle.getString("game_mode");

        Log.d("k6","DEVICE UUID: " + bundle.getString("device"));
        Log.d("k6", "Device Address: " + bundle.getString("address"));
        Log.d("Quantidade_de_Usuários", String.valueOf(quant_users));
        Log.d("Modo_Jogo",bundle.getString("game_mode"));
        Random random = new Random();
        num1 = random.nextInt((6 - 2) + 1) + 2;

        boolean check = false;

        while(!check){
            num2 = random.nextInt((6 - 2) + 1) + 2;
            if(num2 != num1){
                check = true;
            }
        }


        int temp_game;
        if(modo_jogo.equalsIgnoreCase("Normal")){
            temp_game = 1;
        }else{
            temp_game = 2;
        }

        try {
            jsonControl.add_data("jogo","1");
            jsonControl.add_data("quant_users", String.valueOf(quant_users));
            jsonControl.add_data("modo", String.valueOf(temp_game));
            jsonControl.add_data("num", String.valueOf(num1));
            jsonControl.add_data("num2",String.valueOf(num2));
            String tmp = jsonControl.json_prepare();
            data_send = tmp.getBytes(Charset.defaultCharset());

            Intent intent = new Intent("data_send");
            intent.putExtra("data_to_send",data_send);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            Log.d("Data send", tmp);
            Log.d("InteractionActivity","Dados enviados");

            //mBluetoothConnection.write(data_send);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnAgita = (GifTextView) findViewById(R.id.imgAgita);
        //btnRockRelease = (GifTextView) findViewById(R.id.imgRockRelease);
        btnSimple = (GifTextView) findViewById(R.id.imgSimpleButton);

        btnAgita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InteractionActivity.this,ShakeActivity.class);
                bundle.putString("jogo_inter", "agita");
                bundle.putString("quant_users", String.valueOf(quant_users));
                bundle.putString("num1",String.valueOf(num1));
                bundle.putString("num2", String.valueOf(num2));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//        btnRockRelease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(InteractionActivity.this,RockReleaseActivity.class);
//                bundle.putString("jogo_inter","rock");
//                bundle.putString("quant_users", String.valueOf(quant_users));
//                bundle.putString("num1",String.valueOf(num1));
//                bundle.putString("num2", String.valueOf(num2));
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });

        btnSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InteractionActivity.this,ButtonActionActivity.class);
                bundle.putString("jogo_inter", "simples");
                bundle.putString("quant_users", String.valueOf(quant_users));
                bundle.putString("num1",String.valueOf(num1));
                bundle.putString("num2", String.valueOf(num2));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }


   public void onResume() {
        super.onResume();
    }

    protected void onDestroy(){
        super.onDestroy();
        //stopService(new Intent(InteractionActivity.this,BluetoothSearchActivity.class));
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
       // mBluetoothConnection.close_conn();
    }
}
