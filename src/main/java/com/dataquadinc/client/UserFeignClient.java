package com.dataquadinc.client;

import com.dataquadinc.dtos.UserAssignment;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user",
             url = "${user.microservice.url}",
             configuration = FeignClientConfiguration.class)
public interface UserFeignClient {

    @PostMapping("/users/user/userId-userName")
    ResponseEntity<List<UserAssignment>> getUserIdsAndUserNames(@RequestBody List<String> userIds);


}
