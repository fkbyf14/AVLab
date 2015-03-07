package ru.barsic.avlab.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import ru.barsic.avlab.graphics.DrawView;
import ru.barsic.avlab.helper.Logging;
import ru.barsic.avlab.mechanics.*;
import ru.barsic.avlab.molecular.Thermometer;
import ru.barsic.avlab.physics.Scene;

public class WorkActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(DrawView.createInstance(this));
		if (Scene.objects.isEmpty()) {
			Thermometer thermometer = new Thermometer(1, 1, 0.7, 4.5);
			Dynamometer dynamometer = new Dynamometer(2, 1, 0.8, 4);

			Support support = new Support(4.5, 0.5, 0.6, 7);
			Orb orb1 = new Orb(10, 7, 1, 1, 1, Color.RED);
			Cube cube = new Cube(13.5, 6.5, 1.2, 1.2, 2, Color.rgb(192, 192, 192));
			Orb orb2 = new Orb(12, 7, 0.8, 0.8, 0.5, Color.YELLOW);
			//Button btn = (Button)findViewById(R.id.info);
			Balance balance = new Balance(15, 7, 9, 1.5);
			Weight w1 = new Weight(15, 9.5, 0.8, 1, 0.5);
			Weight w2 = new Weight(16, 9.5, 1, 1.2, 0.7);
			Weight w3 = new Weight(17.5, 9.5, 0.5, 0.7, 0.3);
			//btn.setOnClickListener((View.OnClickListener)this);
//			btn.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					for (PhysObject object : Scene.objects) {
//						Logging.log("BUTTON", object, "");
//					}
//				}
//			});
		}
	}


			@Override
			public void onRestart () {
				super.onRestart();
				System.out.println("*****WorkActivity.onRestart");
			}
			@Override
			public void onResume () {
				super.onResume();
				System.out.println("*****WorkActivity.onResume");
			}
			@Override
			public void onPause () {
				super.onPause();
				Logging.closeStream();
				System.out.println("*****WorkActivity.onPause");
			}
			@Override
			public void onDestroy () {
				super.onDestroy();
				Scene.objects.clear();
				Scene.parents.clear();
				DrawView.painters.clear();
				System.out.println("*****WorkActivity.onDestroy");
			}


		}




