package org.easydarwin.easyplayer;

import android.app.Activity;  
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
  
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.videoengine.*;

public class EasyPlayer extends Activity {
	    
    private SurfaceView sSurfaceView = null;   
	
	private long sufaceHandle = 0;
	
	private static final int PORTRAIT = 1;		//竖屏
	private static final int LANDSCAPE = 2;		//横屏
	private static final String TAG = "EasyPlayer";
	
	private int currentOrigentation = PORTRAIT;
	
	private boolean isPlaybackViewStarted = false;
	
	private String playbackUrl = null;
	
	Button btnPopInputText;
    Button btnStartStopPlayback;
    TextView txtCopyright;
    
    LinearLayout lLayout = null;
    FrameLayout fFrameLayout = null;
    
    private Context myContext; 
    
	static {  
		System.loadLibrary("EasyPlayer");
	}
  
    @SuppressWarnings("deprecation")
	@Override protected void onCreate(Bundle icicle) {  
        super.onCreate(icicle);  
        
      Log.i(TAG, "Run into OnCreate++");
      
      int screenWidth = getWindowManager().getDefaultDisplay().getWidth();  
      int screenHeight = getWindowManager().getDefaultDisplay().getHeight();  
      
      Log.i(TAG, "screenWidth: " + screenWidth + ", screenHeight: " + screenHeight);
      
      myContext = this.getApplicationContext();
	    
	  int iInitRet = EasyPlayerJni.EasyPlayerInit(myContext, screenWidth, screenHeight); 
	    
	  if(iInitRet != 0)
	   	return;
	    
	  boolean bViewCreated = CreateView();
	    
	   if(bViewCreated){
		   inflateLayout(LinearLayout.VERTICAL);
	   }
    }
    
    /* For EasyPlayer demo app, the url is based on: baseURL + inputID
     * For example: 
     * baseURL: rtmp://rtmp.easydarwin.org:1935/live/stream
     * inputID: 123456
     * playbackUrl: rtmp://rtmp.easydarwin.org:1935/live/stream123456
     * */
    private void GenerateURL(String id){
    	if(id == null)
    		return;
    	
    	btnStartStopPlayback.setEnabled(true);
    	String baseURL = "rtmp://rtmp.easydarwin.org:1935/live/stream";
    	playbackUrl = baseURL + id;
    }
    
