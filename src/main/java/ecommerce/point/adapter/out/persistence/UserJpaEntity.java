package ecommerce.point.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name= "users")
public class UserJpaEntity {
    @Id
    private Long id;

    private Long userId;

    private String name;

}
