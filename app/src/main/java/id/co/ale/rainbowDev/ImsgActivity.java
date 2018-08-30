package id.co.ale.rainbowDev;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ale.infra.application.RainbowIntent;
import com.ale.infra.contact.Contact;
import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.contact.RainbowPresence;
import com.ale.listener.SignoutResponseListener;
import com.ale.rainbowsdk.RainbowSdk;
import com.ale.service.RainbowService;
import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.co.ale.rainbowDev.Helpers.Util;
import id.co.ale.rainbowDev.IMFragment.ContactSearchFragment;
import id.co.ale.rainbowDev.IMFragment.ContactsFragment;
import id.co.ale.rainbowDev.IMFragment.ConversationsFragment;
import id.co.ale.rainbowDev.IMFragment.RoomsFragment;


public class ImsgActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private View headerView;
    private ImageView profile_photo;
    private TextView profile_name;
    private TextView profile_email;
//    private TextView last_status;
    private ImageView msgHeader;
    private ImageView btnSearch;

    private int currentFragmentPosition = -1;
    private int changeFragmentPosition = 0;

    private EditText inputContactSearch;
    ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();

    private ContactSearchFragment contactSearchFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_imsg);
     //   Log.d("nais", "tes");
        String idd = RainbowSdk.instance().myProfile().getConnectedUser().getImJabberId();
        Log.d("nais1",idd);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.msgHeader = (ImageView) findViewById(R.id.msg_header);
        this.inputContactSearch = (EditText) toolbar.findViewById(R.id.input_search);
        this.inputContactSearch.addTextChangedListener(inputContactSearchListener);
        this.btnSearch = (ImageView) findViewById(R.id.btn_search);

        this.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputContactSearch.isFocused() && inputContactSearch.getText().length() == 0){
                    onBackPressed();
                }else{
                    msgHeader.setVisibility(View.GONE);
                    inputContactSearch.setVisibility(View.VISIBLE);
                    inputContactSearch.requestFocus();
                    inputContactSearch.callOnClick();
                }
            }
        });
        this.msgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSearch.callOnClick();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_copyright, R.string.app_name);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateLastStatus();
                    }
                });
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        Util.bottomNavdisableShiftMode(this.bottomNavigationView);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(onBottomNavigationItemSelected);

        this.headerView = navigationView.getHeaderView(0);
        this.profile_photo = (ImageView) headerView.findViewById(R.id.profile_photo);
        this.profile_name = (TextView) headerView.findViewById(R.id.profil_name);
        this.profile_email = (TextView) headerView.findViewById(R.id.profile_email);


        //fragment init
        this.fragmentsList.add(new ConversationsFragment());
        this.fragmentsList.add(new RoomsFragment());
        this.fragmentsList.add(new ContactsFragment());
        this.fragmentsList.add(new ContactSearchFragment());

        this.switchFragment();

        this.updateMyProfile();
        try{
            RainbowSdk.instance().myProfile().getConnectedUser().registerChangeListener(myProfileListener);
        }catch (Exception e){}

        this.profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfil();
            }
        });

