package com.protobender.artlibary.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.protobender.artlibary.R;
import com.protobender.artlibary.domain.Api;
import com.protobender.artlibary.model.CenterRepo;
import com.protobender.artlibary.model.entity.Artwork;
import com.protobender.artlibary.model.entity.Result;
import com.protobender.artlibary.util.Tags;
import com.protobender.artlibary.util.Utils;
import com.protobender.artlibary.view.adapter.ArtworkAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtworksFragment extends Fragment {


    //region Attributes
    String TAG = "Artworks Fragment";

    View mView, mViewArtworkList;
    EditText mSearch;
    TextView mError;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    ProgressBar mProgress;

    ArtworkAdapter artworkAdapter;
    List<Artwork> artworkList;
    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_artworks, container, false);

        mSearch = mView.findViewById(R.id.txtArtworkSearch);

        mViewArtworkList = mView.findViewById(R.id.viewArtworkList);
        mProgress = mViewArtworkList.findViewById(R.id.progressView);
        mError = mViewArtworkList.findViewById(R.id.txtItemError);
        mSwipeRefreshLayout = mViewArtworkList.findViewById(R.id.swipeView);
        mRecyclerView = mViewArtworkList.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        mSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode ==KeyEvent.KEYCODE_ENTER)
                {
                    fetchArtworks(mSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchArtworks(null);
            }
        });
        fetchArtworks(null);

        return mView;
    }

    private void clearView() {
        mRecyclerView.setAdapter(null);
        mError.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void fetchArtworks(String artworkName) {
        clearView();
        Utils.showProgress(true, mProgress, mSwipeRefreshLayout);
        Call<Result> call;
        if (!TextUtils.isEmpty(artworkName)) call = Api.getInstance().getServices().getArtworkByName(artworkName);
        else call =Api.getInstance().getServices().getArtworks();
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                try {
                    Utils.showProgress(false, mProgress, mSwipeRefreshLayout);
                    if(!response.isSuccessful())
                        throw new Exception(response.errorBody().toString());
                    if(response.body().getError())
                        throw new Exception(response.body().getMessage());
                    Log.d(TAG, "onResponse: artworks");
                    artworkList = response.body().getArtworks();
                    CenterRepo.getCenterRepo().setArtworkList(artworkList);
                    showArtwork();
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, mProgress, mSwipeRefreshLayout);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.handleError(t.getMessage(), mError, mProgress, mSwipeRefreshLayout);
            }
        });
    }

    private void showArtwork() {
        artworkAdapter = new ArtworkAdapter(getActivity(), artworkList, 0);
        artworkAdapter.setOnItemClickListener(new ArtworkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Utils.setPosition(position);
                Utils.switchContent(getActivity(), R.id.fragContainer, Tags.ARTWORK_DETAILS_FRAGMENT);
            }
        });

        mRecyclerView.setAdapter(artworkAdapter);
    }
}
