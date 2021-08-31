package com.stho.mayai.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.ISettings;
import com.stho.mayai.MayaiRepository;
import com.stho.mayai.Settings;

public class SettingsViewModel extends AndroidViewModel {

    private final MayaiRepository repository;
    private final ISettings defaultValues;
    private final ISettings originalValues;


    public static SettingsViewModel build(final @NonNull Fragment fragment) {
        return new ViewModelProvider(fragment).get(SettingsViewModel.class);
    }

    public SettingsViewModel(final @NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
        final Settings settings = repository.getSettings();
        originalValues = settings.clone();
        defaultValues = Settings.defaultValues();
    }

    LiveData<Settings> getSettingsLD() { return repository.getSettingsLD(); }

    LiveData<Boolean> getIsModifiedLD() {
        return Transformations.map(repository.getSettingsLD(), settings -> settings.areDifferent(originalValues));
    }

    LiveData<Boolean> getIsDifferentFromDefaultLD() {
        return Transformations.map(repository.getSettingsLD(), settings -> settings.areDifferent(defaultValues));
    }

    void resetDefault() {
        repository.getSettings().takeOver(defaultValues);
        repository.save(getApplication().getApplicationContext());
        repository.touch();
    }

    void reset() {
        repository.getSettings().takeOver(originalValues);
        repository.save(getApplication().getApplicationContext());
        repository.touch();
    }
}
