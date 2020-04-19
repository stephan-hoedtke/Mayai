package com.stho.mayai.ui.showlog;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Alarms;
import com.stho.mayai.LogEntry;
import com.stho.mayai.MayaiRepository;

import java.util.ArrayList;

public class ShowLogViewModel extends AndroidViewModel {

    private MayaiRepository repository;

    @SuppressWarnings("ConstantConditions")
    public static ShowLogViewModel build(Fragment fragment) {
        return new ViewModelProvider(fragment.getActivity()).get(ShowLogViewModel.class);
    }

    public ShowLogViewModel(@NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
    }

    ArrayList<LogEntry> getLog() {
        return repository.getLog();
    }

    void clearLog() {
        repository.clearLog();
    }
}