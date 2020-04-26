package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.AccountContainer;
import com.example.androbank.containers.CardContainer;
import com.example.androbank.containers.TransactionContainer;

import static com.example.androbank.connection.Transfer.sendRequest;
import static com.example.androbank.session.SessionUtils.genericErrorHandling;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Cards {
    private ArrayList<Card> allCardsList = new ArrayList<Card>();

    public ArrayList<Card> getAllCardsList() {
        return allCardsList;
    }

    public MutableLiveData<Card> createCard(Integer accountId, Integer withdrawLimit, Integer spendingLimit, String area) {
        MutableLiveData<Card> finalResult = new MutableLiveData<Card>();
        CardContainer requestContainer = new CardContainer();
        requestContainer.accountId = accountId;
        requestContainer.withdrawLimit = withdrawLimit;
        requestContainer.spendingLimit = spendingLimit;
        requestContainer.area = area;
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
                Card card = new Card(newCard.cardNumber, newCard.cardId, newCard.accountId, newCard.area, newCard.spendingLimit, newCard.withdrawLimit);
                finalResult.postValue(card);
            }
        });
        return finalResult;
    }

    public MutableLiveData<Integer> cardPaymentOrWithdraw(Integer cardId, Integer amount, boolean isPayment) {
        MutableLiveData<Integer> finalResult = new MutableLiveData<Integer>();
        TransactionContainer requestContainer = new TransactionContainer();
        requestContainer.cardId = cardId;
        requestContainer.amount = amount;
        Response response;
        if (isPayment) response = sendRequest(Transfer.MethodType.POST, "/cards/payment", requestContainer, AccountContainer.class, true, false);
        else response = sendRequest(Transfer.MethodType.POST, "/cards/withdraw", requestContainer, AccountContainer.class, true, false);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                Response response = (Response) observable;
                if (genericErrorHandling(response)) {
                    System.out.println(response.getHttpCode() + ": "+response.getError());
                    finalResult.postValue(1);
                } else {
                    finalResult.postValue(0);
                }
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

                    Card currentCard = new Card(cardContainer.cardNumber, cardContainer.cardId, cardContainer.accountId, cardContainer.area, cardContainer.spendingLimit, cardContainer.withdrawLimit);
                    cardList.add(currentCard);
                }
                finalResult.postValue(cardList);
                allCardsList.addAll(cardList);
            }
        });
        return finalResult;
    }

    /**
     *  Used for changing the card details. Sends the request to server and return mutableLiveData for callback.
     * @param cardID CardId belonging to the card which is to be updated.
     * @param withdrawLimit New withdraw limit for the card.
     * @param spendingLimit New spending limit for the card.
     * @param area New area limit for the card.
     * @param currentCard Is used to delete old card data from the AllCardsList.
     * @return MutableLiveData card for callback.
     */
    public MutableLiveData<Card> uodateCard(Integer cardID, Integer withdrawLimit, Integer spendingLimit, String area, Card currentCard) {
        MutableLiveData<Card> finalResult = new MutableLiveData<Card>();
        CardContainer requestContainer = new CardContainer();
        requestContainer.cardId = cardID;
        requestContainer.withdrawLimit = withdrawLimit;
        requestContainer.spendingLimit = spendingLimit;
        requestContainer.area = area;
        Response response = sendRequest(Transfer.MethodType.PATCH, "/cards/updateCard", requestContainer, CardContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {System.out.println(response.getError()); return;};
                CardContainer updatedCardContainer = (CardContainer) response.getResponse();

                currentCard.setAreaLimit( updatedCardContainer.area);
                currentCard.setPaymentLimit(updatedCardContainer.spendingLimit);
                currentCard.setWithdrawLimit(updatedCardContainer.withdrawLimit);
                finalResult.postValue(currentCard);
            }
        });
        return finalResult;
    }

    //public MutableLiveData<Card> makeCardPayment()

    /**
     * Simple function used for getting a matching card object for given parameters.
     * @param cardNumber Number of the card, not database ID
     * @param cardAccountId AccountId of the account which has the card.
     * @return Returns the matching card object or null if it was not founded.
     */
    public Card getCardByCardNumber(String cardNumber, int cardAccountId) {

        for (Card card: allCardsList) {
            if (card.getCardNumber().equals(cardNumber) && card.getAccountId() == cardAccountId) {
                return card;
            }
        }
        return null;
    }

    /**
     * Clears the Cards AllCardsList.
     */
    public void clearAllCardsList () {
        allCardsList.clear();
    }
}
