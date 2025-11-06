package com.example.tvlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // 状态栏UI
    private ImageView ivWifiStatus;
    private ImageView ivUsbStatus;
    private TextView tvTimeStatus;
    private TextView tvDate;

    private Handler handler = new Handler();
    private Runnable timeRunnable;
    private StatusReceiver statusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        initViews();
        setupAppShortcuts();
        setupActionButtons();
        setupClickListeners();
        setupFocusListeners();
        startTimeUpdater();
    }

    private void initViews() {
        ivWifiStatus = findViewById(R.id.iv_wifi_status);
        ivUsbStatus = findViewById(R.id.iv_usb_status);
        tvTimeStatus = findViewById(R.id.tv_time_status);
        tvDate = findViewById(R.id.tv_date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(timeRunnable);
        registerStatusReceiver();
        updateWifiStatus();
        updateUsbStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timeRunnable);
        unregisterReceiver(statusReceiver);
    }

    private void registerStatusReceiver() {
        statusReceiver = new StatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(statusReceiver, filter);
    }

    private class StatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            if (action.startsWith("android.net.wifi")) {
                updateWifiStatus();
            } else if (action.startsWith("android.hardware.usb")) {
                updateUsbStatus();
            }
        }
    }

    private void updateWifiStatus() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            int rssi = wifiManager.getConnectionInfo().getRssi();
            int level = WifiManager.calculateSignalLevel(rssi, 5);
            int[] wifiIcons = {R.drawable.ic_wifi_0, R.drawable.ic_wifi_1, R.drawable.ic_wifi_2, R.drawable.ic_wifi_3, R.drawable.ic_wifi_4};
            ivWifiStatus.setImageResource(wifiIcons[level]);
        }
    }

    private void updateUsbStatus() {
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        ivUsbStatus.setVisibility(usbManager.getDeviceList().isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void startTimeUpdater() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                tvTimeStatus.setText(new SimpleDateFormat("h:mm a", Locale.US).format(now));
                tvDate.setText(new SimpleDateFormat("EEEE, MMMM d", Locale.US).format(now));
                handler.postDelayed(this, 60000);
            }
        };
    }

    private void setupAppShortcuts() {
        View shortcutNetflix = findViewById(R.id.shortcut_netflix);
        ((ImageView) shortcutNetflix.findViewById(R.id.iv_app_icon)).setImageResource(R.drawable.ic_netflix);
        ((TextView) shortcutNetflix.findViewById(R.id.tv_app_name)).setText("NETFLIX");
        shortcutNetflix.setBackgroundResource(R.drawable.bg_netflix_shortcut); // 设置专用背景

        View shortcutYoutube = findViewById(R.id.shortcut_youtube);
        ((ImageView) shortcutYoutube.findViewById(R.id.iv_app_icon)).setImageResource(R.drawable.ic_youtube);
        ((TextView) shortcutYoutube.findViewById(R.id.tv_app_name)).setText("YouTube");
        shortcutYoutube.setBackgroundResource(R.drawable.bg_youtube_shortcut);

        View shortcutGooglePlay = findViewById(R.id.shortcut_google_play);
        ((ImageView) shortcutGooglePlay.findViewById(R.id.iv_app_icon)).setImageResource(R.drawable.ic_google_play);
        ((TextView) shortcutGooglePlay.findViewById(R.id.tv_app_name)).setText("Google Play");
        shortcutGooglePlay.setBackgroundResource(R.drawable.bg_play_shortcut);

        View shortcutChrome = findViewById(R.id.shortcut_chrome);
        ((ImageView) shortcutChrome.findViewById(R.id.iv_app_icon)).setImageResource(R.drawable.ic_chrome);
        ((TextView) shortcutChrome.findViewById(R.id.tv_app_name)).setText("chrome");
        shortcutChrome.setBackgroundResource(R.drawable.bg_chrome_shortcut);
    }

    private void setupActionButtons() {
        View keystoneButton = findViewById(R.id.btn_mail);
        ((ImageView) keystoneButton.findViewById(R.id.iv_button_icon)).setImageResource(R.drawable.ic_mail);
        ((TextView) keystoneButton.findViewById(R.id.tv_button_name)).setText("Keystone");

        View miracastButton = findViewById(R.id.btn_miracast);
        ((ImageView) miracastButton.findViewById(R.id.iv_button_icon)).setImageResource(R.drawable.ic_miracast);
        ((TextView) miracastButton.findViewById(R.id.tv_button_name)).setText("Miracast");

        View signalSourceButton = findViewById(R.id.btn_signal_source);
        ((ImageView) signalSourceButton.findViewById(R.id.iv_button_icon)).setImageResource(R.drawable.ic_signal_source);
        ((TextView) signalSourceButton.findViewById(R.id.tv_button_name)).setText("Signal Source");

        View myAppsButton = findViewById(R.id.btn_my_apps);
        ((ImageView) myAppsButton.findViewById(R.id.iv_button_icon)).setImageResource(R.drawable.ic_my_apps);
        ((TextView) myAppsButton.findViewById(R.id.tv_button_name)).setText("My Apps");

        View settingsButton = findViewById(R.id.btn_settings);
        ((ImageView) settingsButton.findViewById(R.id.iv_button_icon)).setImageResource(R.drawable.ic_settings);
        ((TextView) settingsButton.findViewById(R.id.tv_button_name)).setText("Settings");
    }

    private void setupClickListeners() {
        findViewById(R.id.shortcut_netflix).setOnClickListener(v -> openApp("com.netflix.mediaclient"));
        findViewById(R.id.shortcut_youtube).setOnClickListener(v -> openApp("com.google.android.youtube.tv"));
        findViewById(R.id.shortcut_google_play).setOnClickListener(v -> openApp("com.android.vending"));
        findViewById(R.id.shortcut_chrome).setOnClickListener(v -> openApp("com.android.chrome"));
        findViewById(R.id.btn_my_apps).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MyAppsActivity.class)));
        findViewById(R.id.btn_settings).setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_SETTINGS)));
        findViewById(R.id.btn_mail).setOnClickListener(v -> Toast.makeText(this, "Message clicked", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btn_miracast).setOnClickListener(v -> Toast.makeText(this, "Miracast clicked", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btn_signal_source).setOnClickListener(v -> Toast.makeText(this, "Signal Source clicked", Toast.LENGTH_SHORT).show());
    }

    private void openApp(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "App not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFocusListeners() {
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            animateFocus(v, hasFocus ? 1.05f : 1.0f);
        };

        findViewById(R.id.shortcut_netflix).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.shortcut_youtube).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.shortcut_google_play).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.shortcut_chrome).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.btn_mail).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.btn_miracast).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.btn_signal_source).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.btn_my_apps).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.btn_settings).setOnFocusChangeListener(focusChangeListener);
    }

    private void animateFocus(View view, float scale) {
        view.animate().scaleX(scale).scaleY(scale).setDuration(150).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && timeRunnable != null) {
            handler.removeCallbacks(timeRunnable);
        }
    }
}