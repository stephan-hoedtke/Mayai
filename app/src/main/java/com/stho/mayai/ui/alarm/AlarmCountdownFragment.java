package com.stho.mayai.ui.alarm;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.stho.mayai.Alarm;
import com.stho.mayai.Helpers;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.ViewAnimation;
import com.stho.mayai.databinding.FragmentAlarmCountdownBinding;

public class AlarmCountdownFragment extends Fragment {

    private AlarmViewModel viewModel;
    private FragmentAlarmCountdownBinding binding;
    private Handler handler = new Handler();
    private ViewAnimation animation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = AlarmViewModel.build(this);
        setHasOptionsMenu(true);

        // see here to set the alarm again after reboot
        // https://developer.android.com/training/scheduling/alarms

        // see here:
        // the windows is kept active via the layout file
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_countdown, container, false);
        binding.image.setOnClickListener(view -> edit());
        binding.textViewRemainingTime.setOnClickListener(view -> edit());
        binding.buttonStopPlaying.setOnClickListener(v -> cancelAlarm());
        viewModel.getAlarmLD().observe(getViewLifecycleOwner(), this::onUpdateAlarm);
        viewModel.getStatusNameLD().observe(getViewLifecycleOwner(), this::onUpdateStatusName);
        viewModel.getRemainingSecondsLD().observe(getViewLifecycleOwner(), this::onUpdateRemainingSeconds);
        viewModel.setAlarm(Helpers.getAlarmFromFragmentArguments(this));
        return binding.getRoot();
    }

    private static final int DELAY_MILLIS = 200;

    @Override
    public void onResume() {
        super.onResume();
        update();
        prepareUpdateHandler();
        animation = ViewAnimation.build(binding.headlineFrame);
    }

    private void prepareUpdateHandler() {
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
        animation.removeCallbacks();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_hint) {
            animation.toggle();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    private void edit() {
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                .navigate(AlarmCountdownFragmentDirections.actionNavigationAlarmCountdownToNavigationAlarmModify()
                        .setAlarm(viewModel.getAlarm().serialize()));
    }

    private void update() {
        viewModel.update();
    }

    private void onUpdateAlarm(Alarm alarm) {
        if (alarm != null) {
            binding.setAlarm(alarm);
            binding.image.setImageResource(alarm.getIconId());
            updateActionBar(alarm);
        }
    }

    private void onUpdateStatusName(String statusName) {
        binding.textViewStatusName.setText(statusName);
    }

    private void onUpdateRemainingSeconds(int remainingSeconds) {
        binding.textViewRemainingTime.setText(Helpers.getSecondsAsString(remainingSeconds));
    }

    @SuppressWarnings("ConstantConditions")
    private void cancelAlarm() {
        Alarm alarm = viewModel.getAlarm();
        MayaiWorker.build(getContext()).cancel(alarm);
        showCancelAlarmSnackBar();
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
    }

    @SuppressWarnings("ConstantConditions")
    private void showCancelAlarmSnackBar() {
        View container = getActivity().findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(container, "Alarm was canceled.", Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(getContext(), R.color.secondaryDarkColor));
        snackbar.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryTextColor));
        snackbar.show();
    }

    @SuppressWarnings("ConstantConditions")
    private void updateActionBar(Alarm alarm) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(alarm.getName());
        actionBar.setSubtitle(alarm.getTriggerTimeAsString());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
}
