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

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedOutputStream;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Sales demo bar chart.
 */
public class StackedBarChart extends AbstractDemoChart {
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public GraphicalView createView(Context context, double[] datas1,
			double[] datas2, double[] datas3) {
		String[] titles = new String[] { "R", "G", "B" };
		List<double[]> values = new ArrayList<double[]>();
		values.add(datas1);
		values.add(datas2);
		values.add(datas3);
		int[] colors = new int[] { 0x77ff0000, 0x7700ff00, 0x770000ff };
		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		setChartSettings(renderer, "Heart Rate", "sample", "Units sold", 0.5,
				12.5, 0, 120, Color.GRAY, Color.LTGRAY);
		renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
		renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
		renderer.getSeriesRendererAt(2).setDisplayChartValues(true);
		renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setXLabelsAlign(Align.LEFT);
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setPanEnabled(true, false);
		// renderer.setZoomEnabled(false);
		renderer.setZoomRate(1.1f);
		renderer.setBarSpacing(0.5f);
		return ChartFactory.getBarChartView(context,
				buildBarDataset(titles, values), renderer, Type.STACKED);

	}

	@Override
	public Intent execute(Context context) {
		return null;
	}

}
