package com.example.androbank.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.androbank.databinding.FragmentAccountsAddMoneyBinding;
import com.example.androbank.session.Session;


public class AccountsAddMoney extends Fragment {
    private  FragmentAccountsAddMoneyBinding binding;
    private View root;
    private float amount;
    private Session session;
    private String ibanWithBalance;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountsAddMoneyBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        session = Session.getSession();

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

        binding.addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String iban = ibanWithBalance.split(" - ")[0];
                System.out.println("ACOOUNT ID EXTRACTED FROM ACCOUNT INFO" + accountId);
                Integer accountdId = Session.getSession().accounts.findAccountIdByIban(iban);
                if (accountdId == null) {
                    System.err.println("Account was not found from the list");
                    System.exit(1);
                } else {
                    session..login(0, email, password).observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            Navigation.findNavController(root).navigate(R.id.action_nav_home_to_main_Menu);
                        }
                    }
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
        binding.addMoneyAccountString.setText(ibanWithBalance);
        System.out.println("ADD MONEY RECEIVED STRING: " + ibanWithBalance);

    }
}


