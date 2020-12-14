package kz.muminov.deliveryservice.repository;

import kz.muminov.deliveryservice.model.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {


}
