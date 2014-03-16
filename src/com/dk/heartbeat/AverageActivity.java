package com.dk.heartbeat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AverageActivity extends Activity {
	public static final String FFT_R = Environment
			.getExternalStorageDirectory() + File.separator + "FFT_R";
	public static final String FFT_G = Environment
			.getExternalStorageDirectory() + File.separator + "FFT_G";
	public static final String FFT_B = Environment
			.getExternalStorageDirectory() + File.separator + "FFT_B";

	private ArrayList<Double> result_G = new ArrayList<Double>();
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	/** The main renderer that includes all the renderers customizing a chart. */
	/** The most recently added series. */
	private XYSeries mCurrentSeriesFFT;
	// private XYSeries mCurrentSeriesFFT2;
	// private XYSeries mCurrentSeriesFFT3;
	/** The most recently created renderer, customizing the current series. */

	private GraphicalView mChartView;

	private XYMultipleSeriesRenderer mRenderer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(com.dk.heartbeat.R.layout.xy_chart);

		int[] colors = new int[] { Color.RED };
		PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
		mRenderer = new XYMultipleSeriesRenderer(1);
		setRenderer(mRenderer, colors, styles);

		mRenderer.setZoomButtonsVisible(true);
		mCurrentSeriesFFT = new XYSeries("Average");
		// mCurrentSeriesFFT2 = new XYSeries("FFT2");
		// mCurrentSeriesFFT3 = new XYSeries("FFT3");
		mDataset.addSeries(mCurrentSeriesFFT);
		// mDataset.addSeries(mCurrentSeriesFFT2);
		// mDataset.addSeries(mCurrentSeriesFFT3);

		try {
			getData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		double[] datas_G = new double[result_G.size()];
		for (int i = 0; i < datas_G.length; i++) {
			datas_G[i] = result_G.get(i);
		}
		double[] fleet = fleetData(datas_G);
		//
		for (int i = 0; i < fleet.length; i++) {
			System.out.println(fleet[i]);
			mCurrentSeriesFFT.add(i, fleet[i]);
		}
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
						Toast.makeText(AverageActivity.this,
								"No chart element", Toast.LENGTH_SHORT).show();
					} else {
						// display information of the clicked point
						Toast.makeText(
								AverageActivity.this,
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

	private void getData() throws FileNotFoundException {
		File inputR = new File(DecodeActivity.log_R);
		String textR = readTextFile(new FileInputStream(inputR));
		String[] dataR = textR.split(",");

		File inputG = new File(DecodeActivity.log_G);
		String textG = readTextFile(new FileInputStream(inputG));
		String[] dataG = textG.split(",");

		File inputB = new File(DecodeActivity.log_B);
		String textB = readTextFile(new FileInputStream(inputB));
		String[] dataB = textB.split(",");

		double[] fftInput = new double[768];
		for (int i = 0; i < 768; i++) {
			if (i < 256) {
				fftInput[i] = Double.parseDouble(dataR[i]);
			} else if (i >= 256 && i < 512) {
				fftInput[i] = Double.parseDouble(dataG[i - 256]);
			} else if (i >= 512 && i < 768) {
				fftInput[i] = Double.parseDouble(dataB[i - 512]);
			}
		}

		double[] fftOutput = new double[768];

		DoubleFFT_1D fft = new DoubleFFT_1D(4096);
		JadeRJni.doSth(fftInput, fftOutput);

		double[] o1 = new double[4096];
		double[] o2 = new double[4096];
		double[] o3 = new double[4096];
		System.arraycopy(fftInput, 0, o1, 0, 256);
		System.arraycopy(fftInput, 256, o2, 0, 256);
		System.arraycopy(fftInput, 512, o3, 0, 256);

		double[] oo1 = new double[4096];
		double[] oo2 = new double[4096];
		double[] oo3 = new double[4096];
		for (int i = 0; i < 60 / 2; i++) {
			for (int j = 0; j < 150; j++) {

				oo1[j] = o1[5 * i + j];
				oo2[j] = o2[5 * i + j];
				oo3[j] = o3[5 * i + j];

			}

			fft.realForward(oo1);
			fft.realForward(oo2);
			fft.realForward(oo3);

			// result_R.add(getArea(oo1));
			result_G.add(getArea(oo2));
			// result_B.add(getArea(oo3));
		}
	}

	private double getArea(double[] array) {
		// 120>5/4096*max*60>40
		int max = 1638;
		int min = 614;
		double a = 0;
		int index = 0;
		for (int i = min; i < max; i++) {
			if (array[i] > a) {
				index = i;
				a = array[i];
			}
		}
		return 5.0 / 4096.0 * index * 60.0;
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

	private double[] fleetData(double[] data) {
		double[] res = new double[data.length];

		for (int i = 0; i < data.length; i++) {
			if (i != 0) {
				res[i] = sum(data, i) / i;
			} else {
				res[0] = data[0];
			}
		}

		return res;
	}

	private double sum(double[] data, int n) {
		double sum = 0;
		for (int i = 0; i < n; i++) {
			sum += data[i];
		}
		return sum;
	}

}
