package com.example.androbank.ui.roniTest;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.androbank.databinding.FragmentRoniTestSuccessBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Session;
import com.example.androbank.session.User;


public class RoniTestFragmentSuccess extends Fragment {
    private FragmentRoniTestSuccessBinding binding;
    private View root;
    private Session session = Session.getSession();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRoniTestSuccessBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        initializeErrorHandling();

        binding.createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateAccountButton();
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginButton();
            }
        });
        return root;
    }

    private void handleCreateAccountButton() {
        binding.errorField.setVisibility(View.INVISIBLE);
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        session.accounts.createAccount().observe(getViewLifecycleOwner(), new Observer<Account>() {
            @Override
            public void onChanged(Account account) {
                binding.loadingIndicator.setVisibility(View.INVISIBLE);
                binding.accountIban.setText(account.getIban());
            }
        });
    }

    private void handleLoginButton() {
        String email = binding.emailField.getText().toString();
        String password = binding.passwordField.getText().toString();
        session.user.login(email, password).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user.getLoggedInStatus()) {
                    binding.loginStatus.setText("Your first name is: " + user.getFirstName());
                }
            }
        });
    }

    private void initializeErrorHandling() {
        session.getLastErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.loadingIndicator.setVisibility(View.INVISIBLE);
                binding.errorField.setVisibility(View.VISIBLE);
                binding.errorField.setText(s);
            }
        });
    }
}
