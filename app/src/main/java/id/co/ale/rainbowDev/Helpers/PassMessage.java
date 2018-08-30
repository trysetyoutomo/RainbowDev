package id.co.ale.rainbowDev.Helpers;

import android.content.Context;
import android.os.AsyncTask;

import com.ale.infra.contact.Contact;
import com.ale.infra.contact.IContact;
import com.ale.infra.http.adapter.concurrent.RainbowServiceException;
import com.ale.infra.manager.Conversation;
import com.ale.infra.proxy.conversation.IRainbowConversation;
import com.ale.infra.proxy.users.IUserProxy;
import com.ale.listener.IRainbowContactsSearchListener;
import com.ale.listener.IRainbowGetConversationListener;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.List;

/**
 * Created by ALEIndo on 30/01/18.
 */

public class PassMessage extends AsyncTask<String, Void, String>{
    Context ctx;
    //Set Context
    PassMessage(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params)
    {
        String jid = params[0];
        final String msg = params[1];

        RainbowSdk.instance().contacts().searchByJid(jid, new IRainbowContactsSearchListener() {
            @Override
            public void searchStarted() {}

            @Override
            public void searchFinished(List<IContact> list) {
                if(list.size() > 0){
                    try{
                        RainbowSdk.instance().contacts().getUserDataFromId(list.get(0).getCorporateId(), new IUserProxy.IGetUserDataListener() {
                            @Override
                            public void onSuccess(final Contact contact) {
                                try{
                                            RainbowSdk.instance().conversations().getConversationFromContact(contact.getImJabberId(), new IRainbowGetConversationListener() {
                                                @Override
                                                public void onGetConversationSuccess(final IRainbowConversation iRainbowConversation) {

                                                            if(iRainbowConversation.getMessages().getCount() == 0){
                                                                Conversation c = new Conversation(contact);
                                                                RainbowSdk.instance().im().sendMessageToConversation(c, "WELCOMING_MESSAGE_ALE_HOTEL "+msg);
                                                            }
                                                }

                                                @Override
                                                public void onGetConversationError() {

                                                            Conversation c = new Conversation(contact);
                                                            RainbowSdk.instance().im().sendMessageToConversation(c, "WELCOMING_MESSAGE_ALE_HOTEL "+msg);

                                                }
                                            });
                                }catch (Exception ee){}
                            }

                            @Override
                            public void onFailure(RainbowServiceException e) {}
                        });
                    }catch (Exception e){}
                }
            }

            @Override
            public void searchError(RainbowServiceException e) {}
        });
        return  null;
    }
}
