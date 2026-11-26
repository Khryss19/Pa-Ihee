package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.ComfortRoom;
import model.MaintenanceRecord;

/**
 * NEW: the "database" of Pa-Ihee. Saves every maintenance record as one line of a .csv file
 * and reads them back when the app starts. MainSystem decides what happens; this class only
 * stores and loads.
 *
 * Columns: DateTime,Building,Floor,Room,Fixture,Status,Reason,UpdatedBy
 */
public class HistoryStore {

    private static final String HEADER = "DateTime,Building,Floor,Room,Fixture,Status,Reason,UpdatedBy";
    private static final int COLUMNS = 8;

    private final Path file;

    public HistoryStore(String path) {
        this.file = Paths.get(path);
    }

    public boolean exists() {
        return Files.exists(file);
    }

    public Path getFile() {
        return file;
    }

    // ---------- writing ----------

    /** Replaces the whole file with the given records (used the first time, to create the file). */
    public void saveAll(List<MaintenanceRecord> records) {
        StringBuilder sb = new StringBuilder(HEADER).append('\n');
        for (MaintenanceRecord rec : records) {
            sb.append(toLine(rec)).append('\n');
        }
        write(sb.toString(), false);
    }

    /** Adds one record at the end of the file (creates the file if it does not exist yet). */
    public void append(MaintenanceRecord rec) {
        if (!exists()) {
            saveAll(List.of(rec));
            return;
        }
        write(toLine(rec) + "\n", true);
    }

    private void write(String text, boolean append) {
        try {
            Path folder = file.toAbsolutePath().getParent();
            if (folder != null) {
                Files.createDirectories(folder);
            }
            Files.writeString(file, text, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                    append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            // The app keeps working from memory; the record just is not on disk.
            System.err.println("Could not save history to " + file + ": " + e.getMessage());
        }
    }

    private static String toLine(MaintenanceRecord r) {
        ComfortRoom room = r.getRoom();
        return String.join(",",
                escape(r.getDateTime().toString()),
                escape(room.getBuilding()),
                String.valueOf(room.getFloor()),
                escape(room.getName()),
                escape(r.getFixtureName()),
                escape(r.getStatus()),
                escape(r.getReason()),
                escape(r.getUpdatedBy()));
    }

    /** Wraps a value in quotes when it contains a comma, quote or line break. */
    static String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // ---------- reading ----------

    /**
     * Reads every valid row of the file. A row that is damaged or names a room that does not exist
     * is skipped (and reported on the console) instead of crashing the app.
     */
    public List<MaintenanceRecord> load(List<ComfortRoom> rooms) {
        List<MaintenanceRecord> result = new ArrayList<>();
        String text;
        try {
            text = Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Could not read history from " + file + ": " + e.getMessage());
            return result;
        }
        if (text.startsWith("\uFEFF")) {
            text = text.substring(1);   // Excel sometimes adds this invisible mark
        }

        boolean first = true;
        for (List<String> row : parse(text)) {
            if (first) {
                first = false;
                if (row.get(0).trim().equals("DateTime")) {
                    continue;   // the header line
                }
            }
            MaintenanceRecord rec = toRecord(row, rooms);
            if (rec == null) {
                System.err.println("Skipped a bad history row: " + row);
            } else {
                result.add(rec);
            }
        }
        return result;
    }

    private static MaintenanceRecord toRecord(List<String> row, List<ComfortRoom> rooms) {
        if (row.size() != COLUMNS) {
            return null;
        }
        try {
            LocalDateTime when = LocalDateTime.parse(row.get(0).trim());
            int floor = Integer.parseInt(row.get(2).trim());
            ComfortRoom room = null;
            for (ComfortRoom r : rooms) {
                if (r.getBuilding().equals(row.get(1)) && r.getFloor() == floor && r.getName().equals(row.get(3))) {
                    room = r;
                }
            }
            if (room == null) {
                return null;
            }
            return new MaintenanceRecord(room, row.get(4), row.get(5), row.get(6), when, row.get(7));
        } catch (RuntimeException e) {
            return null;   // bad date or number
        }
    }

    /** A small CSV reader: handles quoted values, doubled quotes and line breaks inside quotes. */
    static List<List<String>> parse(String text) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < length && text.charAt(i + 1) == '"') {
                        cell.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    cell.append(c);
                }
            } else if (c == '"') {
                quoted = true;
            } else if (c == ',') {
                row.add(cell.toString());
                cell.setLength(0);
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && i + 1 < length && text.charAt(i + 1) == '\n') {
                    i++;
                }
                row.add(cell.toString());
                cell.setLength(0);
                if (!(row.size() == 1 && row.get(0).isEmpty())) {   // skip blank lines
                    rows.add(row);
                }
                row = new ArrayList<>();
            } else {
                cell.append(c);
            }
        }
        if (cell.length() > 0 || !row.isEmpty()) {
            row.add(cell.toString());
            rows.add(row);
        }
        return rows;
    }
}