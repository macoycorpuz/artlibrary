package com.protobender.artlibary.view.fragment;

import com.protobender.artlibary.R;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.protobender.artlibary.domain.Api;
import com.protobender.artlibary.model.CenterRepo;
import com.protobender.artlibary.model.entity.Artwork;
import com.protobender.artlibary.model.entity.Result;
import com.protobender.artlibary.util.SharedPrefManager;
import com.protobender.artlibary.util.Tags;
import com.protobender.artlibary.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtworkDetailsFragment extends Fragment{

    //region Attributes
    String TAG = "Artwork Details";

    View mView, mViewButtons;
    TextView mArtworkName, mAuthor, mDate, mDescription, mDeviceName, mStrength;
    Button mEdit, mDelete;
    ImageView mImage;
    ProgressDialog pDialog;
    TextToSpeech tts;


    int position;
    Artwork artwork;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_artwork_details, container, false);
        mViewButtons = mView.findViewById(R.id.viewArtButtons);

        mStrength = mView.findViewById(R.id.txtArtStrength);
        mArtworkName = mView.findViewById(R.id.txtArtName);
        mAuthor = mView.findViewById(R.id.txtArtAuthor);
        mDate = mView.findViewById(R.id.txtArtDate);
        mDescription = mView.findViewById(R.id.txtArtDescription);
        mDeviceName = mView.findViewById(R.id.txtArtDeviceName);
        mImage = mView.findViewById(R.id.imgArtwork);
        mEdit = mView.findViewById(R.id.btnEdit);
        mDelete = mView.findViewById(R.id.btnDelete);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateArtwork();
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArtwork();
            }
        });
        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        showArtwork();
        authenticate();
        return mView;
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private void authenticate() {
        boolean isLoggedIn = SharedPrefManager.getInstance().isLoggedIn(getActivity());
        int userId = SharedPrefManager.getInstance().getUser(getActivity()).getUserId();
        if(isLoggedIn) mViewButtons.setVisibility((artwork.getUserId() == userId) ? View.VISIBLE : View.GONE);
    }

    private void showArtwork() {
        artwork = CenterRepo.getCenterRepo().getArtworkList().get(position);
        mArtworkName.setText(artwork.getArtworkName());
        mAuthor.setText(artwork.getAuthor());
        mDate.setText(artwork.getDate());
        mDescription.setText(artwork.getDescription());
        mDeviceName.setText(String.valueOf(artwork.getDeviceName()));
        Picasso.get()
                .load(artwork.getArtworkUrl())
                .placeholder(R.drawable.ic_photo_blue_24dp)
                .error(R.drawable.ic_error_outline_red_24dp)
                .fit()
                .centerCrop()
                .into(mImage);
        readDescription();
    }

    private void deleteArtwork() {
        pDialog = Utils.showProgressDialog(getActivity(), "Deleting artwork...");
        Api.getInstance().getServices().deleteArtwork(artwork.getArtworkId()).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull final Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if (response.errorBody() != null)
                        throw new Exception(response.errorBody().string());
                    if (response.body().getError())
                        throw new Exception(response.body().getMessage());
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    Utils.switchContent(getActivity(), R.id.fragContainer, Tags.HOME_FRAGMENT);
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Utils.dismissProgressDialog(pDialog);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void readDescription() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tts.speak(artwork.getDescription(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }, 100);
    }
}
