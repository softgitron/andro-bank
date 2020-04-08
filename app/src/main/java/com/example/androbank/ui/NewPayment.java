package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentNewPaymentBinding;


public class NewPayment extends Fragment {

    private FragmentNewPaymentBinding binding;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewPaymentBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Payment processing
                Navigation.findNavController(root).navigate(R.id.action_newPayment_to_accounts);
            }
        });

        return root;
    }
}
