package com.example.springboot;

import java.util.Hashtable;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloSvc {

    private static Map<String, String> dataStore = new Hashtable<>();

    static {
        dataStore.put("hello", "World");
        dataStore.put("greeting", "Hello");
        dataStore.put("planet", "World");
    }
 
    @Cacheable(value = "mycache")
    @GetMapping("/allDataAsString")
    public ResponseEntity<String> getHello() {
        dataStore.put("time", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(dataStore.toString());
    }

    @Cacheable(value = "mycache")
    @GetMapping("/allDataAsDictionary")
    public ResponseEntity<Map<String, String>> getAllDataAsDictionary() {
        dataStore.put("time", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(dataStore);
    }


    // @Cacheable(value = "mycache", key = "#key")
    @Cacheable(value = "mycache", key = "#key")
    @GetMapping("/data/{key}")
    public ResponseEntity<String> getData(@PathVariable String key) {
        String value = dataStore.get(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(value);
        }
    }

    @PostMapping( path = "/data", consumes = "application/json")
    @CacheEvict(value = "mycache", allEntries = true)
    public ResponseEntity<Void> updateData(@RequestBody Map<String, String> data) {
        if (data.containsKey("key") && data.containsKey("value")) {
            String key = data.get("key");
            String value = data.get("value");
            dataStore.put(key, value);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/data/{key}")
    @CacheEvict(value = "mycache")
    public ResponseEntity<Void> updateData(@PathVariable String key, @RequestParam String value) {
        if (dataStore.containsKey(key)) {
            dataStore.put(key, value);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/data/{key}")
    @CacheEvict(value = "mycache")
    public ResponseEntity<Void> deleteData(@PathVariable String key) {
        if (dataStore.containsKey(key)) {
            dataStore.remove(key);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
