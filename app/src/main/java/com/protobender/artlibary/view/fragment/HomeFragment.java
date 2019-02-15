package com.protobender.artlibary.view.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.protobender.artlibary.domain.Api;
import com.protobender.artlibary.model.CenterRepo;
import com.protobender.artlibary.model.entity.Artwork;
import com.protobender.artlibary.R;
import com.protobender.artlibary.model.entity.Result;
import com.protobender.artlibary.util.ArtworkHelper;
import com.protobender.artlibary.util.Tags;
import com.protobender.artlibary.util.Utils;
import com.protobender.artlibary.view.adapter.ArtworkAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {


    //region Attributes
    String TAG = "Home Fragment";

    View mView, mViewArtworkList;
    Button mBrowse;
    TextView mError;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    ProgressBar mProgress;

    ArtworkAdapter artworkAdapter;
    List<Artwork> discoveredArtwork;

    BluetoothAdapter mBluetoothAdapter;
    List<BluetoothDevice> mDevices = new ArrayList<>();
    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mBrowse = mView.findViewById(R.id.btnBrowse);
        mViewArtworkList = mView.findViewById(R.id.viewArtworkList);
        mProgress = mViewArtworkList.findViewById(R.id.progressView);
        mError = mViewArtworkList.findViewById(R.id.txtItemError);
        mSwipeRefreshLayout = mViewArtworkList.findViewById(R.id.swipeView);
        mRecyclerView = mViewArtworkList.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        mBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showArtwork();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                browseArtwork();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ArtworkHelper.enableBluetooth(getActivity(), mBluetoothAdapter);
        return mView;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(btReceiver);
            mBluetoothAdapter.cancelDiscovery();
        } catch (Exception ex) {
            Log.d(TAG, "onDestroy: " + ex.getLocalizedMessage());
        }
    }

    private void clearView() {
        mRecyclerView.setAdapter(null);
        mSwipeRefreshLayout.setRefreshing(false);
        mDevices.clear();
        mError.setVisibility(View.GONE);
        mBrowse.setVisibility(View.GONE);
        mViewArtworkList.setVisibility(View.VISIBLE);
    }

    private void showArtwork() {
        clearView();
        ArtworkHelper.fetchArtworks(getActivity(), mError, mProgress, mSwipeRefreshLayout);
        browseArtwork();
        discoveredArtwork = new ArrayList<>();
        artworkAdapter = new ArtworkAdapter(getActivity(), discoveredArtwork);
        artworkAdapter.setOnItemClickListener(new ArtworkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Utils.setPosition(position);
                Utils.switchContent(getActivity(), R.id.fragContainer, Tags.ARTWORK_DETAILS_FRAGMENT);
            }
        });
        mRecyclerView.setAdapter(artworkAdapter);
    }

    private void browseArtwork() {
        clearView();
        Utils.showProgress(true, mProgress, mSwipeRefreshLayout);
        mBluetoothAdapter.cancelDiscovery();
        if(!mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(btReceiver, discoverDevicesIntent);
        }
    }

    //region btReceiver
    private BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){

                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress() + ": " + String.valueOf(rssi));

                Artwork artwork = new Artwork();
                List<Artwork> artworks = CenterRepo.getCenterRepo().getArtworkList();
                for(Artwork art : artworks) {
                    Log.d(TAG, "getArtworkByDevice: art " + art.getDeviceName());
                    if(art.getDeviceName().equals(device.getName()))
                        discoveredArtwork.add(artwork);
                        artworkAdapter.notifyDataSetChanged();
                        Utils.showProgress(false, mProgress, mSwipeRefreshLayout);
                }
            }
        }
    };
    //endregion
}
