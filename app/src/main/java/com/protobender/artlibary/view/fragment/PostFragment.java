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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.io.InputStream;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFragment extends Fragment {

    //region Attributes
    View mView;
    EditText mArtworkName, mAuthor, mDate, mDescription, mDeviceName;
    ImageView mImage;
    TextView mError;
    ProgressDialog pDialog;

    RequestBody artworkNameBody, authorBody, dateBody, descriptionBody, deviceNameBody, userIdBody, fileBody;
    Uri fileUri;

    int PICK_IMAGE_REQUEST = 1;
    Artwork artwork;
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
        mImage = mView.findViewById(R.id.imgPostArtwork);
        mError = mView.findViewById(R.id.txtPostError);
        Button mPost = mView.findViewById(R.id.btnPost);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
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
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(fileUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    mImage.setImageBitmap(selectedImage);
                    mImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
                mArtworkName.getText().toString(),
                mAuthor.getText().toString(),
                mDate.getText().toString(),
                mDescription.getText().toString(),
                mDeviceName.getText().toString(),
                userId);

        if (!Utils.isEmptyFields(artwork.getArtworkName(), artwork.getAuthor(), artwork.getDate(), artwork.getDescription(), artwork.getDeviceName())) {
            mError.setText(R.string.error_post_artwork);
            mError.setVisibility(View.VISIBLE);
        } else if (fileUri == null) {
            mError.setText(R.string.error_product_image);
            mError.setVisibility(View.VISIBLE);
        } else {
            Utils.hideKeyboard(getActivity());
            postProduct();
        }
    }

    private void postProduct() {
        pDialog = Utils.showProgressDialog(getActivity(), "Posting your product...");
        parseRequestBody();
        Api.getInstance().getServices().setArtwork(artworkNameBody, authorBody, dateBody, descriptionBody, deviceNameBody, userIdBody, fileBody).enqueue(new Callback<Result>() {
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
        File filePath = new File(Utils.getRealPathFromURI(getActivity(), fileUri));
        try {
            artworkNameBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getArtworkName());
            authorBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getAuthor());
            dateBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getDate());
            descriptionBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getDescription());
            deviceNameBody = RequestBody.create(MediaType.parse("text/plain"), artwork.getDeviceName());
            userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(artwork.getUserId()));
            fileBody = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(fileUri)), new Compressor(getActivity()).compressToFile(filePath));
        } catch (Exception ex) {

        }
    }
}
