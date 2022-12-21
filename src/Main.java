import java.io.File;
import java.io.IOException;
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

        System.out.println("Всего строк в файле: " + logFile.getRowsCount());
        System.out.println("Длина самой длинной строки = " + logFile.getMaxRowLength());
        System.out.println("Длина самой короткой строки = " + logFile.getMinRowLength());
    }
}

