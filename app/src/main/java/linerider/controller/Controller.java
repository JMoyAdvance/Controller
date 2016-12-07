package linerider.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;

public class Controller extends AppCompatActivity {

    Button start, left, right;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        start = (Button)findViewById(R.id.start);
        left = (Button)findViewById(R.id.left);
        right = (Button)findViewById(R.id.right);

        BA = BluetoothAdapter.getDefaultAdapter();
      //  lv = (ListView)findViewById(R.id.);
    }

    public void on(View V){
        if(!BA.isEnabled()){

        }
    }
}
