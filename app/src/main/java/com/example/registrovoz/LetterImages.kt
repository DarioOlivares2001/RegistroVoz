package com.example.registrovoz

import com.example.registrovoz.R

object LetterImages {
    fun getImageResource(letter: String): Int {
        return when (letter) {
            "A" -> R.drawable.letra_a
            "B" -> R.drawable.letra_b
            "C" -> R.drawable.letra_c
            "D" -> R.drawable.letra_d
            "E" -> R.drawable.letra_e
            "F" -> R.drawable.letra_f
            "G" -> R.drawable.letra_g
            "H" -> R.drawable.letra_h
            "I" -> R.drawable.letra_i
            "J" -> R.drawable.letra_j
            "K" -> R.drawable.letra_k
            "L" -> R.drawable.letra_l
            "M" -> R.drawable.letra_m
            "N" -> R.drawable.letra_n
            "O" -> R.drawable.letra_o
            "P" -> R.drawable.letra_p
            "Q" -> R.drawable.letra_q
            "R" -> R.drawable.letra_r
            "S" -> R.drawable.letra_s
            "T" -> R.drawable.letra_t
            "U" -> R.drawable.letra_u
            "V" -> R.drawable.letra_v
            "W" -> R.drawable.letra_w
            "X" -> R.drawable.letra_x
            "Y" -> R.drawable.letra_y
            "Z" -> R.drawable.letra_z
            else -> R.drawable.letra_a // Valor por defecto
        }
    }
}
