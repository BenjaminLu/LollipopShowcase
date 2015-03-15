package com.mikepenz.lollipopshowcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.github.kevinsawicki.http.HttpRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActionBarActivity
{
    private EditText emailET;
    private EditText passwordET;
    private Button loginBtn;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        title = (TextView) findViewById(R.id.title);
        emailET = (EditText) findViewById(R.id.email);
        passwordET = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new LoginAsyncTask().execute();
            }
        });
        Animation titleAnimation = AnimationUtils.loadAnimation(this,R.anim.login_title_anim);
        Animation controlAnimation = AnimationUtils.loadAnimation(this,R.anim.loggin_control_anim);
        emailET.setAnimation(controlAnimation);
        passwordET.setAnimation(controlAnimation);
        loginBtn.setAnimation(controlAnimation);
        title.startAnimation(titleAnimation);
    }

    class LoginAsyncTask extends AsyncTask<Void, Void, Boolean>
    {
        JSONObject responseJson;
        @Override
        protected Boolean doInBackground(Void... params)
        {
            boolean isSuccessful = false;
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();

            if(email.equals("") || password.equals("")) {
                return false;
            }

            Map<String, String> data = new HashMap<String, String>();
            data.put("email", email);
            data.put("password", password);
            HttpRequest request = HttpRequest.post(getString(R.string.server_login_url)).form(data);
            if (request.ok()) {
                String response = request.body();
                Log.i("response",response);
                try {
                    responseJson = new JSONObject(response);
                    isSuccessful = responseJson.getBoolean("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful)
        {
            if (isSuccessful.booleanValue()) {
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
            } else {
                try {
                    if(responseJson == null) {
                        return;
                    }
                    String message = responseJson.getString("error_msg");
                    AlertDialog.Builder errorAlertDialog = new AlertDialog.Builder(LoginActivity.this);
                    errorAlertDialog.setTitle("Error");
                    errorAlertDialog.setMessage(message);
                    DialogInterface.OnClickListener dialogOnClick = new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    };
                    errorAlertDialog.setNeutralButton("ok",dialogOnClick);
                    errorAlertDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
