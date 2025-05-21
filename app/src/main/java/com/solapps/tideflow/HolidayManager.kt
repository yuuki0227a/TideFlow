package com.solapps.tideflow

import kotlinx.serialization.json.Json
import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate

object HolidayManager {

    /* ファイル名: holiday_yyyy.txt */
    private const val FILE_NAME_BASE = "holiday"
    private const val FOLDER_NAME = FILE_NAME_BASE
    private const val FILE_NAME_FORMAT = "%s_%d.txt"

    /**
     * 最新のholidayファイル名を返す。
     * */
    private fun getFileName(): String{
        return String.format(
            FILE_NAME_FORMAT,
            FILE_NAME_BASE,
            LocalDate.now().year
        )
    }
    /**
     * holidayファイルのパスを返す。
     * @param context コンテキスト
     * */
    private fun getFilePath(context: Context): String{
        return File(getHolidayFolder(context), getFileName()).path
    }
    /**
     * fileDir直下にholidayファルダを作成する。
     * @param context コンテキスト
     * @return フォルダのパス
     * */
    private fun getHolidayFolder(context: Context): String {
        val holidayFolder = File(context.filesDir, FOLDER_NAME)
        if (!holidayFolder.exists()) {
            holidayFolder.mkdir()
        }
        return holidayFolder.path
    }

    /**
     * フォルダ内のファイルをすべて削除する。
     * @param context コンテキスト
     * @param folderName 対象のフォルダ名
     * @return 削除成功(true) or 失敗(false)
     * */
    private fun deleteAllFilesInFolder(context: Context, folderName: String): Boolean {
        val folder = File(context.filesDir, folderName)
        if (folder.exists() && folder.isDirectory) {
            val files = folder.listFiles()
            files?.forEach { file ->
                if (file.isFile) {
                    file.delete()
                }
            }

            return true
        }
        return false
    }

    /**
     * JSONデータをMAPにする。
     * */
    fun parseHolidayData(jsonString: String): MutableMap<String, String> {
        return Json.decodeFromString(jsonString)
    }

    /**
     * ファイル出力
     * @param context コンテキスト
     * @param holidays 祝日の日付と祝日名のマップ
     * */
    fun writeToFile(context: Context, holidays: MutableMap<String, String>) {
        // フォルダ内のファイルをすべて削除する。
        deleteAllFilesInFolder(context, FOLDER_NAME)
        val file = File(getFilePath(context))
        val stringBuffer = StringBuffer()
        holidays.forEach { (date, name) ->
            stringBuffer.append("$date,$name\n")
        }
        try {
            FileOutputStream(file).use { output ->
                output.write(stringBuffer.toString().toByteArray())
            }
            println("File written successfully!")
        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to write file")
        }
    }
    /**
     * ファイル読み込み
     * @param context コンテキスト
     * @return 祝日の日付と祝日名のマップ
     * */
    fun readFromFile(context: Context): MutableMap<String, String>{
        val holidays: MutableMap<String, String> = mutableMapOf()
        val file = File(getFilePath(context))
        // ファイルの存在確認
        if(!file.exists()){
            return holidays
        }
        try {
            val inputStr = FileInputStream(file).use { input ->
                input.readBytes().toString(Charsets.UTF_8)
            }
            for(str in inputStr.lines()){
                if(str.isNotEmpty()){
                    val splitStr = str.split(",")
                    holidays[splitStr[0]] = splitStr[1]
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return holidays
    }

    /**
     * holiday.txtの存在確認
     * */
    fun isExistsHolidayFile(context: Context): Boolean{
        val file = File(getFilePath(context))
        return file.exists()
    }
}