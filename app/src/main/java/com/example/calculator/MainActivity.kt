package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.lang.reflect.InvocationTargetException

class MainActivity : AppCompatActivity() {
    var result: TextView? = null
    var isLastDigit: Boolean = false
    var isContainsDot: Boolean = false
    var isSecondNumberNegative: Boolean = false

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
        isSecondNumberNegative = false
    }

    fun onDot(view: View) {
        if (isLastDigit && !isContainsDot) {
            isContainsDot = true
            isLastDigit = false
            result?.append(".")
        }
    }

    // function to add the possibility for the second number to be negative
    // bc minus can be the operator and can be the negative sign
    fun onMinus(view: View) {
        if (isLastDigit)
            return onOperator(view)
        else
            isSecondNumberNegative = true
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

    // this function is for ignoring multiple taps on equal sign
    // sentence is incorrect when it contains only one number, ex 200, -20, 10.5
    private fun isCorrectSentence(sentence: String): Boolean {
        var sentence: String = sentence
        if (sentence.startsWith("-"))
            sentence = sentence.substring(1)
        return sentence.contains("+")
                || sentence.contains("-")
                || sentence.contains("*")
                || sentence.contains("/")
    }

    fun onEqual(view: View) {
        if (isLastDigit) {
            var sentence = result?.text.toString()

            if (!isCorrectSentence(sentence)) {
                result?.text = sentence
                isSecondNumberNegative = false
                return
            }

            else try {
                // for remembering negative signs
                var firstPrefix = ""
                val secondPrefix = if (isSecondNumberNegative) "-" else ""

                // delete and remember negative sign in the first number
                if (sentence.startsWith("-")) {
                    firstPrefix = "-"
                    sentence = sentence.substring(1)
                }

                // "2 + 3" -> [2, 3]
                val numbersInSentence: List<String> = sentence.split("+", "-", "*", "/")

                var first: String = ""
                var second: String = ""

                try {
                    first = numbersInSentence[0]
                    second = numbersInSentence[1]
                } catch (e: InvocationTargetException) {
                    result?.text = sentence
                    isSecondNumberNegative = false
                    return
                }

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

                isSecondNumberNegative = false

            } catch (e: ArithmeticException) {
                println(e.message)
            }

        }
    }
}