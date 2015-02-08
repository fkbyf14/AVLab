package ru.barsic.avlab.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import ru.barsic.avlab.R;

public class Menu extends Activity {
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btnActOne = (Button)findViewById(R.id.start);
		btnActOne.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Menu.this, WorkActivity.class);
				startActivity(intent);
			}
		});
		Button btnActThree = (Button)findViewById(R.id.exit);
		btnActThree.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				System.exit(0);
			}
	});
		Button btnActTwo = (Button)findViewById(R.id.configuration);
		btnActTwo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Menu.this, Settings.class);
				startActivity(intent);
			}
		});
	}
	@Override
	public void onRestart() {
		super.onRestart();
		System.out.println("*****Menu.onRestart");
	}
	@Override
	public void onResume() {
		super.onResume();
		System.out.println("*****Menu.onResume");
	}
	@Override
	public void onPause() {
		super.onPause();
		System.out.println("*****Menu.onPause");
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("*****Menu.onDestroy");
	}
}
