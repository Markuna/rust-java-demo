package pers.metaworm;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

/**
 * JavaScript工具类。
 *
 * @author Michael J Chane
 * @version $Revision: 1.2 $ $Date: 2009/09/10 15:08:30 $
 */
public class JavaScriptUtil {

    /**
     * 缓存头
     */
    private static final String CACHE_KEY_PRE = "JavaScriptUtil:cache:";

    /**
     * 特殊字符-转义字符的映射
     */
    private static final Properties ESCAPE_MAP = new Properties();

    static {
        ESCAPE_MAP.put("\n", "\\n");
        ESCAPE_MAP.put("\r", "\\r");
        ESCAPE_MAP.put("\f", "\\f");
        ESCAPE_MAP.put("\'", "\\\'");
        ESCAPE_MAP.put("\"", "\\\"");
        ESCAPE_MAP.put("\\", "\\\\");
    }

    public static void main(String[] args) {
        StringBuilder ss = new StringBuilder(
                "// eslint-disable-next-line\n" + "!function(){var t;function n(t){var i,e,n=\"undefined\"!=typeof" +
                        " Symbol&&Symbol.iterator&&t[Symbol.iterator];return n?n.call(t)" +
                        ":{next:(i=t,e=0,function(){return e<i.length?{done:!1," +
                        "value:i[e++]}:{done:!0}})}}function o(t){for(var i,e=[];!(i=t" +
                        ".next()).done;)e.push(i.value);return e}var i,e," +
                        "r=\"function\"==typeof Object.create?Object.create:function(t)" +
                        "{function i(){}return i.prototype=t,new i};if" +
                        "(\"function\"==typeof Object.setPrototypeOf)i=Object" +
                        ".setPrototypeOf;else{t:{var s={};try{s.__proto__={a:!0},e=s.a;" + "break t}");
        //        for (int i = 0; i < 10; i++) {
        //            ss.append(ss);
        //        }

        String s = ss.toString();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String s1 = JavaScriptUtil.getStringAlgorithm(s);
        }
        long l = System.currentTimeMillis() - start;
        System.out.println(l);
        //        System.out.println(s1);
//        System.out.println(new String(DigestUtil.md5(s1.trim())));

    }

    /**
     * 将指定的JavaScript代码进行混淆。
     *
     * @param script 指定的JavaScript代码
     *
     * @return 混淆后的JavaScript代码
     */
//    public static String obfuscateScript(CharSequence script) {
//        if (script == null) {
//            return "";
//        }
//
//        String key = CACHE_KEY_PRE + new String(DigestUtil.md5(script.toString()), StandardCharsets.UTF_8);
//
//        RedisService redisService = ApplicationContextHolder.getBean(RedisService.class);
//        if (redisService != null) {
//            String value = RedisService.getString(key);
//            if (StringUtils.isNotBlank(value)) {
//                return value;
//            }
//        }
//
//        String value = getStringAlgorithm(script.toString());
//        if (redisService != null) {
//            RedisService.setValueByHour(key, value, 1L);
//        }
//        return value;
//    }

