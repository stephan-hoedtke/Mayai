package com.stho.mayai.ui.showlog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stho.mayai.R;
import com.stho.mayai.databinding.FragmentShowLogBinding;


@SuppressWarnings("FieldCanBeLocal")
public class ShowLogFragment extends Fragment {

    private ShowLogViewModel viewModel;
    private FragmentShowLogBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ShowLogViewModel.build(this);
        setHasOptionsMenu(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        binding = FragmentShowLogBinding.inflate(inflater,container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));

        final ShowLogRecyclerViewAdapter adapter = new ShowLogRecyclerViewAdapter(viewModel.getLog());
        binding.list.setAdapter(adapter);
        binding.buttonDelete.setOnClickListener(view -> {
            viewModel.clearLog();
            adapter.notifyDataSetChanged();
        });

        updateActionBar();
        return binding.getRoot();
    }

    private void updateActionBar() {
        final ActionBar actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getTitleString());
            actionBar.setSubtitle(null);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private @NonNull String getTitleString() {
        return getString(R.string.title_log);
    }
}

