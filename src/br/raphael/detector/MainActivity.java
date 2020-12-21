/**
 * Simpson Detector - PUC Minas - 2013
 * This project is a academic work for the discipline of Digital Image Processing of Computer Science course.
 * @author Raphael Quintão Silveira
 * @version 0.3 05/2013  
 */

package br.raphael.detector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.raphael.extended.AdapterTwoLinesImage;
import br.raphael.extended.ViewExtended;
import br.raphael.extended.CameraBridgeViewBaseExtended.CvCameraViewFrame;
import br.raphael.extended.CameraBridgeViewBaseExtended.CvCameraViewListener2;


public class MainActivity extends Activity implements CvCameraViewListener2, OnTouchListener, OnClickListener, OnItemClickListener {
    
	
	//Variaveis Basicas - Basic Variables
	private static final String TAG = "Main Activity: ";
	private ViewExtended CameraView;
    private Handler mainHandler;
    //Loading para FullDetection
    private LinearLayout Loading;
    //Botoes - Buttons
    private Button FullDetection;
    private Button CleanList;
    //Menu de Opções - Options Menu
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;
    private SubMenu Mode;
    private MenuItem[] ModeItens;
    private SubMenu Debug;
    private MenuItem[] DebugItens;
    //Personagens(Pré-Carregamento) - Characters(Pre-Load)
    private SimpsonDetector previousDetector;
    private Mat homer;
    private Mat homer2;
    private Mat marge;
    private List<Mat> preLoadedImages;
    private boolean[] DetectedChars;
    //Controle de Thread
    private boolean[] threadControl;
    //Outros
    //Others
    private TextView Status;
    private String ModeValue;
    private int teste1 = 1, teste2 = 1;
    private boolean debug;
    //Lista de Objetos
    //Object List
    private ListView DetectedObj;
	private List<String> Objetos;
    private ArrayList<ObjectList> personal;
    private boolean[] objControl;

    
    
    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Chamado quando a Activity é criada.
     * Called when activity is created
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        CameraView = (ViewExtended) findViewById(R.id.ExtendedSurfaceView);

        CameraView.setVisibility(SurfaceView.VISIBLE);

        CameraView.setCvCameraViewListener(this);
        
        Loading = (LinearLayout) findViewById(R.id.Loading);
        Loading.setVisibility(View.GONE);
        
 		Status = (TextView) findViewById(R.id.text_status);
 		Status.setOnClickListener(this);
 		
 		FullDetection = (Button) findViewById(R.id.fullDetection);
        FullDetection.setOnClickListener(this);
        
        CleanList = (Button) findViewById(R.id.CleanList);
        CleanList.setOnClickListener(this);
        
        DetectedObj = (ListView) findViewById(R.id.DetectedObjects);
        DetectedObj.setOnItemClickListener(this);
        personal = new ArrayList<ObjectList>();
        objControl = new boolean[30];
        
        debug = false;
        
        //test
        //personal.add(new ObjectList(R.drawable.ic_launcher, "Ícone", "Teste"));
        
        
        AdapterTwoLinesImage adapter = new AdapterTwoLinesImage(getApplicationContext(), personal);
	    DetectedObj.setAdapter(adapter);
        
	    
	    //É definido um "handler" para responder a menssagens de outros Threads.
 		// Set a handler to answer messages from other threads.
	    mainHandler = new Handler(){
 			@Override
			public void handleMessage(Message msg) {
 				if(msg.what == 1 && !objControl[msg.what]){
 					personal.add(new ObjectList(R.drawable.homer_1, "Homer", msg.obj.toString(),"file:///android_asset/Homer.html"));
 					objControl[msg.what] = true;
 					AdapterTwoLinesImage adapter = new AdapterTwoLinesImage(getApplicationContext(), personal);
 					DetectedObj.setAdapter(adapter);
 				}else if(msg.what == 2 && !objControl[msg.what]){
 					personal.add(new ObjectList(R.drawable.marge_2, "Marge", msg.obj.toString(), "file:///android_asset/Homer.html"));
 					objControl[msg.what] = true;
 					AdapterTwoLinesImage adapter = new AdapterTwoLinesImage(getApplicationContext(), personal);
 				    DetectedObj.setAdapter(adapter);
 			    }else if(msg.what == 10 && !objControl[msg.what]){
					personal.add(new ObjectList(R.drawable.otto_1, "Otto Mann", msg.obj.toString(), "file:///android_asset/Homer.html"));
					objControl[msg.what] = true;
					AdapterTwoLinesImage adapter = new AdapterTwoLinesImage(getApplicationContext(), personal);
				    DetectedObj.setAdapter(adapter);
			    }else if(msg.what == 100){
			    	Loading.setVisibility(View.VISIBLE);
			    }else if(msg.what == 101){
			    	Loading.setVisibility(View.GONE);
			    }else if(msg.what == 200){
			    	if(!threadControl[Integer.parseInt(msg.obj.toString())])
			    		threadControl[Integer.parseInt(msg.obj.toString())] = true;
			    	else
				    	threadControl[Integer.parseInt(msg.obj.toString())] = false;
			    }
 				
			}
 		};
 		
