# Regras ProGuard/R8 do projeto.
# kotlinx.serialization: manter @Serializable
-keepattributes *Annotation*, InnerClasses
-keep,includedescriptorclasses class com.example.damfp.**$$serializer { *; }
-keepclassmembers class com.example.damfp.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.damfp.** {
    kotlinx.serialization.KSerializer serializer(...);
}
