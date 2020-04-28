package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.R;
import com.example.androbank.RecyclerAdapter;
import com.example.androbank.connection.Transfer;
import com.example.androbank.databinding.FragmentAccountsCardsBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Card;
import com.example.androbank.session.Session;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class AccountsCards extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FragmentAccountsCardsBinding binding;
    private View root;
    private Session session = Session.getSession();
    private ArrayList<Account> accounts;
    private int accountsIndex = 0;
    private ArrayList<Card> cardArrayList = new ArrayList<Card>();
    private ArrayList<RecyclerViewObject> itemList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountsCardsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        binding.cardPaymentButton.setEnabled(false);
        accounts = session.accounts.getSessionAccounts();
        itemList = new ArrayList<>();
        Snackbar.make(root, "Loading cards, please wait.", Snackbar.LENGTH_LONG).show();



        /*recyclerView = binding.cardsListView;
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true); //lista ei kasva kesken suorituksen (tehostaa suoritusta?)
        recyclerView.setLayoutManager(mLayoutManager);

        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Card 1 - 4309 3494 2398"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Card 2 - 9812 1999 6666"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Card 3 - 3230 0392 0001"));

        mAdapter = new RecyclerAdapter(itemList, AccountsCards.this);
        recyclerView.setAdapter(mAdapter);
        */

        binding.cardPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.AccountsNewCardPayment);
            }
        });

        binding.newCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.action_cards_to_accountsNewCard);
            }
        });

        return root;
    }

    private void populateCardList() {
        Snackbar.make(root, "Cards loaded.", Snackbar.LENGTH_LONG).show();
        binding.cardPaymentButton.setEnabled(true);
        recyclerView = binding.cardsListView;
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Card 3 - 3230 0392 0001"));
        mAdapter = new RecyclerAdapter(itemList, AccountsCards.this);
        recyclerView.setAdapter(mAdapter);

    }



    public void getCards () {
        if (accountsIndex < accounts.size() ) {
            Account account = accounts.get(accountsIndex);
            //System.out.println("Account: " + account.getAccountId());
            session.cards.getCardsList(account.getAccountId() ).observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
                @Override
                public void onChanged(ArrayList<Card> cards) {
                    //System.out.println("Setup cards");
                    cardArrayList.clear();
                    cardArrayList.addAll(cards);
                    for (Card card : cards) {

                        String content = account.getIban() + " - " + card.getCardNumber();
                        System.out.println("Account ID: " + card.getAccountId() + " Adding card: " + content);
                        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, content));
                    }
                    accountsIndex++;
                    Transfer.clearCache();
                    getCards();
                }
            });
        } else {
            System.out.println("GOING TO POPULATE CARDS!!!!!!!!!!!!!!");
            populateCardList();
            return;

        }
    }

    public void selectCard(String cardData) {
        Bundle bundle = new Bundle();
        bundle.putString("cardData", cardData);
        Navigation.findNavController(root).navigate(R.id.AccountsEditCard, bundle);
    }

    public void onResume() {
        super.onResume();
        accountsIndex = 0;
        session.cards.clearAllCardsList();
        getCards();
    }
}
