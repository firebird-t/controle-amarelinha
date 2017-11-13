package app.leonardo.controle_amarelinha;

/**
 * Created by Leonardo on 04/10/2017.
 */

import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;


public class BluetoothConnectionService extends Service{
    private static final String TAG = "BluetoothConnectionServ";
    protected static final int MESSAGE_READ = 1;
    private static final String appName = "Controle Amarelinha";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;
    private Boolean conn_status = false;
    private ConnectedThread mConnectedThread;
    private Intent it;

    /*
    public BluetoothConnectionService(Context context) {
        //start();
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(Context context, int startId){
        mContext = context;
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId){
        mContext = getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceUUID = UUID.fromString(intent.getStringExtra("uuid"));
        mmDevice = mBluetoothAdapter.getRemoteDevice(intent.getStringExtra("address"));
        LocalBroadcastManager.getInstance(
                BluetoothConnectionService.this).registerReceiver(
                mReceiver_writer,new IntentFilter("data_send"));

        //Abre o socket para receber dados do bluetooth
        start();

        //inicia comunicação com o cliente
        startClient(mmDevice, deviceUUID);

        it = new Intent("data_receive");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy(){
        super.onDestroy();
        mConnectThread.cancel();
        mConnectedThread.cancel();
        unregisterReceiver(mReceiver_writer);
    }
    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }

        Log.d(TAG,"Service running");
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "AcceptThread: configurando o servidor usando o UUID: " + MY_UUID_INSECURE);
            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: AcceptThread rodando.");

            BluetoothSocket socket = null;

            try{
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start.....");

                socket = mmServerSocket.accept();

                Log.d(TAG, "run: RFCOM server socket aceitou a conexão.");

            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            //talk about this is in the 3rd
            if(socket != null){
                connected(socket,mmDevice);
            }

            Log.i(TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage() );
            }
        }

    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private UUID uuid;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: Iniciada.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "mConnectThread rodando");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Tentado criar uma InsecureRfcommSocket usando o uuid: " + deviceUUID );
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();

                Log.d(TAG, "run: ConnectThread connectado!.");
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }

            //will talk about this in the 3rd video
            connected(mmSocket,mmDevice);

        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    /**
     AcceptThread starts and sits waiting for a connection.
     Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
     **/

    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "Iniciando cliente.");

        //initprogress dialog
        //mProgressDialog = ProgressDialog.show(mContext,"Conectando ao dispositivo","Por favor, espere...",true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    /**
     Finally the ConnectedThread which is responsible for maintaining the BTConnection, Sending the data, and
     receiving incoming data through input/output streams respectively.
     **/

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Iniciada.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //dismiss the progressdialog when connection is established
            /*try{
                mProgressDialog.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }*/


            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            String entrada = null;
            int contador = 0;
            while (true) {
                if (mmSocket.isConnected()) {// Read from the InputStream
                    try {
                        bytes = mmInStream.read(buffer);
                        contador++;
                        if (entrada == null) {
                            entrada = (new String(buffer, 0, bytes)).trim();
                        } else {
                            entrada += (new String(buffer, 0, bytes)).trim();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                        break;
                    }

                    //Log.d("Entrada de dados ", entrada + " Contador:" + contador);

                    if (entrada.contains("}")) {
                        //Log.d("Entrada de dados ", entrada + " Contador:" + contador);
                        it.putExtra("data_rec",entrada);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(it);
                        entrada = null;
                    }
                }
            }
        }


        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "Saída: " + text);

            if(mmSocket.isConnected()){
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) {
                    Log.e(TAG, "write: erro ao escrever para stream de saída. " + e.getMessage() );
                }
            }

        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

        Intent conn_status = new Intent("SendMessage");
        conn_status.putExtra("conection",true);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(conn_status);
        Log.d(TAG,"Local Broadcast Called");

    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */

    BroadcastReceiver mReceiver_writer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //public void write(byte[] out) {
                // Create temporary object
                ConnectedThread r;
                byte[] out = intent.getByteArrayExtra("data_to_send");
                // Synchronize a copy of the ConnectedThread
                Log.d(TAG, "write: Write Called.");
                //perform the write
                mConnectedThread.write(out);
            //}
        }
    };

}


