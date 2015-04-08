/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Yulin_Zhang
 */
public class StringUtil {

    public static Float StringToFloat(String float_str) {
        Float float_value = Float.valueOf(float_str);
        return float_value;
    }

    public static String parseLiteralStr(String raw_str) {
        int end_index = raw_str.indexOf("^");
        String result_str = raw_str.substring(0, end_index).trim();
        return result_str;
    }
}
