package com.chains.larp.utils

import android.util.Log
import java.io.UnsupportedEncodingException

class ByteUtils {
    companion object {
        fun isNullOrEmpty(array: ByteArray?): Boolean {
            if (array == null) {
                return true
            }
            val length = array.size
            for (i in 0 until length) {
                if (array[i].toInt() != 0) {
                    return false
                }
            }

            return true
        }


        /**
         * Convert an array of bytes into a string of hex values.
         * @param bytes Bytes to convert.
         * @return The bytes in hex string format.
         */
        fun byte2Hex(bytes: ByteArray?): String? {
            val ret = StringBuilder()
            if (bytes != null) {
                for (b in bytes) {
                    ret.append(String.format("%02X", b.toInt() and 0xFF))
                }
            }
            return ret.toString()
        }

        /**
         * Convert a string of hex data into a byte array.
         * Original author is: Dave L. (http://stackoverflow.com/a/140861).
         * @param hex The hex string to convert
         * @return An array of bytes with the values of the string.
         */
        fun hex2ByteArray(hex: String?): ByteArray? {
            if (!(hex != null && hex.length % 2 == 0 && hex.matches(Regex.fromLiteral("[0-9A-Fa-f]+")))
            ) {
                return null
            }
            val len = hex.length
            val data = ByteArray(len / 2)
            try {
                var i = 0
                while (i < len) {
                    data[i / 2] = ((Character.digit(hex[i], 16) shl 4)
                            + Character.digit(hex[i + 1], 16)).toByte()
                    i += 2
                }
            } catch (e: Exception) {
                Log.d(
                    "ByteUtils", "Argument(s) for hexStringToByteArray(String s)"
                            + "was not a hex string"
                )
            }
            return data
        }
    }
}