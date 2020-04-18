package com.example.androbank.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.androbank.databinding.FragmentAccountAddMoneyBinding;


public class AccountAddMoney extends Fragment {
    private FragmentAccountAddMoneyBinding binding;
    private View root;
    private float amount;
    private String ibanWithBalance;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountAddMoneyBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                amount = Float.parseFloat(editable.toString());
            }
        });
        return root;
    }

    /**
     * Used for getting the data from bundle which Accounts gives to this fragment.
     */
    public void onStart() {
        super.onStart();
        ibanWithBalance = getArguments().getString("accountData");
        System.out.println("ADD MONEY RECEIVED STRING: " + ibanWithBalance);

    }
}


