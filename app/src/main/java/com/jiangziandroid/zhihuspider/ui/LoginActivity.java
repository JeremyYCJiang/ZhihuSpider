package com.jiangziandroid.zhihuspider.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.ZhihuSpiderApplication;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class LoginActivity extends Activity {
    public static final int SIGNUP_REQUEST = 1;

    @InjectView(R.id.SignUptTextView) TextView mSignUpTextView;
    @InjectView(R.id.LoginNameEditText) EditText mLoginNameEditText;
    @InjectView(R.id.LoginPwdEditText) EditText mLoginPwdEditText;
    @InjectView(R.id.LoginButton) Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.inject(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mLoginNameEditText.getText().toString();
                String password = mLoginPwdEditText.getText().toString();
                username.trim();
                password.trim();
                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle(getString(R.string.login_error_tittle))
                            .setMessage(getString(R.string.login_error_message))
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    //Login
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser != null) {
                                // Hooray! The user is logged in.
                                Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                                ZhihuSpiderApplication.updateParseInstallation(parseUser);
                                if (getIntent().getStringExtra("fromStory") != null) {
                                    Intent returnIntent = new Intent();
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } else {
                                // login failed. Look at the ParseException to see what happened.
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle(getString(R.string.login_error_tittle))
                                        .setMessage(e.getMessage())
                                        .setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }

            }
        });

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                if(getIntent().getStringExtra("fromStory") != null){
                    intent.putExtra("fromStory", "fromStory");
                    startActivityForResult(intent, SIGNUP_REQUEST);
                }
                else {
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Toast.makeText(this, "Successfully signUp and login!", Toast.LENGTH_LONG).show();
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        }
        else if(resultCode == RESULT_CANCELED){
            Toast.makeText(this, "User canceled signUp!", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Other Error!", Toast.LENGTH_LONG).show();
        }
    }
}
