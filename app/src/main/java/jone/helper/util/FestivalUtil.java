package jone.helper.util;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FestivalUtil {
    private int lyear, lmonth, lday;// 农历年月日
    private int syear, smonth, sday;// 阳历年月日
    private boolean leap;
    private int yearCyl, monCyl, dayCyl;
    private Calendar baseDate = Calendar.getInstance();
    private Calendar offDate = Calendar.getInstance();
    private SimpleDateFormat chineseDateFormat = new SimpleDateFormat(
            "yyyy年MM月dd日");
    final static String chineseNumber[] = { "一", "二", "三", "四", "五", "六", "七",
            "八", "九", "十", "十一", "十二" };
    final static long[] lunarInfo = new long[] { 0x04bd8, 0x04ae0, 0x0a570,
            0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0,
            0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50,
            0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
            0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0,
            0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4,
            0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550,
            0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950,
            0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260,
            0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
            0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40,
            0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3,
            0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960,
            0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0,
            0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
            0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
            0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65,
            0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0,
            0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2,
            0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 };

    final static String[] Gan = new String[] { "甲", "乙", "丙", "丁", "戊", "己",
            "庚", "辛", "壬", "癸" };

    final static String[] Zhi = new String[] { "子", "丑", "寅", "卯", "辰", "巳",
            "午", "未", "申", "酉", "戌", "亥" };

    final static String[] Animals = new String[] { "鼠", "牛", "虎", "兔", "龙",
            "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };

    final static String[] SolarTerm = new String[] { "小寒", "大寒", "立春", "雨水",
            "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋",
            "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至" };

    final static long[] STermInfo = new long[] { 0, 21208, 42467, 63836, 85337,
            107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343,
            285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795,
            462224, 483532, 504758 };
    final static String chineseMonthNumber[] = { "正", "二", "三", "四", "五", "六",
            "七", "八", "九", "十", "冬", "腊" };

    final static String[] sFtv = new String[] { "0101*元旦", "0214 情人节",
            "0308 妇女节", "0312 植树节", "0314 国际警察日", "0315 消费者权益日", "0323 世界气象日",
            "0401 愚人节", "0407 世界卫生日", "0501*劳动节", "0504 青年节", "0508 红十字日",
            "0512 护士节", "0515 国际家庭日", "0517 世界电信日", "0519 全国助残日", "0531 世界无烟日",
            "0601 儿童节", "0605 世界环境日", "0606 全国爱眼日", "0623 奥林匹克日", "0625 全国土地日",
            "0626 反毒品日", "0701 建党节", "0707 抗战纪念日", "0711 世界人口日", "0801 建军节",
            "0908 国际扫盲日", "0909 毛泽东逝世纪念", "0910 教师节", "0917 国际和平日",
            "0920 国际爱牙日", "0922 国际聋人节", "0927 世界旅游日", "0928 孔子诞辰", "1001*国庆节",
            "1004 世界动物日", "1006 老人节", "1007 国际住房日", "1009 世界邮政日", "1015 国际盲人节",
            "1016 世界粮食日", "1024 联合国日", "1031 万圣节", "1108 中国记者日", "1109 消防宣传日",
            "1112 孙中山诞辰", "1114 世界糖尿病日", "1117 国际大学生节", "1201 世界艾滋病日",
            "1203 世界残疾人日", "1209 世界足球日", "1220 澳门回归", "1225 圣诞节", "1226 毛泽东诞辰" };

    final static String[] lFtv = { "0101*春节", "0115 元宵", "0505 端午", "0707 七夕",
            "0815 中秋", "0909 重阳", "1208 腊八", "1223 小年", "0100*除夕" };

    final static String[] wFtv = { "母亲节", "父亲节", "感恩节" };// 每年6月第3个星期日是父亲节,5月的第2个星期日是母亲节
    final static int[] otherDays = { 520, 630, 1144 };

    // 星期日是一个周的第1天第3个星期日也就是第3个完整周的第一天

    final private static int lYearDays(int y)// ====== 传回农历 y年的总天数
    {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0)
                sum += 1;
        }
        return (sum + leapDays(y));
    }

    final private static int leapDays(int y)// ====== 传回农历 y年闰月的天数
    {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else
            return 0;
    }

    final private static int leapMonth(int y)// ====== 传回农历 y年闰哪个月 1-12 , 没闰传回 0
    {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }

    final public static int monthDays(int y, int m)// ====== 传回农历 y年m月的总天数
    {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
            return 29;
        else
            return 30;
    }

    final private static String AnimalsYear(int y)// ====== 传回农历 y年的生肖
    {

        return Animals[(y - 4) % 12];
    }

    final private static String cyclical(int num)// ====== 传入 的offset 传回干支,
    // 0=甲子
    {

        return (Gan[num % 10] + Zhi[num % 12]);
    }

    // ===== 某年的第n个节气为几日(从0小寒起算)
    final private int sTerm(int y, int n) {
        offDate.set(1900, 0, 6, 2, 5, 0);
        long temp = offDate.getTime().getTime();
        offDate.setTime(new Date(
                (long) ((31556925974.7 * (y - 1900) + STermInfo[n] * 60000L) + temp)));

        return offDate.get(Calendar.DAY_OF_MONTH);
    }

    private int WeekDay(int year, int month, int day) {
        // TODO Auto-generated method stub
        // 数组元素monthday[i]表示第i个月以前的总天数除以7的余数
        int monthday[] = { 0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5 };
        int iDays = (year - 1) % 7 + (year - 1) / 4 - (year - 1) / 100
                + (year - 1) / 400;
        iDays += (monthday[month - 1] + day);
        // 如果iYear是闰年
        if (IsLeapYear(year) && month > 2)
            iDays++;
        // 返回:0,1,2,3,4,5,6表日、一、二、三、四、五、六
        return iDays % 7;
    }

    public boolean IsLeapYear(int iYear) {
        if ((iYear % 4 == 0 && iYear % 100 != 0) || iYear % 400 == 0) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getFestVals() {
        String solarTerms = null, solarFestival = null, lunarFestival = null, otherFestival = null;
        ArrayList<String> festvals = new ArrayList<String>();
        // ******************计算节气**********//
        if (sday == sTerm(syear, (smonth - 1) * 2))
            solarTerms = SolarTerm[(smonth - 1) * 2];
        else if (sday == sTerm(syear, (smonth - 1) * 2 + 1))
            solarTerms = SolarTerm[(smonth - 1) * 2 + 1];

        // ******************计算公历节日**********//
        for (int i = 0; i < sFtv.length; i++) {
            if (Integer.parseInt(sFtv[i].substring(0, 2)) == smonth
                    && Integer.parseInt(sFtv[i].substring(2, 4)) == sday) {
                solarFestival = sFtv[i].substring(5);
            }
        }

        // ******************计算农历节日**********//
        for (int i = 0; i < lFtv.length; i++) {
            if (Integer.parseInt(lFtv[i].substring(0, 2)) == lmonth
                    && Integer.parseInt(lFtv[i].substring(2, 4)) == lday) {
                lunarFestival = lFtv[i].substring(5);
            }
        }

        // ******************计算周节日**********//
        int weekth = sday / 7;
        if (sday % 7 == 0) {
            weekth = weekth - 1;
        }
        int weekday = WeekDay(syear, smonth, sday);
        for (int i = 0; i < otherDays.length; i++) {
            if (otherDays[i] == smonth * 100 + (weekth + 1) * 10 + weekday) {
                otherFestival = wFtv[i];
            }
        }
        if (solarTerms != null)
            festvals.add(solarTerms);
        if (solarFestival != null)
            festvals.add(solarFestival);
        if (lunarFestival != null)
            festvals.add(lunarFestival);
        if (otherFestival != null)
            festvals.add(otherFestival);
        return festvals;
    }

    public FestivalUtil(Calendar cal) {
        this(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal
                .get(Calendar.DAY_OF_MONTH));
    }

    public FestivalUtil(int y, int m, int d) {
        this.syear = y;
        this.smonth = m;
        this.sday = d;

        baseDate.setMinimalDaysInFirstWeek(7);// 设置一个月的第一个周是一个完整周
        int leapMonth = 0;
        try {
            baseDate.setTime(chineseDateFormat.parse("1900年1月31日"));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long base = baseDate.getTimeInMillis();
        try {
            baseDate.setTime(chineseDateFormat.parse(y + "年" + m + "月" + d
                    + "日"));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long obj = baseDate.getTimeInMillis();

        int offset = (int) ((obj - base) / 86400000L);
        // System.out.println(offset);
        // 求出和1900年1月31日相差的天数

        dayCyl = offset + 40;// 干支天
        monCyl = 14;// 干支月

        // 用offset减去每农历年的天数
        // 计算当天是农历第几天
        // i最终结果是农历的年份
        // offset是当年的第几天
        int iYear, daysOfYear = 0;
        for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
            daysOfYear = lYearDays(iYear);
            offset -= daysOfYear;
            monCyl += 12;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
            monCyl -= 12;
        }
        // 农历年份
        lyear = iYear;

        yearCyl = iYear - 1864;// ***********干支年**********//

        leapMonth = leapMonth(iYear); // 闰哪个月,1-12
        leap = false;

        // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
        int iMonth, daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
            // 闰月
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                --iMonth;
                leap = true;
                daysOfMonth = leapDays(iYear);
            } else
                daysOfMonth = monthDays(iYear, iMonth);

            offset -= daysOfMonth;
            // 解除闰月
            if (leap && iMonth == (leapMonth + 1))
                leap = false;
            if (!leap)
                monCyl++;
        }
        // offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (leap) {
                leap = false;
            } else {
                leap = true;
                --iMonth;
                --monCyl;
            }
        }
        // offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
            --monCyl;
        }
        lmonth = iMonth;
        lday = offset + 1;
    }

    public int getLday(){
        return lday;
    }

    public String getChinaDayString(){
        return getChinaDayString(lday);
    }

    public static String getChinaDayString(int day) {
        String chineseTen[] = { "初", "十", "廿", "卅" };
        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day > 30)
            return "";
        if (day == 10)
            return "初十";
        else
            return chineseTen[day / 10] + chineseNumber[n];
    }

    public static String getWeekDay(int dayOfWeek){
        switch (dayOfWeek){
            case 0:
                return "日";
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            default:
                return "";
        }
    }

    public String getChineseDate(){
        return (leap ? "闰" : "") + chineseNumber[lmonth - 1]
                + "月" + getChinaDayString(lday);
    }

    public String toString() {
        return lyear + "年" + (leap ? "闰" : "") + chineseNumber[lmonth - 1]
                + "月" + getChinaDayString(lday);
    }

    public static void main(String[] args) throws ParseException {
        Calendar today = Calendar.getInstance();
        FestivalUtil util = new FestivalUtil(2014,2,14);
        ArrayList<String> fest = util.getFestVals();
        for(String str:fest){
            System.out.println(str);
        }
    }

    public static String getPinYin(String inputString) {

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE); //生成的拼音是以大写方式显示还是以小写方式显示: LOWERCASE(小写)、UPPERCASE(大写)
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); //生成的拼音声调的显示方式: WITH_TONE_NUMBER(以数字代替声调)、WITHOUT_TONE (无声调)、WITH_TONE_MARK (有声调)
        format.setVCharType(HanyuPinyinVCharType.WITH_V);//碰到unicode 的ü 、v 和 u时的显示方式: WITH_U_AND_COLON : lu:3、WITH_V : lv3、WITH_U_UNICODE : lü3

        char[] input = inputString.trim().toCharArray();
        StringBuffer output = new StringBuffer("");

        try {
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output.append(temp[0]);
                    output.append(" ");
                } else
                    output.append(Character.toString(input[i]));
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public static String getPinYin(String inputString, String space){
        return getPinYin(inputString).trim().replace(" ", space);
    }
}