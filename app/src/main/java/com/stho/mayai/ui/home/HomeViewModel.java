package com.stho.mayai.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Alarm;
import com.stho.mayai.Alarms;
import com.stho.mayai.MayaiRepository;

public class HomeViewModel extends AndroidViewModel {

    private MayaiRepository repository;

    @SuppressWarnings("ConstantConditions")
    public static HomeViewModel build(Fragment fragment) {
        return new ViewModelProvider(fragment.getActivity()).get(HomeViewModel.class);
    }

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
    }

    @NonNull Alarm getAlarm(@NonNull Alarm alarm) {
        return repository.getAlarm(alarm);
    }
}