import service.CoindeskApiService;

import java.util.Scanner;

public class BitcoinTestTask {
    private static CoindeskApiService coindeskApiService;

    public static void main(String[] args) {
        boolean retry = true;
        while (retry) {
            coindeskApiService = new CoindeskApiService();
            System.out.print("Please enter currency code: ");
            Scanner sc = new Scanner(System.in);
            String currency = sc.nextLine();
            coindeskApiService.printData(currency);
            System.out.print("Retry (y/n)? ");
            sc = new Scanner(System.in);
            if (!sc.nextLine().equals("y")) retry = false;
        }
    }
}
