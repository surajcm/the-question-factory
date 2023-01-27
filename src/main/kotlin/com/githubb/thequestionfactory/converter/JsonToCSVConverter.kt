package com.githubb.thequestionfactory.converter

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/json/conversion/")
class JsonToCSVConverter {

    @GetMapping("/{slug}")
    fun findOne(@PathVariable slug: String) = "hello"

}