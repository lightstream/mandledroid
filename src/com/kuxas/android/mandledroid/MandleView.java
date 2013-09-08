package com.kuxas.android.mandledroid;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class MandleView extends View {
	final int maxiterations = 100;
	private int subdivisions = 1;
	private double rmin, rmax, rfactor;
	private double cmin, cmax, cfactor;
	private int screenwidth, screenheight;
	private Rect subdiv;
	private Paint divStyle = new Paint(); 
	private Random randomGenerator = new Random();

	public MandleView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);

		init(ctx);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		double rc = 0, cc = 0;
		int escapeCount;
		
		super.onDraw(canvas);
		
		// Calculate width & height of each subdivision
		int subwidth = screenwidth / subdivisions;
		if (subwidth < 1) {
			subwidth = 1;
		}
		int subheight = screenheight / subdivisions;
		if (subheight < 1) {
			subheight = 1;
		}
		
		// These convert complex numbers to pixels
		rfactor = (rmax - rmin) / screenwidth;
		cfactor = (cmax - cmin) / screenheight;
		
		// Calculate current viewspace complex co-ords
		
		for (int divx = 0; divx < screenwidth; divx += subwidth) {
			// New constant value is centre point of subdiv - y is real, x is complex
			rc = rmin + (divx + subwidth / 2) * rfactor;

			for (int divy = 0; divy < screenheight; divy += subheight) {
				// New constant value is centre point of subdiv - y is real, x is complex
				cc = cmax - (divy + subheight / 2) * cfactor;
				
				// Determine iterations before escape 
				escapeCount = escapesMandelbrot(rc, cc);
				
				// Set colour
				divStyle.setColor(colorForIteration(escapeCount));
				
				// Draw rectangle for current subdivision
				subdiv.set(divx, divy, divx+subwidth, divy+subheight);
				canvas.drawRect(subdiv, divStyle);
			}
		}
		
		if (subwidth > 1 || subheight > 1) {
			subdivisions *= 3;
			invalidate();
		}
	}
	
	/**
	 * Returns the number of iterations before escaping the Mandelbrot set with the given c value.
	 * 
	 * @param rc The real part of c.
	 * @param cc The imaginary part of c.
	 * 
	 * @return Number of iterations - zero means doesn't escape
	 */
	private int escapesMandelbrot(double rc, double cc) {
		int iteration = 0;
		// Set z = c 
		double rz = rc, cz = cc, rtemp;
		// We actually calculate magnitude^2
		double magnitude;
		
		do {
			iteration++;
			
			magnitude = rz*rz + cz*cz;
			
			// Stop if magnitude^2 > 4 
			if (magnitude > 4) {
				break;
			}
			
			// Determine z for the next iteration
			rtemp = rz*rz - cz*cz + rc;
			cz = 2.0 * rz * cz + cc;
			rz = rtemp;
		} while (iteration < maxiterations);
		
		if (magnitude <= 4) {
			// Hasn't escaped before maxiterations
			iteration = 0;
		}
		
		return iteration;
	}
	
	/**
	 * The colour to denote number of iterations before escape.
	 * 
	 * @param iterations
	 * 
	 * @return Integer colour value
	 */
	private int colorForIteration(int iterations) {
		int itcolor;
//		double red = 0, green = 0, blue = 0;
//		
//		if (iterations < maxiterations / 2) {
//			red = (double)iterations * 2.0 / (double)maxiterations * 255.0;
//		} else {
//			red = 255;
//			green = (double)iterations / (double)maxiterations * 255.0;
//			blue = (double)iterations / (double)maxiterations * 255.0;
//		}
		
//		return Color.argb(255, 255, 0,0);
//		itcolor = Color.argb(255, (int)red, (int)green, (int)blue);
		itcolor = 0xFF000000 + (int) ((double)iterations / (double)maxiterations * 0xFFFFFF);
		
		return itcolor;
	}

	private void init(Context ctx) {
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		screenwidth = display.getWidth();
		screenheight = display.getHeight();
		
		// Set initial complex domain
		rmin = -2;
		rmax = 2;
		cmin = -4;
		cmax = cmin + (rmax - rmin) * screenheight / screenwidth;
	
		subdiv = new Rect();
	}
}
