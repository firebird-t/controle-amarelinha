package app.leonardo.controle_amarelinha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class InteractionActivity extends AppCompatActivity {
    public Button button3;

    public ImageButton btnAgita;
    public ImageButton btnRockRelease;
    public ImageButton btnSimple;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);

        Bundle bundle = getIntent().getExtras();

        Log.d("k6","UUID: " + bundle.getString("uuid"));
        Log.d("K6", bundle.getString("quant_users"));
        btnAgita = (ImageButton)findViewById(R.id.imgAgita);
        btnRockRelease = (ImageButton)findViewById(R.id.imgRockRelease);
        btnSimple = (ImageButton)findViewById(R.id.imgSimpleButton);

        btnAgita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnRockRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
