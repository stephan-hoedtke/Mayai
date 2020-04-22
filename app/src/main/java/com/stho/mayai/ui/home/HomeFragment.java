package com.stho.mayai.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.stho.mayai.Alarm;
import com.stho.mayai.MainViewModel;
import com.stho.mayai.MayaiAlarmManager;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = HomeViewModel.build(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.imageViewEgg.setOnLongClickListener(view -> {
            startCounter(Alarm.TYPE_EGG, "EGG", 7.2);
            return false;
        });
        binding.imageViewChampagne.setOnLongClickListener(view -> {
            startCounter(Alarm.TYPE_CHAMPAGNE, "CHAMPAGNE", 27);
            return false;
        });
        binding.imageViewClock.setOnLongClickListener(view -> {
            startCounter(Alarm.TYPE_CLOCK, "CLOCK", 3);
            return false;
        });
        binding.imageViewBread.setOnLongClickListener(view -> {
            startCounter(Alarm.TYPE_BREAD, "BREAD", 10);
            return false;
        });
        binding.imageViewPotatoes.setOnLongClickListener(view -> {
            startCounter(Alarm.TYPE_POTATOES, "POTATOES", 25);
            return false;
        });
        updateActionBar();
        return binding.getRoot();
    }

    private void startCounter(int key, String name, double durationInMinutes) {
        Alarm alarm = new Alarm(key, name, durationInMinutes);

        MayaiWorker.build(getContext()).scheduleAlarm(alarm);

        findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationAlarmCountdown()
                        .setAlarm(alarm.serialize()));
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
