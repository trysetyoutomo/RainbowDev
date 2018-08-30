package id.co.ale.rainbowDev;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ale.infra.contact.IContact;
import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.http.adapter.concurrent.RainbowServiceException;
import com.ale.listener.IRainbowContactsSearchListener;
import com.ale.rainbowsdk.RainbowSdk;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import id.co.ale.rainbowDev.Helpers.Util;

import id.co.ale.rainbowDev.R;

public class CommentActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        this.webView = (WebView) findViewById(R.id.web_view);
        this.webView.setWebViewClient(new CommentsWebViewClient());

        WebSettings webSettings = this.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        this.webView.setWebContentsDebuggingEnabled(true);
        this.webView.loadUrl("file:///android_asset/www/comments.html");
        this.webView.addJavascriptInterface(new WebAppInterface(this), "Android");
    }

    class CommentsWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public String getJID(){
            return RainbowSdk.instance().myProfile().getConnectedUser().getImJabberId();
        }

        @JavascriptInterface
        public int getSID(){
            return Util.tempSID;
        }

        @JavascriptInterface
        public String getContact(String jid){
            IRainbowContact contact = RainbowSdk.instance().contacts().getContactFromJabberId(jid);
            if(contact != null){
                ArrayList<String> s_contact = new ArrayList<String>();
                s_contact.add(contact.getImJabberId());
                s_contact.add(contact.getFirstName()+" "+contact.getLastName());
                s_contact.add(RainbowSdk.instance().contacts().getAvatarUrl(contact.getCorporateId()));

                JSONArray jsonArray = new JSONArray(s_contact);

                return jsonArray.toString();
            }else return null;
        }

        @JavascriptInterface
        public void requestContact(String jid){
            RainbowSdk.instance().contacts().searchByJid(jid, new IRainbowContactsSearchListener() {
                @Override
                public void searchStarted() {}

                @Override
                public void searchFinished(List<IContact> list) {
                    IContact contact = list.get(0);

                    final ArrayList<String> s_contact = new ArrayList<String>();
                    s_contact.add(contact.getImJabberId());
                    s_contact.add(contact.getFirstName()+" "+contact.getLastName());
                    s_contact.add(RainbowSdk.instance().contacts().getAvatarUrl(contact.getCorporateId()));

                    final JSONArray jsonArray = new JSONArray(s_contact);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Util.log("set: "+"javascript:set_contact(\""+jsonArray.toString()+"\")");
                            webView.loadUrl("javascript:set_contact('"+jsonArray.toString()+"')");
                        }
                    });
                }

                @Override
                public void searchError(RainbowServiceException e) {}
            });
        }

        @JavascriptInterface
        public String getShortDate(String sdate){
            try{
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdate = Util.txtDate(f.parse(sdate)).toString();
            }catch (Exception e){
                Util.log(e.toString());
            }
            return sdate;
        }
    }
}