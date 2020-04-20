package com.stho.mayai.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.stho.mayai.Alarm;
import com.stho.mayai.MayaiAlarmManager;
import com.stho.mayai.MayaiNotificationManager;
import com.stho.mayai.MayaiNotificationService;
import com.stho.mayai.MayaiPlayer;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private FragmentSettingsBinding binding;
    private MayaiPlayer player;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = SettingsViewModel.build(this);
        player = MayaiPlayer.build(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        binding.buttonStartPlaying.setOnClickListener(v -> player.ring());
        binding.buttonStopPlaying.setOnClickListener(v -> player.silence());
        binding.buttonSendNotification.setOnClickListener(v -> {
            MayaiNotificationManager.build(getContext()).setCountdown(Alarm.createTest()).sendNotification();
            viewModel.update(getContext());
        });
        binding.buttonStartService.setOnClickListener(v -> {
            MayaiNotificationService.startAsForegroundService(getContext(), Alarm.createTest());
            viewModel.update(getContext());
        });
        binding.buttonStopService.setOnClickListener(v -> {
            MayaiNotificationService.stop(getContext());
            viewModel.update(getContext());
        });
        binding.buttonStopAlarm.setOnClickListener(v -> {
            MayaiWorker.build(getContext()).cancel();
            viewModel.update(getContext());
        });
        binding.buttonOpenAlarm.setOnClickListener(view -> viewModel.openAlarmFromClockInfo(getContext()));
        binding.buttonOpenChannelSettings.setOnClickListener(view -> MayaiNotificationManager.build(getContext()).openChannelSettings());
        binding.buttonShowLog.setOnClickListener(view -> Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                .navigate(SettingsFragmentDirections.actionNavigationSettingsToNavigationShowLog()));
        viewModel.getInfoLD().observe(getViewLifecycleOwner(), info -> binding.nextAlarm.setText(info));
        viewModel.getVersionLD().observe(getViewLifecycleOwner(), this::updateActionBar);
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null)
            player.silence();
    }

    @SuppressWarnings("ConstantConditions")
    private void updateActionBar(String version) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Settings");
        actionBar.setSubtitle(version);
        actionBar.setHomeButtonEnabled(true);
    }
}
