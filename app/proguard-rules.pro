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

-dontwarn android.databinding.**
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
-keep class jone.helper.bean.**{*;}
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

-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

#把[您的应用包名] 替换成您自己的包名，如"com.example.R$*"。
-keep public class jone.helper.R$*{
    public static final int *;
}

#高德地图
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
#高德地图 end

-dontwarn java.lang.invoke.*