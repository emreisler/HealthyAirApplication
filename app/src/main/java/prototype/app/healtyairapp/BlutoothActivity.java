package prototype.app.healtyairapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BlutoothActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    static final    UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    TextView deviceFound,meas1Text,meas2Text,meas3Text,meas4Text,meas5Text,status;
    Button goMapButton,get_measurements;
    Button backToNameButton,search_blutooth_devices_button;
    boolean[] tools;
    int[] bluetoothValues = new int[5];
    BluetoothAdapter btAdapter;
    public static String MACAdress = "00:19:10:08:2A:14";
    public String nameSurname = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blutooth);
        goMapButton = findViewById(R.id.next_to_map_activity);
        backToNameButton = findViewById(R.id.back_to_name_activity);
        deviceFound = findViewById(R.id.deviceFound);
        meas1Text = findViewById(R.id.meas1Text);
        meas2Text = findViewById(R.id.meas2Text);
        meas3Text = findViewById(R.id.meas3Text);
        meas4Text = findViewById(R.id.meas4Text);
        meas5Text = findViewById(R.id.meas5Text);
        status = findViewById(R.id.status);
        search_blutooth_devices_button = findViewById(R.id.search_blutooth_devices_button);
        get_measurements = findViewById(R.id.get_measurements);


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            //Means your Device doesn' t support bluetooth
            status.setText("Device doesn't support Bluetooth");
        }

        if (!btAdapter.isEnabled()) {
            //Request to open blutooth from your phone
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        try{
            getDataFromDisiaseSelection();
        }catch(Exception e){
            System.out.println(""+e);
        }

        search_blutooth_devices_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean hc05Found = false;
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {

                        String deviceName = device.getName();
                        String MACAdress = device.getAddress(); // MAC address
                        if (deviceName.contains("HC-05") || deviceName.contains("hc-05")){
                            deviceFound.setText("Device found : " + deviceName + " --- " + MACAdress);
                            hc05Found = true;
                        }
                    }
                }
                if(!hc05Found){
                    deviceFound.setText("Device found : NO HC05_DEVICE_FOUND" );
                }
            }
        });
        bluetoothValues[0] = 10;
        bluetoothValues[1] = 20;
        bluetoothValues[2] = 30;
        bluetoothValues[3] = 40;
        bluetoothValues[4] = 50;
        get_measurements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_measurements.setText("PLEASE WAIT...");
                BluetoothDevice hc05 = btAdapter.getRemoteDevice(MACAdress);
                BluetoothSocket btSocket= null;
                int counter = 0;
                do{
                    try{
                        btSocket = hc05.createRfcommSocketToServiceRecord(myUUID);
                        //btsocket yazdır
                        btSocket.connect();
                        status.setText("Socket conection : "+btSocket.isConnected());
                    }catch (IOException e ){
                        e.printStackTrace();
                    }
                    counter++;
                }while(!btSocket.isConnected() && counter < 3);
                try{
                    OutputStream outputStream = btSocket.getOutputStream();
                    outputStream.write(48);
                }catch (IOException e ){
                    e.printStackTrace();
                }
                String result = "";
                try {
                    InputStream inputStream = btSocket.getInputStream();
                    inputStream.skip(inputStream.available());

                    for(int i = 0; i < 5; i++){
                        byte b = (byte) inputStream.read();
                        result += b +"," ;

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try{
                    String[] measurementStrings = result.split(",");
                    meas1Text.setText("Measurement 1 : " + measurementStrings[0]);
                    meas2Text.setText("Measurement 2 : " + measurementStrings[1]);
                    meas3Text.setText("Measurement 3 : " + measurementStrings[2]);
                    meas4Text.setText("Measurement 4 : " + measurementStrings[3]);
                    meas5Text.setText("Measurement 5 : " + measurementStrings[4]);


                    bluetoothValues[0] = Integer.parseInt(measurementStrings[0]);
                    bluetoothValues[1] = Integer.parseInt(measurementStrings[1]);
                    bluetoothValues[2] = Integer.parseInt(measurementStrings[2]);
                    bluetoothValues[3] = Integer.parseInt(measurementStrings[3]);
                    bluetoothValues[4] = Integer.parseInt(measurementStrings[4]);


                }catch(Exception e ){
                    meas1Text.setText("Measurement 1(simulating value) : " + 10);
                    meas2Text.setText("Measurement 2(simulating value) : " + 20);
                    meas3Text.setText("Measurement 3(simulating value) : " + 30);
                    meas4Text.setText("Measurement 4(simulating value) : " + 40);
                    meas5Text.setText("Measurement 5(simulating value) : " + 50);
                }

                try{
                    btSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
                get_measurements.setText("GET AIR MEASUREMENTS");
            }
        });



        goMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMap = new Intent(BlutoothActivity.this,MapActivity.class);
                intentToMap.putExtra("tools",tools);
                intentToMap.putExtra("btValues", bluetoothValues);
                intentToMap.putExtra("nameSurname",nameSurname);
                startActivity(intentToMap);
            }
        });

        backToNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToName = new Intent(BlutoothActivity.this, NameActivity.class);
                startActivity(intentToName);
            }
        });
    }

    private void getDataFromDisiaseSelection() {
        Intent intentName = getIntent();
        Bundle bundle = intentName.getExtras();
        tools = bundle.getBooleanArray("tools");
        nameSurname = bundle.getString("nameSurname");


    }




}