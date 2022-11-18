import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите первое число:");
        int vNum1 = scanner.nextInt();

        System.out.println("Введите второе число:");
        int vNum2 = scanner.nextInt();

        System.out.println(vNum1 + " + " + vNum2 + " = " + (vNum1+vNum2));
        System.out.println(vNum1 + " - " + vNum2 + " = " + (vNum1-vNum2));
        System.out.println(vNum1 + " * " + vNum2 + " = " + (vNum1*vNum2));
        System.out.println(vNum1 + " / " + vNum2 + " = " + (double)vNum1/vNum2);
    }
}
