package org.example.expert.client;

import org.example.expert.client.dto.WeatherDto;
import org.example.expert.domain.common.exception.ServerException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class WeatherClient {

    private final RestTemplate restTemplate;
    private final DateTimeFormatter FORMATTER;

    public WeatherClient(RestTemplateBuilder builder) {
        FORMATTER  = DateTimeFormatter.ofPattern("MM-dd");
        this.restTemplate = builder.build();
    }

    /**
     * api를 통해서 오늘의 날씨를 얻는 메서드
     * @return 오늘의 날씨
     */
    public String getTodayWeather() {
        // Api를 통해서 WeatherDto 형태의 데이터를 받는다
        ResponseEntity<WeatherDto[]> responseEntity =
                restTemplate.getForEntity(buildWeatherApiUri(), WeatherDto[].class);

        // Array형태로 들어있는 내용을 weatherArray에 반환받는다
        WeatherDto[] weatherArray = responseEntity.getBody();

        // 상태가 OK가 아닐 경우
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new ServerException("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: " + responseEntity.getStatusCode());
        }
        /* 수정된 코드 if문 분할
            반환 받은 날씨 Dto가 null이거나 비어있을 경우
         */
        if (weatherArray == null || weatherArray.length == 0) {
            throw new ServerException("날씨 데이터가 없습니다.");
        }

        String today = getCurrentDate();

        // 각 날씨 Dto를 확인하면서 오늘 일자의 날씨가 있는지 확인
        for (WeatherDto weatherDto : weatherArray) {
            if (today.equals(weatherDto.getDate())) {
                return weatherDto.getWeather();
            }
        }

        throw new ServerException("오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.");
    }

    private URI buildWeatherApiUri() {
        return UriComponentsBuilder
                .fromUriString("https://f-api.github.io")
                .path("/f-api/weather.json")
                .encode()
                .build()
                .toUri();
    }

    /**
     * MM-dd 형태로 오늘 날짜를 반환하는 메서드
     * @return 09-09와 같은 형태의 오늘 날짜
     */
    private String getCurrentDate() {
    // 수정된 코드 format 형태를 private final로 이전
        return LocalDate.now().format(FORMATTER);
    }
}
