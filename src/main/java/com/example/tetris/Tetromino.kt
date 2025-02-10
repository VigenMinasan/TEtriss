package com.example.tetris

import java.util.Random

class Tetromino(val shape: Array<IntArray>) {
    val width: Int = shape[0].size
    val height: Int = shape.size

    fun rotateRight(): Tetromino {
        val newShape = Array(width) { IntArray(height) }
        for (row in 0 until height) {
            for (col in 0 until width) {
                newShape[col][height - 1 - row] = shape[row][col]
            }
        }
        return Tetromino(newShape)
    }

    companion object {
        private val tetrominoes = arrayOf(
            Tetromino(arrayOf(intArrayOf(1, 1), intArrayOf(1, 1))),  // O
            Tetromino(arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 1), intArrayOf(0, 0, 0))),  // T
            Tetromino(arrayOf(intArrayOf(0, 1, 1), intArrayOf(1, 1, 0), intArrayOf(0, 0, 0))),  // Z
            Tetromino(arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 1), intArrayOf(0, 0, 0))),  // S
            Tetromino(arrayOf(intArrayOf(1), intArrayOf(1), intArrayOf(1), intArrayOf(1))),      // I
            Tetromino(arrayOf(intArrayOf(1, 0), intArrayOf(1, 0), intArrayOf(1, 1))),  // J
            Tetromino(arrayOf(intArrayOf(0, 1), intArrayOf(0, 1), intArrayOf(1, 1)))   // L
        )

        private val random = Random()

        fun getRandomTetromino(): Tetromino {
            return tetrominoes[random.nextInt(tetrominoes.size)]
        }
    }
}