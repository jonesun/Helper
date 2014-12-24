package jone.helper.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jone.helper.lib.util.Utils;

/**
 * Created by jone_admin on 14-1-3.
 */
public class Lunar {

    private static Date TheDate = null;
    private int cYear;
    private int cMonth;
    private int cDay;
    //private int cHour;
    //天干
    private String tgString = "甲乙丙丁戊己庚辛壬癸";
    //地支
    private String dzString = "子丑寅卯辰巳午未申酉戌亥";
    //中文数字
    private String numString = "一二三四五六七八九十";
    //阴历中的月称
    private String monString = "正二三四五六七八九十冬腊";
    //星期的中文
    private String weekString = "日一二三四五六";
    //生肖
    private String sx = "鼠牛虎兔龙蛇马羊猴鸡狗猪";
    private SimpleDateFormat chineseDateFormat = new SimpleDateFormat(
            "yyyy年MM月dd日");

    //1900年到2050年的阴历推算信息
    private long[] CalendarData = new long[] { 0x8096d, 0x4ae, 0xa57, 0x50a4d,
            0xd26, 0xd95, 0x40d55, 0x56a, 0x9ad, 0x2095d, 0x4ae, 0x6149b,
            0xa4d, 0xd25, 0x51aa5, 0xb54, 0xd6a, 0x212da, 0x95b, 0x70937,
            0x497, 0xa4b, 0x5164b, 0x6a5, 0x6d4, 0x415b5, 0x2b6, 0x957,
            0x2092f, 0x497, 0x60c96, 0xd4a, 0xea5, 0x50d69, 0x5ad, 0x2b6,
            0x3126e, 0x92e, 0x7192d, 0xc95, 0xd4a, 0x61b4a, 0xb55, 0x56a,
            0x4155b, 0x25d, 0x92d, 0x2192b, 0xa95, 0x71695, 0x6ca, 0xb55,
            0x50ab5, 0x4da, 0xa5d, 0x30a57, 0x52d, 0x8152a, 0xe95, 0x6aa,
            0x615aa, 0xab5, 0x4b6, 0x414ae, 0xa57, 0x526, 0x31d26, 0xd95,
            0x70b55, 0x56a, 0x96d, 0x5095d, 0x4ad, 0xa4d, 0x41a4d, 0xd25,
            0x81aa5, 0xb54, 0xb5a, 0x612da, 0x95b, 0x49b, 0x41497, 0xa4b,
            0xa164b, 0x6a5, 0x6d4, 0x615b4, 0xab6, 0x957, 0x5092f, 0x497,
            0x64b, 0x30d4a, 0xea5, 0x80d65, 0x55c, 0xab6, 0x5126d, 0x92e,
            0xc96, 0x41a95, 0xd4a, 0xda5, 0x20b55, 0x56a, 0x7155b, 0x25d,
            0x92d, 0x5192b, 0xa95, 0xb4a, 0x416aa, 0xad5, 0x90ab5, 0x4ba,
            0xa5b, 0x60a57, 0x52b, 0xa93, 0x40e95, 0x6aa, 0xad5, 0x209b5,
            0x4b6, 0x614ae, 0xa4e, 0xd26, 0x51d26, 0xd53, 0x5aa, 0x30d6a,
            0x96d, 0x7095d, 0x4ad, 0xa4d, 0x61a4b, 0xd25, 0xd52, 0x51b54,
            0xb5a, 0x56d, 0x2095b, 0x49b, 0x71497, 0xa4b, 0xaa5, 0x516a5,
            0x6d2, 0xada };

    //存放每月一日到每年1月1日的天数，二月都以28天计算
    private int[] madd = new int[] { 0, 31, 59, 90, 120, 151, 181, 212, 243,
            273, 304, 334 };

    //位运算，主要用来从十六进制中得到阴历每个月份是大月还是小月
    private int GetBit(long m, int n) {
        int r = (int) ((m >> n) & 1);
        return r;
    }

    public String getLunarTime(long time) throws ParseException {
        return e2c(Utils.long2string(time, chineseDateFormat));
    }