//        Intent intent = new Intent(getBaseContext(), InvitationActivity.class);
//        startActivity(intent);
    }

    private void openProfil(){
        Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(intent);
    }

    public void updateMyProfile(){
        try{
            IRainbowContact myProfile = RainbowSdk.instance().myProfile().getConnectedUser();

            this.profile_name.setText(myProfile.getFirstName()+" "+myProfile.getLastName());
            this.profile_email.setText(myProfile.getLoginEmail().toString().replace(Util.FAKE_HOST,""));

            Bitmap bmp = myProfile.getPhoto();
            if(bmp != null){
                this.profile_photo.setImageBitmap(Util.getRoundedCornerBitmap(bmp, bmp.getWidth()));
            }else{
                String initName = "?";
                try{
                    initName = myProfile.getFirstName().substring(0,1)+myProfile.getLastName().substring(0,1);
                }catch (Exception e){}
                this.profile_photo.setImageDrawable(TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.parseColor("#F26722"))
                        .endConfig()
                        .buildRound(initName, Color.WHITE));
            }
            updateLastStatus();
        }catch (Exception e){}
    }

    private void updateLastStatus(){
        //last_status.setText(Util.lastStatus);
        try{
            StringRequest postRequest = new StringRequest(Request.Method.POST, "http://openrainbow-indonesia.org/api/get_status/",
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            response = StringEscapeUtils.unescapeJava(response);
                            if(response.startsWith("\"") && response.endsWith("\"") && response.length() >=3){
                                response = response.substring(1, response.length()-1);
                            }

//                            if(response.length() > 140){
//                                response = response.substring(0, 140)+"...";
//                            }
                            final String lastStatus = response;
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
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(postRequest);
        }catch (Exception e){
            Util.log(e.toString());
        }
    }

    private Contact.ContactListener myProfileListener = new Contact.ContactListener() {
        @Override
        public void contactUpdated(Contact contact) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateMyProfile();
                }
            });
        }

        @Override
        public void onPresenceChanged(Contact contact, RainbowPresence rainbowPresence) { }
        @Override
        public void onActionInProgress(boolean b) { }
    };

    @Override
    public void onResume(){ super.onResume(); }

    @Override
    public void onDestroy(){
        try{
            RainbowSdk.instance().myProfile().getConnectedUser().unregisterChangeListener(myProfileListener);
        }catch (Exception e){}

        super.onDestroy();
    }

    @Override
    public void onPause(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(contactSearchFragment != null){
            hideSearchFragment();
        }else if(inputContactSearch.isFocused()){
            inputContactSearch.clearFocus();
            inputContactSearch.setVisibility(View.INVISIBLE);
            msgHeader.setVisibility(View.VISIBLE);
        }else if(currentFragmentPosition > 0){
            BottomNavigationView bottomNavigationView;
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
            bottomNavigationView.setSelectedItemId(R.id.navigation_chat);
        }else{
            super.onBackPressed();
        }
    }

    SignoutResponseListener signoutResponseListener = new SignoutResponseListener() {
        @Override
        public void onSignoutSucceeded() {
            MessengerService.getInstance().stopService();

            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            Log.d("nais",Util.USERNAME_CODE);
            editor.remove(Util.USERNAME_CODE);
            editor.remove(Util.PASSWORD_CODE);
            editor.commit();

            NotificationCompat.Builder notificationBuilder = RainbowSdk.instance().getNotificationBuilder();
            notificationBuilder.setContentText(getResources().getString(R.string.notif_connecting));
            PendingIntent contentIntent = PendingIntent.getActivity(RainbowSdk.instance().getContext(), 0, RainbowIntent.getLauncherIntent(getApplicationContext(), MainActivity.class), 0);
            notificationBuilder.setContentIntent(contentIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(RainbowService.EVENT_NOTIFICATION, notificationBuilder.build());

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_user:
                openProfil();
                break;
            case R.id.nav_invitation:
                openInvitation();
                break;
            case R.id.nav_logout:
                RainbowSdk.instance().connection().signout(signoutResponseListener);
                break;
            case R.id.nav_about:
                openAbout();
                break;
            case R.id.nav_setting:
                openSetting();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openInvitation(){
        Intent intent = new Intent(getBaseContext(), InvitationActivity.class);
        startActivity(intent);
    }

    private void openAbout(){
        Intent intent = new Intent(getBaseContext(), AboutActivity.class);
        startActivity(intent);
    }

    private void openSetting(){
        Intent intent = new Intent(getBaseContext(), SettingActivity.class);
        startActivity(intent);
    }

    private void switchFragment(){
        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        if(currentFragmentPosition == -1){
            fragmentTransaction.add(R.id.im_content_wrapper, fragmentsList.get(changeFragmentPosition));
        }else{
            if(currentFragmentPosition > changeFragmentPosition){
                fragmentTransaction.setCustomAnimations(R.animator.in_from_left, R.animator.out_to_right, R.animator.in_from_right, R.animator.out_to_left);
            }else{
                fragmentTransaction.setCustomAnimations(R.animator.in_from_right, R.animator.out_to_left, R.animator.in_from_left, R.animator.out_to_right);
            }
            fragmentTransaction.replace(R.id.im_content_wrapper, fragmentsList.get(changeFragmentPosition));
        }
        fragmentTransaction.commit();
        currentFragmentPosition = changeFragmentPosition;
    }

    private void showSearchFragment(){
        if(this.contactSearchFragment == null){
            this.bottomNavigationView.setVisibility(View.GONE);

            FragmentManager fragmentManager=getFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

            this.contactSearchFragment = new ContactSearchFragment();
            fragmentTransaction.replace(R.id.im_content_wrapper, this.contactSearchFragment);
            fragmentTransaction.commit();
        }
    }

    private void hideSearchFragment(){
        inputContactSearch.setText("");

        if(this.contactSearchFragment != null){
            FragmentManager fragmentManager=getFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.im_content_wrapper, fragmentsList.get(currentFragmentPosition));
            fragmentTransaction.commit();

            this.bottomNavigationView.setVisibility(View.VISIBLE);
            this.inputContactSearch.clearFocus();
            this.inputContactSearch.setVisibility(View.INVISIBLE);
            this.msgHeader.setVisibility(View.VISIBLE);
            contactSearchFragment = null;
        }else{

        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener onBottomNavigationItemSelected = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            //Edit By Roni
            switch (item.getItemId()){
                case R.id.navigation_chat:
                    changeFragmentPosition = 0; break;
                case R.id.navigation_bubbles:
                    changeFragmentPosition = 1; break;
                case R.id.navigation_contacts:
                    changeFragmentPosition = 2; break;
                default: break;
            }


            if(currentFragmentPosition != changeFragmentPosition){
                switchFragment();
            }

            return true;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {

            if (!this.drawer.isDrawerOpen(this.navigationView)) {
                this.drawer.openDrawer(this.navigationView);
            }
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    TextWatcher inputContactSearchListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String contactSearch = charSequence.toString();

            showSearchFragment();
            if(contactSearch.length() > 2){
                contactSearchFragment.setKeyword(contactSearch);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}