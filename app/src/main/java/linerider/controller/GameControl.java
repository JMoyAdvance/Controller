package linerider.controller;

import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.bluetooth.BluetoothSocket;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import java.io.IOException;
import java.util.UUID;

public class GameControl extends AppCompatActivity
{

    Button start, left, right;
    String address = null;
    BluetoothAdapter BA = null;
    BluetoothSocket BS = null;
    private ProgressDialog progress;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        Intent newint = getIntent();
        address = newint.getStringExtra(Controller.EXTRA_ADDRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_control);

        start = (Button) findViewById(R.id.start);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        
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

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public void startGame()
    {
        if (BS!=null)
        {
            try
            {
                BS.getOutputStream().write("0".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    public void moveLeft()
    {
        if (BS!=null)
        {
            try
            {
                BS.getOutputStream().write("1".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    public void moveRight()
    {
        if (BS!=null)
        {
            try
            {
                BS.getOutputStream().write("2".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(GameControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (BS == null || !isBtConnected)
                {
                    BA = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = BA.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    BS = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    BS.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}

