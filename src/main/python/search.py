#!/usr/bin/env python
# coding: utf-8

# In[12]:

from selenium.webdriver.chrome.service import Service
from sqlalchemy import create_engine, Column, Integer, String, Float, LargeBinary, BLOB
from sqlalchemy.orm import sessionmaker
from sqlalchemy.orm import declarative_base
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
import re
import time
import warnings
import pymysql
warnings.filterwarnings('ignore')
from datetime import datetime

from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait

current_time = datetime.now()

print(current_time.strftime("%Y%m%d%H%M%S"))


# In[2]:


import json

p_region = ""
p_category = ""
p_search = ""


def process_command(command):
    if command["command"] == "run_script":
        script_path = command["script_path"]
        options = command.get("options", {})
        p_region = options.get("p_region", "")
        p_category = options.get("p_category", "")
        p_search = options.get("p_search", "")
        return "Script executed successfully"
    elif command["command"] == "status":
        status = "running"
        return status
    elif command["command"] == "stop":
        status = "stopped"
        return status
    else:
        return "Unrecognized command"
json_command = f'{{"command": "run_script", "script_path": "./Python/search.py", "options": {{"p_region": "p_region", "p_category": "p_category", "p_search": "p_search"}} }}'
command = json.loads(json_command)
response = process_command(command)
json_response = json.dumps({"response": response})


# In[3]:


search = ""
if p_region is not None and p_region != "" : 
    search += p_region
    if p_category is not None and p_category != "" :
        search += " " + p_category
        if p_search is not None and p_search != "" :
            search = p_region + " " + p_category + " " + p_search
    else :      
        if p_search is not None and p_search != "" :
            search += " " + p_search
elif p_category is not None and p_category != "" :
    search += p_category
    if p_search is not None and p_search != "" :
        search += " " + p_search
else :
    if p_search is not None and p_search != "" :
        search += p_search


# In[25]:


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
pordList = []
p_count = 0

s = Service()
options = webdriver.ChromeOptions()
service = Service('chromedriver.exe')
wd = webdriver.Chrome(service=s,options=options)
wd.get(list_url)
time.sleep(4)

# 페이지 다운
def page_down(num):
    body = WebDriverWait(wd, 10).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, "#_pcmap_list_scroll_container"))
    )
    body.click()
    for i in range(num):
        wd.execute_script('arguments[0].scrollTop += arguments[0].offsetHeight;', body)

