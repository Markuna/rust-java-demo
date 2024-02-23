use std::collections::HashMap;
use std::iter::Map;
use std::ops::{Add, Index};
use jni::objects::*;
use jni::sys::jstring;
use jni::JNIEnv;
use rand::prelude::ThreadRng;
use rand::Rng;


// https://zhuanlan.zhihu.com/p/568062165
#[no_mangle]
pub unsafe extern "C" fn Java_com_payegis_antispider_waf_jni_JavaScriptStringJNI_obfuscateScript<'local>(
    env: JNIEnv,
    _class: JClass<'local>,
    script: JString<'local>,
) -> jstring {
    // 输入 --> script
    let mut script: String =
        env.get_string(script).expect("Couldn't get java string!").into();

    let mut random: ThreadRng = rand::thread_rng();
    let sc = opters(&script, &mut random);

    let output = env.new_string(sc)
        .expect("Couldn't create java string!");
    // Finally, extract the raw pointer to return.
    output.into_inner()
}

fn opters(script: &String, random: &mut ThreadRng) -> String{
// String.fromCharCode中参数最大个数
    let string_from_char_code_limit = 100;
    // 每行的参数个数
    let parameters_per_line = 10;
    // 使用 xor 函数的比例
    let xor_rate = 0.1;
    // 字符缓冲
    let mut buff: Vec<String> = Vec::with_capacity(script.len() * 3);

    // 输出String.fromCharCode的别名定义，并返回其别名
    let string_from_char_code = string_from_char_code(&mut buff, random);
    // 输出xor函数，并返回函数名称列表以及对应的xor阈值
    let xor_functions: HashMap<String, i32> = xor_functions(&mut buff, random);
    // xor函数名
    let xor_func_names: Vec<String> = xor_functions.keys().map(|k| k.clone()).collect();

    // eval 函数开始，其中第一个使用String.fromCharCode(32)即空格
    let osf3 = format_arguments(3, &string_from_char_code, random);
    buff.push("/*".to_string());
    buff.push(osf3.index(1).to_string());
    buff.push("*/\\u0065\\u0076\\u0061\\u006c/*".to_string());
    buff.push(osf3.index(2).to_string());
    buff.push("*/(".to_string());
    buff.push(osf3.index(0).to_string());
    buff.push("(32".to_string());

    // 遍历代码中的所有字符
    for (i, code) in script.chars().enumerate() {
        // for (i, code) in script.as_bytes().iter().enumerate() {
        let code = code as i32;
        // let code = *code;
        if i % string_from_char_code_limit == 0 {
            // 结束旧的String.fromCharCode，
            buff.push(")\n".to_string());
            let umw2 = format_arguments(2, &string_from_char_code, random);
            buff.push("+/*".to_string());
            buff.push(umw2.index(1).to_string());
            buff.push("*/".to_string());
            buff.push(umw2.index(0).to_string());
            buff.push("(".to_string());
        } else {
            // 一般的String.fromCharCode参数之间使用逗号分割
            buff.push(",".to_string());
            if i % parameters_per_line == 0 {
                buff.push("\n".to_string());
            }
        }

        // 根据xorRate确定的比例，输出当前字符参数
        // let mut RANDOM: ThreadRng = rand::thread_rng();
        let next_float: f64 = random.gen(); // RANDOM number in range [0, 1)
        if next_float < xor_rate {
            // 使用xor参数的名称
            let xor_func = xor_func_names.index(i % xor_func_names.len());
            // 进行过异或计算后的结果
            let mut xor_code = 0;
            match xor_functions.get(xor_func) {
                Some(v) => {
                    xor_code = code ^ *v
                }
                None => {
                    panic!("can not found key");
                }
            }
            // 输出函数调用
            buff.push(xor_func.to_string());
            buff.push("(".to_string());
            // 输出函数参数
            buff.push(number_format(&xor_code, random));
            // 调用结束
            buff.push(")".to_string());
        } else {
            buff.push(number_format(&code, random));
        }
    }

    // 最后一个String.fromCharCode和eval函数的结尾
    let ram3 = format_arguments_empty(3, random);
    buff.push("/*".to_string());
    buff.push(ram3.index(0).to_string());
    buff.push(ram3.index(1).to_string());
    buff.push("*/));/*".to_string());
    buff.push(ram3.index(2).to_string());
    buff.push("*/\n".to_string());

    // final var script
    let mut sc = String::new();
    for x in buff.iter() {
        sc.push_str(x);
    }
    sc
    // println!("{}", sc);
}

