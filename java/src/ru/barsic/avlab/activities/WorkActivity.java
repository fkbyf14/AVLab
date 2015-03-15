package ru.barsic.avlab.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import ru.barsic.avlab.basic.PhysObject;
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
			Balance balance = new Balance(15, 7, 9, 1.5);

			Weight w1 = new Weight(17, 9.5, 1, 1.2, 0.5);
			Weight w2 = new Weight(16, 9.5, 0.8, 1, 0.2);
			Weight w3 = new Weight(15.2, 9.5, 0.6, 0.8, 0.1);
			Weight w4 = new Weight(14.6, 9.5, 0.4, 0.6, 0.05);
			Weight w5 = new Weight(14, 9.5, 0.3, 0.5, 0.02);

//			Button btn = (Button)findViewById(R.id.info);
//
//			btn.setOnClickListener((View.OnClickListener)this);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			for(PhysObject object : Scene.objects) {
				Logging.log("BUTTON", object, " " );
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
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




