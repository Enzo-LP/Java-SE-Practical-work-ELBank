package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import components.*;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BankApp {

    // 1.1.2 Génération des clients
    public static List<Client> generateClients(int number) {
        List<Client> clients = new ArrayList<>();
        for (int i = 1; i <= number; i++) {
            clients.add(new Client("LastName" + i, "FirstName" + i));
        }
        return clients;
    }

    public static void displayClients(List<Client> clients) {
        clients.forEach(System.out::println);
    }

    // 1.2.3 Génération des comptes
    public static List<Account> generateAccounts(List<Client> clients) {
        List<Account> accounts = new ArrayList<>();
        for (Client client : clients) {
            accounts.add(new CurrentAccount(client));
            accounts.add(new SavingsAccount(client));
        }
        return accounts;
    }

    public static void displayAccounts(List<Account> accounts) {
        accounts.forEach(System.out::println);
    }

    // 1.3.1 Création de la map des comptes
    public static Map<Integer, Account> generateAccountMap(List<Account> accounts) {
        Map<Integer, Account> map = new Hashtable<>();
        for (Account account : accounts) {
            map.put(account.getAccountId(), account);
        }
        return map;
    }

    public static void displayAccountsSorted(Map<Integer, Account> accountMap) {
        accountMap.values().stream()
                .sorted(Comparator.comparingDouble(Account::getBalance))
                .forEach(System.out::println);
    }

    // 1.3.4 Génération des flux
    public static List<Flow> generateFlows(List<Account> accounts) {
        List<Flow> flows = new ArrayList<>();

        int acc1 = accounts.get(0).getAccountId(); // Account n°1
        int acc2 = accounts.get(1).getAccountId(); // Account n°2

        flows.add(new Debit("Débit 50€", 50.0, acc1));
        for (Account acc : accounts) {
            if (acc instanceof CurrentAccount)
                flows.add(new Credit("Crédit 100.50€", 100.50, acc.getAccountId()));
            else if (acc instanceof SavingsAccount)
                flows.add(new Credit("Crédit 1500€", 1500.0, acc.getAccountId()));
        }
        flows.add(new Transfer("Transfer 50€", 50.0, acc2, acc1));

        return flows;
    }

    // 1.3.5 Application des flux aux comptes
    public static void applyFlows(List<Flow> flows, Map<Integer, Account> accountMap) {
        for (Flow flow : flows) {
            if (flow instanceof Transfer transfert) {
                // Débiter le compte émetteur
                accountMap.get(transfert.getIssuingAccountNumber()).setBalance(transfert);
                // Créditer le compte récepteur
                accountMap.get(transfert.getTargetAccountNumber()).setBalance(transfert);
            } else {
                accountMap.get(flow.getTargetAccountNumber()).setBalance(flow);
            }
            flow.setEffect(true);
        }

        // Vérifier si solde négatif
        Optional<Account> negative = accountMap.values().stream()
                .filter(((Predicate<Account>) acc -> acc.getBalance() < 0)).findAny();

        negative.ifPresent(acc -> System.out.println("❌ Solde négatif détecté sur le compte : " + acc));
    }

    // 2.1 Charger les flux depuis un fichier JSON
    public static List<Flow> loadFlowsFromJson(String resourcePath) {
        List<Flow> flows = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = BankApp.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            if (is == null) {
                throw new FileNotFoundException("Ressource non trouvée: " + resourcePath);
            }

            JsonNode root = mapper.readTree(reader);

            for (JsonNode node : root) {
                String type = node.get("type").asText();
                String comment = node.get("comment").asText();
                double amount = node.get("amount").asDouble();
                int targetAccount = node.get("targetAccount").asInt();

                Flow flow;
                switch (type.toLowerCase()) {
                    case "debit" -> flow = new Debit(comment, amount, targetAccount);
                    case "credit" -> flow = new Credit(comment, amount, targetAccount);
                    case "transfer" -> {
                        int sourceAccount = node.get("sourceAccount").asInt();
                        flow = new Transfer(comment, amount, targetAccount, sourceAccount);
                    }
                    default -> {
                        System.err.println("Type de flux inconnu : " + type);
                        continue;
                    }
                }

                flow.setEffect(true); // On applique directement l'effet (comme demandé dans 1.3.5)
                flows.add(flow);
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier JSON : " + e.getMessage());
        }

        return flows;
    }


    // 2.2 Charger les comptes depuis un fichier XML
    public static List<Account> loadAccountsFromXml(String resourcePath) {
        List<Account> accounts = new ArrayList<>();

        try (InputStream is = BankApp.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new FileNotFoundException("Fichier non trouvé : " + resourcePath);

            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(is);

            NodeList nodes = doc.getElementsByTagName("account");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element accountEl = (Element) nodes.item(i);

                String type = accountEl.getAttribute("type");
                double balance = Double.parseDouble(accountEl.getElementsByTagName("balance").item(0).getTextContent());
                Element clientEl = (Element) accountEl.getElementsByTagName("client").item(0);
                String last = clientEl.getElementsByTagName("lastName").item(0).getTextContent();
                String first = clientEl.getElementsByTagName("firstName").item(0).getTextContent();

                Client client = new Client(last, first);
                Account account = "current".equals(type) ? new CurrentAccount(client) : new SavingsAccount(client);

                if (balance != 0) {
                    Flow credit = new Credit("Initial balance", balance, account.getAccountId());
                    credit.setEffect(true);
                    account.setBalance(credit);
                }

                accounts.add(account);
            }

        } catch (Exception e) {
            System.err.println("Erreur XML : " + e.getMessage());
        }

        return accounts;
    }



    public static void main(String[] args) {
        // === 1.1.2 Création de clients ===
        List<Client> clients = generateClients(3);
        System.out.println("===== Clients générés =====");
        displayClients(clients);

        // === 1.2.3 Création des comptes à partir des clients ===
        List<Account> accounts = generateAccounts(clients);
        System.out.println("\n===== Comptes créés (courants + épargne) =====");
        displayAccounts(accounts);

        // === 1.3.1 Génération de la map de comptes (Hashtable) ===
        Map<Integer, Account> accountMap = generateAccountMap(accounts);

        // === 2.1 Chargement des flux depuis un fichier JSON ===
        List<Flow> flows = loadFlowsFromJson("flows.json");
        System.out.println("\n===== Flux chargés depuis flows.json =====");
        flows.forEach(flow -> System.out.println(
                flow.getClass().getSimpleName() + " #" + flow.getIdentifier() +
                        " | " + flow.getComment() +
                        " | Montant: " + flow.getAmount() +
                        " | Cible: " + flow.getTargetAccountNumber() +
                        (flow instanceof Transfer t ? " | Émetteur: " + t.getIssuingAccountNumber() : "")
        ));

        // === 1.3.5 Application des flux aux comptes ===
        applyFlows(flows, accountMap);

        // === 1.3.1 Affichage trié des comptes selon solde croissant ===
        System.out.println("\n===== Comptes triés après application des flux =====");
        displayAccountsSorted(accountMap);

        // === 2.2 Test du chargement XML (bonus ou test indépendant) ===
        List<Account> xmlAccounts = loadAccountsFromXml("accounts.xml");
        System.out.println("\n===== Comptes chargés depuis accounts.xml =====");
        displayAccounts(xmlAccounts);
    }
}