fn xor_functions(buff: &mut Vec<String>, random: &mut ThreadRng) -> HashMap<String, i32> {
    let mut xor_array: [i32; 5] = Default::default();
    for i in 0..5 {
        xor_array[i] = random_int(4096, random);
    }
    let xor_array_name = "_x_".to_string() + &random_string(3, random);
    let fag3_name = format_arguments(3, &xor_array_name, random);
    buff.push("var/*".to_string());
    buff.push(fag3_name.index(1).to_string());
    buff.push("*/".to_string());
    buff.push(fag3_name.index(0).to_string());
    buff.push(" = [/*".to_string());
    buff.push(fag3_name.index(2).to_string());
    buff.push("*/".to_string());

    for item in xor_array {
        buff.push(item.to_string());
        buff.push(",".to_string());
    }

    let fago = format_arguments_empty(2, random);
    buff.push("/*".to_string());
    buff.push(fago.index(0).to_string());
    buff.push("*/];//".to_string());
    buff.push(fago.index(1).to_string());
    buff.push("\n".to_string());

    let mut functions: HashMap<String, i32> = HashMap::new();
    for (i, item) in xor_array.iter().enumerate() {
        let func = "_$".to_string() + &random_alphanumeric(3, 5, random);
        let faf5 = format_arguments(5, &func, random);
        buff.push("var/*".to_string());
        buff.push(faf5.index(1).to_string());
        buff.push("*/".to_string());
        buff.push(faf5.index(0).to_string());
        buff.push("/*".to_string());
        buff.push(faf5.index(2).to_string());
        buff.push("*/=/*".to_string());
        buff.push(faf5.index(3).to_string());
        buff.push("*/function(/*".to_string());
        buff.push(faf5.index(4).to_string());
        buff.push("*/){\n".to_string());

        let afs4 = format_arguments_empty(4, random);
        buff.push("/*".to_string());
        buff.push(afs4.index(0).to_string());
        buff.push("*/return/*".to_string());
        buff.push(afs4.index(1).to_string());
        buff.push("*/arguments[/*".to_string());
        buff.push(afs4.index(2).to_string());
        buff.push("*/0]^/*".to_string());
        buff.push(afs4.index(3).to_string());
        buff.push("*/\n".to_string());

        let maf6 = format_arguments2(6, &xor_array_name, &i.to_string(), random);
        buff.push("/*".to_string());
        buff.push(maf6.index(2).to_string());
        buff.push("*/".to_string());
        buff.push(maf6.index(0).to_string());
        buff.push("[/*".to_string());
        buff.push(maf6.index(3).to_string());
        buff.push("*/".to_string());
        buff.push(maf6.index(1).to_string());
        buff.push("];/*".to_string());
        buff.push(maf6.index(4).to_string());
        buff.push("*/}/*".to_string());
        buff.push(maf6.index(5).to_string());
        buff.push("*/;\n".to_string());

        functions.insert(func, *item);
    }

    return functions;
}

fn string_from_char_code(buff: &mut Vec<String>, random: &mut ThreadRng) -> String {
    let string_from_char_code = "__".to_string() + &random_alphanumeric(3, 10, random);
    let vec = format_arguments(7, &string_from_char_code, random);
    buff.push("/*".to_string());
    buff.push(vec.index(1).to_string());
    buff.push("*/var/*".to_string());
    buff.push(vec.index(2).to_string());
    buff.push("*/".to_string());
    buff.push(vec.index(0).to_string());
    buff.push("/*".to_string());
    buff.push(vec.index(3).to_string());
    buff.push("*/=\\u0053\\u0074\\u0072\\u0069\\u006e\\u0067\n/*".to_string());
    buff.push(vec.index(4).to_string());
    buff.push("*/./*".to_string());
    buff.push(vec.index(5).to_string());
    buff.push("*/\\u0066r\\u006fm\\u0043ha\\u0072C\\u006fde/*".to_string());
    buff.push(vec.index(6).to_string());
    buff.push("*/;\n".to_string());
    return string_from_char_code;
}

fn format_arguments(count: i32, str: &String, random: &mut ThreadRng) -> Vec<String> {
    let mut first_fixed_parameters: Vec<String> = Vec::new();
    first_fixed_parameters.push(str.to_string());
    let mut vec: Vec<String> = Vec::with_capacity(count as usize);
    return if first_fixed_parameters.is_empty() {
        for _ in 0..vec.capacity() {
            vec.push(random_alphanumeric(5, 20, random));
        }
        vec
    } else {
        for i in 0..first_fixed_parameters.len() {
            vec.push(String::from(first_fixed_parameters.index(i)));
        }
        for _ in first_fixed_parameters.len()..count as usize {
            vec.push(random_alphanumeric(5, 20, random));
        }
        vec
    };
}

