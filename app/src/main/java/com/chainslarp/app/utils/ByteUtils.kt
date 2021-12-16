package com.chainslarp.app.utils

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

        /**
         * Convert a hex string to ASCII string.
         * @param hex Hex string to convert.
         * @return Converted ASCII string. Null on error.
         */
        fun hex2Ascii(hex: String?): String? {
            if (!(hex != null && hex.length % 2 == 0 && hex.matches(Regex.fromLiteral("[0-9A-Fa-f]+")))
            ) {
                return null
            }
            val bytes = hex2ByteArray(hex)
            var ret: String? = null
            // Replace non printable ASCII with ".".
            for (i in bytes!!.indices) {
                if (bytes[i] < 0x20.toByte() || bytes[i] == 0x7F.toByte()) {
                    bytes[i] = 0x2E.toByte()
                }
            }
            // Hex to ASCII.
            try {
                ret = String(bytes, Charsets.US_ASCII)
            } catch (e: UnsupportedEncodingException) {
                Log.e("ByteUtils", "Error while encoding to ASCII", e)
            }
            return ret
        }

        /**
         * Convert a ASCII string to a hex string.
         * @param ascii ASCII string to convert.
         * @return Converted hex string.
         */
        fun ascii2Hex(ascii: String?): String? {
            if (!(ascii != null && ascii != "")) {
                return null
            }
            val chars = ascii.toCharArray()
            val hex = java.lang.StringBuilder()
            for (aChar in chars) {
                hex.append(String.format("%02X", aChar.toInt()))
            }
            return hex.toString()
        }

    }
}