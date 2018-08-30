package id.co.ale.rainbowDev.IMFragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ale.infra.contact.Contact;
import com.ale.infra.contact.RainbowPresence;
import com.ale.infra.list.IItemListChangeListener;
import com.ale.infra.manager.room.Room;
import com.ale.infra.proxy.conversation.IRainbowConversation;
import com.ale.rainbowsdk.RainbowSdk;


import java.util.ArrayList;

import id.co.ale.rainbowDev.Adapter.ConversationsAdapter;
import id.co.ale.rainbowDev.BroadcastActivity;
import id.co.ale.rainbowDev.Helpers.Util;
import id.co.ale.rainbowDev.R;

/**
 * Created by Alcatel-Dev on 04/09/2017.
 */

public class ConversationsFragment extends Fragment{
    private ConversationsAdapter conversationsAdapterBot = null;
    private ConversationsAdapter conversationsAdapterContact = null;
    private ConversationsAdapter conversationsAdapterContactService = null;
    private ListView listViewContact;
    private ListView listViewBot;
    private ListView listViewContactService;
    private FloatingActionButton broadcastBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_conversations, container, false);

//        this.listViewBot = (ListView) v.findViewById(R.id.conversations_list_bot);
//        this.conversationsAdapterBot = new ConversationsAdapter(getActivity(), new ArrayList<IRainbowConversation>());
//        listViewBot.setAdapter(conversationsAdapterBot);

        this.listViewContact = (ListView) v.findViewById(R.id.conversations_list_contact);
        Log.d("nais", "masuuuuukk");
        this.conversationsAdapterContact = new ConversationsAdapter(getActivity(), new ArrayList<IRainbowConversation>());
        listViewContact.setAdapter(conversationsAdapterContact);

//        this.listViewContactService = (ListView) v.findViewById(R.id.conversations_list_contact);
//        this.conversationsAdapterContactService = new ConversationsAdapter(getActivity(), new ArrayList<IRainbowConversation>());
//        listViewContactService.setAdapter(conversationsAdapterContactService);

        updateConversation();
        RainbowSdk.instance().conversations().getAllConversations().registerChangeListener(conversationsChangeListener);

        this.broadcastBtn = (FloatingActionButton) v.findViewById(R.id.btn_broadcast);
        this.broadcastBtn.setOnClickListener(broadcasrListener);

        return v;
    }

    private View.OnClickListener broadcasrListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent broadcastIntent = new Intent(getActivity().getBaseContext(), BroadcastActivity.class);
            startActivity(broadcastIntent);
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        updateConversation();
    }

    @Override
    public void onDestroyView() {
        RainbowSdk.instance().conversations().getAllConversations().unregisterChangeListener(conversationsChangeListener);

        for(IRainbowConversation conversation : RainbowSdk.instance().conversations().getAllConversations().getCopyOfDataList()){
            if(conversation.isRoomType()){
                conversation.getRoom().unregisterChangeListener(roomListener);
            }else{
                conversation.getContact().unregisterChangeListener(contactListener);
            }
        }

        super.onDestroyView();
    }

    private void updateConversation(){

        conversationsAdapterContact.clear();


        for(IRainbowConversation conversation : RainbowSdk.instance().conversations().getAllConversations().getCopyOfDataList()){
                if(conversation.isRoomType()){
                    conversationsAdapterContact.add(conversation);
                }else{

                    if( ! conversation.getContact().isBot()){
                        conversationsAdapterContact.add(conversation);
                    }
                }
         //   Log.d("nais", conversation.toString());

        }

        Util.log(String.valueOf(conversationsAdapterContact.getCount()));

    //    Log.d("nais", String.valueOf(conversationsAdapterContact.getCount()));
        conversationsAdapterContact.notifyDataSetChanged();
        Util.listDynamicHeight(listViewContact);



//        conversationsAdapterContactService.notifyDataSetChanged();
//        Util.listDynamicHeight(listViewContactService);
    }

    public IItemListChangeListener conversationsChangeListener = new IItemListChangeListener() {
        @Override
        public void dataChanged() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateConversation();
                }
            });
        }
    };

    IItemListChangeListener messagesListener = new IItemListChangeListener() {
        @Override
        public void dataChanged() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateConversation();
                }
            });
        }
    };

    Room.RoomListener roomListener = new Room.RoomListener() {
        @Override
        public void roomUpdated(Room room) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    conversationsAdapterContact.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void conferenceUpdated(Room room) {

        }
    };

    Contact.ContactListener contactListener = new Contact.ContactListener() {
        @Override
        public void contactUpdated(Contact contact) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    conversationsAdapterContact.notifyDataSetChanged();
//                    conversationsAdapterBot.notifyDataSetChanged();
                    conversationsAdapterContactService.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onPresenceChanged(Contact contact, RainbowPresence rainbowPresence) {}
        @Override
        public void onActionInProgress(boolean b) {}
    };
}