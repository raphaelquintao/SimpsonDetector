package br.raphael.detector;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CharDialog{

	protected static final String TAG = null;
	
	final Dialog dialog;
	final WebView web;
	private String Adress;
	private String Name;
	
	public CharDialog(Context contexto, String name, String adress){
		Name = name;
		Adress = adress;
		dialog = new Dialog(contexto);
		dialog.setContentView(R.layout.info_chars);
		dialog.setCancelable(true);
		dialog.setTitle(Name);
		web = (WebView) dialog.findViewById(R.id.webView);
		
		web.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url) {
				web.setVisibility(View.VISIBLE);
			}
		});
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl(Adress);//"file:///android_asset/Homer.html"
	}
	public void show(){
		dialog.show();
	}

}
