package com.stho.mayai.ui.alarms;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Alarms;
import com.stho.mayai.IAlarms;
import com.stho.mayai.MainViewModel;
import com.stho.mayai.MayaiRepository;

public class AlarmsViewModel extends AndroidViewModel {

    private MayaiRepository repository;

    @SuppressWarnings("ConstantConditions")
    public static AlarmsViewModel build(Fragment fragment) {
        return new ViewModelProvider(fragment.getActivity()).get(AlarmsViewModel.class);
    }

    public AlarmsViewModel(@NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
    }

    LiveData<Alarms> getAlarmsLD() { return repository.getAlarmsLD(); }

    IAlarms getAlarms() {
        return repository.getAlarms();
    }
}