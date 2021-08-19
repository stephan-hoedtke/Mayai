package com.stho.mayai.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.stho.mayai.Alarm;
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
        binding.buttonSave.setOnClickListener(view -> save());
        binding.buttonModifyEgg.setOnClickListener(view -> showEditDialog(Alarm.TYPE_EGG, binding.minutesEgg));
        binding.buttonModifyChampagne.setOnClickListener(view -> showEditDialog(Alarm.TYPE_CHAMPAGNE, binding.minutesChampagne));
        binding.buttonModifyBread.setOnClickListener(view -> showEditDialog(Alarm.TYPE_BREAD, binding.minutesBread));
        binding.buttonModifyPotatoes.setOnClickListener(view -> showEditDialog(Alarm.TYPE_POTATOES, binding.minutesPotatoes));
        binding.buttonModifyClock.setOnClickListener(view -> showEditDialog(Alarm.TYPE_CLOCK, binding.minutesClock));
        viewModel.getVersionLD().observe(getViewLifecycleOwner(), this::updateActionBar);
        viewModel.getSettingsLD().observe(getViewLifecycleOwner(), this::updateSettings);
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

    private void save() {
        try {
            Settings settings = viewModel.getSettings();
            settings.setMinutesEgg(parseDouble(binding.minutesEgg));
            settings.setMinutesChampagne(parseDouble(binding.minutesChampagne));
            settings.setMinutesBread(parseDouble(binding.minutesBread));
            settings.setMinutesPotatoes(parseDouble(binding.minutesPotatoes));
            settings.setMinutesClock(parseDouble(binding.minutesClock));
            viewModel.save();
            findNavController().navigateUp();
        }
        catch (Exception ex) {
            showExceptionSnackBar(ex.getMessage());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void showExceptionSnackBar(final String exception) {
        View container = getActivity().findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(container, exception, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(getContext(), R.color.secondaryDarkColor));
        snackbar.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryTextColor));
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

    private static double parseDouble(TextView view) {
        String str = view.getText().toString();
        double value = Double.parseDouble(str);
        if (value == 0) {
            throw new IllegalArgumentException("Invalid double: " + str);
        }
        return value;
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("SetTextI18n")
    private void showEditDialog(final int type, final TextView view) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(Alarm.getTypeStringId(type));
        @SuppressLint("InflateParams") final View customLayout = getLayoutInflater().inflate(R.layout.edit_number_dialog, null);
        alertDialog.setView(customLayout);
        final EditText input = customLayout.findViewById(R.id.editView);
        input.setText(view.getText());
        input.requestFocus();
        alertDialog.setIcon(Alarm.getIconId(type));
        alertDialog.setPositiveButton("OK", (dialogInterface, i) -> {
            try {
                String str = input.getText().toString();
                double value = Double.parseDouble(str);
                if (value > 0) {
                    view.setText(str);
                    dialogInterface.cancel();
                }
            }
            catch(Exception ex) {
                // ignore, but don't close.
            }
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }
}
