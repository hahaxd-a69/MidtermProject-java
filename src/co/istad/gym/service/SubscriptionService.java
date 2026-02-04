package co.istad.gym.service;

import co.istad.gym.dao.MemberDAO;
import co.istad.gym.dao.SubscriptionDAO;
import co.istad.gym.dao.MemberDAOImpl;
import co.istad.gym.dao.SubscriptionDAOImpl;
import co.istad.gym.model.Member;
import co.istad.gym.model.Subscription;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class SubscriptionService {
    private final SubscriptionDAO subscriptionDAO;
    private final MemberDAO memberDAO;
    private final Scanner scanner;

    public SubscriptionService() {
        this.subscriptionDAO = new SubscriptionDAOImpl();
        this.memberDAO = new MemberDAOImpl();
        this.scanner = new Scanner(System.in);
    }

    public void addSubscription() {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("         ADD NEW SUBSCRIPTION");
        System.out.println("══════════════════════════════════════════");

        try {
            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            Optional<Member> memberOpt = memberDAO.getMemberById(memberId);
            if (!memberOpt.isPresent()) {
                System.out.println("Member not found!");
                return;
            }

            Member member = memberOpt.get();
            System.out.println("Member: " + member.getFirstName() + " " + member.getLastName());

            System.out.println("\nAvailable Plans:");
            System.out.println("1. Basic Monthly - $29.99");
            System.out.println("2. Premium Monthly - $59.99");
            System.out.println("3. Gold Annual - $499.99");
            System.out.print("Select plan (1-3): ");

            int planChoice = Integer.parseInt(scanner.nextLine());
            String planName;
            double price;

            switch (planChoice) {
                case 1:
                    planName = "Basic Monthly";
                    price = 29.99;
                    break;
                case 2:
                    planName = "Premium Monthly";
                    price = 59.99;
                    break;
                case 3:
                    planName = "Gold Annual";
                    price = 499.99;
                    break;
                default:
                    System.out.println("Invalid plan!");
                    return;
            }

            System.out.print("Start Date (YYYY-MM-DD) [today]: ");
            String startDateStr = scanner.nextLine();
            LocalDate startDate = startDateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(startDateStr);

            System.out.print("Payment Status (PAID/PENDING) [PENDING]: ");
            String paymentStatus = scanner.nextLine();
            if (paymentStatus.isEmpty()) paymentStatus = "PENDING";

            // Calculate end date
            LocalDate endDate;
            if (planName.contains("Annual")) {
                endDate = startDate.plusYears(1);
            } else {
                endDate = startDate.plusMonths(1);
            }

            Subscription subscription = new Subscription(memberId, planName, price, startDate, endDate);
            subscription.setPaymentStatus(paymentStatus);

            int subscriptionId = subscriptionDAO.addSubscription(subscription);

            if (subscriptionId > 0) {
                System.out.println("Subscription added successfully!");
                System.out.println("Subscription ID: " + subscriptionId);
                System.out.println("End Date: " + endDate);
            } else {
                System.out.println("Failed to add subscription!");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewAllSubscriptions() throws Exception {
        System.out.println("\n══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                                                                 ALL SUBSCRIPTIONS");
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-15s %-10s %-20s %-15s %-15s %-12s %-12s %-10s%n",
                "ID", "Member ID", "Plan Name", "Price", "Status", "Start Date", "End Date", "Active");
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");

        List<Subscription> subscriptions = subscriptionDAO.getAllSubscriptions();

        if (subscriptions.isEmpty()) {
            System.out.println("No subscriptions found.");
        } else {
            for (Subscription subscription : subscriptions) {
                String active = subscription.isActive() ? "Yes" : "No";

                System.out.printf("%-15d %-10d %-20s $%-14.2f %-15s %-12s %-12s %-10s%n",
                        subscription.getSubscriptionId(),
                        subscription.getMemberId(),
                        subscription.getPlanName(),
                        subscription.getMonthlyPrice(),
                        subscription.getPaymentStatus(),
                        subscription.getStartDate(),
                        subscription.getEndDate(),
                        active
                );
            }
        }

        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("Total Subscriptions: " + subscriptions.size());
    }

    public void renewSubscription() throws Exception {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("           RENEW SUBSCRIPTION");
        System.out.println("══════════════════════════════════════════");

        System.out.print("Enter Subscription ID to renew: ");
        try {
            int subscriptionId = Integer.parseInt(scanner.nextLine());

            Optional<Subscription> subOpt = subscriptionDAO.getSubscriptionById(subscriptionId);
            if (!subOpt.isPresent()) {
                System.out.println("Subscription not found!");
                return;
            }

            Subscription subscription = subOpt.get();

            System.out.println("\nCurrent Subscription:");
            System.out.println("Plan: " + subscription.getPlanName());
            System.out.println("Current End Date: " + subscription.getEndDate());

            System.out.print("\nExtend by how many days? ");
            int days = Integer.parseInt(scanner.nextLine());

            if (days <= 0) {
                System.out.println("Invalid number of days!");
                return;
            }

            System.out.print("Mark as PAID? (yes/no): ");
            String markPaid = scanner.nextLine();

            boolean success = subscriptionDAO.renewSubscription(subscriptionId, days);

            if (success && markPaid.equalsIgnoreCase("yes")) {
                subscriptionDAO.updatePaymentStatus(subscriptionId, "PAID");
            }

            if (success) {
                System.out.println("Subscription renewed successfully!");
                System.out.println("Extended by " + days + " days");
            } else {
                System.out.println("Failed to renew subscription!");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
        }
    }

    public void searchSubscriptions() throws Exception {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("         SEARCH SUBSCRIPTIONS");
        System.out.println("══════════════════════════════════════════");

        System.out.print("Search by Member Name (leave blank for all): ");
        String memberName = scanner.nextLine();

        System.out.print("Search by Payment Status (PAID/PENDING/OVERDUE) [leave blank]: ");
        String paymentStatus = scanner.nextLine();

        List<Subscription> subscriptions = subscriptionDAO.searchSubscriptions(
                memberName.isEmpty() ? null : memberName,
                paymentStatus.isEmpty() ? null : paymentStatus
        );

        System.out.println("\n══════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                          SEARCH RESULTS");
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════");

        if (subscriptions.isEmpty()) {
            System.out.println("No subscriptions found.");
        } else {
            System.out.printf("%-15s %-10s %-20s %-15s %-15s %-12s %-12s%n",
                    "ID", "Member ID", "Plan Name", "Price", "Status", "Start Date", "End Date");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════");

            for (Subscription subscription : subscriptions) {
                System.out.printf("%-15d %-10d %-20s $%-14.2f %-15s %-12s %-12s%n",
                        subscription.getSubscriptionId(),
                        subscription.getMemberId(),
                        subscription.getPlanName(),
                        subscription.getMonthlyPrice(),
                        subscription.getPaymentStatus(),
                        subscription.getStartDate(),
                        subscription.getEndDate()
                );
            }
        }

        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("Found " + subscriptions.size() + " subscription(s)");
    }
}