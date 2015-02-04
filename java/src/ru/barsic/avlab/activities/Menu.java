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
		Button btnActTwo = (Button)findViewById(R.id.start);
		btnActTwo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Menu.this, WorkActivity.class);
				startActivity(intent);
			}
		});
	}
}
