package id.co.ale.rainbowDev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.list.ArrayItemList;
import com.ale.rainbowsdk.RainbowSdk;

import id.co.ale.rainbowDev.Helpers.Util;
import id.co.ale.rainbowDev.R;

public class PromotionActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        this.webView = findViewById(R.id.web_view_promo);
        this.webView.setWebViewClient(new PromotionWebViewClient());
        WebSettings webSettings = this.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webView.loadUrl("file:///android_asset/www/list_promo.html");
        webView.addJavascriptInterface(new PromotionWebAppInterface(PromotionActivity.this), "Android");
        //this.webView.setWebContentsDebuggingEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 69 && resultCode == RESULT_OK)
        {
            setResult(RESULT_OK);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
class PromotionWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }
}
class PromotionWebAppInterface {
    Context mContext;

    ArrayItemList<IRainbowContact> friendList;

    PromotionWebAppInterface(Context c) {
        mContext = c;
        friendList = RainbowSdk.instance().contacts().getRainbowContacts();
    }

    @JavascriptInterface
    public void showComment(int sid){
        Util.tempSID = sid;
        Intent commentIntent = new Intent(mContext, CommentActivity.class);
        mContext.startActivity(commentIntent);
    }

    @JavascriptInterface
    public void goToLoginPage()
    {
        Intent loginIntent = new Intent(mContext, LoginActivity.class);
        ((Activity)mContext).startActivityForResult(loginIntent,69);
    }
}
