package linerider.controller;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.view.View;
import android.app.ProgressDialog;
import java.util.Objects;
import java.util.Set;

public class GameControl extends AppCompatActivity
{

    Button start, left, right;
    String address = null;
    BluetoothSocket BS = null;
    private ProgressDialog progress;
    private boolean isBtConnected = false;
    Bluetooth bluetoothService;
    BluetoothDevice Device;

    private Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_control);
        start = (Button) findViewById(R.id.start);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                if(Objects.equals(device.getAddress(), "20:16:03:25:62:42"))
                    Device = device;
                System.out.println(device.getName() + "\n" + device.getAddress());
            }
        }
        android.os.Handler handler = new android.os.Handler();
        bluetoothService = new Bluetooth();
        bluetoothService.connect();

        start.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View V)
            {
                startGame();
            }
        });

        left.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View V)
            {
                moveLeft();
            }
        });

        right.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View V)
            {
                moveRight();
            }
        });
    }

    private void startGame()
    {
        byte[] message =  Integer.toString(0).getBytes();
        bluetoothService.write(message);
    }

    private void moveLeft()
    {
        byte[] message =  Integer.toString(1).getBytes();
        bluetoothService.write(message);
    }

    private void moveRight()
    {
        byte[] message =  Integer.toString(2).getBytes();
        bluetoothService.write(message);
    }
}

