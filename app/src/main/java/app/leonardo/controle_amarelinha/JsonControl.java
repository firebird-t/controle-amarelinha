package app.leonardo.controle_amarelinha;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leonardo on 02/10/2017.
 */

public class JsonControl {
    public JSONObject json_entrada;
    public JSONObject json_saida;

    public JsonControl() {

    }

    private JSONObject json_send(JSONObject out_obj) throws JSONException {
        String json;

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("tipo", "");
        jsonObject.accumulate("info", "");
        jsonObject.accumulate("teste", "");
        json = jsonObject.toString();

        return out_obj;
    }

    private JSONObject json_read(JSONObject in_obj) {
        return in_obj;
    }
}