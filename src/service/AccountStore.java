package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * NEW: the accounts "database". Saves every account as one line of data/accounts.csv.
 * Columns: SRCode,Name,Role,Floor,Salt,Hash   (the password itself is never saved)
 */
public class AccountStore {

    private static final String HEADER = "SRCode,Name,Role,Floor,Salt,Hash";
    private static final int COLUMNS = 6;

    private final Path file;

    public AccountStore(String path) {
        this.file = Paths.get(path);
    }

    public boolean exists() {
        return Files.exists(file);
    }

    /** Adds one account at the end of the file (creates the file with its header first if needed). */
    public void append(Account account) {
        String line = String.join(",",
                HistoryStore.escape(account.getSrCode()),
                HistoryStore.escape(account.getName()),
                HistoryStore.escape(account.getRole()),
                String.valueOf(account.getFloor()),
                HistoryStore.escape(account.getSalt()),
                HistoryStore.escape(account.getHash())) + "\n";
        try {
            Path folder = file.toAbsolutePath().getParent();
            if (folder != null) {
                Files.createDirectories(folder);
            }
            if (!exists()) {
                Files.writeString(file, HEADER + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }
            Files.writeString(file, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Could not save the account to " + file + ": " + e.getMessage());
        }
    }

    /** Reads every valid account. A damaged row is skipped instead of crashing the app. */
    public List<Account> load() {
        List<Account> result = new ArrayList<>();
        String text;
        try {
            text = Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Could not read accounts from " + file + ": " + e.getMessage());
            return result;
        }
        if (text.startsWith("\uFEFF")) {
            text = text.substring(1);
        }
        boolean first = true;
        for (List<String> row : HistoryStore.parse(text)) {
            if (first) {
                first = false;
                if (row.get(0).trim().equals("SRCode")) continue;   // the header line
            }
            try {
                if (row.size() != COLUMNS) throw new IllegalArgumentException("wrong column count");
                result.add(new Account(row.get(0).trim(), row.get(1), row.get(2),
                        Integer.parseInt(row.get(3).trim()), row.get(4), row.get(5)));
            } catch (RuntimeException e) {
                System.err.println("Skipped a bad account row.");
            }
        }
        return result;
    }
}