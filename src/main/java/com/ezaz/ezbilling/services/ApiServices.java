package com.ezaz.ezbilling.services;

import com.ezaz.ezbilling.model.GstDetailsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class ApiServices {

    private final WebClient webClient;

    public ApiServices(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public GstDetailsResponse getGstinData(String gstinNumber) {
        String url = "https://sheet.gstincheck.co.in/check/11957efe387abf0229c05503cf00374e/" + gstinNumber;
        System.out.println("url "+url);
        Mono<GstDetailsResponse> response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(GstDetailsResponse.class)  // Automatically deserialize to DTO
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // Handle WebClient exceptions
                    System.err.println("Error occurred: " + ex.getMessage());
                    return Mono.empty();  // Handle error scenario
                });

        return response.block(); // Synchronous call to get the response
    }
}
