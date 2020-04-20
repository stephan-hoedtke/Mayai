package com.stho.mayai.ui.alarms;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stho.mayai.Alarm;
import com.stho.mayai.R;
import com.stho.mayai.databinding.FragmentAlarmsBinding;


public class AlarmsFragment extends Fragment {

    private AlarmsViewModel viewModel;
    private FragmentAlarmsBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = AlarmsViewModel.build(this);
    }

    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarms, container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        final AlarmsRecyclerViewAdapter adapter = new AlarmsRecyclerViewAdapter(getActivity(), viewModel.getAlarms());
        binding.list.setAdapter(adapter);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeAlarmToDelete(adapter));
        itemTouchHelper.attachToRecyclerView(binding.list);
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        binding.list.addItemDecoration(dividerItemDecoration);
        updateActionBar();
        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    private void updateActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Alarms");
        actionBar.setSubtitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
}