 		//Initial Status
        ModeValue = "Parallel";
        //ModeValue = "Manual";
        
        
        //Status.setText("OK Computer");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (CameraView != null)
            CameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();        
        if(OpenCVLoader.initDebug()){
        	CameraView.enableView();
            CameraView.setOnTouchListener(MainActivity.this);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (CameraView != null)
            CameraView.disableView();
    }
    /**
     * Quando esse metodo for chamado ja teremos acesso a camera e podemos 
     * pre-carregar todo nosso banco de imagens usando o segundo construtor do objeto SimpsonsDetector.
     */
    public void onCameraViewStarted(int width, int height) {
    	Log.i(TAG, "Started.");
    	//Inicializa Variaveis para personagens
    	//Starts Characters Variables
    	homer = new Mat();
    	homer2 = new Mat();
    	Mat homer3 = new Mat();
    	marge = new Mat();
    	Mat marge2 = new Mat();
    	Mat ottoMann1 = new Mat();
    	Mat ottoMann2 = new Mat();
    	
    	preLoadedImages = new ArrayList<Mat>();
    	
        //Variaveis Temporarias
    	//Temp Variables
        Mat rawHomer1 = new Mat();
        Mat rawHomer2 = new Mat();
        Mat rawHomer3 = new Mat();
        Mat rawMarge1 = new Mat();
        Mat rawMarge2 = new Mat();
        Mat rawOttoMann1 = new Mat();
        Mat rawOttoMann2 = new Mat();
        
 		try {
 			rawHomer1 = Utils.loadResource(this, R.drawable.homer_1);
 			rawHomer2 = Utils.loadResource(this, R.drawable.homer_2);
 			rawHomer3 = Utils.loadResource(this, R.drawable.homer_3);
 			Imgproc.cvtColor(rawHomer1, homer, Imgproc.COLOR_RGB2BGR);
 			Imgproc.cvtColor(rawHomer2, homer2, Imgproc.COLOR_RGB2BGR);
 			Imgproc.cvtColor(rawHomer3, homer3, Imgproc.COLOR_RGB2BGR);
 			preLoadedImages.add(homer);//0
 			preLoadedImages.add(homer2);//1
 			preLoadedImages.add(homer3);//2
 			
 			rawMarge1 = Utils.loadResource(this, R.drawable.marge_1);
 			rawMarge2 = Utils.loadResource(this, R.drawable.marge_2);
 			Imgproc.cvtColor(rawMarge1, marge, Imgproc.COLOR_RGB2BGR);
 			Imgproc.cvtColor(rawMarge2, marge2, Imgproc.COLOR_RGB2BGR);
 			preLoadedImages.add(marge);//3
 			preLoadedImages.add(marge2);//4
 			
 			//Otto Man
 			rawOttoMann1 = Utils.loadResource(this, R.drawable.otto_1);
 			rawOttoMann2 = Utils.loadResource(this, R.drawable.otto_2);
 			Imgproc.cvtColor(rawOttoMann1, ottoMann1, Imgproc.COLOR_RGB2BGR);
 			Imgproc.cvtColor(rawOttoMann2, ottoMann2, Imgproc.COLOR_RGB2BGR);
 			preLoadedImages.add(ottoMann1);//5
 			preLoadedImages.add(ottoMann2);//6
 			
 			previousDetector = new SimpsonDetector(preLoadedImages);
 			previousDetector.ComputeImages();
 			
 			DetectedChars = new boolean[preLoadedImages.size()];
 			threadControl = new boolean[preLoadedImages.size()];
 			
 			
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
    }

    public void onCameraViewStopped() {
    }
    
    //Test Variables
    
    private Thread getThreadByName(String threadName)
    {
        Thread __tmp = null;
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        for (int i = 0; i < threadArray.length; i++)
            if (threadArray[i].getName().equals(threadName))
                __tmp =  threadArray[i];
        return __tmp;
    }
    
    
	// Test Variables END.
	
	
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {	
		Mat novo = new Mat();
    	 
    	Mat circles = new Mat();
    	
    	final Mat resp = inputFrame.rgba();
    	novo = inputFrame.gray();
    	

    	if(ModeValue.equals("Parallel")){
	    	
    		Thread Homer = new Thread(new Runnable() {
				@Override
				public void run() {
					SimpsonDetector SD = new SimpsonDetector();
			    	SD.setFrame(resp);
			    	
			    	SD.ProcessFrame();
			    	if ( SD.Process( previousDetector.getDescriptors().get(0), "Homer") ||
						 SD.Process( previousDetector.getDescriptors().get(1), "Homer") ||
						 SD.Process( previousDetector.getDescriptors().get(2), "Homer") ){
							DetectedChars[0] = true;
							DetectedChars[1] = true;
							DetectedChars[2] = true;
							Message m = new Message();
							m.what=1;
							m.obj=SD.getGood();
							mainHandler.sendMessage(m);
			    	
					
			    	}
			    	//SD.Debug();
				}
			},"Homer");

	    	if((getThreadByName("Homer") == null) && !DetectedChars[0] ){
	    		Homer.start();
	    	}
	    	
	    	Thread Marge = new Thread(new Runnable() {
				@Override
				public void run() {
					SimpsonDetector SD = new SimpsonDetector();
			    	SD.setFrame(resp);
			    	SD.ProcessFrame();
			    	if(SD.Process( previousDetector.getDescriptors().get(3), "Marge")||
					   SD.Process( previousDetector.getDescriptors().get(4), "Marge") ){
						Message m = new Message();
						m.what=2;
						m.obj=SD.getGood();
						mainHandler.sendMessage(m);
						DetectedChars[3] = true;
						DetectedChars[4] = true;
					}
			    	//SD.Debug();
				}
			},"Marge");
	    	if((getThreadByName("Marge") == null) && !DetectedChars[3]){
	    		Marge.start();
	    	}
	    	
	    	Thread Otto = new Thread(new Runnable() {
				@Override
				public void run() {
					SimpsonDetector SD = new SimpsonDetector();
			    	SD.setFrame(resp);
			    	SD.ProcessFrame();
			    	if( SD.Process( previousDetector.getDescriptors().get(6), "Otto Mann") ||
			    		SD.Process( previousDetector.getDescriptors().get(5), "Otto Mann") ){
			    		DetectedChars[5] = true;
			    		DetectedChars[6] = true;
						Message m = new Message();
						m.what=10;
						m.obj=SD.getGood();
						mainHandler.sendMessage(m);
					}
			    	//SD.Debug();
				}
			},"Otto");
	    	if((getThreadByName("Otto") == null) && !DetectedChars[5]){
	    		Otto.start();
	    	}
	    	
	    	
	    	
    	}else if(ModeValue.equals("Sequential")){
    		
    		SimpsonDetector SD = new SimpsonDetector();
	    	SD.setFrame(resp);
	    	SD.ProcessFrame();
	    	if(debug) SD.DrawMatches();
	    	
	    	if ( SD.Process( previousDetector.getDescriptors().get(0), "Homer") ||
				SD.Process( previousDetector.getDescriptors().get(1), "Homer") ||
				SD.Process( previousDetector.getDescriptors().get(2), "Homer") ){
				DetectedChars[0] = true;
				DetectedChars[1] = true;
				DetectedChars[2] = true;
				Message m = new Message();
				m.what=1;
				m.obj=SD.getGood();
				mainHandler.sendMessage(m);
	    	}else if(SD.Process( previousDetector.getDescriptors().get(3), "Marge")||
				SD.Process( previousDetector.getDescriptors().get(4), "Marge") ){
				Message m = new Message();
				m.what=2;
				m.obj=SD.getGood();
				mainHandler.sendMessage(m);
				DetectedChars[3] = true;
				DetectedChars[4] = true;
			}else if( SD.Process( previousDetector.getDescriptors().get(6), "Otto Mann") ||
	    		SD.Process( previousDetector.getDescriptors().get(5), "Otto Mann") ){
	    		DetectedChars[5] = true;
	    		DetectedChars[6] = true;
				Message m = new Message();
				m.what=10;
				m.obj=SD.getGood();
				mainHandler.sendMessage(m);
			}
	    	if(debug){ 
	    		SD.Debug(); 
	    	}
	    	SD.getFrame();
    	}else if(ModeValue.equals("Circle Detection")){
    		Process.CircleDetection(inputFrame, resp);
    		
    	}
	    
	    //Core.putText(resp, DetectedChars[0]+"", new Point(CameraView.getResolution().width-400,40), 5, 1.8, new org.opencv.core.Scalar(255, 0, 0, 255));
        return resp;
	}


    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    List<String> effects = CameraView.getEffectList();
	    //Set Options Menu
	    
	    Debug = menu.addSubMenu("Debug");
        DebugItens = new MenuItem[4];
        DebugItens[0] = Debug.add(4, 0, Menu.NONE,"Enable");
        DebugItens[1] = Debug.add(4, 0, Menu.NONE,"Disable");
        DebugItens[2] = Debug.add(4, 0, Menu.NONE,"Show FPS");
        DebugItens[3] = Debug.add(4, 0, Menu.NONE,"Hide FPS");
	    
	    Mode = menu.addSubMenu("Detection Mode");
	    ModeItens = new MenuItem[3];
	    ModeItens[0] = Mode.add(3, 0, Menu.NONE,"Parallel");
	    ModeItens[1] = Mode.add(3, 1, Menu.NONE,"Sequential");
	    ModeItens[2] = Mode.add(3, 2, Menu.NONE,"Circle Detection");
	    
	    if (effects == null) {
	        Log.e(TAG, "Color effects are not supported by device!");
	        return true;
	    }
	
	    mColorEffectsMenu = menu.addSubMenu("Color Effect");
	    mEffectMenuItems = new MenuItem[effects.size()];
	
	    int idx = 0;
	    ListIterator<String> effectItr = effects.listIterator();
	    while(effectItr.hasNext()) {
	       String element = effectItr.next();
	       mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
	       idx++;
	    }
	
	    mResolutionMenu = menu.addSubMenu("Resolution");
	    mResolutionList = CameraView.getResolutionList();
	    mResolutionMenuItems = new MenuItem[mResolutionList.size()];
	
	    ListIterator<Size> resolutionItr = mResolutionList.listIterator();
	    idx = 0;
	    while(resolutionItr.hasNext()) {
	        Size element = resolutionItr.next();
	        mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
	                Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
	        idx++;
	     }
	
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item.getGroupId() == 1){
            CameraView.setEffect((String) item.getTitle());
        	Toast.makeText(this, CameraView.getEffect(), Toast.LENGTH_SHORT).show();
        }else if (item.getGroupId() == 2){
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            CameraView.setResolution(resolution);
            resolution = CameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }else if(item.getGroupId() == 3){
        	ModeValue = (String) item.getTitle();
        }else if(item.getGroupId() == 4){
        	if((String) item.getTitle() == "Disable"){
        		debug = false;
        	}else if((String) item.getTitle() == "Show FPS"){
        		Toast.makeText(this, "Showing FPS...", Toast.LENGTH_SHORT).show();
        		CameraView.enableFpsMeter();
        	}else if((String) item.getTitle() == "Hide FPS"){
        		CameraView.disableFpsMeter();
        	}else{
        		debug = true;
        	}
        }

