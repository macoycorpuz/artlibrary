package com.protobender.artlibary.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.protobender.artlibary.AppController;
import com.protobender.artlibary.model.CenterRepo;
import com.protobender.artlibary.model.entity.Artwork;
import com.protobender.artlibary.view.fragment.AccountFragment;
import com.protobender.artlibary.view.fragment.ArtworkDetailsFragment;
import com.protobender.artlibary.view.fragment.ArtworksFragment;
import com.protobender.artlibary.view.fragment.HomeFragment;
import com.protobender.artlibary.view.fragment.PostFragment;

import java.util.List;

public class Utils {

    //region Initialize
    private static Utils utils;

    public static Utils getUtils() {

        if (null == utils) {
            utils = new Utils();
        }
        return utils;
    }
    //endregion

    //region UI Interaction
    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    public static void dismissProgressDialog(ProgressDialog pDialog) {
        if (pDialog != null) pDialog.dismiss();
    }

    public static void showProgress(final boolean show, final View progressView, final View goneForm) {
        int shortAnimTime = AppController.getInstance().resources.getInteger(android.R.integer.config_shortAnimTime);

        goneForm.setVisibility(show ? View.GONE : View.VISIBLE);
        goneForm.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                goneForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //endregion

    //region Authentication
    public static boolean isEmailValid(String email) {
        if (email == null) return false;
        return (!TextUtils.isEmpty(email)) && (email.contains("@"));
    }

    public static boolean isPasswordValid(String password) {
        return (!TextUtils.isEmpty(password)) && (password.length() > 8);
    }

    public static boolean isEmptyFields(String... fields) {
        for(String f : fields) {
            if(f.isEmpty()) return false;
        }
        return true;
    }
    //endregion

    //region Real Path
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        } catch (Exception ex) {
            result = ex.getMessage();
        }
        return result;
    }
    //endregion

    //region Api Util

    public static void handleError(String error, TextView mErrorView, ProgressDialog pDialog) {
        Utils.dismissProgressDialog(pDialog);
        mErrorView.setText(error);
        mErrorView.setVisibility(View.VISIBLE);
    }

    public static void handleError(String error, TextView mErrorView, View mProgress, View mRecyclerView) {
        Utils.showProgress(false, mProgress, mRecyclerView);
        mErrorView.setText(error);
        mErrorView.setVisibility(View.VISIBLE);
    }

    //endregion

    //region Fragment Util
    private static String CURRENT_TAG = null;
    private static int position = 0;

    public static void switchContent(FragmentActivity baseActivity, int id, String TAG) {

        Fragment fragment;
        FragmentManager fragmentManager = baseActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!TAG.equals(CURRENT_TAG)) {
            switch (TAG) {
                case Tags.HOME_FRAGMENT:
                    fragment = new HomeFragment();
                    break;
                case Tags.ARTWORKS_FRAGMENT:
                    fragment = new ArtworksFragment();
                    break;
                case Tags.POST_FRAGMENT:
                    fragment = new PostFragment();
                    break;
                case Tags.ACCOUNT_FRAGMENT:
                    fragment = new AccountFragment();
                    break;
                case Tags.ARTWORK_DETAILS_FRAGMENT:
                    fragment = new ArtworkDetailsFragment();
                    ((ArtworkDetailsFragment) fragment).setPosition(position);
                    break;
                default:
                    fragment = null;
                    break;
            }

            CURRENT_TAG = TAG;
            transaction.replace(id, fragment, TAG);
            transaction.commit();
        }
    }

    public static void setPosition(int position) {
        Utils.position = position;
    }

    //endregion
}