//    private static String getObfuscateScriptByJNI(String script) {
//        return JavaScriptStringJNI.obfuscateScriptJNI(script);
//    }

    private static String getStringAlgorithm(String script) {
        // String.fromCharCode中参数最大个数
        final int stringFromCharCodeLimit = 100;
        // 每行的参数个数
        final int parametersPerLine = 10;
        // 使用xor函数的比例
        final float xorRate = 0.1f;
        // 字符缓冲
        StringBuilder buff = new StringBuilder();
        // 格式化输出到字符串缓冲

        // 输出String.fromCharCode的别名定义，并返回其别名
        String stringFromCharCode = stringFromCharCode(buff);
        // 输出xor函数，并返回函数名列表及对应的xor阈值
        Map<String, Integer> xorFunctions = xorFunctions(buff);
        // xor函数名称
        String[] xorFuncNames = xorFunctions.keySet().toArray(new String[0]);

        // eval函数开始，其中第一个使用String.fromCharCode(32)即空格
        String[] osf3 = formatArguments(3, stringFromCharCode);
        buff.append("/*");
        buff.append(osf3[1]);
        buff.append("*/\\u0065\\u0076\\u0061\\u006c/*");
        buff.append(osf3[2]);
        buff.append("*/(");
        buff.append(osf3[0]);
        buff.append("(32");

        // 遍历代码中的所有字符
        for (int i = 0; i < script.length(); i++) {
            // 当前字符
            int code = script.charAt(i);

            if (i % stringFromCharCodeLimit == 0) {
                // 结束旧的String.fromCharCode，
                buff.append(")\n");
                // 开始新的String.fromCharCode
                Object[] umw2 = formatArguments(2, stringFromCharCode);
                buff.append("+/*");
                buff.append(umw2[1]);
                buff.append("*/");
                buff.append(umw2[0]);
                buff.append("(");
            } else {
                // 一般的String.fromCharCode参数之间使用逗号分隔
                buff.append(",");
                if (i % parametersPerLine == 0) {
                    // 当前字符结束后需要换行
                    buff.append("\n");
                }
            }

            // 根据xorRate确定的比例，输出当前字符参数
            if (ThreadLocalRandom.current().nextFloat() < xorRate) {
                // 使用xor参数的名称
                String xorFunc = xorFuncNames[i % xorFuncNames.length];
                // 对应的异或计算阈值
                int xor = xorFunctions.get(xorFunc);
                // 进行过异或计算后的结果
                int xorCode = code ^ xor;
                // 输出函数调用
                buff.append(xorFunc).append("(");
                // 输出函数参数
                buff.append(numberFormat(xorCode));
                // 调用结束
                buff.append(")");
            } else {
                // 正常输出
                buff.append(numberFormat(code));
            }
        }

        // 最后一个String.fromCharCode和eval函数的结尾
        Object[] ram3 = formatArguments(3);
        buff.append("/*");
        buff.append(ram3[0]);
        buff.append(ram3[1]);
        buff.append("*/));/*");
        buff.append(ram3[2]);
        buff.append("*/\n");

        // 返回混淆代码
        return buff.toString();
    }

    /**
     * 输出String.fromCharCode的别名定义，并返回其别名。
     *
     * @param sb 格式化输出
     *
     * @return String.fromCharCode别名
     */
    private static String stringFromCharCode(StringBuilder sb) {
        String stringFromCharCode = "__" + randomAlphanumeric(3, 10);
        Object[] objects = formatArguments(7, stringFromCharCode);
        sb.append("/*");
        sb.append(objects[1]);
        sb.append("*/var/*");
        sb.append(objects[2]);
        sb.append("*/");
        sb.append(objects[0]);
        sb.append("/*");
        sb.append(objects[3]);
        sb.append("*/=\\u0053\\u0074\\u0072\\u0069\\u006e\\u0067\n/*");
        sb.append(objects[4]);
        sb.append("*/./*");
        sb.append(objects[5]);
        sb.append("*/\\u0066r\\u006fm\\u0043ha\\u0072C\\u006fde/*");
        sb.append(objects[6]);
        sb.append("*/;\n");
        return stringFromCharCode;
    }

    /**
     * 输出xor函数，并返回函数名列表及对应的xor阈值。
     *
     * @param sb 格式化输出
     *
     * @return xor函数名列表及对应的xor阈值
     */
    private static Map<String, Integer> xorFunctions(StringBuilder sb) {
        int[] xorArray = new int[5];
        for (int i = 0; i < xorArray.length; i++) {
            xorArray[i] = randomInt(4096);
        }
        String xorArrayName = "_x_" + randomString(3);

        Object[] fag3Name = formatArguments(3, xorArrayName);
        sb.append("var/*");
        sb.append(fag3Name[1]);
        sb.append("*/");
        sb.append(fag3Name[0]);
        sb.append(" = [/*");
        sb.append(fag3Name[2]);
        sb.append("*/");

        for (int i = 0; i < xorArray.length; i++) {
            sb.append(xorArray[i]).append(",");
        }
        Object[] fago = formatArguments(2);
        sb.append("/*");
        sb.append(fago[0]);
        sb.append("*/];//");
        sb.append(fago[1]);
        sb.append("\n");

        Map<String, Integer> functions = new HashMap<String, Integer>();
        for (int i = 0; i < xorArray.length; i++) {
            String func = "_$" + randomAlphanumeric(3, 5);
            Object[] faf5 = formatArguments(5, func);
            sb.append("var/*");
            sb.append(faf5[1]);
            sb.append("*/");
            sb.append(faf5[0]);
            sb.append("/*");
            sb.append(faf5[2]);
            sb.append("*/=/*");
            sb.append(faf5[3]);
            sb.append("*/function(/*");
            sb.append(faf5[4]);
            sb.append("*/){\n");

            Object[] afs4 = formatArguments(4);
            sb.append("/*");
            sb.append(afs4[0]);
            sb.append("*/return/*");
            sb.append(afs4[1]);
            sb.append("*/arguments[/*");
            sb.append(afs4[2]);
            sb.append("*/0]^/*");
            sb.append(afs4[3]);
            sb.append("*/\n");

            Object[] maf6 = formatArguments(6, xorArrayName, String.valueOf(i));
            sb.append("/*");
            sb.append(maf6[2]);
            sb.append("*/");
            sb.append(maf6[0]);
            sb.append("[/*");
            sb.append(maf6[3]);
            sb.append("*/");
            sb.append(maf6[1]);
            sb.append("];/*");
            sb.append(maf6[4]);
            sb.append("*/}/*");
            sb.append(maf6[5]);
            sb.append("*/;\n");

            functions.put(func, xorArray[i]);
        }
        return functions;
    }

    /**
     * 获取格式化输出参数。
     *
     * @param count                参数个数
     * @param firstFixedParameters 最初的固定参数
     *
     * @return 根据给定的参数个数，在最初的固定参数后生成随机字符串作为格式化输出参数
     */
    private static String[] formatArguments(int count, String... firstFixedParameters) {
        if (count < firstFixedParameters.length) {
            throw new IllegalArgumentException("length < codes.length");
        }
        String[] args = new String[count];
        System.arraycopy(firstFixedParameters, 0, args, 0, firstFixedParameters.length);
        for (int i = firstFixedParameters.length; i < args.length; i++) {
            args[i] = randomAlphanumeric(5, 20);
        }
        return args;
    }

    /**
     * 生成由字母及数字组成的随机字符串。
     *
     * @param length 随机字符串长度
     *
     * @return 由字母及数字组成的随机字符串
     */
    private static String randomAlphanumeric(int length) {
        return randomAlphanumeric(length, length);
    }

    /**
     * 生成由字母及数字组成的随机字符串。
     *
     * @param minLength 随机字符串最小长度
     * @param maxLength 随机字符串最大长度
     *
     * @return 由字母及数字组成的随机字符串
     */
    private static String randomAlphanumeric(int minLength, int maxLength) {
        if (minLength <= 0) {
            throw new IllegalArgumentException("minLength <= 0");
        }
        if (maxLength <= 0) {
            throw new IllegalArgumentException("maxLength <= 0");
        }
        if (minLength > maxLength) {
            throw new IllegalArgumentException("minLength > maxLength");
        } else if (minLength == maxLength) {
            return randomString(minLength);
        } else {
            int length = minLength + randomInt(maxLength - minLength);
            return randomString(length);
        }
    }

    private static final String baseStr = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM1234567890";

    private static String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(baseStr.charAt(randomInt(baseStr.length())));
        }
        return sb.toString();
    }

    /**
     * 随机的格式化输出格式。
     *
     * @param seed 随机种子
     * @return 随机的格式化输出格式
     */
    static int seed = Math.abs(randomInt(123)) + 100;

    private static String numberFormat(int code) {
        int rnd = randomInt(seed);
        switch (rnd % 17) {
            case 0:
                return "0x" + Integer.toHexString(code);
            case 1:
                return "-1-~/*" + randomAlphanumeric(2, 5) + "*/(0x" + Integer.toHexString(code) + "^0)";
            case 2:
                return code + "" + randomInt(10) + "/0xA";// 防止数字相加
            case 3:
                return "Math.abs(" + code + ")&-1";
            case 4:
                return "0" + Integer.toOctalString(code);
            case 5:
                return code + "&(-1^0x00)";
            case 6:
                return "0x0|0x" + Integer.toHexString(code);
            case 7:
                Object[] objects = formatArguments(2);
                return "~/*" + objects[0] + "*/~/*" + objects[1] + "*/" + code;
            case 8:
                return "~(0x" + Integer.toHexString(code) + "^/*" + randomAlphanumeric(2, 5) + "*/-1)";
            case 9:
                return "0x" + Integer.toHexString(code) + randomInt(10) + randomInt(10) + "/0400";
            case 10:
                Object[] objects1 = formatArguments(3, String.valueOf(randomInt(10)), String.valueOf(randomInt(10)));
                return "0x" + Integer.toHexString(code) + objects1[0] + objects1[1] + ">>/*" + objects1[2] + "*/4>>4";
            case 11:
                return code + "/*" + randomString(2) + "*/";

            default:
                return code + "";
        }
    }

    private static int randomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
//        return 1;
    }

}