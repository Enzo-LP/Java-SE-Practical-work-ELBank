package components;

// 1.1.1 Creation of the client class
public class Client {
    private static int clientCounter = 1;

    private String lastName;
    private String firstName;
    private final int clientNumber;

    public Client(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.clientNumber = clientCounter++;
    }

    // Accessors (getters)
    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    // Mutators (setters)
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "Client [number=" + clientNumber + ", name=" + lastName + ", firstname=" + firstName + "]";
    }
}