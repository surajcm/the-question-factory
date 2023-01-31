package com.githubb.thequestionfactory.converter

import com.google.gson.Gson
import com.opencsv.CSVWriter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileWriter

@RestController
@RequestMapping("/json/conversion/")
class JsonToCSVConverter {
    @PostMapping
    fun listFiles(@RequestBody fileLocation: String): ResponseEntity<String> {
        println(fileLocation)
        val file = File(fileLocation)
        if (file.exists() && file.isDirectory) {
            val files = file.listFiles()
            val sortedFiles = files?.sortedBy { it.name }

            val questions = sortedFiles?.map { convertSingleFile(it) }
            validateQuestionPojo(questions)
            //toCSVFile(questions)
            val fileNames = sortedFiles?.map { it.name }
            val fileNameString = fileNames?.joinToString(",")
            return ResponseEntity.ok(fileNameString)
        }
        return ResponseEntity.badRequest().body("Invalid file location")
    }

    private fun validateQuestionPojo(questions: List<FromJson>?) {
        if (questions != null) {
            for (question in questions) {
                println("\n validating  : "+question.number)
                verifyValidOptions(question.options)
            }
        }
    }

    private fun verifyValidOptions(options: Set<JsonOptions>) {
        //println("\n Options : "+options.size)
        var i :Int = 0;
        for (option in options) {
            i++
            if (option.optionNo.toInt() != i) {
               println("something is wrong :: option is"+option.optionNo + "but counter is "+ i)
           }
        }
    }

    private fun convertSingleFile(file: File?): FromJson {
        try {
            val gson = Gson()
            val jsonString = file?.readText()
            return gson.fromJson(jsonString, FromJson::class.java)
        } catch (e: Exception) {
            // Handle exception, e.g. print error message
            println("Error occurred while reading JSON file: ${e.message}")
            throw Exception()
        }
    }

    private fun getQuestionTypeFromOptions(type: String): String {
        return if (type == "radio") {
            "multiple-choice"
        } else {
            "multi-select"
        }
    }

    private fun toCSVFile(questionsFromJson: List<FromJson>?) {
        val fileWriter = FileWriter("/Users/a-3133/Downloads/questions.csv")
        val csvWriter = CSVWriter(fileWriter)
        //Question,Question Type (multiple-choice or multi-select),
        // Answer Option 1,Answer Option 2,Answer Option 3,
        // Answer Option 4,Answer Option 5,Answer Option 6,
        // Answer Option 7,Answer Option 8,Answer Option 9,
        // Answer Option 10,Answer Option 11,Answer Option 12,
        // Answer Option 13,Answer Option 14,Answer Option 15,
        // Correct Response,Explanation,Knowledge Area

        val header = arrayOf("Question","Question Type (multiple-choice or multi-select)",
            "Answer Option 1","Answer Option 2","Answer Option 3",
            "Answer Option 4","Answer Option 5","Answer Option 6",
            "Answer Option 7","Answer Option 8","Answer Option 9",
            "Answer Option 10","Answer Option 11","Answer Option 12",
            "Answer Option 13","Answer Option 14","Answer Option 15",
            "Correct Response","Explanation","Knowledge Area")
        csvWriter.writeNext(header)

        if (questionsFromJson != null) {
            for (question in questionsFromJson) {
                println("\n Currently processing : "+question.number)
                println("\n Options : "+question.options.size)
                val questionType = getQuestionTypeFromOptions(question.options.first().type)
                val questionData = arrayOf(question.question, questionType,
                    getAnswerOptions(1, question.options),
                    getAnswerOptions(2, question.options),
                    getAnswerOptions(3, question.options),
                    getAnswerOptions(4, question.options),
                    getAnswerOptions(5, question.options),
                    getAnswerOptions(6, question.options),
                    "","","",
                    "","","",
                    "","","",
                    getAnswerIds(question.options),
                    question.explanation,
                    question.tags)
                csvWriter.writeNext(questionData)
            }
        }

        csvWriter.close()
    }

    fun getAnswerIds(options: Set<JsonOptions>): String {
        val answerIds = options.filter { it.isAnswer }.map { it.optionNo }
        return if (answerIds.size > 1) answerIds.joinToString(",") else answerIds.first().toString()
    }

    fun getAnswerOptions(questionNumber:Int, options:Set<JsonOptions>) :String {
        for (option in options) {
             if (option != null && option.optionNo != null) {
                if (option.optionNo.toInt() == questionNumber) {
                    return option.text
                }
            }
        }
        return ""
    }


}