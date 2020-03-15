package com.example.androbank.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.startRoniTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // https://developer.android.com/guide/components/fragments#Transactions
                /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, new RoniTestFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                Navigation.findNavController(root).navigate(R.id.roni_test);
            }
        });

        return root;
    }
}
