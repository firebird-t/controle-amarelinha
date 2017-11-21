package app.leonardo.controle_amarelinha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.nio.charset.Charset;
import java.util.Random;

public class ButtonActionActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_pedra;
    private Bundle bundle;
    private BluetoothConnectionService mBluetoothConnection;
    private String modo_jogo;
    private int anterior;
    private String caminho;
    private int quant_users;
    private int control_users;
    private TextView textView;
    private int valor_jogadas[];
    private int int_valor_jogada;
    private Boolean state_player[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_action);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Fixa em modo Retrato
        //Inicialização
        anterior = 0;
        caminho = "ida";
        bundle = getIntent().getExtras();
        modo_jogo = bundle.getString("game_mode");
        quant_users = Integer.parseInt(bundle.getString("quant_users"));
        textView = (TextView)findViewById(R.id.textView7);
        control_users = 1;

        textView.setText("Jogador: " + control_users);
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
                int max = 7;
                int min = 1;
                valor = random.nextInt((max - min) + 1) + min;

                if(caminho.equals("ida")){
                    if(valor != anterior && valor > anterior && valor <= anterior + 3){
                        anterior = valor;
                        check = true;
                        int_valor_jogada = valor;
                        if(valor >= max){
                            caminho = "volta";
                        }
                    }
                }else if(caminho.equals("volta")){
                    if(valor != anterior && valor < anterior && valor > anterior - 3){
                        anterior = valor;
                        int_valor_jogada = valor;
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
            //String tmp = null;
            //String cmh = null;
            String btn_pedra_state = null;
            try {
                //tmp = bundle.getString("data_rec");
                json = new JSONObject(bundle.getString("data_rec"));
                //cmh = json.getString("caminho");
                btn_pedra_state = json.getString("pedra_ok").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*if(cmh != null){
                if(!cmh.isEmpty()){
                    caminho = cmh;
                }
            }
            */

            if(btn_pedra_state != null){
               if(!btn_pedra_state.isEmpty() && btn_pedra_state.equalsIgnoreCase("ok")) {
                   btn_pedra.setEnabled(true);
                   //textView.setText("");

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
