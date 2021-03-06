package com.stho.mayai.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.BuildConfig;
import com.stho.mayai.MayaiRepository;
import com.stho.mayai.Settings;

import java.util.Set;

public class SettingsViewModel extends AndroidViewModel {

    private final MutableLiveData<String> versionLiveData = new MutableLiveData<>();
    private MayaiRepository repository;

    @SuppressWarnings("ConstantConditions")
    public static SettingsViewModel build(Fragment fragment) {
        return new ViewModelProvider(fragment.getActivity()).get(SettingsViewModel.class);
    }

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
        versionLiveData.setValue(BuildConfig.VERSION_NAME);
    }


    LiveData<String> getVersionLD() { return versionLiveData; }
    LiveData<Settings> getSettingsLD() { return repository.getSettingsLD(); }

    Settings getSettings() { return repository.getSettings(); }

    void save() {
        repository.save(getApplication().getApplicationContext());
    }
}
