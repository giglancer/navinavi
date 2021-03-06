package com.navinavi

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.navinavi.data.SearchInformation
import com.navinavi.databinding.FragmentSearchBinding
import com.navinavi.room.SearchHistoryDAO
import com.navinavi.room.SearchHistoryDatabase
import com.navinavi.room.SearchHistoryEntity
import com.savvyapps.togglebuttonlayout.Toggle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.SocketTimeoutException
import java.net.URL
import java.util.Calendar
import javax.net.ssl.HttpsURLConnection

// ホーム画面(乗換検索画面)
// 乗換検索APIを使用して乗換検索を実施する
class SearchFragment : Fragment(), View.OnClickListener {
    companion object {
        private const val TAG = "HomeFragment"
    }
    private lateinit var db: SearchHistoryDatabase
    private lateinit var dao: SearchHistoryDAO

    // 検索APIのパラメータ
    private val searchInformation = SearchInformation()

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0
    private var selectedHourOfDay: Int = 0
    private var selectedMinute: Int = 0

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.db = Room.databaseBuilder(
            requireContext(),
            SearchHistoryDatabase::class.java,
            "searchHistory.db"
        ).build()
        this.dao = this.db.searchHistoryDAO()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToggleButton()
        onClickListener()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    トグルボタン設定
    private fun initToggleButton() {
        binding.arrivalToLastTrainToggleButton.addToggle(Toggle(1, null, "出発"))
        binding.arrivalToLastTrainToggleButton.addToggle(Toggle(2, null, "到着"))
        binding.arrivalToLastTrainToggleButton.addToggle(Toggle(3, null, "始発"))
        binding.arrivalToLastTrainToggleButton.addToggle(Toggle(4, null, "終電"))

//     デフォルトは出発
        binding.arrivalToLastTrainToggleButton.setToggled(1, true)
    }

    private fun toggleButtonClickLister() {
        binding.arrivalToLastTrainToggleButton.onToggledListener = {_, toggle, selected ->
            if (toggle.isSelected) {
                when (toggle.id) {
                    1 -> {
                        binding.timePickerText.isClickable = true
                        binding.timePickerText.text = "現在時刻"
                        Log.d(TAG, toggle.title.toString())
                        }
                    2 ->{
                        binding.timePickerText.isClickable = true
                        binding.timePickerText.text = "到着時刻"
                        Log.d(TAG, toggle.title.toString())
                        }
                    3 -> {
                        binding.timePickerText.isClickable = false
                        binding.timePickerText.text = "始発"
                        Log.d(TAG, toggle.title.toString())
                        }
                    4 -> {
                        binding.timePickerText.isClickable = false
                        binding.timePickerText.text = "終電"
                        Log.d(TAG, toggle.title.toString())
                        }
                }
            }
        }
    }

    private fun showDateTimePicker(): DatePickerDialog {
//        初期値は現在時刻
        val cal = Calendar.getInstance()
        selectedYear = cal.get(Calendar.YEAR)
        selectedMonth = cal.get(Calendar.MONTH)
        selectedDay = cal.get(Calendar.DAY_OF_MONTH)
        selectedHourOfDay = cal.get(Calendar.HOUR_OF_DAY)
        selectedMinute = cal.get(Calendar.MINUTE)
        Log.d(
            "defaultTime",
            "$selectedYear/$selectedMonth/$selectedDay ${selectedHourOfDay}時${selectedMinute}分"
        )

        val timePickerDialog = TimePickerDialog(requireContext(), R.style.timePickerStyle_Spinner,
            { view, hourOfDay, minute ->
                selectedHourOfDay = hourOfDay
                selectedMinute = minute

                if (binding.timePickerText.text == "現在時刻") {
                    binding.timePickerText.text = "出発時刻：$selectedYear/$selectedMonth/$selectedDay ${selectedHourOfDay}時${selectedMinute}分"
                } else {
                    binding.timePickerText.text = "到着時刻：$selectedYear/$selectedMonth/$selectedDay ${selectedHourOfDay}時${selectedMinute}分"
                }


            }, selectedHourOfDay, selectedMinute, true
        )

        return DatePickerDialog(
            requireActivity(),
            R.style.datePickerStyle_Spinner,
            { view, year, month, dayOfMonth ->
                selectedYear = year
                selectedMonth = month + 1
                selectedDay = dayOfMonth

                timePickerDialog.show()

            },
            selectedYear,
            selectedMonth,
            selectedDay
        )
    }

