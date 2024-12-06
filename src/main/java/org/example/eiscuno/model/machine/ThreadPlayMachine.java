package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private GameUno gameUno;
    private volatile boolean hasPlayerPlayed;
    private boolean modelTurn;


    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUno gameUno) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.gameUno = gameUno;
        this.hasPlayerPlayed = false;
    }

    public void run() {
        while (true){
            if(hasPlayerPlayed){
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Aqui iria la logica de colocar la carta
                putCardOnTheTable();
                hasPlayerPlayed = false;

            }
        }
    }

    public boolean getHasPlayerPlayed(){
        return hasPlayerPlayed;
    }


    private void putCardOnTheTable(){
        int index = findIndex();

        if(index == -1) {
            System.out.println("la maquina tiene que comer");
            gameUno.eatCard(this.machinePlayer, 1);
            index = findIndex();
        }

        if(index == -1) {
            System.out.println("la maquina pasa");
            return;
        }

        Card card = machinePlayer.getCard(index);
        this.machinePlayer.removeCard(index);
        table.addCardOnTheTable(card);
        tableImageView.setImage(card.getImage());
    }


    private int findIndex() {
        int index = -1;
        Card cardTable = table.getCurrentCardOnTheTable();

        for (int i = 0; i < this.machinePlayer.getCardsPlayer().size(); i++) {
            Card cardMachine = this.machinePlayer.getCardsPlayer().get(i);

            // pueden agregar la logica para las cargas especiales de la maquina
            if(cardMachine.getColor() == cardTable.getColor() || cardMachine.getValue() == cardTable.getValue() || cardTable.getValue() == "WILD" ) {
                index = i;
                break;
            }
        }

        return index;
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}