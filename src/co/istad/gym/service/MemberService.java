package co.istad.gym.service;

import co.istad.gym.dao.MemberDAO;
import co.istad.gym.dao.MemberDAOImpl;
import co.istad.gym.model.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MemberService {
    private final MemberDAO memberDAO;
    private final Scanner scanner;

    public MemberService() {
        this.memberDAO = new MemberDAOImpl();
        this.scanner = new Scanner(System.in);
    }

    public void addMember() {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("           ADD NEW MEMBER");
        System.out.println("══════════════════════════════════════════");

        try {
            System.out.print("First Name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            // Check if email exists
            Optional<Member> existingMember = memberDAO.getMemberByEmail(email);
            if (existingMember.isPresent()) {
                System.out.println("Email already exists!");
                return;
            }

            System.out.print("Phone Number: ");
            String phone = scanner.nextLine();

            System.out.print("Date of Birth (YYYY-MM-DD) [optional]: ");
            String dobStr = scanner.nextLine();
            LocalDate dob = null;
            if (!dobStr.isEmpty()) {
                dob = LocalDate.parse(dobStr);
            }

            System.out.print("Emergency Contact: ");
            String emergencyContact = scanner.nextLine();

            Member member = new Member(firstName, lastName, email, phone, dob, emergencyContact);
            int memberId = memberDAO.addMember(member);

            if (memberId > 0) {
                System.out.println("Member added success!");
                System.out.println("Member ID: " + memberId);
            } else {
                System.out.println("Failed to add member!");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewAllMembers() throws Exception {
        System.out.println("\n══════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                                                      ALL MEMBERS");
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-10s %-20s %-25s %-15s %-20s%n",
                "ID", "Name", "Email", "Phone", "Emergency Contact");
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════════════");

        List<Member> members = memberDAO.getAllMembers();

        if (members.isEmpty()) {
            System.out.println("No members found.");
        } else {
            for (Member member : members) {
                System.out.printf("%-10d %-20s %-25s %-15s %-20s%n",
                        member.getMemberId(),
                        member.getFirstName() + " " + member.getLastName(),
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getEmergencyContact()
                );
            }
        }

        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("Total Members: " + members.size());
    }

    public void updateMember() throws Exception {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("            UPDATE MEMBER");
        System.out.println("══════════════════════════════════════════");

        System.out.print("Enter Member ID to update: ");
        try {
            int memberId = Integer.parseInt(scanner.nextLine());

            Optional<Member> memberOpt = memberDAO.getMemberById(memberId);
            if (!memberOpt.isPresent()) {
                System.out.println("Member not found!");
                return;
            }

            Member member = memberOpt.get();

            System.out.println("\nCurrent Information:");
            System.out.println("Name: " + member.getFirstName() + " " + member.getLastName());
            System.out.println("Email: " + member.getEmail());
            System.out.println("Phone: " + member.getPhoneNumber());
            System.out.println("Emergency Contact: " + member.getEmergencyContact());

            System.out.println("\nEnter new information (press Enter to keep current):");

            System.out.print("First Name [" + member.getFirstName() + "]: ");
            String firstName = scanner.nextLine();
            if (!firstName.isEmpty()) member.setFirstName(firstName);

            System.out.print("Last Name [" + member.getLastName() + "]: ");
            String lastName = scanner.nextLine();
            if (!lastName.isEmpty()) member.setLastName(lastName);

            System.out.print("Phone [" + member.getPhoneNumber() + "]: ");
            String phone = scanner.nextLine();
            if (!phone.isEmpty()) member.setPhoneNumber(phone);

            System.out.print("Emergency Contact [" + member.getEmergencyContact() + "]: ");
            String emergencyContact = scanner.nextLine();
            if (!emergencyContact.isEmpty()) member.setEmergencyContact(emergencyContact);

            boolean success = memberDAO.updateMember(member);

            if (success) {
                System.out.println("Member updated successfully!");
            } else {
                System.out.println("Failed to update member!");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid member ID!");
        }
    }

    public void deleteMember() throws Exception {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("            DELETE MEMBER");
        System.out.println("══════════════════════════════════════════");

        System.out.print("Enter Member ID to delete: ");
        try {
            int memberId = Integer.parseInt(scanner.nextLine());

            System.out.print("Are you sure? (yes/no): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            boolean success = memberDAO.softDeleteMember(memberId);

            if (success) {
                System.out.println("Member deleted successfully!");
            } else {
                System.out.println("Failed to delete member!");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid member ID!");
        }
    }

    public void searchMembers() throws Exception {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("            SEARCH MEMBERS");
        System.out.println("══════════════════════════════════════════");

        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();

        List<Member> members = memberDAO.searchMembers(keyword);

        System.out.println("\n══════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                    SEARCH RESULTS FOR: \"" + keyword + "\"");
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════");

        if (members.isEmpty()) {
            System.out.println("No members found.");
        } else {
            System.out.printf("%-10s %-20s %-25s %-15s%n", "ID", "Name", "Email", "Phone");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════");

            for (Member member : members) {
                System.out.printf("%-10d %-20s %-25s %-15s%n",
                        member.getMemberId(),
                        member.getFirstName() + " " + member.getLastName(),
                        member.getEmail(),
                        member.getPhoneNumber()
                );
            }
        }

        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("Found " + members.size() + " member(s)");
    }
}