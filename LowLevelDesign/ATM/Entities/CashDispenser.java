package LowLevelDesign.ATM.Entities;

import LowLevelDesign.ATM.ChainOfResponsibility.DispenseChain;

public class CashDispenser {
    private final DispenseChain chain;

    public CashDispenser(DispenseChain chain) {
        this.chain = chain;
    }

    public synchronized void dispenseCash(int amount) {
        chain.dispense(amount);
    }

    public synchronized boolean canDispenseCash(int amount) {
        if (amount % 10 != 0) {
            return false;
        }
        return chain.canDispense(amount);
    }

}
