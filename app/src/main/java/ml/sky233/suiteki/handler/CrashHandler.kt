package ml.sky233.choseki.handler

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Process
import android.util.Log
import ml.sky233.suiteki.MainApplication.Companion.context


import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date

/**
 * Create by ChenHao on 2018/6/299:30
 * use : 应用异常处理类
 * 使用方式： 在Application 中初始化  CrashHandler.getInstance().init(this);
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    private var mDefaultCrashHandler: Thread.UncaughtExceptionHandler? = null
    private var mContext: Context? = null

    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context) {
        //得到系统的应用异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        //将当前应用异常处理器改为默认的
        Thread.setDefaultUncaughtExceptionHandler(this)
        mContext = context.applicationContext
    }

    /**
     * 这个是最关键的函数，当系统中有未被捕获的异常，系统将会自动调用 uncaughtException 方法
     *
     * @param thread 为出现未捕获异常的线程
     * @param ex     为未捕获的异常 ，可以通过e 拿到异常信息
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        //导入异常信息到SD卡中
        try {
            dumpExceptionToSDCard(ex)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //这里可以上传异常信息到服务器，便于开发人员分析日志从而解决Bug
        uploadExceptionToServer()
        ex.printStackTrace()
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler!!.uncaughtException(thread, ex)
        } else {
            Process.killProcess(Process.myPid())
        }
    }

    /**
     * 将异常信息写入SD卡
     *
     * @param e
     */
    @Throws(IOException::class)
    private fun dumpExceptionToSDCard(e: Throwable) {
        //如果SD卡不存在或无法使用，则无法将异常信息写入SD卡
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            if (DEBUG) {
                Log.w(TAG, "sdcard unmounted,skip dump exception")
                return
            }
        }
        val dir = File(PATH)
        //如果目录下没有文件夹，就创建文件夹
        if (!dir.exists()) {
            dir.mkdirs()
        }
        //得到当前年月日时分秒
        val current = System.currentTimeMillis()
        @SuppressLint("SimpleDateFormat") val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
            Date(current)
        )
        //在定义的Crash文件夹下创建文件
        val file = File(PATH + FILE_NAME + current + FILE_NAME_SUFFIX)
        try {
            val pw = PrintWriter(BufferedWriter(FileWriter(file)))
            //写入时间
            pw.println(time)
            //写入手机信息
            dumpPhoneInfo(pw)
            pw.println("-------------------------") //换行
            e.printStackTrace(pw)
            pw.close() //关闭输入流
        } catch (e1: Exception) {
            Log.e(TAG, "dump crash info failed")
        }
    }

    /**
     * 获取手机各项信息
     *
     * @param pw
     */
    @Throws(PackageManager.NameNotFoundException::class)
    private fun dumpPhoneInfo(pw: PrintWriter) {
        //得到包管理器
        val pm = mContext!!.packageManager
        //得到包对象
        val pi = pm.getPackageInfo(mContext!!.packageName, PackageManager.GET_ACTIVITIES)
        //写入APP版本号
        pw.print("App Version: ")
        pw.print(pi.versionName)
        pw.print("_")
        pw.println(pi.versionCode)
        //写入 Android 版本号
        pw.print("OS Version: ")
        pw.print(Build.VERSION.RELEASE)
        pw.print("_")
        pw.println(Build.VERSION.SDK_INT)
        //手机制造商
        pw.print("Vendor: ")
        pw.println(Build.MANUFACTURER)
        //手机型号
        pw.print("Model: ")
        pw.println(Build.MODEL)
        //CPU架构
        pw.print("CPU ABI: ")
        pw.println(Arrays.toString(Build.SUPPORTED_ABIS))
    }

    /**
     * 将错误信息上传至服务器
     */
    private fun uploadExceptionToServer() {
        //没钱买服务器,看什么看
    }

    companion object {
        private const val TAG = "CrashHandler"
        const val DEBUG = true

        /**
         * 文件名
         */
        const val FILE_NAME = "crashLog"

        /**
         * 异常日志 存储位置为根目录下的 Crash文件夹
         */
        private val PATH = context.getExternalFilesDir("")!!
            .path +
                "/Crash/"

        /**
         * 文件名后缀
         */
        private const val FILE_NAME_SUFFIX = ".txt"
        @SuppressLint("StaticFieldLeak")
        val instance = CrashHandler()
    }
}