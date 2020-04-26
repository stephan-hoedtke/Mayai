package com.stho.mayai.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Alarm;
import com.stho.mayai.Alarms;
import com.stho.mayai.MayaiRepository;
import com.stho.mayai.Settings;
import com.stho.mayai.Summary;

public class HomeViewModel extends AndroidViewModel {

    private MayaiRepository repository;
    private final MutableLiveData<Summary> summaryLiveData = new MutableLiveData<>();

    @SuppressWarnings("ConstantConditions")
    public static HomeViewModel build(Fragment fragment) {
        return new ViewModelProvider(fragment.getActivity()).get(HomeViewModel.class);
    }

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
        summaryLiveData.setValue(new Summary());
    }

    @NonNull
    Settings getSettings() { return repository.getSettings(); }

    LiveData<Summary> getSummaryLD() { return summaryLiveData; }

    @SuppressWarnings("ConstantConditions")
    void updateSummary() {
        Summary summary = summaryLiveData.getValue();
        summary.update(repository.getAlarms().getCollection());
        summaryLiveData.postValue(summary);
    }
}