package com.example.tvlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class AppListAdapter extends ArrayAdapter<AppInfo> {

    private LayoutInflater inflater;

    public AppListAdapter(@NonNull Context context, List<AppInfo> apps) {
        super(context, 0, apps);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_grid_app, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.iv_grid_app_icon);
            holder.name = convertView.findViewById(R.id.tv_grid_app_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppInfo app = getItem(position);
        if (app != null) {
            holder.icon.setImageDrawable(app.icon);
            holder.name.setText(app.label);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView name;
    }
}
