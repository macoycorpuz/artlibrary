package com.protobender.artlibary.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.protobender.artlibary.R;
import com.protobender.artlibary.domain.Api;
import com.protobender.artlibary.model.entity.Result;
import com.protobender.artlibary.model.entity.User;
import com.protobender.artlibary.util.SharedPrefManager;
import com.protobender.artlibary.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private TextView mError;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.txtEmail);
        mPassword = findViewById(R.id.txtPassword);
        mError = findViewById(R.id.txtLoginError);

        Button mLoginButton = findViewById(R.id.btnLogin);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });

        Button mSignUpButton = findViewById(R.id.btnRegister);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void clearViews() {
        mError.setVisibility(View.GONE);
        mEmail.setError(null);
        mPassword.setError(null);
    }

    private void authenticate() {
        clearViews();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (!Utils.getUtils().isEmptyFields(email, password)) {
            mError.setText(R.string.error_login);
            mError.setVisibility(View.VISIBLE);
        } else {
            Utils.hideKeyboard(this);
            fetchLogin(email, password);
        }
    }

    private void fetchLogin(String email, String password) {
        pDialog = Utils.showProgressDialog(this, "Logging in...");
        Api.getInstance().getServices().login(email, password).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if(!response.isSuccessful())
                        throw new Exception(response.errorBody().toString());
                    if(response.body().getError())
                        throw new Exception(response.body().getMessage());
                    login(response.body().getUser());
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, pDialog);
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.dismissProgressDialog(pDialog);
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login(User user) {
        SharedPrefManager.getInstance().userLogin(getApplicationContext(), user);
        finish();
        startActivity(new Intent(this, HomeActivity.class));
    }
}
