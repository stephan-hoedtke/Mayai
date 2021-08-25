package com.stho.mayai.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.MayaiRepository;
import com.stho.mayai.Settings;
import com.stho.mayai.Summary;

public class HomeViewModel extends AndroidViewModel {

    private final MayaiRepository repository;
    private final MutableLiveData<Summary> summaryLiveData = new MutableLiveData<>();

    public static HomeViewModel build(final @NonNull Fragment fragment) {
        return new ViewModelProvider(fragment.requireActivity()).get(HomeViewModel.class);
    }

    public HomeViewModel(final @NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
        summaryLiveData.setValue(new Summary());
    }

    @NonNull
    Settings getSettings() { return repository.getSettings(); }

    LiveData<Summary> getSummaryLD() { return summaryLiveData; }

    void updateSummary() {
        final Summary summary = summaryLiveData.getValue();
        if (summary != null) {
            summary.update(repository.getAlarms().getCollection());
            summaryLiveData.postValue(summary);
        }
    }
}