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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.example.androbank.R;
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountsAddMoneyBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getActivity();
        session = Session.getSession();
        //binding.addMoneyButton.setEnabled(true);


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

        return root;
    }


    /**
     * Used for getting the data from bundle which Accounts gives to this fragment.
     */
    public void onStart() {
        super.onStart();
        ibanWithBalance = getArguments().getString("accountData");
        binding.addMoneyAccountString.setText(ibanWithBalance);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }
}


