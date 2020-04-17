package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentUpdateContactBinding;
import com.example.androbank.databinding.FragmentUserDetailsBinding;
import com.example.androbank.session.Session;


public class UpdateContact extends Fragment {

    private FragmentUpdateContactBinding binding;
    private View root;
    private Session session = Session.getSession();

    private final String USERNAME_REGEX = "^[A-Za-z0-9\\._]{3,}$";
    private final String NAME_REGEX = "^[A-Za-z]{2,}$";
    private final String EMAIL_REGEX = "^[A-Za-z0-9_.]{2,}@[A-Za-z0-9_]{2,}\\.[A-Za-z]{2,3}$";
    // First error flags for username, first name, last name, email, phone number and password
    private boolean[] errors = {true, true, true, true, true, true};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateContactBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        initializeErrorHandling();


        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUpdateButton();
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_updateContact_to_userDetails);
            }
        });
        binding.phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                if (binding.phone.length() > 6) {
                    binding.numberError2.setVisibility(View.GONE);
                    errors[4] = false;
                } else {
                    binding.numberError2.setText("Phone number is too short.");
                    binding.numberError2.setVisibility(View.VISIBLE);
                    errors[4] = true;
                }
                checkSaveButtonStatus();
            }
        });
        binding.first.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                if (binding.first.getText().toString().matches(NAME_REGEX)) {
                    binding.firstNameError2.setVisibility(View.GONE);
                    errors[1] = false;
                } else {
                    binding.firstNameError2.setVisibility(View.VISIBLE);
                    errors[1] = true;
                }
            }
        });

        binding.last.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                if (binding.last.getText().toString().matches(NAME_REGEX)) {
                    binding.lastNameError2.setVisibility(View.GONE);
                    errors[2] = false;
                } else {
                    binding.lastNameError2.setVisibility(View.VISIBLE);
                    errors[2] = true;
                }
            }
        });

        binding.email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                String email = binding.email.getText().toString();
                if (email.matches(EMAIL_REGEX)) {
                    binding.emailError2.setVisibility(View.GONE);
                    errors[3] = false;
                } else {
                    binding.emailError2.setText("Not correctly formatted email address.");
                    binding.emailError2.setVisibility(View.VISIBLE);
                    errors[3] = true;
                }
                checkSaveButtonStatus();
            }
        });
        binding.username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                String username = binding.username.getText().toString();
                if (username.matches(USERNAME_REGEX)) {
                    binding.usernameError2.setVisibility(View.GONE);
                    errors[0] = false;
                } else {
                    binding.usernameError2.setText("Should be longer than 3 characters\nand contain only letters, numbers,\ndots and underscores");
                    binding.usernameError2.setVisibility(View.VISIBLE);
                    errors[0] = true;
                }
                checkSaveButtonStatus();
            }
        });

        return root;
    }

    private void checkSaveButtonStatus() {
        for (boolean error : errors) {
            if (error) {
                binding.update.setEnabled(false);
                return;
            }
        }
        binding.update.setEnabled(true);
    }
    private void handleUpdateButton() {
        String username = binding.username.getText().toString();
        String email = binding.email.getText().toString();
        String first = binding.first.getText().toString();
        String last = binding.last.getText().toString();
        String phone = binding.phone.getText().toString();

        // TODO update logic

        Navigation.findNavController(root).navigate(R.id.action_updateContact_to_userDetails);
    }
    private void initializeErrorHandling() {
        session.getLastErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.accountUpdateError.setVisibility(View.VISIBLE);
                binding.accountUpdateError.setText(s);
            }
        });
    }
}
