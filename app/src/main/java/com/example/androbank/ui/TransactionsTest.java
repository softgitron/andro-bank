package com.example.androbank.ui;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.androbank.databinding.FragmentTransactionsTestBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Session;
import com.example.androbank.session.Transaction;

import java.util.ArrayList;

public class TransactionsTest extends Fragment {


    private FragmentTransactionsTestBinding binding;
    private View root;
    private Session session = Session.getSession();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionsTestBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        binding.transactionsDisplay.setMovementMethod(new ScrollingMovementMethod());

        session.accounts.getAccountsList(true).observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                System.out.println("Accounts retrieved");
                initSpinner();
            }
        });



        return root;
    }

    public void initSpinner() {
        ArrayList<String> accountStrings = new ArrayList<String>();
        for (Account account : session.accounts.getSessionAccounts()) {
            String bal = String.format("%.2f",  (float) account.getBalance() / 100 );
            accountStrings.add(account.getIban() + " - "+ bal + "€");
        }
        ArrayAdapter<String> accountadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountStrings);
        accountadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.accountSelect.setAdapter(accountadapter);
        binding.accountSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getSelectedItem().toString();
                String selectedAccountIban = selectedItem.split(" -")[0];
                System.out.println(selectedAccountIban);
                session.transactions.getTransactions(session.accounts.findAccountIdByIban(selectedAccountIban)).observe(getViewLifecycleOwner(), new Observer<ArrayList<Transaction>>() {
                    @Override
                    public void onChanged(ArrayList<Transaction> transactions) {
                        System.out.println("transactions retrieved");
                        String displayString = "";
                        for (Transaction t: transactions) {
                            displayString = displayString + t.toString(selectedAccountIban) + "\n\n";
                        }
                        binding.transactionsDisplay.setText(displayString);
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}
