package com.example.androbank.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.example.androbank.connection.Transfer;
import com.example.androbank.databinding.FragmentAccountsAddMoneyBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Session;
import com.google.android.material.snackbar.Snackbar;


public class AccountsAddMoney extends Fragment {
    private  FragmentAccountsAddMoneyBinding binding;
    private View root;
    private float amount;
    private Session session;
    private String ibanWithBalance;
    private Context context;
    private Account.AccountType accountType;
    private Account.AccountType newType;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountsAddMoneyBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getActivity();
        session = Session.getSession();

        binding.amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) amount = Float.parseFloat(editable.toString());
            }
        });

        binding.addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String iban = ibanWithBalance.split(" - ")[0];
                Integer accountdId = Session.getSession().accounts.findAccountIdByIban(iban);
                if (accountdId == null) {
                    System.err.println("Account was not found from the list");
                    System.exit(1);
                } else  if (amount >= 0.01 && amount <= 9000) {
                    session.transactions.depositMoney(accountdId, amount).observe(getViewLifecycleOwner(), new Observer<Account>() {
                        @Override
                        public void onChanged(Account account) {
                            // If we get this far the transaction has gone through, since there is generic error handling in sendRequest which is called from session.transaction.
                            Snackbar.make(getView(), "Money was added.", Snackbar.LENGTH_LONG).show();
                            //binding.addMoneyButton.setEnabled(false);
                            Transfer.clearCache();
                            closeKeyboard();
                            // Basically goes one back in navigation without saving it to navigation history.
                            Navigation.findNavController(root).popBackStack();
                        }
                    });
                } else {
                    closeKeyboard();
                    if (amount < 0.01) {
                        Snackbar.make(getView(), "Money to be added has to be at least 1 cent", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(getView(), "Its over 9000! Maximum deposit is 9000.", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        binding.changeTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String iban = ibanWithBalance.split(" - ")[0];
                Integer accountdId = Session.getSession().accounts.findAccountIdByIban(iban);
                if (accountdId == null) {
                    System.err.println("Account was not found from the list");
                    System.exit(1);
                } else {
                    session.accounts.updateAccountType(accountdId, newType).observe(getViewLifecycleOwner(), new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            if (integer == 0) {
                                Snackbar.make(getView(), "Account type was changed.", Snackbar.LENGTH_LONG).show();
                                closeKeyboard();
                                Navigation.findNavController(root).popBackStack();
                            } else {
                                Snackbar.make(getView(), "Your account has cards attached to it. Can't update to savings type.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
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
        accountType = Session.getSession().accounts.findAccountTypeByIban(ibanWithBalance.split(" - ")[0]);
        binding.addMoneyAccountString.setText(ibanWithBalance);
        populateTypesSpinner();
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }

    private void populateTypesSpinner() {
        binding.currentTypeText.setText("Current type is "+accountType.toString());
        String[] types = {"Normal", "Credit", "Savings"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.typesSpinner.setAdapter(adapter);
        binding.typesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        newType = Account.AccountType.Normal;
                        break;
                    case 1:
                        newType = Account.AccountType.Credit;
                        break;
                    case 2:
                        newType = Account.AccountType.Savings;
                        break;
                }
                if (accountType == newType) binding.changeTypeButton.setEnabled(false);
                else binding.changeTypeButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}


