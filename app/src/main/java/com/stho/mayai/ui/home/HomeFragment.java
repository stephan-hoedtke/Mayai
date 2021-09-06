package com.stho.mayai.ui.home;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.Summary;
import com.stho.mayai.ViewAnimation;
import com.stho.mayai.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ViewAnimation animation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = HomeViewModel.build(this);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.imageViewEgg.setOnClickListener(view -> startCounter(Alarm.TYPE_EGG));
        binding.imageViewChampagne.setOnClickListener(view -> startCounter(Alarm.TYPE_CHAMPAGNE));
        binding.imageViewBread.setOnClickListener(view -> startCounter(Alarm.TYPE_BREAD));
        binding.imageViewPotatoes.setOnClickListener(view -> startCounter(Alarm.TYPE_POTATOES));
        binding.imageViewClock.setOnClickListener(view -> startCounter(Alarm.TYPE_CLOCK));
        binding.imageViewCake.setOnClickListener(view -> startCounter(Alarm.TYPE_CAKE));
        binding.imageViewEgg.setOnLongClickListener(view -> { display(); return false; });
        binding.imageViewChampagne.setOnLongClickListener(view -> { display(); return false; });
        binding.imageViewBread.setOnLongClickListener(view -> { display(); return false; });
        binding.imageViewPotatoes.setOnLongClickListener(view -> { display(); return false; });
        binding.imageViewClock.setOnLongClickListener(view -> { display(); return false; });
        binding.imageViewCake.setOnLongClickListener(view -> { display(); return false; });
        binding.headlineFrame.setOnClickListener(view -> animation.hide());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getSummaryLD().observe(getViewLifecycleOwner(), this::updateUI);
        animation = ViewAnimation.build(binding.headlineFrame);
        updateActionBar();
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
                handler.postDelayed(this, DELAY_MILLIS);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_hint) {
            animation.toggle();
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        viewModel.updateSummary();
    }

    private void display() {
        findNavController().navigate(HomeFragmentDirections.actionGlobalNavigationAlarms());
    }

    private void startCounter(final int type) {
        final double minutes = viewModel.getSettings().getMinutes(type);
        startCounter(type, minutes);
    }

    private void startCounter(final int type, final double durationInMinutes) {
        final Alarm alarm = new Alarm(type, getString(Alarm.getTypeStringId(type)), durationInMinutes);
        MayaiWorker.build(requireContext()).scheduleAlarm(alarm);
        navigateToCountdownFragment(alarm);
        showScheduleAlarmSnackBar();
    }

    private void navigateToCountdownFragment(final @NonNull Alarm alarm) {
        findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationAlarmCountdown()
                        .setAlarm(alarm.serialize()));
    }

    private void updateUI(final @NonNull Summary summary) {
        updateUI(summary.getAlarmInfo(Alarm.TYPE_EGG), binding.infoCircleEgg, binding.infoCounterEgg);
        updateUI(summary.getAlarmInfo(Alarm.TYPE_CHAMPAGNE), binding.infoCircleChampagne, binding.infoCounterChampagne);
        updateUI(summary.getAlarmInfo(Alarm.TYPE_BREAD), binding.infoCircleBread, binding.infoCounterBread);
        updateUI(summary.getAlarmInfo(Alarm.TYPE_POTATOES), binding.infoCirclePotatoes, binding.infoCounterPotatoes);
        updateUI(summary.getAlarmInfo(Alarm.TYPE_CLOCK), binding.infoCircleClock, binding.infoCounterClock);
        updateUI(summary.getAlarmInfo(Alarm.TYPE_CAKE), binding.infoCircleCake, binding.infoCounterCake);

        if (summary.hasAlarm()) {
            binding.imageViewClock.setAlarmTime(summary.getTriggerTime());
        } else {
            binding.imageViewClock.removeAlarm();
        }
    }

    private void showScheduleAlarmSnackBar() {
        final View container = requireActivity().findViewById(R.id.container);
        final Context context = requireContext();
        final String text = getString(R.string.text_alarm_scheduled);
        final Snackbar snackbar = Snackbar.make(container, text, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.secondaryColor));
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.secondaryTextColor));
        snackbar.show();
    }

    private void updateUI(final @NonNull Summary.AlarmInfo info, final @NonNull ImageView infoCircle, final @NonNull TextView imageCounter) {
        if (info.getCounter() > 0) {
            infoCircle.setVisibility(View.VISIBLE);
            infoCircle.setImageResource(info.isHot() ? R.drawable.circle_red : R.drawable.circle_green);
            imageCounter.setVisibility(View.VISIBLE);
            imageCounter.setText(getCounterString(info.getCounter()));
        } else {
            infoCircle.setVisibility(View.INVISIBLE);
            imageCounter.setVisibility(View.INVISIBLE);
        }
    }

    private void updateActionBar() {
        final ActionBar actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getTitleString());
            actionBar.setSubtitle(null);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }
    }

    private @NonNull String getCounterString(final int counter) {
        return Integer.toString(counter);
    }

    private @NonNull String getTitleString() {
        return getString(R.string.title_mayai);
    }

    private @NonNull NavController findNavController() {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    private static final int DELAY_MILLIS = 500;
    private static final int INITIAL_DELAY_MILLIS = 100;
}
