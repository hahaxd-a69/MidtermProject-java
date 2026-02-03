package co.istad.gym.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Subscription {
    private int subscriptionId;
    private int memberId;
    private String planName;
    private double monthlyPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    // Constructors
    public Subscription() {}

    public Subscription(int memberId, String planName, double monthlyPrice,
                        LocalDate startDate, LocalDate endDate) {
        this.memberId = memberId;
        this.planName = planName;
        this.monthlyPrice = monthlyPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentStatus = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    // Getters and Setters
    public int getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(int subscriptionId) { this.subscriptionId = subscriptionId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public double getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(double monthlyPrice) { this.monthlyPrice = monthlyPrice; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return "PAID".equals(paymentStatus) &&
                !today.isBefore(startDate) &&
                !today.isAfter(endDate) &&
                !isDeleted;
    }

    @Override
    public String toString() {
        return String.format("Subscription{id=%d, memberId=%d, plan='%s', price=%.2f, status='%s'}",
                subscriptionId, memberId, planName, monthlyPrice, paymentStatus);
    }
}