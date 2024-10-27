from datetime import datetime, timedelta, time

from selenium.webdriver.chrome.service import Service
from sqlalchemy import create_engine, Column, Integer, String, Float, Boolean, ForeignKey, BigInteger, DateTime, Time
from sqlalchemy.orm import sessionmaker
from sqlalchemy.orm import declarative_base
from selenium import webdriver 
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from fastapi import FastAPI
from fastapi.responses import JSONResponse
from datetime import time
app = FastAPI()

import re
import time

import warnings
warnings.filterwarnings('ignore')

from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait

from typing import Optional

from pydantic import BaseModel

class PlaceSearchDTO(BaseModel):
    p_area: Optional[str]
    p_subArea: Optional[str]
    p_category: Optional[str]
    p_count: Optional[int]
    p_keyword: Optional[str]

class PlanDTO(BaseModel):
    writer: Optional[str]
    t_startDateTime: Optional[datetime]
    start_location: Optional[str]
    arrive_location: Optional[str]
    isCar: Optional[bool]

# POST 요청을 받을 엔드포인트 설정
@app.post("/place/list")
async def get_place_list(place_search_dto: PlaceSearchDTO):
    p_count = place_search_dto.p_count or 10
    # 받은 데이터를 처리하거나 가공
    search = f"{place_search_dto.p_area}{ place_search_dto.p_subArea}{ place_search_dto.p_category}{ place_search_dto.p_keyword}".strip()

    list_url = f'https://map.naver.com/p/search/{search}?c=10.00,0,0,0,dh'

    p_imageList = []
    p_nameList = []
    p_starList = []
    p_categoryList = []
    p_addressList = []
    p_parkList = []
    p_timeList = []
    p_callList = []
    p_siteList = []
    p_contentList = []
    p_ordList = []

    p_count = int(p_count)

    breakPoint = False;

    s = Service()
    options = webdriver.ChromeOptions()
    wd = webdriver.Chrome(service=s, options=options)
    wd.get(list_url)
    time.sleep(4)

    # 페이지 다운
    def page_down(num):
        body = wd.find_element(By.TAG_NAME, 'body')
        body.click()
        for i in range(num):
            body.send_keys(Keys.PAGE_DOWN)

    for i in range(1, (p_count // 50) + 2):

        if i == (p_count // 50) + 1:
            eleCount = p_count % 50 + 1
        else:
            eleCount = 51
        # 페이지 로딩 대기
        time.sleep(3)
        wd.switch_to.default_content()
        wd.switch_to.frame('searchIframe')
        time.sleep(2)
        page_down(eleCount)
        time.sleep(2)

        for j in range(1, eleCount):
            try:
                body = wd.find_element(By.CSS_SELECTOR,
                                       '#_pcmap_list_scroll_container > ul > li:nth-child(%d) > div.CHC5F > a > div > div > span.TYaxT' % j)
            except:
                breakPoint = True
                break
            else:
                try:
                    body.click()
                    time.sleep(2)
                    wd.switch_to.default_content()
                    wd.switch_to.frame('entryIframe')

                    # 이미지
                    img_element = WebDriverWait(wd, 10).until(
                        EC.any_of(
                            EC.presence_of_element_located((By.CSS_SELECTOR, "#cp0_1")),
                            EC.presence_of_element_located((By.CSS_SELECTOR, "#ibu_1"))
                        )
                    )

                    if img_element is not None:
                        P_image = img_element.get_attribute("src")
                    else:
                        P_image = None
                    p_imageList.append(P_image)

                    # 별점
                    P_star = wd.find_element(By.XPATH,
                                             '//*[@id="app-root"]/div/div/div/div[2]/div[1]/div[2]/span[1]').text

                    if '별점' in P_star:
                        starRatingInsertComma = re.sub(r'[^0-9]', '', P_star)
                        P_star = float(starRatingInsertComma[:1] + "." + starRatingInsertComma[1:])  # 별점

                    else:
                        P_star = None
                    p_starList.append(P_star)

                    # 여행지명
                    try:
                        P_name = wd.find_element(By.XPATH, '//*[@id="_title"]/div/span[1]').text
                        p_nameList.append(P_name)
                    except:
                        p_nameList.append(None)

                    # 카테고리
                    try:
                        P_category = wd.find_element(By.XPATH, '//*[@id="_title"]/div/span[2]').text
                        p_categoryList.append(P_category)
                    except:
                        p_categoryList.append(None)

                    # 주소
                    try:
                        P_address = wd.find_element(By.CSS_SELECTOR, ".O8qbU.tQY7D").text[2:]
                        P_address = re.sub(r'[\n]', '', P_address)
                        p_addressList.append(P_address)
                    except:
                        p_addressList.append(None)
                    # -----------------------------------------------------------------------------------------------------------------------------------------------------------------
                    # 주차 안내
                    try:
                        P_park = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.AZ9_F a.xHaT3")
                    except:
                        try:
                            P_park = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.AZ9_F span.zPfVt").text
                        except:
                            p_parkList.append(None)
                        else:
                            p_parkList.append(P_park)
                    else:
                        P_park.click()
                        time.sleep(0.1)
                        P_park = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.AZ9_F span.zPfVt").text
                        p_parkList.append(P_park)

                    # 영업 시간
                    try:
                        P_time = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.pSavy a.gKP9i.RMgN0")
                    except:
                        try:
                            P_time = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.pSavy > div").text
                        except:
                            p_timeList.append(None)
                        else:
                            p_timeList.append(P_time)
                    else:
                        P_time.click()
                        time.sleep(0.1)
                        P_time = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.pSavy a.gKP9i.RMgN0").text[:-2]
                        if P_time[:5] == "영업 종료":
                            P_time = P_time[5:]
                        elif P_time[:4] == "영업 전":
                            P_time = P_time[4:]
                        elif P_time[:4] == "영업 중":
                            P_time = P_time[4:]
                        else:
                            P_time = P_time
                        p_timeList.append(P_time)

                    # 전화번호
                    try:
                        P_call = wd.find_element(By.CSS_SELECTOR, ".O8qbU.nbXkr").text
                        P_call = re.sub(r'[\n가-힣]', '', P_call)
                        p_callList.append(P_call)
                    except:
                        p_siteList.append(None)

                    # 사이트
                    try:
                        P_site = wd.find_element(By.CSS_SELECTOR, ".O8qbU.yIPfO").text
                        P_site = re.sub(r'[\n가-힣]', '', P_site)
                        p_siteList.append(P_site)
                    except:
                        p_siteList.append(None)

                    # 내용
                    try:
                        more = wd.find_element(By.CSS_SELECTOR,
                                               "#app-root > div > div > div > div:nth-child(6) > div > div:nth-child(2) > div.NSTUp > div > a")
                    except:
                        try:
                            EleBody = wd.find_element(By.XPATH, '/html/body')
                            EleBody.send_keys(Keys.PAGE_DOWN)
                            more = WebDriverWait(wd, 2).until(
                                EC.any_of(
                                    EC.presence_of_element_located((By.CSS_SELECTOR,
                                                                    "#app-root > div > div > div > div:nth-child(6) > div > div:nth-child(2) > div.NSTUp > div > a")),
                                )
                            )
                        except:
                            p_contentList.append(None)
                        else:
                            more.click()
                            try:
                                P_content = WebDriverWait(wd, 2).until(
                                    EC.any_of(
                                        EC.presence_of_element_located((By.CSS_SELECTOR,
                                                                        "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp")),
                                        EC.presence_of_element_located((By.CSS_SELECTOR,
                                                                        "div.place_section.no_margin.no_border.TMUvC > div > div > div.ztuVm")),
                                    )
                                )
                                try:
                                    P_content = wd.find_element(By.CSS_SELECTOR,
                                                                "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp > a.OWPIf")
                                except:
                                    try:
                                        P_content = wd.find_element(By.CSS_SELECTOR,
                                                                    "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp").text
                                    except:
                                        P_content = wd.find_element(By.CSS_SELECTOR,
                                                                    "div.place_section.no_margin.no_border.TMUvC div.ztuVm").text
                                else:
                                    P_content.click()
                                    time.sleep(0.1)
                                    P_content = wd.find_element(By.CSS_SELECTOR,
                                                                "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp").text[
                                                :-2]
                            except:
                                P_content = None
                            finally:
                                p_contentList.append(P_content)
                    else:
                        more.click()
                        P_content = WebDriverWait(wd, 2).until(
                            EC.any_of(
                                EC.presence_of_element_located(
                                    (By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp")),
                                EC.presence_of_element_located((By.CSS_SELECTOR,
                                                                "div.place_section.no_margin.no_border.TMUvC > div > div > div.ztuVm")),
                            )
                        )
                        try:
                            P_content = wd.find_element(By.CSS_SELECTOR,
                                                        "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp > a.OWPIf")
                        except:
                            try:
                                P_content = wd.find_element(By.CSS_SELECTOR,
                                                            "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp").text
                            except:
                                try:
                                    P_content = wd.find_element(By.CSS_SELECTOR,
                                                                "div.place_section.no_margin.no_border.TMUvC div.ztuVm").text
                                except:
                                    P_content = None
                        else:
                            P_content.click()
                            time.sleep(0.1)
                            P_content = wd.find_element(By.CSS_SELECTOR,
                                                        "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp").text
                        finally:
                            p_contentList.append(P_content)
                    wd.switch_to.default_content()
                    wd.switch_to.frame('searchIframe')
                except Exception as e:
                    wd.switch_to.default_content()
                    wd.switch_to.frame('searchIframe')
                    print(f"error : {e}")
                p_ordList.append(j)

        if breakPoint == True:
            break
        # 페이지 이동
        wd.find_element(By.CSS_SELECTOR, '#app-root > div > div.XUrfU > div.zRM9F > a:nth-child(%d)' % (i + 2)).click()

    wd.quit()

    # 데이터베이스 연결
    DATABASE_URL = "mysql+pymysql://CatStone:catstone@localhost:3306/catstonedb"
    engine = create_engine(DATABASE_URL, echo=True)

    Base = declarative_base()

    Base.metadata.drop_all(engine)

    #데이터베이스 모델 정의
    class PlaceModel(Base):
        __tablename__ = 'place'
        pord = Column(Integer, primary_key=True)
        p_name = Column(String)            # 추가 데이터 예시
        p_category = Column(String)
        p_content = Column(String)
        p_image = Column(String)
        p_address = Column(String)
        p_call = Column(String)
        p_star = Column(Float)
        p_site = Column(String)
        p_opentime = Column(String)
        p_park = Column(String)

    Base.metadata.create_all(engine)

    Session = sessionmaker(bind=engine)
    session = Session()

    try:
        # 기존 데이터를 모두 삭제
        session.query(PlaceModel).delete()
        # 데이터 삽입
        place_data = [
            {
                'pord': pord,
                'p_name': p_name,
                'p_category': p_category,
                'p_star': p_star,
                'p_image': p_image,
                'p_content': p_content,
                'p_address': p_address,
                'p_park': p_park,
                'p_opentime': p_opentime,
                'p_call': p_call,
                'p_site': p_site
            }
            for pord, p_name, p_category, p_star, p_image, p_content, p_address, p_park, p_opentime, p_call, p_site
            in zip(p_ordList, p_nameList, p_categoryList, p_starList, p_imageList, p_contentList, p_addressList, p_parkList, p_timeList, p_callList, p_siteList)
        ]
        # 세션에 추가
        session.bulk_insert_mappings(PlaceModel, place_data)

        # 모든 변경 사항을 커밋
        session.commit()
    except Exception as e:
        session.rollback()
        print(f"error: {e}")

    # 작업이 완료되면 JSON 응답을 반환합니다.
    return JSONResponse(content={
        "message": "장소 검색이 성공적으로 실행 완료 됨",
        "redirect_url": "http://localhost:3000/place/list"
    })

@app.post("/plan/transport/add")
async def transport_add(Plan_DTO: PlanDTO):
    writer = Plan_DTO.writer
    t_startDateTime = Plan_DTO.t_startDateTime
    start_location = Plan_DTO.start_location
    arrive_location = Plan_DTO.arrive_location
    isCar = Plan_DTO.isCar

    list_url = f'https://map.naver.com/p/directions/-/-/-/transit?c=13.00,0,0,0,dh'

    s = Service()
    options = webdriver.ChromeOptions()
    wd = webdriver.Chrome(service=s, options=options)
    wd.get(list_url)

    c_methodList = []
    c_timeList = []

    def takeTime(goTime, goalTime, t_startDateTime):
        if hasattr(goTime, 'text'):
            goTime = goTime.text
        if hasattr(goalTime, 'text'):
            goalTime = goalTime.text
        onlyGoaltime = re.findall(r'\d+:\d+', goalTime)
        t_goalHour, t_goalMinute = onlyGoaltime[0].split(':')
        if t_goalHour is not None:
            t_goalHour = int(t_goalHour)
        if t_goalMinute is not None:
            t_goalMinute = int(t_goalMinute)

        if '다음날' in goTime:
            t_goalHour += 24
        else:
            t_goalHour = t_goalHour

        t_takeHour = t_goalHour - t_startDateTime.hour
        t_takeMinute = t_goalMinute - t_startDateTime.minute

        if t_takeMinute < 0:
            t_takeHour = t_takeHour - 1
            t_takeMinute = 60 + t_takeMinute
        else:
            t_takeHour = t_takeHour
            t_takeMinute = t_takeMinute

        return t_takeHour, t_takeMinute, t_goalHour, t_goalMinute

    def childrenTakeTime(TimeSet):
        TimeSet = TimeSet.text
        if '시간' in TimeSet:
            tc_takeHour, tc_takeMinute = TimeSet.split('시간')
        else:
            tc_takeHour = None
            tc_takeMinute = TimeSet  # 분만 있는 경우

        tc_takeMinute = re.findall(r'\d+', tc_takeMinute)
        tc_takeMinute = tc_takeMinute[0]

        if tc_takeHour is not None:
            tc_takeHour = int(tc_takeHour)
        else:
            tc_takeHour = 0
        if tc_takeMinute is not None:
            tc_takeMinute = int(tc_takeMinute)
        else:
            tc_takeMinute = 0
        c_time = time(hour=tc_takeHour, minute=tc_takeMinute)
        c_timeList.append(c_time)
        return c_timeList

    # 출발지와 도착지 입력
    try:
        start = WebDriverWait(wd, 10).until(
            EC.any_of(
                EC.presence_of_element_located((By.XPATH, '//*[@id="input_search:r5:"]')),
            )
        )
        WebDriverWait(wd, 10).until(
            EC.any_of(
                EC.presence_of_element_located((By.CSS_SELECTOR,
                                                '#app-layout > div.sc-1s2kvgn.gRrCqu > div.sc-6t4syl.lijNcU > div > div:nth-child(1) > div > div.mapboxgl-canvas-container.mapboxgl-interactive > canvas')),
            )
        )
        start.send_keys(start_location)
        WebDriverWait(wd, 5).until(
            EC.text_to_be_present_in_element_value((By.XPATH, '//*[@id="input_search:r5:"]'), start_location)
        )
        start.send_keys(Keys.ENTER)
        try:
            WebDriverWait(wd, 1).until(
                EC.any_of(
                    EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                )
            )
        except:
            start.send_keys(Keys.ENTER)
            try:
                WebDriverWait(wd, 1).until(
                    EC.any_of(
                        EC.presence_of_element_located(
                            (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                    )
                )
            except:
                start.send_keys(Keys.ENTER)
                try:
                    WebDriverWait(wd, 1).until(
                        EC.any_of(
                            EC.presence_of_element_located(
                                (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                        )
                    )
                except:
                    start.send_keys(Keys.ENTER)
                    try:
                        WebDriverWait(wd, 1).until(
                            EC.any_of(
                                EC.presence_of_element_located(
                                    (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                            )
                        )
                    except:
                        start.send_keys(Keys.ENTER)
                        try:
                            WebDriverWait(wd, 1).until(
                                EC.any_of(
                                    EC.presence_of_element_located(
                                        (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                )
                            )
                        except:
                            start.send_keys(Keys.ENTER)
                            try:
                                WebDriverWait(wd, 1).until(
                                    EC.any_of(
                                        EC.presence_of_element_located(
                                            (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                    )
                                )

                            except:
                                start.send_keys(Keys.ENTER)
                                try:
                                    WebDriverWait(wd, 1).until(
                                        EC.any_of(
                                            EC.presence_of_element_located(
                                                (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                        )
                                    )

                                except:
                                    start.send_keys(Keys.ENTER)
                                    try:
                                        WebDriverWait(wd, 1).until(
                                            EC.any_of(
                                                EC.presence_of_element_located((By.XPATH,
                                                                                '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                            )
                                        )

                                    except:
                                        start.send_keys(Keys.ENTER)
                                        try:
                                            WebDriverWait(wd, 1).until(
                                                EC.any_of(
                                                    EC.presence_of_element_located((By.XPATH,
                                                                                    '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                                )
                                            )

                                        except:
                                            start.send_keys(Keys.ENTER)
                                            WebDriverWait(wd, 1).until(
                                                EC.any_of(
                                                    EC.presence_of_element_located((By.XPATH,
                                                                                    '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                                )
                                            )

        end = wd.find_element(By.XPATH, '//*[@id="input_search:r6:"]')
        end.send_keys(arrive_location)
        WebDriverWait(wd, 15).until(
            EC.text_to_be_present_in_element_value((By.XPATH, '//*[@id="input_search:r6:"]'), arrive_location)
        )
        end.send_keys(Keys.ENTER)
        try:
            WebDriverWait(wd, 1).until(
                EC.any_of(
                    EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                )
            )
        except:
            end.send_keys(Keys.ENTER)
            try:
                WebDriverWait(wd, 1).until(
                    EC.any_of(
                        EC.presence_of_element_located(
                            (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                    )
                )
            except:
                end.send_keys(Keys.ENTER)
                try:
                    WebDriverWait(wd, 1).until(
                        EC.any_of(
                            EC.presence_of_element_located(
                                (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                        )
                    )
                except:
                    end.send_keys(Keys.ENTER)
                    try:
                        WebDriverWait(wd, 1).until(
                            EC.any_of(
                                EC.presence_of_element_located(
                                    (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                            )
                        )
                    except:
                        end.send_keys(Keys.ENTER)
                        try:
                            WebDriverWait(wd, 1).until(
                                EC.any_of(
                                    EC.presence_of_element_located(
                                        (By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                )
                            )
                        except:
                            print("Problem")
        end.send_keys(Keys.ENTER)
        WebDriverWait(wd, 15).until(
            EC.any_of(
                EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
            )
        )
    except:
        print("Problem")
    else:
        # 차 == True일 때 요소 수집
        if isCar == True:
            wd.find_element(By.CSS_SELECTOR,
                            '#section_content > div > div.sc-1gjw9x4.cSWvey > ul > li.sc-aqx8ux.bACEaq.item_search_tab').click()
            try:
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.CSS_SELECTOR,
                                                        '#section_content > div > div.sc-531umo.iMtezi > div > div.direction_summary_list_wrap > ul > li:nth-child(1)')),
                        EC.presence_of_element_located(
                            (By.CSS_SELECTOR, '#section_content > div > div.sc-531umo.iMtezi > div > div > span')),
                    )
                )
            except:
                print("차 페이지가 안 뜸")
            else:
                try:
                    wd.find_element(By.CSS_SELECTOR,
                                    '#section_content > div > div.sc-531umo.iMtezi > div > div.direction_summary_list_wrap')
                except:
                    # 차도가 없으면 도보를 찾기
                    wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[1]/ul/li[3]/button').click()
                    try:
                        WebDriverWait(wd, 15).until(
                            EC.any_of(
                                EC.presence_of_element_located(
                                    (By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[1]/ul/li[1]')),
                            )
                        )
                    except:
                        print("도보 없음, 출발지와 도착지가 같음")
                    else:
                        TimeSet = wd.find_element(By.CSS_SELECTOR,
                                                  '#section_content div.direction_search_result_area li.sc-hzvcqx.sc-2b19ds.fbuohL.hHLcei.is_selected strong.sc-cdjj58 lelshy type_walk_direction').text
                        t_method = "도보"
                else:
                    # 차도가 있음
                    TimeSet = wd.find_element(By.CSS_SELECTOR,
                                              '#section_content > div > div.sc-531umo.iMtezi > div > div.direction_summary_list_wrap > ul > li:nth-child(1) > div > div > div.route_summary_info_duration > strong').text
                    t_method = "차"

            # 시간 설정
            if '시간' in TimeSet:
                t_takeHour, t_takeMinute = TimeSet.split('시간')
            else:
                t_takeHour = None
                t_takeMinute = TimeSet  # 분만 있는 경우

            t_takeMinute = re.findall(r'\d+', t_takeMinute)
            t_takeMinute = t_takeMinute[0]

            if t_takeHour is not None:
                t_takeHour = int(t_takeHour)
            else:
                t_takeHour = 0
            if t_takeMinute is not None:
                t_takeMinute = int(t_takeMinute)
            else:
                t_takeMinute = 0

            t_goalHour = t_startDateTime.hour + t_takeHour
            t_goalMinute = t_startDateTime.minute + t_takeMinute

        else:
            # 차가 없음
            wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[1]/ul/li[1]/button').click()
            try:
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located(
                            (By.CSS_SELECTOR, '#section_content > div > div.sc-531umo.iMtezi > div > div')),
                    )
                )
            except:
                # 대중교통이 없으면 도보를 찾기
                wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[1]/ul/li[3]/button').click()
                try:
                    WebDriverWait(wd, 15).until(
                        EC.any_of(
                            EC.presence_of_element_located(
                                (By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[1]/ul/li[1]')),
                        )
                    )
                except:
                    print("도보 없음, 출발지와 도착지가 같음")
                else:
                    TimeSet = wd.find_element(By.CSS_SELECTOR,
                                              '#section_content div.direction_search_result_area li.sc-hzvcqx.sc-2b19ds.fbuohL.hHLcei.is_selected strong.sc-cdjj58 lelshy type_walk_direction').text
                    t_method = "도보"
            else:
                # 대중교통 이용
                t_method = "대중교통"
                # 대중교통 시간 설정
                tx_startMinute = (t_startDateTime.minute + 9) // 10
                if tx_startMinute == 6:
                    tx_startHour = t_startDateTime.hour + 1
                    tx_startMinute = 0
                else:
                    tx_startHour = t_startDateTime.hour
                    # 대중교통 시간설정 창 열기
                    WebDriverWait(wd, 15).until(
                        EC.any_of(
                            EC.presence_of_element_located(
                                (By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div/button[2]')),
                        )
                    )
                wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div/button[2]').click()
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located(
                            (By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/button')),
                    )
                )
                # 대중교통 시간 열기
                wd.find_element(By.XPATH,
                                '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/button').click()
                # 대중교통 시간 선택
                h = tx_startHour + 1
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH,
                                                        '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/div/div/ul')),
                    )
                )
                hour = wd.find_element(By.XPATH,
                                       '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/div/div/ul/li[%d]' % h)
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH,
                                                        '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/div/div/ul/li[%d]' % h)),
                    )
                )
                wd.execute_script("arguments[0].scrollIntoView();", hour)
                hour.click()
                # 대중교통 분 열기
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located(
                            (By.CSS_SELECTOR, '#tab_pubtrans_directions > ul:nth-child(1) > li:nth-child(1)')
                        ),
                        EC.presence_of_element_located(
                            (By.CSS_SELECTOR, '#tab_pubtrans_directions > ul:nth-child(3) > li:nth-child(1)')
                        ),
                    )
                )
                wd.find_element(By.XPATH,
                                '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[3]/div/button').click()

                # 대중교통 분 선택
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH,
                                                        '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[3]/div/div/div/ul')),
                    )
                )
                minute = wd.find_element(By.XPATH,
                                         '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[3]/div/div/div/ul/li[%d]' % (
                                                 tx_startMinute + 1))
                minute.click()

                # 대중교통 선택
                try:
                    WebDriverWait(wd, 15).until(
                        EC.any_of(
                            EC.presence_of_element_located((By.CSS_SELECTOR, '#tab_pubtrans_directions > ul')),
                        )
                    )
                    wd.find_element(By.CSS_SELECTOR,
                                    '#tab_pubtrans_directions > ul:nth-child(1) > li:nth-child(1)').click()
                except:
                    goTime = "다음날"
                    wd.find_element(By.CSS_SELECTOR,
                                    '#tab_pubtrans_directions > ul:nth-child(3) > li:nth-child(1)').click()
                    try:
                        WebDriverWait(wd, 15).until(
                            EC.any_of(
                                EC.presence_of_element_located((By.CSS_SELECTOR,
                                                                '#sub_panel div.panel_content div.sc-1ngh13j.cupLro li:nth-child(1) div.detail_step_icon_area span.blind')),
                            )
                        )
                    except:
                        print("대중교통 팝업 창 안 뜸 오류")
                    else:
                        li_elements = wd.find_elements(By.CSS_SELECTOR,
                                                       '#sub_panel > div > div.panel_content > div > div > div.sc-1ngh13j.cupLro > ol > li')
                        for i in range(1, len(li_elements)):
                            c_method = wd.find_element(By.CSS_SELECTOR,
                                                       '#sub_panel div.panel_content div.sc-1ngh13j.cupLro li:nth-child(%d) div.detail_step_icon_area span.blind' % i)
                            wd.execute_script("arguments[0].scrollIntoView();", c_method)

                            c_methodList.append(c_method.text)

                            TimeSet = wd.find_element(By.CSS_SELECTOR,
                                                      '#sub_panel div.panel_content div.sc-1ngh13j.cupLro li:nth-child(%d) div.detail_step_icon_area strong.time_travel' % i)

                            c_timeList = childrenTakeTime(TimeSet)

                        goalTime = wd.find_element(By.CSS_SELECTOR,
                                                   '#sub_panel div.panel_content div.sc-1ngh13j.cupLro li:nth-child(%d) div time' % len(
                                                       li_elements))
                else:
                    try:
                        WebDriverWait(wd, 15).until(
                            EC.any_of(
                                EC.presence_of_element_located(
                                    (By.CSS_SELECTOR, '#sub_panel div.panel_content div.sc-1ngh13j.cupLro')),
                            )
                        )
                    except:
                        goTime = None
                    else:
                        goTime = wd.find_element(By.CSS_SELECTOR,
                                                 '#tab_pubtrans_directions ul:nth-child(1) li.sc-1tj2a62.is_selected div.base_info')
                        li_elements = wd.find_elements(By.CSS_SELECTOR,
                                                       '#sub_panel > div > div.panel_content > div > div > div.sc-1ngh13j.cupLro > ol > li')
                        for i in range(1, len(li_elements)):
                            try:
                                c_method = wd.find_element(By.CSS_SELECTOR,
                                                           '#sub_panel div.panel_content div.sc-1ngh13j.cupLro li:nth-child(%d) div.detail_step_icon_area span.blind' % i)
                            except:
                                continue

                            wd.execute_script("arguments[0].scrollIntoView();", c_method)
                            c_methodList.append(c_method.text)

                            TimeSet = wd.find_element(By.CSS_SELECTOR,
                                                      '#sub_panel div.panel_content div.sc-1ngh13j.cupLro li:nth-child(%d) div.detail_step_icon_area strong.time_travel' % i)

                            c_timeList = childrenTakeTime(TimeSet)

                        goalTime = wd.find_element(By.CSS_SELECTOR,
                                                   '#sub_panel div.panel_content div.sc-1ngh13j.cupLro li:nth-child(%d) div time' % len(
                                                       li_elements))
            t_takeHour, t_takeMinute, t_goalHour, t_goalMinute = takeTime(goTime, goalTime, t_startDateTime)
    t_takeTime = time(hour=t_takeHour, minute=t_takeMinute)
    t_goalDate = t_startDateTime.date()
    t_goalTime = time(hour=0, minute=0)
    t_goalDateTime = datetime.combine(t_goalDate, t_goalTime)
    goalDelta = timedelta(hours=t_goalHour, minutes=t_goalMinute)
    t_goalDateTime = t_goalDateTime + goalDelta

    wd.quit()

    from sqlalchemy.orm import relationship

    # 데이터베이스 연결
    DATABASE_URL = "mysql+pymysql://CatStone:catstone@localhost:3306/catstonedb"
    engine = create_engine(DATABASE_URL, echo=True)

    Base = declarative_base()

    class TransportParentModel(Base):
        __tablename__ = 'transportParent'
        tno = Column(BigInteger, primary_key=True, autoincrement=True)
        ppOrd = Column(BigInteger, ForeignKey('plan_place.ppOrd'))
        writer = Column(String(50))
        isCar = Column(Boolean)  # 추가 데이터 예시
        t_method = Column(String(13))
        t_startDateTime = Column(DateTime)
        t_takeTime = Column(Time)
        t_goalDateTime = Column(DateTime)
        NightToNight = Column(Boolean)

        children = relationship("TransportChildModel", back_populates="parent")

    class TransportChildModel(Base):
        __tablename__ = 'transportChild'
        tord = Column(BigInteger, primary_key=True, autoincrement=True)
        tno = Column(BigInteger, ForeignKey('transportParent.tno'))
        c_method = Column(String(36))
        c_takeTime = Column(Time)

        parent = relationship("TransportParentModel", back_populates="children")

    Base.metadata.create_all(engine)

    Session = sessionmaker(bind=engine)
    session = Session()
    # 데이터베이스 모델 정의

    try:
        if t_startDateTime.date() == t_goalDateTime.date():
            # 데이터 삽입
            TransportParent_data = [
                {
                    'writer': writer,
                    'isCar': isCar,
                    't_method': t_method,
                    't_startDateTime': t_startDateTime,
                    't_takeTime': t_takeTime,
                    't_goalDateTime': t_goalDateTime,
                    'NightToNight' : False
                }
            ]
            # 세션에 추가
            session.bulk_insert_mappings(TransportParentModel, TransportParent_data)
            session.commit()

            getTNumber = 1
        else:
            TransportParent_data = [
                {
                    'writer': writer,
                    'isCar': isCar,
                    't_method': t_method,
                    't_startDateTime': t_startDateTime,
                    't_takeTime': t_takeTime,
                    't_goalDateTime': t_startDateTime.replace(hour=23, minute=59, second = 59),
                    'NightToNight': True
                }
            ]
            # 세션에 추가
            session.bulk_insert_mappings(TransportParentModel, TransportParent_data)
            session.commit()

            TransportParent_data = [
                {
                    'writer': writer,
                    'isCar': isCar,
                    't_method': t_method,
                    't_startDateTime': t_goalDateTime.replace(hour=23, minute=59, second = 59),
                    't_takeTime': t_takeTime,
                    't_goalDateTime': t_goalDateTime,
                    'NightToNight': True
                }
            ]
            # 세션에 추가
            session.bulk_insert_mappings(TransportParentModel, TransportParent_data)
            session.commit()

            getTNumber = 2

        if c_methodList:
            parent = session.query(TransportParentModel).order_by(TransportParentModel.tno.desc()).first()
            tno = parent.tno
            # 데이터 삽입
            TransportChild_data = [
                {
                    'tno': tno,
                    'c_method': c_method,
                    'c_takeTime': c_takeTime,
                }
                for c_method, c_takeTime
                in zip(c_methodList, c_timeList)
            ]
            # 세션에 추가
            session.bulk_insert_mappings(TransportChildModel, TransportChild_data)

            # 모든 변경 사항을 커밋
            session.commit()
    except Exception as e:
        session.rollback()
        print(f"error: {e}")
    return JSONResponse(content={
        "message": "교통 검색이 성공적으로 실행 완료 됨",
        "getTNumber": getTNumber
    })
