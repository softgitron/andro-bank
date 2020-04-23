package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.example.androbank.databinding.FragmentAccountsNewPaymentBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Session;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;


public class AccountsNewPayment extends Fragment {

    private FragmentAccountsNewPaymentBinding binding;
    private View root;
    private String fromAccountIban;
    private float amount;
    private String toAccountIban;
    private LocalDate dueDate;
    private int atInterval;
    private int paymentTimes;
    private Session session = Session.getSession();
    private ArrayList<Account> myAccounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountsNewPaymentBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.dueDateInput.setText(LocalDate.now().toString());
        binding.timesInput.setText("1");
        binding.timesInput.setEnabled(false);
        dueDate = LocalDate.now();
        paymentTimes = 1;

        // Pay Button
        binding.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(fromAccountIban + " " + amount + " " + toAccountIban + " " + dueDate + " " + atInterval);
                makePayment();
                //TODO: Payment processing
                //Navigation.findNavController(root).navigate(R.id.action_newPayment_to_accounts);
            }
        });

        // Accounts spinner
        session.accounts.getAccountsList(false).observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                ArrayList<String> accountStrings = new ArrayList<String>();
                myAccounts = accounts;
                for (Account account : accounts) {
                    String bal = String.format("%.2f", (float) account.getBalance() / 100);
                    accountStrings.add(account.getIban() + " - " + bal + "€");
                }
                ArrayAdapter<String> accountadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountStrings);
                accountadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.accountSpinner.setAdapter(accountadapter);
                binding.accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedItem = adapterView.getSelectedItem().toString();
                        fromAccountIban = selectedItem.split(" -")[0];
                        System.out.println(fromAccountIban);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
        });

        // Options spinner
        String[] options = {"Only once", "Every week", "Every month"};
        ArrayAdapter<CharSequence> optionsadapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, options);
        optionsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.recurringOptions.setAdapter(optionsadapter);
        binding.recurringOptions.setSelection(0);
        binding.recurringOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getSelectedItem().toString();
                //System.out.println(selectedItem);
                if (i == 0) {
                    binding.timesInput.setEnabled(false);
                    binding.timesInput.setText("1");
                    paymentTimes = 1;
                    atInterval = 0;
                } else if (i == 1) {
                    binding.timesInput.setEnabled(true);
                    atInterval = 7 * 24 * 60;
                } else if (i == 2) {
                    binding.timesInput.setEnabled(true);
                    atInterval = 30 * 24 * 60;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Amount
        binding.amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() != 0) amount = Float.parseFloat(editable.toString());
                //System.out.println(amount);
            }
        });

        //Payment Times
        binding.timesInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) paymentTimes = Integer.parseInt(editable.toString());
            }
        });

        // Receiver
        binding.receiverInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) toAccountIban = editable.toString();
                //System.out.println(amount);
            }
        });

        binding.dueDateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    LocalDate newDate = LocalDate.parse(editable.toString());
                    dueDate = newDate;
                    System.out.println("New date changed");
                } catch (DateTimeParseException ex) {

                }

            }
        });

        //Switch
        binding.dueDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binding.dueDateInput.setEnabled(b);
                if (b == false) {
                    binding.dueDateInput.setText(LocalDate.now().toString());
                    dueDate = LocalDate.now();
                    System.out.println("Reset duedate");
                }
            }
        });

        return root;
    }

    public void makePayment() {
        Integer fromAccountId = null;
        for (Account ac : myAccounts) {
            //System.out.println(fromAccountIban + ac.getIban());
            if (ac.getIban().equals(fromAccountIban)) {
                fromAccountId = ac.getAccountId();
                System.out.println(ac.getAccountId());
            }
        }
        if (fromAccountId != null) {
            if (binding.dueDateSwitch.isChecked() == false && atInterval == 0) {
                session.transactions.makeTransaction(fromAccountId, toAccountIban, Math.round(amount * 100)).observe(getViewLifecycleOwner(), new Observer<Account>() {
                    @Override
                    public void onChanged(Account account) {
                        Snackbar.make(getView(), "Payment made", Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                System.out.println(atInterval +" "+paymentTimes +" "+dueDate+" "+fromAccountId);
                session.transactions.makeFutureTransaction(fromAccountId, toAccountIban, Math.round(amount * 100), dueDate, atInterval, paymentTimes).observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        Snackbar.make(getView(), "Future payment made", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        } else
                Snackbar.make(getView(), "There was an error picking Account Id", Snackbar.LENGTH_LONG).show();
    }
}
