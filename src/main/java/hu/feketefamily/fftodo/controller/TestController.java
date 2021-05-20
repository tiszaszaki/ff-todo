package hu.feketefamily.fftodo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/test")
	public ResponseEntity<String> helloWorld() {
		return ResponseEntity.ok("Hello world!");
	}
}
