package com.lazyapps.tideflow

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.concurrent.thread

class TideFlowManager {
    companion object {
        private const val TIDE_DATE_YEAR = 0
        private const val TIDE_DATE_MONTH = 1
        private const val TIDE_DATE_DAY = 2
        // %s:年(yyyy) %s:観測地点(OS)
        private const val TIDE_DATA_TXT_URL_FORMAT = "https://www.data.jma.go.jp/kaiyou/data/db/tide/suisan/txt/%s/%s.txt"
        /* データ範囲の定義 */
        private const val HOURLY_TIDE_LEVELS_START = 0
        private const val HOURLY_TIDE_LEVELS_END = 72
        private const val TIDE_DATE_START = HOURLY_TIDE_LEVELS_END
        private const val TIDE_DATE_END = 78
        private const val LOCATION_NAME_START = TIDE_DATE_END
        private const val LOCATION_NAME_END = 80
        private const val HIGH_TIDE_TIMES_START = LOCATION_NAME_END
        private const val HIGH_TIDE_TIMES_END = 108
        private const val LOW_TIDE_TIMES_START = HIGH_TIDE_TIMES_END
        private const val LOW_TIDE_TIMES_END = 136
        /* 固定長の定義 */
        // 時間毎の潮位の列数の固定長
        private const val HOURLY_TIDE_FIXED_COLUMNS = 3
        // 日付の列数の固定長
        private const val TIDE_DATE_FIXED_COLUMNS = 2
        // 観測地点の列数の固定長
        private const val LOCATION_NAME_FIXED_COLUMNS = 2
        // 満潮の列数の固定長
        private const val HIGH_TIDE_TIMES_FIXED_COLUMNS = 7
        // 干潮の列数の固定長
        private const val LOW_TIDE_TIMES_FIXED_COLUMNS = 7
        // 満潮時間の列数の固定長
        private const val HIGH_TIME_FIXED_COLUMNS = 4
        // 満潮の列数の固定長
        private const val HIGH_TIDE_LEVEL_TIME_FIXED_COLUMNS = 3
        // 干潮時間の列数の固定長
        private const val LOW_TIME_FIXED_COLUMNS = 4
        // 干潮の列数の固定長
        private const val LOW_TIDE_LEVEL_TIME_FIXED_COLUMNS = 3
        // 日付データの個数
        private const val DATE_TIMES_MAX = 3
        // 満潮データの個数
        private const val HIGH_TIDE_TIMES_MAX = 4
        // 干潮データの個数
        private const val LOW_TIDE_TIMES_MAX = 4
        // レコードの固定文字数
        private const val RECORD_LENGTH_MAX = 136
        // 潮汐データのファイルネームフォーマット
        private const val TIDE_FILE_TXT_NAME = "%s.txt"
        /* 文字列データから年月日をサブストリングする祭の定数 */
        private const val RECORD_DATE_YEAR_START = 72
        private const val RECORD_DATE_MONTH_START = 74
        private const val RECORD_DATE_DAY_START = 76

        // メモリに取り込むデータの前後範囲
        const val READ_DATA_RANGE = 90

        /* dataList要素の定数 */
        private const val DATA_LIST_BEFORE = 0
        private const val DATA_LIST_CURRENT = 1
        private const val DATA_LIST_AFTER = 2
    }

    /**
     * データ受信完了時のコールバックインターフェース
     * */
    interface DataFetchCallback {
        fun onDataFetched(data: String, fileWriteFinishFlag: Int)
        fun onError(exception: IOException, fileWriteFinishFlag: Int, year: Int)
    }

