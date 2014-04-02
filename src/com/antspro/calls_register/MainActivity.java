package com.antspro.calls_register;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.antspro.calls_register.controllers.StatisticController;
import com.antspro.calls_register.model.Statistic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends ListActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calls);
        findViewById(R.id.btn_refresh_statistic).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatisticController controller = new StatisticController(this);
        StatisticController.initStatistics(this);
        ArrayList<Statistic> list = controller.getStatistics();
        if (list.size() == 0) {
            list = controller.getStatistics();
        }
        AllowedPhoneAdapter adapter = new AllowedPhoneAdapter(this,
                R.layout.statistic_list_item, list);
        this.setListAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_refresh_statistic:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//                StatisticController.initStatistics(MainActivity.this);
//                onResume();
                break;
        }
    }

    private class AllowedPhoneAdapter extends ArrayAdapter<Statistic> {

        Context context;
        int layoutResourceId;
        ArrayList<Statistic> data = null;

        public AllowedPhoneAdapter(Context context, int layoutResourceId,
                                   ArrayList<Statistic> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            AllowedPhoneHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context)
                        .getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new AllowedPhoneHolder();
                holder.tvCallsDuration = (TextView) row
                        .findViewById(R.id.tv_calls_duration);
                holder.tvCallsCount = (TextView) row
                        .findViewById(R.id.tv_calls_count);
                holder.tvDate = (TextView) row
                        .findViewById(R.id.tv_date);
                holder.tvSendToServerStatus = (TextView) row
                        .findViewById(R.id.tv_send_to_server_status);

                row.setTag(holder);
            } else {
                holder = (AllowedPhoneHolder) row.getTag();
            }

            Statistic statistic = data.get(position);
            holder.tvCallsDuration.setText(String.valueOf(statistic.getDuration()));
            holder.tvCallsCount.setText(String.valueOf(statistic.getCallsCount()));
            holder.tvDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(statistic.getDate()));
            if (statistic.isPostedToServer()) {
                holder.tvSendToServerStatus.setText("Отправлен");
                holder.tvSendToServerStatus.setTextColor(getResources().getColor(R.color.green));
            } else {
                holder.tvSendToServerStatus.setText("Не отправлен");
                holder.tvSendToServerStatus.setTextColor(getResources().getColor(R.color.red));
            }
            return row;
        }

        class AllowedPhoneHolder {
            TextView tvCallsCount;
            TextView tvCallsDuration;
            TextView tvDate;
            TextView tvSendToServerStatus;
        }

    }
}
