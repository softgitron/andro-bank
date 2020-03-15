package com.example.androbank.ui.roniTest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentRoniTestBinding;

public class RoniTestFragment extends Fragment {

    private FragmentRoniTestBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRoniTestBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        return root;
    }
}
