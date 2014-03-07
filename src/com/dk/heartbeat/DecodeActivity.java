/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dk.heartbeat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import wseemann.media.FFmpegMediaMetadataRetriever;

import com.dk.heartbeat.DecodeActivity.MyAsyncTask;

import android.R.color;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DecodeActivity extends Activity {
	/** The main dataset that includes all the series that go into a chart. */
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	/** The main renderer that includes all the renderers customizing a chart. */
	/** The most recently added series. */
	private XYSeries mCurrentSeriesR;
	private XYSeries mCurrentSeriesG;
	private XYSeries mCurrentSeriesB;
	/** The most recently created renderer, customizing the current series. */

	private GraphicalView mChartView;
	// private static final String path = Environment
	// .getExternalStorageDirectory()
	// + "/DCIM/Camera/VID_20140216_011502.mp4";

	 private static final String path = "/mnt/sdcard/Download/VID_20140307_231413.mov";
//	private static final String path = "/mnt/sdcard/Download/VID_20140216_011502.mp4";
	// private static final String log =
	// Environment.getExternalStorageDirectory()
	// + "/DCIM/Camera/log.txt";
	public static final String log_R = Environment
			.getExternalStorageDirectory() + File.separator + "Data_R";

	public static final String log_G = Environment
			.getExternalStorageDirectory() + File.separator + "Data_G";

	public static final String log_B = Environment
			.getExternalStorageDirectory() + File.separator + "Data_B";
	private XYMultipleSeriesRenderer mRenderer;
	private int[] R;
	private int[] G;
	private int[] B;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.dk.heartbeat.R.layout.xy_chart);
		int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLUE };
		PointStyle[] styles = new PointStyle[] { PointStyle.POINT,
				PointStyle.POINT, PointStyle.POINT };
		mRenderer = new XYMultipleSeriesRenderer(3);
		setRenderer(mRenderer, colors, styles);

		mRenderer.setZoomButtonsVisible(true);
		mCurrentSeriesR = new XYSeries("R");
		mCurrentSeriesG = new XYSeries("G");
		mCurrentSeriesB = new XYSeries("B");
		mDataset.addSeries(mCurrentSeriesR);
		mDataset.addSeries(mCurrentSeriesG);
		mDataset.addSeries(mCurrentSeriesB);

		File video = new File(path);
		if (!video.exists()) {
			Toast.makeText(this, "file doesn't exist", 200).show();
			return;
		}
		MyAsyncTask task = new MyAsyncTask();
		task.execute();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(com.dk.heartbeat.R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			// enable the chart click events
			mRenderer.setClickEnabled(true);
			mRenderer.setSelectableBuffer(10);
			mChartView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// handle the click event on the chart
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(DecodeActivity.this, "No chart element",
								Toast.LENGTH_SHORT).show();
					} else {
						// display information of the clicked point
						Toast.makeText(
								DecodeActivity.this,
								"Chart element in series index "
										+ seriesSelection.getSeriesIndex()
										+ " data point index "
										+ seriesSelection.getPointIndex()
										+ " was clicked"
										+ " closest point value X="
										+ seriesSelection.getXValue() + ", Y="
										+ seriesSelection.getValue(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			boolean enabled = mDataset.getSeriesCount() > 0;
		} else {
			mChartView.repaint();
		}
	}

	class MyAsyncTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {

			decode();
			return null;
		}

		private void decode() {

			FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
			retriever.setDataSource(path);
			// 取得视频的长度(单位为毫秒)
			String time = retriever
					.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);

			// 取得视频的长度(单位为秒)
			int seconds = Integer.valueOf(time) / 1000;
			// 得到每一秒时刻的bitmap比如第一秒,第二秒
			R = new int[seconds * 5];
			G = new int[seconds * 5];
			B = new int[seconds * 5];
			for (int i = 0; i < seconds * 5; i++) {
				Bitmap bitmap=null;
				try {
					bitmap = retriever.getFrameAtTime(
							((long) i) * 1000 * 1000 / 5,
							MediaMetadataRetriever.OPTION_CLOSEST);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (bitmap != null) {
					int[] pixels = new int[bitmap.getWidth()
							* bitmap.getHeight()];
					bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0,
							bitmap.getWidth(), bitmap.getHeight());
					bitmap.recycle();
					addFrameData(i, pixels);
				} else {
					// 取得视频的长度(单位为毫秒)
					System.out.println("error");
					return;
				}
			}

			System.out.println("---------------");
			writeDataToFile(log_R, R);
			writeDataToFile(log_G, G);
			writeDataToFile(log_B, B);
			System.out.println("Decode successful");
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
			RGB obj = (RGB) values[0];
			mCurrentSeriesR.add(obj.index, obj.r);
			mCurrentSeriesG.add(obj.index, obj.g);
			mCurrentSeriesB.add(obj.index, obj.b);
			// repaint the chart such as the newly added point to be visible
			if (obj.index % 10 == 0) {
				mChartView.repaint();

			}
		}

		private int getR(int px) {
			return ((px >> 16) & 0xFF);
		}

		private int getG(int px) {
			return ((px >> 8) & 0xFF);
		}

		private int getB(int px) {
			return (px & 0xFF);
		}

		private void addFrameData(final int index, int[] pixels) {
			int r = 0, g = 0, b = 0;
			for (int i = 0; i < pixels.length; i++) {
				int px = pixels[i];
				r += getR(px);
				g += getG(px);
				b += getB(px);
			}
			R[index] = r / pixels.length;
			G[index] = g / pixels.length;
			B[index] = b / pixels.length;
			System.out.println("frame<" + index + ">:" + "R(" + R[index] + ") "
					+ "G(" + G[index] + ") " + "B(" + B[index] + ") ");
			// System.out.println("" + R[index]);

			publishProgress(new RGB(index, R[index], G[index], B[index]));
		}

		private void writeDataToFile(String path, int[] data) {
			String rString = "";
			for (int j = 0; j < data.length; j++) {
				// System.out.println("" + R[j]);
				rString += (data[j] + ",");
			}
			rString = rString.substring(0, rString.length() - 1);
			try {
				File f = new File(path);
				if (!f.exists()) {
					f.createNewFile();
				}

				FileWriter fw = new FileWriter(f);
				fw.write(rString);
				fw.flush();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class RGB {
		RGB(int index, int r, int g, int b) {
			this.index = index;
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public int r, g, b;
		public int index;
	}

	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles) {
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
	}

}