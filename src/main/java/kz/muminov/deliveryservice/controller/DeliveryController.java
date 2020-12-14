package kz.muminov.deliveryservice.controller;

import kz.muminov.deliveryservice.model.entity.Delivery;
import kz.muminov.deliveryservice.repository.DeliveryRepository;
import kz.muminov.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryRepository deliveryRepository;

    private static final String DELIVERY = "/delivery";
    private static final String GET_DELIVERIES = DELIVERY + "/list";
    private static final String FINISH_DELIVERY = DELIVERY + "/finish/{id}";

    @GetMapping(GET_DELIVERIES)
    public ResponseEntity<List<Delivery>> getDeliveries(){
        return new ResponseEntity<>(deliveryRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping(DELIVERY)
    public ResponseEntity<Delivery> createDelivery(@RequestBody Delivery delivery){
        return new ResponseEntity<>(deliveryService.createDelivery(delivery), HttpStatus.CREATED);
    }

    @PutMapping(FINISH_DELIVERY)
    public ResponseEntity<Delivery> createDelivery(@PathVariable Long id){
        return new ResponseEntity<>(deliveryService.finishDelivery(id), HttpStatus.CREATED);
    }

}
