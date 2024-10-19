package org.zerock.b01.naver;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zerock.b01.naver.dto.DrivingRequest;
import org.zerock.b01.naver.dto.DrivingResponse;
import org.zerock.b01.naver.dto.SearchLocalReq;
import org.zerock.b01.naver.dto.SearchLocalRes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class NaverClient {
        // yaml 파일 사용하는데 @Value 어노테이션을 사용하며
        // 내부에 "${}"형태로 yaml에 설정한 대로 기입
        @Value("${naver.client.id}")
        private String naverClientId;

        @Value("${naver.client.secret}")
        private String naverSecret;

        @Value("${naver.url.search.local}")
        private String naverLocalSearchUrl;

        @Value("${naver.url.search.driving}")
        private String naverDrivingSearchUrl;

        public SearchLocalRes localSearch(SearchLocalReq searchLocalReq) {
                try {
                        String query = URLEncoder.encode(searchLocalReq.getQuery(), StandardCharsets.UTF_8);
                        String apiURL = naverLocalSearchUrl + "?query=" + query;

                        URL url = new URL(apiURL);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", naverClientId);
                        con.setRequestProperty("X-NCP-APIGW-API-KEY", naverSecret);

                        int responseCode = con.getResponseCode();
                        BufferedReader br;
                        if (responseCode == 200) {
                                br = new BufferedReader(
                                                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                        } else {
                                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                        }

                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = br.readLine()) != null) {
                                response.append(inputLine);
                        }
                        br.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONArray addresses = jsonResponse.getJSONArray("addresses");

                        SearchLocalRes result = new SearchLocalRes();
                        result.setAddresses(new ArrayList<>());

                        for (int i = 0; i < addresses.length(); i++) {
                                JSONObject address = addresses.getJSONObject(i);
                                SearchLocalRes.Address addressObj = new SearchLocalRes.Address();
                                addressObj.setX(address.getString("x"));
                                addressObj.setY(address.getString("y"));
                                result.getAddresses().add(addressObj);
                        }

                        return result;
                } catch (Exception e) {
                        throw new RuntimeException("Failed to perform local search", e);
                }
        }

        public DrivingResponse Driving(DrivingRequest drivingRequest) {
                try {
                        String start = URLEncoder.encode(drivingRequest.getStart(), StandardCharsets.UTF_8);
                        String goal = URLEncoder.encode(drivingRequest.getGoal(), StandardCharsets.UTF_8);
                        String apiURL = naverDrivingSearchUrl + "?start=" + start + "&goal=" + goal;

                        URL url = new URL(apiURL);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", naverClientId);
                        con.setRequestProperty("X-NCP-APIGW-API-KEY", naverSecret);

                        int responseCode = con.getResponseCode();
                        BufferedReader br;
                        if (responseCode == 200) {
                                br = new BufferedReader(
                                                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                        } else {
                                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                        }

                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = br.readLine()) != null) {
                                response.append(inputLine);
                        }
                        br.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONObject routeObject = jsonResponse.getJSONObject("route");
                        JSONArray traoptimalArray = routeObject.getJSONArray("traoptimal");
                        JSONObject firstTraoptimal = traoptimalArray.getJSONObject(0);
                        JSONObject summaryObject = firstTraoptimal.getJSONObject("summary");

                        DrivingResponse result = new DrivingResponse();
                        DrivingResponse.Route route = new DrivingResponse.Route();
                        DrivingResponse.Route.Traoptimal traoptimal = new DrivingResponse.Route.Traoptimal();
                        DrivingResponse.Route.Traoptimal.Summary summary = new DrivingResponse.Route.Traoptimal.Summary();

                        summary.setDuration(summaryObject.getInt("duration"));
                        traoptimal.setSummary(summary);
                        route.setTraoptimal(new DrivingResponse.Route.Traoptimal[] { traoptimal });
                        result.setRoute(route);

                        return result;
                } catch (Exception e) {
                        throw new RuntimeException("경로 검색 실패", e);
                }
        }

}
