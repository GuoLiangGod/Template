package com.guoliang.frame.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.guoliang.frame.R
import com.guoliang.frame.util.permission_observable.PermissionObserver
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.indices
import kotlin.collections.set
import kotlin.collections.toTypedArray

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/12/10 17:02
 */
object PermissionManage {
    private const val REQUEST_CODE = 666
    private val permissionObserverMap = HashMap<String, PermissionObserver>()

    /**
     * 检测权限
     */
    fun checkPermission(context: Context, vararg permissions: String): Boolean {
        val permissionsList: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission)
            }
        }
        return permissionsList.isEmpty()
    }

    fun applyPermission(activity: Activity, vararg permissions: String) {
        val permissionsList: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q&&permission== Manifest.permission.READ_PHONE_STATE){
                continue
            }
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission)
            }
        }
        if (permissionsList.isEmpty()) {
            permissionObserverMap[activity.toString()]?.authorizationSuccess()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                permissionsList.toTypedArray(),
                REQUEST_CODE
            )
        }
    }

    fun applyPermission(fragment: Fragment, vararg permissions: String) {
        val permissionsList: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q&&permission== Manifest.permission.READ_PHONE_STATE){
                continue
            }
            if (ContextCompat.checkSelfPermission(fragment.activity!!, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission)
            }
        }
        if (permissionsList.isEmpty()) {
            permissionObserverMap[fragment.toString()]?.authorizationSuccess()
        } else {
            fragment.requestPermissions(permissionsList.toTypedArray(), REQUEST_CODE)
        }
    }
    
    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionMap: MutableMap<String, String> = HashMap()
        val firstPermissionMap: MutableMap<String, Int> = HashMap()
        if (requestCode == REQUEST_CODE) {
            for (i in grantResults.indices) {
                firstPermissionMap[permissions[i]] = grantResults[i]
                //判断是否成功
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    //判断是否有禁止选项
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            permissions[i]
                        )) {
                        permissionMap[permissions[i]] = "forbid"
                    } else {
                        permissionMap[permissions[i]] = "allow"
                    }
                }
            }
        }
        if (permissionMap.isEmpty()) {
            permissionObserverMap[activity.toString()]?.authorizationSuccess()
        } else {
            if (permissionMap.containsValue("forbid")) {
                showPermissionDialog(activity)
            } else {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.please_permission),
                    Toast.LENGTH_LONG
                ).show()
                permissionObserverMap[activity.toString()]?.authorizationFailure()
            }
        }
        for (entry in permissionObserverMap.entries) {
            permissionObserverMap[entry.key]?.firstAuthorization(firstPermissionMap)
        }
    }

    fun onRequestPermissionsResult(
        fragment: Fragment,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionMap: MutableMap<String, String> = HashMap()
        val firstPermissionMap: MutableMap<String, Int> = HashMap()
        if (requestCode == REQUEST_CODE) {
            for (i in grantResults.indices) {
                firstPermissionMap[permissions[i]] = grantResults[i]
                //判断是否成功
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    //判断是否有禁止选项
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            fragment.activity!!,
                            permissions[i]
                        )) {
                        permissionMap[permissions[i]] = "forbid"
                    } else {
                        permissionMap[permissions[i]] = "allow"
                    }
                }
            }
        }
        if (permissionMap.isEmpty()) {
            permissionObserverMap[fragment.toString()]?.authorizationSuccess()
        } else {
            if (permissionMap.containsValue("forbid")) {
                showPermissionDialog(fragment)
            } else {
                Toast.makeText(
                    fragment.activity,
                    fragment.activity!!.getString(R.string.please_permission),
                    Toast.LENGTH_LONG
                ).show()
                permissionObserverMap[fragment.toString()]?.authorizationFailure()
            }
        }
        for (entry in permissionObserverMap.entries) {
            permissionObserverMap[entry.key]?.firstAuthorization(firstPermissionMap)
        }
    }


    private fun showPermissionDialog(activity: Activity) {
        AlertDialog.Builder(activity).setMessage(activity.getString(R.string.disable_the_prompt))
                .setPositiveButton(R.string.setting) { dialog, which ->
                    dialog.cancel()
                    val packageURI = Uri.parse("package:" + activity.packageName)
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                    activity.startActivity(intent)
                }.setNegativeButton(R.string.cancel) { dialog, which -> dialog.cancel() }
                .setOnCancelListener {
                    permissionObserverMap[activity.toString()]?.authorizationFailure()
                }.create().show()
    }


    private fun showPermissionDialog(fragment: Fragment) {
        AlertDialog.Builder(fragment.activity!!).setMessage(fragment.activity!!.getString(R.string.disable_the_prompt))
                .setPositiveButton(R.string.setting) { dialog, which ->
                    dialog.cancel()
                    val packageURI = Uri.parse("package:" + fragment.activity!!.packageName)
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                    fragment.activity!!.startActivity(intent)
                }.setNegativeButton(R.string.cancel) { dialog, which -> dialog.cancel() }
                .setOnCancelListener {
                    permissionObserverMap[fragment.toString()]?.authorizationFailure()
                }.create().show()
    }
    

    fun setOnPermissionsListener(fragment: Fragment, permissionObserver: PermissionObserver): PermissionManage {
        permissionObserverMap[fragment.toString()] = permissionObserver
        return this
    }

    fun setOnPermissionsListener(activity: Activity, permissionObserver: PermissionObserver): PermissionManage {
        permissionObserverMap[activity.toString()] = permissionObserver
        return this
    }

    fun setOnPermissionsListener(
        activity: Activity, authorizationSuccess: (() -> Unit)? = null,
        authorizationFailure: (() -> Unit)? = null,
        firstAuthorization: ((map: Map<String, Int>) -> Unit)? = null
    ): PermissionManage {
        val permissionObserver = object : PermissionObserver {
            override fun authorizationSuccess() {
                authorizationSuccess?.invoke()
            }

            override fun authorizationFailure() {
                authorizationFailure?.invoke()
            }

            override fun firstAuthorization(map: Map<String, Int>) {
                firstAuthorization?.invoke(map)
            }
        }
        setOnPermissionsListener(activity, permissionObserver)
        return this
    }

    fun setOnPermissionsListener(
        fragment: Fragment, authorizationSuccess: (() -> Unit)? = null,
        authorizationFailure: (() -> Unit)? = null,
        firstAuthorization: ((map: Map<String, Int>) -> Unit)? = null
    ): PermissionManage {
        val permissionObserver = object : PermissionObserver {
            override fun authorizationSuccess() {
                authorizationSuccess?.invoke()
            }

            override fun authorizationFailure() {
                authorizationFailure?.invoke()
            }

            override fun firstAuthorization(map: Map<String, Int>) {
                firstAuthorization?.invoke(map)
            }
        }
        setOnPermissionsListener(fragment, permissionObserver)
        return this
    }
}