package com.stho.mayai.ui.settings;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.stho.mayai.Alarm;
import com.stho.mayai.R;
import com.stho.mayai.Settings;
import com.stho.mayai.databinding.FragmentSettingsBinding;

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

    private static class DialogInfo {
        int type;
        TextView view;

        DialogInfo(int type, TextView view) {
            this.type = type;
            this.view = view;
        }

        int getStringId() {
            return Alarm.getTypeStringId(type);
        }

        int getIconId() {
            return Alarm.getIconId(type);
        }
    }

    private static double parseDouble(TextView view) {
        String str = view.getText().toString();
        double value = Double.parseDouble(str);
        if (value == 0) {
            throw new IllegalArgumentException("Invalid double: " + str);
        }
        return value;
    }

    @SuppressLint("SetTextI18n")
    private void showEditDialog(int type, TextView view) {
        final DialogInfo info = new DialogInfo(type, view);
        showEditDialog(info);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("SetTextI18n")
    private void showEditDialog(DialogInfo info) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(info.getStringId());
        final EditText input = new EditText(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final int margin = (int)getResources().getDimension(R.dimen.activity_vertical_margin);
        input.setLayoutParams(lp);
        input.setText(info.view.getText());
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setPadding(margin, margin, margin, margin);
        alertDialog.setView(input);
        alertDialog.setIcon(info.getIconId());

        alertDialog.setPositiveButton("OK", (dialogInterface, i) -> {
            try {
                String str = input.getText().toString();
                double value = Double.parseDouble(str);
                if (value > 0) {
                    info.view.setText(str);
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
