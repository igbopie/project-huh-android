package com.huhapp.android;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Setting;
import com.huhapp.android.util.PropertyAccessor;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class NotificationSettingsActivity extends ListActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private NotificationSettingsAdapter adapter;
    private ArrayList<Setting> adapterArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapterArrayList = new ArrayList<Setting>();
        adapter = new NotificationSettingsAdapter(this, adapterArrayList);
        setListAdapter(adapter);

        new GetSettings().execute();
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
    }

    public class NotificationSettingsAdapter extends ArrayAdapter<Setting> {
        public NotificationSettingsAdapter(Context context, ArrayList notificationSettings) {
            super(context, R.layout.notification_setting_list_item, notificationSettings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_setting_list_item, parent, false);
            }

            // Get the data item for this position
            final Setting item = getItem(position);
            TextView titleView = (TextView) convertView.findViewById(R.id.title);
            TextView descriptionView = (TextView) convertView.findViewById(R.id.description);
            final Switch switchView = (Switch) convertView.findViewById(R.id.settingSwitch);

            titleView.setText(item.getTitle());
            descriptionView.setText(item.getDescription());
            switchView.setChecked(item.getValue());
            switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setValue(isChecked);
                    new UpdateNotificationSettings(item).execute();
                }
            });

            return convertView;
        }
    }

    private class GetSettings extends AsyncTask<Void,Void, List<Setting>> {

        @Override
        protected List<Setting> doInBackground(Void... voids) {
            return Api.settingList();
        }

        @Override
        protected void onPostExecute(List<Setting>  result) {
            super.onPostExecute(result);
            adapterArrayList.clear();
            if (result != null) {
                adapterArrayList.addAll(result);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private class UpdateNotificationSettings  extends AsyncTask<Void,Void, Boolean> {
        private Setting setting;

        private UpdateNotificationSettings(Setting setting) {
            this.setting = setting;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return Api.updateSetting(setting.getName(), setting.getValue());
        }
    }
}