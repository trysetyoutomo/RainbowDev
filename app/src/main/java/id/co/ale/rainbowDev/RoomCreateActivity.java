package id.co.ale.rainbowDev;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ale.infra.contact.Contact;
import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.list.IItemListChangeListener;
import com.ale.infra.manager.room.Room;
import com.ale.infra.proxy.room.IRoomProxy;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.dmoral.toasty.Toasty;
import id.co.ale.rainbowDev.Adapter.ContactSelectAdapter;
import id.co.ale.rainbowDev.Adapter.ContactSelectedAdapter;
import id.co.ale.rainbowDev.Helpers.Util;
import id.co.ale.rainbowDev.Model.ContactModel;
import id.co.ale.rainbowDev.R;

public class RoomCreateActivity extends AppCompatActivity {
    private Button btnCreateBubble;
    private EditText tBubbleName;
    private EditText tBubblesubject;
    private LinearLayout addParticipantLayout;

    private ContactSelectAdapter contactSelectAdapter;
    private ContactSelectedAdapter memberAdapter;
    private ListView listContact;
    private ListView listMemberView;
    private TextView noMember;

    private ImageView addConfirm;
    private List<IRainbowContact> listMember = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_room_create);
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

        this.addParticipantLayout = (LinearLayout) findViewById(R.id.layout_add_participants);

        this.tBubbleName = (EditText) findViewById(R.id.create_bubble_name);
        this.tBubblesubject = (EditText) findViewById(R.id.create_bubble_subject);

        this.btnCreateBubble = (Button) findViewById(R.id.btn_create_bubble);
        this.btnCreateBubble.setOnClickListener(btnCreateClickListener);

        this.addConfirm = (ImageView) findViewById(R.id.add_confirm);
        this.addConfirm.setOnClickListener(addConfirmClickListener);

        this.noMember = (TextView) findViewById(R.id.no_member);
        listMemberView = (ListView) findViewById(R.id.list_members);
        listMember = new ArrayList<IRainbowContact>();

        listContact = (ListView) findViewById(R.id.list_contact);
        updateContactSelect();
        RainbowSdk.instance().contacts().getRainbowContacts().registerChangeListener(contactListener);
    }

    private IItemListChangeListener contactListener = new IItemListChangeListener() {
        @Override
        public void dataChanged() {
            if(addParticipantLayout.getVisibility() != View.VISIBLE){
                runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateContactSelect();
                }
            });
            }
        }
    };

    private View.OnClickListener addConfirmClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for(int i=0; i<contactSelectAdapter.getCount(); i++){
                ContactModel cm = contactSelectAdapter.getItem(i);
                if(cm.isChecked()){
                    listMember.add(cm.getContact());
                }
            }
            addParticipantLayout.setVisibility(View.GONE);
            updateSelectedMember();
        }
    };

    private void updateSelectedMember(){
        memberAdapter = new ContactSelectedAdapter(this, listMember);
        Util.log(String.valueOf(memberAdapter.getCount()));
        listMemberView.setAdapter(memberAdapter);

        if(listMember.size() > 0){
            noMember.setVisibility(View.GONE);
            listMemberView.setVisibility(View.VISIBLE);
        }
    }

    private void updateContactSelect(){
        List<IRainbowContact> contacts = nonBotContact();
        List<ContactModel> contactModels = new ArrayList<ContactModel>(contacts.size());
        for(IRainbowContact c : contacts){
            boolean isAdded = false;
            for(IRainbowContact _c : listMember){
                if(_c.getContactId().equals(c.getContactId())){
                    isAdded = true;
                    break;
                }
            }
            if(!isAdded) contactModels.add(new ContactModel(c, false));
        }
        contactSelectAdapter = new ContactSelectAdapter(this, contactModels);
        listContact.setAdapter(contactSelectAdapter);
    }

    private List<IRainbowContact> nonBotContact(){
        List<IRainbowContact> contacts = RainbowSdk.instance().contacts().getRainbowContacts().getCopyOfDataList();

        Iterator iterator = contacts.iterator();
        while (iterator.hasNext()) {
            IRainbowContact _c = (IRainbowContact) iterator.next();
            if(_c.isBot()){
                iterator.remove();
            }
        }
        return contacts;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.room_create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.participant_add:
                updateContactSelect();
                addParticipantLayout.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener btnCreateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String bubbleName = tBubbleName.getText().toString();
            String bubblesubject = tBubblesubject.getText().toString();

            if(bubbleName.length() == 0){
                tBubbleName.requestFocus();
                Toasty.error(getBaseContext(), getString(R.string.toast_bubble_name)).show();
                return;
            }else if(bubblesubject.length() == 0){
                tBubblesubject.requestFocus();
                Toasty.error(getBaseContext(), getString(R.string.toast_bubble_subject)).show();
                return;
            }else if(listMember.isEmpty()) {
                Toasty.error(getBaseContext(), getString(R.string.toast_bubble_member)).show();
                return;
            }else{
                btnCreateBubble.setEnabled(false);
//                RainbowSdk.instance().bubbles().createBubble(bubbleName, bubblesubject, roomCreationListener);
                RainbowSdk.instance().bubbles().createBubble(bubbleName, bubblesubject, false, true, roomCreationListener);
            }
        }
    };

    IRoomProxy.IRoomCreationListener roomCreationListener = new IRoomProxy.IRoomCreationListener() {
        @Override
        public void onCreationSuccess(final Room room) {
            Util.tempRoom = room;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(listMember.size() > 0){
                        RainbowSdk.instance().bubbles().addParticipantsToBubble(room, listMember, new IRoomProxy.IAddParticipantsListener() {
                            @Override
                            public void onAddParticipantsSuccess() {
                                showRoom();
                            }

                            @Override
                            public void onMaxParticipantsReached() {}

                            @Override
                            public void onAddParticipantFailed(Contact contact) {}
                        });
                    }else{
                        showRoom();
                    }
                }
            });
        }

        private void showRoom(){
            Intent intent = new Intent(getBaseContext(), RoomDetailActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onCreationFailed() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toasty.error(getBaseContext(), getString(R.string.toast_bubble_failed)).show();
                    btnCreateBubble.setEnabled(true);
                }
            });
        }
    };

    @Override
    public void onBackPressed(){
        if(addParticipantLayout.getVisibility() == View.VISIBLE){
            addParticipantLayout.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }
}