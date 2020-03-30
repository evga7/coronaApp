package com.example.coronaapp.world

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.coronaapp.R
import com.example.coronaapp.Singleton
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class WorldCrawling(act:AppCompatActivity, context: Context,frg:Fragment) : AsyncTask<String, String, ArrayList<Information>>() { // 세번째 doinbackground 반환타입, onPostExecute 매개변수
    var infoList: ArrayList<Information> = arrayListOf()
    val progressCircle = CustomProgressCircle()
    val dialogContext : Context = context
    val currentActivity:AppCompatActivity = act
    val fragment:Fragment = frg

    override fun onPreExecute() {
        super.onPreExecute()
        progressCircle.show(dialogContext)
    }

    override fun doInBackground(vararg params: String?): ArrayList<Information> {

        try{
            val doc = Jsoup.connect(params[0]).get()
            //val data = doc.select("#main_table_countries > tbody > tr")
            //val data = doc.select("#main_table_countries_yesterday > tbody > tr")
            val data = doc.select("#main_table_countries_today > tbody > tr")

            var country :String
            var totalCases : String
            var newCases : String
            var totalDeaths :String
            var newDeaths : String
            var totalRecovered : String

            // country cnt
            var countCnt :Int = 1

            for (datum in data){

                country = datum.select("td")[0].text().trim()
                totalCases = datum.select("td")[1].text().trim()
                newCases = datum.select("td")[2].text().trim()
                totalDeaths = datum.select("td")[3].text().trim()
                newDeaths = datum.select("td")[4].text().trim()
                totalRecovered = datum.select("td")[5].text().trim()

                // 영어 -> 한글
                country = countryTrans(country)
                //Log.d("나라",country)

                if(totalCases.length == 0){
                     totalCases += '0'
                }
                if (totalDeaths.length == 0){
                    totalDeaths += '0'
                }
                if (totalRecovered.length == 0){
                    totalRecovered += '0'
                }
                if (newCases.length == 0){
                    newCases += "+0"
                }
                if (newDeaths.length == 0){
                    newDeaths += "+0"
                }


                val total = Information(null,country,totalCases + '\n' + newCases,totalDeaths + '\n' + newDeaths, totalRecovered)

                infoList.add(total)

                countCnt++
            }

            // total data add
            val totalCasesSum:String
            val totalDeathsSum:String
            val totalRecoveredSum:String


            val caseArr = infoList[infoList.size - 1].totalCases.split("\n")
            val deathArr = infoList[infoList.size - 1].totalDeaths.split("\n")

            totalCasesSum = caseArr[0]
            totalDeathsSum = deathArr[0]
            totalRecoveredSum = infoList[infoList.size - 1].totalRecovered

            //total remove
            infoList.remove(infoList[infoList.size - 1])

            val splitData  = { c: String->
                val case = c.split('\n')

                if (case[0].length > 4){
                    val a = case[0].split(',')
                    a[0] + a[1]
                }else{
                    case[0]
                }
            }

            // totalCase reverse sort
            infoList.sortByDescending { splitData(it.totalCases).toInt() }

            // numberling
           for (i in 0 until infoList.size){
                infoList[i].num = i+1
            }


            infoList.add(Information(0,countCnt.toString(),totalCasesSum,totalDeathsSum,totalRecoveredSum))

        }catch (e : IOException) {
            Log.d("안됨","안딤")
            e.printStackTrace()
        }

        return infoList
    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: ArrayList<Information>) {
        super.onPostExecute(result)
        Singleton.coronaList = result

        progressCircle.dialog.dismiss()

        currentActivity.supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    fun countryTrans(c:String):String =
        when{
            c == "China" -> "중국"
            c == "Italy" -> "이탈리아"
            c == "USA" -> "미국"
            c == "Spain" -> "스페인"
            c == "Germany" -> "독일"
            c == "Iran" -> "이란"
            c == "France" ->"프랑스"
            c == "Switzerland" -> "스위스"
            c == "UK" -> "영국"
            c == "S. Korea" -> "대한민국"
            c == "Netherlands" -> "네덜란드"
            c == "Austria" -> "오스트리아"
            c == "Belgium" -> "벨기에"
            c == "Canada" -> "캐나다"
            c == "Norway" -> "노르웨이"
            c == "Portugal" -> "포르투칼"
            c == "Australia" -> "호주"
            c == "Brazil" -> "브라질"
            c == "Sweden" -> "스웨덴"
            c == "Israel" -> "이스라엘"
            c == "Turkey" -> "터키"
            c == "Malaysia" -> "말레이시아"
            c == "Denmark" -> "덴마크"
            c == "Czechia" -> "체코"
            c == "Ireland" -> "아일랜드"
            c == "Luxembourg" -> "룩셈부르크"
            c == "Japan" -> "일본"
            c == "Ecuador" -> "에콰도르"
            c == "Chile" -> "칠레"
            c == "Pakistan" -> "파키스탄"
            c == "Poland" -> "폴란드"
            c == "Thailand" -> "태국"
            c == "Romania" -> "루마니아"
            c == "Saudi Arabia" -> "사우디아라비아"
            c == "Indonesia" -> "인도네시아"
            c == "Finland" -> "필란드"
            c == "Russia" -> "러시아"
            c == "Greece" -> "그리스"
            c == "Iceland" -> "아이슬란드"
            c == "Diamond Princess" -> "Diamond Princess\n(일본 크루즈)"
            c == "South Africa" -> "남아프리카 공화국"
            c == "Philippines" -> "필리핀"
            c == "India" -> "인도"
            c == "Singapore" -> "싱가포르"
            c == "Panama" -> "파나마"
            c == "Estonia" -> "에스토니아"
            c == "Qatar" -> "타카르"
            c == "Slovenia" -> "슬로베니아"
            c == "Argentina" -> "아르헨티나"
            c == "Croatia" -> "크로아티아"
            c == "Peru" -> "페루"
            c == "Mexico" -> "멕시코"
            c == "Colombia" -> "콜롬비아"
            c == "Egypt" -> "이집트"
            c == "Hong Kong" -> "홍콩"
            c == "Bahrain" -> "바레인"
            c == "Dominican Republic" -> "도미니카 공화국"
            c == "Serbia" -> "세르비아"
            c == "Iraq" -> "이라크"
            c == "Lebanon" -> "레바논"
            c == "Algeria" -> "알제리"
            c == "Armenia" -> "아르메니아"
            c == "Lithuania" -> "리투아니아"
            c == "New Zealand" -> "뉴질랜드"
            c == "Hungary" -> "헝가리"
            c == "Taiwan" -> "대만"
            c == "Latvia" -> "라트비아"
            c == "Bulgaria" -> "불가리아"
            c == "Morocco" -> "모로코"
            c == "Uruguay" -> "울과이"
            c == "Slovakia" -> "슬로바키아"
            c == "Kuwait" -> "쿠웨이트"
            c == "San Marino" -> "산마리노"
            c == "Costa Rica" -> "코스타리카"
            c == "Andorra" -> "안도라"
            c == "Bosnia and Herzegovina" -> "보스니아 헤르체고비나"
            c == "North Macedonia"  -> "북마케도니아"
            c == "Tunisia" -> "튀니지"
            c == "Jordan" -> "요르단"
            c == "Ukraine" -> "우크라니아"
            c == "Moldova" -> "몰도바"
            c == "Vietnam" -> "베트남"
            c == "Albania" -> "알바니아"
            c == "Burkina Faso" -> "부르키나파소"
            c == "Faeroe Islands" -> "페로제도"
            c == "Cyprus" -> "키프로스"
            c == "Malta" -> "몰타"
            c == "Réunion" -> "레위니옹"
            c == "Brunei" -> "브루나이"
            c == "Venezuela" -> "베네수엘라"
            c == "Sri Lanka" -> "스리랑카"
            c == "Oman" -> "오만"
            c == "Senegal" -> "세네갈"
            c == "Kazakhstan" -> "카자흐스탄"
            c == "Cambodia" -> "캄보디아"
            c == "Azerbaijan" -> "아제르바이잔"
            c == "Belarus" -> "벨라루스"
            c == "Palestine" -> "팔레스타인"
            c == "Afghanistan" -> "아프가니스탄"
            c == "Ivory Coast" -> "코트디부아르"
            c == "Georgia" -> "조지아"
            c == "Cameroon" -> "카메룬"
            c == "Guadeloupe" -> "과들루프"
            c == "Ghana" -> "가나"
            c == "Montenegro" -> "몬테네그로"
            c == "Martinique" -> "마르티니크"
            c == "Uzbekistan" -> "우즈베키스탄"
            c == "Trinidad and Tobago" -> "트리니다드 토바고"
            c == "Cuba" -> "쿠바"
            c == "Honduras" -> "온두라스"
            c == "Mauritius" -> "모리셔스"
            c == "DRC" -> "콩고민주공화국"
            c == "Liechtenstein"-> "리히텐슈타인"
            c == "Nigeria" -> "나이지리아"
            c == "Channel Islands" -> "채널 제도"
            c == "Kyrgyzstan" -> "키르기스탄"
            c == "Paraguay" -> "파라과이"
            c == "Rwanda" -> "르완다"
            c == "Bolivia" -> "볼리비아"
            c == "Mayotte" -> "마요트"
            c == "Macao" -> "마카오"
            c == "Monaco" -> "모나코"
            c == "French Guiana" -> "기아나\n(프랑스령)"
            c == "Kenya" -> "케냐"
            c == "Gibraltar" -> "지브롤터"
            c == "Jamaica" -> "자메이카"
            c == "French Polynesia" -> "폴리네시아\n(프랑스령)"
            c == "Guatemala" -> "과테말라"
            c == "Isle of Man" -> "맨섬"
            c == "Togo" -> "토고"
            c == "Aruba" -> "아루바"
            c == "Madagascar" -> "마다가스카르"
            c == "Barbados" -> "바베이도스"
            c == "New Caledonia" -> "버진아일랜드\n(미국령)"
            c == "Uganda" -> "우간다"
            c == "El Salvador" -> "엘사바도르"
            c == "Maldives" -> "몰디브"
            c == "Tanzania" -> "탄자니아"
            c == "Ethiopia" -> "에디오피아"
            c == "Zambia" -> "잠비아"
            c == "Djibouti" -> "지부"
            c == "Dominica" -> "도미니카"
            c == "Mongolia" -> "몽골"
            c == "Saint Martin" -> "세인트마틴"
            c == "Equatorial Guinea" -> ""
            c == "Cayman Islands" -> "캐이맨 섬"
            c == "Haiti" -> "아이티"
            c == "Suriname" -> "수리남"
            c == "Bermuda" -> "버뮤다제도"
            c == "Gabon" -> "가봉"
            c == "Namibia" -> "나미비아"
            c == "Niger" -> "니제르"
            c == "Seychelles" -> "세이셸"
            c == "Benin" -> "베냉"
            c == "Curaçao" -> "퀴라"
            c == "Greenland" -> "그린란드"
            c == "Bahamas" -> "바하마"
            c == "Fiji" -> "피지"
            c == "Guyana" -> "가이아나"
            c == "Mozambique" -> "모잠비크"
            c == "Syria" -> "시리아"
            c == "Cabo Verde" -> "카부 베르드"
            c == "Congo" -> "콩고"
            c == "Eritrea" -> "에리트레아"
            c == "Eswatini" -> "에스타와니"
            c == "Guinea" -> "기니"
            c == "Vatican City" -> "바티칸"
            c == "Angola" -> "앙골라"
            c == "Antigua and Barbuda" -> "안티쿠아바부다"
            c == "Chad" -> "차드"
            c == "Gambia" -> "잠비아"
            c == "Laos" -> "라오스"
            c == "Liberia" -> "라이베리"
            c == "Myanmar" -> "미얀마"
            c == "Nepal" -> "네팔"
            c == "Saint Lucia" -> "세인트 루시아"
            c == "Sint Maarten" -> "신트마르턴"
            c == "Sudan" -> "수단"
            c == "Zimbabwe" -> "짐바브웨"
            c == "Belize" -> "벨리즈"
            c == "Bhutan" -> "부탄"
            c == "British Virgin Islands" -> "버진 아일랜드\n(영국령)"
            c == "Guinea-Bissau" -> "기니비사우"
            c == "Mali" -> "말리"
            c == "Mauritania" -> "모리타니"
            c == "Nicaragua" -> "니카라과"
            c == "Saint Kitts and Nevis" -> "세인트키츠네비스"
            c == "Somalia" -> "소말리아"
            c == "Grenada" -> "그레나다"
            c == "Libya" -> "리비아"

            else -> c
        }

}

