package com.example.tvlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyAppsActivity extends AppCompatActivity {

    private GridView gridView;
    private List<AppInfo> appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_apps);

        gridView = findViewById(R.id.gv_apps);
        loadApps();

        AppListAdapter adapter = new AppListAdapter(this, appList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo app = appList.get(position);
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.packageName.toString());
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(MyAppsActivity.this, "Cannot open this app", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadApps() {
        appList = new ArrayList<>();
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        for (ResolveInfo ri : resolveInfos) {
            AppInfo app = new AppInfo();
            app.label = ri.loadLabel(pm);
            app.packageName = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(pm);
            appList.add(app);
        }
    }
}
