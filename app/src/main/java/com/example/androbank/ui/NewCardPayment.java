package com.example.androbank.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentNewCardPaymentBinding;


public class NewCardPayment extends Fragment {

    private FragmentNewCardPaymentBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewCardPaymentBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Payment processing
                Navigation.findNavController(root).navigate(R.id.action_newCardPayment2_to_accounts);
            }
        });

        return root;
    }
}
