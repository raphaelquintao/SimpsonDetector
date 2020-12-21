package br.raphael.detector;




import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.os.Bundle;

public class Draw extends View {
	Bitmap mBitmap;
	Paint mPaintBlack;
	Paint mPaintYellow;
	Paint mPaintRed;
	Paint mPaintGreen;
	Paint mPaintBlue;
	byte[] mYUVData;
	int[] mRGBData;
	int mImageWidth, mImageHeight;
	int[] mRedHistogram;
	int[] mGreenHistogram;
	int[] mBlueHistogram;
	double[] mBinSquared;
	Bitmap ok;
	long time_start;
		
    public Draw(Context context) {
        super(context);
        
        mPaintBlack = new Paint();
        mPaintBlack.setStyle(Paint.Style.FILL);
        mPaintBlack.setColor(Color.BLACK);
        mPaintBlack.setTextSize(40);
        
        mPaintYellow = new Paint();
        mPaintYellow.setStyle(Paint.Style.FILL);
        mPaintYellow.setColor(Color.YELLOW);
        mPaintYellow.setTextSize(25);
        
        mPaintRed = new Paint();
        mPaintRed.setStyle(Paint.Style.FILL);
        mPaintRed.setColor(Color.RED);
        mPaintRed.setTextSize(40);
        
        mPaintGreen = new Paint();
        mPaintGreen.setStyle(Paint.Style.FILL);
        mPaintGreen.setColor(Color.GREEN);
        mPaintGreen.setTextSize(25);
        
        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.BLUE);
        mPaintBlue.setTextSize(25);
        
        
    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
    	time_start = SystemClock.currentThreadTimeMillis();
    	if (mBitmap != null)
        {
	    	String tag = "onDraw";
			Log.v(tag, "Called");
    	

	    	
        }
    	
    	long delta_time = SystemClock.currentThreadTimeMillis() - time_start;
    	
    	String toPrint = delta_time + " ms" + " - " ;
    	canvas.drawText( toPrint, 11, 700, mPaintBlack);
    	//canvas.drawText( toPrint, 11, 700, mPaintRed);
    	
        super.onDraw(canvas);
        
    } // end onDraw method
    
    
} 
