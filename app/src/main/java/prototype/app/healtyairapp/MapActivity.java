package prototype.app.healtyairapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

public class MapActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "1";
    Button exploreButton, backToBlutoothButton, getAirQualityButton;

    //public static Button infoLabel;
    boolean[] tools;
    int[] measurements = new int[5];
    int[] disiases = new int[6];
    public static String urlString;
    public static String notificationText = "";
    public static double lat, lon;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static String nameSurname = "Mr/Mrs. Sir/Madam";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        exploreButton = findViewById(R.id.exploreButton);
        backToBlutoothButton = findViewById(R.id.back_to_blutooth_activity);
        //infoLabel = findViewById(R.id.infoLabel);
        getAirQualityButton = findViewById(R.id.getAirQuality);
        //Initilize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //latlon = findViewById(R.id.latlon);

        //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(intent);
        statusCheck();
        try {
            getDataFromDisiaseSelection();
            getDataViaBlutooth();
        } catch (Exception e) {
            System.out.println("" + e);
        }

        exploreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                sendDataGetResultFromServer();

                if (ActivityCompat.checkSelfPermission(MapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //When permission granted
                    getLocation();
                    //latlon.setText("izinvar");
                } else {
                    //latlon.setText("izin yok");
                    //When permission denied
                    ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }

                getAirQualityButton.setEnabled(true);

            }
        });
        getAirQualityButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                notifyUser();
            }
        });
        backToBlutoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackToBlutooth = new Intent(MapActivity.this, BlutoothActivity.class);
                startActivity(intentBackToBlutooth);
            }
        });

        //Initilize fragment
        Fragment fragment = new MapsFragment();

        //Open fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initilize location
                Location location = task.getResult();
                if (location != null) {

                    try {
                        //Initilize geoCoder
                        Geocoder geocoder = new Geocoder(MapActivity.this,
                                Locale.getDefault());
                        //Initilize address list
                        List<Address> adresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        lat = adresses.get(0).getLatitude();
                        lon = adresses.get(0).getLongitude();
                        //latlon.setText(""+lat+lon);
                        MapsFragment.addMarker("I am here", lat, lon);
                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                }

            }
        });

    }
    private void takeActionsAccordingToResult() {

        //notifications will be implemented in this method
    }

    private void sendDataGetResultFromServer() {

        urlString = String.format("https://firla-277516.ey.r.appspot.com/?meas1=%d&meas2=%d&meas3=%d&meas4=%d&meas5=%d&" +
                        "dis1=%d&dis2=%d&dis3=%d&dis4=%d&dis5=%d&dis6=%d", measurements[0],measurements[1],measurements[2],measurements[3],measurements[4],
                disiases[0],disiases[1],disiases[2],disiases[3],disiases[4],disiases[5]);

        FetchData process = new FetchData();

        process.execute();
    }

    private void getDataViaBlutooth() {
        //Random datas are assigned
        //Bluttoth implementation  is not done

        try{
            Intent intentName = getIntent();
            Bundle bundle = intentName.getExtras();
            measurements = bundle.getIntArray("btValues");
        }catch (Exception e){
            measurements[0] = 65;
            measurements[1] = 75;
            measurements[2] = 85;
            measurements[3] = 95;
            measurements[4] = 105;
            System.out.println(""+e);
        }
    }

    private void getDataFromDisiaseSelection() {
        Intent intentName = getIntent();
        Bundle bundle = intentName.getExtras();
        tools = bundle.getBooleanArray("tools");
        nameSurname = bundle.getString("nameSurname");
        for(int i = 0; i < tools.length; i++){
            disiases[i] = (tools[i] == false) ? 0 : 1 ;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notifyUser(){
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,0);
        Notification notification=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText("Air Quality Info")
                .setContentTitle(notificationText)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.sym_action_chat,"Title",pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .build();

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);

    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}