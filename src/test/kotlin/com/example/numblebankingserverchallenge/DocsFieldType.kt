package com.example.numblebankingserverchallenge

import org.springframework.restdocs.payload.JsonFieldType
import kotlin.reflect.KClass

sealed class DocsFieldType(val type:JsonFieldType)
object ARRAY: DocsFieldType(JsonFieldType.ARRAY)
object OBJECT:DocsFieldType(JsonFieldType.OBJECT)
object BOOLEAN:DocsFieldType(JsonFieldType.BOOLEAN)
object NUMBER:DocsFieldType(JsonFieldType.NUMBER)
object NULL:DocsFieldType(JsonFieldType.NULL)
object STRING:DocsFieldType(JsonFieldType.STRING)
object ANY: DocsFieldType(JsonFieldType.VARIES)
object DATE:DocsFieldType(JsonFieldType.STRING)
object DATETIME:DocsFieldType(JsonFieldType.STRING)
data class ENUM<T : Enum<T>>(val enums: Collection<T>) : DocsFieldType(JsonFieldType.STRING) {
    constructor(clazz: KClass<T>) : this(clazz.java.enumConstants.asList())   // (1)
}