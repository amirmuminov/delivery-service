package kz.muminov.deliveryservice.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kz.muminov.deliveryservice.model.enums.DeliveryStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery")
@Data
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotNull
    private String address;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate = LocalDateTime.now();

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closedDate;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.OPENED;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Employee receiver;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "delivery_meals",
            joinColumns = {@JoinColumn(name = "delivery_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "meal_id", referencedColumnName = "id")}
    )
    private List<Meal> meals = new ArrayList<>();


}
