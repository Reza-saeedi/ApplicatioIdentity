package io.github.rajdeep1008.apkwizard.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import io.github.rajdeep1008.apkwizard.MainActivity
import io.github.rajdeep1008.apkwizard.models.Apk
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * Created by rajdeep1008 on 22/04/18.
 */
class Utilities {

    companion object {

        const val SORT_ORDER_INSTALLATION_DATE = 1
        const val STORAGE_PERMISSION_CODE = 1008
        const val PREF_SORT_KEY = "sort_order"
        const val SORT_ORDER_UPDATE_DATE = 2
        const val SORT_ORDER_NAME = 0
        const val SORT_ORDER_SIZE = 3

        fun checkPermission(activity: AppCompatActivity): Boolean {
            var permissionGranted = false

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    val rootView: View = (activity as MainActivity).window.decorView.findViewById(android.R.id.content)
                    Snackbar.make(rootView, "Storage permission required", Snackbar.LENGTH_LONG)
                            .setAction("Allow") {
                                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                            }
                            .setActionTextColor(Color.WHITE)
                            .show()
                } else {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                }
            } else {
                permissionGranted = true
            }

            return permissionGranted
        }

        private fun checkExternalStorage(): Boolean = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)

        private fun getAppFolder(): File? {
            var file: File? = null
            if (checkExternalStorage()) {
                file = File(Environment.getExternalStorageDirectory(), "ApkWizard")
                return file
            }
            return file
        }

        fun makeAppDir() {
            val file = getAppFolder()
            if (file != null && !file.exists()) {
                file.mkdir()
            }
        }

        fun extractApk(apk: Apk): Boolean {
            makeAppDir()
            var extracted = true
            val originalFile = File(apk.sourceDir)
            val extractedFile: File = getApkFile(apk)

            try {
                FileUtils.copyFile(originalFile, extractedFile)
                extracted = true
            } catch (e: Exception) {
                Log.d("test", "problem - " + e.message)
            }

            return extracted
        }

        private fun getApkFile(apk: Apk): File {
            val fileName = getAppFolder()?.path + File.separator + apk.appName + "_" + apk.version + ".apk"
            return File(fileName)
        }

        fun getShareableIntent(apk: Apk): Intent {
            extractApk(apk)
            val file = getApkFile(apk)
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            shareIntent.type = "application/vnd.android.package-archive"
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            return shareIntent
        }

        fun updateSortOrder(context: Context, option: Int) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            preferences.edit().putInt(PREF_SORT_KEY, option).apply()
        }
    }
}