package com.dataquadinc.client;

import com.dataquadinc.commons.ApiResponse;
import com.dataquadinc.exceptions.FeignClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.ErrorResponse;

import java.io.IOException;

public class CustomErrorDecoder implements ErrorDecoder {

    private static final ObjectMapper objectMapper=new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(CustomErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {

       try{
            log.info("Feign Error Url : {}",response.request().url());
            log.info("Feign Error Status : {}",response.status());
            log.info("Feign Body Is Null : {}",response.body()==null);

           ApiResponse errorResponse=objectMapper.readValue(
                   response.body().asInputStream(),
                   ApiResponse.class);
        return new FeignClientException(errorResponse.getMessage());
       }catch (IOException e){
          throw new RuntimeException("Failed To Parse Error Response",e);
       }
    }
}
