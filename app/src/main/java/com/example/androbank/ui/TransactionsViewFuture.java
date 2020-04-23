package com.example.androbank.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentTransactionsViewFutureBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.FutureTransaction;
import com.example.androbank.session.Session;
import com.example.androbank.session.Transaction;

import java.util.ArrayList;


public class TransactionsViewFuture extends Fragment {
    private FragmentTransactionsViewFutureBinding binding;
    private View root;
    private Session session;

    private int accountsIndex = 0;
    private ArrayList<Account> accounts;
    private ArrayList<FutureTransaction> futureTransactions = new ArrayList<FutureTransaction>();
    private Transaction selectedItem;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionsViewFutureBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        session = Session.getSession();

        binding.transactionsDetails.setText("This will be populated with the information about currently selected upcoming transaction.");

        binding.transactionSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = (Transaction) adapterView.getSelectedItem();
                binding.transactionsDetails.setText(selectedItem.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo finish implementation.
                session.transactions.deleteFutureTransaction(selectedItem.getTransferId(), selectedItem.getFromAccount());
            }
        });

        return root;
    }

    /**
     * Recursively gets all the future transactions on all of the users accounts.
     */
    private void getTransactions () {
        if (accountsIndex < accounts.size() ) {
            Account account = accounts.get(accountsIndex);
            session.transactions.getFutureTransactions(account.getAccountId() ).observe(getViewLifecycleOwner(), new Observer<ArrayList<FutureTransaction>>() {
                @Override
                public void onChanged(ArrayList<FutureTransaction> transactions) {
                    futureTransactions.addAll(transactions);
                    accountsIndex++;
                    getTransactions();
                }
            });
        } else {
            System.out.println("Transactions were fetched, going to put them to the spinner!");
            renderFutureTransactions();
            return;

        }
    }

    /**
     *  Used for resetting the fragment's state every time fragment is loaded.
     */
    public void onResume() {
        super.onResume();
        accountsIndex = 0;
        futureTransactions.clear();
        accounts = session.accounts.getSessionAccounts();
        if (accounts.isEmpty()) {
            session.accounts.getAccountsList(false).observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
                @Override
                public void onChanged(ArrayList<Account> accounts) {
                    TransactionsViewFuture.this.accounts = accounts;
                    getTransactions();
                }
            });

        } else {
            getTransactions();
        }

    }


    /**
     * Populates the spinner with the contents of the futureTransactions list.
     */
    private void renderFutureTransactions() {
            ArrayAdapter<FutureTransaction> futureTransactionsAdapter = new ArrayAdapter<FutureTransaction>(getActivity(), android.R.layout.simple_spinner_item, futureTransactions);
            futureTransactionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.transactionSelectSpinner.setAdapter(futureTransactionsAdapter);
    }


}
