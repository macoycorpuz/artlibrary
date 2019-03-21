package com.protobender.artlibary.view.fragment;

import com.protobender.artlibary.R;
import com.protobender.artlibary.domain.Api;
import com.protobender.artlibary.model.entity.Artwork;
import com.protobender.artlibary.model.entity.Result;
import com.protobender.artlibary.util.SharedPrefManager;
import com.protobender.artlibary.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFragment extends Fragment{

    //region Attributes
    String TAG = "Post Fragment";
    View mView;
    Button mRecord, mPlay, mPost;
    EditText mArtworkName, mAuthor, mDate, mDescription, mDeviceName, mPrice, mLocation;
    ImageView mImage;
    TextView mError;
    ProgressDialog pDialog;

    RequestBody artworkNameBody, authorBody, dateBody, descriptionBody, deviceNameBody, userIdBody, fileBody, priceBody, locationBody, audioBody;
    Uri fileUri;

    int PICK_IMAGE_REQUEST = 1;
    Artwork artwork;

    private static String audioFileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_post, container, false);

        // Initialize Views
        mArtworkName = mView.findViewById(R.id.txtPostArtworkName);
        mAuthor = mView.findViewById(R.id.txtPostAuthor);
        mDate = mView.findViewById(R.id.txtPostDate);
        mDescription = mView.findViewById(R.id.txtPostDescription);
        mDeviceName = mView.findViewById(R.id.txtPostDeviceName);
        mPrice = mView.findViewById(R.id.txtPostPrice);
        mLocation = mView.findViewById(R.id.txtPostLocation);
        mImage = mView.findViewById(R.id.imgPostArtwork);
        mError = mView.findViewById(R.id.txtPostError);

        mRecord = mView.findViewById(R.id.btnRecord);
        mPlay = mView.findViewById(R.id.btnPlay);
        mPost = mView.findViewById(R.id.btnPost);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRecord();
            }
        });
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPlay();
            }
        });
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                try {
                    fileUri = data.getData();
                    mImage.setImageBitmap(new Compressor(getActivity())
                            .compressToBitmap(new File(Utils.getRealPathFromURI(getActivity(), fileUri))));
                    mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Log.d(TAG, "onActivityResult: " + getActivity().getContentResolver().getType(fileUri));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

    private void authenticate() {
        mError.setVisibility(View.GONE);
        int userId = SharedPrefManager.getInstance().getUser(getActivity()).getUserId();
        artwork = new Artwork(
                userId,
                mDeviceName.getText().toString(),
                mArtworkName.getText().toString(),
                mAuthor.getText().toString(),
                mDate.getText().toString(),
                mDescription.getText().toString(),
                mPrice.getText().toString(),
                mLocation.getText().toString());

        if (!Utils.isEmptyFields(artwork.getArtworkName(), artwork.getAuthor(), artwork.getDate(), artwork.getDescription(), artwork.getDeviceName(), artwork.getPrice(), artwork.getLocation())) {
            mError.setText(R.string.error_post_artwork);
            mError.setVisibility(View.VISIBLE);
        } else if (fileUri == null) {
            mError.setText(R.string.error_product_image);
            mError.setVisibility(View.VISIBLE);
        } else if (audioFileName == null) {
            mError.setText(R.string.error_audio);
            mError.setVisibility(View.VISIBLE);
        } else {
            Utils.hideKeyboard(getActivity());
            postProduct();
        }
    }

    private void postProduct() {
        pDialog = Utils.showProgressDialog(getActivity(), "Posting your product...");
        parseRequestBody();
        Api.getInstance().getServices().setArtwork(userIdBody, deviceNameBody, artworkNameBody, authorBody, dateBody, descriptionBody, priceBody, locationBody, fileBody, audioBody).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if (response.errorBody() != null)
                        throw new Exception(response.errorBody().string());
                    if (response.body().getError())
                        throw new Exception(response.body().getMessage());
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, pDialog);
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Utils.dismissProgressDialog(pDialog);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseRequestBody() {
        File imagePath = new File(Utils.getRealPathFromURI(getActivity(), fileUri));
        File audioPath = new File(audioFileName);
        try {
            artworkNameBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getArtworkName());
            authorBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getAuthor());
            dateBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getDate());
            descriptionBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getDescription());
            deviceNameBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getDeviceName());
            userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(artwork.getUserId()));
            priceBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getPrice());
            locationBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getLocation());
            fileBody = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(fileUri)), new Compressor(getActivity()).compressToFile(imagePath));
            audioBody = RequestBody.create(MediaType.parse("audio/mp3"), audioPath);
        } catch (Exception ex) {

        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    //region Recording

    public void onClickRecord() {
        onRecord(mStartRecording);
        if (mStartRecording) {
            mRecord.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorError));
            mRecord.setText("Stop Recording");
        } else {
            mRecord.setBackgroundResource(android.R.drawable.btn_default);
            mRecord.setText("Record again");
        }

        mStartRecording = !mStartRecording;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        audioFileName = getActivity().getExternalCacheDir().getAbsolutePath();
        audioFileName += "/artworkAudio.mp3";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(audioFileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;
    }

    //endregion

    //region Playing

    public void onClickPlay(){
        onPlay(mStartPlaying);
        if (mStartPlaying) {
            mPlay.setText("Stop playing");
        } else {
            mPlay.setText("Start playing");
        }
        mStartPlaying = !mStartPlaying;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            Log.d(TAG, "startPlaying: " + audioFileName);
            player.setDataSource(audioFileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    //endregion
}
