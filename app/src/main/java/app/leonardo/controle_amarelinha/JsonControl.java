package app.leonardo.controle_amarelinha;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leonardo on 02/10/2017.
 */

public class JsonControl {
    public JSONObject json_entrada;
    JSONObject jsonObject;

    public JsonControl() {
        jsonObject = new JSONObject();
    }

    public void add_data(String field, String valor) throws JSONException {
        jsonObject.accumulate(field, valor);
    }

    public String json_prepare() {
        return jsonObject.toString();
    }

    public String json_read(String input_stream, String field) throws JSONException {
        json_entrada = new JSONObject(input_stream);
        String tmp = json_entrada.getString(field);
        return tmp;
    }
}