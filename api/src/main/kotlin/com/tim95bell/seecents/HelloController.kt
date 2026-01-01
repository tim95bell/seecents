package com.tim95bell.seecents

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping

@RestController
class HelloController {
    @GetMapping("/hello")
    fun hello(): String = "Hello World"
}
