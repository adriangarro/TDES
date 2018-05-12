package DES

/**
  *
  * Created by E. Adrián Garro Sánchez on 22/04/18.
  * Instituto Tecnológico de Costa Rica.
  *
  */

object TDES {

  def encrypt(key1: String, key2: String, key3: String, plainText: String): String = {
    val eK1 = new DES(key1, plainText)
    val dK2 = new DES(key2, eK1.run(), false)
    val eK3 = new DES(key3, dK2.run())
    val cipherText = eK3.run()
    cipherText
  }

  def decrypt(key1: String, key2: String, key3: String, cipherText: String): String = {
    val dK3 = new DES(key3, cipherText, false)
    val eK2 = new DES(key2, dK3.run())
    val dK1 = new DES(key1, eK2.run(), false)
    val plainText = dK1.run()
    plainText
  }

}
