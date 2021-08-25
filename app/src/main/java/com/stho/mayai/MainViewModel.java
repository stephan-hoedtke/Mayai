package com.stho.mayai;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModel extends AndroidViewModel {

    private final MayaiRepository repository;

    public static MainViewModel build(final @NonNull FragmentActivity activity) {
        return new ViewModelProvider(activity).get(MainViewModel.class);
    }

    public MainViewModel(final @NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application);

    }

    boolean hasUnfinishedAlarms() {
        return repository.hasUnfinishedAlarms();
    }
}