    private fun replaceStationName() {
        val previousEditDepartureName = binding.editDepatureName.text
        val previousEditArrivalName = binding.editArraivalName.text
        binding.editDepatureName.text = previousEditArrivalName
        binding.editArraivalName.text = previousEditDepartureName
    }

    private fun isDepartureToArrivalEmpty(): Boolean {
        var flg = true
        val departureText = binding.editDepatureName
        val arrivalText = binding.editArraivalName
        val selectedStatus = binding.timePickerText.text.toString()

        when{
            departureText.text.toString().isEmpty() && arrivalText.text.toString().isEmpty() -> {
                departureText.error = "出発地を入力して下さい"
                arrivalText.error = "到着地を入力して下さい"
                flg = false
            }
            departureText.text.toString().isEmpty() -> {
                departureText.error = "出発地を入力して下さい"
                flg = false
            }
            arrivalText.text.toString().isEmpty() -> {
                arrivalText.error = "到着地を入力して下さい"
                flg = false
            }
            selectedStatus == "到着時刻" -> {
                Toast.makeText(requireContext(), "到着時刻を入力して下さい" ,Toast.LENGTH_LONG).show()
                flg = false
            }
        }
        return flg
    }

    private fun createFullUrl(): String {
        val urlYear = String.format("%04d", selectedYear)
        val urlMonth = String.format("%02d", selectedMonth)
        val urlDay = String.format("%02d", selectedDay)
        val urlHourOfDay = String.format("%02d", selectedHourOfDay)
        val urlMinute = String.format("%02d", selectedMinute)
        searchInformation.date = "$urlYear$urlMonth$urlDay"
        searchInformation.time = "$urlHourOfDay$urlMinute"
        searchInformation.from = Uri.encode(binding.editDepatureName.text.toString())
        searchInformation.to = Uri.encode(binding.editArraivalName.text.toString())


        val selectedStatus = binding.timePickerText.text
        searchInformation.searchType = when  {
            selectedStatus == "現在時刻" || Regex("出発時刻").containsMatchIn(selectedStatus) -> "departure"
            selectedStatus == "始発" -> "firstTrain"
            selectedStatus == "終電" -> "lastTrain"
            else -> "arrival"
        }
//        時刻設定しない場合現在時刻で検索される
        return if (binding.timePickerText.text == "現在時刻" || binding.timePickerText.text == "始発" || binding.timePickerText.text == "終電") {
            "${searchInformation.baseUrl}${BuildConfig.API_KEY}" +
                    "&from=${searchInformation.from}" +
                    "&to=${searchInformation.to}" +
                    "&searchType=${searchInformation.searchType}"
        } else {
            "${searchInformation.baseUrl}${BuildConfig.API_KEY}" +
                    "&from=${searchInformation.from}" +
                    "&to=${searchInformation.to}" +
                    "&date=${searchInformation.date}" +
                    "&time=${searchInformation.time}" +
                    "&searchType=${searchInformation.searchType}"
        }
    }

//    apiレスポンスデータ加工
    private fun inputStreamToString(stream: InputStream): String {
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        var line = reader.readLine()
        while (line != null) {
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }
    private suspend fun requestSearchAPI() {
        var result = ""
        withContext(Dispatchers.IO) {
            val url = URL(createFullUrl())
            val con = url.openConnection() as? HttpsURLConnection

            con?.let {
                try {
                    it.connectTimeout = 1000
                    it.readTimeout = 1000
                    it.requestMethod = "GET"
                    it.connect()

                    val resCode = con.responseCode
                    if (resCode == HttpsURLConnection.HTTP_OK) {
                        val stream = it.inputStream
                        result = inputStreamToString(stream)
                        stream.close()
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.editDepatureName.error = "入力値を確認してください"
                            binding.editArraivalName.error = "入力値を確認してください"
                            binding.editDepatureName.setText("")
                            binding.editArraivalName.setText("")
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    Log.w(TAG,"通信タイムアウト",e)
                    Toast.makeText(requireContext(), "通信タイムアウト", Toast.LENGTH_SHORT).show()
                }
                it.disconnect()
            }
        }
        withContext(Dispatchers.Main) {
            if (result.isNotEmpty()) {
                val rootJSON = JSONObject(result)
                val resultSetJSON = rootJSON.getJSONObject("ResultSet")
                val resourceUri = resultSetJSON.getString("ResourceURI")
                Log.d("URL", resourceUri)

                insertSearchInformation(resourceUri)

                val action = SearchFragmentDirections.actionHomeFragmentToSearchResultFragment(resourceUri)
                findNavController().navigate(action)
            }
        }
    }
    //region View.OnClickListener
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id){
                R.id.timePickerText -> showDateTimePicker().show()
                R.id.returnBtn -> replaceStationName()
                R.id.researchBtn -> {
                    if (isDepartureToArrivalEmpty()) {
                        lifecycleScope.launch() {
                            requestSearchAPI()
                        }
                    }
                }
            }
        }
    }
    //endregion

    private fun onClickListener() {
        binding.timePickerText.setOnClickListener(this)
        binding.returnBtn.setOnClickListener(this)
        binding.researchBtn.setOnClickListener(this)
        toggleButtonClickLister()
    }
    private suspend fun insertSearchInformation(resourceUri: String) {
        //　現在時刻で検索かつ時間指定なし
        if (selectedYear == 0) {
            val cal = Calendar.getInstance()
            selectedYear = cal.get(Calendar.YEAR)
            selectedMonth = cal.get(Calendar.MONTH) + 1
            selectedDay = cal.get(Calendar.DAY_OF_MONTH)
            selectedHourOfDay = cal.get(Calendar.HOUR_OF_DAY)
            selectedMinute = cal.get(Calendar.MINUTE)
        }
        Log.d("selectedYear", selectedYear.toString())

        val formatYear = String.format("%04d", selectedYear)
        val formatMonth = String.format("%02d", selectedMonth)
        val formatDay = String.format("%02d", selectedDay)
        val formatHourOfDay = String.format("%02d", selectedHourOfDay)
        val formatMinute = String.format("%02d", selectedMinute)
        val date = "$formatYear/$formatMonth/$formatDay"
        val time = "/$formatHourOfDay:$formatMinute"

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val selectedStatus = binding.timePickerText.text
                var searchTypeDateTime = when  {
                    selectedStatus == "現在時刻" || Regex("出発時刻").containsMatchIn(selectedStatus) -> "出発時刻：$date$time"
                    selectedStatus == "始発" -> "始発：$date"
                    selectedStatus == "終電" -> "終電：$date"
                    else -> "到着時刻：$date$time"
                }
                val searchHistory = SearchHistoryEntity(
                    id = 0,
                    from = binding.editDepatureName.text.toString(),
                    to = binding.editArraivalName.text.toString(),
                    searchTypeDateTime = "$searchTypeDateTime",
                    url = resourceUri
                )
                Log.d(TAG, searchHistory.toString())
                dao.insert(searchHistory)
                // 20以上表示しないので20を超えるものはデータから削除
                dao.deleteOver20()
            }
        }
    }
}