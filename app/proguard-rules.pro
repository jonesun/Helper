# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\work\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontshrink
-dontoptimize
-dontpreverify

-dontwarn com.**
-dontwarn org.**
-dontwarn net.**
-dontwarn java.**
-dontwarn javax.**
-dontwarn ch.**
-dontwarn core.**
-dontwarn edu.**


-keep public class * extends Android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.app.IntentService
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class jone.helper.bean.**{*;}
-keepclasseswithmembernames class * {
native <methods>;
}

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}

-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

-dontwarn jone.helper.**
-keep class jone.helper.** { *;}

-keepattributes Signature
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
-keep class jone.helper.lib.bean.** { *; }
-keep class jone.helper.model.weather.entity.** { *; }
-keep class jone.helper.lib.ormlite.entities.** { *; }
-keep public class cn.waps.** {*;}
-keep public interface cn.waps.** {*;}

-dontwarn cn.waps.**

-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

-dontwarn com.umeng.**
-keep class com.umeng.** {*;}