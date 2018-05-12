package DES

import scala.collection.immutable
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

/**
  *
  * Created by E. Adrián Garro Sánchez on 22/04/18.
  * Instituto Tecnológico de Costa Rica.
  *
  * All supplementary data gotten from Wikipedia:
  * http://en.wikipedia.org/wiki/DES_supplementary_material
  *
  */
class DES(var password: String, var text: String,
          var encrypt: Boolean = true,
          var keys: ArrayBuffer[Array[Int]] = ArrayBuffer()) {

  // Initial permutation for the input data.
  private val IP = Array(
    58, 50, 42, 34, 26, 18, 10, 2,
    60, 52, 44, 36, 28, 20, 12, 4,
    62, 54, 46, 38, 30, 22, 14, 6,
    64, 56, 48, 40, 32, 24, 16, 8,
    57, 49, 41, 33, 25, 17, 9, 1,
    59, 51, 43, 35, 27, 19, 11, 3,
    61, 53, 45, 37, 29, 21, 13, 5,
    63, 55, 47, 39, 31, 23, 15, 7
  )

  // Final permutation for data after the 16 rounds.
  private val FP = Array(
    40, 8, 48, 16, 56, 24, 64, 32,
    39, 7, 47, 15, 55, 23, 63, 31,
    38, 6, 46, 14, 54, 22, 62, 30,
    37, 5, 45, 13, 53, 21, 61, 29,
    36, 4, 44, 12, 52, 20, 60, 28,
    35, 3, 43, 11, 51, 19, 59, 27,
    34, 2, 42, 10, 50, 18, 58, 26,
    33, 1, 41, 9, 49, 17, 57, 25
  )

  // Expansion function to get a 48 bits of data to apply the XOR with K_i.
  private val E = Array(
    32, 1, 2, 3, 4, 5,
    4, 5, 6, 7, 8, 9,
    8, 9, 10, 11, 12, 13,
    12, 13, 14, 15, 16, 17,
    16, 17, 18, 19, 20, 21,
    20, 21, 22, 23, 24, 25,
    24, 25, 26, 27, 28, 29,
    28, 29, 30, 31, 32, 1
  )

  // Permutation made after each SBox substitution for each round.
  private val P = Array(
    16, 7, 20, 21, 29, 12, 28, 17,
    1, 15, 23, 26, 5, 18, 31, 10,
    2, 8, 24, 14, 32, 27, 3, 9,
    19, 13, 30, 6, 22, 11, 4, 25
  )

  // Initial permutations made on the key.
  private val PC1 = Array(
    57, 49, 41, 33, 25, 17, 9, // Left Half.
    1, 58, 50, 42, 34, 26, 18,
    10, 2, 59, 51, 43, 35, 27,
    19, 11, 3, 60, 52, 44, 36, // Right Half.
    63, 55, 47, 39, 31, 23, 15,
    7, 62, 54, 46, 38, 30, 22,
    14, 6, 61, 53, 45, 37, 29,
    21, 13, 5, 28, 20, 12, 4
  )

  // Permutation applied on shifted key to get K_i+1.
  private val PC2 = Array(
    14, 17, 11, 24, 1, 5,
    3, 28, 15, 6, 21, 10,
    23, 19, 12, 4, 26, 8,
    16, 7, 27, 20, 13, 2,
    41, 52, 31, 37, 47, 55,
    30, 40, 51, 45, 33, 48,
    44, 49, 39, 56, 34, 53,
    46, 42, 50, 36, 29, 32
  )

  private val SBoxes =  HashMap(
    // S1
    0 -> Array(
      Array(14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7),
      Array(0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8),
      Array(4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0),
      Array(15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13)
    ),
    // S2
    1 -> Array(
      Array(15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10),
      Array(3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5),
      Array(0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15),
      Array(13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9)
    ),
    // S3
    2 -> Array(
      Array(10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8),
      Array(13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1),
      Array(13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7),
      Array(1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12)
    ),
    // S4
    3 -> Array(
      Array(7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15),
      Array(13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9),
      Array(10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4),
      Array(3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14)
    ),
    // S5
    4 -> Array(
      Array(2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9),
      Array(14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6),
      Array(4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14),
      Array(11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3)
    ),
    // S6
    5 -> Array(
      Array(12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11),
      Array(10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8),
      Array(9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6),
      Array(4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13)
    ),
    // S7
    6-> Array(
      Array(4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1),
      Array(13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6),
      Array(1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2),
      Array(6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12)
    ),
    // S8
    7-> Array(
      Array(13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7),
      Array(1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2),
      Array(7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8),
      Array(2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11)
    )
  )
  // Determine the shift for each round of keys.
  private val SHIFT = Array(1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1)

  /*
   * Run DES Algorithm.
   */
  def run(): String = {
    if (password.length < 8) {
      throw new Exception("Key Should be 8 bytes long.")
    }
    // If key size is above 8 bytes, cut to be 8 bytes long.
    else if (password.length > 8) {
      password = password.slice(0, 8)
    }
    // Data size must be multiple of 8 bytes.
    if (text.length % 8 != 0) {
      addPadding()
    }
    // Generate all the keys.
    genKeys()
    // Split the text in blocks of 8 bytes i.e. 64 bits.
    val textBlocks = text.grouped(8)
    var result: Array[Int] = Array()
    // Loop over all the blocks of data.
    for (block <- textBlocks) {
      // Convert the block in bits array.
      var eightBytes: Array[Int] = Tools.stringToBits(block)
      // Apply the initial permutation.
      eightBytes = permute(eightBytes, IP)
      // g(LEFT), d(RIGHT)
      val parts: Array[Array[Int]] = Tools.nSplit(eightBytes, 32)
      var g: Array[Int] = parts(0)
      var d: Array[Int] = parts(1)
      var temp: Array[Int] = Array()
      // Do the 16 rounds, Feistel's network.
      for (i <- 0 to 15) {
        /** Cipher Function **/
        // Expand d to match K_i size (48 bits).
        val dExpanded = permute(d, E)
        // If encrypt use K_i.
        if (encrypt) {
          temp = xor(keys(i), dExpanded)
        }
        // If decrypt start by the last key.
        else {
          temp = xor(keys(15 - i), dExpanded)
        }
        // Method that will apply the SBOXes.
        temp = substitute(temp)
        temp = permute(temp, P)
        /** Cipher Function **/
        // Round XOR
        temp = xor(g, temp)
        // Round Swap
        g = d
        d = temp
      }
      // Do the last permutation and append the result to result.
      result = result ++ permute(d ++ g, FP)
    }
    // Return the final string of data ciphered/deciphered.
    Tools.bitsToString(result)
  }

  /*
   * Add padding to the data using PKCS (Public-Key Cryptography Standards) #5 spec.
   */
  private def addPadding(): Unit = {
    val padLen: Int = 8 - (text.length % 8)
    var count = padLen
    while (count > 0) {
      text += padLen.toChar
      count -= 1
    }
  }

  /*
   * Permutes block using a given permutation.
   */
  private def permute(block: Array[Int], permutation: Array[Int]): Array[Int] = {
    for (i <- permutation) yield {
      block(i-1)
    }
  }

  /*
   * Algorithm that generates all DES keys.
   */
  private def genKeys(): Unit = {
    var key: Array[Int] = Tools.stringToBits(password)
    // Applies the initial permutation on the key, set 56 bits.
    key = permute(key, PC1)
    // Splits it in to (g->LEFT),(d->RIGHT)
    val parts = Tools.nSplit(key, 28)
    // Left Half, 28 bits.
    var g = parts(0)
    // Right Half, 28 bits.
    var d = parts(1)
    // Apply the 16 rounds.
    for (i <- 0 to 15) {
      // Apply the shift associated with the round (not always 1).
      val fissure: Array[Array[Int]] = shift(g, d, SHIFT(i))
      g = fissure(0)
      d = fissure(1)
      // Merge them.
      var mix = g ++ d
      // Applies the second permutation on the mix, set 48 bits.
      mix = permute(mix, PC2)
      // Saves de the K_i key.
      keys += mix
    }
  }

  /*
   * Shift a array of the given value.
   */
  private def shift(g: Array[Int], d: Array[Int], n: Int): Array[Array[Int]] = {
    val fissure = Array(
      g.drop(n) ++ g.take(n),
      d.drop(n) ++ d.take(n)
    )
    fissure
  }

  /*
   * Apply a XOR and return the resulting list.
   */
  private def xor(a1: Array[Int], a2: Array[Int]): Array[Int] = {
    a1.zip(a2).map { case (x, y) => x ^ y }
  }

  private def substitute(dExpanded: Array[Int]): Array[Int] = {
    // Split bits array into sublist of 6 bits.
    val subBlocks: Array[Array[Int]] = Tools.nSplit(dExpanded, 6)
    var result: Array[Int] = Array()
    // For all the sub arrays...
    // Must be 8 loops, because 8 * 6 = 48 and there are 8 SBoxes.
    for (i <- subBlocks.indices) {
      val block: Array[Int] = subBlocks(i)
      // Row is the first and last bit.
      val row: Int = Integer.parseInt(
        block(0).toString + block(5).toString,
        2
      )
      // Column is the 2th, 3th, 4th, 5th bits.
      val column = Integer.parseInt(
        block(1).toString
          + block(2).toString
          + block(3).toString
          + block(4).toString,
        2
      )
      // Take the value in the SBOX appropriated for the round_i.
      val sBoxValue = SBoxes(i)(row)(column)
      // Convert the value to binary.
      val bin: String = Tools.intToBinary(sBoxValue, 4)
      val binArr: immutable.IndexedSeq[Int] = for (char <- bin) yield {
        char.asDigit
      }
      // And append it to the resulting list.
      result = result ++ binArr
    }
    result
  }

}
