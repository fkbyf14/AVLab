package ru.barsic.avlab.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import ru.barsic.avlab.basic.World;
import ru.barsic.avlab.graphics.DrawView;
import ru.barsic.avlab.helper.Logging;
import ru.barsic.avlab.mechanics.*;
import ru.barsic.avlab.molecular.Glass;
import ru.barsic.avlab.molecular.Thermometer;
import ru.barsic.avlab.physics.Scene;

public class WorkActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(DrawView.createInstance(this));
		if (Scene.objects.isEmpty()) {
			Scene scene = new Scene(0, World.WORLD_HEIGHT - 2, World.WORLD_WIDTH, 2);
			Thermometer thermometer = new Thermometer(4, 9, 0.7, 4.5, 0.05);
			Dynamometer dynamometer = new Dynamometer(5, 9, 0.8, 4);

			Support support = new Support(7.5, 8.5, 0.6, 7);
			Orb orb1 = new Orb(12, 15.5, 1, 1, 1, Color.RED);
			Cube cube = new Cube(15, 15, 1.2, 1.2, 2, Color.rgb(192, 192, 192));
			Orb orb2 = new Orb(14, 15.5, 0.8, 0.8, 0.5, Color.YELLOW);
			Balance balance = new Balance(18, 14, 9, 1.5);
			Weight w1 = new Weight(20.2, 15, 1, 1.2, 0.5);
			Weight w11 = new Weight(20, 15, 1, 1.2, 0.5);
			Weight w2 = new Weight(19.2, 15.2, 0.8, 1, 0.2);
			Weight w22 = new Weight(19, 15.2, 0.8, 1, 0.2);
			Weight w3 = new Weight(18.2, 15.4, 0.6, 0.8, 0.1);
			Weight w33 = new Weight(18.4, 15.4, 0.6, 0.8, 0.1);
			Weight w4 = new Weight(17.6, 15.6, 0.4, 0.6, 0.05);
			Weight w44 = new Weight(17.8, 15.6, 0.4, 0.6, 0.05);
			Weight w5 = new Weight(17, 15.7, 0.4, 0.5, 0.02);
			Weight w55 = new Weight(17.2, 15.7, 0.4, 0.5, 0.02);
			Weight w6 = new Weight(16.6, 15.8, 0.4, 0.4, 0.01);
			Weight w66 = new Weight(16.4, 15.8, 0.4, 0.4, 0.01);
			Glass glass = new Glass(8, 12 , 2, 3, 0.1);

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

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_BACK){
//			for(PhysObject object : Scene.objects) {
//				Logging.log("BUTTON", object, " " );
//			}
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

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




