package app.leonardo.controle_amarelinha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ActionActivity extends AppCompatActivity {

    Bundle bundle;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        bundle = getIntent().getExtras();
        String uuid = bundle.getString("uuid");
        String qJogadores = bundle.getString("quant_users");
        //String tInteraction = bundle.getString("");

        Log.d("k6","UUID: " + uuid);
        Log.d("k6","Usuários: " + uuid);

    }
}
