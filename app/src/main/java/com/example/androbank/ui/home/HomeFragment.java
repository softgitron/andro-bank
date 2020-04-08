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
import androidx.navigation.Navigation;

import com.example.androbank.MainActivity;
import com.example.androbank.R;
import com.example.androbank.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View root;

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

                // TODO: Login functionality and checks
                System.out.println("Let's login");

                Navigation.findNavController(root).navigate((R.id.action_nav_home_to_main_Menu));
            }
        });

        binding.createAccount.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_nav_home_to_roni_test));



        return root;
    }
}
