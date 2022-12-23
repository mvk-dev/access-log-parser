package ru.accesslogparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class LogFile {
    private final File file;

    private final Checkable check;

    private final List<LogEntry> entries = new ArrayList<>();

    public LogFile(File file, Checkable checkString) {
        this.file = file;
        this.check = checkString;
    }

    public void read() throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String text;

            while ((text = reader.readLine()) != null) {
                if (check != null && !check.checkString(text))
                    continue;

                try {
                    entries.add(new LogEntry(text));
                } catch (LogEntryCreateException ex) {
                    // Просто пишем в консоль
                    System.out.println(ex);
                }
            }
        }
    }

    public List<LogEntry> getEntries() {
        return new ArrayList<>(entries);
    }
}