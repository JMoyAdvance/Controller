package linerider.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Bluetooth
{
    private Handler myHandler;
    BluetoothDevice myDevice;

    ConnectThread connectThread;
    ConnectedThread connectedThread;

    String TAG = "LandTiger Board";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    protected Bluetooth()
    {

        myHandler = new android.os.Handler();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null)
            Log.d("Bluetooth Set up", "Failed");
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            // Add the name and address to an array adapter to show in a ListView
            if(Objects.equals(device.getAddress(), "20:16:04:06:37:94"))
                myDevice = device;
            System.out.println(device.getName() + "\n" + device.getAddress());
        }
        this.connect();
    }

    public void connect() {
        Log.d(TAG, "Connecting to: " + myDevice.getName() + " - " + myDevice.getAddress());
        // Start the thread to connect with the given device

        connectThread = new ConnectThread(myDevice);
        connectThread.start();
    }

    public void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "connected to: " + device.getName());

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    private class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device)
        {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                UUID uuid = myUUID;
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "Create RFcomm socket failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            try
            {
                mmSocket.connect();
            }
            catch(IOException connectException)
            {
                Log.e(TAG, "Unable to connect", connectException);
                cancel();
                return;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch(IOException e)
            {
                Log.e(TAG, "Unable to close socket during connection failure", e);

            }
        }

    }
    public void write(byte[] out) {
        ConnectedThread send = connectedThread;
        send.write(out);
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch(IOException e)
            {

            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    myHandler.obtainMessage(2, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                myHandler.obtainMessage(3, -1, -1, bytes).sendToTarget();

            } catch (IOException e) { }
        }


        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
