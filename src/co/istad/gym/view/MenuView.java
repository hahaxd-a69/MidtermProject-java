package co.istad.gym.view;

import co.istad.gym.config.DatabaseConfig;
import co.istad.gym.service.MemberService;
import co.istad.gym.service.SubscriptionService;
import co.istad.gym.service.ReportService;
import java.util.Scanner;

public class MenuView {
    private final Scanner scanner;
    private final MemberService memberService;
    private final SubscriptionService subscriptionService;
    private final ReportService reportService;

    public MenuView() {
        this.scanner = new Scanner(System.in);
        this.memberService = new MemberService();
        this.subscriptionService = new SubscriptionService();
        this.reportService = new ReportService();
    }

    public void displayMainMenu() throws Exception {
        DatabaseConfig.testConnection();

        while (true) {
            System.out.println("\n══════════════════════════════════════════");
            System.out.println("               MAIN MENU                  ");
            System.out.println("══════════════════════════════════════════");
            System.out.println("1. Member Management");
            System.out.println("2. Subscription Management");
            System.out.println("3. Reports");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        memberManagementMenu();
                        break;
                    case 2:
                        subscriptionManagementMenu();
                        break;
                    case 3:
                        reportsMenu();
                        break;
                    case 4:
                        System.out.println("\nThank you for using Gym Management System!");
                        DatabaseConfig.closeConnection();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private void memberManagementMenu() throws Exception {
        while (true) {
            System.out.println("\n══════════════════════════════════════════");
            System.out.println("           MEMBER MANAGEMENT              ");
            System.out.println("══════════════════════════════════════════");
            System.out.println("1. Add New Member");
            System.out.println("2. View All Members");
            System.out.println("3. Update Member");
            System.out.println("4. Delete Member");
            System.out.println("5. Search Members");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        memberService.addMember();
                        break;
                    case 2:
                        memberService.viewAllMembers();
                        break;
                    case 3:
                        memberService.updateMember();
                        break;
                    case 4:
                        memberService.deleteMember();
                        break;
                    case 5:
                        memberService.searchMembers();
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private void subscriptionManagementMenu() throws Exception {
        while (true) {
            System.out.println("\n══════════════════════════════════════════");
            System.out.println("         SUBSCRIPTION MANAGEMENT          ");
            System.out.println("══════════════════════════════════════════");
            System.out.println("1. Add New Subscription");
            System.out.println("2. View All Subscriptions");
            System.out.println("3. Renew Subscription");
            System.out.println("4. Search Subscriptions");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        subscriptionService.addSubscription();
                        break;
                    case 2:
                        subscriptionService.viewAllSubscriptions();
                        break;
                    case 3:
                        subscriptionService.renewSubscription();
                        break;
                    case 4:
                        subscriptionService.searchSubscriptions();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private void reportsMenu() throws Exception {
        while (true) {
            System.out.println("\n══════════════════════════════════════════");
            System.out.println("                 REPORTS                  ");
            System.out.println("══════════════════════════════════════════");
            System.out.println("1. Member Statistics");
            System.out.println("2. Subscription Statistics");
            System.out.println("3. Financial Report");
            System.out.println("4. Daily Summary");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        reportService.generateMemberReport();
                        break;
                    case 2:
                        reportService.generateSubscriptionReport();
                        break;
                    case 3:
                        reportService.generateFinancialReport();
                        break;
                    case 4:
                        reportService.generateDailySummary();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }
}