package com.example.calculator

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var displayTextView: TextView
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        displayTextView = findViewById(R.id.digitalrview)
        resultTextView = findViewById(R.id.result)

        // Set listeners for number and operator buttons
        val buttonIds = listOf(
            R.id.zero, R.id.one, R.id.two, R.id.three,
            R.id.four, R.id.five, R.id.six, R.id.seven,
            R.id.eight, R.id.nine, R.id.point,
            R.id.addition, R.id.subtraction,
            R.id.multiplication, R.id.division
        )

        for (id in buttonIds) {
            findViewById<Button>(id).setOnClickListener {
                val buttonText = (it as Button).text.toString()
                displayTextView.append(buttonText)
            }
        }

        // Equal button
        val equalButton: Button = findViewById(R.id.equal)
        equalButton.setOnClickListener {
            equalAction()
        }

        // Clear button
        val clearButton: Button = findViewById(R.id.ac)
        clearButton.setOnClickListener {
            displayTextView.text = ""
            resultTextView.text = ""
        }
    }

    private fun equalAction() {
        val result = calculateResult()
        resultTextView.text = result
    }

    private fun calcTimeDivision(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var skipNext = false

        for (i in passedList.indices) {
            if (skipNext) {
                skipNext = false
                continue
            }

            when {
                passedList[i] is Float -> {
                    newList.add(passedList[i])
                }
                passedList[i] is String && (passedList[i] == "x" || passedList[i] == "/") -> {
                    val operator = passedList[i] as String
                    val prevDigit = newList.removeAt(newList.size - 1) as Float
                    val nextDigit = passedList[i + 1] as Float

                    val result = when (operator) {
                        "x" -> prevDigit * nextDigit
                        "/" -> prevDigit / nextDigit
                        else -> 0f
                    }

                    newList.add(result)
                    skipNext = true
                }
                else -> {
                    newList.add(passedList[i])
                }
            }
        }

        return newList
    }


    private fun timeDivisionXCalculator(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains("x") || list.contains("/")) {
            list = calcTimeDivision(list)
        }
        return list
    }

    private fun addSubtraction(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float
        for (i in 1 until passedList.size step 2) {
            val operator = passedList[i] as String
            val nextDigit = passedList[i + 1] as Float
            result = when (operator) {
                "+" -> result + nextDigit
                "-" -> result - nextDigit
                else -> result
            }
        }
        return result
    }

    private fun calculateResult(): String {
        val timesDivision = digitalOperator()
        if (timesDivision.isEmpty()) return ""

        val timeDivision = timeDivisionXCalculator(timesDivision)
        if (timeDivision.isEmpty()) return ""

        val result = addSubtraction(timeDivision)
        return result.toString()
    }

    private fun digitalOperator(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigits = ""

        for (character in displayTextView.text) {
            when {
                character.isDigit() || character == '.' -> currentDigits += character
                character in listOf('+', '-', 'x', '/') -> {
                    if (currentDigits.isNotEmpty()) {
                        list.add(currentDigits.toFloat())
                        currentDigits = ""
                    }
                    list.add(character.toString())
                }
            }
        }

        if (currentDigits.isNotEmpty()) {
            list.add(currentDigits.toFloat())
        }
        return list
    }
}
