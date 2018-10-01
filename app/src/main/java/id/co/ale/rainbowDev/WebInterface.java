package id.co.ale.rainbowDev;

import es.dmoral.toasty.Toasty;

import com.ale.infra.application.RainbowIntent;
import com.ale.infra.contact.Contact;
import com.ale.infra.contact.IContact;
import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.contact.RainbowPresence;
import com.ale.infra.http.GetFileResponse;
import com.ale.infra.http.adapter.concurrent.RainbowServiceException;
import com.ale.infra.list.ArrayItemList;
import com.ale.infra.list.IItemListChangeListener;
import com.ale.infra.manager.Conversation;
import com.ale.infra.manager.IMMessage;
import com.ale.infra.manager.fileserver.IFileProxy;
import com.ale.infra.manager.fileserver.RainbowFileDescriptor;
import com.ale.infra.proxy.conversation.IRainbowConversation;
import com.ale.infra.proxy.room.IRoomProxy;
import com.ale.infra.proxy.users.IUserProxy;
import com.ale.infra.xmpp.xep.IMamNotification;
import com.ale.listener.IRainbowContactsSearchListener;
import com.ale.listener.IRainbowGetConversationListener;
import com.ale.listener.SigninResponseListener;
import com.ale.listener.SignoutResponseListener;
import com.ale.listener.StartResponseListener;
import com.ale.rainbowsdk.Connection;
import com.ale.rainbowsdk.RainbowSdk;
import com.ale.service.RainbowService;
import com.amulyakhare.textdrawable.TextDrawable;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.ale.infra.contact.Contact;
import com.ale.rainbowsdk.RainbowSdk;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.ale.rainbowDev.Adapter.InstantMessagesAdapter;
import id.co.ale.rainbowDev.Helpers.Util;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.ale.infra.capabilities.CapabilitiesMgr.LOG_TAG;

/**
 * Created by Jib Ridwan on 2/24/2017.
 */

public class WebInterface extends AppCompatActivity {
    Context mContext;
    public Contact contact;
    String cid;

    private String pUsername;
    private String pPassword;
    private String rUsername;
    private String rPassword;
    private String savedMail;
    public StartResponseListener a;
    private String[] listsavedMail;
    public static final String mypreference = "mypref";
    Conversation conversation;
    String jid = "eaa0dd9146164d23a2f739295c7ed7f1@openrainbow.com";

    WebInterface(Context c) {
        mContext = c;
        Connection connection = RainbowSdk.instance().connection();

//        String[] perms = {
//                "android.permission.CAMERA",
//                "android.permission.WRITE_EXTERNAL_STORAGE",
//                "android.permission.RECORD_AUDIO"
//        };
//        int permsRequestCode = 200;
//        ActivityCompat.requestPermissions(mContext.startActivity;, perms, permsRequestCode);

        if (connection.isConnected()) {
            Log.d("tomos", "Connected");
        } else {
            Log.d("tomos", "Not Connected");

            connection.start(this.startResponseListener);
        }


    }


//    private StartResponseListener startResponseListener = new StartResponseListener() {
//        @Override
//        public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
//
//        }
//
//        @Override
//        public void onStartSucceeded() {
////            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
////            String username = sp.getString(Util.USERNAME_CODE,"");
////            String password = sp.getString(Util.PASSWORD_CODE,"");
////            Log.d("tomo","mantap :"+username);
////            Log.d("tomo","mantap"+password);
//
//
//            SharedPreferences sp = mContext.getSharedPreferences("userdetails", MODE_PRIVATE);
//            Util.AUTO_TRANSLATE = sp.getString("AUTO_TRANSLATE", "off");
//            String username = sp.getString(Util.USERNAME_CODE, "");
//            String password = sp.getString(Util.PASSWORD_CODE, "");
//            if (username.length() > 0 && password.length() > 0) {
//                RainbowSdk.instance().connection().signin(username, password, "openrainbow.com", signinResponseListener
//                );
//            } else {
////                showLogin();
//            }
//        }
//    };
//


