package com.stho.mayai.ui.debug;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.stho.mayai.MayaiNotificationManager;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.ViewAnimation;
import com.stho.mayai.databinding.FragmentDebugBinding;

public class DebugFragment extends Fragment {

    private DebugViewModel viewModel;
    private FragmentDebugBinding binding;
    private ViewAnimation animation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = DebugViewModel.build(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDebugBinding.inflate(inflater, container, false);
        binding.buttonStopPlaying.setOnClickListener(v -> {
            MayaiWorker.build(getContext()).cancel();
            viewModel.update(getContext());
        });
        binding.buttonOpenAlarm.setOnClickListener(view -> viewModel.openAlarmFromClockInfo(getContext()));
        binding.buttonOpenChannelSettings.setOnClickListener(view -> MayaiNotificationManager.openChannelSettings(getContext()));
        binding.buttonShowLog.setOnClickListener(view -> findNavController().navigate(DebugFragmentDirections.actionNavigationDebugToNavigationShowLog()));
        binding.headlineFrame.setVisibility(View.INVISIBLE);
        viewModel.getInfoLD().observe(getViewLifecycleOwner(), info -> binding.nextAlarm.setText(info));
        viewModel.getVersionLD().observe(getViewLifecycleOwner(), this::updateActionBar);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        animation = ViewAnimation.build(binding.headlineFrame);
    }

    @Override
    public void onPause() {
        super.onPause();
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
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
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
