package com.example.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.internal.samplers.ProbabilisticSampler;
import io.opentracing.util.GlobalTracer;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;

@RestController
@Configuration
@EnableAutoConfiguration
@SpringBootConfiguration
@ComponentScan
public class DemoApplication {
   
	
    @Autowired
    private RestTemplate restTemplate;

	
    @RequestMapping("/")
    public String home() {
//    	Histogram.Timer requestTimer = requestLatency.startTimer();
    	System.out.println("HW");
//    	requestTimer.observeDuration();
        return "Hello World";
    }
    

    @RequestMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }
    
    @RequestMapping("/chaining")
    public String chaining() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/hello", String.class);
        return "Chaining + " + response.getBody();
    }
    
    public void test() {
    	
    }

	@Bean
	public io.opentracing.Tracer jaegerTracer() {
		
		SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv()
				  .withType(ConstSampler.TYPE)
				  .withParam(1);

				ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv()
				  .withLogSpans(true);

				io.jaegertracing.Configuration config = new io.jaegertracing.Configuration("helloWorld")
				  .withSampler(samplerConfig)
				  .withReporter(reporterConfig);

				GlobalTracer.register(config.getTracer());
				return config.getTracer();
	}
	

	public static void main(String[] args) {
		
		SpringApplication.run(DemoApplication.class, args);
		try {
			HTTPServer server = new HTTPServer(1234);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return factory;
    }
}
