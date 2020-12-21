/**
 * Simpson Detector - PUC Minas - 2013
 * This project is a academic work for the discipline of Digital Image Processing of Computer Science course.
 * @author Raphael Quintão Silveira
 * @version 0.3 05/2013  
 */

package br.raphael.detector;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;

import android.util.Log;

public class SimpsonDetector {
	private static final String TAG = "Debug Detection";
	
	//Basicos
	private Mat Frame;
	private double MinDist;
	private List<Mat> Images;
	private List<Point> usedKP;
	private List<KeyPoint> kp;
	private boolean[] flags;
	
	//Personagens
	private int Good;
	
	//Keypoints
	private MatOfKeyPoint FrameKeypoints;
	private MatOfKeyPoint TesteKeypoints;
	private MatOfKeyPoint AtualKeypoints;
	
	//Armazedores de Descricao
	private Mat FrameDescriptors;
	private List<Mat> AtualDescriptors;
	private Mat previousDescriptors;
	private List<MatOfDMatch> AtualMatch = new ArrayList<MatOfDMatch>();
	
	
	//Feature Detector
	private FeatureDetector FD;
	//Extrator de Descricao
	private DescriptorExtractor Extractor;
	//Descriptors Matcher
	private DescriptorMatcher Matcher;
	//Colors
	
	
	/**
	 * Constroi o objeto para a deteccao em tempo real do frame da camera.
	 */
	SimpsonDetector(){
		flags = new boolean[3];
		flags[0] = false;
		flags[1] = false;
		flags[2] = false;
				
		MinDist = 2.1;
		FrameKeypoints = new MatOfKeyPoint();
		FrameDescriptors = new Mat();
		
		usedKP = new ArrayList<Point>();

		Good = 0;
		FD = FeatureDetector.create(FeatureDetector.FAST);
		Extractor = DescriptorExtractor.create(FeatureDetector.SURF);
		Matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
	}
	/**
	 * Setar o frame atual.
	 * @param frame Recebe o frame da comera em RGB
	 */
	public void setFrame(Mat frame){
		Frame = frame;
	}
	/**
	 * Processa o frame atual, para extrair keypoints e descritores.
	 */
	public void ProcessFrame(){
		FD.detect(Frame, FrameKeypoints);
		Extractor.compute(Frame, FrameKeypoints, FrameDescriptors);
		kp = FrameKeypoints.toList();
	}
	/**
	 * Usa o descritor recebido e compara com o descritor do frame usando K-Nearest Neighbors (K-Vizinhos).
	 * @param PreviousDescriptors Recebe descritor de uma imagem para comparação.
	 * @param name Recebe nome a ser mostrado quando o objeto for detectado.
	 */
	public boolean Process(Mat PreviousDescriptors, String name){
		//Process Camera Frame
		previousDescriptors = PreviousDescriptors;
		Matcher.knnMatch(FrameDescriptors, previousDescriptors, AtualMatch, 2);
		
		double minY = 9999, maxY = 0, minX = 9999, maxX = 0;
		
		for(int i = 0; i < AtualMatch.size(); i++ ){
	    	DMatch[] atual = AtualMatch.get(i).toArray();
	    	
	    	for(int j = 0; j < AtualMatch.get(i).rows(); j++){
	    		if(atual[0].distance * 2.0 < atual[1].distance  ){
	    			
	
    				Point ptAtual = kp.get(atual[0].queryIdx).pt;
    				
    				if(true || !usedKP.contains(ptAtual) ){
    					Good++;
    					usedKP.add(ptAtual);
    					if(flags[1]){
    						double x = ptAtual.x;
	    					double y = ptAtual.y;
	    				
		    				if(x < minX){
		    					minX = x;
		    				}else if(x > maxX ){
		    					maxX = x;
		    				} 
		    				if(y < minY){
		    					minY = y;
		    				}else if(y > maxY ){
		    					maxY = y;
		    				}
	    				}
    				}
    				if(flags[0]){//DrawMatches
    					Core.circle(Frame, kp.get(atual[0].queryIdx).pt ,6, new Scalar(255, 0, 255));
    				}
		    	    	
		    			
	    		}
	    	}
	    	//i++;
	    }
		int pts = 9;
		
		if (FrameKeypoints.size().height > 5000)
			pts = 17;
		else if (FrameKeypoints.size().height > 9000)
			pts = 30;
		
		
		if( Good >=pts){
			return true;
		}else{
			Good = 0;
		}
		
		return false;
	}
	/**
	 * Habilita o desenho de do Keypoints que "batem".
	 */
	public void DrawMatches(){
		flags[0] = true;
	}
	/**
	 * Habilita o desenho de um quadrado em volta do objeto detectado.
	 */
	public void DrawSquare(){
		flags[1] = true;
	}
	//private native boolean P(Mat previous, String name);
	//private native boolean P(Mat Previous, String Name);
	public boolean clean(){
		Good=0;
		return true;
	}
	public int getGood(){
		int x = Good;
		Good = 0;
		return x;
	}
	public void DrawKeypoints(){
		Features2d.drawKeypoints(Frame, FrameKeypoints, Frame);
		//Features2d.drawKeypoints(Frame, FrameKeypoints, Frame, new Scalar(255, 255, 255), Features2d.DRAW_RICH_KEYPOINTS);
	}
	/**
	 * Desenha um texto com proposito de debugar.
	 * Não sera usado no programa final.
	 */
	public void Debug(){
		Log.d(TAG, "Total Keypoints"+ FrameKeypoints.size().height);
		Core.putText(Frame, "Total Keypoints: " + FrameKeypoints.size(), new Point(10, 100), 5, 1.8, new Scalar(255, 255, 255));
		
	}
	/**
	 * Retorna o Frame processado.
	 * @return Frame com desenhos e textos.
	 */
	public Mat getFrame(){
		return Frame;
	}
	//----------------------------------------------------------------------------------------------------------
	/**
	 * Constroi o objeto para previamente gerar os descritores e os keypoints das imagens base.
	 * Evitando o delay na deteccao em tempo real do frame da camera. 
	 * @param homers Recebe a lista de imagens.
	 */
	SimpsonDetector(List<Mat> images){
		Images = images;
		AtualKeypoints = new MatOfKeyPoint();
		AtualDescriptors = new ArrayList<Mat>();
		FD = FeatureDetector.create(FeatureDetector.FAST);
		Extractor = DescriptorExtractor.create(FeatureDetector.SURF);
		Matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
	}
	/**
	 * Extrai keypoints e Descritores das imagens.
	 * Para recuperar os Keypoints chame getKeypoints.
	 * Para recuperar os Descritores chame getDescriptors.
	 */
	public void ComputeImages(){
		for(int c = 0; c < Images.size(); c++){
			Mat atual = Images.get(c);
			Mat atualDesc = new Mat();
			FD.detect(atual, AtualKeypoints);
			Extractor.compute(atual, AtualKeypoints, atualDesc);
			AtualDescriptors.add(atualDesc);
		}
		
		
	}
	/**
	 * Pega os keypoits referentes as imagens passadas.
	 * Primeiro e necessario chamar ComputeImages.
	 * @return Keypoints referentes a imagem.
	 */
	public Mat getKeypoints(){
		return AtualKeypoints;
	}
	/**
	 * Pegos os descritores referentes as imagens passadas.
	 * @return Descritores referentes as imagens.
	 */
	public List<Mat> getDescriptors(){
		return AtualDescriptors;
	}

}