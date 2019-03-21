package com.protobender.artlibary.view.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import com.protobender.artlibary.R;
import com.protobender.artlibary.util.SharedPrefManager;
import com.protobender.artlibary.util.Tags;
import com.protobender.artlibary.util.Utils;

public class HomeActivity  extends AppCompatActivity {

    String TAG = "Home Activity";
    BottomNavigationView navigation;
    MenuItem post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: Started");

        navigation = findViewById(R.id.navigation);
        post = navigation.getMenu().findItem(R.id.navigation_post);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        checkPermissions();
        showPost();
        Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.HOME_FRAGMENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void showPost() {
        boolean isLoggedIn = SharedPrefManager.getInstance().isLoggedIn(this);
        post.setVisible(isLoggedIn);
    }

    private void checkPermissions() {
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
        permissionCheck += this.checkSelfPermission("Manifest.permission.BLUETOOTH");
        permissionCheck += this.checkSelfPermission("Manifest.permission.BLUETOOTH_ADMIN");
        permissionCheck += this.checkSelfPermission("Manifest.permission.INTERNET");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_NETWORK_STATE");
        permissionCheck += this.checkSelfPermission("Manifest.permission.RECORD_AUDIO");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.RECORD_AUDIO}, 1001); //Any number
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }

    }

    //region Navigation Listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.HOME_FRAGMENT);
                    return true;
                case R.id.navigation_artworks:
                    Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.ARTWORKS_FRAGMENT);
                    return true;
                case R.id.navigation_post:
                    Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.POST_FRAGMENT);
                    return true;
                case R.id.navigation_account:
                    Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.ACCOUNT_FRAGMENT);
                    return true;
            }
            return false;
        }
    };
    //endregion
}
