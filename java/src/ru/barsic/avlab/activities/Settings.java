package ru.barsic.avlab.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import ru.barsic.avlab.R;
import ru.barsic.avlab.mechanics.*;
import ru.barsic.avlab.molecular.Thermometer;

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
				if(((CheckBox)findViewById(R.id.support)).isChecked())
					new Support(4.5, 0.5, 0.6, 7);
				if(((CheckBox)findViewById(R.id.thermometer)).isChecked())
					new Thermometer(1, 1, 0.7, 4.5);
				if(((CheckBox)findViewById(R.id.dynamometer)).isChecked())
					new Dynamometer(2, 1, 0.8, 4);
				if(((CheckBox)findViewById(R.id.cube)).isChecked())
					new Cube(13.5, 6.5, 1.2, 1.2, 2, Color.rgb(192, 192, 192));
				if(((CheckBox)findViewById(R.id.orb1)).isChecked())
					new Orb(10, 7, 1, 1, 1, Color.RED);
				if(((CheckBox)findViewById(R.id.orb2)).isChecked())
					new Orb(12, 7, 0.8, 0.8, 0.5, Color.YELLOW);
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