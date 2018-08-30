package id.co.ale.rainbowDev;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.http.adapter.concurrent.RainbowServiceException;
import com.ale.infra.list.IItemListChangeListener;
import com.ale.listener.IRainbowSentInvitationListener;
import com.ale.rainbowsdk.RainbowSdk;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import id.co.ale.rainbowDev.Adapter.InvitationRcvAdapter;
import id.co.ale.rainbowDev.Adapter.InvitationSentAdapter;
import id.co.ale.rainbowDev.Helpers.Util;
import id.co.ale.rainbowDev.R;

public class InvitationActivity extends AppCompatActivity {
    private ListView listRcvInvitation;
    private ListView listSentInvitation;
    private InvitationRcvAdapter invitationRcvAdapter;
    private InvitationSentAdapter invitationSentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        this.listRcvInvitation = (ListView) findViewById(R.id.list_rcv_invitation);
        this.listSentInvitation = (ListView) findViewById(R.id.list_sent_invitation);

        List<IRainbowContact> pendingInvitations = RainbowSdk.instance().contacts().getPendingReceivedInvitations();
        this.invitationRcvAdapter = new InvitationRcvAdapter(InvitationActivity.this, pendingInvitations);
        this.listRcvInvitation.setAdapter(this.invitationRcvAdapter);
        Util.listDynamicHeight(this.listRcvInvitation);

        List<IRainbowContact> sentInvitations = RainbowSdk.instance().contacts().getPendingSentInvitations();
        this.invitationSentAdapter = new InvitationSentAdapter(InvitationActivity.this, sentInvitations);
        this.listSentInvitation.setAdapter(this.invitationSentAdapter);
        Util.listDynamicHeight(this.listSentInvitation);

        RainbowSdk.instance().contacts().getSentInvitations().registerChangeListener(new IItemListChangeListener() {
            @Override
            public void dataChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateSentInvitations();
                    }
                });
            }
        });
        RainbowSdk.instance().contacts().getReceivedInvitations().registerChangeListener(new IItemListChangeListener() {
            @Override
            public void dataChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateRcvInvitations();
                    }
                });
            }
        });
    }

    private void updateRcvInvitations(){
        this.invitationRcvAdapter.clear();
        this.invitationRcvAdapter.addAll(RainbowSdk.instance().contacts().getPendingReceivedInvitations());  //.getPendingReceivedInvitations());
        this.invitationRcvAdapter.notifyDataSetChanged();
        Util.listDynamicHeight(this.listRcvInvitation);
    }

    private void updateSentInvitations(){
        this.invitationSentAdapter.clear();
        this.invitationSentAdapter.addAll(RainbowSdk.instance().contacts().getPendingSentInvitations()); //getPendingSentInvitations());
        this.invitationSentAdapter.notifyDataSetChanged();
        Util.listDynamicHeight(this.listSentInvitation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.invitation_menu, menu);
        return true;
    }

    private void openScanner(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    IntentIntegrator integrator = new IntentIntegrator(InvitationActivity.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setCameraId(0);
                    integrator.setPrompt("");
                    integrator.initiateScan();
                }catch (Exception e){
                    Util.log(e.toString());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.profile_qrcode){
            openScanner();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                addFriend(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addFriend(String qrContent){
        String []contactFromQr = qrContent.split(":");
        if(contactFromQr.length == 2){
            RainbowSdk.instance().contacts().addRainbowContactToRoster(contactFromQr[0], new IRainbowSentInvitationListener() {
                @Override
                public void onInvitationSentSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Util.log("sent");
                        }
                    });
                }

                @Override
                public void onInvitationSentError(RainbowServiceException e) {

                }

                @Override
                public void onInvitationError() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Util.log("error");
                        }
                    });
                }
            });
        }
    }
}
