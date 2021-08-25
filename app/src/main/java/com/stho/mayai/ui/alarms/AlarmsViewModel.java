package com.stho.mayai.ui.alarms;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Alarms;
import com.stho.mayai.IAlarms;
import com.stho.mayai.MayaiRepository;
import com.stho.mayai.Summary;

public class AlarmsViewModel extends AndroidViewModel {

    private final MayaiRepository repository;
    private final MutableLiveData<Summary> summaryLiveData = new MutableLiveData<>();

    public static AlarmsViewModel build(final @NonNull Fragment fragment) {
        return new ViewModelProvider(fragment.requireActivity()).get(AlarmsViewModel.class);
    }

    public AlarmsViewModel(final @NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
        summaryLiveData.setValue(new Summary());
    }

    LiveData<Alarms> getAlarmsLD() { return repository.getAlarmsLD(); }
    LiveData<Summary> getSummaryLD() { return summaryLiveData; }

    IAlarms getAlarms() {
        return repository.getAlarms();
    }

    void updateSummary() {
        final Summary summary = summaryLiveData.getValue();
        if (summary != null) {
            summary.update(repository.getAlarms().getCollection());
            summaryLiveData.postValue(summary);
        }
    }
}
