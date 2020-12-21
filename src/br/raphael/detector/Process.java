package br.raphael.detector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import br.raphael.extended.CameraBridgeViewBaseExtended.CvCameraViewFrame;

public class Process {
	
	public static void CircleDetection(CvCameraViewFrame Frame, Mat retorno){
		Mat circles = new Mat();
		Point pt;
		int radius;
		
		Imgproc.GaussianBlur(Frame.gray(), retorno , new org.opencv.core.Size(9, 9), 2, 2 );
    	Imgproc.HoughCircles(retorno, circles, Imgproc.CV_HOUGH_GRADIENT, 2.0, retorno.rows() / 8, 100, 150, 10, 400);
    	
    	retorno = Frame.rgba();
    	if (circles.cols() > 0){
    		//Toast.makeText(this,"Detected"  , Toast.LENGTH_SHORT).show();
    		for (int x = 0; x < circles.cols(); x++){
    	        double vCircle[] = circles.get(0,x);

    	        if (vCircle == null)
    	            break;

    	        pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
    	        radius = (int)Math.round(vCircle[2]);

    	        // draw the found circle
    	        Core.circle(retorno, pt, radius, new Scalar(255,255,0));
    	        Core.circle(retorno, pt, 3, new Scalar(0,0,255));
    	        }
    	}
    	Core.putText(retorno, "Circulos: " + circles.cols(), new Point(10,100), 5, 1.8, new Scalar(255, 255, 0, 255));	
	}
	

	
}





