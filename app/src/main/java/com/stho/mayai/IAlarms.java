package com.stho.mayai;

import org.jetbrains.annotations.Nullable;

public interface IAlarms {
    int size();
    @Nullable Alarm get(int position);
}