    /**
     * データ受信完了時にコールバックを呼び出す関数
     * */
    private fun fetchData(context: Context, url: String, callback: DataFetchCallback, fileWriteFinishFlag: Int, year: Int) {
//        Handler(Looper.getMainLooper()).post {
//            Toast.makeText(context, "ダウンロード中", Toast.LENGTH_SHORT).show()
//        }
        thread {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            try {
                val response: Response = client.newCall(request).execute()
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    callback.onDataFetched(responseData, fileWriteFinishFlag)
                } else {
                    callback.onError(IOException("Failed to fetch data"), fileWriteFinishFlag, year)
                }
            } catch (e: IOException) {
                callback.onError(e, fileWriteFinishFlag, year)
            }
        }
    }

    data class TideFlowData(
        // １時間毎の潮位データ(24時間分)
        val hourlyTideLevels: ArrayList<Int> =
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        // 潮汐データの日付 [0]:年　[1]:月　[2]:日
        var tideDate: Triple<Int, Int, Int> = Triple(2024, 1, 1),
        // 観測地点
        var locationName: String = "",
        // 満潮時間と潮位(4要素)  first:時 second:分 third:潮位
        val highTideTimes: ArrayList<Triple<Int, Int, Int>> = arrayListOf(),
        // 干潮時間と潮位(4要素)  first:時 second:分 third:潮位
        val lowTideTimes: ArrayList<Triple<Int, Int, Int>> = arrayListOf(),
    ) : Serializable

    /**
     * 潮汐データのテキストファイルの取得。内部にファイルをコピーする。
     * 気象庁サイトから取得する。(https://www.data.jma.go.jp/kaiyou/data/db/tide/suisan/txt/2024(年)/OS(地点).txt)
     * @param year データ取得の年
     * @param locationName 観測地点
     * @param callback コールバック先(インターフェイスの実装先)を指定する。
     * */
    fun getTideFlowDataTxt(context: Context, year: Int, locationName: String, callback: DataFetchCallback, fileWriteFinishFlag: Int){
        // URL作成
        val url = String.format(TIDE_DATA_TXT_URL_FORMAT, year, locationName)
        println("url $url")
        // データ取得
        fetchData(context, url, callback, fileWriteFinishFlag, year)
    }

    /**
     * 潮汐データのファイルを読み込む。
     * */
    fun readTideFlowDataTxt(){

    }

    /**
     * 潮汐データのファイルを作成する。
     * */
    fun writeTideFlowDataTxt(){

    }

    /**
     * 潮汐データのレコードをマップにする。
     * @param data 潮汐データ(すべて)
     * @return TideFlowDataのMapを返す。
     * */
    private fun makeTideFlowDataMap(data: String, currentDate: LocalDate, tideFlowDataMap: LinkedHashMap<Triple<Int, Int, Int>, TideFlowData>):
            LinkedHashMap<Triple<Int, Int, Int>, TideFlowData>{
        // レコードをマップに分割する。
        for (dataRecord in data.lines()) {
            if (RECORD_LENGTH_MAX == dataRecord.length) {
                val year = dataRecord.substring(RECORD_DATE_YEAR_START, RECORD_DATE_YEAR_START+2).trim().toInt() + 2000
                val month = dataRecord.substring(RECORD_DATE_MONTH_START, RECORD_DATE_MONTH_START+2).trim().toInt()
                val day = dataRecord.substring(RECORD_DATE_DAY_START, RECORD_DATE_DAY_START+2).trim().toInt()
                val recordDate = LocalDate.of(year, month, day)
                var diff = ChronoUnit.DAYS.between(currentDate, recordDate)
                // マイナス値の場合はプラスにする。
                if (diff < 0) {
                    diff *= -1
                }

                // 指定日から●●日の場合はデータをメモリに格納する。
                if (diff <= READ_DATA_RANGE) {
                    val tideFlowData = getTideFlowData(dataRecord)
                    tideFlowDataMap[tideFlowData.tideDate] = tideFlowData
                }
            }

        }
        // tideFlowDataMapを日付でソートする
        val sortedMap = tideFlowDataMap.toSortedMap(compareBy { (year, month, day) ->
            LocalDate.of(year, month, day)
        })

        return LinkedHashMap(sortedMap)
    }
    /**
     * １行分の潮汐データを取得する。
     * @param dataRecord 潮汐データ(１行)
     * @return TideFlowDataを返す。
     * */
    fun getTideFlowData(dataRecord: String): TideFlowData{
        val tideFlowData = TideFlowData()

        /* データ分割 */
        // 時間毎の潮位
        val hourlyTideLevelsString = dataRecord.substring(HOURLY_TIDE_LEVELS_START, HOURLY_TIDE_LEVELS_END)
        // 日付
        val tideDateString = dataRecord.substring(TIDE_DATE_START, TIDE_DATE_END)
        // 観測地点
        val locationNameString = dataRecord.substring(LOCATION_NAME_START, LOCATION_NAME_END)
        // 満潮
        val highTideTimesString = dataRecord.substring(HIGH_TIDE_TIMES_START, HIGH_TIDE_TIMES_END)
        // 干潮
        val lowTideTimesString = dataRecord.substring(LOW_TIDE_TIMES_START, LOW_TIDE_TIMES_END)

        /* 変数への代入処理 */
        // 時間毎の潮位の分割と代入
        for ((index, _) in tideFlowData.hourlyTideLevels.withIndex()) {
            tideFlowData.hourlyTideLevels[index] = hourlyTideLevelsString.substring(
                index * HOURLY_TIDE_FIXED_COLUMNS,
                (index + 1) * HOURLY_TIDE_FIXED_COLUMNS
            ).trim().toInt()
        }
        // 日付の分割と代入
        var year = 0
        var month = 0
        var day = 0
        for (index in 0 until DATE_TIMES_MAX) {
            val value =
                tideDateString.substring(
                    index * TIDE_DATE_FIXED_COLUMNS,
                    (index + 1) * TIDE_DATE_FIXED_COLUMNS
                ).trim().toInt()
            when(index){
                0 -> year = (2000 + value)
                1 -> month = value
                else -> day = value
            }
        }
        tideFlowData.tideDate = Triple(year, month, day)
        // 観測地点の分割と代入
        tideFlowData.locationName = locationNameString
        // 満潮の分割と代入
        for(index in 0 until HIGH_TIDE_TIMES_MAX){
            val timeTideLevel = highTideTimesString.substring(
                index * HIGH_TIDE_TIMES_FIXED_COLUMNS,
                (index + 1) * HIGH_TIDE_TIMES_FIXED_COLUMNS
            )
            val hour = timeTideLevel.substring(0, 2).trim()
            val min = timeTideLevel.substring(2, 4).trim()
            val tideLevel = timeTideLevel.substring(4, 7).trim()
            tideFlowData.highTideTimes.add(Triple(hour.toInt(), min.toInt(), tideLevel.toInt()))
        }
        // 干潮の分割と代入
        for(index in 0 until LOW_TIDE_TIMES_MAX){
            val timeTideLevel = lowTideTimesString.substring(
                index * LOW_TIDE_TIMES_FIXED_COLUMNS,
                (index + 1) * LOW_TIDE_TIMES_FIXED_COLUMNS
            )
            val hour = timeTideLevel.substring(0, 2).trim()
            val min = timeTideLevel.substring(2, 4).trim()
            val tideLevel = timeTideLevel.substring(4, 7).trim()
            tideFlowData.lowTideTimes.add(Triple(hour.toInt(), min.toInt(), tideLevel.toInt()))
        }
        return tideFlowData
    }

    /**
     * 潮汐データを加工せずに出力する。
     * @param context コンテキスト
     * @param fileNameYear ファイルネームにする年。(yyyy.txt)
     * @param data 潮汐データ(レスポンスで得た文字列)
     * */
    fun saveToTideFileTxt(context: Context, fileNameYear: Int, data: String, locationName: String) {
        try {
            // ファイル名を作成
            val fileName = String.format(TIDE_FILE_TXT_NAME, String.format("%s_%s", fileNameYear, locationName))
            // ファイル出力ストリームを開く
            val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            // データを書き込む
            fileOutputStream.write(data.toByteArray())
            // ストリームを閉じる
            fileOutputStream.close()
            println("ファイル保存成功: $fileName")
        } catch (e: IOException) {
            e.printStackTrace()
            println("ファイル保存失敗")
        }
    }

    /**
     * ファイルから読み込んだ潮汐データのリストを返す。
     * @param context コンテキスト
     * @param fileNameYear 潮汐データのファイルネームの年。(yyyy_xx.txt) xx: ロケーション
     * @return データリスト
     * */
    fun getTideFlowDataList(context: Context, fileNameYear: Int, locationName: String): List<String>{
        var fileInputStream: FileInputStream? = null
        var tideFlowDataList: List<String> = listOf()
        try {
            // ファイル名を作成
            val fileName = String.format(TIDE_FILE_TXT_NAME, String.format("%s_%s", fileNameYear, locationName))
            // 存在確認
            if(!File(context.filesDir, fileName).exists()){
                return tideFlowDataList
            }
            // ファイル入力ストリームを開く
            fileInputStream = context.openFileInput(fileName)
            // ストリームからデータを読み込む
            val inputStreamReader = fileInputStream.bufferedReader()
            val stringBuilder = StringBuilder()
            var line: String?
            while (inputStreamReader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append('\n')
            }
            // 読み込んだデータを文字列として返す
            val data = stringBuilder.toString()
            // リストにする。
            tideFlowDataList = data.lines()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // ストリームを閉じる
            fileInputStream?.close()
        }
        return tideFlowDataList
    }

    /**
     * 潮汐データのtxtファイルを読み込む。
     * @param context コンテキスト
     * @param dateList 読み込むファイル名の年のリスト
     * @param locationName 潮汐データのファイルネームの年。(yyyy_xx.txt) xx: ロケーション
     * @return 潮汐の生データ。※ファイルがなければnull
     * */
    fun readFromTideFileTxt(context: Context, dateList: ArrayList<LocalDate>, locationName: String): LinkedHashMap<Triple<Int, Int, Int>, TideFlowData> {
        var fileInputStream: FileInputStream? = null
        val tideFlowDataMap: LinkedHashMap<Triple<Int, Int, Int>, TideFlowData> = linkedMapOf()
        // 起点となる日付
        val currentDate = dateList[DATA_LIST_CURRENT]
        // ファイル読み込みの重複を避けるための変数
        var memoryYear = 0
        try {
            // for分のdateはファイル名の指定でのみ使用する。
            for(date in dateList) {
                println("readFromTideFileTxt  date $date")
                // 同じ年のファイルは読み込まない。(無駄な処理は避ける)
                if(memoryYear == date.year){
                    continue
                }
                // 読み込む年を記憶する。
                memoryYear = date.year
                // ファイル名を作成
                val fileName = String.format(
                    TIDE_FILE_TXT_NAME,
                    String.format("%s_%s", date.year, locationName)
                )
                // 存在確認
                if (!File(context.filesDir, fileName).exists()) {
                    return tideFlowDataMap
                }
                // ファイル入力ストリームを開く
                fileInputStream = context.openFileInput(fileName)
                // ストリームからデータを読み込む
                val inputStreamReader = fileInputStream.bufferedReader()
                val stringBuilder = StringBuilder()
                var line: String?
                while (inputStreamReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append('\n')
                }
                // 読み込んだデータを文字列として返す
                val data = stringBuilder.toString()
                // tideFlowDataMapを作成する。
                makeTideFlowDataMap(data, currentDate, tideFlowDataMap)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // ストリームを閉じる
            fileInputStream?.close()
        }
        return tideFlowDataMap
    }

    /**
     * ファイルパスの取得
     * @param context コンテキスト
     * @param fileNameYear ファイルネームの年。(yyyy.txt)
     * @return 潮汐データファイルパス
     * */
    fun getFilePath(context: Context, fileNameYear: Int, locationName: String): String {
        // ファイル名を作成
        val fileName = String.format(TIDE_FILE_TXT_NAME, String.format("%s_%s", fileNameYear, locationName))
        // 内部ストレージのファイルディレクトリを取得し、ファイルパスを生成
        val file = File(context.filesDir, fileName)
        return file.absolutePath
    }
}