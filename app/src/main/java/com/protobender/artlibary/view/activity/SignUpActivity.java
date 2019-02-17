package com.protobender.artlibary.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
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
import com.protobender.artlibary.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    //region Attributes
    EditText mName, mEmail, mPassword, mConfirmPassword, mNumber, mAddress;
    ProgressDialog pDialog;
    TextView mError;
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mError = findViewById(R.id.txtSignUpError);
        mName = findViewById(R.id.txtSignUpName);
        mEmail = findViewById(R.id.txtSignUpEmail);
        mPassword = findViewById(R.id.txtSignUpPassword);
        mConfirmPassword = findViewById(R.id.txtSignUpConfirmPassword);
        mNumber = findViewById(R.id.txtSignUpMobileNumber);
        mAddress = findViewById(R.id.txtSignUpAddress);

        Button mSignUpButton = findViewById(R.id.btnSignUp);
        Button mSignInButton = findViewById(R.id.btnSignIn);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
        User user = new User(
                mName.getText().toString(),
                mEmail.getText().toString(),
                mPassword.getText().toString(),
                mNumber.getText().toString(),
                mAddress.getText().toString()
        );

        if (!Utils.isEmptyFields(user.getName(), user.getEmail(), user.getPassword(), mConfirmPassword.getText().toString(), user.getNumber(), user.getAddress())) {
            mError.setText(R.string.error_sign_up);
            mError.setVisibility(View.VISIBLE);
        } else if (!Utils.isEmailValid(user.getEmail())) {
            mError.setError(getString(R.string.error_invalid_email));
            mError.requestFocus();
        } else if (!Utils.isPasswordValid(user.getPassword()) || !user.getPassword().equals(mConfirmPassword.getText().toString())) {
            mPassword.setError(getString(R.string.error_invalid_password));
            mPassword.requestFocus();
        } else {
            Utils.hideKeyboard(this);
            fetchSignUp(user);
        }
    }

    private void fetchSignUp(User user) {
        pDialog = Utils.showProgressDialog(this, "Creating your account...");
        Api.getInstance().getServices().setUser(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getNumber(),
                user.getAddress()).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if(response.errorBody() != null)
                        throw new Exception(response.errorBody().string());
                    if(response.body().getError())
                        throw new Exception(response.body().getMessage());
                    signUp(response.body().getMessage());
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, pDialog);
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Utils.dismissProgressDialog(pDialog);
                Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signUp(String message) {
        finish();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
