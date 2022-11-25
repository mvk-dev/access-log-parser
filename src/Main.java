import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String path;
        int filesCount = 0;
        boolean isDirectory;
        boolean isFileExists;

        while(true) {
            System.out.println("Укажите путь к файлу:");
            path = scanner.nextLine();

            File file = new File(path);
            isFileExists = file.exists();
            isDirectory = file.isDirectory();

            if (!isFileExists || isDirectory) {
                System.out.println("Путь к файлу указан неверно: " + (isFileExists ? " каталог вместо файла" : " файл не существует"));
                continue;   // избыточно
            }
            else {
                filesCount++;
                System.out.println("Путь указан верно. Это файл номер " + filesCount);
            }
        }

    }
}
