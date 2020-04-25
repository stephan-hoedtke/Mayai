package com.stho.mayai.ui.settings;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.stho.mayai.Alarm;
import com.stho.mayai.MayaiAlarmManager;
import com.stho.mayai.MayaiNotificationManager;
import com.stho.mayai.MayaiNotificationService;
import com.stho.mayai.MayaiPlayer;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.Settings;
import com.stho.mayai.databinding.FragmentSettingsBinding;

import java.util.Locale;
import java.util.Set;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private FragmentSettingsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = SettingsViewModel.build(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        binding.buttonShowLog.setOnClickListener(view -> findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToNavigationShowLog()));
        binding.buttonDebug.setOnClickListener(view -> findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToNavigationDebug()));
        binding.buttonSave.setOnClickListener(view -> save());
        viewModel.getVersionLD().observe(getViewLifecycleOwner(), this::updateActionBar);
        viewModel.getSettingsLD().observe(getViewLifecycleOwner(), this::updateSettings);
        return binding.getRoot();
    }

    private void save() {
        try {
            Settings settings = viewModel.getSettings();
            settings.setMinutesEgg(parseDouble(binding.minutesEgg));
            settings.setMinutesChampagne(parseDouble(binding.minutesChampagne));
            settings.setMinutesBread(parseDouble(binding.minutesBread));
            settings.setMinutesPotatoes(parseDouble(binding.minutesPotatoes));
            settings.setMinutesClock(parseDouble(binding.minutesClock));
            viewModel.save();
        }
        catch (Exception ex) {
            showExceptionSnackBar(ex.getMessage());
        }
    }


    private double parseDouble(EditText view) {
        String str = view.getText().toString();
        double value = Double.parseDouble(str);
        if (value == 0) {
            throw new IllegalArgumentException("Invalid double: " + str);
        }
        return value;
    }

    @SuppressWarnings("ConstantConditions")
    private void showExceptionSnackBar(final String exception) {
        View container = getActivity().findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(container, exception, Snackbar.LENGTH_LONG);
        snackbar.show();
    }



    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    @SuppressLint("SetTextI18n")
    private void updateSettings(Settings settings) {
        binding.setSettings(settings);
        binding.minutesEgg.setText(Double.toString(settings.getMinutesEgg()));
        binding.minutesChampagne.setText(Double.toString(settings.getMinutesChampagne()));
        binding.minutesBread.setText(Double.toString(settings.getMinutesBread()));
        binding.minutesPotatoes.setText(Double.toString(settings.getMinutesPotatoes()));
        binding.minutesClock.setText(Double.toString(settings.getMinutesClock()));
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
