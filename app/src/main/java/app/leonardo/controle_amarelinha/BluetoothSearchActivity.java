package app.leonardo.controle_amarelinha;


//https://github.com/mitchtabian/Bluetooth---How-to-Pair/
// blob/master/Bluetooth-HowToPair/app/src/main/
// java/com/example/user/bluetooth_howtopair/MainActivity.java

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothSearchActivity extends MainActivity{

    private static final String TAG = "MainActivity";
    public DeviceListAdapter mDeviceListAdapter;
    ArrayList<String> mDeviceList = new ArrayList<String>();
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    BluetoothConnectionService mBluetoothConnection;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;
    Button btnEnableDisable_Discoverable;
    Button btnEnable;
    Button Discover;
    ListView listView;
    private ProgressDialog progressDialog;
    private Boolean registerReceive1Boolean = false;
    private Boolean registerReceive2Boolean = false;
    private Boolean registerReceive3Boolean = false;
    private Boolean registerReceive4Boolean = false;
    private Boolean enable = false;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private UUID uuid_device;
    Button btnStartConnection;
    Button btnSend;
    EditText etSend;

    @Override
    public void onResume(){
       super.onResume();
//        if (registerReceive1Boolean)
//            unregisterReceiver(mBroadcastReceiver1);
//
//        if (registerReceive2Boolean)
//            unregisterReceiver(mBroadcastReceiver2);
//
//        if (registerReceive3Boolean)
//            unregisterReceiver(mBroadcastReceiver3);
//
//        if (registerReceive4Boolean)
//            unregisterReceiver(mBroadcastReceiver4);
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

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_search);

        listView = (ListView)findViewById(R.id.listview_bt);
        Discover = (Button)findViewById(R.id.btnDiscover);
        btnStartConnection = (Button) findViewById(R.id.btnStartConnection);
        btnSend = (Button) findViewById(R.id.btnSend);
        etSend = (EditText) findViewById(R.id.editText);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);
        registerReceive4Boolean = true;

        //mBTDevices = new ArrayList<>();
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

        btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnection();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes = etSend.getText().toString().getBytes(Charset.defaultCharset());
                etSend.setText("");
                mBluetoothConnection.write(bytes);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBluetoothAdapter.cancelDiscovery();

                Log.d(TAG, "onItemClick: You Clicked on a device.");
                String deviceName = mBTDevices.get(position).getName();
                String deviceAddress = mBTDevices.get(position).getAddress();

                Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                //create the bond.
                //NOTE: Requires API 17+? I think this is JellyBean
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                    Log.d(TAG, "Trying to pair with " + deviceName);
                    mBTDevices.get(position).createBond();
                    //mBTDevices.get(position).setPairingConfirmation(true);
                    Log.d(TAG, "UUID: " + String.valueOf(mBTDevices.get(position).getUuids()[0].getUuid()));
                    uuid_device = mBTDevices.get(position).getUuids()[0].getUuid();

                    //Log.d(TAG, String.valueOf(mBTDevices.get(position)));
                    mBTDevice = mBTDevices.get(position);
                    if(mBTDevices.get(position).getBondState() == 12){
                        Log.d(TAG, "o dispositivo foi pareado com sucesso");
                        mBluetoothConnection = new BluetoothConnectionService(BluetoothSearchActivity.this);
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


    public void btnDiscover(/*View v*/) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        Toast.makeText(this, "Pesquisando dispositivos não pareados", Toast.LENGTH_SHORT).show();
        if (!enable) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "btnDiscover: Canceling discovery.");

                //check BT permissions in manifest
                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                Log.d(TAG, "btnDiscover: Starting Discovery.");
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

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     * <p>
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
   // @RequiresApi(api = Build.VERSION_CODES.M)
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

    //create method for starting connection
//***remember the conncction will fail and app will crash if you haven't paired first
    public void startConnection(){

        startBTConnection(mBTDevice, uuid_device);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }
}