fn format_arguments_empty(count: i32, random: &mut ThreadRng) -> Vec<String> {
    let mut vec: Vec<String> = Vec::with_capacity(count as usize);
    for _ in 0..vec.capacity() {
        vec.push(random_alphanumeric(5, 20, random));
    }
    vec
}

fn format_arguments2(count: i32, str: &String, str2: &String, random: &mut ThreadRng) -> Vec<String> {
    let mut first_fixed_parameters: Vec<String> = Vec::with_capacity(2);
    first_fixed_parameters.push(str.to_string());
    first_fixed_parameters.push(str2.to_string());

    let mut vec: Vec<String> = Vec::with_capacity(count as usize);
    for i in 0..first_fixed_parameters.len() {
        vec.push(String::from(first_fixed_parameters.index(i)));
    }
    for _ in first_fixed_parameters.len()..count as usize {
        vec.push(random_alphanumeric(5, 20, random));
    }
    vec
}

fn number_format(code: &i32, random: &mut ThreadRng) -> String {
    let rnd = random_int(123, random) + 100;
    let o = rnd % 17;
    return if o == 0 {
        "0x".to_string() + &format!("{:02x}", code)
    } else if o == 1 {
        "-1-~/*".to_string() + &random_alphanumeric(2, 5, random) + "*/(0x" + &format!("{:02x}", code) + "^0)"
    } else if o == 2 {
        code.to_string() + &random_int(10, random).to_string() + "/0xA"
    } else if o == 3 {
        "Math.abs(".to_string() + &code.to_string() + ")&-1"
    } else if o == 4 {
        "0".to_string() + &format!("{:o}", code)
    } else if o == 5 {
        code.to_string() + "&(-1^0x00)"
    } else if o == 6 {
        "0x0|0x".to_string() + &format!("{:02x}", code)
    } else if o == 7 {
        let objects = format_arguments_empty(2, random);
        "~/*".to_string() + &objects.index(0) + "*/~/*" + &objects.index(1) + "*/" + &code.to_string()
    } else if o == 8 {
        "~(0x".to_string() + &format!("{:02x}", code) + "^/*" + &random_alphanumeric(2, 5, random) + "*/-1)"
    } else if o == 9 {
        "0x".to_string() + &format!("{:02x}", code) + &random_int(10, random).to_string() + &random_int(10, random).to_string() + "/0400"
    } else if o == 10 {
        let object1 = format_arguments2(3, &random_int(10, random).to_string(), &random_int(10, random).to_string(), random);
        "0x".to_string() + &format!("{:02x}", code) + &object1.index(0) + &object1.index(1) + ">>/*" + &object1.index(2) + "*/4>>4"
    } else if o == 11 {
        code.to_string() + "/*" + &random_string(2, random) + "*/"
    } else {
        code.to_string()
    };
}


fn random_alphanumeric(min: i32, max: i32, random: &mut ThreadRng) -> String {
    random_string(min + random_int(max - min, random), random)
}


fn random_int(bone: i32, random: &mut ThreadRng) -> i32 {
    // let mut RANDOM: ThreadRng = rand::thread_rng();
    random.gen_range(0..bone) // [0, bone)
}

// fn random_u8(bone: u8, random: &mut ThreadRng) -> u8 {
//     // let mut RANDOM: ThreadRng = rand::thread_rng();
//     random.gen_range(0..bone)
// }

// const BASE_STR: &str = "qwertyuioplkjhgfdsazxcvbnmMNBVCXZASDFGHJKLPOIUYTREWQ1234567890";

fn random_string(bone: i32, random: &mut ThreadRng) -> String {
    let BASE_STR: &str = &"qwertyuioplkjhgfdsazxcvbnmMNBVCXZASDFGHJKLPOIUYTREWQ1234567890"[..];
    // bast_str.len() == 62
    let len = BASE_STR.len();
    let mut result = String::with_capacity(bone as usize);
    for _ in 1..=bone {
        let i: usize = random_int(len as i32, random) as usize; // 0 - 62
        let i1 = i + 1;
        let s = &BASE_STR[i..i1];
        result.push_str(s);
    }
    result
    // "xxx".to_string()
}