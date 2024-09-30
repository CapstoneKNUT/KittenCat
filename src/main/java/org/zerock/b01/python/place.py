#!/usr/bin/env python
# coding: utf-8

# In[48]:


from selenium.webdriver.chrome.service import Service
from sqlalchemy import create_engine, Column, Integer, String, Float, LargeBinary, BLOB
from sqlalchemy.orm import sessionmaker
import requests
from sqlalchemy.orm import declarative_base
from selenium import webdriver 
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
import re
import time
import io
from PIL import Image
from io import BytesIO
import sys

import warnings
warnings.filterwarnings('ignore')
from datetime import datetime

from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait

current_time = datetime.now()

print(current_time.strftime("%Y%m%d%H%M%S"))


# In[4]:


import json

p_area = sys.argv[1]
p_subArea = sys.argv[2]
p_category = sys.argv[3]
p_count = sys.argv[4]
p_keyword = sys.argv[5]


# In[4]:


search = ""
search_parts = []
if p_area is not None and p_area != "" :
    search_parts.append(p_area)

if p_subArea is not None and p_subArea != "" :
    search_parts.append(p_subArea)

if p_category is not None and p_category != "" :
    search_parts.append(p_category)

if p_keyword is not None and p_keyword != "" :
    search_parts.append(p_keyword)

search = " ".join(search_parts)

# In[74]:


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

breakPoint = False;
count = p_count + 1

s = Service()
options = webdriver.ChromeOptions()
service = Service('chromedriver.exe')
wd = webdriver.Chrome(service=s,options=options)
wd.get(list_url)
time.sleep(4)

# 페이지 다운
def page_down(num):
    body = wd.find_element(By.TAG_NAME, 'body')
    body.click()
    for i in range(num):
        body.send_keys(Keys.PAGE_DOWN)

