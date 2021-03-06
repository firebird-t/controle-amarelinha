package app.leonardo.controle_amarelinha;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothSearchActivity extends MainActivity{

    private static final String TAG = "MainActivity";
    //public DeviceListAdapter mDeviceListAdapter;
    ArrayList<String> mDeviceList = new ArrayList<String>();
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    BluetoothConnectionService mBluetoothConnection;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;
    ImageButton Discover;
    ListView listView;
    private Boolean registerReceive1Boolean = false;
    private Boolean registerReceive2Boolean = false;
    private Boolean registerReceive3Boolean = false;
    private Boolean registerReceive4Boolean = false;
    private Boolean enable = false;
    private Boolean valid_device = false;
    private UUID uuid_device;
    Button btnNext;
    public Bundle bundle;
    private Button btback7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bluetooth_search);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Fixa em modo Retrato

        listView = (ListView)findViewById(R.id.listview_bt);
        Discover = (ImageButton)findViewById(R.id.btnDiscover);

        //Botão avançar
        btnNext = (Button)findViewById(R.id.bntNext);

        //Botão Voltar
        btback7 = (Button)findViewById(R.id.btback7);
        btback7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(BluetoothSearchActivity.this,MainActivity.class);
                startActivity(it);
            }
        });

        TextView textView = (TextView)findViewById(R.id.textView16);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/ff.ttf");
        textView.setTypeface(typeface);

        bundle = new Bundle();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);
        registerReceive4Boolean = true;

        mBTDevices = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDiscover();
            }
        });

        if (!mBluetoothAdapter.isEnabled()) {
            registerReceive1Boolean = true;
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBluetoothAdapter.cancelDiscovery();

                ProgressDialog mProgressDialog = new ProgressDialog(BluetoothSearchActivity.this);
                mProgressDialog.setTitle("Preparando o celular");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Conectando ao dispositivo");
                mProgressDialog.show();

                Log.d(TAG, "Item Selecionado.");
                String deviceName = mBTDevices.get(position).getName();
                String deviceAddress = mBTDevices.get(position).getAddress();

                Log.d(TAG, "Nome do dispositivo = " + deviceName);
                Log.d(TAG, "Endereço do dispositivo = " + deviceAddress);

                //create the bond.
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){

                    Log.d(TAG, "Tentando parear com o dispositivo " + deviceName);
                    mBTDevices.get(position).createBond();

                    Log.d(TAG, "UUID: " + String.valueOf(mBTDevices.get(position).getUuids()[0].getUuid()));
                    uuid_device = mBTDevices.get(position).getUuids()[0].getUuid();

                    mBTDevice = mBTDevices.get(position);

                    if(mBTDevices.get(position).getBondState() == 12){
                        Log.d(TAG, "o dispositivo foi pareado com sucesso");

                        bundle.putString("device",uuid_device.toString());
                        bundle.putString("address", deviceAddress);

                        //Toast.makeText(BluetoothSearchActivity.this, "Dispositivo pareado com sucesso", Toast.LENGTH_LONG).show();
                        valid_device = true;
                        startService(uuid_device.toString(), deviceAddress);
                        mProgressDialog.dismiss();
                        if(valid_device) {
                            Intent quant_jogadores = new Intent(BluetoothSearchActivity.this,GameChoose.class);
                            quant_jogadores.putExtras(bundle);
                            startActivity(quant_jogadores);
                        }else {
                            Toast.makeText(BluetoothSearchActivity.this,"Um dispositivo deve ser selecionado", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        //Log.d(TAG, "Habilitando bluetooth");
        Toast.makeText(this, "Pesquisando dispositivos", Toast.LENGTH_SHORT).show();
        //enableDisableBT();

        Log.d(TAG, "pesquisando dispositivos");
        //btnDiscover();
    }

    @Override
    public void onResume(){
       super.onResume();
    }


    public void startService(String uuid, String address){
        Intent intent = new Intent(this, BluetoothConnectionService.class);
        intent.putExtra("uuid", uuid);
        intent.putExtra("address",address);
        startService(intent);
    }

    public void stopService(View view, Intent intent){
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Activity destruída.");
        mBTDevices = null;
        JsonControl jsonControl = new JsonControl();

        try {
            jsonControl.add_data("fim", "fim");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String tmp = jsonControl.json_prepare();
        byte[] data_send = tmp.getBytes(Charset.defaultCharset());

        Intent intent = new Intent("data_send");
        intent.putExtra("data_to_send",data_send);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();

        if (registerReceive1Boolean) {
            unregisterReceiver(mBroadcastReceiver1);
        }
        if (registerReceive2Boolean) {
            unregisterReceiver(mBroadcastReceiver2);
        }
        if (registerReceive3Boolean) {
            unregisterReceiver(mBroadcastReceiver3);
        }
        if (registerReceive4Boolean) {
            unregisterReceiver(mBroadcastReceiver4);
        }
    }


    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        btnDiscover();
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }

            registerReceive1Boolean = true;
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }

            registerReceive2Boolean = true;
        }
    };


    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //List<String> nomes = new ArrayList<String>();

            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Boolean device_existent = false;

                mBTDevices.add(device);

                if(device.getName().equalsIgnoreCase("hc-05")){
                    //do connect();
                }

                for (String temp : mDeviceList) {
                    if(temp.equals(device.getName() + "\n" + device.getAddress())){
                        device_existent = true;
                    }
                }

                if(!device_existent){
                    mDeviceList.add(device.getName() + "\n" + device.getAddress());
                    listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, mDeviceList));
                    Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                }

            }

            registerReceive3Boolean = true;
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    Toast.makeText(BluetoothSearchActivity.this, "pareado com dispositivo: " + mDevice.getName(), Toast.LENGTH_SHORT).show();
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                    Toast.makeText(BluetoothSearchActivity.this, "pareando com dispositivo: " + mDevice.getName(), Toast.LENGTH_LONG).show();
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                    Toast.makeText(BluetoothSearchActivity.this, "não pareado com dispositivo: " + mDevice.getName(), Toast.LENGTH_LONG).show();
                }

                registerReceive4Boolean = true;
            }
        }
    };

    public void btnDiscover(/*View v*/) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        if (!enable) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "btnDiscover: Cancelando descoberta.");

                //check BT permissions in manifest
                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                Log.d(TAG, "btnDiscover: Iniciando descoberta.");
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }

            if (!mBluetoothAdapter.isDiscovering()) {

                //check BT permissions in manifest
                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                Log.d(TAG, "btnDiscover: Starting Discovery.");
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }

            enable = true;
        } else {
            if(mBluetoothAdapter.isDiscovering()){
                mBluetoothAdapter.cancelDiscovery();
            }

            mBluetoothAdapter.startDiscovery();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}
