package com.example.androbank.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import java.util.Arrays;
import java.util.List;


public class AccountsNewCardPayment extends Fragment {

    private FragmentAccountsNewCardPaymentBinding binding;
    private View root;
    private float amount;
    private boolean isPayment;
    private ArrayList<Card> cardList;
    private ArrayList<Account> accountList;
    private Integer balance;
    private Card selected;
    private Integer areaRow;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountsNewCardPaymentBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        populateCardSpinner();
        binding.radioGroup.check(R.id.radioPayment);
        isPayment = true;

        //PaymentButton
        binding.payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkAreaLimit()) {
                    Snackbar.make(getView(), "You are outside of your card's payment area.", Snackbar.LENGTH_LONG).show();
                } else if (Math.round(amount * 100) > balance) {
                    Snackbar.make(getView(), "Amount can't be larger than balance", Snackbar.LENGTH_LONG).show();
                } else if (isPayment) {
                    System.out.println(selected.getPaymentLimit() / 100f);
                    if (amount < 0.01 || amount > selected.getPaymentLimit() / 100f) {
                        Snackbar.make(getView(), "Payment amount must be smaller than payment limit and at least 0.01€", Snackbar.LENGTH_LONG).show();
                    } else {
                        System.out.println("Lets pay");
                        Session.getSession().cards.cardPaymentOrWithdraw(selected.getCardId(), Math.round(amount * 100), true).observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                if (integer == 1) {
                                    Snackbar.make(getView(), "Card Payment failed.", Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(getView(), "Card Payment was successful.", Snackbar.LENGTH_LONG).show();
                                    closeKeyboard();
                                    Navigation.findNavController(root).popBackStack();
                                }
                            }
                        });
                    }
                } else {
                    if (amount < 0.01 || amount > selected.getWithdrawLimit() / 100f) {
                        Snackbar.make(getView(), "Withdraw amount must be smaller than withdraw limit and at least 0.01€", Snackbar.LENGTH_LONG).show();
                    } else {
                        System.out.println("Lets withdraw");
                        Session.getSession().cards.cardPaymentOrWithdraw(selected.getCardId(), Math.round(amount * 100), false).observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                if (integer == 1) {
                                    Snackbar.make(getView(), "Card Withdraw failed.", Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(getView(), "Card Withdraw was successful.", Snackbar.LENGTH_LONG).show();
                                    closeKeyboard();
                                    Navigation.findNavController(root).popBackStack();
                                }
                            }
                        });
                    }
                }
            }
        });

        String[] areaOptions = {"Finland", "Nordics (not Finland)", "Europe (not Nordics/Estonia)", "Outside Europe"};
        List<String> areaList = new ArrayList<String>(Arrays.asList(areaOptions));
        ArrayAdapter<String> areaSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areaList);
        areaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.debugAreaSpinner.setAdapter(areaSpinnerAdapter);
        binding.debugAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                areaRow = i;
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
                        balance = account.getBalance();
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

    private boolean checkAreaLimit() {
        boolean canMakePayment;
        if (selected.getAreaLimit().equals("-- none --")) canMakePayment = true;
        else if (selected.getAreaLimit().equals("Europe") && areaRow < 3) canMakePayment = true;
        else if (selected.getAreaLimit().equals("Nordics & Estonia") && areaRow < 2) canMakePayment = true;
        else if (selected.getAreaLimit().equals("Finland") && areaRow < 1) canMakePayment = true;
        else canMakePayment = false;
        return canMakePayment;
    }

    private void closeKeyboard() {
        Context context = getActivity();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }
}
