# Basic proguard rules
-keep class com.mults.parser.** { *; }
-keep class org.jsoup.** { *; }

# Keep - Applications. Keep all application classes, along with their 'main'
# method.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}