package com.stho.mayai.ui.alarms;

import android.os.Bundle;
import android.os.Handler;
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
import com.stho.mayai.Alarms;
import com.stho.mayai.R;
import com.stho.mayai.Summary;
import com.stho.mayai.databinding.FragmentAlarmsBinding;


public class AlarmsFragment extends Fragment {

    private AlarmsViewModel viewModel;
    private FragmentAlarmsBinding binding;
    private Handler handler = new Handler();

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
        viewModel.getAlarmsLD().observe(getViewLifecycleOwner(), this::updateAlarms);
        viewModel.getSummaryLD().observe(getViewLifecycleOwner(), this::updateSummary);
        return binding.getRoot();
    }

    private static final int DELAY_MILLIS = 1000;

    @Override
    public void onResume() {
        super.onResume();
        update();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                update();
                handler.postDelayed(this, DELAY_MILLIS);
            }
        }, DELAY_MILLIS);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private void updateAlarms(Alarms alarms) {
        update();
    }

    private void updateSummary(Summary summary) {
        updateActionBar(summary);
    }

    @SuppressWarnings("ConstantConditions")
    private void update() {
        viewModel.updateSummary();
        binding.list.getAdapter().notifyDataSetChanged();
    }

    @SuppressWarnings("ConstantConditions")
    private void updateActionBar(Summary summary) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        int counter = summary.getPendingAlarmsCounter();
        actionBar.setTitle(counter == 0 ? "Alarms" : ("Alarms: " + Integer.toString(counter)));
        actionBar.setSubtitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
}