for i in range(1,(p_count//50)+2):

    if i == (p_count//50) + 1 :
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
                    img_src = img_element.get_attribute("src")
                    response = requests.get(img_src)
                else :
                    response = None

                if response.status_code == 200 :
                    P_image = BytesIO(response.content)
                    P_image = Image.open(P_image)
                    P_image = P_image.resize((300,200), Image.Resampling.LANCZOS)

                    img_byte_arr = io.BytesIO()
                    P_image.save(img_byte_arr, format='JPEG', quality=85)  # 'PNG'나 'JPEG' 등 원하는 포맷 지정 가능
                    P_image = img_byte_arr.getvalue()
                else :
                    P_image = None
                p_imageList.append(P_image)

                #별점
                P_star = wd.find_element(By.XPATH, '//*[@id="app-root"]/div/div/div/div[2]/div[1]/div[2]/span[1]').text

                if '별점' in P_star :
                    starRatingInsertComma = re.sub(r'[^0-9]', '', P_star)
                    P_star = float(starRatingInsertComma[:1] + "." + starRatingInsertComma[1:]) #별점

                else :
                    P_star = None
                p_starList.append(P_star)

                #여행지명
                try:
                    P_name = wd.find_element(By.XPATH, '//*[@id="_title"]/div/span[1]').text
                    p_nameList.append(P_name)
                except:
                    p_nameList.append(None)

                #카테고리
                try:
                    P_category = wd.find_element(By.XPATH, '//*[@id="_title"]/div/span[2]').text
                    p_categoryList.append(P_category)
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
                    P_park = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.AZ9_F span.zPfVt").text[:-2]
                    p_parkList.append(P_park)

                #영업 시간
                try:
                    P_time = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.pSavy a.gKP9i.RMgN0")
                except:
                    try:
                        P_time = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.pSavy > div").text[:-2]
                    except :
                        p_timeList.append(None)
                    else:
                        p_timeList.append(P_time)
                else:
                    P_time.click()
                    time.sleep(0.1)
                    P_time = wd.find_element(By.CSS_SELECTOR, "div.O8qbU.pSavy a.gKP9i.RMgN0").text[:-2]
                    p_timeList.append(P_time)

                #전화번호
                try:
                    P_call = wd.find_element(By.CSS_SELECTOR, ".O8qbU.nbXkr").text
                    P_call = re.sub(r'[\n가-힣]', '', P_call)
                    p_callList.append(P_call)
                except :
                    p_siteList.append(None)

                #사이트
                try:
                    P_site = wd.find_element(By.CSS_SELECTOR, ".O8qbU.yIPfO").text
                    P_site = re.sub(r'[\n가-힣]', '', P_site)
                    p_siteList.append(P_site)
                except :
                    p_siteList.append(None)

                #내용
                try:
                    more = wd.find_element(By.CSS_SELECTOR, "#app-root > div > div > div > div:nth-child(6) > div > div:nth-child(2) > div.NSTUp > div > a")
                except:
                    try:
                        EleBody = wd.find_element(By.XPATH, '/html/body')
                        EleBody.send_keys(Keys.PAGE_DOWN)
                        more = WebDriverWait(wd, 2).until(
                            EC.any_of(
                                EC.presence_of_element_located((By.CSS_SELECTOR, "#app-root > div > div > div > div:nth-child(6) > div > div:nth-child(2) > div.NSTUp > div > a")),
                            )
                        )
                    except:
                        p_contentList.append(None)
                    else:
                        more.click()
                        try:
                            P_content = WebDriverWait(wd, 2).until(
                                EC.any_of(
                                    EC.presence_of_element_located((By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp")),
                                    EC.presence_of_element_located((By.CSS_SELECTOR, "div.place_section.no_margin.no_border.TMUvC > div > div > div.ztuVm")),
                                )
                            )
                            try:
                                P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp > a.OWPIf")
                            except:
                                try:
                                    P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp").text
                                except:
                                    P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.no_border.TMUvC div.ztuVm").text
                            else:
                                P_content.click()
                                time.sleep(0.1)
                                P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp").text
                        except:
                            P_content = None
                        finally:
                            p_contentList.append(P_content)
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
                        time.sleep(0.1)
                        P_content = wd.find_element(By.CSS_SELECTOR, "div.place_section.no_margin.Od79H > div > div > div.Ve1Rp").text
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
    #페이지 이동
    wd.find_element(By.CSS_SELECTOR, '#app-root > div > div.XUrfU > div.zRM9F > a:nth-child(%d)' % (i + 2)).click()

wd.quit()


# In[67]:


Base = declarative_base()

# 데이터베이스 연결
DATABASE_URL = "mysql+pymysql://CatStone:catstone@localhost:3306/catstonedb"
engine = create_engine(DATABASE_URL, echo=True) 
Base.metadata.create_all(engine)

Session = sessionmaker(bind=engine)
session = Session()

#데이터베이스 모델 정의
class PlaceModel(Base):
    __tablename__ = 'place'
    p_ord = Column(Integer, primary_key=True)
    p_name = Column(String)            # 추가 데이터 예시
    p_category = Column(String)
    p_content = Column(String)
    p_image = Column(BLOB)  # BLOB 타입
    p_address = Column(String)
    p_call = Column(String)
    p_star = Column(Float)
    p_site = Column(String)
    p_opentime = Column(String)
    p_park = Column(String)


# In[75]:


try:
    # 기존 데이터를 모두 삭제
    session.query(PlaceModel).delete()
    # 데이터 삽입
    place_data = [
        {
            'p_ord': p_ord,
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
        for p_ord, p_name, p_category, p_star, p_image, p_content, p_address, p_park, p_opentime, p_call, p_site
        in zip(p_ordList, p_nameList, p_categoryList, p_starList, p_imageList, p_contentList, p_addressList, p_parkList, p_timeList, p_callList, p_siteList)
    ]
    # 세션에 추가
    session.bulk_insert_mappings(PlaceModel, place_data)
    
    # 모든 변경 사항을 커밋
    session.commit()
except Exception as e:
    session.rollback()
    print(f"error: {e}")


# 
