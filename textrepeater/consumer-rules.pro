-keep class com.lib.textrepeater.callback.** { *; }
-keep class com.lib.textrepeater.database.entitity.** { *; }
-keep class com.lib.textrepeater.database.database.** { *; }
-keep class com.lib.textrepeater.database.dao.** { *; }

-keepnames class androidx.lifecycle.ViewModel -keepclassmembers public class * extends androidx.lifecycle.ViewModel { public <init>(...); }
-keepclassmembers class * { public <init>(...); }

-keep class * extends androidx.room.RoomDatabase