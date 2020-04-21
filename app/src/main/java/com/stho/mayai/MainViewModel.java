package com.stho.mayai;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class MainViewModel extends AndroidViewModel {

    private final MayaiRepository repository;

    public static MainViewModel build(FragmentActivity activity) {
        return new ViewModelProvider(activity).get(MainViewModel.class);
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application);

    }

    boolean hasUnfinishedAlarms() {
        return repository.hasUnfinishedAlarms();
    }
}


