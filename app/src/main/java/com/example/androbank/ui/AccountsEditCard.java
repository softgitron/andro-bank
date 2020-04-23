package com.example.androbank.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.example.androbank.databinding.FragmentAccountsEditCardBinding;
import com.example.androbank.session.Card;
import com.example.androbank.session.Session;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountsEditCard extends Fragment {
    private FragmentAccountsEditCardBinding binding;
    private View root;
    private Session session;
    private String cardDetails;
    private String areaLimit;
    private String cardNumber;
    private Card currentCard;
    private int cardAccountId;
    private final String[] areaOptions = {"-- none --", "Europe", "Nordics & Estonia", "Finland"};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountsEditCardBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        session = Session.getSession();
        initAreSpinner();
        initEditButton();

        return root;
    }


    /**
     * Used only for setting up the Area limit spinner and its event listener.
     */
    private void initAreSpinner() {
        // Area Limit Spinner Adapter
        List<String> areaList = new ArrayList<String>(Arrays.asList(areaOptions));
        ArrayAdapter<String> areaSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areaList);
        areaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.EditCardAreaSpinner.setAdapter(areaSpinnerAdapter);
        // Area Limit
        binding.EditCardAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getSelectedItem().toString();
                areaLimit = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Used for getting the data from bundle which Accounts gives to this fragment.
     */
    public void onStart() {
        super.onStart();
        cardDetails = getArguments().getString("cardData");
        String[] cardDetailsSplitted = cardDetails.split(" - ");
        cardNumber = cardDetailsSplitted[1];
        String cardAccountIban = cardDetailsSplitted[0];
        System.out.println("Card Number: " + cardNumber + "accountIban" + cardAccountIban);
        cardAccountId = session.accounts.findAccountIdByIban(cardAccountIban);
        currentCard = session.cards.getCardByCardNumber(cardNumber, cardAccountId);
        if (currentCard == null) {
            System.err.println("This should never happen, Card was not found from the list. System will exit!");
            System.exit(1);

        }
        setState(cardAccountIban);
        System.out.println(currentCard.getAccountId());
    }

    private void setState(String cardAccountIban) {
        binding.EditCardCardNumber.setText("Account: " + cardAccountIban +"\nCard Number: " + cardNumber);
        binding.EditCardAreaSpinner.setSelection(java.util.Arrays.asList(areaOptions).indexOf(currentCard.getAreaLimit()));
        binding.EditCardInputWithdraw.setText(String.format("%.2f", (float) (currentCard.getWithdrawLimit() / 100) ).replace(",", ".") );
        binding.EditCardSpendingInput.setText(String.format("%.2f", (float) (currentCard.getPaymentLimit() / 100) ).replace(",", ".") );
    }

    private void initEditButton() {
        binding.EditCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String areaLimit = binding.EditCardAreaSpinner.getSelectedItem().toString();
                float withdrawLimit = Float.parseFloat(binding.EditCardInputWithdraw.getText().toString() );
                float spendingLimit = Float.parseFloat(binding.EditCardSpendingInput.getText().toString() );
                Integer wLimit = Math.round(withdrawLimit * 100);
                Integer sLimit = Math.round(spendingLimit * 100);
                session.cards.uodateCard(currentCard.getCardId(), wLimit, sLimit, areaLimit, currentCard).observe(getViewLifecycleOwner(), new Observer<Card>() {
                    @Override
                    public void onChanged(Card card) {

                        Snackbar.make(getView(), "Card updated.", Snackbar.LENGTH_LONG).show();
                        Navigation.findNavController(root).popBackStack();
                    }
                });
            }
        });
    }
}
