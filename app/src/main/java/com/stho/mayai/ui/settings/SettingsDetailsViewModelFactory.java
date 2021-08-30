package com.stho.mayai.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SettingsDetailsViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final @NonNull Application application;
    private final int type;

    public SettingsDetailsViewModelFactory(final @NonNull Application application, final int type) {
        super(application);
        this.application = application;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(SettingsDetailsViewModel.class)){
            SettingsDetailsViewModel viewModel = new SettingsDetailsViewModel(application, type);
            return (T) viewModel;
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}

