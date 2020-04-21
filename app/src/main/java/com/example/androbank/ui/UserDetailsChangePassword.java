package com.example.androbank.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.R;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.example.androbank.databinding.FragmentUserDetailsChangePasswordBinding;
import com.example.androbank.session.Session;
import com.example.androbank.session.User;
import com.google.android.material.snackbar.Snackbar;

public class UserDetailsChangePassword extends Fragment {

    private FragmentUserDetailsChangePasswordBinding binding;
    private View root;
    private Session session = Session.getSession();
    private boolean passwordsMatch = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDetailsChangePasswordBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        initializeErrorHandling();
        binding.changePassword.setEnabled(passwordsMatch);

        binding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = session.user.getEmail();
                String password = binding.oldPassword.getEditText().getText().toString();
                int bankId = session.banks.getCurrentBank().getBankId();
                if (password.length() < 4) {
                    Snackbar.make(getView(), "Old password is not valid.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                session.user.login(bankId, email, password).observe(getViewLifecycleOwner(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        System.out.println("CHANGE PASSWORD: RIGHT OLD PASSWORD!");
                        updatePassword();
                        //Navigation.findNavController(root).navigate(R.id.action_nav_home_to_main_Menu);
                    }
                });
            }
        });

        binding.confirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed.
            }

            @Override
            public void afterTextChanged(Editable s) {
                String confirmPassword = s.toString();
                String newPassword = binding.newPassword.getEditText().getText().toString();
                if (confirmPassword.equals(newPassword)) {
                    System.out.println("PASSWORDS MATCH");
                    binding.confirmPassword.setErrorEnabled(false);
                    passwordsMatch = true;
                    binding.changePassword.setEnabled(passwordsMatch);
                } else {
                    binding.confirmPassword.setError("Passwords do not match");
                    binding.confirmPassword.setErrorEnabled(true);
                    passwordsMatch = false;
                }
            }
        });


        return root;
    }

    private void initializeErrorHandling() {
        session.getLastErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(getView(), s, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void updatePassword() {
        if(passwordsMatch) {
            String newPassword = binding.newPassword.getEditText().getText().toString();
            session.user.changePassword(newPassword).observe(getViewLifecycleOwner(), new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    Snackbar.make(getView(), "User details were updated!", Snackbar.LENGTH_LONG).show();
                    binding.changePassword.setEnabled(false);
                    System.out.println(user.getUsername() + " ; " + user.getFirstName() + " ; " + user.getLastName() + " ; " + user.getEmail() + " ; " + user.getPhoneNumber());
                }
            });
        }
    }

}


