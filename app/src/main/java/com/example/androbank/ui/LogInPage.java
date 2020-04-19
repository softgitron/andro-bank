package com.example.androbank.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.example.androbank.MainActivity;
import com.example.androbank.R;
import com.example.androbank.databinding.FragmentLogInPageBinding;
import com.example.androbank.session.Bank;
import com.example.androbank.session.User;
import com.example.androbank.session.Session;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.getSystemService;

public class LogInPage extends Fragment {

    private FragmentLogInPageBinding binding;
    private View root;
    private Session session = Session.getSession();
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogInPageBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getContext();

        //Populate spinner
        populateBankList();
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        initializeErrorHandling();

        binding.startRoniTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // https://developer.android.com/guide/components/fragments#Transactions
                /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, new RoniTestFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_roni_test);
            }
        });
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                int bankId = binding.bankspinner.getSelectedItemPosition();
                session.user.login(bankId, email, password).observe(getViewLifecycleOwner(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        Navigation.findNavController(root).navigate(R.id.action_nav_home_to_main_Menu);
                    }
                });
        }
        });
        // Source: https://stackoverflow.com/questions/8063439/android-edittext-finished-typing-event
        binding.password.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                binding.login.performClick();
                                // How to close/hide keyboard https://stackoverflow.com/a/17789187
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );

        binding.createAccount.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_nav_home_to_createUser));



        return root;
    }

    private void populateBankList() {
        session.banks.getBanksList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Bank>>() {
            @Override
            public void onChanged(ArrayList<Bank> banks) {
                String[] items = new String[banks.size()];
                for (int i = 0; i < banks.size(); i++) {
                    items[i] = banks.get(i).name;
                }
                // https://stackoverflow.com/questions/5241660/how-to-add-items-to-a-spinner-in-android
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                binding.bankspinner.setAdapter(adapter);
            }
        });
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
