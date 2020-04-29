package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.databinding.FragmentUserDetailsUpdateBinding;
import com.example.androbank.session.Session;
import com.example.androbank.session.User;
import com.google.android.material.snackbar.Snackbar;


public class UserDetailsUpdate extends Fragment {

    private FragmentUserDetailsUpdateBinding binding;
    private View root;
    private Session session = Session.getSession();

    private final String USERNAME_REGEX = "^[A-Za-z0-9\\._]{3,}$";
    private final String NAME_REGEX = "^[A-Za-z]{2,}$";
    private final String EMAIL_REGEX = "^[A-Za-z0-9_.]{2,}@[A-Za-z0-9_]{2,}\\.[A-Za-z]{2,3}$";
    // First error flags for email, username, first name, last name, phone number
    private boolean[] errors = {true, true, true, true, true};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserDetailsUpdateBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        initializeErrorHandling();



        binding.email.getEditText().setText(session.user.getEmail());
        binding.username.getEditText().setText(session.user.getUsername());
        binding.first.getEditText().setText(session.user.getFirstName());
        binding.last.getEditText().setText(session.user.getLastName());
        binding.phone.getEditText().setText(session.user.getPhoneNumber());


        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUpdateButton();
            }
        });

        // Email field logic:
        binding.email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = binding.email.getEditText().getText().toString();
                if (email.matches(EMAIL_REGEX)) {
                    binding.email.setErrorEnabled(false);
                    errors[0] = true;
                } else {
                    binding.email.setError("Not correctly formatted email address.");
                    binding.email.setErrorEnabled(true);
                    errors[0] = false;
                }
                checkSaveButtonStatus();
            }
        });

        // Username field logic:
        binding.username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = binding.username.getEditText().getText().toString();
                if (username.matches(USERNAME_REGEX)) {
                    binding.username.setErrorEnabled(false);
                    errors[1] = true;
                } else {
                    binding.username.setError("Should be longer than 3 characters\nand contain only letters, numbers,\ndots and underscores");
                    binding.username.setErrorEnabled(true);
                    errors[1] = false;
                }
                checkSaveButtonStatus();
            }
        });

        // First name field logic:
        binding.first.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.first.getEditText().getText().toString().matches(NAME_REGEX)) {
                    binding.first.setErrorEnabled(false);
                    errors[2] = true;
                } else {
                    binding.first.setError("Should be filled and contain only letters");
                    binding.first.setErrorEnabled(true);
                    errors[2] = false;
                }
                checkSaveButtonStatus();
            }
        });

        // Last name field logic:
        binding.last.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.last.getEditText().getText().toString().matches(NAME_REGEX)) {
                    binding.last.setErrorEnabled(false);
                    errors[3] = true;
                } else {
                    binding.last.setError("Should be filled and contain only letters");
                    binding.last.setErrorEnabled(true);
                    errors[3] = false;
                }
                checkSaveButtonStatus();
            }

        });


        // Phone number field logic:
        binding.phone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.phone.getEditText().length() > 6) {
                    binding.phone.setErrorEnabled(false);
                    errors[4] = true;
                } else {
                    binding.phone.setError("Phone number is too short.");
                    binding.phone.setErrorEnabled(true);
                    errors[4] = false;
                }
                checkSaveButtonStatus();
            }
        });
        return root;
    }

    private void checkSaveButtonStatus() {
        //System.out.println(errors[0] + " " + errors[1] + " " + errors[2] + " " + errors[3] + " " + errors[4]);
        for (boolean error : errors) {
            if (!error) {
                binding.update.setEnabled(false);
                return;
            }
        }
        binding.update.setEnabled(true);
    }

    private void handleUpdateButton() {
        String username = binding.username.getEditText().getText().toString();
        String email = binding.email.getEditText().getText().toString();
        String first = binding.first.getEditText().getText().toString();
        String last = binding.last.getEditText().getText().toString();
        String phone = binding.phone.getEditText().getText().toString();

        session.user.updateUser(username, first, last, email, phone).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Snackbar.make(getView(), "User details were updated!", Snackbar.LENGTH_LONG).show();
                binding.update.setEnabled(false);
                System.out.println(user.getUsername() + " ; " + user.getFirstName() + " ; " + user.getLastName() + " ; " + user.getEmail() + " ; " + user.getPhoneNumber());
            }
        });

        //Navigation.findNavController(root).navigate(R.id.action_updateContact_to_userDetails);
    }
    private void initializeErrorHandling() {
        session.getLastErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(getView(), s, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
