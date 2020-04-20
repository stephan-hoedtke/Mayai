package com.stho.mayai.ui.alarm;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stho.mayai.Alarm;
import com.stho.mayai.Helpers;
import com.stho.mayai.MayaiAlarmManager;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.RotaryView;
import com.stho.mayai.Touch;
import com.stho.mayai.databinding.FragmentAlarmModifyBinding;

public class AlarmModifyFragment extends Fragment {

    private AlarmViewModel viewModel;
    private FragmentAlarmModifyBinding binding;
    private Handler handler = new Handler();
    private Touch touch = new Touch(300);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = AlarmViewModel.build(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_modify, container, false);
        binding.buttonStopPlaying.setOnClickListener(v -> stopAlarm());
        binding.rotary.setOnAngleChangedListener(angle -> {
            touch.touch();
            viewModel.rotate(angle);
        });
        viewModel.getAlarmLD().observe(getViewLifecycleOwner(), this::onUpdateAlarm);
        viewModel.getRemainingSecondsLD().observe(getViewLifecycleOwner(), this::onUpdateRemainingSeconds);
        viewModel.getAngleLD().observe(getViewLifecycleOwner(), this::setAngle);
        viewModel.setAlarm(Helpers.getAlarmFromFragmentArguments(this));
        return binding.getRoot();
    }

    private void onUpdateAlarm(Alarm alarm) {
        if (alarm != null) {
            binding.setAlarm(alarm);
            binding.image.setImageResource(alarm.getIconId());
            updateActionBar(alarm);
        }
    }

    private void setAngle(float angle) {
        binding.rotary.setAngle(angle);
    }

    @SuppressWarnings("ConstantConditions")
    private void updateActionBar(Alarm alarm) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(alarm.getName());
        actionBar.setSubtitle(alarm.getTriggerTimeAsString());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private static final int DELAY_MILLIS = 200;

    @Override
    public void onResume() {
        super.onResume();
        update();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                update();
                handler.postDelayed(this, DELAY_MILLIS);
            }
        }, DELAY_MILLIS);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @SuppressWarnings("ConstantConditions")
    private void update() {
        if (touch.isReady()) {
            Alarm alarm = viewModel.getAlarm();
            MayaiWorker.build(getContext()).scheduleAlarm(alarm);
        }
        viewModel.update();
    }

    private void onUpdateRemainingSeconds(int remainingSeconds) {
        binding.textViewRemainingTime.setText(Helpers.getSecondsAsString(remainingSeconds));
    }

    @SuppressWarnings("ConstantConditions")
    private void stopAlarm() {
        Alarm alarm = viewModel.getAlarm();
        MayaiWorker.build(getContext()).cancel(alarm);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack(R.id.navigation_home, false);
    }
}
