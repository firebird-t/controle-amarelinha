package app.leonardo.controle_amarelinha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("data_rec"));


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
                    if(valor != anterior && valor > anterior && valor <= anterior + 3){
                        anterior = valor;
                        check = true;
                    }
                }else if(caminho.equals("volta")){
                    if(valor != anterior && valor < anterior && valor <= anterior - 3){
                        anterior = valor;
                        check = true;
                    }
                }

            }

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
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //intent = intent.getExtras();

        }
    };
}
