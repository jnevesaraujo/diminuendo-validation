package dam.a50274.diminuendo.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromIntArray(value: IntArray?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toIntArray(value: String?): IntArray {
        if (value.isNullOrEmpty()) return IntArray(0)
        return value.split(",").map { it.toInt() }.toIntArray()
    }

    @TypeConverter
    fun fromDoubleList(value: List<Double>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toDoubleList(value: String?): List<Double> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",").map { it.toDouble() }
    }
}
