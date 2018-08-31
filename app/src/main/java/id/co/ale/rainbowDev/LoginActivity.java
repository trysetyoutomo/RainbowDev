package id.co.ale.rainbowDev;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ale.infra.application.RainbowIntent;
import com.ale.listener.SigninResponseListener;
import com.ale.rainbowsdk.RainbowSdk;
import com.ale.service.RainbowService;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import id.co.ale.rainbowDev.Helpers.Util;

public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView iEmail;
    private EditText iPassword;
    private Button btnLogin;
    private ProgressBar loading;
    private LinearLayout container;
    private ImageView showPassword;
    private boolean isPasswordshow = false;

    private String pUsername;
    private String pPassword;
    private String rUsername;
    private String rPassword;
    private String savedMail;
    private String []listsavedMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        this.container = (LinearLayout) findViewById(R.id.loginContainer);

        this.iEmail = (AutoCompleteTextView) findViewById(R.id.inputEmail);
        this.iEmail.setThreshold(1);

        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        this.savedMail = sp.getString("saved_mail", "");
        this.listsavedMail = savedMail.split(",");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, listsavedMail);
        this.iEmail.setAdapter(adapter);

        this.iPassword = (EditText) findViewById(R.id.inputPassword);
        this.btnLogin = (Button) findViewById(R.id.buttonLogin);
        this.loading = (ProgressBar) findViewById(R.id.loginProgress);
        this.showPassword = (ImageView) findViewById(R.id.btn_show_password);

        this.showPassword.setOnClickListener(togglePassword);
        this.btnLogin.setOnClickListener(loginProcess);

        TextView versionName = (TextView) findViewById(R.id.version_name);
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
            versionName.setText(packageInfo.versionName);
        }catch (Exception e){}

        this.btnLogin.callOnClick();
     }

    @Override
    public void onResume(){
        super.onResume();
        this.iPassword.setText("");
    }

    private void showLoginForm(){
        loading.setVisibility(View.INVISIBLE);
        container.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener loginProcess = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            pUsername = iEmail.getText().toString();
            pPassword = iPassword.getText().toString();

            if(pUsername.length() == 0 || pPassword.length() == 0){
                return;
            }

            container.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);

            RainbowSdk.instance().connection().signin(pUsername, pPassword, Util.RAINBOW_HOST, signinResponseListener);
        }
    };



    private View.OnClickListener togglePassword = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {
            isPasswordshow = !isPasswordshow;
            iPassword.setTransformationMethod(!isPasswordshow ? new PasswordTransformationMethod() : null);
            showPassword.setImageDrawable(isPasswordshow ? getDrawable(R.drawable.eye) : getDrawable(R.drawable.eye_slash));
        }
    };

    private SigninResponseListener signinResponseListener = new SigninResponseListener() {
        @Override
        public void onSigninSucceeded() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if( ! ArrayUtils.contains(listsavedMail, iEmail.getText().toString())){
                        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        if(savedMail.length() == 0){
                            editor.putString("saved_mail", iEmail.getText().toString());
                        }else{
                            editor.putString("saved_mail", savedMail+","+iEmail.getText().toString());
                        }
                        editor.commit();
                    }

//                    NotificationCompat.Builder notificationBuilder = RainbowSdk.instance().getNotificationBuilder();
//                    notificationBuilder.setContentText(getResources().getString(R.string.notif_connected));
//                    PendingIntent contentIntent = PendingIntent.getActivity(RainbowSdk.instance().getContext(), 0, RainbowIntent.getLauncherIntent(getApplicationContext(), ImsgActivity.class), 0);
//                    notificationBuilder.setContentIntent(contentIntent);
//
//                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    mNotificationManager.notify(RainbowService.EVENT_NOTIFICATION, notificationBuilder.build());
////                    mNotificationManager.cancel(RainbowService.EVENT_NOTIFICATION);
//
//                    MessengerService.getInstance().startService();
                }
            });

            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Util.USERNAME_CODE, rUsername);
            editor.putString(Util.PASSWORD_CODE, rPassword);
            editor.commit();
//
//            setResult(RESULT_OK);


            try{
                String idd = RainbowSdk.instance().myProfile().getConnectedUser().getImJabberId();
                String mail = iEmail.getText().toString();

                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://gis.35utech.com/index.php?r=site/jid&jabber_id="+idd+"&email="+mail,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                      //          response = StringEscapeUtils.unescapeJava(response);
                        //        if(response.startsWith("\"") && response.endsWith("\"") && response.length() >=3){
                          //          response = response.substring(1, response.length()-1);
                            //    }

//                            if(response.length() > 140){
//                                response = response.substring(0, 140)+"...";
//                            }
                               final String lastStatus = response;
                                Intent intent = new Intent(RainbowSdk.instance().getContext(), TukangDagang .class);
                                intent.putExtra("jid",idd);
                                Log.d("nais1", idd);
                                startActivity(intent);
                                finish();

                                //  Log.d("nais1","qkqkqkkqkq"+ lastStatus);
                                //Last Status Off
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    last_status.setText(lastStatus);
//                                    Util.lastStatus = lastStatus;
//                                }
//                            });
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(final VolleyError error) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Util.log(error.toString());
                                    }
                                });
                            }
                        }
                ){
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("jid", RainbowSdk.instance().myProfile().getConnectedUser().getCorporateId());
                        return params;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(postRequest);
            }catch (Exception e){
                Util.log(e.toString());
            }


        }

        @Override
        public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
            Util.log(errorCode.toString()+" "+s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toasty.error(getBaseContext(), "Login fail").show();
                    showLoginForm();
                }
            });
        }
    };
}