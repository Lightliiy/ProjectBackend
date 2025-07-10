package com.example.Student.Controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/signaling")
public class SignalingControler {

    private Map<String, Map<String, Object>> signals = new HashMap<>();

    @PostMapping("/{clientId}")
    public void postSignal(@PathVariable String clientId, @RequestBody Map<String, Object> payload) {
        signals.put(clientId, payload);
    }

    @GetMapping("/{clientId}")
    public Map<String, Object> getSignal(@PathVariable String clientId) {
        return signals.getOrDefault(clientId, Collections.emptyMap());
    }

    @DeleteMapping("/{clientId}")
    public void deleteSignal(@PathVariable String clientId) {
        signals.remove(clientId);
    }
}

