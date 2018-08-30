package id.co.ale.rainbowDev.IMFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ale.infra.contact.Contact;
import com.ale.infra.contact.RainbowPresence;
import com.ale.infra.list.IItemListChangeListener;
import com.ale.infra.proxy.conversation.IRainbowConversation;
import com.ale.rainbowsdk.RainbowSdk;


import id.co.ale.rainbowDev.Adapter.CallsAdapter;
import id.co.ale.rainbowDev.R;

/**
 * Created by Alcatel-Dev on 04/09/2017.
 */

public class CallsFragment extends Fragment{
    private CallsAdapter callsAdapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calls, container, false);
        CallsFragmentViewHolder viewHolder = new CallsFragmentViewHolder(v);

        //-------------
        this.listView = (ListView) viewHolder.listView;
        this.callsAdapter = new CallsAdapter(getActivity(), RainbowSdk.instance().conversations().getAllConversations().getCopyOfDataList());
        listView.setAdapter(callsAdapter);

        RainbowSdk.instance().conversations().getAllConversations().registerChangeListener(conversationsChangeListener);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        RainbowSdk.instance().conversations().getAllConversations().unregisterChangeListener(conversationsChangeListener);

        for(IRainbowConversation conversation : RainbowSdk.instance().conversations().getAllConversations().getCopyOfDataList()){
            conversation.getContact().unregisterChangeListener(contactListener);
        }

        super.onDestroyView();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public IItemListChangeListener conversationsChangeListener = new IItemListChangeListener() {
        @Override
        public void dataChanged() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callsAdapter.clear();
                    callsAdapter.addAll( RainbowSdk.instance().conversations().getAllConversations().getCopyOfDataList());
                    callsAdapter.notifyDataSetChanged();
                }
            });

            for(IRainbowConversation conversation : RainbowSdk.instance().conversations().getAllConversations().getCopyOfDataList()){
                conversation.getContact().registerChangeListener(contactListener);
            }
        }
    };


    Contact.ContactListener contactListener = new Contact.ContactListener() {
        @Override
        public void contactUpdated(Contact contact) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callsAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onPresenceChanged(Contact contact, RainbowPresence rainbowPresence) {}
        @Override
        public void onActionInProgress(boolean b) {}
    };
}

class CallsFragmentViewHolder{
    public ListView listView;
    public CallsFragmentViewHolder(View v){
        this.listView = v.findViewById(R.id.calls_list);
    }
}