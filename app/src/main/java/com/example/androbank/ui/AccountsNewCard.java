package com.example.androbank.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentAccountsNewCardBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Card;
import com.example.androbank.session.Session;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountsNewCard extends Fragment {

    private FragmentAccountsNewCardBinding binding;
    private View root;
    private Session session = Session.getSession();

    private float withdrawLimit = 0;
    private float spendingLimit = 0;
    private String areaLimit;
    private String myAccountIban;
    private ArrayList<Account> myAccounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountsNewCardBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        //Create Card Button
        binding.createCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(myAccountIban+" "+withdrawLimit+" "+spendingLimit+" "+areaLimit);
                Integer selectedAccountId = null;
                for (Account account : myAccounts) {
                    if (account.getIban().equals(myAccountIban)) {
                        selectedAccountId = account.getAccountId();
                        break;
                    }
                }
                if (selectedAccountId == null) {
                    System.out.println("Error parsing accountId");
                    return;
                }
                Integer wLimit = Math.round(withdrawLimit * 100);
                Integer sLimit = Math.round(spendingLimit * 100);
                System.out.println("Lets create card");
                session.cards.createCard(selectedAccountId, wLimit, sLimit, areaLimit).observe(getViewLifecycleOwner(), new Observer<Card>() {
                    @Override
                    public void onChanged(Card card) {

                        Snackbar.make(getView(), "New card created.", Snackbar.LENGTH_LONG).show();
                        binding.createCardButton.setEnabled(false);
                    }
                });
            }
        });

        // Area Limit Spinner Adapter
        String[] areaOptions = {"-- none --", "Europe", "Nordics & Estonia", "Finland"};
        List<String> areaList = new ArrayList<String>(Arrays.asList(areaOptions));
        ArrayAdapter<String> areaSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areaList);
        areaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.areaSpinner.setAdapter(areaSpinnerAdapter);
        // Area Limit
        binding.areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getSelectedItem().toString();
                areaLimit = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Accounts spinner
        session.accounts.getAccountsList(false).observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                ArrayList<String> accountStrings = new ArrayList<String>();
                myAccounts = accounts;
                for (Account account : accounts) {
                    String bal = String.format("%.2f",  (float) account.getBalance() / 100 );
                    accountStrings.add(account.getIban() + " - "+ bal + "€");
                }
                ArrayAdapter<String> accountadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountStrings);
                accountadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.accountSpinner.setAdapter(accountadapter);
                binding.accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedItem = adapterView.getSelectedItem().toString();
                        myAccountIban = selectedItem.split(" -")[0];
                        System.out.println(myAccountIban);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
        });

        //Spending limit
        binding.inputSpend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) spendingLimit = Float.parseFloat(editable.toString());
                else spendingLimit = 0;
            }
        });

        //Withdraw limit
        binding.inputWithdraw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) withdrawLimit = Float.parseFloat(editable.toString());
                else withdrawLimit = 0;
            }
        });

        return root;
    }
}
