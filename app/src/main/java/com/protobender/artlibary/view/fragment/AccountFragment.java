package com.protobender.artlibary.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.protobender.artlibary.R;
import com.protobender.artlibary.model.CenterRepo;
import com.protobender.artlibary.model.entity.Artwork;
import com.protobender.artlibary.util.ArtworkHelper;
import com.protobender.artlibary.util.SharedPrefManager;
import com.protobender.artlibary.util.Tags;
import com.protobender.artlibary.util.Utils;
import com.protobender.artlibary.view.activity.LoginActivity;
import com.protobender.artlibary.view.activity.SignUpActivity;
import com.protobender.artlibary.view.adapter.ArtworkAdapter;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    //region Attributes
    View mView, mViewLogin, mViewUser;
    TextView mName, mEmail, mNumber, mAddress;
    Button mLogin, mSignIn, mLogout;

    View mViewArtworkList;
    Button mBrowse;
    TextView mError;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    ProgressBar mProgress;

    ArtworkAdapter artworkAdapter;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account, container, false);
        mViewLogin = mView.findViewById(R.id.formLogin);
        mViewUser = mView.findViewById(R.id.formAccount);

        mName = mView.findViewById(R.id.txtUserName);
        mEmail = mView.findViewById(R.id.txtUserEmail);
        mNumber = mView.findViewById(R.id.txtUserNumber);
        mAddress = mView.findViewById(R.id.txtUserAddress);
        mLogin = mView.findViewById(R.id.btnAccountLogin);
        mSignIn = mView.findViewById(R.id.btnAccountSignIn);
        mLogout = mView.findViewById(R.id.btnAccountLogout);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        mViewArtworkList = mView.findViewById(R.id.viewArtworkList);
        mProgress = mViewArtworkList.findViewById(R.id.progressView);
        mError = mViewArtworkList.findViewById(R.id.txtItemError);
        mSwipeRefreshLayout = mViewArtworkList.findViewById(R.id.swipeView);
        mRecyclerView = mViewArtworkList.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showUser();
            }
        });

        if (SharedPrefManager.getInstance().isLoggedIn(getActivity())) showUser();
        else showLogin();

        return mView;
    }

    private void clearView() {
        mRecyclerView.setAdapter(null);
        mError.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showUser() {
        mViewUser.setVisibility(View.VISIBLE);
        mViewLogin.setVisibility(View.GONE);

        mName.setText(SharedPrefManager.getInstance().getUser(getActivity()).getName());
        mEmail.setText(SharedPrefManager.getInstance().getUser(getActivity()).getEmail());
        mNumber.setText(SharedPrefManager.getInstance().getUser(getActivity()).getNumber());
        mAddress.setText(SharedPrefManager.getInstance().getUser(getActivity()).getAddress());

        clearView();
        ArtworkHelper.fetchArtworks(getActivity(), mError, mProgress, mSwipeRefreshLayout);
        artworkAdapter = new ArtworkAdapter(getActivity(), artworkList());
        artworkAdapter.setOnItemClickListener(new ArtworkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Utils.setPosition(position);
                Utils.switchContent(getActivity(), R.id.fragContainer, Tags.ARTWORK_DETAILS_FRAGMENT);
            }
        });
        mRecyclerView.setAdapter(artworkAdapter);
        Utils.showProgress(false, mProgress, mSwipeRefreshLayout);
    }

    private List<Artwork> artworkList(){
        int userId = SharedPrefManager.getInstance().getUser(getActivity()).getUserId();
        List<Artwork> artwork = new ArrayList<>();
        for(Artwork art : CenterRepo.getCenterRepo().getArtworkList()) {
            if(art.getUserId() == userId) artwork.add(art);
        }
        return artwork;
    }

    private void showLogin() {
        mViewUser.setVisibility(View.GONE);
        mViewLogin.setVisibility(View.VISIBLE);
    }

    private void login() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    private void signIn() {
        startActivity(new Intent(getActivity(), SignUpActivity.class));
    }

    private void logOut() {
        SharedPrefManager.getInstance().logout(getActivity());
        getActivity().finish();
    }
}
