package com.antspro.calls_register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class SettingsActivity extends Activity {
    EditText etServerUrl;
    SharedPreferences sp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        sp = getSharedPreferences("com.exampe.calls_register", Context.MODE_PRIVATE);

        etServerUrl = (EditText) findViewById(R.id.et_server_url);
        etServerUrl.setText(sp.getString("server_url", "http://"));

        findViewById(R.id.btn_change_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username", "");
                editor.putString("password", "");
                editor.commit();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
            }
        });

        ToggleButton tbtn = (ToggleButton) findViewById(R.id.tbtn_sync_on_off);
        tbtn.setChecked(sp.getBoolean("sync_active", true));
        tbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("sync_active", b);
                editor.commit();
            }
        });
    }

    public void onPause() {
        super.onPause();
        String serverUrl = etServerUrl.getText().toString();
        if (serverUrl.equals("http://"))
            serverUrl = null;
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("server_url", serverUrl);
        editor.commit();
    }
}