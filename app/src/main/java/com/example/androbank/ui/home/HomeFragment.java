package com.example.androbank.ui.home;

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
import com.example.androbank.session.User;
import com.example.androbank.session.Session;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View root;
    private Session session = Session.getSession();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        //Populate spinner
        Spinner bank_spinner = binding.bankspinner;
        String[] temp_banks = {"Nordea", "OP", "Dank"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, temp_banks);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bank_spinner.setAdapter(adapter);

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
                String username = binding.username.getText().toString();
                String password = binding.password.getText().toString();

                session.user.login(username, password).observe(getViewLifecycleOwner(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        Navigation.findNavController(root).navigate(R.id.action_nav_home_to_main_Menu);
                    }
                });
        }
        });

        binding.createAccount.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_nav_home_to_roni_test));



        return root;
    }
}
