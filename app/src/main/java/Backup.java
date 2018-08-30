package id.co.ale.rainbowDev;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaActivity;
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

public class Backup extends CordovaActivity {

    public CordovaWebView webInterface;
    private CordovaInterfaceImpl cordovaInterface = new CordovaInterfaceImpl(this);
    private String TAG = "CordovaTestActivity";
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private boolean permissionIsGranted = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);

    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tukang_dagang);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
//        } else {
//            permissionIsGranted = true;
//        }
//
//        //Set up the webview
//        ConfigXmlParser parser = new ConfigXmlParser();
//        parser.parse(this);
//
//        Bundle extras = getIntent().getExtras();
//        String jid = null;
//        if (extras != null) {
//            jid = extras.getString("jid");;
//        }else{
//            jid = "123";
//        }
//
//
//        Log.d("nais1",jid);
//
//        SystemWebView webView = (SystemWebView) findViewById(R.id.tutorialView);
//        webInterface = new CordovaWebViewImpl(new SystemWebViewEngine(webView));
//        webInterface.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
//        webView.getSettings().setGeolocationEnabled(true);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setAllowContentAccess(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.addJavascriptInterface(new WebInterface(this), "Android");
//
//
////        webView.loadUrl("javascript:alert('123')");
//        webView.loadUrl("file:///android_asset/www/main.html");
//
//    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    webInterface.loadUrl("javascript:cordova.fireDocumentEvent('backbutton');");
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    // This is still required by Cordova
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        PluginManager pluginManager = webInterface.getPluginManager();
        if(pluginManager != null)
        {
            pluginManager.onDestroy();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        cordovaInterface.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Called by the system when the user grants permissions!
     *
     * Note: The fragment gets priority over the activity, since the activity doesn't call
     * into the parent onRequestPermissionResult, which is why there's no override.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
//    public void onRequestPermissionsResult(int requestCode, String permissions[],
//                                           int[] grantResults) {
//        try
//        {
//            cordovaInterface.onRequestPermissionResult(requestCode, permissions, grantResults);
//        }
//        catch (JSONException e)
//        {
//            Log.d(TAG, "JSONException: Parameters fed into the method are not valid");
//            e.printStackTrace();
//        }
//
//    }

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