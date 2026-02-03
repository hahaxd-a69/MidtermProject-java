package co.istad.gym.dao;

import co.istad.gym.config.DatabaseConfig;
import co.istad.gym.dao.MemberDAO;
import co.istad.gym.model.Member;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAOImpl implements MemberDAO {

    @Override
    public int addMember(Member member) throws Exception {
        String sql = "INSERT INTO members (first_name, last_name, email, phone_number, " +
                "date_of_birth, emergency_contact, created_at, updated_at, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING member_id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setString(3, member.getEmail());
            pstmt.setString(4, member.getPhoneNumber());
            pstmt.setDate(5, member.getDateOfBirth() != null ?
                    Date.valueOf(member.getDateOfBirth()) : null);
            pstmt.setString(6, member.getEmergencyContact());
            pstmt.setTimestamp(7, Timestamp.valueOf(member.getCreatedAt()));
            pstmt.setTimestamp(8, Timestamp.valueOf(member.getUpdatedAt()));
            pstmt.setBoolean(9, member.isDeleted());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Member> getAllMembers() throws Exception {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE is_deleted = false ORDER BY member_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all members: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public Optional<Member> getMemberById(int memberId) throws Exception {
        String sql = "SELECT * FROM members WHERE member_id = ? AND is_deleted = false";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMember(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting member by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Member> getMemberByEmail(String email) throws Exception {
        String sql = "SELECT * FROM members WHERE email = ? AND is_deleted = false";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMember(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting member by email: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean updateMember(Member member) throws Exception {
        String sql = "UPDATE members SET first_name = ?, last_name = ?, email = ?, " +
                "phone_number = ?, date_of_birth = ?, emergency_contact = ?, " +
                "updated_at = ? WHERE member_id = ? AND is_deleted = false";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setString(3, member.getEmail());
            pstmt.setString(4, member.getPhoneNumber());
            pstmt.setDate(5, member.getDateOfBirth() != null ?
                    Date.valueOf(member.getDateOfBirth()) : null);
            pstmt.setString(6, member.getEmergencyContact());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(8, member.getMemberId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean softDeleteMember(int memberId) throws Exception {
        String sql = "UPDATE members SET is_deleted = true, updated_at = ? WHERE member_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, memberId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error soft deleting member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Member> searchMembers(String keyword) throws Exception {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE is_deleted = false AND " +
                "(first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ? OR phone_number ILIKE ?) " +
                "ORDER BY member_id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching members: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public List<Member> getActiveMembers() throws Exception {
        // Members with active subscriptions
        String sql = "SELECT DISTINCT m.* FROM members m " +
                "JOIN subscriptions s ON m.member_id = s.member_id " +
                "WHERE m.is_deleted = false AND s.is_deleted = false " +
                "AND s.payment_status = 'PAID' AND s.end_date >= CURRENT_DATE " +
                "ORDER BY m.member_id";

        List<Member> members = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting active members: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public int countMembers() throws Exception {
        String sql = "SELECT COUNT(*) FROM members WHERE is_deleted = false";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting members: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countActiveMembers() throws Exception {
        String sql = "SELECT COUNT(DISTINCT m.member_id) FROM members m " +
                "JOIN subscriptions s ON m.member_id = s.member_id " +
                "WHERE m.is_deleted = false AND s.is_deleted = false " +
                "AND s.payment_status = 'PAID' AND s.end_date >= CURRENT_DATE";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting active members: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setMemberId(rs.getInt("member_id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setEmail(rs.getString("email"));
        member.setPhoneNumber(rs.getString("phone_number"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            member.setDateOfBirth(dob.toLocalDate());
        }

        member.setEmergencyContact(rs.getString("emergency_contact"));
        member.setDeleted(rs.getBoolean("is_deleted"));
        member.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        member.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return member;
    }
}