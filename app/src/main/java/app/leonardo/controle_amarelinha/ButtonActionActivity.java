package app.leonardo.controle_amarelinha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Random;

public class ButtonActionActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_pedra;
    private Bundle bundle;
    private BluetoothConnectionService mBluetoothConnection;
    private String modo_jogo;
    private int anterior;
    private String caminho;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_action);

        //Inicialização
        anterior = 0;
        caminho = "ida";
        bundle = getIntent().getExtras();
        modo_jogo = bundle.getString("game_mode");
        btn_pedra = (Button)findViewById(R.id.bt_pedra);
        btn_pedra.setOnClickListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("data_receive"));

    }


    @Override
    public void onClick(View v) {
        int valor = 0;

        if(modo_jogo.equals("Normal")){
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
            btn_pedra.setEnabled(false);
        }else{
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
        intent.putExtra("data_to_send",data_send);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
            JSONObject json = null;
            String tmp = null;
            String cmh = null;
            String btn_pedra_state = null;
            try {
                tmp = bundle.getString("data_rec");
                json = new JSONObject(bundle.getString("data_rec"));
                cmh = json.getString("caminho");
                btn_pedra_state = json.getString("pedra_ok").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(cmh != null){
                if(!cmh.isEmpty()){
                    caminho = cmh;
                }
            }


            if(btn_pedra_state != null){
               if(!btn_pedra_state.isEmpty() && btn_pedra_state.equalsIgnoreCase("ok"))
                   btn_pedra.setEnabled(true);
            }
        }
    };
}
