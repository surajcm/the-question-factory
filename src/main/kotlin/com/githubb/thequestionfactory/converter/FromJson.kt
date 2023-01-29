package com.githubb.thequestionfactory.converter

data class JsonOptions(
    val optionNo: String,
    val type: String,
    val text: String,
    val isAnswer: Boolean
)

data class FromJson(
    val number: String,
    val question: String,
    val options: Set<JsonOptions>,
    val explanation: String,
    val reference: String,
    val tags: String
)
