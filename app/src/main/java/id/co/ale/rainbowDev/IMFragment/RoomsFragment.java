package id.co.ale.rainbowDev.IMFragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ale.infra.list.IItemListChangeListener;
import com.ale.rainbowsdk.RainbowSdk;

import id.co.ale.rainbowDev.Adapter.InvitationRoomRcvAdapter;
import id.co.ale.rainbowDev.Adapter.RoomsAdapter;
import id.co.ale.rainbowDev.Helpers.Util;
import id.co.ale.rainbowDev.R;
import id.co.ale.rainbowDev.RoomCreateActivity;

/**
 * Created by Alcatel-Dev on 04/09/2017.
 */

public class RoomsFragment extends Fragment {
    private ListView listView;
    private ListView pendingListView;
    private RoomsAdapter roomsAdapter;
    private InvitationRoomRcvAdapter pendingAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rooms, container, false);
        RoomsFragmentViewHolder viewHolder = new RoomsFragmentViewHolder(v);

        this.listView = (ListView) viewHolder.listView;
        this.pendingListView = (ListView) viewHolder.pendingListView;

        //----------------
        this.roomsAdapter = new RoomsAdapter(getActivity(), RainbowSdk.instance().bubbles().getAllList());
        listView.setAdapter(roomsAdapter);
        Util.listDynamicHeight(listView);

        this.pendingAdapter = new InvitationRoomRcvAdapter(getActivity(), RainbowSdk.instance().bubbles().getPendingList());
        pendingListView.setAdapter(pendingAdapter);
        Util.listDynamicHeight(pendingListView);

        RainbowSdk.instance().bubbles().getAllBubbles().registerChangeListener(roomsChangeListener);

        viewHolder.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(getActivity().getBaseContext(), RoomCreateActivity.class);
                startActivity(createIntent);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        RainbowSdk.instance().bubbles().getAllBubbles().unregisterChangeListener(roomsChangeListener);
        super.onDestroyView();
    }

    public IItemListChangeListener roomsChangeListener = new IItemListChangeListener() {
        @Override
        public void dataChanged() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    roomsAdapter.clear();
                    roomsAdapter.addAll(RainbowSdk.instance().bubbles().getAllList());
                    roomsAdapter.notifyDataSetChanged();
                    Util.listDynamicHeight(listView);

                    pendingAdapter.clear();
                    pendingAdapter.addAll(RainbowSdk.instance().bubbles().getPendingList());
                    pendingAdapter.notifyDataSetChanged();
                    Util.listDynamicHeight(pendingListView);
                }
            });
        }
    };
}

class RoomsFragmentViewHolder{
    public ListView listView;
    public ListView pendingListView;
    public Button btnCreate;
    public RoomsFragmentViewHolder(View v){
        this.listView = (ListView) v.findViewById(R.id.rooms_list);
        this.pendingListView = (ListView) v.findViewById(R.id.pending_list);
        this.btnCreate = (Button) v.findViewById(R.id.btn_create_bubble);
    }
}