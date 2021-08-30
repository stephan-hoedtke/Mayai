package com.stho.mayai.ui.alarm;

import android.animation.Animator;
import android.content.Context;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.stho.mayai.Alarm;
import com.stho.mayai.AnimatorOnAnimationEndListener;
import com.stho.mayai.Helpers;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.Touch;
import com.stho.mayai.ViewAnimation;
import com.stho.mayai.databinding.FragmentAlarmCountdownBinding;

public class AlarmCountdownFragment extends Fragment {

    private ViewAnimation animation;
    private AlarmViewModel viewModel;
    private FragmentAlarmCountdownBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Touch touch = new Touch(500);
    private final Touch autoDisappearRotary = new Touch(5000);

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = AlarmViewModel.build(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        binding = FragmentAlarmCountdownBinding.inflate(inflater, container, false);
        binding.image.setOnClickListener(view -> edit());
        binding.textViewRemainingTime.setOnClickListener(view -> edit());
        binding.headlineFrame.setOnClickListener(view -> animation.hide());
        binding.buttonStopPlaying.setOnClickListener(view -> cancelAlarm());
        binding.buttonShowRotary.setOnClickListener(view -> onShowRotary());
        binding.rotary.setOnAngleChangedListener(delta -> {
            touch.touch();
            autoDisappearRotary.touch();
            viewModel.rotate(delta);
        });
        rotaryHide();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final @NonNull View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getAlarmLD().observe(getViewLifecycleOwner(), this::onUpdateAlarm);
        viewModel.getStatusNameLD().observe(getViewLifecycleOwner(), this::onUpdateStatusName);
        viewModel.getRemainingSecondsLD().observe(getViewLifecycleOwner(), this::onUpdateRemainingSeconds);
        viewModel.setAlarm(Helpers.getAlarmFromFragmentArguments(this));
        viewModel.getAngleLD().observe(getViewLifecycleOwner(), this::setAngle);
        animation = ViewAnimation.build(binding.headlineFrame);
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
        handler.removeCallbacksAndMessages(null);
        animation.removeCallbacks();
    }

    @Override
    public boolean onOptionsItemSelected(final @NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_hint) {
            animation.toggle();
        }
        return super.onOptionsItemSelected(item);
    }

    private void edit() {
        rotaryAppear();
    }

    private void update() {
        scheduleAlarmWhenReady();
        rotaryAutoDisappearWhenReady();
        viewModel.update();
    }

    private void scheduleAlarmWhenReady() {
        if (touch.isReady()) {
            final Alarm alarm = viewModel.getAlarm();
            MayaiWorker.build(requireContext()).scheduleAlarm(alarm);
        }
    }

    private void rotaryAutoDisappearWhenReady() {
        if (autoDisappearRotary.isReady()) {
            if (!viewModel.isPermanent()) {
                rotaryDisappear();
            }
        }
    }

    private void onUpdateAlarm(final @Nullable Alarm alarm) {
        if (alarm != null) {
            binding.image.setAlarm(alarm);
            updateActionBar(alarm);
        }
    }

    private void onUpdateStatusName(final @NonNull String statusName) {
        binding.textViewStatusName.setText(statusName);
    }

    private void onUpdateRemainingSeconds(final int remainingSeconds) {
        binding.textViewRemainingTime.setText(Helpers.getSecondsAsString(remainingSeconds));
    }

    private void setAngle(final float angle) {
        binding.rotary.setAngle(angle);
    }

    private void cancelAlarm() {
        final Alarm alarm = viewModel.getAlarm();
        MayaiWorker.build(requireContext()).cancel(alarm);
        showCancelAlarmSnackBar();
        findNavController().navigateUp();
    }

    private @NonNull NavController findNavController() {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    private void showCancelAlarmSnackBar() {
        final View container = requireActivity().findViewById(R.id.container);
        final Context context = requireContext();
        final String text = getString(R.string.text_alarm_canceled);
        Snackbar snackbar = Snackbar.make(container, text, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.secondaryDarkColor));
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.secondaryTextColor));
        snackbar.show();
    }

    private boolean isRotaryVisible() {
        return (binding.rotaryFrame.getVisibility() == View.VISIBLE);
    }

    private void onShowRotary() {
        if (isRotaryVisible()) {
            viewModel.setPermanent(false);
            rotaryDisappear();
        }
        else {
            viewModel.setPermanent(true);
            rotaryAppear();
        }
    }

    private void updateActionBar(final @NonNull Alarm alarm) {
        final ActionBar actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(alarm.getName());
            actionBar.setSubtitle(alarm.getTriggerTimeAsString());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void rotaryHide() {
        binding.rotaryFrame.setVisibility(View.INVISIBLE);
        binding.image.setAlpha(1f);
    }

    private void rotaryAppear() {
        if (!isRotaryVisible()) {
            binding.rotaryFrame.setVisibility(View.VISIBLE);
            binding.rotaryFrame.animate().alpha(1f).setDuration(DURATION).setListener(new AnimatorOnAnimationEndListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    binding.rotaryFrame.setAlpha(1f);
                }
            });
            binding.image.animate().alpha(0.7f).setDuration(DURATION);
            autoDisappearRotary.touch();
        }
    }

    private void rotaryDisappear() {
        if (isRotaryVisible()) {
            binding.rotaryFrame.animate().alpha(0f).setDuration(DURATION).setListener(new AnimatorOnAnimationEndListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    binding.rotaryFrame.setVisibility(View.INVISIBLE);
                }
            });
            binding.image.animate().alpha(1f).setDuration(DURATION);
        }
    }

    private static final int DURATION = 333;
    private static final int FAST_DELAY_MILLIS = 200;
    private static final int INITIAL_DELAY_MILLIS = 100;
}

