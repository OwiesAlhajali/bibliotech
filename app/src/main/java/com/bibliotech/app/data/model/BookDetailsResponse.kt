package com.bibliotech.app.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class BookDetailsResponse(
    @SerializedName("title") val title: String?,
    @SerializedName("number_of_pages") val numberOfPages: Int?,
    @SerializedName("description")
    @JsonAdapter(DescriptionDeserializer::class)
    val description: String?
)

// 🔥 محول ذكي لأن الـ API أحياناً يرجع الوصف كنص وأحياناً كـ Object 🔥
class DescriptionDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): String {
        return if (json.isJsonObject) {
            json.asJsonObject.get("value")?.asString ?: ""
        } else if (json.isJsonPrimitive) {
            json.asString
        } else {
            ""
        }
    }
}