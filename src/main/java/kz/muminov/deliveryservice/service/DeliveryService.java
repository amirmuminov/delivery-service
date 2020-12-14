package kz.muminov.deliveryservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import kz.muminov.deliveryservice.model.entity.Delivery;
import kz.muminov.deliveryservice.model.entity.Employee;
import kz.muminov.deliveryservice.model.entity.Meal;
import kz.muminov.deliveryservice.model.enums.DeliveryStatus;
import kz.muminov.deliveryservice.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RestTemplate restTemplate;

    @HystrixCommand(
            fallbackMethod = "fallbackCreateDelivery",
            threadPoolKey = "createDelivery",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "100"),
                    @HystrixProperty(name = "maxQueueSize", value = "50")
            }
    )
    public Delivery createDelivery(Delivery delivery){

        String credentials = "rest-client:passwordd";
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<Meal> deliveryMeals = new ArrayList<>();

        for (Meal meal: delivery.getMeals()){
            Meal existingMeal = restTemplate.exchange("http://menu-service/meal/" + meal.getId(),
                    HttpMethod.GET,
                    entity,
                    Meal.class).getBody();
            deliveryMeals.add(existingMeal);
        }

        delivery.setMeals(deliveryMeals);

        Employee receiver = getEmployee(delivery.getReceiver().getId());

        delivery.setReceiver(receiver);

        return deliveryRepository.save(delivery);

    }

    @HystrixCommand(
            fallbackMethod = "getEmployeeFallback",
            threadPoolKey = "getEmployeeDelivery",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "100"),
                    @HystrixProperty(name = "maxQueueSize", value = "50")
            }
    )
    private Employee getEmployee(Long id){

        String apiCredentials = "rest-client:password";
        String encodedCredentials = new String(Base64.encodeBase64(apiCredentials.getBytes()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        Employee receiver = restTemplate.exchange("http://employee-service/employee/" + id,
                HttpMethod.GET,
                httpEntity,
                Employee.class).getBody();
        return receiver;
    }

    public Delivery finishDelivery(Long id){

        if (!deliveryRepository.existsById(id)){
            throw new RuntimeException("Delivery with id " + id + " does not exist");
        }

        Delivery delivery = deliveryRepository.findById(id).get();

        if(delivery.getStatus() == DeliveryStatus.FINISHED){
            throw new RuntimeException("Delivery with id " + id + " has status FINISHED");
        }

        delivery.setClosedDate(LocalDateTime.now());
        delivery.setStatus(DeliveryStatus.FINISHED);

        return deliveryRepository.save(delivery);

    }

    public Employee getEmployeeFallback(Long id){
        Employee employee = new Employee();
        employee.setId(-1L);
        return employee;
    }

    public Delivery fallbackCreateDelivery(Delivery delivery, Throwable e){
        Delivery fallbackDelivery = new Delivery();
        fallbackDelivery.setId(-1L);
        fallbackDelivery.setStatus(DeliveryStatus.ERROR);
        log.error(e.getMessage());
        return fallbackDelivery;
    }

}
