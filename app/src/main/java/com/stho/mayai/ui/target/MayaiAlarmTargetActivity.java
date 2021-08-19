package com.stho.mayai.ui.target;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.stho.mayai.Alarm;
import com.stho.mayai.Helpers;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.databinding.ActivityAlarmTargetBinding;

public class MayaiAlarmTargetActivity extends AppCompatActivity {

    AlarmTargetViewModel viewModel;
    ActivityAlarmTargetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = AlarmTargetViewModel.build(this);
        viewModel.getAlarmLD().observe(this, this::setAlarm);
        onNewIntent(getIntent());
        turnScreenOnAndKeyguardOff();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_target);
        binding.image.setOnClickListener(view -> snooze());
        binding.buttonStopPlaying.setOnClickListener(view -> cancel());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        viewModel.setAlarm(Helpers.getAlarmFromIntent(intent));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            openMainActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAlarm(Alarm alarm) {
        binding.setAlarm(alarm);
        binding.image.setImageResource(alarm.getIconId());
        updateActionBar(alarm);
    }

    private void turnScreenOnAndKeyguardOff() {
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (keyguardManager != null) {
            keyguardManager.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {
            });
        }
    }

    private void snooze() {
        MayaiWorker.build(this).snooze(viewModel.getAlarm());
        finish();
    }

    private void cancel() {
        MayaiWorker.build(this).cancel(viewModel.getAlarm());
        finish();
    }

    private void openMainActivity() {
        MayaiWorker.build(this).openMainActivity();
        finish();
    }

    @SuppressWarnings("ConstantConditions")
    private void updateActionBar(Alarm alarm) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(alarm.getName());
        actionBar.setSubtitle(alarm.getTriggerTimeAsString());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
}

