package com.tiles.server;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;

@RestController  //sets class as a spring rest controller so it can expose the http endpoints - spring boot configuration
public class MetricsController {
    
    @GetMapping(value = "/metrics", produces = MediaType.TEXT_PLAIN_VALUE)    //exposes prometheus metrics as plain text, then scrapes this endpoint to collect the data.
    public String metrics() throws IOException {
        StringWriter writer = new StringWriter();
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
        return writer.toString();
    }
}