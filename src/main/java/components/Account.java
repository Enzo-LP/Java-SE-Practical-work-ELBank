package components;

// 1.2.1 Creation of the account class
public abstract class Account {
    protected String label;
    protected double balance;
    protected int accountId;
    protected Client client;
    private static int accountCounter = 1;

    public Account(String label, Client client) {
        this.label = label;
        this.client = client;
        this.accountId = accountCounter++;
        this.balance = 0.0;
    }

    // Accessors
    public String getLabel() {
        return label;
    }

    public double getBalance() {
        return balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public Client getClient() {
        return client;
    }

    // Mutators
    public void setLabel(String label) {
        this.label = label;
    }

    public void setBalance(Flow flow) {
        if (!flow.isEffect()) return;

        if (flow instanceof Credit) {
            this.balance += flow.getAmount();
        } else if (flow instanceof Debit) {
            this.balance -= flow.getAmount();
        } else if (flow instanceof Transfer transfert) {
            if (this.accountId == transfert.getTargetAccountNumber()) {
                this.balance += flow.getAmount();
            } else if (this.accountId == transfert.getIssuingAccountNumber()) {
                this.balance -= flow.getAmount();
            }
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

//    public void updateBalance(Flow flow) {
//        if (!flow.isEffect()) return;
//
//        if (flow instanceof Credit) {
//            this.balance += flow.getAmount();
//        } else if (flow instanceof Debit) {
//            this.balance -= flow.getAmount();
//        } else if (flow instanceof Transfer transfert) {
//            if (this.accountId == transfert.getTargetAccountNumber()) {
//                this.balance += flow.getAmount();
//            } else if (this.accountId == transfert.getIssuingAccountNumber()) {
//                this.balance -= flow.getAmount();
//            }
//        }
//    }

    // ToString
    @Override
    public String toString() {
        return "Account #" + accountId + " (" + label + ") | Balance: " + balance + " € | Client: " + client;
    }
}