for i in range(1,(p_count//50)+2):

    if i == (p_count//50 + 1) :
        eleCount = p_count%50 + 1
    else :
        eleCount = 51
    #페이지 로딩 대기
    time.sleep(3)
    wd.switch_to.default_content()
    wd.switch_to.frame('searchIframe')
    time.sleep(2)

    page_down(eleCount)
    time.sleep(2)

    for j in range(1, eleCount):
        try:
            body = wd.find_element(By.CSS_SELECTOR, '#_pcmap_list_scroll_container > ul > li:nth-child(%d) > div.CHC5F > a > div > div > span.TYaxT' %j)
        except:
            breakPoint = True
            break
        else:
            try:
                body.click()
                print("버튼 클릭")
                time.sleep(2)
                wd.switch_to.default_content()
                wd.switch_to.frame('entryIframe')

                #이미지
                img_element = WebDriverWait(wd, 10).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.CSS_SELECTOR, "#cp0_1")),
                        EC.presence_of_element_located((By.CSS_SELECTOR, "#ibu_1"))
                    )
                )

                if img_element is not None:
                    P_image = img_element.get_attribute("src")
                    print("이미지")
                else :
                    P_image = None
                p_imageList.append(P_image)
                print("이미지 리스트에 담음")

                #별점
                P_star = wd.find_element(By.XPATH, '//*[@id="app-root"]/div/div/div/div[2]/div[1]/div[2]/span[1]').text

                if '별점' in P_star :
                    starRatingInsertComma = re.sub(r'[^0-9]', '', P_star)
                    P_star = float(starRatingInsertComma[:1] + "." + starRatingInsertComma[1:]) #별점

                else :
                    P_star = None
                p_starList.append(P_star)
                print(P_star)

                #여행지명
                try:
                    P_name = wd.find_element(By.XPATH, '//*[@id="_title"]/div/span[1]').text
                    p_nameList.append(P_name)
                    print(P_name)
                except:
                    p_nameList.append(None)

                #카테고리
                try:
                    P_category = wd.find_element(By.XPATH, '//*[@id="_title"]/div/span[2]').text
                    p_categoryList.append(P_category)
                    print(P_category)
                except:
                    p_categoryList.append(None)

                #주소
                try:
                    P_address = wd.find_element(By.CSS_SELECTOR, ".O8qbU.tQY7D").text[2:]
                    P_address = re.sub(r'[\n]', '', P_address)
                    p_addressList.append(P_address)
                except:
                    p_addressList.append(None)
                #-----------------------------------------------------------------------------------------------------------------------------------------------------------------
                #주차 안내
                try:
                    P_park = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.AZ9_F a.xHaT3")
                except:
                    try:
                        P_park = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.AZ9_F span.zPfVt").text
                    except :
                        p_parkList.append(None)
                    else:
                        p_parkList.append(P_park)
                else:
                    P_park.click()
                    time.sleep(0.1)
                    P_park = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.AZ9_F span.zPfVt").text
                    p_parkList.append(P_park)
                    print("주차 안내")

                #영업 시간
                try:
                    P_time = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.pSavy a.gKP9i.RMgN0")
                except:
                    try:
                        P_time = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.pSavy > div").text
                    except :
                        p_timeList.append(None)
                    else:
                        p_timeList.append(P_time)
                else:
                    P_time.click()
                    time.sleep(0.1)
                    P_time = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.pSavy a.gKP9i.RMgN0").text[:-2]
                    if P_time[:5] == "영업 종료" :
                        P_time = P_time[6:]
                    elif P_time[:4] == "영업 전" :
                        P_time = P_time[5:]
                    elif P_time[:4] == "영업 중" :
                        P_time = P_time[5:]
                    else :
                        P_time = P_time
                    p_timeList.append(P_time)
                    print("시간 안내")

                #전화번호
                try:
                    P_call = wd.find_element(By.CSS_SELECTOR, ".O8qbU.nbXkr").text
                    P_call = re.sub(r'[\n가-힣]', '', P_call)
                    p_callList.append(P_call)
                    print(P_call)
                except :
                    p_siteList.append(None)

                #사이트
                try:
                    P_site = wd.find_element(By.CSS_SELECTOR, ".O8qbU.yIPfO").text
                    P_site = re.sub(r'[\n가-힣]', '', P_site)
                    p_siteList.append(P_site)
                    print(P_site)
                except :
                    p_siteList.append(None)

                #내용
                try:
                    more = wd.find_element(By.CSS_SELECTOR, "#app-root > div > div > div > div:nth-child(5) > div > div:nth-child(2) > div.NSTUp > div > a > span.TeItc")
                except:
                    try:
                        EleBody = wd.find_element(By.CSS_SELECTOR, '#sub_panel > div > div.panel_content > div')
                        wd.execute_script('arguments[0].scrollTop += arguments[0].offsetHeight;', EleBody)
                        more = WebDriverWait(wd, 2).until(
                            EC.any_of(
                                EC.presence_of_element_located((By.CSS_SELECTOR, "#app-root > div > div > div > div:nth-child(5) > div > div:nth-child(2) > div.NSTUp > div > a > span.TeItc")),
                                EC.presence_of_element_located((By.CSS_SELECTOR, "#app-root > div > div > div > div:nth-child(6) > div > div:nth-child(2) > div.NSTUp > div > a > span.TeItc")),
                            )
                        )
                        print("요소 찾음")
                    except:
                        print("더보기 없음")
                        p_contentList.append(None)
                    else:
                        more.click()
                        try:
                            P_content = WebDriverWait(wd, 4).until(
                                EC.any_of(
                                    EC.presence_of_element_located((By.CSS_SELECTOR, "div.place_section.no_margin.Od79H div.Ve1Rp")),
                                    EC.presence_of_element_located((By.CSS_SELECTOR, "div.place_section.no_margin.no_border.TMUvC div.ztuVm")),
                                )
                            )
                            try:
                                P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H div.Ve1Rp a.OWPIf")
                            except:
                                try:
                                    P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H div.Ve1Rp").text
                                except:
                                    P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.no_border.TMUvC div.ztuVm").text
                            else:
                                P_content.click()
                                time.sleep(0.5)
                                P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H div.Ve1Rp").text[:-2]
                        except:
                            P_content = None
                        finally:
                            p_contentList.append(P_content)
                            print("내용")
                else:
                    more.click()
                    P_content = WebDriverWait(wd, 2).until(
                            EC.any_of(
                                EC.presence_of_element_located((By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp")),
                                EC.presence_of_element_located((By.CSS_SELECTOR, "div.place_section.no_margin.no_border.TMUvC > div > div > div.ztuVm")),
                            )
                        )
                    try:
                        P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp > a.OWPIf")
                        print("버튼 찾음")
                    except:
                        try:
                            P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp").text
                        except:
                            try:
                                P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.no_border.TMUvC div.ztuVm").text
                            except:
                                P_content = None
                    else:
                        P_content.click()
                        print("버튼 클릭함")
                        time.sleep(0.1)
                        P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp").text
                        print("내용 가져옴")
                    finally:
                        p_contentList.append(P_content)
                        print("내용")

                print('데이터 담기 완료')
                wd.switch_to.default_content()
                wd.switch_to.frame('searchIframe')
                print('done')
            except Exception as e:
                wd.switch_to.default_content()
                wd.switch_to.frame('searchIframe')
                print(f"error : {e}")
                print('None')
            pordList.append(j)

    if breakPoint == True:
        break
    #페이지 이동
    wd.find_element(By.CSS_SELECTOR, '#app-root > div > div.XUrfU > div.zRM9F > a:nth-child(%d)' % (i + 2)).click()

wd.quit()



# In[29]:


# 데이터베이스 연결
DATABASE_URL = "mysql+pymysql://CatStone:catstone@localhost:3306/catstonedb"
engine = create_engine(DATABASE_URL, echo=True)

Base = declarative_base()

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
#데이터베이스 모델 정의


# In[30]:


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
        in zip(pordList, p_nameList, p_categoryList, p_starList, p_imageList, p_contentList, p_addressList, p_parkList, p_timeList, p_callList, p_siteList)
    ]
    # 세션에 추가
    session.bulk_insert_mappings(PlaceModel, place_data)
    
    # 모든 변경 사항을 커밋
    session.commit()
except Exception as e:
    session.rollback()
    print(f"error: {e}")


# 
