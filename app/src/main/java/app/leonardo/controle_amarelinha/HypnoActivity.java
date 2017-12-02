package app.leonardo.controle_amarelinha;

import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;

import java.nio.charset.Charset;

public class HypnoActivity extends AppCompatActivity {

    Bundle bundle;
    JsonControl jsonControl;
    int quant_users;
    private byte[] data_send;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hypno);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Fixa em modo Retrato

    }
}
