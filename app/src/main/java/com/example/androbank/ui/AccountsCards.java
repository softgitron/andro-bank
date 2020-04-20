package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.R;
import com.example.androbank.RecyclerAdapter;

import java.util.ArrayList;

public class AccountsCards extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_accounts_cards, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.cardsListView);
        recyclerView.setHasFixedSize(true); //lista ei kasva kesken suorituksen (tehostaa suoritusta?)
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        ArrayList<RecyclerViewObject> itemList = new ArrayList<>();
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Card 1 - 4309 3494 2398"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Card 2 - 9812 1999 6666"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Card 3 - 3230 0392 0001"));


        mAdapter = new RecyclerAdapter(itemList, AccountsCards.this);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    // TODO Finish implementation.
    public void selectCard(String cardData) {
        System.out.println("CARD + '" + cardData + "' WAS SELECTED!");
    }
}
