package id.co.ale.rainbowDev.IMFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.list.IItemListChangeListener;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.Iterator;
import java.util.List;

import id.co.ale.rainbowDev.Adapter.ContactsAdapter;
import id.co.ale.rainbowDev.R;
import id.co.ale.rainbowDev.Helpers.Util;

/**
 * Created by Alcatel-Dev on 04/09/2017.
 */

public class ContactsFragment extends Fragment{
    private ListView listView;
    private ContactsAdapter contactsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("nais1", "hahahah");
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        ContactsFragmentViewHolder viewHolder = new ContactsFragmentViewHolder(v);
        this.listView = (ListView) viewHolder.listView;

        //----------------
        this.contactsAdapter = new ContactsAdapter(getActivity(), nonBotContact());
        listView.setAdapter(contactsAdapter);
        listView.setLongClickable(true);

        RainbowSdk.instance().contacts().getRainbowContacts().registerChangeListener(contactsChangeListener);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        RainbowSdk.instance().contacts().getRainbowContacts().unregisterChangeListener(contactsChangeListener);
        super.onDestroyView();
    }

    public IItemListChangeListener contactsChangeListener = new IItemListChangeListener() {
        @Override
        public void dataChanged() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("nais1", "run : ok ");
                    contactsAdapter.clear();
                    contactsAdapter.addAll(nonBotContact());
                    contactsAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private List<IRainbowContact> nonBotContact(){
        List<IRainbowContact> contacts = RainbowSdk.instance().contacts().getRainbowContacts().getCopyOfDataList();
       //Log.d("nais1", contacts.toString());
//        Log.d("nais1", contacts.get(0).getFirstName());
  //      Log.d("nais1", contacts.get(0).getImJabberId());

        Iterator iterator = contacts.iterator();
        while (iterator.hasNext()) {
            IRainbowContact _c = (IRainbowContact) iterator.next();
            if(_c.isBot()){
                iterator.remove();
            }
        }

//        String s = contacts.size().
  //      Log.d("nais1",contacts.size());
        for(int i=0; i<contacts.size(); i++){
            if(contacts.get(i).getImJabberId().equals(Util.BOT_JABBER_ID)){
                IRainbowContact tempContact = contacts.get(i);
                for(int j=i; j>0; j--){
                    contacts.set(j, contacts.get(j-1));
                    Log.d("nais1",String.valueOf(j));
                    Log.d("nais1",contacts.get(j-1).toString());



                    //Util.log(j.);
                }
                contacts.set(0,     tempContact);
                break;
            }
        }
        return contacts;
    }
}

class ContactsFragmentViewHolder{
    public ListView listView;
    public ContactsFragmentViewHolder(View v){
        this.listView = v.findViewById(R.id.contacts_list);
    }
}