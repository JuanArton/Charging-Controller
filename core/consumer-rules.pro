##---------------Begin: proguard configuration for SQLCipher  ----------
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }

-dontwarn com.github.mikephil.**
-keep class com.github.mikephil.charting.animation.ChartAnimator{*;}
