package LowLevelDesign.ATM.State;

import LowLevelDesign.ATM.Entities.ATMSystem;
import LowLevelDesign.ATM.Entities.Card;
import LowLevelDesign.ATM.Enums.OperationType;


public class IdleState implements ATMState {
    @Override
    public void insertCard(ATMSystem atmSystem, String cardNumber) {
        System.out.println("\nCard has been inserted.");
        Card card = atmSystem.getCard(cardNumber);

        if (card == null) {
            ejectCard(atmSystem);
        } else {
            atmSystem.setCurrentCard(card);
            atmSystem.changeState(new HasCardState());
        }
    }

    @Override
    public void enterPin(ATMSystem atmSystem, String pin) {
        System.out.println("Error: Please insert a card first.");
    }

    @Override
    public void selectOperation(ATMSystem atmSystem, OperationType op, int... args) {
        System.out.println("Error: Please insert a card first.");
    }

    @Override
    public void ejectCard(ATMSystem atmSystem) {
        System.out.println("Error: Card not found.");
        atmSystem.setCurrentCard(null);
    }
}
