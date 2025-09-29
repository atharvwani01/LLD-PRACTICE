package LowLevelDesign.ATM.State;

import LowLevelDesign.ATM.Entities.ATMSystem;
import LowLevelDesign.ATM.Enums.OperationType;

public interface ATMState {
    void insertCard(ATMSystem atmSystem, String cardNumber);
    void enterPin(ATMSystem atmSystem, String pin);
    void selectOperation(ATMSystem atmSystem, OperationType op, int... args);
    void ejectCard(ATMSystem atmSystem);
}