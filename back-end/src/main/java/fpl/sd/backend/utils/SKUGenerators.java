package fpl.sd.backend.utils;

import org.springframework.stereotype.Service;

@Service
public class SKUGenerators {
    public String generateSKU(String brand, String name, Double size) {
        String brandCode = brand.substring(0, Math.min(3, brand.length())).toUpperCase();
        String[] words = name.split(" ");
        String nameCode;
        
        if (words.length >= 3) {
            // Tên có 3 từ trở lên: Lấy 2 ký tự đầu của 2 từ đầu + 1 ký tự đầu của từ thứ 3
            nameCode = (words[0].substring(0, Math.min(2, words[0].length())) + 
                       words[1].substring(0, Math.min(2, words[1].length())) + 
                       words[2].charAt(0)).toUpperCase();
        } else if (words.length == 2) {
            // Tên có 2 từ: Lấy 2 ký tự đầu của mỗi từ + 1 ký tự cuối của từ thứ 2
            nameCode = (words[0].substring(0, Math.min(2, words[0].length())) + 
                       words[1].substring(0, Math.min(2, words[1].length())) + 
                       words[1].charAt(words[1].length() - 1)).toUpperCase();
        } else {
            // Tên có 1 từ: Lấy tối đa 5 ký tự đầu
            nameCode = name.substring(0, Math.min(5, name.length())).toUpperCase();
        }

        String sizeCode = size % 1 == 0 ? String.format("%d", size.intValue()) : String.format("%.1f", size);

        return String.format("%s-%s-%s", brandCode, nameCode, sizeCode);
    }

}
