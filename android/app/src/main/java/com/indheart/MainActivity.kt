// MainActivity.kt 
package com.indheart


import android.app.NotificationChannel
import android.app.NotificationManager

import android.os.Build
import android.os.Bundle

import android.content.Context
import androidx.core.app.NotificationCompat

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

import expo.modules.ReactActivityDelegateWrapper

class MainActivity : ReactActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    // Set the theme to AppTheme BEFORE onCreate to support
    // coloring the background, status bar, and navigation bar.
    // This is required for expo-splash-screen.
    setTheme(R.style.AppTheme);
    super.onCreate(null)
    // Create notification channels
        createNotificationChannels()
  }


   private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = 
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Urgent channel for heads-up notifications
            NotificationChannel(
                "urgent",
                "Urgent Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority notifications that require immediate attention"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                notificationManager.createNotificationChannel(this)
            }

            // Default channel for regular notifications
            NotificationChannel(
                "default",
                "Default Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Regular notifications"
                setShowBadge(true)
                notificationManager.createNotificationChannel(this)
            }
        }
    }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "main"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate {
    return ReactActivityDelegateWrapper(
          this,
          BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,
          object : DefaultReactActivityDelegate(
              this,
              mainComponentName,
              fabricEnabled
          ){})
  }

  /**
    * Align the back button behavior with Android S
    * where moving root activities to background instead of finishing activities.
    * @see <a href="https://developer.android.com/reference/android/app/Activity#onBackPressed()">onBackPressed</a>
    */
  override fun invokeDefaultOnBackPressed() {
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
          if (!moveTaskToBack(false)) {
              // For non-root activities, use the default implementation to finish them.
              super.invokeDefaultOnBackPressed()
          }
          return
      }

      // Use the default back button implementation on Android S
      // because it's doing more than [Activity.moveTaskToBack] in fact.
      super.invokeDefaultOnBackPressed()
  } // Add this closing brace

  fun setBadge(context: Context, count: Int) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val notification = NotificationCompat.Builder(context, "default")
          .setContentTitle("New Notifications")
          .setContentText("You have $count unread notifications.")
          .setSmallIcon(R.drawable.rn_edit_text_material)  // Replace with your app's notification icon
          .setNumber(count)  // Set the badge count
          .build()

      notificationManager.notify(0, notification)
  }
}