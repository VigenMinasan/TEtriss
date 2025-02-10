package com.example.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.os.Handler
import android.os.Looper

class TetrisView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val gridWidth = 10
    private val gridHeight = 20
    private var blockSize = 50f // Размер блока теперь может быть вычислен
    private var score = 0

    private var grid: Array<IntArray> = Array(gridHeight) { IntArray(gridWidth) { 0 } }

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLUE
        isAntiAlias = true
    }

    private val gridLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.LTGRAY
        strokeWidth = 2f
    }

    private val scorePaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private var currentPiece: Tetromino? = null
    private var currentX = 0
    private var currentY = 0

    private val handler = Handler(Looper.getMainLooper())
    private val gameTickInterval = 800L
    private var isRunning = false

    init {
        startGame()
    }

    fun startGame() {
        grid = Array(gridHeight) { IntArray(gridWidth) { 0 } }
        score = 0
        spawnNewPiece()
        isRunning = true
        gameTick()
    }

    private fun gameTick() {
        if (!isRunning) return

        moveDown()

        handler.postDelayed({ gameTick() }, gameTickInterval)
    }

    private fun spawnNewPiece() {
        currentPiece = Tetromino.getRandomTetromino()
        currentX = gridWidth / 2 - currentPiece!!.width / 2
        currentY = 0

        if (!isValidMove(currentX, currentY, currentPiece!!)) {
            isRunning = false
            println("Game Over!")
            //TODO: implement game over screen
            return
        }
    }

    fun moveLeft() {
        if (isValidMove(currentX - 1, currentY, currentPiece!!)) {
            currentX--
            invalidate()
        }
    }

    fun moveRight() {
        if (isValidMove(currentX + 1, currentY, currentPiece!!)) {
            currentX++
            invalidate()
        }
    }

    fun moveDown() {
        if (isValidMove(currentX, currentY + 1, currentPiece!!)) {
            currentY++
            invalidate()
        } else {
            placePiece()
            val linesRemoved = removeFullLines()
            score += linesRemoved * linesRemoved * 100
            spawnNewPiece()
        }
    }

    fun rotate() {
        val rotatedPiece = currentPiece!!.rotateRight()
        if (isValidMove(currentX, currentY, rotatedPiece)) {
            currentPiece = rotatedPiece
            invalidate()
        }
    }
    fun stopGame() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }
    private fun isValidMove(x: Int, y: Int, piece: Tetromino): Boolean {
        for (row in 0 until piece.height) {
            for (col in 0 until piece.width) {
                if (piece.shape[row][col] == 1) {
                    val gridX = x + col
                    val gridY = y + row

                    if (gridX < 0 || gridX >= gridWidth || gridY < 0 || gridY >= gridHeight) {
                        return false
                    }

                    if (grid[gridY][gridX] == 1) {
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun placePiece() {
        for (row in 0 until currentPiece!!.height) {
            for (col in 0 until currentPiece!!.width) {
                if (currentPiece!!.shape[row][col] == 1) {
                    grid[currentY + row][currentX + col] = 1
                }
            }
        }
    }

    private fun removeFullLines(): Int {
        var linesRemovedCount = 0
        for (row in gridHeight - 1 downTo 0) {
            if (isLineFull(row)) {
                removeLine(row)
                linesRemovedCount++
            }
        }
        return linesRemovedCount
    }

    private fun isLineFull(row: Int): Boolean {
        for (col in 0 until gridWidth) {
            if (grid[row][col] == 0) {
                return false
            }
        }
        return true
    }

    private fun removeLine(rowToRemove: Int) {
        for (row in rowToRemove downTo 1) {
            grid[row] = grid[row - 1].copyOf()
        }
        grid[0] = IntArray(gridWidth) { 0 }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // Calculate block size based on the smallest dimension to avoid lines going out of bounds
        blockSize = Math.min(viewWidth / gridWidth, viewHeight / gridHeight)

        // Calculate horizontal and vertical offset for centering
        val horizontalOffset = (viewWidth - gridWidth * blockSize) / 2
        val verticalOffset = (viewHeight - gridHeight * blockSize) / 2

        // Draw the grid lines
        for (row in 0..gridHeight) {
            canvas.drawLine(
                horizontalOffset,
                verticalOffset + row * blockSize,
                horizontalOffset + gridWidth * blockSize,
                verticalOffset + row * blockSize,
                gridLinePaint
            )
        }
        for (col in 0..gridWidth) {
            canvas.drawLine(
                horizontalOffset + col * blockSize,
                verticalOffset,
                horizontalOffset + col * blockSize,
                verticalOffset + gridHeight * blockSize,
                gridLinePaint
            )
        }

        // Draw the grid
        for (row in 0 until gridHeight) {
            for (col in 0 until gridWidth) {
                if (grid[row][col] == 1) {
                    paint.color = Color.DKGRAY
                    canvas.drawRect(
                        horizontalOffset + col * blockSize,
                        verticalOffset + row * blockSize,
                        horizontalOffset + (col + 1) * blockSize,
                        verticalOffset + (row + 1) * blockSize,
                        paint
                    )
                }
            }
        }

        // Draw the current piece
        if (currentPiece != null) {
            paint.color = Color.BLUE
            for (row in 0 until currentPiece!!.height) {
                for (col in 0 until currentPiece!!.width) {
                    if (currentPiece!!.shape[row][col] == 1) {
                        canvas.drawRect(
                            horizontalOffset + (currentX + col) * blockSize,
                            verticalOffset + (currentY + row) * blockSize,
                            horizontalOffset + (currentX + col + 1) * blockSize,
                            verticalOffset + (currentY + row + 1) * blockSize,
                            paint
                        )
                    }
                }
            }
        }

        // Draw the score at the top center
        val scoreText = "Score: $score"
        canvas.drawText(scoreText, width / 2f, scorePaint.textSize, scorePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (gridWidth * blockSize).toInt()
        val desiredHeight = (gridHeight * blockSize).toInt()

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }
}