        return true;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
    	
    	final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
           case MotionEvent.ACTION_DOWN: {
        	   //Toast.makeText(this,"Touch", Toast.LENGTH_SHORT).show();
        	   if(teste1 == 1){
        		   teste1 = 2 ;
        	   }else if (teste1 == 2){
        		   teste1 = 0;
        	   }else{
        		   teste1 = 1;
        	   }
        	   break;
           }
           case MotionEvent.ACTION_MOVE:{
        	   	//teste1 = (int) event.getX();
       			//teste2 = (int) event.getY();
       			//break;
           }
        }
    	Log.i(TAG,"onTouch event");
        
        return true;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == Status)
			Toast.makeText(this,"Click on " + Status.getText(), Toast.LENGTH_SHORT).show();
		else if(v == FullDetection){
			//Toast.makeText(this,"Take a picture and do a Full Detection", Toast.LENGTH_SHORT).show();
			
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        //String currentDateandTime = sdf.format(new Date());
	        //String fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Photo/temp_picture_" + currentDateandTime + ".jpg";
	        //String fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Photo/temp_picture_" + "processing" + ".jpg";
	        
	        //CameraView.takePicture(fileName);
	        //Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
	        
	        doFullDetection();
	        
	        //File file = new File(fileName);
	        //boolean deleted = file.delete();
	        
		}else if(v == CleanList){
			
			
			Toast.makeText(this,"Clean", Toast.LENGTH_SHORT).show();
			objControl = new boolean[30];
			personal = new ArrayList<ObjectList>();
			AdapterTwoLinesImage adapter = new AdapterTwoLinesImage(getApplicationContext(), personal);
		    DetectedObj.setAdapter(adapter);
		    for(int c = 0; c < DetectedChars.length; c++){
		    	
		    	DetectedChars[c] = false;
		    }
		    
		}
	}
	
	private void doFullDetection(){
		
		Thread Full = new Thread(new Runnable(){
			
			@Override
			public void run() {
				Message VISIBLE = new Message();
				VISIBLE.what=100;
				mainHandler.sendMessage(VISIBLE);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		        String currentDateandTime = sdf.format(new Date());
		        //String fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Photo/temp_picture_" + currentDateandTime + ".jpg";
		        String fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Photo/temp_picture_" + "processing" + ".jpg";
		        
		        CameraView.takePicture(fileName);
		        //Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
				
		        File File = new File(fileName);
				
				Mat BaseImage = new Mat();
				
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inPreferredConfig = Config.RGB_565;
				opts.inSampleSize = 2;
				Bitmap bitmap;
				bitmap = BitmapFactory.decodeFile(File.getAbsolutePath(),opts);
				if(bitmap != null){
					Log.d(TAG, File.getAbsolutePath());
					Utils.bitmapToMat(bitmap, BaseImage);
					bitmap.recycle();
					
					//Toast.makeText(this, File.getAbsolutePath(), Toast.LENGTH_SHORT).show();
			        
					SimpsonDetector SD = new SimpsonDetector();
			    	SD.setFrame(BaseImage);
			    	//SD.DrawSquare();
			    	//SD.DrawMatches();
			    	SD.ProcessFrame();
			    	for(int c =0; c<preLoadedImages.size(); c++){
						if (!DetectedChars[0] && ( SD.Process( previousDetector.getDescriptors().get(0), "Homer 1") ||
						   SD.Process( previousDetector.getDescriptors().get(1), "Homer 2")) && SD.clean()){
							DetectedChars[0] = true;
							DetectedChars[1] = true;
							Message m = new Message();
							m.what=1;
							mainHandler.sendMessage(m);
							
						}else if(!DetectedChars[2] && SD.Process( previousDetector.getDescriptors().get(2), "Marge")&& SD.clean()){
							Message m = new Message();
							m.what=2;
							mainHandler.sendMessage(m);
							DetectedChars[2] = true;
						}else if(!DetectedChars[3] && SD.Process( previousDetector.getDescriptors().get(3), "Otto Mann") && SD.clean()){
							Message m = new Message();
							m.what=10;
							mainHandler.sendMessage(m);
							DetectedChars[3] = true;
						}
			    	}
			    	BaseImage.release();
			    	//boolean deleted = File.delete();
				}else{
					bitmap.recycle();
				}
				Message GONE = new Message();
				GONE.what=101;
				mainHandler.sendMessage(GONE);
				
			}
		}, "FullDetection Thread");
		
		Full.start();
		
	}

	@Override
	public void onItemClick(AdapterView<?> av, View arg1, int position, long arg3) {
		//AdapterTwoLinesImage atualAdapter =  (AdapterTwoLinesImage)av.getAdapter();
		//String atualAdress = atualAdapter.personal.get(position).getAdress();
		//String atualName = atualAdapter.personal.get(position).getTitle();
		//CharDialog atual = new CharDialog(this, atualName, atualAdress);
		//atual.show();
		//Toast.makeText(this, atualAdapter.personal.get(position).getAdress() + "", Toast.LENGTH_SHORT).show();
		
	}

}