    /* Popup InputID dialog */
    private void PopDialog(){
    	final EditText inputID = new EditText(this);
    	inputID.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("如 rtmp://rtmp.easydarwin.org:1935/live/stream123456,请输入123456").setView(inputID).setNegativeButton(
                "取消", null);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String strID = inputID.getText().toString();
                    	GenerateURL(strID);
                    }
                });
        builder.show();
    }
    
    /* Generate basic layout */
    private void inflateLayout(int orientation) {
    	if (null == lLayout)
            lLayout = new LinearLayout(this);

	    addContentView(lLayout,  new android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,  
	      android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    	
        lLayout.setOrientation(orientation);
   
        fFrameLayout = new FrameLayout(this);
        
        LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, 1.0f);
        fFrameLayout.setLayoutParams(lp);
        Log.i(TAG, "++inflateLayout..");
               
        sSurfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));       
        
        fFrameLayout.addView(sSurfaceView, 0);

        RelativeLayout outLinearLayout = new RelativeLayout(this);
        outLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        
        LinearLayout lLinearLayout = new LinearLayout(this);
        lLinearLayout.setOrientation(LinearLayout.VERTICAL);
        lLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
 
                
        LinearLayout copyRightLinearLayout = new LinearLayout(this);
        copyRightLinearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rl.topMargin = getWindowManager().getDefaultDisplay().getHeight()-150;
        copyRightLinearLayout.setLayoutParams(rl);
 
        txtCopyright=new TextView(this);
        txtCopyright.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        txtCopyright.setText("Copyright 2013~2016 www.EasyDarwin.org v1.0.16.0303");
        copyRightLinearLayout.addView(txtCopyright, 0);
        
        
        /* PopInput button */
        btnPopInputText = new Button(this);
        btnPopInputText.setText("输入urlID");
        btnPopInputText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lLinearLayout.addView(btnPopInputText, 0);
        
        /* Start playback stream button */
        btnStartStopPlayback = new Button(this);
        btnStartStopPlayback.setText("开始播放 ");
        btnStartStopPlayback.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lLinearLayout.addView(btnStartStopPlayback, 1);
 
        outLinearLayout.addView(lLinearLayout, 0);
        outLinearLayout.addView(copyRightLinearLayout, 1);
        fFrameLayout.addView(outLinearLayout, 1);

        lLayout.addView(fFrameLayout, 0);
  
        if(isPlaybackViewStarted)
        {
        	btnPopInputText.setEnabled(false);
        	btnStartStopPlayback.setText("停止播放 ");
        }
        else
        {
        	btnPopInputText.setEnabled(true);
        	btnStartStopPlayback.setText("开始播放 ");
        }
        
        /* PopInput button listener */
        btnPopInputText.setOnClickListener(new Button.OnClickListener() {  
        	  
            //  @Override  
              public void onClick(View v) {  
            	  Log.i(TAG, "Run into input playback ID++");
	              
            	  PopDialog();
            	  
            	  Log.i(TAG, "Run out from input playback ID--");
              	}
              });  

        btnStartStopPlayback.setOnClickListener(new Button.OnClickListener() {  
        	  
            //  @Override  
              public void onClick(View v) {  
	              
            	  if(isPlaybackViewStarted)
            	  {
                	  Log.i(TAG, "Stop playback stream++");
            		  btnStartStopPlayback.setText("开始播放 ");
            		  btnPopInputText.setEnabled(true);
            		  EasyPlayerJni.EasyPlayerClose(sufaceHandle);	
                      sufaceHandle = 0;
            		  isPlaybackViewStarted = false;
                      Log.i(TAG, "Stop playback stream--");
            	  }
            	  else
            	  {
            		  Log.i(TAG, "Start playback stream++");
 	              	  sufaceHandle =  EasyPlayerJni.EasyPlayerSetSurface(sSurfaceView); 
 	            	
 	              	  if(playbackUrl == null){
 	              		 Log.e(TAG, "playback URL with NULL..."); 
 	              		 return;
 	              	  }
 	              	  
 	              	  int iPlaybackRet = EasyPlayerJni.EasyPlayerStartPlayback(sufaceHandle, playbackUrl);
 	                  if(iPlaybackRet != 0)
 	                  {
 	                	 Log.e(TAG, "StartPlayback strem failed.."); 
 	                	 return;
 	                  }
   	
            		  btnStartStopPlayback.setText("停止播放 ");
 	                  btnPopInputText.setEnabled(false);
 	              	  isPlaybackViewStarted = true;
 	              	  Log.i(TAG, "Start playback stream--");
            	  }
              	}
              });
	}
	
    /* Create rendering */
    private boolean CreateView() {
    	
        if(sSurfaceView == null)
        {
        	sSurfaceView = NTRenderer.CreateRenderer(this, true);
        }
        
        if(sSurfaceView == null)
        {
        	Log.i(TAG, "Create render failed..");
        	return false;
        }

        return true;
	}
    
	@Override  
    public void onConfigurationChanged(Configuration newConfig) {  
            super.onConfigurationChanged(newConfig);  
            
            Log.i(TAG, "Run into onConfigurationChanged++");
            
            if (null != fFrameLayout)
            {
            	fFrameLayout.removeAllViews();
            	fFrameLayout = null;
            }
            
            if (null != lLayout)
            {
                lLayout.removeAllViews();
                lLayout = null;
            }
            
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) 
            {   
            	Log.i(TAG, "onConfigurationChanged, with LANDSCAPE。。");

            	inflateLayout(LinearLayout.HORIZONTAL);
                 
            	currentOrigentation = LANDSCAPE;
            } 
            else
            {  
            	Log.i(TAG, "onConfigurationChanged, with PORTRAIT。。"); 
            	
            	inflateLayout(LinearLayout.VERTICAL);
            	
            	currentOrigentation = PORTRAIT;
            }  
            
            if(!isPlaybackViewStarted)
            	return;
            
            EasyPlayerJni.EasyPlayerSetOrientation(sufaceHandle, currentOrigentation);

            Log.i(TAG, "Run out of onConfigurationChanged--");
    }
    
	@Override
    protected  void onDestroy()
	{
		Log.i(TAG, "Run into activity destory++");   	
    	
		if(sufaceHandle!=0)
		{
			EasyPlayerJni.EasyPlayerClose(sufaceHandle);	
			sufaceHandle = 0;
		}
		super.onDestroy();
    	finish();
    	System.exit(0);
    }
}