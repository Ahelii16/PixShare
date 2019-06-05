/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  Boolean signUpActive = true;
  TextView login;
  TextView username;
  TextView password;
  ImageView imageView;
  RelativeLayout layout;

  //onkeylistener is to login automatically when we click tick on keyboard. Also, keyboard should
  //disappear if we click on white space nearby. (set onclick listeners for them)

  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {

    if (i == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
      signup(view);
    }
    return false;
  }

  public void showUserList() {
    Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
    startActivity(intent);
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.login) {
      Button button = findViewById(R.id.button);
      if (signUpActive) {
        signUpActive = false;
        login.setText("or, Sign Up");
        button.setText("Login");
      }
      else {
        signUpActive = true;
        login.setText("or, Log In");
        button.setText("Sign Up");
      }
    } else if (view.getId() == R.id.imageView || view.getId() == R.id.layout) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
    }
  }

  public void signup(View view) {
    EditText username = findViewById(R.id.usernameEditText);
    EditText password = findViewById(R.id.passwordEditText);

    if (username.getText().toString().matches("") || password.getText().toString().matches("")) {
      Toast.makeText(getApplicationContext(), "Username and Password required!", Toast.LENGTH_LONG).show();
    }
    else {
      final ParseUser user = new ParseUser();
      if (signUpActive) {
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if(e == null) {
              Log.i("signup", "success");
              showUserList();
            }
            else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
      else {
        //login
        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser parseUser, ParseException e) {
            if (parseUser != null) {
              Log.i("login", "success");
              showUserList();
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("Photo Sharing App");

    username = findViewById(R.id.usernameEditText);
    password = findViewById(R.id.passwordEditText);
    password.setOnKeyListener(this);

    login = findViewById(R.id.login);
    login.setOnClickListener(this);

    ImageView imageView = findViewById(R.id.imageView);
    RelativeLayout layout = findViewById(R.id.layout);
    imageView.setOnClickListener(this);
    layout.setOnClickListener(this);

    if (ParseUser.getCurrentUser() != null) {
      showUserList();
    }
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}