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
import com.example.androbank.databinding.FragmentAccountsBinding;
import com.example.androbank.databinding.FragmentAccountsCardsBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Card;
import com.example.androbank.session.Session;

import java.util.ArrayList;

public class AccountsCards extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FragmentAccountsCardsBinding binding;
    private View root;
    private Session session = Session.getSession();
    private ArrayList<Card> cardArrayList = new ArrayList<Card>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountsCardsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        populateCardList();

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

        binding.newCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.action_cards_to_accountsNewCard);
            }
        });

        return root;
    }

    //TODO Jostain syystä getCardsList palauttaa httpCode 0 responseen, jolloin ei saada yhtään kortteja
    private void populateCardList() {
        session.accounts.getAccountsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                ArrayList<RecyclerViewObject> itemList = new ArrayList<>();
                for (Account account : accounts) {
                    System.out.println("Move to next account.");
                    session.cards.getCardsList(account.getAccountId()).observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
                        @Override
                        public void onChanged(ArrayList<Card> cards) {
                            System.out.println("Setup cards");
                            cardArrayList.addAll(cards);
                            for (Card card : cards) {
                                String a = account.getIban() + " " + card.getCardNumber();
                                itemList.add(new RecyclerViewObject(R.drawable.ic_forward, a));
                            }
                        }
                    });
                }
                recyclerView = binding.cardsListView;
                mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(mLayoutManager);
                itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Card 3 - 3230 0392 0001"));
                mAdapter = new RecyclerAdapter(itemList, AccountsCards.this);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }

    // TODO Finish implementation.
    public void selectCard(String cardData) {
        System.out.println("CARD + '" + cardData + "' WAS SELECTED!");
    }
}
