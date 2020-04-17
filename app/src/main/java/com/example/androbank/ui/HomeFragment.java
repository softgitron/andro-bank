package com.example.androbank.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.example.androbank.MainActivity;
import com.example.androbank.R;
import com.example.androbank.databinding.FragmentHomeBinding;
import com.example.androbank.session.Bank;
import com.example.androbank.session.User;
import com.example.androbank.session.Session;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View root;
    private Session session = Session.getSession();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

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

                session.user.login(0, email, password).observe(getViewLifecycleOwner(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        Navigation.findNavController(root).navigate(R.id.action_nav_home_to_main_Menu);
                    }
                });
        }
        });

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