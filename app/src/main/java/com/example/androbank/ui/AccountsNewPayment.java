package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentAccountsNewPaymentBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Session;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AccountsNewPayment extends Fragment {

    private FragmentAccountsNewPaymentBinding binding;
    private View root;
    private String account;
    private float amount;
    private String receiver;
    private LocalDate duedate;
    private String option;
    private Session session = Session.getSession();
    private ArrayList<Account> myAccounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountsNewPaymentBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.dueDateInput.setText(LocalDate.now().toString());
        duedate = LocalDate.now();

        // Pay Button
        binding.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(account+" "+amount+" "+ receiver +" "+duedate+" "+option);
                makePayment();
                //TODO: Payment processing
                Navigation.findNavController(root).navigate(R.id.action_newPayment_to_accounts);
            }
        });

        // Accounts spinner
        session.accounts.getAccountsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                ArrayList<String> accountStrings = new ArrayList<String>();
                myAccounts = accounts;
                for (Account account : accounts) {
                    accountStrings.add(account.getIban());
                }
                ArrayAdapter<String> accountadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountStrings);
                accountadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.accountDropdown.setAdapter(accountadapter);
                binding.accountDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedItem = adapterView.getSelectedItem().toString();
                        account = selectedItem;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
        });
        //String[] accounts = {"Account 1", "Account 2", "Account 3"};
        //ArrayAdapter<CharSequence> accountsadapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, accounts);


        // Options spinner
        String[] options = {"Only once", "Every week", "Every month"};
        ArrayAdapter<CharSequence> optionsadapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, options);
        optionsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.recurringOptions.setAdapter(optionsadapter);
        binding.recurringOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getSelectedItem().toString();
                //System.out.println(selectedItem);
                option = selectedItem;
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
                amount = Float.parseFloat(editable.toString());
                //System.out.println(amount);
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
                receiver = editable.toString();
                //System.out.println(amount);
            }
        });

        //Switch
        binding.dueDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binding.dueDateInput.setEnabled(b);
                if (b = false){
                    binding.dueDateInput.setText(LocalDate.now().toString());
                }
            }
        });

        return root;
    }

    public void makePayment() {
        Integer fromAccount = null;
        for (Account ac : myAccounts) {
            if (ac.getIban() == account) {
                fromAccount = ac.getAccountId();
            }
        }
        session.transactions.makeTransaction(fromAccount, receiver, Math.round(amount*100)).observe(getViewLifecycleOwner(), new Observer<Account>() {
            @Override
            public void onChanged(Account account) {
                System.out.println("Payment made!");
            }
        });
    }
}
