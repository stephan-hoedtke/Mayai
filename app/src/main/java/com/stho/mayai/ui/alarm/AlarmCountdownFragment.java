package com.stho.mayai.ui.alarm;

import android.animation.Animator;
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

    private AlarmViewModel viewModel;
    private FragmentAlarmCountdownBinding binding;
    private final Handler handler = new Handler();
    private ViewAnimation animation;
    private final Touch touch = new Touch(500);
    private final Touch autoDisappearRotary = new Touch(5000);

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
        binding = FragmentAlarmCountdownBinding.inflate(inflater, container, false);
        binding.image.setOnClickListener(view -> edit());
        binding.clock.setOnClickListener(view -> edit());
        binding.textViewRemainingTime.setOnClickListener(view -> edit());
        binding.buttonStopPlaying.setOnClickListener(view -> cancelAlarm());
        binding.buttonShowRotary.setOnClickListener(view -> onShowRotary());
        binding.rotary.setOnAngleChangedListener(delta -> {
            touch.touch();
            autoDisappearRotary.touch();
            viewModel.rotate(delta);
        });
        viewModel.getAlarmLD().observe(getViewLifecycleOwner(), this::onUpdateAlarm);
        viewModel.getStatusNameLD().observe(getViewLifecycleOwner(), this::onUpdateStatusName);
        viewModel.getRemainingSecondsLD().observe(getViewLifecycleOwner(), this::onUpdateRemainingSeconds);
        viewModel.setAlarm(Helpers.getAlarmFromFragmentArguments(this));
        viewModel.getAngleLD().observe(getViewLifecycleOwner(), this::setAngle);
        rotaryHide();
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
            Alarm alarm = viewModel.getAlarm();
            MayaiWorker.build(getContext()).scheduleAlarm(alarm);
        }
    }

    private void rotaryAutoDisappearWhenReady() {
        if (autoDisappearRotary.isReady()) {
            if (!viewModel.isPermanent()) {
                rotaryDisappear();
            }
        }
    }

    private void onUpdateAlarm(Alarm alarm) {
        if (alarm != null) {
            binding.setAlarm(alarm);
            binding.image.setImageResource(alarm.getIconId());

            if (alarm.getType() == Alarm.TYPE_CLOCK) {
                binding.clock.setAlarmTime(alarm.getTriggerTime());
                binding.clock.setVisibility(View.VISIBLE);
                binding.image.setVisibility(View.GONE);
            }
            else {
                binding.clock.setVisibility(View.GONE);
                binding.image.setVisibility(View.VISIBLE);
            }

            updateActionBar(alarm);
        }
    }

    private void onUpdateStatusName(String statusName) {
        binding.textViewStatusName.setText(statusName);
    }

    private void onUpdateRemainingSeconds(int remainingSeconds) {
        binding.textViewRemainingTime.setText(Helpers.getSecondsAsString(remainingSeconds));
    }

    private void setAngle(float angle) {
        binding.rotary.setAngle(angle);
    }

    private void cancelAlarm() {
        Alarm alarm = viewModel.getAlarm();
        MayaiWorker.build(getContext()).cancel(alarm);
        showCancelAlarmSnackBar();
        findNavController().navigateUp();
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    @SuppressWarnings("ConstantConditions")
    private void showCancelAlarmSnackBar() {
        View container = getActivity().findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(container, "Alarm was canceled.", Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(getContext(), R.color.secondaryDarkColor));
        snackbar.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryTextColor));
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

    @SuppressWarnings("ConstantConditions")
    private void updateActionBar(Alarm alarm) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(alarm.getName());
        actionBar.setSubtitle(alarm.getTriggerTimeAsString());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void rotaryHide() {
        binding.rotaryFrame.setVisibility(View.INVISIBLE);
        binding.image.setAlpha(1f);
        binding.clock.setAlpha(1f);
    }

    private void rotaryAppear() {
        if (!isRotaryVisible()) {
            final long duration = 333;
            binding.rotaryFrame.setVisibility(View.VISIBLE);
            binding.rotaryFrame.animate().alpha(1f).setDuration(duration).setListener(new AnimatorOnAnimationEndListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    binding.rotaryFrame.setAlpha(1f);
                }
            });
            binding.image.animate().alpha(0.7f).setDuration(duration);
            binding.clock.animate().alpha(0.7f).setDuration(duration);
            autoDisappearRotary.touch();
        }
    }

    private void rotaryDisappear() {
        if (isRotaryVisible()) {
            final long duration = 333;
            binding.rotaryFrame.animate().alpha(0f).setDuration(duration).setListener(new AnimatorOnAnimationEndListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    binding.rotaryFrame.setVisibility(View.INVISIBLE);
                }
            });
            binding.image.animate().alpha(1f).setDuration(duration);
            binding.clock.animate().alpha(1f).setDuration(duration);
        }
    }
}
