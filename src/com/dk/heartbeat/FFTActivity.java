package com.dk.heartbeat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class FFTActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		FFT();
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
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}
