package org.zerock.b01.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.zerock.b01.controller.PlaceController;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.Place;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.zerock.b01.dto.PlaceSearchDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
public class PlaceRepositoryTests {

    @Autowired
    private PlaceRepository placeRepository;


    @Test
    public void testInsert(){
        Place place1 = Place.builder()
                .pord(11)
                .p_name("을지다락 강남")
                .p_category("양식")
                .p_content(null)
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fvideo-phinf.pstatic.net%2F20240920_40%2F1726792574994KnzQC_JPEG%2F46o3j90jLU_03.jpg")
                .p_address("서울 강남구 강남대로96길 22 2층2신분당강남역 11번 출구에서 269m미터")
                .p_call("0507-1343-9474")
                .p_star(4.53F)
                .p_site("https://catchtable.co.kr/gangnamdarak")
                .p_opentime("\n" +
                        "11:30에 영업 시작\n" +
                        "11시 30분에 영업 시작\n" +
                        "일\n" +
                        "11:30 - 21:15\n" +
                        "20:30 라스트오더\n" +
                        "월\n" +
                        "11:30 - 21:15\n" +
                        "15:10 - 16:30 브레이크타임\n" +
                        "14:25, 20:30 라스트오더\n" +
                        "화\n" +
                        "11:30 - 21:15\n" +
                        "15:10 - 16:30 브레이크타임\n" +
                        "14:25, 20:30 라스트오더\n" +
                        "수\n" +
                        "11:30 - 21:15\n" +
                        "15:10 - 16:30 브레이크타임\n" +
                        "14:25, 20:30 라스트오더\n" +
                        "목\n" +
                        "11:30 - 21:15\n" +
                        "15:10 - 16:30 브레이크타임\n" +
                        "14:25, 20:30 라스트오더\n" +
                        "금\n" +
                        "11:30 - 21:15\n" +
                        "15:10 - 16:30 브레이크타임\n" +
                        "14:25, 20:30 라스트오더\n" +
                        "토\n" +
                        "11:30 - 21:15\n" +
                        "20:30 라스트오더\n" +
                        "- 평일 런치, 디너 라스트 오더 (14:30/20:30)\n")
                .p_park("대중교통 이용시,\n" +
                        "지하철 2호선, 신분당선 강남역 11번 출구로 나와주세요 :)\n" +
                        "\n" +
                        "강남대로변을 따라 도보2분 거리에 뉴발란스 매장이 있습니다. 그곳을 끼고 오른쪽 골목으로 올라가서 도보 5분 후에 을지다락이 2층에 보입니다!\n" +
                        "\n" +
                        "강남역 12번출구로 나올 경우,\n" +
                        "도보 3분 후에, 바나프레소 카페에서 좌회전 후, 도보 3분 후, 을지다락이 보일 거에요 :)\n" +
                        "\n" +
                        "애플스토어, 강남역cgv, 강남역롯데시네마, 강남역메가박스에서도 가깝습니다~\n" +
                        "\n" +
                        "자가용 이용시,\n" +
                        "죄송하지만, 주차공간이 따로 없어서, 매장 근처 유료 주차장 이용을 부탁 드릴게요~\n" +
                        "\n" +
                        "역삼문화공원제1호공영주차장\n" +
                        "강남구 테헤란로7길 21\n" +
                        "5분당 300원\n" +
                        "\n" +
                        "강남역파스타, 강남역브런치, 강남역양식, 강남역청첩장모임, 강남역데이트, 강남역점심, 강남역소개팅, 강남역맛집")
                .build();
        Place result = placeRepository.save(place1);

        log.info("pord: " + result.getPord());
        Place place2 = Place.builder()
                .pord(12)
                .p_name("디슬로우 청담")
                .p_category("피자")
                .p_content("'디슬로우' 는 디슬로우 만의 감성으로 해석한 시카고 스타일 딥 디쉬 피자와 크래커 씬 피자를 즐기실 수 있는 공간 입니다.\n" +
                        "\n" +
                        "딥디쉬 메뉴, 단체 예약 문의는 매장으로 전화 주시면 감사하겠습니다.\n" +
                        "\n" +
                        "발렛 주차 가능합니다.\n" +
                        "(매장 앞 검정색 발렛 파킹 부스에 차량을 맡기실 수 있습니다.)\n" +
                        "\n" +
                        "(브레이크 타임이 없습니다.)\n" +
                        "\n" +
                        "*콜키지는 매장으로 문의 바랍니다.\n" +
                        "접기")
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20230627_128%2F16878582143040C0NW_JPEG%2FIMG_7266-1.JPEG")
                .p_address("서울 강남구 선릉로160길 7 지하1층수인분당압구정로데오역 4번 출구에서 91m미터")
                .p_call("0507-1330-8419")
                .p_star(4.85F)
                .p_site(null)
                .p_opentime("\n" +
                        "05:30에 라스트오더\n" +
                        "5시 30분에 라스트오더\n" +
                        "토\n" +
                        "12:00 - 06:00\n" +
                        "05:30 라스트오더\n" +
                        "일\n" +
                        "12:00 - 23:30\n" +
                        "23:00 라스트오더\n" +
                        "월\n" +
                        "12:00 - 23:30\n" +
                        "23:00 라스트오더\n" +
                        "화\n" +
                        "12:00 - 06:00\n" +
                        "05:30 라스트오더\n" +
                        "수\n" +
                        "12:00 - 06:00\n" +
                        "05:30 라스트오더\n" +
                        "목\n" +
                        "12:00 - 06:00\n" +
                        "05:30 라스트오더\n" +
                        "금\n" +
                        "12:00 - 06:00\n" +
                        "05:30 라스트오더\n")
                .p_park("[주차정보] [발렛주차가능]\n" +
                        "- 매장 앞 검정색 발렛 파킹 부스에 차량을 맡기실 수 있습니다.\n" +
                        "- (유료) 2시간 이용 하실 시 5000원\n" +
                        "\n" +
                        "1) 도보 이용시\n" +
                        "- 압구정 로데오역 4번출구 도보 1분거리\n" +
                        "\n" +
                        "2) 자가용 이용시\n" +
                        "- 도로명 : 서울 강남구 선릉로160길 7 지하 1층\n" +
                        "- 지번 : 서울 강남구 청담동 84-19번지 지하 1층")
                .build();
        Place result2 = placeRepository.save(place2);
        log.info("pord: " + result2.getPord());

        log.info("pord: " + result.getPord());

        Place place3 = Place.builder()
                .pord(13)
                .p_name("어거스트 힐 강남점")
                .p_category("양식")
                .p_content("<식사 이용 시간 >\n" +
                        "식사 이용시간은 100분으로, 매장상황에 따라 100분 이상은 어려울 수 있음을 사전 공지 및 양해바랍니다.\n" +
                        "외부음식 취식이 어려운 점 또한 양해부탁드립니다\n" +
                        "\n" +
                        "< 주차 안내 >\n" +
                        "* 전용 무료 주차는 2대 까지만 가능하십니다.\n" +
                        " (주차장 예약 불가)\n" +
                        "\n" +
                        "<발렛 파킹 안내>\n" +
                        "매장 앞 배너--> 발렛 번호로 전화하신 후\n" +
                        "'어거스트 힐 왔습니다' 말씀해 주시면 기사님이 오십니다.\n" +
                        "\n" +
                        "**30분 지원해 드리고, 10분당 1,000원 개인부담 발생합니다.**\n" +
                        "\n" +
                        "식사 후 출차 요청 전화하신 후,\n" +
                        "결제 시 발렛 맡겼다고 하시면 쿠폰을 따로 챙겨드립니다.\n" +
                        "영수증과 쿠폰을 같이 지참 해주세요. (카드 결제 가능)\n" +
                        "\n" +
                        "강남역(11번출구)/신논현역(5,6번출구)에 위치한 합리적인 가격의\n" +
                        "아메리칸 스테이크 하우스 어거스트 힐입니다.\n" +
                        "다양한 부문에서 선별된 탑초이스 이상급의 육류로\n" +
                        "일정한 온도의 저온 숙성고에서\n" +
                        "240시간 이상 웻에이징을 하여 육즙과 풍미를 극대화하여\n" +
                        "고객분들께 서브합니다.\n" +
                        "\n" +
                        "합리적인 가격으로 만족스러운 식사를 제공하겠습니다.\n" +
                        "접기")
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20240220_241%2F1708398564214Rqc5U_JPEG%2F%25C6%25BC%25BA%25BB.jpg")
                .p_address("서울 강남구 강남대로106길 259신분당신논현역 6번 출구에서 259m미터")
                .p_call("0507-1478-8013")
                .p_star(4.56F)
                .p_site("http://www.instagram.com/augusthill_official_/")
                .p_opentime("\n" +
                        "11:30에 영업 시작\n" +
                        "11시 30분에 영업 시작\n" +
                        "일\n" +
                        "11:30 - 22:00\n" +
                        "21:00 라스트오더\n" +
                        "월\n" +
                        "11:30 - 22:00\n" +
                        "21:00 라스트오더\n" +
                        "화\n" +
                        "11:30 - 22:00\n" +
                        "21:00 라스트오더\n" +
                        "수\n" +
                        "11:30 - 22:00\n" +
                        "21:00 라스트오더\n" +
                        "목\n" +
                        "11:30 - 22:00\n" +
                        "21:00 라스트오더\n" +
                        "금\n" +
                        "11:30 - 22:00\n" +
                        "21:00 라스트오더\n" +
                        "토\n" +
                        "11:30 - 22:00\n" +
                        "21:00 라스트오더\n" +
                        "- 식사 이용 시간 : 100분\n")
                .p_park("1. 신논현역 5,6번 출구에서 출발 시,\n" +
                        "1블럭 직진 대연빌딩 좌측 골목 언덕길('원퍼밀커피' 보이는 골목)로\n" +
                        "4블럭 직진하시면 좌측에 위치해 있습니다.\n" +
                        "\n" +
                        "2. 강남역 출발 시,\n" +
                        "강남역 11번 출구에서 4블럭 직진 후,나이키 매장 옆 공사중인 건물 우측 골목 언덕길('원퍼밀커피' 보이는 골목)로 4블럭 직진하시면 좌측에 위치해 있습니다.")
                .build();
        Place result3 = placeRepository.save(place3);
        log.info("pord: " + result3.getPord());

        Place place4 = Place.builder()
                .pord(14)
                .p_name("울프강 스테이크하우스")
                .p_category("스테이크,립")
                .p_content("세계적으로 유명한 스테이크하우스의 한국 1호점,\n" +
                        "울프강 스테이크하우스 코리아\n" +
                        "\n" +
                        "안녕하세요\n" +
                        "뉴욕 정통 드라이에이징 스테이크하우스,\n" +
                        "울프강 스테이크하우스 코리아입니다.\n" +
                        "\n" +
                        "소중한 날, 지인분들과 특별한 식사를 위해\n" +
                        "저희 브랜드를 찾아주셔서 대단히 감사합니다.\n" +
                        "\n" +
                        "울프강 스테이크하우스에서는 네이버 온라인 예약 및 네이버 톡톡과 함께 카카오톡 플러스친구 상담을 추가로 운영하고 있습니다.\n" +
                        "\n" +
                        "울프강 스테이크하우스의 네이버 톡톡, 카카오톡 공식 채널에서\n" +
                        "예약 및 식사에 관한 상담이 가능합니다.\n" +
                        "\n" +
                        "울프강 스테이크하우스에 찾아주시는 모든 고객님들께\n" +
                        "더욱 나은 서비스를 제공할 수 있도록 진심을 다해 노력하겠습니다.\n" +
                        "\n" +
                        "고맙습니다.\n" +
                        "\n" +
                        "- 울프강 스테이크하우스 코리아 임직원 일동 -\n" +
                        "접기")
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20160630_10%2F14672593049284cNr4_JPEG%2F176580533161010_27.jpeg")
                .p_address("서울 강남구 선릉로152길 21 영인빌딩 1층수인분당압구정로데오역 4번 출구에서 371m미터")
                .p_call("0507-1486-8700")
                .p_star(4.5F)
                .p_site("http://wolfgangssteakhouse.co.kr/")
                .p_opentime("\n" +
                        "11:00에 영업 시작\n" +
                        "11시 0분에 영업 시작\n" +
                        "매일\n" +
                        "11:00 - 22:00\n" +
                        "20:30 라스트오더\n" +
                        "- 예약률에 따라 예약이 조기 마감될 수 있습니다.\n")
                .p_park(null)
                .build();
        Place result4 = placeRepository.save(place4);
        log.info("pord: " + result4.getPord());

        Place place5 = Place.builder()
                .pord(15)
                .p_name("미도인 강남")
                .p_category("양식")
                .p_content("과거와 현재, 동양과 서양이 공존하는 공간으로 젋은 연인들의 데이트 장소나 친구들과의 모임, 특별한 날에 찾아볼 만한 공간에서 특별한 순간을 만들어줍니다.\n" +
                        "\n" +
                        "먹는 즐거움과 보는 즐거움, 머무는 즐거움으로 만족을 드리는 미도인에서 특별한 경험을 해보시기 바랍니다.")
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20230626_128%2F168775914700784teo_JPEG%2FIMG_4115.jpeg")
                .p_address("서울 강남구 강남대로102길 16 지상 2층2신분당강남역 11번 출구에서 414m미터")
                .p_call("0507-1441-2065")
                .p_star(4.4F)
                .p_site(null)
                .p_opentime("\n" +
                        "11:30에 영업 시작\n" +
                        "11시 30분에 영업 시작\n" +
                        "일\n" +
                        "11:30 - 21:00\n" +
                        "월\n" +
                        "11:30 - 21:00\n" +
                        "화\n" +
                        "11:30 - 21:00\n" +
                        "수\n" +
                        "11:30 - 21:00\n" +
                        "목\n" +
                        "11:30 - 21:00\n" +
                        "금\n" +
                        "11:30 - 21:00\n" +
                        "토\n" +
                        "11:30 - 21:00\n")
                .p_park("강남역: 2호선 강남역 10번 출구로 나와서 도보로 약 6분 거리에 위치합니다.\n" +
                        "\n" +
                        "주차가 되지않습니다 양해 부탁드립니다.\n" +
                        "강남 CGV 영화관 위로 쭉 올라오시다보면 포토이즘 바로 옆에 입구가\n" +
                        "준비되어 있습니다.")
                .build();
        Place result5 = placeRepository.save(place5);
        log.info("pord: " + result5.getPord());

        Place place6 = Place.builder()
                .pord(16)
                .p_name("틴틴 강남점")
                .p_category("이탈리아음식")
                .p_content("<캐주얼 레스토랑 & 브런치 카페 틴틴>\n" +
                        "곳곳의 감성적인 포토존과 틴틴만의 시그니처 메뉴들\n" +
                        "\n" +
                        "단체테이블, 야외테이블(애견동반가능) 다수 보유\n" +
                        "소개팅, 청첩장모임, 동호회, 동창회 등 모임 가능\n" +
                        "\n" +
                        "예약은 네이버,전화 및 인스타DM으로 가능합니다!\n" +
                        "틴틴에서 러블리한 시간 보내세요 :) 감사합니다")
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20230329_38%2F1680077186912m9uzX_JPEG%2F%25C5%25EB%25BA%25A3%25C0%25CC%25C4%25C1.jpg")
                .p_address("서울 강남구 봉은사로2길 199신분당신논현역 4번 출구에서 174m미터")
                .p_call("0507-1476-0558")
                .p_star(null)
                .p_site("https://www.instagram.com/tteenteen_official")
                .p_opentime("\n" +
                        "11:30에 영업 시작\n" +
                        "11시 30분에 영업 시작\n" +
                        "매일\n" +
                        "11:30 - 22:00\n" +
                        "20:00 라스트오더\n")
                .p_park("신논현역 4번 출구에서 5시방향으로 170m정도 위치에 있습니다.")
                .build();
        Place result6 = placeRepository.save(place6);
        log.info("pord: " + result6.getPord());

        Place place7 = Place.builder()
                .pord(17)
                .p_name("꽁티드툴레아")
                .p_category("브런치")
                .p_content("꽁티드툴레아는 내추럴 프래그런스 브랜드입니다. 꽁티드툴레아가 추구하는 개성과 라이프스타일의 방향성을 명확히 프레젠테이션하기 위해서 카페를 운영하게 되었습니다. 자연친화적인 요소들을 담고 있으며 브런치 및 와인을 즐길 수 있는 공간입니다.")
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20200914_239%2F1600012748465FVukt_JPEG%2FdlyCQ41XRzwZcpmE1sPYTpuY.jpeg.jpg")
                .p_address("서울 강남구 도산대로49길 39수인분당압구정로데오역 5번 출구에서 437m미터")
                .p_call("0507-1325-8490")
                .p_star(null)
                .p_site("http://www.contedetulear.com")
                .p_opentime("\n" +
                        "11:00에 영업 시작\n" +
                        "11시 0분에 영업 시작\n" +
                        "일\n" +
                        "11:00 - 23:00\n" +
                        "월\n" +
                        "11:00 - 24:00\n" +
                        "화\n" +
                        "11:00 - 24:00\n" +
                        "수\n" +
                        "11:00 - 24:00\n" +
                        "목\n" +
                        "11:00 - 24:00\n" +
                        "금\n" +
                        "11:00 - 24:00\n" +
                        "토\n" +
                        "11:00 - 24:00\n" +
                        "- 브레이크 타임(5시-6시) : 디저트 및 음료 주문가능\n")
                .p_park("*발렛파킹이 가능하며 매장 오시기전에 위치해 있고 일방통행길입니다.\n" +
                        "'서울 강남구 도산대로49길 35'를 검색하고 오시면 빨간글씨의 부동산과 청춘이라고 적혀있는\n" +
                        "발렛부스에 차를 맡기시고 오시면 됩니다.\n" +
                        "*발렛이 부득이하게 만차일 경우 근처 유료주차장 이용 부탁드립니다.\n" +
                        "(주차비용 지원되지않음)")
                .build();
        Place result7 = placeRepository.save(place7);
        log.info("pord: " + result7.getPord());

        Place place8 = Place.builder()
                .pord(18)
                .p_name("트리드")
                .p_category("양식")
                .p_content("Diversity, Delicious, Delight - t r i d 입니다.\n" +
                        "\n" +
                        "* 매 주 월요일/일요일은 정기 휴무입니다.\n" +
                        "* 예약은 http://catchtable.co.kr/trid (캐치테이블) 또는 유선문의 부탁드립니다.\n" +
                        "* 노 키즈 존으로 운영되고 있으며 미취학아동 이용 불가능합니다.\n" +
                        "* 단체나 기타 문의사항은 유선문의 주시면 감사하겠습니다.")
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20231123_78%2F1700736475707ozbTJ_JPEG%2FKakaoTalk_20230926_172536936_09.jpg")
                .p_address("서울 강남구 선릉로162길 16 2층수인분당압구정로데오역 3번 출구에서 166m미터")
                .p_call("02-512-8312")
                .p_star(4.7F)
                .p_site("https://www.instagram.com/tridseoul")
                .p_opentime("오늘 휴무\n" +
                        "매주 일요일 휴무\n" +
                        "매주 일요일 휴무\n" +
                        "일\n" +
                        "정기휴무 (매주 일요일)\n" +
                        "월\n" +
                        "정기휴무 (매주 월요일)\n" +
                        "화\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 18:00 브레이크타임\n" +
                        "수\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 18:00 브레이크타임\n" +
                        "목\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 18:00 브레이크타임\n" +
                        "금\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 18:00 브레이크타임\n" +
                        "토\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 18:00 브레이크타임\n" +
                        "- Last order 13:30, 19:30\n")
                .p_park("압구정로데오역 3번출구로 나오셔서 직진하신후 꼬르소꼬모 건물 맞은편 좁은 골목으로 들어오셔서 좌측 바라보시면 1층에 BAR TEASENT가 있는 건물 2층에 위치해 있습니다.")
                .build();
        Place result8 = placeRepository.save(place8);
        log.info("pord: " + result8.getPord());

        Place place9 = Place.builder()
                .pord(19)
                .p_name("이식일사")
                .p_category("양식")
                .p_content("신논현역 맛집 양식 이식일사\n" +
                        "\n" +
                        "< 주차 안내 >\n" +
                        "*건물 뒷편의 주차 자리가 있으나 두자리로 다소 협소하여 서울특별시 강남구 강남대로106길 25-1 세명주차장 이용을 추천드립니다.\n" +
                        "\n" +
                        "<이식일사>\n" +
                        "안녕하세요 둘이먹다 한명 죽어도 모르는 이식일사입니다.\n" +
                        "\n" +
                        "소개팅, 데이트, 가벼운 식사까지 언제와도 완벽한 선택이 되도록 노력하겠습니다. 강남역 떡볶이, 경양식 돈까스 신논현역 맛집 이식일사입니다.\n" +
                        "\n" +
                        "저희 이식일사에서는 24시간 수비드한 오겹살스테이크와 한우육회파스타, 함박, 리조또, 파스타, 고메버터떡볶이, 수제 밀크쉐이크등을 판매하고 있습니다. 어린아이부터 성인까지 남녀노소 가리지 않고 여러분의 입맛을 사로잡을 수 있는 최고의 맛으로 보답하겠습니다.\n" +
                        "신논현역에 위치한 맛집 이식일사로 오세요 :)\n" +
                        "접기")
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20240927_284%2F1727440202735bLOvg_JPEG%2F%25C4%25A1%25C1%25EE%25BE%25D8%25C5%25BE_1_%25281%2529.jpg")
                .p_address("서울 강남구 봉은사로4길 17 지하 1층9신분당신논현역 4번 출구에서 168m미터")
                .p_call("0507-1319-7102")
                .p_star(null)
                .p_site(null)
                .p_opentime("\n" +
                        "12:00에 영업 시작\n" +
                        "12시 0분에 영업 시작\n" +
                        "일\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 16:00 브레이크타임\n" +
                        "21:30 라스트오더\n" +
                        "월\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 17:00 브레이크타임\n" +
                        "21:30 라스트오더\n" +
                        "화\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 17:00 브레이크타임\n" +
                        "21:30 라스트오더\n" +
                        "수\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 17:00 브레이크타임\n" +
                        "21:30 라스트오더\n" +
                        "목\n" +
                        "12:00 - 22:00\n" +
                        "15:00 - 17:00 브레이크타임\n" +
                        "21:30 라스트오더\n" +
                        "금\n" +
                        "12:00 - 23:00\n" +
                        "15:00 - 17:00 브레이크타임\n" +
                        "22:30 라스트오더\n" +
                        "토\n" +
                        "12:00 - 23:00\n" +
                        "15:00 - 16:00 브레이크타임\n" +
                        "22:30 라스트오더\n")
                .p_park("신논현역 4번 출구로 나오시자마자 골목으로 들어오신다음 50m직진입니다. 씨유CU가 보이면 CU 편의점 골목으로 80m오시면 이식일사입니다.")
                .build();
        Place result9 = placeRepository.save(place9);
        log.info("pord: " + result9.getPord());

        Place place10 = Place.builder()
                .pord(20)
                .p_name("달마시안 압구정점")
                .p_category("양식")
                .p_content("압구정에 압구정로데오맛집 유럽식 레스토랑 달마시안은 도심 속에서 마치 유럽의 정원에 온 듯한 분위기를 자아내는 매력적인 공간입니다.\n" +
                        "입구에는 잘 차려진 정원과 예쁜 꽃분수가 있으며, 도심의 번잡함을 잊게 만드는 한적하고 여유로운 분위기를 제공합니다.\n" +
                        "날씨가 좋은 날에는 분수 주변에 앉아 여유로운 식사를 즐길 수도 있습니다!\n" +
                        "\n" +
                        "달마시안의 메뉴는 유럽식 브런치, 파스타 그리고 각종 플레이트로 이루어져 있습니다.\n" +
                        "인기메뉴로는 브런치인 달마시안 블랙퍼스트, 부드러운 에그베네딕트, 육즙이 가득한 스테이크 등이 있습니다.\n" +
                        "모든 메뉴는 신선한 재료를 사용하여 정성스럽게 조리되며, 맛과 비주얼 모두 만족시킬 수 있도록 항상 노력하고 있습니다.\n" +
                        "\n" +
                        "달마시안은 다양한 음료 메뉴도 제공합니다. 시원한 맥주와 세계 각국의 와인을 맛볼 수 있으며, 와인 리스트는 특히 잘 구성되어 있어 와인과 함께 하는 식사는 달마시안에서의 분위기를 한층 더 고급스럽게 만들어줍니다.\n" +
                        "이 외에 아메리카노와 과일주스 등 무알콜 음료도 다양하게 준비되어 있습니다.\n" +
                        "\n" +
                        "레스토랑은 가든과 1층, 2층으로 이루어져 있어 각 층마다 다른 매력을 느낄 수 있습니다.\n" +
                        "가든은 꽃분수를 바라보며 여유로운 분위기를 만끽하면서 식사할 수 있는 공간으로, 날씨가 좋은 날에는 특히 인기가 많습니다.\n" +
                        "1층과 2층은 아늑한 분위기로, 편안한 식사를 즐기기에 좋습니다.\n" +
                        "2인~6인 까지 앉을 수 있는 테이블로 이루어져 있지만 손님 요청에 따라 많은 인원이 오셔도 앉으실 수 있게 세팅이 가능합니다.\n" +
                        "\n" +
                        "압구정에 위치한 유럽식 레스토랑 달마시안은 아름다운 정원과 로맨틱한 분위기, 그리고 맛있는 음식과 음료로 많은 이들에게 사랑받는 곳입니다. 특별한 날을 기념하거나 사랑하는 사람과의 데이트를 위해 방문하기에 더없이 좋은 장소입니다.\n" +
                        "달마시안에서의 한 끼 식사를 최고의 경험으로 기억하실 수 있게 노력하겠습니다.\n" +
                        "\n" +
                        "*운영시간\n" +
                        "@dalmatian_dosan / MON-FRI : 09:00 ~ 17:00 - Brunch\n" +
                        " SAT,SUN : 09:00 ~ 17:00 - Brunch\n" +
                        "Kitchen Break Time / 17:00 ~ 18:00\n" +
                        "@dalmatian_dining / 18:00 ~ 23:00 - Dining\n" +
                        "\n" +
                        "*예약문의\n" +
                        "매장 연락처는 0507-1491-0926 입니다.\n" +
                        "네이버, 캐치테이블로 예약하실 수 있고 각 플랫폼에 안내사항이 적혀있으니 확인을 부탁드리겠습니다.\n" +
                        "\n" +
                        "*실내 좌석 애견 동반은 애견 가방이나 캐리어,\n" +
                        "유모차 지참 시 이용이 가능합니다:)\n" +
                        "\n" +
                        "*예약은 식사 및 주류(바틀) 고객님만 가능합니다.\n" +
                        "카페 및 디저트 이용 고객님께서는 워크인으로 방문 부탁드립니다.\n" +
                        "\n" +
                        "*예약 시간이 10분 지나면 자동으로 취소되며, 변동 사항 있을 시에는 매장으로 전화 부탁 드립니다.\n" +
                        "\n" +
                        "*예약 가능한 좌석은 가든, 실내1층, 실내2층, 2층 테라스로 구성되어 있으며, 예약하신 순서대로 좋은 자리로 배정해드립니다.\n" +
                        "원하시는 자리가 있을 경우 요청사항에 꼭! 기재 부탁드리며, 최대한 적용해드리나 부득이하게 불가능 할 수도 있는 점 양해 부탁드립니다.\n" +
                        "또한, 자리 이동이 어려우실 수 있으니, 양해 부탁드립니다.\n" +
                        "접기")
                .p_image("https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20210913_18%2F1631492525833HWAbY_JPEG%2F3tYcm3RZh_CgstNdpODxOE2B.jpg")
                .p_address("서울 강남구 압구정로42길 42수인분당압구정로데오역 5번 출구에서 638m미터")
                .p_call("0507-1491-0926")
                .p_star(null)
                .p_site("https://app.catchtable.co.kr/ct/shop/dalmatian")
                .p_opentime("\n" +
                        "09:00에 영업 시작\n" +
                        "9시 0분에 영업 시작\n" +
                        "일\n" +
                        "09:00 - 23:00\n" +
                        "22:00 라스트오더\n" +
                        "월\n" +
                        "09:00 - 23:00\n" +
                        "22:00 라스트오더\n" +
                        "화\n" +
                        "09:00 - 23:00\n" +
                        "22:00 라스트오더\n" +
                        "수\n" +
                        "09:00 - 23:00\n" +
                        "22:00 라스트오더\n" +
                        "목\n" +
                        "09:00 - 23:00\n" +
                        "22:00 라스트오더\n" +
                        "금\n" +
                        "09:00 - 23:00\n" +
                        "22:00 라스트오더\n" +
                        "토\n" +
                        "09:00 - 23:00\n" +
                        "22:00 라스트오더\n" +
                        "- 17:00-18:00 음료와 디저트만 주문 가능합니다.\n")
                .p_park("대중교통 이용시 압구정파출소 버스정류장에서 하차하신 후\n" +
                        "뒤편에 보이는 골목으로 들어가셔서 약 550m 정도 걸어오시면 오른편에 압구정로데오맛집 달마시안이 보입니다.\n" +
                        "\n" +
                        "네비게이션을 사용하실때에는\n" +
                        "압구정로 42길 42\n" +
                        "달마시안 압구정점을 입력하시면 됩니다\n" +
                        "\n" +
                        "발렛주차는 30m 전방 '핑크색 카페' 앞 발렛부스를 이용해주시면 됩니다 :)")
                .build();
        Place result10 = placeRepository.save(place10);
        log.info("pord: " + result10.getPord());
    }

    @Test
    public void testSelect() {
        int pord = 1;

        Optional<Place> result = placeRepository.findById(pord);

        Place place = result.orElseThrow();

        log.info(place);

    }

    @Test
    void testFindAllSortedByPOrd() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("pord").ascending());
        List<Place> places = placeRepository.findAll(pageable).getContent();

        assertNotNull(places);
        // 추가 검증 로직을 여기에 작성합니다.
    }
}










