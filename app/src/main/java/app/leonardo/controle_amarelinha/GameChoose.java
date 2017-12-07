package app.leonardo.controle_amarelinha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;

import java.nio.BufferUnderflowException;
import java.nio.charset.Charset;

public class GameChoose extends AppCompatActivity {

    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private Button btback6;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_choose);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Fixa em modo Retrato


        TextView textView = (TextView)findViewById(R.id.textView13);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/ff.ttf");
        textView.setTypeface(typeface);

        bundle = getIntent().getExtras();
        imageButton1 = (ImageButton)findViewById(R.id.imageButton);
        imageButton2 = (ImageButton)findViewById(R.id.imageButton2);
        btback6 = (Button)findViewById(R.id.btback6);

        btback6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GameChoose.this,BluetoothSearchActivity.class);
                it.putExtras(bundle);
                startActivity(it);
            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent temp_intent = new Intent(GameChoose.this,Main2Activity.class);
                temp_intent.putExtras(bundle);
                startActivity(temp_intent);
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent temp_intent = new Intent(GameChoose.this,Minesweeper.class);
                temp_intent.putExtras(bundle);
                startActivity(temp_intent);
            }
        });
    }
}
