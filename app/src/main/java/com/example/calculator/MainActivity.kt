package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var result: TextView? = null
    var isLastDigit: Boolean = false
    var isContainsDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        result = findViewById(R.id.result)
    }

    fun onDigit(view: View) {
        val value = (view as Button).text
        result?.append(value)

        isLastDigit = true
    }

    fun onClear(view: View) {
        result?.text = ""
        isContainsDot = false
    }

    fun onDot(view: View) {
        if (isLastDigit && !isContainsDot) {
            isContainsDot = true
            isLastDigit = false
            result?.append(".")
        }
    }

    // try to add possibility for second number to be negative
    private fun isPossibleToAddOperator(sentence: String, operatorToAdd: String): Boolean {
        if (sentence.startsWith("-"))
            return true

        if (!sentence.contains("+")
            && !sentence.contains("-")
            && !sentence.contains("*")
            && !sentence.contains("/")) {
            return true
        }

        if ((operatorToAdd == "-")
            && ( !sentence.contains("*-")
                    || !sentence.contains("/-")
                    || !sentence.contains("+-")
                    || !sentence.contains("--")) ) {
            return true
        }

        return false
    }

    // check if any of operators already added
    // but ignoring first minus bc it can be negative sign
    private fun isOperatorAdded(value : String): Boolean {
        return when {
            value.startsWith("-") -> false
            else -> value.contains("*")
                    || value.contains("/")
                    || value.contains("+")
                    || value.contains("-")
        }
    }

    fun onOperator(view: View) {
        result?.text?.let { presentText ->
            val operatorToAdd = (view as Button).text
            
            if (isLastDigit && !isOperatorAdded(presentText.toString())) {
                result?.append(operatorToAdd)
                isLastDigit = false
            }
        }
    }

    // 155.0 -> 155
    private fun removeZeroAfterDot(result: String): String {
        var newResult = result
        if (result.contains(".0"))
            newResult = result.substring(0, result.length-2)
        return newResult
    }

    fun onEqual(view: View) {
        if (isLastDigit) {
            var sentence = result?.text.toString()

            try {
                // for remembering negative signs
                var firstPrefix = ""
                var secondPrefix = ""

                // delete and remember negative sign in the first number
                if (sentence.startsWith("-")) {
                    firstPrefix = "-"
                    sentence = sentence.substring(1)
                }

                // delete and remember negative sign in the second number
                if (sentence.contains("+-")
                    || sentence.contains("--")
                    || sentence.contains("*-")
                    || sentence.contains("/-")) {
                    secondPrefix = "-"
                    sentence = sentence.replaceFirst("-", "")
                }

                // "2 + 3" -> [2, 3]
                val numbersInSentence: List<String> = sentence.split("+", "-", "*", "/")

                var first = numbersInSentence[0]
                var second = numbersInSentence[1]

                // add negative signs if needed
                if (firstPrefix.isNotEmpty())
                    first = firstPrefix + first

                if (secondPrefix.isNotEmpty())
                    second = secondPrefix + second

                result?.text = removeZeroAfterDot( when {
                    sentence.contains("*") -> (first.toDouble() * second.toDouble()).toString()
                    sentence.contains("/") -> (first.toDouble() / second.toDouble()).toString()
                    sentence.contains("+") -> (first.toDouble() + second.toDouble()).toString()
                    sentence.contains("-") -> (first.toDouble() - second.toDouble()).toString()
                    else -> sentence
                })

            } catch (e: ArithmeticException) {
                println(e)
            }

        }
    }
}