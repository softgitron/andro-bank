package com.example.androbank.ui.roniTest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.androbank.R;
import com.example.androbank.connection.Response;
import com.example.androbank.databinding.FragmentRoniTestBinding;
import com.example.androbank.session.Session;
import com.example.androbank.session.User;

public class RoniTestFragment extends Fragment {

    private FragmentRoniTestBinding binding;
    private View root;
    private Session session = Session.getSession();

    private final String USERNAME_REGEX = "^[A-Za-z0-9\\._]{3,}$";
    private final String NAME_REGEX = "^[A-Za-z]{2,}$";
    private final String EMAIL_REGEX = "^[A-Za-z0-9_.]{2,}@[A-Za-z0-9_]{2,}\\.[A-Za-z]{2,3}$";
    // First error flags for username, first name, last name, email, phone number and password
    private boolean[] errors = {true, true, true, true, true, true};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRoniTestBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        initializeErrorHandling();

        binding.usernameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                String username = binding.usernameField.getText().toString();
                if (username.matches(USERNAME_REGEX)) {
                    binding.usernameError.setVisibility(View.GONE);
                    errors[0] = false;
                } else {
                    binding.usernameError.setText("Should be longer than 3 characters\nand contain only letters, numbers,\ndots and underscores");
                    binding.usernameError.setVisibility(View.VISIBLE);
                    errors[0] = true;
                }
                checkSaveButtonStatus();
            }
        });

        binding.firstNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                if (binding.firstNameField.getText().toString().matches(NAME_REGEX)) {
                    binding.firstNameError.setVisibility(View.GONE);
                    errors[1] = false;
                } else {
                    binding.firstNameError.setVisibility(View.VISIBLE);
                    errors[1] = true;
                }
            }
        });

        binding.lastNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                if (binding.lastNameField.getText().toString().matches(NAME_REGEX)) {
                    binding.lastNameError.setVisibility(View.GONE);
                    errors[2] = false;
                } else {
                    binding.lastNameError.setVisibility(View.VISIBLE);
                    errors[2] = true;
                }
            }
        });

        binding.emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                String email = binding.emailField.getText().toString();
                if (email.matches(EMAIL_REGEX)) {
                    binding.emailError.setVisibility(View.GONE);
                    errors[3] = false;
                } else {
                    binding.emailError.setText("Not correctly formatted email address.");
                    binding.emailError.setVisibility(View.VISIBLE);
                    errors[3] = true;
                }
                checkSaveButtonStatus();
            }
        });

        binding.phonenumberField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }
                if (binding.phonenumberField.length() > 6) {
                    binding.numberError.setVisibility(View.GONE);
                    errors[4] = false;
                } else {
                    binding.numberError.setText("Phone number is too short.");
                    binding.numberError.setVisibility(View.VISIBLE);
                    errors[4] = true;
                }
                checkSaveButtonStatus();
            }
        });

        View.OnFocusChangeListener passwordListener = new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus)  {
                if (hasFocus || binding.passwodAgainField.length() == 0) {
                    return;
                }
                passwordCheck();
            }};
        binding.passwordField.setOnFocusChangeListener(passwordListener);
        binding.passwodAgainField.setOnFocusChangeListener(passwordListener);
        binding.passwodAgainField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                passwordCheck();
                return false;
            }
        });

        binding.createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateUserButton();
            }
        });
        return root;
    }

    private void passwordCheck() {
        String password = binding.passwordField.getText().toString();
        if (password.equals(binding.passwodAgainField.getText().toString()) && password.length() >= 5) {
            binding.passwordMachView.setVisibility(View.INVISIBLE);
            errors[5] = false;
        } else {
            binding.passwordMachView.setVisibility(View.VISIBLE);
            errors[5] = true;
        }
        checkSaveButtonStatus();
    }

    private void checkSaveButtonStatus() {
        for (boolean error : errors) {
            if (error) {
                binding.createUserButton.setEnabled(false);
                return;
            }
        }
        binding.createUserButton.setEnabled(true);
    }

    private void handleCreateUserButton() {
        String username = binding.usernameField.getText().toString();
        String firstName = binding.firstNameField.getText().toString();
        String lastName = binding.lastNameField.getText().toString();
        String email = binding.emailField.getText().toString();
        String phoneNumber = binding.phonenumberField.getText().toString();
        String password = binding.passwordField.getText().toString();
        binding.accountCreationError.setVisibility(View.INVISIBLE);

        session.user.createUser("Deals", username, firstName, lastName, email, phoneNumber, password).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                binding.loadingIndicator.setVisibility(View.INVISIBLE);
                Navigation.findNavController(root).navigate(R.id.action_roni_test_to_fragment_roni_test_success);
            }
        });
        binding.loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void initializeErrorHandling() {
        session.getLastErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.accountCreationError.setVisibility(View.VISIBLE);
                binding.accountCreationError.setText(s);
            }
        });
    }
}
