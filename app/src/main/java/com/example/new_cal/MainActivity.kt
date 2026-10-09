package com.example.new_cal

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.PI
import kotlin.math.E
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.pow
import kotlin.math.round


class MainActivity : AppCompatActivity() {

    private lateinit var expressionText: TextView
    private lateinit var resultText: TextView
    private lateinit var statusText: TextView
    private lateinit var modeButton: TextView

    private var expression = ""
    private var lastAnswer = 0.0
    private var radians = true
    private var justCalculated = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initializeViews()
        setupButtons()

        updateDisplay()
    }


    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private fun initializeViews() {

        expressionText = findViewById(R.id.expressionText)
        resultText = findViewById(R.id.resultText)
        statusText = findViewById(R.id.statusText)
        modeButton = findViewById(R.id.modeButton)
    }


    // ============================================================
    // BUTTON SETUP
    // ============================================================

    @SuppressLint("SetTextI18n")
    private fun setupButtons() {

        // Numbers

        findViewById<Button>(R.id.zeroButton).setOnClickListener {
            addText("0")
        }

        findViewById<Button>(R.id.oneButton).setOnClickListener {
            addText("1")
        }

        findViewById<Button>(R.id.twoButton).setOnClickListener {
            addText("2")
        }

        findViewById<Button>(R.id.threeButton).setOnClickListener {
            addText("3")
        }

        findViewById<Button>(R.id.fourButton).setOnClickListener {
            addText("4")
        }

        findViewById<Button>(R.id.fiveButton).setOnClickListener {
            addText("5")
        }

        findViewById<Button>(R.id.sixButton).setOnClickListener {
            addText("6")
        }

        findViewById<Button>(R.id.sevenButton).setOnClickListener {
            addText("7")
        }

        findViewById<Button>(R.id.eightButton).setOnClickListener {
            addText("8")
        }

        findViewById<Button>(R.id.nineButton).setOnClickListener {
            addText("9")
        }


        // Decimal

        findViewById<Button>(R.id.dotButton).setOnClickListener {
            addText(".")
        }


        // Operators

        findViewById<Button>(R.id.plusButton).setOnClickListener {
            addOperator("+")
        }

        findViewById<Button>(R.id.minusButton).setOnClickListener {
            addOperator("-")
        }

        findViewById<Button>(R.id.multiplyButton).setOnClickListener {
            addOperator("*")
        }

        findViewById<Button>(R.id.divideButton).setOnClickListener {
            addOperator("/")
        }


        // AC

        findViewById<Button>(R.id.acButton).setOnClickListener {

            expression = ""
            lastAnswer = 0.0
            justCalculated = false

            statusText.text = "READY"

            updateDisplay()
        }


        // Delete

        findViewById<Button>(R.id.deleteButton).setOnClickListener {

            if (expression.isNotEmpty()) {

                expression = expression.dropLast(1)

                justCalculated = false

                statusText.text = "READY"

                updateDisplay()
            }
        }


        // Equals

        findViewById<Button>(R.id.equalsButton).setOnClickListener {
            calculate()
        }


        // ANS

        findViewById<Button>(R.id.ansButton).setOnClickListener {
            addText(formatNumber(lastAnswer))
        }


        // Plus / Minus

        findViewById<Button>(R.id.percentMainButton).setOnClickListener {
            addText("%")
        }


        // Scientific buttons

        findViewById<Button>(R.id.sinButton).setOnClickListener {
            addFunction("sin(")
        }

        findViewById<Button>(R.id.cosButton).setOnClickListener {
            addFunction("cos(")
        }

        findViewById<Button>(R.id.tanButton).setOnClickListener {
            addFunction("tan(")
        }

        findViewById<Button>(R.id.asinButton).setOnClickListener {
            addFunction("asin(")
        }

        findViewById<Button>(R.id.acosButton).setOnClickListener {
            addFunction("acos(")
        }

        findViewById<Button>(R.id.atanButton).setOnClickListener {
            addFunction("atan(")
        }

        findViewById<Button>(R.id.sqrtButton).setOnClickListener {
            addFunction("sqrt(")
        }

        findViewById<Button>(R.id.squareButton).setOnClickListener {
            addText("^2")
        }

        findViewById<Button>(R.id.powerButton).setOnClickListener {
            addOperator("^")
        }

        findViewById<Button>(R.id.inverseButton).setOnClickListener {

            if (expression.isNotEmpty()) {
                expression = "1/($expression)"
                updateDisplay()
            }
        }

        findViewById<Button>(R.id.factorialButton).setOnClickListener {
            addText("!")
        }

        findViewById<Button>(R.id.lnButton).setOnClickListener {
            addFunction("ln(")
        }

        findViewById<Button>(R.id.logButton).setOnClickListener {
            addFunction("log(")
        }

        findViewById<Button>(R.id.absButton).setOnClickListener {
            addFunction("abs(")
        }

        findViewById<Button>(R.id.expButton).setOnClickListener {
            addFunction("exp(")
        }

        findViewById<Button>(R.id.piButton).setOnClickListener {
            addText("π")
        }

        findViewById<Button>(R.id.eButton).setOnClickListener {
            addText("e")
        }

        findViewById<Button>(R.id.percentButton).setOnClickListener {
            addText("%")
        }

        findViewById<Button>(R.id.openButton).setOnClickListener {
            addText("(")
        }

        findViewById<Button>(R.id.closeButton).setOnClickListener {
            addText(")")
        }


        // RAD / DEG

        modeButton.setOnClickListener {

            radians = !radians

            if (radians) {
                modeButton.text = "RAD"
            } else {
                modeButton.text = "DEG"
            }
        }
    }


    // ============================================================
    // ADD TEXT
    // ============================================================

    private fun addText(value: String) {

        if (justCalculated) {

            if (
                value.firstOrNull()?.isDigit() == true ||
                value == "." ||
                value == "π" ||
                value == "e"
            ) {
                expression = ""
            }

            justCalculated = false
        }

        expression += value

        statusText.text = "READY"

        updateDisplay()
    }


    // ============================================================
    // ADD FUNCTION
    // ============================================================

    private fun addFunction(function: String) {

        if (justCalculated) {
            justCalculated = false
        }

        expression += function

        statusText.text = "READY"

        updateDisplay()
    }


    // ============================================================
    // ADD OPERATOR
    // ============================================================

    private fun addOperator(operator: String) {

        if (expression.isEmpty()) {

            if (operator == "-") {
                expression = "-"
                updateDisplay()
            }

            return
        }

        justCalculated = false

        val last = expression.last()

        if (last == '+' ||
            last == '-' ||
            last == '*' ||
            last == '/' ||
            last == '^'
        ) {

            expression = expression.dropLast(1)
        }

        expression += operator

        statusText.text = "READY"

        updateDisplay()
    }


    // ============================================================
    // CALCULATE
    // ============================================================

    private fun calculate() {

        if (expression.isBlank()) {
            return
        }

        try {

            val parser = ExpressionParser(
                expression,
                radians,
                lastAnswer
            )

            val value = parser.parse()

            if (value.isNaN() || value.isInfinite()) {
                throw IllegalArgumentException("Invalid result")
            }

            lastAnswer = value

            expressionText.text = expression

            resultText.text = formatNumber(value)

            statusText.text = "RESULT"

            justCalculated = true

        } catch (e: Exception) {

            statusText.text = "ERROR"

            resultText.text = "Invalid"

            justCalculated = false
        }
    }


    // ============================================================
    // UPDATE DISPLAY
    // ============================================================

    private fun updateDisplay() {

        expressionText.text = expression

        if (expression.isEmpty()) {
            resultText.text = "0"
        }
    }


    // ============================================================
    // FORMAT NUMBER
    // ============================================================

    private fun formatNumber(value: Double): String {

        if (abs(value) < 0.0000000001) {
            return "0"
        }

        if (abs(value - round(value)) < 0.0000000001) {
            return round(value).toLong().toString()
        }

        return String.format("%.10f", value)
            .trimEnd('0')
            .trimEnd('.')
    }


    // ============================================================
    // EXPRESSION PARSER
    // ============================================================

    private class ExpressionParser(
        private val text: String,
        private val radians: Boolean,
        private val answer: Double
    ) {

        private var position = 0


        fun parse(): Double {

            val result = parseExpression()

            skipSpaces()

            if (position < text.length) {
                throw IllegalArgumentException("Unexpected character")
            }

            return result
        }


        // --------------------------------------------------------
        // +
        // -
        // --------------------------------------------------------

        private fun parseExpression(): Double {

            var value = parseTerm()

            while (true) {

                skipSpaces()

                if (match('+')) {

                    value += parseTerm()

                } else if (match('-')) {

                    value -= parseTerm()

                } else {

                    break
                }
            }

            return value
        }


        // --------------------------------------------------------
        // *
        // /
        // --------------------------------------------------------

        private fun parseTerm(): Double {

            var value = parsePower()

            while (true) {

                skipSpaces()

                if (match('*')) {

                    value *= parsePower()

                } else if (match('/')) {

                    val divisor = parsePower()

                    if (divisor == 0.0) {
                        throw ArithmeticException("Division by zero")
                    }

                    value /= divisor

                } else {

                    break
                }
            }

            return value
        }


        // --------------------------------------------------------
        // POWER
        // --------------------------------------------------------

        private fun parsePower(): Double {

            var value = parseUnary()

            skipSpaces()

            if (match('^')) {

                val exponent = parsePower()

                value = value.pow(exponent)
            }

            return value
        }


        // --------------------------------------------------------
        // UNARY
        // --------------------------------------------------------

        private fun parseUnary(): Double {

            skipSpaces()

            if (match('+')) {
                return parseUnary()
            }

            if (match('-')) {
                return -parseUnary()
            }

            return parsePostfix()
        }


        // --------------------------------------------------------
        // FACTORIAL + PERCENT
        // --------------------------------------------------------

        private fun parsePostfix(): Double {

            var value = parsePrimary()

            while (true) {

                skipSpaces()

                if (match('!')) {

                    value = factorial(value)

                } else if (match('%')) {

                    value /= 100.0

                } else {

                    break
                }
            }

            return value
        }


        // --------------------------------------------------------
        // PRIMARY
        // --------------------------------------------------------

        private fun parsePrimary(): Double {

            skipSpaces()

            if (position >= text.length) {
                throw IllegalArgumentException("Unexpected end")
            }


            // Parentheses

            if (match('(')) {

                val value = parseExpression()

                skipSpaces()

                if (!match(')')) {
                    throw IllegalArgumentException("Missing )")
                }

                return value
            }


            // Number

            if (
                text[position].isDigit() ||
                text[position] == '.'
            ) {

                return parseNumber()
            }


            // Pi

            if (text[position] == 'π') {

                position++

                return PI
            }


            // Variables / Functions

            if (text[position].isLetter()) {

                val nameStart = position

                while (
                    position < text.length &&
                    text[position].isLetter()
                ) {
                    position++
                }

                val name = text.substring(
                    nameStart,
                    position
                ).lowercase()


                if (name == "e") {
                    return E
                }

                if (name == "ans") {
                    return answer
                }


                if (!match('(')) {
                    throw IllegalArgumentException("Unknown value")
                }

                val argument = parseExpression()

                skipSpaces()

                if (!match(')')) {
                    throw IllegalArgumentException("Missing )")
                }

                return applyFunction(name, argument)
            }


            throw IllegalArgumentException("Invalid character")
        }


        // --------------------------------------------------------
        // NUMBER
        // --------------------------------------------------------

        private fun parseNumber(): Double {

            val start = position

            while (
                position < text.length &&
                (
                        text[position].isDigit() ||
                                text[position] == '.'
                        )
            ) {
                position++
            }

            val numberText = text.substring(
                start,
                position
            )

            return numberText.toDouble()
        }


        // --------------------------------------------------------
        // FUNCTIONS
        // --------------------------------------------------------

        private fun applyFunction(
            name: String,
            value: Double
        ): Double {

            return when (name) {

                "sin" -> {
                    sin(toRadiansIfNeeded(value))
                }

                "cos" -> {
                    cos(toRadiansIfNeeded(value))
                }

                "tan" -> {
                    tan(toRadiansIfNeeded(value))
                }

                "asin" -> {
                    fromRadiansIfNeeded(asin(value))
                }

                "acos" -> {
                    fromRadiansIfNeeded(acos(value))
                }

                "atan" -> {
                    fromRadiansIfNeeded(atan(value))
                }

                "sqrt" -> {
                    if (value < 0) {
                        throw ArithmeticException("Negative square root")
                    }

                    sqrt(value)
                }

                "ln" -> {

                    if (value <= 0) {
                        throw ArithmeticException("Invalid ln")
                    }

                    ln(value)
                }

                "log" -> {

                    if (value <= 0) {
                        throw ArithmeticException("Invalid log")
                    }

                    log10(value)
                }

                "abs" -> {
                    abs(value)
                }

                "exp" -> {
                    exp(value)
                }

                else -> {
                    throw IllegalArgumentException("Unknown function")
                }
            }
        }


        // --------------------------------------------------------
        // DEG / RAD
        // --------------------------------------------------------

        private fun toRadiansIfNeeded(value: Double): Double {

            return if (radians) {
                value
            } else {
                Math.toRadians(value)
            }
        }


        private fun fromRadiansIfNeeded(value: Double): Double {

            return if (radians) {
                value
            } else {
                Math.toDegrees(value)
            }
        }


        // --------------------------------------------------------
        // FACTORIAL
        // --------------------------------------------------------

        private fun factorial(value: Double): Double {

            if (value < 0) {
                throw ArithmeticException("Negative factorial")
            }

            if (value != value.toLong().toDouble()) {
                throw ArithmeticException("Factorial needs integer")
            }

            if (value > 170) {
                throw ArithmeticException("Number too large")
            }

            var result = 1.0

            var i = 2L

            while (i <= value.toLong()) {

                result *= i.toDouble()

                i++
            }

            return result
        }


        // --------------------------------------------------------
        // MATCH
        // --------------------------------------------------------

        private fun match(character: Char): Boolean {

            skipSpaces()

            if (
                position < text.length &&
                text[position] == character
            ) {

                position++

                return true
            }

            return false
        }


        // --------------------------------------------------------
        // SKIP SPACES
        // --------------------------------------------------------

        private fun skipSpaces() {

            while (
                position < text.length &&
                text[position].isWhitespace()
            ) {
                position++
            }
        }
    }
}