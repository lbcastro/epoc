package com.castro.epoc;

import android.content.Context;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.LineGraphView;


public class Graphic {

	private GraphView graphView;
//	private static GraphView barGraphView;

	public GraphView initGraphic(Context c) {
		graphView = new LineGraphView(c, "") {
			   @Override  
			   protected String formatLabel(double value, boolean isValueX) {  
			      if (isValueX) {  
			         return "";  
			      } else return super.formatLabel(value, isValueX);
			   }  
		};
		graphView.setShowLegend(true);
		graphView.setLegendAlign(LegendAlign.BOTTOM);
		graphView.setLegendWidth(80.0f);
		graphView.setOrientation(0);
		
		graphView.setScrollable(true);
		graphView.setScalable(true);
		
		return graphView;
	}
	
//	public static GraphView initBarGraphic(Context c) {
//		barGraphView = new BarGraphView(c, "") {
//			   @Override  
//			   protected String formatLabel(double value, boolean isValueX) {  
//			      if (isValueX) {  
//			         return super.formatLabel(value, isValueX);  
//			      } else return "";
//			   } 
//		};
//		return barGraphView;
//	}
}
