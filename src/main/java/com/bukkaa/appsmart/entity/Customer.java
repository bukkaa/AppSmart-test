package com.bukkaa.appsmart.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Entity(name = "customers")
public class Customer implements Serializable {

    @Id
    @GeneratedValue(generator = "customer_uuid_generator")
    @GenericGenerator(name = "customer_uuid_generator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    private List<Product> products;
}
