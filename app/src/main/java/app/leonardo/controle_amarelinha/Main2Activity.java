package app.leonardo.controle_amarelinha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.content.Context.*;

public class Main2Activity extends AppCompatActivity {

    public Button button2;
    public EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editText = (EditText)findViewById(R.id.editText2);
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer mode = Activity.MODE_PRIVATE;
                SharedPreferences shared = getApplication().getSharedPreferences("q_users",mode);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("q_users", String.valueOf(editText.getText()));
                editor.commit();

                Intent escolhe_interacao = new Intent(Main2Activity.this, InteractionActivity.class);
                startActivity(escolhe_interacao);
            }
        });
    }
}
