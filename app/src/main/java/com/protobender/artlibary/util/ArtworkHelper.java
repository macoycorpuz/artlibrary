package com.protobender.artlibary.util;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.protobender.artlibary.domain.Api;
import com.protobender.artlibary.model.CenterRepo;
import com.protobender.artlibary.model.entity.Artwork;
import com.protobender.artlibary.model.entity.Result;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtworkHelper {

    //region Initialize
    static String TAG = "Artwork Helper";
    private static ArtworkHelper artworkHelper;

    public static ArtworkHelper getInstance() {

        if (null == artworkHelper) {
            artworkHelper = new ArtworkHelper();
        }
        return artworkHelper;
    }
    //endregion

    //region Artworks
    public static void enableBluetooth(Context mCtx, BluetoothAdapter mBluetoothAdapter){
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mCtx.startActivity(enableBTIntent);

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            mCtx.startActivity(discoverableIntent);
        }
    }

    public static void fetchArtworks(final Context mCtx, final TextView mError, final View mProgress, final View mRecyclerView) {
        Api.getInstance().getServices().getArtworks().enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                try {
                    if(!response.isSuccessful())
                        throw new Exception(response.errorBody().toString());
                    if(response.body().getError())
                        throw new Exception(response.body().getMessage());
                    Log.d(TAG, "onResponse: " + response.body().getMessage());
                    CenterRepo.getCenterRepo().setArtworkList(response.body().getArtworks());
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, mProgress, mRecyclerView);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.handleError(t.getMessage(), mError, mProgress, mRecyclerView);
            }
        });
    }
    //endregion

}
