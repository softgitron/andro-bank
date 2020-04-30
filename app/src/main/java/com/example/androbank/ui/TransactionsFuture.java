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
import com.example.androbank.SpinnerAdapterFutureT;
import com.example.androbank.databinding.FragmentTransactionsFutureBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.FutureTransaction;
import com.example.androbank.session.Session;
import com.example.androbank.session.Transaction;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class TransactionsFuture extends Fragment {
    private FragmentTransactionsFutureBinding binding;
    private View root;
    private Session session;

    private int accountsIndex = 0;
    private ArrayList<Account> accounts;
    private ArrayList<FutureTransaction> futureTransactions = new ArrayList<FutureTransaction>();
    private Transaction selectedItem;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionsFutureBinding.inflate(inflater, container, false);
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

        binding.deleteFutureTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FutureTransaction futureTransaction = (FutureTransaction) selectedItem;
                session.transactions.deleteFutureTransaction(futureTransaction.getFutureTransferId(), futureTransaction.getFromAccountId() ).observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer returnCode) {
                        // If we get this far, generic error handling has handled any exceptions and we can tell the user that the transaction was deleted.
                        Snackbar.make(getView(), "Future transaction was deleted.", Snackbar.LENGTH_LONG).show();
                        // Resetting the state since transaction was deleted.
                        onResume();
                    }
                });
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
        // If accounts list is empty, it must be fetched from the server to get all the transactions on each account.
        if (accounts.isEmpty()) {
            session.accounts.getAccountsList(false).observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
                @Override
                public void onChanged(ArrayList<Account> accounts) {
                    TransactionsFuture.this.accounts = accounts;
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
            ArrayAdapter<FutureTransaction> futureTransactionsAdapter = new SpinnerAdapterFutureT(getActivity(), R.layout.spinner_custom_future_transactions, futureTransactions);
            futureTransactionsAdapter.setDropDownViewResource(R.layout.spinner_custom_future_transactions);
            binding.transactionSelectSpinner.setAdapter(futureTransactionsAdapter);
    }


}
