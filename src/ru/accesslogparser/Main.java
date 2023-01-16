package ru.accesslogparser;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        File file;
        String path;

        while(true) {
            System.out.println("Укажите путь к файлу:");
            path = scanner.nextLine();
            if (path.equalsIgnoreCase("exit"))
                return;

            file = new File(path);

            if (!file.exists() || file.isDirectory()) {
                System.out.println("Путь к файлу указан неверно: " + (file.exists() ? " каталог вместо файла" : " файл не существует"));
            }
            else {
                break;
            }
        }

        LogFile logFile = new LogFile(file, new MaxLengthCheck(1024));

        try {
            logFile.read();
        }
        catch (IOException ex) {
            System.out.println(ex);
            return;
        }

        //long startTime = System.currentTimeMillis();
        System.out.println("Всего разобранных записей из лог-файла " + logFile.getEntries().size());

        // Подсчёт статистики
        Statistics stat = new Statistics();
        for(LogEntry entry: logFile.getEntries()) {
            stat.addEntry(entry);
        }

        System.out.println(stat);
        System.out.println("Доля траффика в минуту = " + String.format("%,.3f", stat.getTrafficRate(Statistics.TimeIntervals.MINUTE)));
        System.out.println("Доля траффика в час = " + String.format("%,.3f", stat.getTrafficRate()));
        System.out.println("Доля траффика в день = " + String.format("%,.3f", stat.getTrafficRate(Statistics.TimeIntervals.DAY)));

        System.out.println("==================================================================");
        System.out.println("Существующие страницы:");
        System.out.println(stat.getExistingPages());

        System.out.println("==================================================================");
        System.out.println("Статистика используемых ОС:");
        for (Map.Entry<String, Double> entry: stat.getOsRate().entrySet())
            System.out.println(entry.getKey() + " = " + String.format("%.2f", entry.getValue()));
    }
}