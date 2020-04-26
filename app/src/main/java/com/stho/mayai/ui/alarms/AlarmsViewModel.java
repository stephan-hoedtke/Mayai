package com.stho.mayai.ui.alarms;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Alarms;
import com.stho.mayai.IAlarms;
import com.stho.mayai.MainViewModel;
import com.stho.mayai.MayaiRepository;
import com.stho.mayai.Summary;

public class AlarmsViewModel extends AndroidViewModel {

    private MayaiRepository repository;
    private final MutableLiveData<Summary> summaryLiveData = new MutableLiveData<>();

    @SuppressWarnings("ConstantConditions")
    public static AlarmsViewModel build(Fragment fragment) {
        return new ViewModelProvider(fragment.getActivity()).get(AlarmsViewModel.class);
    }

    public AlarmsViewModel(@NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
        summaryLiveData.setValue(new Summary());
    }

    LiveData<Alarms> getAlarmsLD() { return repository.getAlarmsLD(); }
    LiveData<Summary> getSummaryLD() { return summaryLiveData; }

    IAlarms getAlarms() {
        return repository.getAlarms();
    }
    Summary getSummary() { return summaryLiveData.getValue(); }

    @SuppressWarnings("ConstantConditions")
    void updateSummary() {
        Summary summary = summaryLiveData.getValue();
        summary.update(repository.getAlarms().getCollection());
        summaryLiveData.postValue(summary);
    }
}