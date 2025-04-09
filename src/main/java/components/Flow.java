package components;

import java.time.LocalDateTime;

// 1.3.2 Creation of the Flow class
public abstract class Flow {
    private String comment;
    private final int identifier;
    private double amount;
    private int targetAccountNumber;
    private boolean effect;
    private LocalDateTime dateOfFlow;

    private static int flowCounter = 1;

    public Flow(String comment, double amount, int targetAccountNumber) {
        this.comment = comment;
        this.amount = amount;
        this.targetAccountNumber = targetAccountNumber;
        this.effect = false;
        this.dateOfFlow = LocalDateTime.now().plusDays(2);
        this.identifier = flowCounter++;
    }

    // Accessors
    public String getComment() {
        return comment;
    }

    public int getIdentifier() {
        return identifier;
    }

    public double getAmount() {
        return amount;
    }

    public int getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public boolean isEffect() {
        return effect;
    }

    public LocalDateTime getDateOfFlow() {
        return dateOfFlow;
    }

    // Mutators
    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTargetAccountNumber(int targetAccountNumber) {
        this.targetAccountNumber = targetAccountNumber;
    }

    public void setEffect(boolean effect) {
        this.effect = effect;
    }

    public void setDateOfFlow(LocalDateTime dateOfFlow) {
        this.dateOfFlow = dateOfFlow;
    }
}