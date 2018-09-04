package id.co.ale.rainbowDev;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.PluginManager;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.co.ale.rainbowDev.R;

public class TukangDagang extends AppCompatActivity {

    public CordovaWebView webInterface;
    private CordovaInterfaceImpl cordovaInterface = new CordovaInterfaceImpl(this);
    private String TAG = "CordovaTestActivity";
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private boolean permissionIsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tukang_dagang);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
        } else {
            permissionIsGranted = true;
        }

        //Set up the webview
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);

        Bundle extras = getIntent().getExtras();
        String jid = null;
        if (extras != null) {
            jid = extras.getString("jid");;
        }else{
            jid = "123";
        }


//        Log.d("nais1",jid);

        SystemWebView webView = (SystemWebView) findViewById(R.id.tutorialView);
        webInterface = new CordovaWebViewImpl(new SystemWebViewEngine(webView));
        webInterface.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.loadUrl("javascript:initialize(" + jid+ ");");
        webView.loadUrl("file:///android_asset/www/main.html?id="+jid);
        webView.addJavascriptInterface(new WebInterface(this), "Android");
        }catch (Exception err){
            Toast.makeText(this.getBaseContext(),err.toString(),Toast.LENGTH_LONG).show();
        }
    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            switch (keyCode) {
//                case KeyEvent.KEYCODE_BACK:
//                    webInterface.loadUrl("javascript:cordova.fireDocumentEvent('backbutton');");
//                    return true;
//            }
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }


     //This is still required by Cordova
    @Override
    public void onDestroy()
    {
        super.onDestroy();
//        PluginManager pluginManager = webInterface.getPluginManager();
//        if(pluginManager != null)
//        {
//            pluginManager.onDestroy();
//        }

    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode       The request code originally supplied to startActivityForResult(),
     *                          allowing you to identify who this result came from.
     * @param resultCode        The integer result code returned by the child activity through its setResult().
     * @param intent            An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        cordovaInterface.onActivityResult(requestCode, resultCode, intent);
    }

    public void startActivityForResult(CordovaPlugin cordovaPlugin, Intent intent, int i) {

    }


    public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {

    }


    public Activity getActivity() {
        return this;
    }

    public Object onMessage(String s, Object o) {
        return null;
    }


    public ExecutorService getThreadPool() {
        return threadPool;
    }



    private final ExecutorService threadPool = Executors.newCachedThreadPool();



}