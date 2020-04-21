package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.AccountContainer;
import com.example.androbank.containers.CardContainer;

import static com.example.androbank.connection.Transfer.sendRequest;
import static com.example.androbank.session.SessionUtils.genericErrorHandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Cards {


    public MutableLiveData<Card> createCard(Integer accountId, Integer withdrawLimit, Integer spendingLimit, String area) {
        MutableLiveData<Card> finalResult = new MutableLiveData<Card>();
        CardContainer requestContainer = new CardContainer();
        requestContainer.accountId = accountId;
        requestContainer.withdrawLimit = withdrawLimit;
        requestContainer.spendingLimit = spendingLimit;
        Response response = sendRequest(Transfer.MethodType.POST, "/cards/createCard", requestContainer, CardContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {System.out.println(response.getError()); return;};
                CardContainer newCard = (CardContainer) response.getResponse();

                /*List<String> areaList = null;
                if (!newCard.area.equals("")) {
                    areaList = new ArrayList<String>(Arrays.asList(newCard.area.split(" ")));
                }*/
                Card card = new Card(newCard.cardNumber, newCard.accountId, newCard.area, newCard.spendingLimit, newCard.withdrawLimit);
                finalResult.postValue(card);
            }
        });
        return finalResult;
    }

    //TODO rivillä 55 responsen hhtpcode on 0, jolloin ei päästä eteenpäin
    public MutableLiveData<ArrayList<Card>> getCardsList(Integer accountId) {
        System.out.println("Lets get some cards");
        ArrayList<Card> cardList = new ArrayList<Card>();
        MutableLiveData<ArrayList<Card>> finalResult = new MutableLiveData<ArrayList<Card>>();
        AccountContainer requestContainer = new AccountContainer();
        requestContainer.accountId = accountId;
        Response response = sendRequest(Transfer.MethodType.POST, "/cards/getCards", requestContainer, CardContainer.class, true, false);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {System.out.println(response.getError()); return;};
                ArrayList<CardContainer> cardContainers = (ArrayList<CardContainer>) response.getResponse();
                for (CardContainer cardContainer : cardContainers) {
                    //System.out.println(cardContainer.cardNumber);
                    /*List<String> areaList = null;
                    if (!cardContainer.area.equals("")) {
                        areaList = new ArrayList<String>(Arrays.asList(cardContainer.area.split(" ")));
                    }*/
                    Card currentCard = new Card(cardContainer.cardNumber, cardContainer.accountId, cardContainer.area, cardContainer.spendingLimit, cardContainer.withdrawLimit);
                    cardList.add(currentCard);
                }
                finalResult.postValue(cardList);
            }
        });
        return finalResult;
    }
}
