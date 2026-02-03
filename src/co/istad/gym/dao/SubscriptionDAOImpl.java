package co.istad.gym.dao;

import co.istad.gym.config.DatabaseConfig;
import co.istad.gym.dao.SubscriptionDAO;
import co.istad.gym.model.Subscription;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubscriptionDAOImpl implements SubscriptionDAO {

    @Override
    public int addSubscription(Subscription subscription) throws Exception {
        String sql = "INSERT INTO subscriptions (member_id, plan_name, monthly_price, " +
                "start_date, end_date, payment_status, created_at, updated_at, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING subscription_id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subscription.getMemberId());
            pstmt.setString(2, subscription.getPlanName());
            pstmt.setDouble(3, subscription.getMonthlyPrice());
            pstmt.setDate(4, Date.valueOf(subscription.getStartDate()));
            pstmt.setDate(5, Date.valueOf(subscription.getEndDate()));
            pstmt.setString(6, subscription.getPaymentStatus());
            pstmt.setTimestamp(7, Timestamp.valueOf(subscription.getCreatedAt()));
            pstmt.setTimestamp(8, Timestamp.valueOf(subscription.getUpdatedAt()));
            pstmt.setBoolean(9, subscription.isDeleted());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error adding subscription: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Subscription> getAllSubscriptions() throws Exception {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions WHERE is_deleted = false ORDER BY subscription_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                subscriptions.add(mapResultSetToSubscription(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return subscriptions;
    }

    @Override
    public Optional<Subscription> getSubscriptionById(int subscriptionId) throws Exception {
        String sql = "SELECT * FROM subscriptions WHERE subscription_id = ? AND is_deleted = false";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subscriptionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToSubscription(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting subscription by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Subscription> getSubscriptionsByMemberId(int memberId) throws Exception {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions WHERE member_id = ? AND is_deleted = false " +
                "ORDER BY start_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subscriptions.add(mapResultSetToSubscription(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting subscriptions by member ID: " + e.getMessage());
            e.printStackTrace();
        }
        return subscriptions;
    }

    @Override
    public List<Subscription> getActiveSubscriptions() throws Exception {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions WHERE is_deleted = false " +
                "AND payment_status = 'PAID' AND end_date >= CURRENT_DATE " +
                "ORDER BY end_date";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                subscriptions.add(mapResultSetToSubscription(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting active subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return subscriptions;
    }

    @Override
    public List<Subscription> getExpiringSubscriptions(int daysBefore) throws Exception {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions WHERE is_deleted = false " +
                "AND payment_status = 'PAID' AND end_date BETWEEN CURRENT_DATE " +
                "AND CURRENT_DATE + INTERVAL '" + daysBefore + " days' " +
                "ORDER BY end_date";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                subscriptions.add(mapResultSetToSubscription(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting expiring subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return subscriptions;
    }

    @Override
    public boolean updateSubscription(Subscription subscription) throws Exception {
        String sql = "UPDATE subscriptions SET plan_name = ?, monthly_price = ?, " +
                "start_date = ?, end_date = ?, payment_status = ?, updated_at = ? " +
                "WHERE subscription_id = ? AND is_deleted = false";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, subscription.getPlanName());
            pstmt.setDouble(2, subscription.getMonthlyPrice());
            pstmt.setDate(3, Date.valueOf(subscription.getStartDate()));
            pstmt.setDate(4, Date.valueOf(subscription.getEndDate()));
            pstmt.setString(5, subscription.getPaymentStatus());
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(7, subscription.getSubscriptionId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating subscription: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean renewSubscription(int subscriptionId, int days) throws Exception {
        String sql = "UPDATE subscriptions SET end_date = end_date + INTERVAL '" + days + " days', " +
                "updated_at = ? WHERE subscription_id = ? AND is_deleted = false";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, subscriptionId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error renewing subscription: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updatePaymentStatus(int subscriptionId, String status) throws Exception {
        String sql = "UPDATE subscriptions SET payment_status = ?, updated_at = ? " +
                "WHERE subscription_id = ? AND is_deleted = false";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, subscriptionId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean softDeleteSubscription(int subscriptionId) throws Exception {
        String sql = "UPDATE subscriptions SET is_deleted = true, updated_at = ? " +
                "WHERE subscription_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, subscriptionId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error soft deleting subscription: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Subscription> searchSubscriptions(String memberName, String paymentStatus) throws Exception {
        List<Subscription> subscriptions = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT s.* FROM subscriptions s ");
        sqlBuilder.append("JOIN members m ON s.member_id = m.member_id ");
        sqlBuilder.append("WHERE s.is_deleted = false AND m.is_deleted = false ");

        List<Object> params = new ArrayList<>();

        if (memberName != null && !memberName.isEmpty()) {
            sqlBuilder.append("AND (m.first_name ILIKE ? OR m.last_name ILIKE ?) ");
            params.add("%" + memberName + "%");
            params.add("%" + memberName + "%");
        }

        if (paymentStatus != null && !paymentStatus.isEmpty()) {
            sqlBuilder.append("AND s.payment_status = ? ");
            params.add(paymentStatus);
        }

        sqlBuilder.append("ORDER BY s.subscription_id");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                subscriptions.add(mapResultSetToSubscription(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return subscriptions;
    }

    @Override
    public int countSubscriptions() throws Exception {
        String sql = "SELECT COUNT(*) FROM subscriptions WHERE is_deleted = false";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countActiveSubscriptions() throws Exception {
        String sql = "SELECT COUNT(*) FROM subscriptions WHERE is_deleted = false " +
                "AND payment_status = 'PAID' AND end_date >= CURRENT_DATE";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting active subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double calculateMonthlyRevenue() throws Exception {
        String sql = "SELECT COALESCE(SUM(monthly_price), 0) FROM subscriptions " +
                "WHERE is_deleted = false AND payment_status = 'PAID' " +
                "AND EXTRACT(MONTH FROM created_at) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                "AND EXTRACT(YEAR FROM created_at) = EXTRACT(YEAR FROM CURRENT_DATE)";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.err.println("Error calculating monthly revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    private Subscription mapResultSetToSubscription(ResultSet rs) throws SQLException {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(rs.getInt("subscription_id"));
        subscription.setMemberId(rs.getInt("member_id"));
        subscription.setPlanName(rs.getString("plan_name"));
        subscription.setMonthlyPrice(rs.getDouble("monthly_price"));
        subscription.setStartDate(rs.getDate("start_date").toLocalDate());
        subscription.setEndDate(rs.getDate("end_date").toLocalDate());
        subscription.setPaymentStatus(rs.getString("payment_status"));
        subscription.setDeleted(rs.getBoolean("is_deleted"));
        subscription.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        subscription.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return subscription;
    }
}