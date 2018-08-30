package id.co.ale.rainbowDev;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.ale.infra.contact.Contact;
import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.list.IItemListChangeListener;
import com.ale.infra.manager.Conversation;
import com.ale.infra.manager.room.Room;
import com.ale.infra.manager.room.RoomParticipant;
import com.ale.infra.proxy.conversation.IRainbowConversation;
import com.ale.listener.IRainbowGetConversationListener;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.dmoral.toasty.Toasty;
import id.co.ale.rainbowDev.Adapter.ContactSelectAdapter;
import id.co.ale.rainbowDev.Adapter.RoomSelectAdapter;
import id.co.ale.rainbowDev.Helpers.Util;
import id.co.ale.rainbowDev.Model.ContactModel;
import id.co.ale.rainbowDev.Model.RoomModel;
import id.co.ale.rainbowDev.R;

public class BroadcastActivity extends AppCompatActivity {
    private ContactSelectAdapter contactSelectAdapter;
    private ListView listContact;
    private List<ContactModel> contactModels;

    private RoomSelectAdapter roomSelectAdapter;
    private ListView listRoom;
    private List<RoomModel> roomModels;

    private ImageView btnSend;
    private EditText inputChat;
    private int numberToSend;
    private int numberSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listContact = (ListView) findViewById(R.id.list_contact);
        listRoom = (ListView) findViewById(R.id.list_room);
        btnSend = (ImageView) findViewById(R.id.send_button);
        btnSend.setOnClickListener(sendClickListener);
        inputChat = (EditText) findViewById(R.id.chat_input);
        inputChat.addTextChangedListener(inputChatListener);

        contactModels = new ArrayList<ContactModel>();
        contactSelectAdapter = new ContactSelectAdapter(this, contactModels);
        listContact.setAdapter(contactSelectAdapter);

        roomModels = new ArrayList<RoomModel>();
        roomSelectAdapter = new RoomSelectAdapter(this, roomModels);
        listRoom.setAdapter(roomSelectAdapter);

        updateContactSelect();
        RainbowSdk.instance().contacts().getRainbowContacts().registerChangeListener(new IItemListChangeListener() {
            @Override
            public void dataChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateContactSelect();
                    }
                });
            }
        });

        updateRoomSelect();
        RainbowSdk.instance().bubbles().getAllBubbles().registerChangeListener(new IItemListChangeListener() {
            @Override
            public void dataChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateRoomSelect();
                    }
                });
            }
        });
    }

    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String msg = inputChat.getText().toString();
            inputChat.setText("");

            List<Contact> cToSend = new ArrayList<Contact>();
            List<String> cId = new ArrayList<String>();

            for(int i=0; i<roomModels.size(); i++){
                RoomModel roomModel = roomModels.get(i);
                if(roomModel.isChecked()){
                    Room room = roomModel.getRoom();
                    for(RoomParticipant participant : room.getParticipants().getCopyOfDataList()){
                        Contact contact = participant.getContact();
                        if(contact.getContactId() == RainbowSdk.instance().myProfile().getConnectedUser().getContactId()){
                            continue;
                        }
                        else if( ! cId.contains(contact.getContactId())){
                            cToSend.add(contact);
                            cId.add(contact.getContactId());
                        }
                    }
                }
            }

            for(int i=0; i<contactModels.size(); i++){
                ContactModel contactModel = contactModels.get(i);
                if(contactModel.isChecked()){
                    Contact contact = (Contact) contactModel.getContact();
                    if( ! cId.contains(contact.getContactId())){
                        cToSend.add(contact);
                    }
                }
            }

            if((numberToSend = cToSend.size()) > 0) {
                numberSent = 0;
                for(Contact contact : cToSend){
                    sendBroadcastMsg(contact, msg);
                }
            }else{
                Toasty.error(getBaseContext(), getString(R.string.toast_broadcast_contact)).show();
            }
        }
    };

    private void sendBroadcastMsg(final Contact contact, final String msg){
        RainbowSdk.instance().conversations().getConversationFromContact(contact.getImJabberId(), new IRainbowGetConversationListener() {
            @Override
            public void onGetConversationSuccess(IRainbowConversation iRainbowConversation) {
                RainbowSdk.instance().im().sendMessageToConversation(iRainbowConversation, msg);
                cekComplete();
            }

            @Override
            public void onGetConversationError() {
                Conversation c = new Conversation(contact);
                RainbowSdk.instance().im().sendMessageToConversation(c, msg);
                cekComplete();
            }
        });
    }

    private void cekComplete(){
        numberSent ++;
        if(numberSent >= numberToSend){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }

    private void updateContactSelect(){
        List<IRainbowContact> contacts = nonBotContact();
        contactModels.clear();
        for(IRainbowContact c : contacts){
            contactModels.add(new ContactModel(c, false));
        }
        contactSelectAdapter.notifyDataSetChanged();
        Util.listDynamicHeight(listContact);
    }

    private void updateRoomSelect(){
        roomModels.clear();
        for(Room r : RainbowSdk.instance().bubbles().getAllBubbles().getCopyOfDataList()){
            roomModels.add(new RoomModel(r, false));
        }
        roomSelectAdapter.notifyDataSetChanged();
        Util.listDynamicHeight(listRoom);
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

    private void  updateSendButton(){
        if(inputChat.getText().length() > 0){
            btnSend.setVisibility(View.VISIBLE);
        }else{
            btnSend.setVisibility(View.GONE);
        }
    }

    private TextWatcher inputChatListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            updateSendButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}