package com.stho.mayai.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.stho.mayai.Alarm;
import com.stho.mayai.Helpers;
import com.stho.mayai.R;
import com.stho.mayai.Settings;
import com.stho.mayai.ViewAnimation;
import com.stho.mayai.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private FragmentSettingsBinding binding;
    private ViewAnimation animation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = SettingsViewModel.build(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        binding.buttonDebug.setOnClickListener(view -> findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToNavigationDebug()));
        binding.buttonReset.setOnClickListener(view -> viewModel.reset());
        binding.buttonResetDefault.setOnClickListener(view -> viewModel.resetDefault());
        binding.buttonModifyEgg.setOnClickListener(view -> onEdit(Alarm.TYPE_EGG));
        binding.buttonModifyChampagne.setOnClickListener(view -> onEdit(Alarm.TYPE_CHAMPAGNE));
        binding.buttonModifyBread.setOnClickListener(view -> onEdit(Alarm.TYPE_BREAD));
        binding.buttonModifyPotatoes.setOnClickListener(view -> onEdit(Alarm.TYPE_POTATOES));
        binding.buttonModifyClock.setOnClickListener(view -> onEdit(Alarm.TYPE_CLOCK));
        binding.headlineFrame.setOnClickListener(view -> animation.hide());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getSettingsLD().observe(getViewLifecycleOwner(), this::onObserveSettings);
        viewModel.getIsModifiedLD().observe(getViewLifecycleOwner(), this::onObserveIsModified);
        viewModel.getIsDifferentFromDefaultLD().observe(getViewLifecycleOwner(), this::onObserveIsDifferentFromDefault);
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

    private void showExceptionSnackBar(final @NonNull String exception) {
        final View container = requireActivity().findViewById(R.id.container);
        final Context context = requireContext();
        final Snackbar snackbar = Snackbar.make(container, exception, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.secondaryDarkColor));
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.secondaryTextColor));
        snackbar.show();
    }

    private NavController findNavController() {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @SuppressLint("SetTextI18n")
    private void onObserveSettings(final @NonNull Settings settings) {
        binding.minutesEgg.setText(Helpers.getMinutesAsString(settings.getMinutesEgg()));
        binding.minutesChampagne.setText(Helpers.getMinutesAsString(settings.getMinutesChampagne()));
        binding.minutesBread.setText(Helpers.getMinutesAsString(settings.getMinutesBread()));
        binding.minutesPotatoes.setText(Helpers.getMinutesAsString(settings.getMinutesPotatoes()));
        binding.minutesClock.setText(Helpers.getMinutesAsString(settings.getMinutesClock()));
    }

    private void onObserveIsModified(final boolean isModified) {
        binding.buttonReset.setEnabled(isModified);
    }

    private void onObserveIsDifferentFromDefault(final boolean isDifferentFromDefault) {
        binding.buttonResetDefault.setEnabled(isDifferentFromDefault);
        binding.buttonResetDefault.setTextColor(isDifferentFromDefault ?
                ContextCompat.getColor(requireContext(), R.color.primaryTextColor) : Color.GRAY);
    }

    private void updateActionBar()    {
        final ActionBar actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getTitleString());
            actionBar.setSubtitle(null);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private @NonNull String getTitleString() {
        return getString(R.string.title_settings);
    }

    private void onEdit(final int type) {
        findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToNavigationSettingsDetails(type));
    }
}

