package com.dk.heartbeat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class HeartRateActivity extends Activity {
	private ArrayList<Double> result_R = new ArrayList<Double>();
	private ArrayList<Double> result_G = new ArrayList<Double>();
	private ArrayList<Double> result_B = new ArrayList<Double>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.bar_chart);
		LinearLayout continer = (LinearLayout) findViewById(R.id.container);

		try {
			getData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		double[] datas_R = new double[result_R.size()];
		double[] datas_G = new double[result_G.size()];
		double[] datas_B = new double[result_B.size()];
		for (int i = 0; i < datas_R.length; i++) {
			datas_R[i] = result_R.get(i);
			datas_G[i] = result_G.get(i);
			datas_B[i] = result_B.get(i);
		}

		continer.addView(new StackedBarChart().createView(this, datas_R,
				datas_G, datas_B));

		System.out.println("R:" + avarge(datas_R));
		System.out.println("G:" + avarge(datas_G));
		System.out.println("B:" + avarge(datas_B));

		double[] fleet = fleetData(datas_R);

		for (double d : fleet) {
			System.out.println(d);
		}
	}

	private double avarge(double[] datas) {
		double sum = 0;
		for (int i = 0; i < datas.length; i++) {
			sum += datas[i];
		}
		return sum / datas.length;
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

			result_R.add(getArea(oo1));
			result_G.add(getArea(oo2));
			result_B.add(getArea(oo3));

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
			res[i] = sum(data, i) / i;
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
