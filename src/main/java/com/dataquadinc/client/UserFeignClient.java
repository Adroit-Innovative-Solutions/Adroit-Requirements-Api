package com.dataquadinc.client;

import com.dataquadinc.dtos.TeamDTO;
import com.dataquadinc.dtos.UserAssignment;
import com.dataquadinc.dtos.UserDTO;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user",
             url = "${user.microservice.url}",
             configuration = FeignClientConfiguration.class)
public interface UserFeignClient {

    @PostMapping("/users/user/userId-userName")
    ResponseEntity<List<UserAssignment>> getUserIdsAndUserNames(@RequestBody List<String> userIds);
//    @PostMapping("/users/associated-users/{userId}")
//    ResponseEntity<TeamDTO> getRolesByUserId(@PathVariable String userId);
//
//    @PostMapping("/users/user/{userId}")
//    ResponseEntity<UserDTO> getTeamsData(@PathVariable String userId);
}
