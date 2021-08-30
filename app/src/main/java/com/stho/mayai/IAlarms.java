package com.stho.mayai;


import androidx.annotation.Nullable;

public interface IAlarms {
    int size();
    @Nullable
    Alarm get(int position);
}
