package com.stho.mayai.ui.debug;

import android.content.Context;
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

    private ViewAnimation animation;
    private DebugViewModel viewModel;
    private FragmentDebugBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = DebugViewModel.build(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context context = requireContext();
        binding = FragmentDebugBinding.inflate(inflater, container, false);
        binding.buttonStopPlaying.setOnClickListener(v -> {
            MayaiWorker.build(context).cancel();
            viewModel.update(context);
        });
        binding.buttonOpenAlarm.setOnClickListener(view -> viewModel.openAlarmFromClockInfo(context));
        binding.buttonOpenChannelSettings.setOnClickListener(view -> MayaiNotificationManager.openChannelSettings(context));
        binding.buttonShowLog.setOnClickListener(view -> findNavController().navigate(DebugFragmentDirections.actionNavigationDebugToNavigationShowLog()));
        binding.headlineFrame.setOnClickListener(view -> animation.hide());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getInfoLD().observe(getViewLifecycleOwner(), this::onObserveInfo);
        viewModel.getVersionLD().observe(getViewLifecycleOwner(), this::onObserveVersion);
        animation = ViewAnimation.build(binding.headlineFrame);
        updateActionBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        animation.removeCallbacks();
    }

    @Override
    public boolean onOptionsItemSelected(final @NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_hint) {
            animation.toggle();
        }
        return super.onOptionsItemSelected(item);
    }

    private NavController findNavController() {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    private void onObserveInfo(final @NonNull String info) {
        binding.nextAlarm.setText(info);
    }

    private void onObserveVersion(final @NonNull String version) {
        final String versionInfo = getString(R.string.label_version_params, version);
        binding.version.setText(versionInfo);
    }

    private void updateActionBar() {
        final ActionBar actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getTitleString());
            actionBar.setSubtitle(null);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private String getTitleString() {
        return getString(R.string.title_debug);
    }
}
