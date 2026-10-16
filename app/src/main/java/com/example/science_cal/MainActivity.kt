package com.example.science_cal

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.acos
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class MainActivity : Activity() {

    // =========================================================
    // COLORS
    // =========================================================

    private val backgroundColor = Color.rgb(5, 5, 5)
    private val displayColor = Color.rgb(15, 15, 15)
    private val buttonColor = Color.rgb(28, 28, 28)
    private val specialButtonColor = Color.rgb(42, 42, 42)
    private val accentColor = Color.rgb(108, 76, 180)

    private val white = Color.WHITE
    private val gray = Color.rgb(145, 145, 145)

    // =========================================================
    // UI VARIABLES
    // =========================================================

    private lateinit var expressionText: TextView
    private lateinit var resultText: TextView
    private lateinit var statusText: TextView
    private lateinit var angleButton: Button
    private lateinit var angleModeText: TextView

    // =========================================================
    // CALCULATOR VARIABLES
    // =========================================================

    private var expression = ""
    private var radians = true
    private var justCalculated = false

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        val root = FrameLayout(this)

        root.setBackgroundColor(backgroundColor)

        setContentView(root)

        buildCalculator(root)
    }

    // =========================================================
    // BUILD CALCULATOR
    // =========================================================

    private fun buildCalculator(root: FrameLayout) {

        val scrollView = ScrollView(this)

        scrollView.setBackgroundColor(backgroundColor)
        scrollView.isFillViewport = true

        val mainLayout = LinearLayout(this)

        mainLayout.orientation = LinearLayout.VERTICAL

        mainLayout.setPadding(
            dp(12),
            dp(8),
            dp(12),
            dp(12)
        )

        // IMPORTANT:
        // We simply add the layout to ScrollView.
        scrollView.addView(mainLayout)

        root.addView(
            scrollView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        createHeader(mainLayout)

        createDisplay(mainLayout)

        createAngleMode(mainLayout)

        // ---------------------------------------------------------
        // SCIENTIFIC BUTTONS
        // ---------------------------------------------------------

        createButtonRow(
            mainLayout,
            listOf(
                "sin",
                "cos",
                "tan",
                "sin⁻¹",
                "cos⁻¹"
            )
        )

        createButtonRow(
            mainLayout,
            listOf(
                "tan⁻¹",
                "√",
                "x²",
                "xʸ",
                "1/x"
            )
        )

        createButtonRow(
            mainLayout,
            listOf(
                "x!",
                "ln",
                "log",
                "|x|",
                "eˣ"
            )
        )

        createButtonRow(
            mainLayout,
            listOf(
                "π",
                "e",
                "%",
                "(",
                ")"
            )
        )

        // ---------------------------------------------------------
        // MAIN CALCULATOR
        // ---------------------------------------------------------

        createButtonRow(
            mainLayout,
            listOf(
                "AC",
                "⌫",
                "÷",
                "×",
                "−"
            ),
            true
        )

        createButtonRow(
            mainLayout,
            listOf(
                "7",
                "8",
                "9",
                "+/−",
                "+"
            )
        )

        createButtonRow(
            mainLayout,
            listOf(
                "4",
                "5",
                "6",
                "0",
                "="
            )
        )

        // Bottom row
        createBottomRow(mainLayout)

        // ---------------------------------------------------------
        // FOOTER
        // ---------------------------------------------------------

        val footer = TextView(this)

        footer.text = "N O V A  •  SCIENTIFIC CALCULATOR"
        footer.textColor = Color.rgb(60, 60, 60)
        footer.textSize = 7f
        footer.gravity = Gravity.CENTER

        mainLayout.addView(
            footer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(35)
            )
        )

        updateDisplay()
    }

    // =========================================================
    // HEADER
    // =========================================================

    private fun createHeader(parent: LinearLayout) {

        val header = LinearLayout(this)

        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL

        parent.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(62)
            )
        )

        val titleLayout = LinearLayout(this)

        titleLayout.orientation = LinearLayout.VERTICAL
        titleLayout.gravity = Gravity.CENTER_VERTICAL

        header.addView(
            titleLayout,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        )

        val title = TextView(this)

        title.text = "NOVA"
        title.textColor = white
        title.textSize = 23f
        title.setTypeface(null, Typeface.BOLD)

        titleLayout.addView(title)

        val subtitle = TextView(this)

        subtitle.text = "SCIENTIFIC CALCULATOR"
        subtitle.textColor = gray
        subtitle.textSize = 7f

        titleLayout.addView(subtitle)

        angleButton = createButton("RAD")

        angleButton.textSize = 10f

        setButtonBackground(
            angleButton,
            accentColor,
            20f
        )

        header.addView(
            angleButton,
            LinearLayout.LayoutParams(
                dp(62),
                dp(38)
            )
        )

        angleButton.setOnClickListener {

            radians = !radians

            if (radians) {
                angleButton.text = "RAD"
                angleModeText.text = "RAD"
            } else {
                angleButton.text = "DEG"
                angleModeText.text = "DEG"
            }

            updateDisplay()
        }
    }

    // =========================================================
    // DISPLAY
    // =========================================================

    private fun createDisplay(parent: LinearLayout) {

        val display = LinearLayout(this)

        display.orientation = LinearLayout.VERTICAL

        display.setPadding(
            dp(18),
            dp(14),
            dp(18),
            dp(14)
        )

        setViewBackground(
            display,
            displayColor,
            25f
        )

        parent.addView(
            display,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(180)
            )
        )

        // -------------------------------------------------------
        // TOP STATUS
        // -------------------------------------------------------

        val topRow = LinearLayout(this)

        topRow.orientation = LinearLayout.HORIZONTAL
        topRow.gravity = Gravity.CENTER_VERTICAL

        display.addView(
            topRow,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(25)
            )
        )

        statusText = TextView(this)

        statusText.text = "READY"
        statusText.textColor = gray
        statusText.textSize = 8f

        topRow.addView(
            statusText,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        )

        val novaText = TextView(this)

        novaText.text = "NOVA"
        novaText.textColor = gray
        novaText.textSize = 8f
        novaText.gravity = Gravity.CENTER

        topRow.addView(
            novaText,
            LinearLayout.LayoutParams(
                dp(50),
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        // -------------------------------------------------------
        // EXPRESSION
        // -------------------------------------------------------

        expressionText = TextView(this)

        expressionText.text = ""
        expressionText.textColor = gray
        expressionText.textSize = 17f
        expressionText.gravity = Gravity.BOTTOM or Gravity.END
        expressionText.maxLines = 1

        display.addView(
            expressionText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // -------------------------------------------------------
        // RESULT
        // -------------------------------------------------------

        resultText = TextView(this)

        resultText.text = "0"
        resultText.textColor = white
        resultText.textSize = 40f
        resultText.gravity = Gravity.BOTTOM or Gravity.END
        resultText.maxLines = 1

        display.addView(
            resultText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(65)
            )
        )
    }

    // =========================================================
    // ANGLE MODE
    // =========================================================

    private fun createAngleMode(parent: LinearLayout) {

        val row = LinearLayout(this)

        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL

        parent.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(40)
            )
        )

        val label = TextView(this)

        label.text = "ANGLE MODE"
        label.textColor = gray
        label.textSize = 8f

        row.addView(
            label,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        angleModeText = TextView(this)

        angleModeText.text = "RAD"
        angleModeText.textColor = white
        angleModeText.textSize = 8f
        angleModeText.gravity = Gravity.CENTER

        row.addView(
            angleModeText,
            LinearLayout.LayoutParams(
                dp(50),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    // =========================================================
    // BUTTON ROW
    // =========================================================

    private fun createButtonRow(
        parent: LinearLayout,
        names: List<String>,
        special: Boolean = false
    ) {

        val row = LinearLayout(this)

        row.orientation = LinearLayout.HORIZONTAL

        parent.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        for (name in names) {

            val button = createButton(name)

            val color =
                if (special) {
                    specialButtonColor
                } else {
                    buttonColor
                }

            setButtonBackground(
                button,
                color,
                17f
            )

            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )

            params.setMargins(
                dp(2),
                dp(2),
                dp(2),
                dp(2)
            )

            row.addView(button, params)

            button.setOnClickListener {
                handleButton(name)
            }
        }
    }

    // =========================================================
    // BOTTOM ROW
    // =========================================================

    private fun createBottomRow(parent: LinearLayout) {

        val row = LinearLayout(this)

        row.orientation = LinearLayout.HORIZONTAL

        parent.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        addBottomButton(
            row,
            "00",
            1f
        )

        addBottomButton(
            row,
            "000",
            1f
        )

        addBottomButton(
            row,
            ".",
            1f
        )

        addBottomButton(
            row,
            "=",
            1f
        )
    }

    private fun addBottomButton(
        row: LinearLayout,
        text: String,
        weight: Float
    ) {

        val button = createButton(text)

        val color =
            if (text == "=") {
                accentColor
            } else {
                buttonColor
            }

        setButtonBackground(
            button,
            color,
            17f
        )

        val params = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            weight
        )

        params.setMargins(
            dp(2),
            dp(2),
            dp(2),
            dp(2)
        )

        row.addView(button, params)

        button.setOnClickListener {
            handleButton(text)
        }
    }

    // =========================================================
    // CREATE BUTTON
    // =========================================================

    private fun createButton(text: String): Button {

        val button = Button(this)

        button.text = text
        button.textColor = white
        button.gravity = Gravity.CENTER

        button.textSize =
            when {
                text.length >= 5 -> 9f
                text.length >= 4 -> 11f
                else -> 17f
            }

        button.setPadding(0, 0, 0, 0)

        button.stateListAnimator = null

        button.setOnTouchListener { view, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    view.alpha = 0.6f
                    view.scaleX = 0.96f
                    view.scaleY = 0.96f
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    view.alpha = 1f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
            }

            false
        }

        return button
    }

    // =========================================================
    // BUTTON HANDLER
    // =========================================================

    private fun handleButton(value: String) {

        when (value) {

            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9" -> addNumber(value)

            "00" -> addNumber("00")

            "000" -> addNumber("000")

            "." -> addDecimal()

            "+" -> addOperator("+")

            "−" -> addOperator("-")

            "×" -> addOperator("*")

            "÷" -> addOperator("/")

            "xʸ" -> addOperator("^")

            "AC" -> clearCalculator()

            "⌫" -> deleteLast()

            "+/−" -> toggleSign()

            "=" -> calculate()

            "%" -> addPercent()

            "x!" -> addFactorial()

            "x²" -> addPowerTwo()

            "π" -> addValue("π")

            "e" -> addValue("e")

            "(" -> addOpenParenthesis()

            ")" -> addCloseParenthesis()

            "sin" -> addFunction("sin(")

            "cos" -> addFunction("cos(")

            "tan" -> addFunction("tan(")

            "sin⁻¹" -> addFunction("asin(")

            "cos⁻¹" -> addFunction("acos(")

            "tan⁻¹" -> addFunction("atan(")

            "√" -> addFunction("sqrt(")

            "ln" -> addFunction("ln(")

            "log" -> addFunction("log(")

            "|x|" -> addFunction("abs(")

            "eˣ" -> addFunction("exp(")

            "1/x" -> inverse()
        }
    }

    // =========================================================
    // ADD NUMBER
    // =========================================================

    private fun addNumber(value: String) {

        if (justCalculated) {
            expression = ""
            justCalculated = false
        }

        expression += value

        updateDisplay()
    }

    // =========================================================
    // DECIMAL
    // =========================================================

    private fun addDecimal() {

        if (justCalculated) {
            expression = ""
            justCalculated = false
        }

        val currentNumber =
            expression
                .split('+', '-', '*', '/', '^', '(', ')')
                .lastOrNull()
                ?: ""

        if (currentNumber.contains(".")) {
            return
        }

        if (
            expression.isEmpty() ||
            expression.last() in "+-*/^("
        ) {
            expression += "0."
        } else {
            expression += "."
        }

        updateDisplay()
    }

    // =========================================================
    // OPERATOR
    // =========================================================

    private fun addOperator(operator: String) {

        if (expression.isEmpty()) {

            if (operator == "-") {
                expression = "-"
            }

            updateDisplay()
            return
        }

        justCalculated = false

        if (expression.last() in "+-*/^") {
            expression = expression.dropLast(1)
        }

        expression += operator

        updateDisplay()
    }

    // =========================================================
    // FUNCTION
    // =========================================================

    private fun addFunction(function: String) {

        if (justCalculated) {
            expression = ""
            justCalculated = false
        }

        if (expression.isNotEmpty()) {

            val last = expression.last()

            if (
                last.isDigit() ||
                last == ')' ||
                last == 'π' ||
                last == 'e'
            ) {
                expression += "*"
            }
        }

        expression += function

        updateDisplay()
    }

    // =========================================================
    // CONSTANT
    // =========================================================

    private fun addValue(value: String) {

        if (justCalculated) {
            expression = ""
            justCalculated = false
        }

        if (expression.isNotEmpty()) {

            val last = expression.last()

            if (
                last.isDigit() ||
                last == ')' ||
                last == 'π' ||
                last == 'e'
            ) {
                expression += "*"
            }
        }

        expression += value

        updateDisplay()
    }

    // =========================================================
    // OPEN PARENTHESIS
    // =========================================================

    private fun addOpenParenthesis() {

        if (justCalculated) {
            expression = ""
            justCalculated = false
        }

        if (expression.isNotEmpty()) {

            val last = expression.last()

            if (
                last.isDigit() ||
                last == ')' ||
                last == 'π' ||
                last == 'e'
            ) {
                expression += "*"
            }
        }

        expression += "("

        updateDisplay()
    }

    // =========================================================
    // CLOSE PARENTHESIS
    // =========================================================

    private fun addCloseParenthesis() {

        if (expression.isEmpty()) {
            return
        }

        val open =
            expression.count { it == '(' }

        val close =
            expression.count { it == ')' }

        if (open <= close) {
            return
        }

        val last = expression.last()

        if (
            last.isDigit() ||
            last == ')' ||
            last == 'π' ||
            last == 'e'
        ) {
            expression += ")"
        }

        updateDisplay()
    }

    // =========================================================
    // PERCENT
    // =========================================================

    private fun addPercent() {

        if (expression.isEmpty()) {
            return
        }

        val last = expression.last()

        if (
            last.isDigit() ||
            last == ')' ||
            last == 'π' ||
            last == 'e'
        ) {
            expression += "%"
        }

        updateDisplay()
    }

    // =========================================================
    // FACTORIAL
    // =========================================================

    private fun addFactorial() {

        if (expression.isEmpty()) {
            return
        }

        val last = expression.last()

        if (
            last.isDigit() ||
            last == ')' ||
            last == 'π' ||
            last == 'e'
        ) {
            expression += "!"
        }

        updateDisplay()
    }

    // =========================================================
    // SQUARE
    // =========================================================

    private fun addPowerTwo() {

        if (expression.isEmpty()) {
            return
        }

        val last = expression.last()

        if (
            last.isDigit() ||
            last == ')' ||
            last == 'π' ||
            last == 'e'
        ) {
            expression += "^2"
        }

        updateDisplay()
    }

    // =========================================================
    // DELETE
    // =========================================================

    private fun deleteLast() {

        if (expression.isEmpty()) {
            return
        }

        justCalculated = false

        val functions = listOf(
            "asin(",
            "acos(",
            "atan(",
            "sqrt(",
            "sin(",
            "cos(",
            "tan(",
            "abs(",
            "exp(",
            "log(",
            "ln("
        )

        for (function in functions) {

            if (expression.endsWith(function)) {

                expression =
                    expression.dropLast(function.length)

                updateDisplay()

                return
            }
        }

        expression = expression.dropLast(1)

        updateDisplay()
    }

    // =========================================================
    // CLEAR
    // =========================================================

    private fun clearCalculator() {

        expression = ""
        justCalculated = false

        expressionText.text = ""
        resultText.text = "0"
        statusText.text = "READY"
    }

    // =========================================================
    // PLUS / MINUS
    // =========================================================

    private fun toggleSign() {

        if (expression.isEmpty()) {
            return
        }

        try {

            val value =
                ExpressionParser(
                    expression,
                    radians
                ).parse()

            expression = formatNumber(-value)

        } catch (e: Exception) {

            expression =
                if (expression.startsWith("-")) {
                    expression.substring(1)
                } else {
                    "-$expression"
                }
        }

        updateDisplay()
    }

    // =========================================================
    // INVERSE
    // =========================================================

    private fun inverse() {

        if (expression.isEmpty()) {
            return
        }

        try {

            val value =
                ExpressionParser(
                    expression,
                    radians
                ).parse()

            if (value == 0.0) {
                showError("Cannot divide by zero")
                return
            }

            expression = formatNumber(1.0 / value)

            justCalculated = true

            updateDisplay()

        } catch (e: Exception) {

            showError("Invalid expression")
        }
    }

    // =========================================================
    // CALCULATE
    // =========================================================

    private fun calculate() {

        if (expression.isBlank()) {
            return
        }

        try {

            val value =
                ExpressionParser(
                    expression,
                    radians
                ).parse()

            if (
                value.isNaN() ||
                value.isInfinite()
            ) {
                throw Exception()
            }

            val answer = formatNumber(value)

            expressionText.text = expression
            resultText.text = answer
            statusText.text = "RESULT"

            expression = answer

            justCalculated = true

        } catch (e: Exception) {

            showError("Invalid expression")
        }
    }

    // =========================================================
    // UPDATE DISPLAY
    // =========================================================

    private fun updateDisplay() {

        expressionText.text = expression

        if (expression.isEmpty()) {

            resultText.text = "0"
            statusText.text = "READY"

            return
        }

        try {

            val value =
                ExpressionParser(
                    expression,
                    radians
                ).parsePreview()

            if (
                value != null &&
                !value.isNaN() &&
                !value.isInfinite()
            ) {

                resultText.text =
                    formatNumber(value)

                statusText.text = "LIVE RESULT"

            } else {

                resultText.text = "0"
                statusText.text = "CALCULATING"
            }

        } catch (e: Exception) {

            resultText.text = "0"
            statusText.text = "CALCULATING"
        }
    }

    // =========================================================
    // ERROR
    // =========================================================

    private fun showError(message: String) {

        resultText.text = "Error"
        statusText.text = message

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    // =========================================================
    // FORMAT NUMBER
    // =========================================================

    private fun formatNumber(value: Double): String {

        if (abs(value) < 0.000000000001) {
            return "0"
        }

        if (
            value == value.toLong().toDouble() &&
            abs(value) < 1000000000000000.0
        ) {
            return value.toLong().toString()
        }

        return String.format(
            java.util.Locale.US,
            "%.10f",
            value
        )
            .trimEnd('0')
            .trimEnd('.')
    }

    // =========================================================
    // VIEW BACKGROUND
    // =========================================================

    private fun setViewBackground(
        view: View,
        color: Int,
        radius: Float
    ) {

        val drawable = GradientDrawable()

        drawable.setColor(color)
        drawable.cornerRadius = radius

        drawable.setStroke(
            dp(1),
            Color.rgb(50, 50, 50)
        )

        view.background = drawable
    }

    // =========================================================
    // BUTTON BACKGROUND
    // =========================================================

    private fun setButtonBackground(
        button: Button,
        color: Int,
        radius: Float
    ) {

        val drawable = GradientDrawable()

        drawable.setColor(color)
        drawable.cornerRadius = radius

        drawable.setStroke(
            dp(1),
            Color.rgb(55, 55, 55)
        )

        button.background = drawable
    }

    // =========================================================
    // DP CONVERTER
    // =========================================================

    private fun dp(value: Int): Int {

        return (
                value * resources.displayMetrics.density
                ).roundToInt()
    }

    // =========================================================
    // EXPRESSION PARSER
    // =========================================================

    private class ExpressionParser(
        private val input: String,
        private val radians: Boolean
    ) {

        private var position = 0

        // -------------------------------------------------------
        // PARSE
        // -------------------------------------------------------

        fun parse(): Double {

            position = 0

            val result = expression()

            skipSpaces()

            if (position != input.length) {
                throw Exception()
            }

            return result
        }

        // -------------------------------------------------------
        // PREVIEW PARSER
        // -------------------------------------------------------

        fun parsePreview(): Double? {

            return try {

                position = 0

                val result = expression()

                skipSpaces()

                if (position == input.length) {
                    result
                } else {
                    null
                }

            } catch (e: Exception) {

                null
            }
        }

        // -------------------------------------------------------
        // ADDITION / SUBTRACTION
        // -------------------------------------------------------

        private fun expression(): Double {

            var result = term()

            while (true) {

                skipSpaces()

                when {

                    match('+') -> {
                        result += term()
                    }

                    match('-') -> {
                        result -= term()
                    }

                    else -> {
                        break
                    }
                }
            }

            return result
        }

        // -------------------------------------------------------
        // MULTIPLICATION / DIVISION
        // -------------------------------------------------------

        private fun term(): Double {

            var result = power()

            while (true) {

                skipSpaces()

                when {

                    match('*') -> {
                        result *= power()
                    }

                    match('/') -> {

                        val divisor = power()

                        if (divisor == 0.0) {
                            throw Exception()
                        }

                        result /= divisor
                    }

                    implicitMultiplication() -> {
                        result *= power()
                    }

                    else -> {
                        break
                    }
                }
            }

            return result
        }

        // -------------------------------------------------------
        // POWER
        // -------------------------------------------------------

        private fun power(): Double {

            val base = unary()

            skipSpaces()

            return if (match('^')) {

                val exponent = power()

                base.pow(exponent)

            } else {

                base
            }
        }

        // -------------------------------------------------------
        // UNARY
        // -------------------------------------------------------

        private fun unary(): Double {

            skipSpaces()

            return when {

                match('+') -> unary()

                match('-') -> -unary()

                else -> postfix()
            }
        }

        // -------------------------------------------------------
        // POSTFIX
        // -------------------------------------------------------

        private fun postfix(): Double {

            var result = primary()

            while (true) {

                skipSpaces()

                when {

                    match('!') -> {
                        result = factorial(result)
                    }

                    match('%') -> {
                        result /= 100.0
                    }

                    else -> {
                        break
                    }
                }
            }

            return result
        }

        // -------------------------------------------------------
        // PRIMARY
        // -------------------------------------------------------

        private fun primary(): Double {

            skipSpaces()

            if (position >= input.length) {
                throw Exception()
            }

            // Parentheses
            if (match('(')) {

                val result = expression()

                if (!match(')')) {
                    throw Exception()
                }

                return result
            }

            // Pi
            if (match('π')) {
                return Math.PI
            }

            // Letters / functions
            if (input[position].isLetter()) {

                val start = position

                while (
                    position < input.length &&
                    input[position].isLetter()
                ) {
                    position++
                }

                val name =
                    input
                        .substring(start, position)
                        .lowercase()

                return when (name) {

                    "pi" -> Math.PI

                    "e" -> Math.E

                    "sin" ->
                        trig(
                            argument(),
                            "sin"
                        )

                    "cos" ->
                        trig(
                            argument(),
                            "cos"
                        )

                    "tan" ->
                        trig(
                            argument(),
                            "tan"
                        )

                    "asin" ->
                        inverseTrig(
                            argument(),
                            "asin"
                        )

                    "acos" ->
                        inverseTrig(
                            argument(),
                            "acos"
                        )

                    "atan" ->
                        inverseTrig(
                            argument(),
                            "atan"
                        )

                    "sqrt" -> {

                        val value = argument()

                        if (value < 0) {
                            throw Exception()
                        }

                        sqrt(value)
                    }

                    "ln" -> {

                        val value = argument()

                        if (value <= 0) {
                            throw Exception()
                        }

                        ln(value)
                    }

                    "log" -> {

                        val value = argument()

                        if (value <= 0) {
                            throw Exception()
                        }

                        log10(value)
                    }

                    "abs" ->
                        abs(argument())

                    "exp" ->
                        exp(argument())

                    else ->
                        throw Exception()
                }
            }

            return number()
        }

        // -------------------------------------------------------
        // FUNCTION ARGUMENT
        // -------------------------------------------------------

        private fun argument(): Double {

            skipSpaces()

            if (!match('(')) {
                throw Exception()
            }

            val result = expression()

            if (!match(')')) {
                throw Exception()
            }

            return result
        }

        // -------------------------------------------------------
        // NUMBER
        // -------------------------------------------------------

        private fun number(): Double {

            skipSpaces()

            val start = position

            var hasDecimal = false

            while (position < input.length) {

                val character = input[position]

                when {

                    character.isDigit() -> {
                        position++
                    }

                    character == '.' && !hasDecimal -> {
                        hasDecimal = true
                        position++
                    }

                    else -> {
                        break
                    }
                }
            }

            if (start == position) {
                throw Exception()
            }

            return input
                .substring(start, position)
                .toDouble()
        }

        // -------------------------------------------------------
        // IMPLICIT MULTIPLICATION
        // -------------------------------------------------------

        private fun implicitMultiplication(): Boolean {

            skipSpaces()

            if (position >= input.length) {
                return false
            }

            val character = input[position]

            return character == '(' ||
                    character == 'π' ||
                    character.isLetter()
        }

        // -------------------------------------------------------
        // TRIGONOMETRY
        // -------------------------------------------------------

        private fun trig(
            value: Double,
            function: String
        ): Double {

            val angle =
                if (radians) {
                    value
                } else {
                    Math.toRadians(value)
                }

            return when (function) {

                "sin" -> sin(angle)

                "cos" -> cos(angle)

                "tan" -> tan(angle)

                else -> throw Exception()
            }
        }

        // -------------------------------------------------------
        // INVERSE TRIGONOMETRY
        // -------------------------------------------------------

        private fun inverseTrig(
            value: Double,
            function: String
        ): Double {

            val result =
                when (function) {

                    "asin" -> asin(value)

                    "acos" -> acos(value)

                    "atan" -> atan(value)

                    else -> throw Exception()
                }

            return if (radians) {
                result
            } else {
                Math.toDegrees(result)
            }
        }

        // -------------------------------------------------------
        // FACTORIAL
        // -------------------------------------------------------

        private fun factorial(value: Double): Double {

            if (
                value < 0 ||
                value != floor(value) ||
                value > 170
            ) {
                throw Exception()
            }

            var result = 1.0

            var i = 2

            while (i <= value.toInt()) {

                result *= i

                i++
            }

            return result
        }

        // -------------------------------------------------------
        // MATCH CHARACTER
        // -------------------------------------------------------

        private fun match(character: Char): Boolean {

            skipSpaces()

            if (
                position < input.length &&
                input[position] == character
            ) {

                position++

                return true
            }

            return false
        }

        // -------------------------------------------------------
        // SKIP SPACES
        // -------------------------------------------------------

        private fun skipSpaces() {

            while (
                position < input.length &&
                input[position].isWhitespace()
            ) {
                position++
            }
        }
    }
}