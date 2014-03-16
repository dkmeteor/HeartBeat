package com.dk.heartbeat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.decode).setOnClickListener(this);
		findViewById(R.id.fft).setOnClickListener(this);
		findViewById(R.id.bar_chat).setOnClickListener(this);
		findViewById(R.id.average).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		Intent intent = null;
		switch (view.getId()) {
		case R.id.decode:
			intent = new Intent(this, DecodeActivity.class);
			startActivity(intent);
			break;
		case R.id.fft:
			intent = new Intent(this, FFTActivity.class);
			startActivity(intent);
			break;
		case R.id.bar_chat:
			intent = new Intent(this, HeartRateActivity.class);
			startActivity(intent);
			break;
		case R.id.average:
			intent = new Intent(this, AverageActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
