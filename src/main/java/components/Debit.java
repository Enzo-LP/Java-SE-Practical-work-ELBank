package components;

// 1.3.3 Creation of the Debit class
public class Debit extends Flow {
    public Debit(String comment, double amount, int targetAccountNumber) {
        super(comment, amount, targetAccountNumber);
    }
}