package ru.barsic.avlab.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import ru.barsic.avlab.graphics.DrawView;
import ru.barsic.avlab.mechanics.*;
import ru.barsic.avlab.molecular.Thermometer;

public class WorkActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(DrawView.createInstance(this));
		Thermometer thermometer = new Thermometer(1, 1, 0.7, 4.5);
		Dynamometer dynamometer = new Dynamometer(2, 1, 0.8, 4);

		Support support = new Support(4.5, 0.5, 0.6, 7);
		Orb orb1 = new Orb(10, 7, 0.8, 0.8, 10, Color.RED);
		Orb orb2 = new Orb(12, 7, 0.8, 0.8, 15, Color.YELLOW);
		Cube cube = new Cube(13.5, 6.5, 1.2, 1.2, 0.5, Color.rgb(192, 192, 192));
	}
}