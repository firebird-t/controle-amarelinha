package app.leonardo.controle_amarelinha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ButtonActionActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_pedra;
    private Bundle bundle;
    private BluetoothConnectionService mBluetoothConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_action);

        bundle = getIntent().getExtras();
        btn_pedra = (Button)findViewById(R.id.bt_pedra);
        btn_pedra.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
