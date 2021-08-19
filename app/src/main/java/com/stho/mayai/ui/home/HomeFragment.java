package com.stho.mayai.ui.home;

import static com.stho.mayai.Alarm.TYPE_BREAD;
import static com.stho.mayai.Alarm.TYPE_CHAMPAGNE;
import static com.stho.mayai.Alarm.TYPE_CLOCK;
import static com.stho.mayai.Alarm.TYPE_EGG;
import static com.stho.mayai.Alarm.TYPE_POTATOES;

import android.annotation.SuppressLint;
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
        binding.imageViewEgg.setOnClickListener(view -> startCounter(TYPE_EGG,  viewModel.getSettings().getMinutesEgg()));
        binding.imageViewChampagne.setOnClickListener(view -> startCounter(Alarm.TYPE_CHAMPAGNE, viewModel.getSettings().getMinutesChampagne()));
        binding.imageViewBread.setOnClickListener(view -> startCounter(Alarm.TYPE_BREAD, viewModel.getSettings().getMinutesBread()));
        binding.imageViewPotatoes.setOnClickListener(view -> startCounter(Alarm.TYPE_POTATOES, viewModel.getSettings().getMinutesPotatoes()));
        binding.imageViewClock.setOnClickListener(view -> startCounter(Alarm.TYPE_CLOCK, viewModel.getSettings().getMinutesClock()));
        binding.imageViewEgg.setOnLongClickListener(view -> { display(); return false; });
        binding.imageViewChampagne.setOnLongClickListener(view -> { display(); return false; });
        binding.imageViewBread.setOnLongClickListener(view -> { display(); return false; });
        binding.imageViewPotatoes.setOnLongClickListener(view -> { display(); return false; });
        binding.imageViewClock.setOnLongClickListener(view -> { display(); return false; });
        viewModel.getSummaryLD().observe(getViewLifecycleOwner(), this::updateUI);
        updateActionBar();
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

    private void update() {
        viewModel.updateSummary();
    }

    private void display() {
        findNavController().navigate(HomeFragmentDirections.actionGlobalNavigationAlarms());
    }

    private void startCounter(int type, double durationInMinutes) {
        Alarm alarm = new Alarm(type, getString(Alarm.getTypeStringId(type)), durationInMinutes);
        MayaiWorker.build(getContext()).scheduleAlarm(alarm);

        findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationAlarmCountdown()
                        .setAlarm(alarm.serialize()));

        showScheduleAlarmSnackBar();
    }

    private void updateUI(Summary summary) {
        updateUI(summary.getAlarmInfo(TYPE_EGG), binding.infoCircleEgg, binding.infoCounterEgg);
        updateUI(summary.getAlarmInfo(TYPE_CHAMPAGNE), binding.infoCircleChampagne, binding.infoCounterChampagne);
        updateUI(summary.getAlarmInfo(TYPE_BREAD), binding.infoCircleBread, binding.infoCounterBread);
        updateUI(summary.getAlarmInfo(TYPE_POTATOES), binding.infoCirclePotatoes, binding.infoCounterPotatoes);
        updateUI(summary.getAlarmInfo(TYPE_CLOCK), binding.infoCircleClock, binding.infoCounterClock);
    }

    @SuppressWarnings("ConstantConditions")
    private void showScheduleAlarmSnackBar() {
        View container = getActivity().findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(container, "Alarm scheduled.", Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(getContext(), R.color.secondaryColor));
        snackbar.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryTextColor));
        snackbar.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(Summary.AlarmInfo i, ImageView infoCircle, TextView imageCounter) {
        if (i.getCounter() > 0) {
            infoCircle.setVisibility(View.VISIBLE);
            infoCircle.setImageResource(i.isHot() ? R.drawable.circle_red : R.drawable.circle_green);
            imageCounter.setVisibility(View.VISIBLE);
            imageCounter.setText(Integer.toString(i.getCounter()));
        }
        else {
            infoCircle.setVisibility(View.INVISIBLE);
            imageCounter.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void updateActionBar() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(getActivity().getTitle());
        actionBar.setSubtitle(null);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }

    @SuppressWarnings("ConstantConditions")
    private NavController findNavController() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }
}
