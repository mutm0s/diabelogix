package com.example.diabetesdetector.adapters;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diabetesdetector.R;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private List<String> notifications;
    private LayoutInflater inflater;

    public NotificationAdapter(Context context, List<String> notifications) {
        this.context = context;
        this.notifications = notifications;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.notification_item, parent, false);
        }

        TextView notificationText = convertView.findViewById(R.id.notification_text);
        ImageView deleteNotification = convertView.findViewById(R.id.delete_notification);

        final String notification = notifications.get(position);
        notificationText.setText(notification);

        deleteNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifications.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}

