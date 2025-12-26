package com.dataquadinc.controller;


import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.DashBoardData;
import com.dataquadinc.service.impl.DashBoardServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://35.188.150.92", "http://192.168.0.140:3000", "http://192.168.0.139:3000","https://mymulya.com","http://localhost:3000","http://192.168.0.135:8080","http://192.168.0.135",
        "http://182.18.177.16","http://192.168.0.139:3000"})
@Slf4j
@RestController
@RequestMapping(SystemConstants.API_BASE_PATH+"/dash")
public class DashBoardController {

    @Autowired
    DashBoardServiceImpl dashBoardService;

    @GetMapping("/get-all")
    public ResponseEntity<DashBoardData> getDashBoradData(){
        DashBoardData dashBoardData = dashBoardService.getDashboardData();
        return new ResponseEntity<> (dashBoardData, HttpStatus.FOUND);
    }
}

