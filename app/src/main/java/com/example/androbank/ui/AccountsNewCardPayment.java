package com.example.androbank.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentAccountsNewCardPaymentBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Card;
import com.example.androbank.session.Session;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class AccountsNewCardPayment extends Fragment {

    private FragmentAccountsNewCardPaymentBinding binding;
    private View root;
    private float amount;
    private boolean isPayment;
    private ArrayList<Card> cardList;
    private ArrayList<Account> accountList;
    private Card selected;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountsNewCardPaymentBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        populateCardSpinner();
        binding.radioGroup.check(R.id.radioPayment);
        isPayment = true;


        binding.payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPayment) {
                    if (amount < 0.01 || amount > selected.getPaymentLimit() / 100f) {
                        Snackbar.make(getView(), "Payment amount must be smaller than payment limit and at least 0.01€", Snackbar.LENGTH_LONG).show();
                    } else {
                        System.out.println("Lets pay");
                    }
                } else {
                    if (amount < 0.01 || amount > selected.getWithdrawLimit() / 100f) {
                        Snackbar.make(getView(), "Withdraw amount must be smaller than withdraw limit and at least 0.01€", Snackbar.LENGTH_LONG).show();
                    } else {
                        System.out.println("Lets withdraw");
                    }
                }

                //TODO: Payment processing
                //Navigation.findNavController(root).navigate(R.id.action_newCardPayment2_to_accounts);
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

        // RadioGroup
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioId) {
                if (radioId == R.id.radioPayment) {
                    isPayment = true;
                } else if (radioId == R.id.radioWithdraw) {
                    isPayment = false;
                }
            }
        });

        return root;
    }

    private void populateCardSpinner() {
        ArrayList<String> cardArray = new ArrayList<String>();
        accountList = Session.getSession().accounts.getSessionAccounts();
        cardList = Session.getSession().cards.getAllCardsList();
        for (Card card : cardList) {
            cardArray.add(card.getCardNumber());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cardArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.cardDropdown.setAdapter(adapter);
        binding.cardDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (Account account : accountList) {
                    if (account.getAccountId() == cardList.get(i).getAccountId()) {
                        selected = cardList.get(i);
                        String iban = account.getIban();
                        String balance = String.format("%.2f", (float) account.getBalance() / 100);
                        String withdrawLimit = String.format("%.2f", (float) selected.getWithdrawLimit() / 100);
                        String paymentLimit = String.format("%.2f", (float) selected.getPaymentLimit() / 100);
                        String s = "Account: "+iban+"\nBalance: "+balance+"€\nPayment limit: "+paymentLimit+"€\nWithdraw limit: "+withdrawLimit+"€\nArea: "+selected.getAreaLimit();
                        binding.cardInfoBox.setText(s);
                        break;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }
}
