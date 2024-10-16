package com.GaoQue.service.AIPrice;

import com.GaoQue.model.RicePrice;
import com.GaoQue.repository.RicePriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class RicePriceService {

    @Autowired
    private RicePriceRepository ricePriceRepository;

    private static final String API_URL = "https://wifeed.vn/api/du-lieu-vimo/hang-hoa/gia-hang-hoa-trong-nuoc/ngay?page=1&limit=100&apikey=demo";

    public void fetchAndSaveRicePrices() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(API_URL, Map.class);

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

        for (Map<String, Object> record : data) {
            RicePrice ricePrice = new RicePrice();
            ricePrice.setNgay((String) record.get("ngay"));
            ricePrice.setLuaTuoiHatDai(parseDouble(record.get("lua_tuoi_tai_ruong_hat_dai")));
            ricePrice.setLuaTuoiLuaThuong(parseDouble(record.get("lua_tuoi_tai_ruong_lua_thuong")));
            ricePrice.setLuaKhoHatDai(parseDouble(record.get("lua_kho_uot_tai_kho_hat_dai")));
            ricePrice.setLuaKhoLuaThuong(parseDouble(record.get("lua_kho_uot_tai_kho_lua_thuong")));

            ricePriceRepository.save(ricePrice);
        }
    }

    private double parseDouble(Object value) {
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }
}

