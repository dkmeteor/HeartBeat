package com.dk.heartbeat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {
	private static final String KEY_VIDEO_PATH = "KEY_VIDEO_PATH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.decode).setOnClickListener(this);
		findViewById(R.id.capture).setOnClickListener(this);
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
		case R.id.capture:
			intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// create
																	// a
			// intent to
			// record
			// video
			Uri fileUri = getOutputMediaFileUri(); // create a file Uri to save
													// the
													// video

			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
			startActivityForResult(intent, 101);
			break;
		default:
			break;
		}
	}

	/** Create a File Uri for saving a video */
	private static Uri getOutputMediaFileUri() {
		// get the mobile Pictures directory
		File picDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		// get the current time
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File videoFile = new File(picDir.getPath() + File.separator + "VIDEO_"
				+ timeStamp + ".mp4");
		return Uri.fromFile(videoFile);
	}

	private void save(String key, String value) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private String get(String key) {
		return PreferenceManager.getDefaultSharedPreferences(this).getString(
				key, null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// TODO
			save(KEY_VIDEO_PATH, "");
		}
	}
}
