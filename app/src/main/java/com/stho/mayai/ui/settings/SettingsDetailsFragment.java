package com.stho.mayai.ui.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.stho.mayai.Alarm;
import com.stho.mayai.Helpers;
import com.stho.mayai.R;
import com.stho.mayai.Touch;
import com.stho.mayai.ViewAnimation;
import com.stho.mayai.databinding.FragmentSettingsDetailsBinding;

public class SettingsDetailsFragment extends Fragment {

    private ViewAnimation animation;
    private SettingsDetailsViewModel viewModel;
    private FragmentSettingsDetailsBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Touch touch = new Touch(500);

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = Helpers.getTypeFromFragmentArguments(this);
        viewModel = SettingsDetailsViewModel.build(this, type);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsDetailsBinding.inflate(inflater, container, false);
        binding.rotary.setOnAngleChangedListener(delta -> viewModel.rotate(delta));
        binding.headlineFrame.setOnClickListener(view -> animation.hide());
        binding.buttonReset.setOnClickListener(view -> viewModel.reset());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final @NonNull View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getDefaultSecondsLD().observe(getViewLifecycleOwner(), this::onObserveDefaultSeconds);
        viewModel.getAngleLD().observe(getViewLifecycleOwner(), this::onObserveAngle);
        viewModel.getTypeLD().observe(getViewLifecycleOwner(), this::onObserveType);
        viewModel.getIsModifiedLD().observe(getViewLifecycleOwner(), this::observeIsModified);
        animation = ViewAnimation.build(binding.headlineFrame);
        rotaryShow();
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareUpdateHandler();
    }

    private void prepareUpdateHandler() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                update();
                handler.postDelayed(this, FAST_DELAY_MILLIS);
            }
        }, INITIAL_DELAY_MILLIS);
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

    private void onObserveDefaultSeconds(final int defaultSeconds) {
        binding.textViewDefaultTime.setText(Helpers.getSecondsAsString(defaultSeconds));
    }

    private void onObserveAngle(final float angle) {
        binding.rotary.setAngle(angle);
    }

    private void onObserveType(final int type) {
        final int imageId = Alarm.getIconId(type);
        final int stringId = Alarm.getTypeStringId(type);
        final String typeString = getString(stringId);
        binding.image.setImageResource(imageId);
        updateActionBar(typeString);
    }

    private void observeIsModified(final boolean isModified) {
        binding.buttonReset.setEnabled(isModified);
    }

    private void update() {
        saveSettingsWhenReady();
    }

    private void saveSettingsWhenReady() {
        if (touch.isReady()) {
            viewModel.save();
        }
    }

    private @NonNull NavController findNavController() {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    private void updateActionBar(final @NonNull String typeString) {
        final ActionBar actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getTitleString(typeString));
            actionBar.setSubtitle(null);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private @NonNull String getTitleString(final @NonNull String typeString) {
        return getString(R.string.title_settings_params, typeString);
    }

    private void rotaryShow() {
        binding.rotaryFrame.setVisibility(View.VISIBLE);
        binding.rotaryFrame.setAlpha(1f);
        binding.image.setAlpha(0.7f);
    }

    private static final int FAST_DELAY_MILLIS = 200;
    private static final int INITIAL_DELAY_MILLIS = 100;
}

