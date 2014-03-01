package com.antspro.calls_register;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.antspro.calls_register.controllers.StatisticController;
import com.antspro.calls_register.receivers.SyncReceiver;

public class LoginActivity extends Activity {
    final String SERVER_URL = "http://143ae3d2.ngrok.com/export.json";
    static final boolean DEBUG_MODE = true;

    SharedPreferences sp;
    EditText etPhoneNumber, etUsername, etPassword;
    static AlarmManager am;
    Intent intent1;
    PendingIntent pIntent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        sp = getSharedPreferences("com.exampe.calls_register", Context.MODE_PRIVATE);
        etUsername = (EditText)findViewById(R.id.txt_login);
        etPassword = (EditText)findViewById(R.id.txt_password);

        etUsername.setText(sp.getString("username", ""));
        etPassword.setText(sp.getString("password", ""));

        if (DEBUG_MODE){
            StatisticController.initStatistics(this);
            etUsername.setText("s");
            etPassword.setText("s");
        }
        //StatisticController.getStatistics(this, null);
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();
        etPhoneNumber = (EditText) findViewById(R.id.txt_phone_number);
        etPhoneNumber.setText(phoneNumber);

        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        intent1 = new Intent(this, SyncReceiver.class);
        pIntent1 = PendingIntent.getBroadcast(this, 0, intent1, 0);
        Log.d("ssp_reveiver", "start");
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 10000, pIntent1);  // 24 hours = 86400000

        // Нажатие кнопки Войти
        findViewById(R.id.btn_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.commit();

            }
        });
    }
}
