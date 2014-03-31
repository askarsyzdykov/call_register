package com.antspro.calls_register;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.antspro.calls_register.model.Statistic;
import com.antspro.calls_register.receivers.SyncReceiver;
import com.ubertesters.sdk.Ubertesters;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoginActivity extends Activity {
    String serverUrl;

    SharedPreferences sp;
    EditText etUsername, etPassword;
    static AlarmManager am;
    Intent intent1;
    PendingIntent pIntent1;
    String mUsername, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        sp = getSharedPreferences("com.exampe.calls_register", Context.MODE_PRIVATE);
        etUsername = (EditText) findViewById(R.id.txt_login);
        etPassword = (EditText) findViewById(R.id.txt_password);
        if (TextUtils.isEmpty(sp.getString("server_url", "")))
        {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("server_url", "http://162.243.52.247/");
            editor.commit();
        }
        // Нажатие кнопки Войти
        findViewById(R.id.btn_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsername = etUsername.getText().toString();
                mPassword = etPassword.getText().toString();
                serverUrl = sp.getString("server_url", "");
                if (TextUtils.isEmpty(serverUrl)) {
                    Toast.makeText(LoginActivity.this, "Не указан адрес сервера, измените настройки", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
                    Toast.makeText(LoginActivity.this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                    return;
                }
                new LoginAsyncTask(LoginActivity.this).execute();
            }
        });

        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
            }
        });

        etUsername.setText(sp.getString("username", ""));
        etPassword.setText(sp.getString("password", ""));

        if (!TextUtils.isEmpty(etUsername.getText().toString()) && !TextUtils.isEmpty(etPassword.getText().toString())) {
            findViewById(R.id.btn_enter).performClick();
        }
    }

    public class LoginAsyncTask extends AsyncTask<ArrayList<Statistic>, Void, Object> {
        private Context mContext;

        public LoginAsyncTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Exception){
                Toast.makeText(mContext, ((Exception)result).getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Boolean.valueOf(result.toString())) {
                Toast.makeText(mContext, "Неверная пара логин пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("username", mUsername);
            editor.putString("password", mPassword);
            editor.commit();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

            boolean alarmUp = (PendingIntent.getBroadcast(mContext, 0,
                    new Intent(mContext, SyncReceiver.class),
                    PendingIntent.FLAG_NO_CREATE) != null);

            if (!alarmUp)
            {
                am = (AlarmManager) getSystemService(ALARM_SERVICE);
                intent1 = new Intent(mContext, SyncReceiver.class);
                pIntent1 = PendingIntent.getBroadcast(mContext, 0, intent1, 0);
                Log.d("askarlog", "starting alarm manager");
                am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime()+10000, 1000 * 60 * 60, pIntent1);  // 24 hours = 86400000;
            } else {
                Log.d("askarlog", "Alarm is already active");
            }
        }

        @Override
        protected Object doInBackground(ArrayList<Statistic>... args) {
            try {
                return login(mUsername, mPassword);
            } catch (Exception e) {
                return e;
            }
        }
    }

    public boolean login(String username, String password) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(serverUrl + "/auth.json");
        StringEntity se;
        JSONObject json = new JSONObject();
        JSONObject retVal;
        try {
            json.put("login", username);
            json.put("password", password);
            se = new StringEntity(json.toString());
            httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            HttpResponse response = httpclient.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            retVal = new JSONObject(reader.readLine());
            return retVal.getBoolean("success");
        } catch (HttpHostConnectException e) {
            throw new Exception("Нет интернет-соединения");
        } catch (Exception e) {
            Log.e("askarlog", e.getMessage());
            return false;
        }
    }


}
