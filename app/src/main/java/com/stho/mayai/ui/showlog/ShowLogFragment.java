package com.stho.mayai.ui.showlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stho.mayai.R;
import com.stho.mayai.databinding.FragmentShowLogBinding;

import org.jetbrains.annotations.NotNull;

public class ShowLogFragment extends Fragment {

    private ShowLogViewModel viewModel;
    private FragmentShowLogBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ShowLogViewModel.build(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_log, container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.list.setAdapter(new ShowLogRecyclerViewAdapter(viewModel.getLog()));
        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                viewModel.clearLog();
                binding.list.getAdapter().notifyDataSetChanged();
            }
        });
        updateActionBar();
        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    private void updateActionBar() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Activity Log");
        actionBar.setSubtitle(null);
        actionBar.setHomeButtonEnabled(true);
    }

}
