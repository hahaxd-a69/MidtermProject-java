package co.istad.gym.service;

import co.istad.gym.dao.MemberDAO;
import co.istad.gym.dao.SubscriptionDAO;
import co.istad.gym.dao.MemberDAOImpl;
import co.istad.gym.dao.SubscriptionDAOImpl;
import co.istad.gym.model.Member;
import co.istad.gym.model.Subscription;
import java.time.LocalDate;
import java.util.List;

public class ReportService {
    private final MemberDAO memberDAO;
    private final SubscriptionDAO subscriptionDAO;

    public ReportService() {
        this.memberDAO = new MemberDAOImpl();
        this.subscriptionDAO = new SubscriptionDAOImpl();
    }

    public void generateMemberReport() throws Exception {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("           MEMBER STATISTICS");
        System.out.println("══════════════════════════════════════════");

        int totalMembers = memberDAO.countMembers();
        int activeMembers = memberDAO.countActiveMembers();

        System.out.println("Total Members: " + totalMembers);
        System.out.println("Active Members: " + activeMembers);
        System.out.println("Inactive Members: " + (totalMembers - activeMembers));

        if (totalMembers > 0) {
            double activePercentage = (activeMembers * 100.0) / totalMembers;
            System.out.printf("Active Rate: %.1f%%\n", activePercentage);
        }

        // Show recent members
        List<Member> members = memberDAO.getAllMembers();
        if (!members.isEmpty()) {
            System.out.println("\n--- Recent Members ---");
            int count = 0;
            for (Member member : members) {
                if (count >= 5) break;
                System.out.printf("ID: %d, Name: %s %s, Email: %s\n",
                        member.getMemberId(),
                        member.getFirstName(),
                        member.getLastName(),
                        member.getEmail()
                );
                count++;
            }
        }
    }

    public void generateSubscriptionReport() throws Exception {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("        SUBSCRIPTION STATISTICS");
        System.out.println("══════════════════════════════════════════");

        int totalSubscriptions = subscriptionDAO.countSubscriptions();
        int activeSubscriptions = subscriptionDAO.countActiveSubscriptions();
        double monthlyRevenue = subscriptionDAO.calculateMonthlyRevenue();

        System.out.println("Total Subscriptions: " + totalSubscriptions);
        System.out.println("Active Subscriptions: " + activeSubscriptions);
        System.out.printf("Monthly Revenue: $%.2f\n", monthlyRevenue);

        if (totalSubscriptions > 0) {
            double activePercentage = (activeSubscriptions * 100.0) / totalSubscriptions;
            double avgRevenue = monthlyRevenue / activeSubscriptions;
            System.out.printf("Active Rate: %.1f%%\n", activePercentage);
            System.out.printf("Average Revenue per Active Sub: $%.2f\n", avgRevenue);
        }

        // Show expiring subscriptions
        List<Subscription> expiring = subscriptionDAO.getExpiringSubscriptions(7);
        if (!expiring.isEmpty()) {
            System.out.println("\n--- Subscriptions Expiring in Next 7 Days ---");
            for (Subscription sub : expiring) {
                long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(
                        LocalDate.now(), sub.getEndDate()
                );
                System.out.printf("ID: %d, Member ID: %d, Plan: %s, Ends: %s (%d days left)\n",
                        sub.getSubscriptionId(),
                        sub.getMemberId(),
                        sub.getPlanName(),
                        sub.getEndDate(),
                        daysLeft
                );
            }
        }
    }

    public void generateFinancialReport() throws Exception {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("          FINANCIAL REPORT");
        System.out.println("══════════════════════════════════════════");

        double monthlyRevenue = subscriptionDAO.calculateMonthlyRevenue();
        List<Subscription> activeSubs = subscriptionDAO.getActiveSubscriptions();

        System.out.printf("Monthly Recurring Revenue: $%.2f\n", monthlyRevenue);
        System.out.println("Active Subscriptions: " + activeSubs.size());

        if (!activeSubs.isEmpty()) {
            System.out.println("\n--- Revenue by Plan Type ---");
            double basicTotal = 0;
            double premiumTotal = 0;
            double goldTotal = 0;
            int basicCount = 0;
            int premiumCount = 0;
            int goldCount = 0;

            for (Subscription sub : activeSubs) {
                if (sub.getPlanName().contains("Basic")) {
                    basicTotal += sub.getMonthlyPrice();
                    basicCount++;
                } else if (sub.getPlanName().contains("Premium")) {
                    premiumTotal += sub.getMonthlyPrice();
                    premiumCount++;
                } else if (sub.getPlanName().contains("Gold")) {
                    goldTotal += sub.getMonthlyPrice();
                    goldCount++;
                }
            }

            System.out.printf("Basic Monthly: %d subscriptions, $%.2f\n", basicCount, basicTotal);
            System.out.printf("Premium Monthly: %d subscriptions, $%.2f\n", premiumCount, premiumTotal);
            System.out.printf("Gold Annual: %d subscriptions, $%.2f\n", goldCount, goldTotal);

            double totalRevenue = basicTotal + premiumTotal + goldTotal;
            System.out.printf("\nTotal Revenue: $%.2f\n", totalRevenue);
        }
    }

    public void generateDailySummary() throws Exception {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("           DAILY SUMMARY");
        System.out.println("══════════════════════════════════════════");

        System.out.println("Date: " + LocalDate.now());

        int totalMembers = memberDAO.countMembers();
        int activeMembers = memberDAO.countActiveMembers();
        int activeSubscriptions = subscriptionDAO.countActiveSubscriptions();
        List<Subscription> expiring = subscriptionDAO.getExpiringSubscriptions(7);

        System.out.println("\nToday's Statistics:");
        System.out.println("Total Members: " + totalMembers);
        System.out.println("Active Members: " + activeMembers);
        System.out.println("Active Subscriptions: " + activeSubscriptions);
        System.out.println("Subscriptions Expiring in Next 7 Days: " + expiring.size());

        // Show summary
        if (totalMembers > 0) {
            double memberGrowth = ((double) activeMembers / totalMembers) * 100;
            System.out.printf("Member Engagement: %.1f%%\n", memberGrowth);
        }
    }
}