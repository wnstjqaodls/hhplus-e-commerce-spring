package ecommerce.point.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users_points")
public class PointJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public long getAmount () {
        return amount;
    }

    public void setAmount (long amount) {
        this.amount = amount;
    }

    public LocalDateTime getLastChargedAt () {
        return lastChargedAt;
    }

    public void setLastChargedAt (LocalDateTime lastChargedAt) {
        this.lastChargedAt = lastChargedAt;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "last_charged_at")
    private LocalDateTime lastChargedAt;

    @Column(name = "created_at")
    String status;

    public PointJpaEntity () {
    }

    public PointJpaEntity (Long id) {
        this.id = id;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }


    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointJpaEntity that = (PointJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode () {
        return Objects.hash(id);
    }

    @Override
    public String toString () {
        return "PointJpaEntity{" +
            "id=" + id +
            '}';
    }

    public Long getUserId () {
        return userId;
    }

    public void setUserId (Long userId) {
        this.userId = userId;
    }
}
