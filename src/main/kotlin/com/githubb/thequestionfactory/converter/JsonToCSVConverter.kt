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
            // Print the file names
            /*if (sortedFiles != null) {
                for (file2 in sortedFiles) {
                    println(file2.name)
                }
            }*/
            //val question = convertSingleFile(sortedFiles);
            val questions = sortedFiles?.map { convertSingleFile(it) }
            toCSVFile(questions)

            val fileNames = sortedFiles?.map { it.name }
            //println("File names: $fileNames")
            val fileNameString = fileNames?.joinToString(",")
            return ResponseEntity.ok(fileNameString)
        }
        return ResponseEntity.badRequest().body("Invalid file location")
    }

    private fun convertSingleFile(file: File?): FromJson {
        val gson = Gson()
        val jsonString = file?.readText()
        return gson.fromJson(jsonString, FromJson::class.java)
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
                //Which of the following are woodwind instruments?,multi-select,
                // Oboe,Trumpet,Flute,Bassoon,Violin,Timpani,,,,,,,,,,
                // "1,3,4",
                // "Clarinets, flutes, oboes, bassoons, contrabasoons, and English horns make up the core of the woodwind family. Trumpets are wind instruments but not woodwinds - they belong to the brass family. Violins and timpani are strings and percussion, respectively.",
                // Music
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