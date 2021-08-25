package com.stho.mayai.ui.showlog;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.LogEntry;
import com.stho.mayai.Logger;

import java.util.ArrayList;

public class ShowLogViewModel extends AndroidViewModel {

    public static ShowLogViewModel build(final @NonNull Fragment fragment) {
        return new ViewModelProvider(fragment.requireActivity()).get(ShowLogViewModel.class);
    }

    public ShowLogViewModel(@NonNull Application application) {
        super(application);
    }

    ArrayList<LogEntry> getLog() {
        return Logger.getLog();
    }

    void clearLog() {
        Logger.clearLog();
    }
}