    private void signinWithToken(String email, String password) {
//        Log.d("tomo", email);
//        Log.d("tomo", password);
        // RainbowSdk.instance().connection().signinWithToken(token, "https://openrainbow.com",new SigninResponseListener() {
        RainbowSdk.instance().connection().signin(email, password, Util.RAINBOW_HOST, new SigninResponseListener() {

            @Override
            public void onSigninSucceeded() {
//                Log.d("tomo", "sukses");
//                Log.d("tomo", password);
                try {

                    SharedPreferences userDetails = mContext.getSharedPreferences("userdetails", MODE_PRIVATE);
                    SharedPreferences.Editor edit = userDetails.edit();
                    edit.putString(Util.USERNAME_CODE, email);
                    edit.putString(Util.PASSWORD_CODE, password);
                    edit.apply();
//                    Log.d("tomo", "masuk pa eko");

                } catch (Exception err) {
//                    Log.d("tomo", "error pa eko " + err.toString());


                }



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MessengerService.getInstance().startService();


//                        if( ! ArrayUtils.contains(listsavedMail, email)){
//                            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sp.edit();
//
//                            if(savedMail.length() == 0){
//                                editor.putString("saved_mail", email);
//                            }else{
//                                editor.putString("saved_mail", savedMail+","+email);
//                            }
//                            editor.commit();
//                        }

//                        NotificationCompat.Builder notificationBuilder = RainbowSdk.instance().getNotificationBuilder();
//                        notificationBuilder.setContentText(getResources().getString(R.string.notif_connected));
//                        PendingIntent contentIntent = PendingIntent.getActivity(RainbowSdk.instance().getContext(), 0, RainbowIntent.getLauncherIntent(getApplicationContext(), TukangDagang.class), 0);
//                        notificationBuilder.setContentIntent(contentIntent);
//
//                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        mNotificationManager.notify(RainbowService.EVENT_NOTIFICATION, notificationBuilder.build());
//                    mNotificationManager.cancel(RainbowService.EVENT_NOTIFICATION);


                    }
                });


//
//            setResult(RESULT_OK);


            }

            @Override
            public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
                Util.log(errorCode.toString() + " " + s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("tomo", s);
                        //   Toasty.error(getBaseContext(), "Login fail").show();
//                        showLoginForm();
                    }
                });
            }
