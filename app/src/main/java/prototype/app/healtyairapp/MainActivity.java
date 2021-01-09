package prototype.app.healtyairapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity {
    Button doneButton;
    CheckBox dis1CheckBox,dis2CheckBox,dis3CheckBox,dis4CheckBox,dis5CheckBox,dis6CheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doneButton = findViewById(R.id.next_to_name_activity);
        dis1CheckBox = findViewById(R.id.dis1);
        dis2CheckBox = findViewById(R.id.dis2);
        dis3CheckBox = findViewById(R.id.dis3);
        dis4CheckBox = findViewById(R.id.dis4);
        dis5CheckBox = findViewById(R.id.dis5);
        dis6CheckBox = findViewById(R.id.dis6);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean[] tools = new boolean[6];
                tools[0] = dis1CheckBox.isChecked();
                tools[1] = dis2CheckBox.isChecked();
                tools[2] = dis3CheckBox.isChecked();
                tools[3] = dis4CheckBox.isChecked();
                tools[4] = dis5CheckBox.isChecked();
                tools[5] = dis6CheckBox.isChecked();

                //Start intent to name activity and send colected datas from this page
                Intent intentName = new Intent(MainActivity.this, NameActivity.class);
                intentName.putExtra("tools",tools);
                startActivity(intentName);
            }
        });
    }
}