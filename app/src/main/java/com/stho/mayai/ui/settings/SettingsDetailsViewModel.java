package com.stho.mayai.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Helpers;
import com.stho.mayai.MayaiRepository;
import com.stho.mayai.Settings;

public class SettingsDetailsViewModel extends AndroidViewModel {

    private final int type;
    private final double originalMinutes;
    private final MutableLiveData<Integer> typeLiveData = new MutableLiveData<>();
    private final MayaiRepository repository;

    public static SettingsDetailsViewModel build(final @NonNull Fragment fragment, int type) {
        final @NonNull Application application = fragment.requireActivity().getApplication();
        SettingsDetailsViewModelFactory factory = new SettingsDetailsViewModelFactory(application, type);
        return new ViewModelProvider(fragment, factory).get(SettingsDetailsViewModel.class);
    }

    public SettingsDetailsViewModel(final @NonNull Application application, int type) {
        super(application);
        this.type = type;
        repository = MayaiRepository.getRepository(application.getBaseContext());
        originalMinutes = repository.getSettings().getMinutes(type);
        typeLiveData.setValue(type);
    }

    void save() {
        repository.save(getApplication().getApplicationContext());
    }

    void reset() {
        setMinutes(originalMinutes);
    }

    LiveData<Integer> getTypeLD() { return typeLiveData; }

    LiveData<Integer> getDefaultSecondsLD() {
        return Transformations.switchMap(typeLiveData,
                type -> Transformations.map(repository.getSettingsLD(),
                        settings -> Helpers.toSeconds(settings.getMinutes(type))));
    }

    LiveData<Float> getAngleLD() {
        return Transformations.map(getDefaultSecondsLD(), this::getAngle);
    }

    LiveData<Boolean> getIsModifiedLD() {
        return Transformations.map(repository.getSettingsLD(),
                settings -> Settings.areDifferent(settings.getMinutes(type), originalMinutes));
    }

    private float getAngle(final int seconds) {
        double angle = Helpers.normalizeAngle360(seconds / SECONDS_PER_DEGREE);
        return (float)angle;
    }

    void rotate(final double delta) {
        int seconds = Helpers.toSeconds(getMinutes()) + (int) (delta * SECONDS_PER_DEGREE);
        double minutes = Helpers.toMinutes(seconds);
        setMinutes(minutes);
    }

    private double getMinutes() {
        return repository.getSettings().getMinutes(type);
    }

    private void setMinutes(double minutes) {
        repository.getSettings().setMinutes(type, minutes);
        repository.touch();
    }

    private final static double SECONDS_PER_DEGREE = 10f; // 1 minute = 6 degrees
}