//            @Override
//            public void onSigninSucceeded() {
//                if (!alreadyConnected) {
//
////                    Log.d("tomo", "onSigninSucceeded: ");
//                    // Load normally on the thread UI
//                } // else you may not need to do anything because you are already connected
//            }
//
//            @Override
//            public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String err) {
//                Log.d("tomo", err);
//
////                Log.getLogger().info(LOG_TAG, "onRequestFailed: signin failed " + err);
//                if (errorCode == RainbowSdk.ErrorCode.TOKEN_MAXIMUM_RENEW_REACHED) {
//                    String newToken = "The new token"; // Generate a new token
//                    signinWithToken(newToken, true);
//                } // else..
//            }
        });
    }

    SignoutResponseListener signoutResponseListener = new SignoutResponseListener() {
        @Override
        public void onSignoutSucceeded() {


            try {


                MessengerService.getInstance().stopService();

                SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                Log.d("trysetyo", Util.USERNAME_CODE);
                editor.remove(Util.USERNAME_CODE);
                editor.remove(Util.PASSWORD_CODE);
                editor.commit();
            } catch (Exception err) {
                Log.d("trysetyo", err.toString());
                Log.d("trysetyo", Util.USERNAME_CODE);
                Log.d("trysetyo", Util.PASSWORD_CODE);
            }


//            NotificationCompat.Builder notificationBuilder = RainbowSdk.instance().getNotificationBuilder();
//            notificationBuilder.setContentText(getResources().getString(R.string.notif_connecting));
//            PendingIntent contentIntent = PendingIntent.getActivity(RainbowSdk.instance().getContext(), 0, RainbowIntent.getLauncherIntent(getApplicationContext(), MainActivity.class), 0);
//            notificationBuilder.setContentIntent(contentIntent);
//
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(RainbowService.EVENT_NOTIFICATION, notificationBuilder.build());

            // Intent intent = new Intent(getBaseContext(), TukangDagang.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // startActivity(intent);
            // finish();
        }

//        @Override
//        public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Util.toast("Terjadi kesalahan saat logout",getBaseContext());
//                }
//            });
//        }
    };


    @JavascriptInterface
    public void signInByTOken(String email, String password) {
//        Log.d("tomo", token);
        RainbowSdk.instance().connection().start(new StartResponseListener() {
            @Override
            public void onStartSucceeded() {
//                String yourFirstToken = token;
                signinWithToken(email, password);
            }

            @Override
            public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String err) {
                // Something was wrong
                Log.d("tomo", err.toString());
            }
        });


    }

    @JavascriptInterface
    public void showLogin() {
        Intent i = new Intent(mContext, MainActivity.class);
        mContext.startActivity(i);
    }


    boolean isUserClickButton = false;

    @JavascriptInterface
    public void exitAp() {
        if (isUserClickButton == false) {
            Toast.makeText(mContext, "Jika menekan sekali lagi, app akan ditutup", Toast.LENGTH_LONG).show();
            isUserClickButton = true;
        } else {

            System.exit(1);
            finish();
        }
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isUserClickButton = false;
            }
        }.start();

    }

    @JavascriptInterface
    public void logoutRainbow() {
        Log.d("trysetyo", "logoutRainbow: ");
        RainbowSdk.instance().connection().signout(signoutResponseListener);


    }

    private StartResponseListener startResponseListener = new StartResponseListener() {
        @Override
        public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
            Log.d("tomos", s.toString());
        }

        @Override
        public void onStartSucceeded() {
            Log.d("tomos","onStartSucceeded");

            SharedPreferences sp = mContext.getSharedPreferences("userdetails", MODE_PRIVATE);
            Util.AUTO_TRANSLATE = sp.getString("AUTO_TRANSLATE", "off");
            String username = sp.getString(Util.USERNAME_CODE, "");
            String password = sp.getString(Util.PASSWORD_CODE, "");
            if (username.length() > 0 && password.length() > 0) {
                RainbowSdk.instance().connection().signin(
                        username, password, "openrainbow.com", signinResponseListener
                );
                Log.d("tomos", "lewat sini");
//                MessengerService.getInstance().startService();

            } else {
                Log.d("tomos", "lewat sini 2");
//                showLogin();
            }
        }
    };

        public SigninResponseListener signinResponseListener = new SigninResponseListener() {
            @Override
            public void onSigninSucceeded() {
                Log.d("tomos", "onSigninSucceeded");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                    NotificationCompat.Builder notificationBuilder = RainbowSdk.instance().getNotificationBuilder();
//                    notificationBuilder.setContentText(getResources().getString(R.string.notif_connected));
//                    PendingIntent contentIntent = PendingIntent.getActivity(RainbowSdk.instance().getContext(), 0, RainbowIntent.getLauncherIntent(getApplicationContext(), TukangDagang.class), 0);
//                    notificationBuilder.setContentIntent(contentIntent);
//
//                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    mNotificationManager.notify(RainbowService.EVENT_NOTIFICATION, notificationBuilder.build());
//                    mNotificationManager.cancel(RainbowService.EVENT_NOTIFICATION);

                        MessengerService.getInstance().startService();

                        Log.d("tomos", "message starting");
//                        showIM();
//                        Log.d("tomos", "succes show IM");
                    }

                });


            }


            @Override
            public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
                Log.d("tomos", "failed show IM");
                //showFrontPage();
            }
        };

    private void showIM(){
        Intent intent = new Intent(mContext, ImsgActivity.class);
        mContext.startActivity(intent);
        finish();
    }
        @JavascriptInterface
        public void showChat(String jid2) {
            try {
                String msg = "123";
                RainbowSdk.instance().contacts().searchByJid(jid2, new IRainbowContactsSearchListener() {
                    @Override
                    public void searchStarted() {

                    }

                    @Override
                    public void searchFinished(List<IContact> list) {
                        if (list.size() > 0) {
                            try {
                                RainbowSdk.instance().contacts().getUserDataFromId(list.get(0).getCorporateId(), new IUserProxy.IGetUserDataListener() {
                                    @Override
                                    public void onSuccess(final Contact contact) {
//                                        Util.tempContact = contact;
//                                        Intent intent = new Intent(mContext, ChatActivity.class);
//                                         mContext.startActivity(intent);
//                                        finish();

                                        Util.tempContact = contact;

                                        Intent intentCall = new Intent(mContext, ChatActivity.class);
                                        mContext.startActivity(intentCall);

                                    }

                                    @Override
                                    public void onFailure(RainbowServiceException e) {
                                    }
                                });
                            } catch (Exception e) {
                                Log.d("tomos", e.toString());
                            }
                        }
                    }

                    @Override
                    public void searchError(RainbowServiceException e) {
                    }
                });


            } catch (Exception err) {
                Log.d("tomo", err.toString());
            }
        }

        @JavascriptInterface
        public void showTampil(String jid2) {
            String msg = "123";
            RainbowSdk.instance().contacts().searchByJid(jid2, new IRainbowContactsSearchListener() {
                @Override
                public void searchStarted() {
                }

                @Override
                public void searchFinished(List<IContact> list) {
                    if (list.size() > 0) {
                        try {
                            RainbowSdk.instance().contacts().getUserDataFromId(list.get(0).getCorporateId(), new IUserProxy.IGetUserDataListener() {
                                @Override
                                public void onSuccess(final Contact contact) {
//                                IRainbowContact x = RainbowSdk.instance().contacts().getContactFromJabberId(jid2);
                                    RainbowSdk.instance().webRTC().makeCall((Contact) contact, true);
                                    Intent intentCall = new Intent(mContext, VoiceCallActivity.class);
                                    mContext.startActivity(intentCall);

                                }

                                @Override
                                public void onFailure(RainbowServiceException e) {
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void searchError(RainbowServiceException e) {
                }
            });
        }




    }




