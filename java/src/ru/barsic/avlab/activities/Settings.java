package ru.barsic.avlab.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import ru.barsic.avlab.R;
import ru.barsic.avlab.basic.World;
import ru.barsic.avlab.mechanics.*;
import ru.barsic.avlab.molecular.Thermometer;
import ru.barsic.avlab.physics.Scene;

public class Settings extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		System.out.println("*****Settings.onCreate");

		Button btnActOne = (Button)findViewById(R.id.ok);
		btnActOne.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					new Scene(0, World.WORLD_HEIGHT - 2, World.WORLD_WIDTH, 2);
				if(((CheckBox)findViewById(R.id.support)).isChecked())
					new Support(7.5, 8.5, 0.6, 7);
				if(((CheckBox)findViewById(R.id.thermometer)).isChecked())
					new Thermometer(4, 9, 0.7, 4.5, 0.05);
				if(((CheckBox)findViewById(R.id.dynamometer)).isChecked())
					new Dynamometer(5, 9, 0.8, 4);
				if(((CheckBox)findViewById(R.id.cube)).isChecked())
					new Cube(15, 15, 1.2, 1.2, 2, Color.rgb(192, 192, 192));
				if(((CheckBox)findViewById(R.id.orb)).isChecked()) {
					new Orb(12, 15.5, 1, 1, 1, Color.RED);
					new Orb(14, 15.5, 0.8, 0.8, 0.5, Color.YELLOW);
				}
				if(((CheckBox)findViewById(R.id.balance)).isChecked())
					new Balance(18, 14, 9, 1.5);
				if(((CheckBox)findViewById(R.id.weight)).isChecked()){
					new Weight(20.2, 15, 1, 1.2, 0.5);
					new Weight(20, 15, 1, 1.2, 0.5);
					new Weight(19.2, 15.2, 0.8, 1, 0.2);
					new Weight(19, 15.2, 0.8, 1, 0.2);
					new Weight(18.2, 15.4, 0.6, 0.8, 0.1);
					new Weight(18.4, 15.4, 0.6, 0.8, 0.1);
					new Weight(17.6, 15.6, 0.4, 0.6, 0.05);
					new Weight(17.8, 15.6, 0.4, 0.6, 0.05);
					new Weight(17, 15.7, 0.4, 0.5, 0.02);
					new Weight(17.2, 15.7, 0.4, 0.5, 0.02);
					new Weight(16.6, 15.8, 0.4, 0.4, 0.01);
					new Weight(16.4, 15.8, 0.4, 0.4, 0.01);
				}
				Intent intent = new Intent(Settings.this, WorkActivity.class);
				startActivity(intent);
			}
		});
	}
	@Override
	public void onRestart() {
		super.onRestart();
		System.out.println("*****Settings.onRestart");
	}
	@Override
	public void onResume() {
		super.onResume();
		System.out.println("*****Settings.onResume");
	}
	@Override
	public void onPause() {
		super.onPause();
		System.out.println("*****Settings.onPause");
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("*****Settings.onDestroy");
	}
}