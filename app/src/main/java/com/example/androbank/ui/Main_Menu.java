package com.example.androbank.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentMainMenuBinding;

public class Main_Menu extends Fragment {

    private FragmentMainMenuBinding binding;
    private View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.viewAccounts.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_main_Menu_to_accounts));

        binding.viewTransactions.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_main_Menu_to_transactions));

        binding.viewUser.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_main_Menu_to_userDetails));

        return root;
    }
}
