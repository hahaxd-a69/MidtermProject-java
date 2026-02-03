package co.istad.gym.dao;

import co.istad.gym.model.Subscription;
import java.util.List;
import java.util.Optional;

public interface SubscriptionDAO {
    // CREATE
    int addSubscription(Subscription subscription) throws Exception;

    // READ
    List<Subscription> getAllSubscriptions() throws Exception;
    Optional<Subscription> getSubscriptionById(int subscriptionId) throws Exception;
    List<Subscription> getSubscriptionsByMemberId(int memberId) throws Exception;
    List<Subscription> getActiveSubscriptions() throws Exception;
    List<Subscription> getExpiringSubscriptions(int daysBefore) throws Exception;

    // UPDATE
    boolean updateSubscription(Subscription subscription) throws Exception;
    boolean renewSubscription(int subscriptionId, int days) throws Exception;
    boolean updatePaymentStatus(int subscriptionId, String status) throws Exception;

    // DELETE
    boolean softDeleteSubscription(int subscriptionId) throws Exception;

    // SEARCH
    List<Subscription> searchSubscriptions(String memberName, String paymentStatus) throws Exception;

    // STATISTICS
    int countSubscriptions() throws Exception;
    int countActiveSubscriptions() throws Exception;
    double calculateMonthlyRevenue() throws Exception;
}