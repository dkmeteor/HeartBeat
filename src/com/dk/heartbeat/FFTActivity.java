package com.dk.heartbeat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class FFTActivity extends Activity {
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	/** The main renderer that includes all the renderers customizing a chart. */
	/** The most recently added series. */
	private XYSeries mCurrentSeriesFFT;
	/** The most recently created renderer, customizing the current series. */

	private GraphicalView mChartView;

	private XYMultipleSeriesRenderer mRenderer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.dk.heartbeat.R.layout.xy_chart);

		int[] colors = new int[] { Color.RED };
		PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
		mRenderer = new XYMultipleSeriesRenderer(1);
		setRenderer(mRenderer, colors, styles);

		mRenderer.setZoomButtonsVisible(true);
		mCurrentSeriesFFT = new XYSeries("FFT");
		mDataset.addSeries(mCurrentSeriesFFT);

		FFT();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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
						Toast.makeText(FFTActivity.this, "No chart element",
								Toast.LENGTH_SHORT).show();
					} else {
						// display information of the clicked point
						Toast.makeText(
								FFTActivity.this,
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

	private void FFT() {
		String text;
		try {
			File input = new File(DecodeActivity.log_R);
			text = readTextFile(new FileInputStream(input));
			String[] datas = text.split(",");

			double[] fftInput = new double[256];
			for (int i = 0; i < 256; i++) {
				if (i < 256) {
					fftInput[i] = Double.parseDouble(datas[i]);
				}
			}
			DoubleFFT_1D fft = new DoubleFFT_1D(256);
			fft.realForward(fftInput);

			for (int i = 0; i < 256; i++) {
				System.out.println("" + fftInput[i]);
				if (Math.abs(fftInput[i]) < 100) {
					mCurrentSeriesFFT.add(i, fftInput[i]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// mChartView.repaint();
	}

	private String readTextFile(InputStream inputStream) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte buf[] = new byte[1024];

		int len;

		try {

			while ((len = inputStream.read(buf)) != -1) {

				outputStream.write(buf, 0, len);

			}

			outputStream.close();

			inputStream.close();

		} catch (IOException e) {

		}

		return outputStream.toString();

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
