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
import com.example.androbank.session.Session;

public class Main_Menu extends Fragment {

    private FragmentMainMenuBinding binding;
    private View root;
    private Session session = Session.getSession();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.viewAccounts.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_main_Menu_to_accounts));

        binding.viewFutureTransactionsTest.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.TransactionsFuture));

        binding.viewUser.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_main_Menu_to_userDetails));

        binding.viewTransactionsTest.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.Transactions));

        return root;
    }
}
