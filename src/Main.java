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
            if (path.toLowerCase().equals("exit"))
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

        System.out.println("Всего запросов = " + logFile.getRowsCount());

        System.out.println("Доля запросов от поисковых ботов:");
        for(Map.Entry<String, Integer> pair: logFile.getWebCrawlers1().entrySet()) {
            System.out.println(pair.getKey() + ". Всего запросов = " + pair.getValue() + ". Доля = " + ((double)pair.getValue() / logFile.getRowsCount())*100 + "%");
        }
        System.out.println("Альтернативный подсчёт:");
        for(Map.Entry<String, Integer> pair: logFile.getWebCrawlers2().entrySet()) {
            System.out.println(pair.getKey() + ". Всего запросов = " + pair.getValue() + ". Доля = " + ((double)pair.getValue() / logFile.getRowsCount())*100 + "%");
        }
    }
}