    //将阳历向阴历转换
    @SuppressWarnings("deprecation")
    public String e2c(String time) throws ParseException {
        TheDate = chineseDateFormat.parse(time);
        int total, m, n, k;
        boolean isEnd = false;
        int tmp = TheDate.getYear();
        if (tmp < 1900) {
            tmp += 1900;
        }

        //计算TheDate到1900年1月30日的总天数，1900年1月31日是“庚子年正月初一”我们以这个时间点来推测
        total = (int) ((tmp - 1900) * 365/*先以每年365天粗算*/
                + countLeapYears(1900, tmp)/*再加上其中的闰年2月多出的一天*/
                + madd[TheDate.getMonth()]/*当前时间月份到元旦的天数*/
                + TheDate.getDate()/*载加上当前月份已过天数*/
                - 30/*因为1900年1月31日才是正月初一，粗算时多算了30天*/);
        //判断当前年份是否是闰年，如果为闰年并且二月已过，应再加上2月多的一天，才是准确的总天数
        if (isLeapYear(tmp) && TheDate.getMonth() > 1)
            total++;
        //开始推算已经过了几个阴历年，从1900年开始
        for (m = 0;; m++) {
            //检查16进制中信息，当年是否有闰月，有，则为13个月份
            k = (CalendarData[m] < 0xfff) ? 11 : 12;

            for (n = k; n >= 0; n--) {
                //如果总天数被减得小于29或30（由16进制中的规律来确定），则推算结束
                if (total <= 29 + GetBit(CalendarData[m], n)) {
                    isEnd = true;
                    break;
                }
                //如果不小于29或30，则继续做减
                total = total - 29 - GetBit(CalendarData[m], n);
            }
            if (isEnd)
                break;
        }
        //当前阴历年份
        cYear = 1900 + m;
        //当前阴历月份
        cMonth = k - n + 1;
        //当前阴历日子
        cDay = total;

        //如果阴历cYear年有闰月，则确定是闰几月，并精确阴历月份
        if (k == 12) {
            //如果cMonth恰巧等于该年闰月，则需要标示当前阴历月份为闰月
            if (cMonth == Math.floor(CalendarData[m] / 0x10000) + 1){
                cMonth = 1 - cMonth;
            }
            //如果cMonth大于该年闰月，则表示闰月已过，需要对cMonth减1
            if (cMonth > Math.floor(CalendarData[m] / 0x10000) + 1){
                cMonth--;
            }
        }
        //计算时辰，夜里23点到1点为子时，每两个小时为一个时辰，用“地支”依次类推
        //cHour = (int) Math.floor((TheDate.getHours() + 3) / 2);

        return getLunarDateString();
    }

    //整理输出
    public String getLunarDateString() {
        String tmp = "";
        //算天干，1900年1月31日是庚子年，庚是天干中的第七位需要对cYear-4再做模运算
        tmp += tgString.charAt((cYear - 4) % 10); // 年干
        tmp += dzString.charAt((cYear - 4) % 12); // 年支
        tmp += "年(";
        //算生肖
        tmp += sx.charAt((cYear - 4) % 12);
        tmp += ")";
        //处理闰月标记之前的闰月，是被处理为负数了
        if (cMonth < 1) {
            tmp += "闰";
            tmp += monString.charAt(-cMonth - 1);
        } else
            tmp += monString.charAt(cMonth - 1);
        tmp += "月";
        //处理日子
        tmp += (cDay < 11) ? "初" : ((cDay < 20) ? "十" : ((cDay < 30) ? "廿"
                : "卅"));
        if (cDay % 10 != 0 || cDay == 10)
            tmp += numString.charAt((cDay - 1) % 10);
//        if (cHour == 13)
//            tmp += "夜";
//        //处理时辰
//        tmp += dzString.charAt((cHour - 1) % 12);
//        tmp += "时";
        return tmp;
    }

    //计算两个年份间的闰年数
    private int countLeapYears(int s, int e) {
        int count = 0;
        for (int i = s; i < e; i++) {
            if (isLeapYear(i)) {
                count++;
            }
        }
        return count;
    }

    //判断年份是否为闰年
    private boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static final String chineseZodiacs[] = new String[]{
            "鼠","牛","虎","兔",
            "龙","蛇","马","羊",
            "猴","鸡","狗","猪"
    };

    public static int getChineseZodiacId(Integer year){
        if(year < 1900){
            return -1;
        }
        Integer start=1900;
        return (year-start)%chineseZodiacs.length;
    }
    public static int getChineseZodiacId(){
        return getChineseZodiacId(Calendar.getInstance().get(Calendar.YEAR));
    }

    public static void main(String[] args) {
        Lunar lunar = new Lunar();
        try {
            System.out.println(lunar.e2c("2011年1月20日"));
            System.out.println(lunar.getLunarTime(System.currentTimeMillis()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}