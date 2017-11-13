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
            btn_pedra.setEnabled(false);
        }else{
            valor++;
        }

        Intent intent = new Intent("data_send");
        intent.putExtra("valor_botao",valor);
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
            String cmh = null;
            String btn_pedra_state = null;
            try {
                json = new JSONObject(bundle.getString("data_rec"));
                cmh = json.getJSONObject("caminho").toString();
                btn_pedra_state = json.getJSONObject("pedra_ok").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(!cmh.isEmpty() || cmh != null)
                caminho = cmh;

            if(!btn_pedra_state.isEmpty() || btn_pedra_state != null){
                btn_pedra.setEnabled(true);
            }
        }
    };
}
