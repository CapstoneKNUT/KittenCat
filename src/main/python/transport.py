#!/usr/bin/env python
# coding: utf-8

# In[24]:


from selenium.webdriver.chrome.service import Service
from sqlalchemy import create_engine, Column, Integer, String, Float, Boolean, ForeignKey
from sqlalchemy.orm import sessionmaker
from sqlalchemy.orm import declarative_base
from selenium import webdriver 
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
import re

import warnings


warnings.filterwarnings('ignore')
from datetime import datetime

from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait

from typing import Optional

from pydantic import BaseModel

current_time = datetime.now()

print(current_time.strftime("%Y%m%d%H%M%S"))


# In[ ]:


class PlanDTO(BaseModel):
    t_startHour: Optional[int]
    t_startMinute: Optional[int]
    start_location: Optional[str]
    arrive_location: Optional[str]
    isCar: Optional[bool]


# In[25]:


list_url = f'https://map.naver.com/p/directions/-/-/-/transit?c=13.00,0,0,0,dh'

s = Service()
options = webdriver.ChromeOptions()
wd = webdriver.Chrome(service=s,options=options)
wd.get(list_url)

isCar = True
t_startHour = 13
t_startMinute = 20

start_location = "충북 충주시 호암토성2로 3 선우프라자 2층 201호"
arrive_location = "부산역"

c_methodList = []
c_timeHourList =  []
c_timeMinuteList =  []

def takeTime(goTime, goalTime, t_startHour, t_startMinute):
    if hasattr(goTime, 'text'):
        goTime = goTime.text
    if hasattr(goalTime, 'text'):
        goalTime = goalTime.text
    onlyGoaltime  = re.findall(r'\d+:\d+', goalTime)
    t_goalHour, t_goalMinute = onlyGoaltime[0].split(':')
    if t_goalHour is not None:
        t_goalHour = int(t_goalHour)
    if t_goalMinute is not None:
        t_goalMinute = int(t_goalMinute)
    
    if '다음날' in goTime:
        t_goalHour += 24
    else:
        t_goalHour = t_goalHour
        
    t_takeHour = t_goalHour - t_startHour
    t_takeMinute = t_goalMinute - t_startMinute
    
    if t_takeMinute < 0:
        t_takeHour = t_takeHour - 1
        t_takeMinute = 60 - t_takeMinute
    else : 
        t_takeHour = t_takeHour
        t_takeMinute = t_takeMinute
        
    return t_takeHour, t_takeMinute, t_goalHour, t_goalMinute

def childrenTakeTime(Time):
    Time = Time.text
    if '시간' in Time:
        tc_takeHour, tc_takeMinute = Time.split('시간')
    else:
        tc_takeHour = None
        tc_takeMinute = Time  # 분만 있는 경우
    
    tc_takeMinute  = re.findall(r'\d+', tc_takeMinute)
    tc_takeMinute = tc_takeMinute[0]

    if tc_takeHour is not None:
        tc_takeHour = int(tc_takeHour)
    if tc_takeMinute is not None:
        tc_takeMinute = int(tc_takeMinute)
    c_timeHourList.append(tc_takeHour)
    c_timeMinuteList.append(tc_takeMinute)
    return c_timeHourList, c_timeMinuteList
    
    
#출발지와 도착지 입력
try:
    start = WebDriverWait(wd, 10).until(
            EC.any_of(
                EC.presence_of_element_located((By.XPATH, '//*[@id="input_search:r5:"]')),
            )
        )
    WebDriverWait(wd, 10).until(
            EC.any_of(
                EC.presence_of_element_located((By.CSS_SELECTOR, '#app-layout > div.sc-1s2kvgn.gRrCqu > div.sc-6t4syl.lijNcU > div > div:nth-child(1) > div > div.mapboxgl-canvas-container.mapboxgl-interactive > canvas')),
            )
        )
    print("1")
    start.send_keys(start_location) 
    print("2")
    WebDriverWait(wd, 5).until(
        EC.text_to_be_present_in_element_value((By.XPATH, '//*[@id="input_search:r5:"]'), start_location)
    )
    print("3")
    start.send_keys(Keys.ENTER)
    print("4")
    try:
        print("5")
        WebDriverWait(wd, 1).until(
            EC.any_of(
                EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
            )
        )
    except:
        start.send_keys(Keys.ENTER)
        try:
            print("6")
            WebDriverWait(wd, 1).until(
                EC.any_of(
                    EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                )
            )
        except:
            start.send_keys(Keys.ENTER)
            try:
                print("7")
                WebDriverWait(wd, 1).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                    )
                )
            except:
                start.send_keys(Keys.ENTER)
                try:
                    print("8")
                    WebDriverWait(wd, 1).until(
                        EC.any_of(
                            EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                        )
                    )
                except:
                    start.send_keys(Keys.ENTER)
                    try:
                        print("9")
                        WebDriverWait(wd, 1).until(
                            EC.any_of(
                                EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                            )
                        )
                    except:
                        start.send_keys(Keys.ENTER)
                        try:
                            print("9")
                            WebDriverWait(wd, 1).until(
                                EC.any_of(
                                    EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                )
                            )
                            
                        except:
                            start.send_keys(Keys.ENTER)
                            try:
                                print("9")
                                WebDriverWait(wd, 1).until(
                                    EC.any_of(
                                        EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                    )
                                )
                                
                            except:
                                start.send_keys(Keys.ENTER)
                                try:
                                    print("9")
                                    WebDriverWait(wd, 1).until(
                                        EC.any_of(
                                            EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                        )
                                    )
                                    
                                except:
                                    start.send_keys(Keys.ENTER)
                                    try:
                                        print("9")
                                        WebDriverWait(wd, 1).until(
                                            EC.any_of(
                                                EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                            )
                                        )
                                        
                                    except:
                                        start.send_keys(Keys.ENTER)
                                        WebDriverWait(wd, 1).until(
                                            EC.any_of(
                                                EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                                            )
                                        )
                                    else:
                                        print("out")
                                else:
                                    print("get1")
                            else:
                                print("get1")
                        else:
                            print("get1")
                    else:
                        print("get1")
                else:
                    print("get2")
            else:
                print("get3")
        else:
            print("get4")
    else:
        print("get5")
            
    print("7.1")
    end = wd.find_element(By.XPATH, '//*[@id="input_search:r6:"]')
    print("8")
    end.send_keys(arrive_location) 
    print("9")
    WebDriverWait(wd, 15).until(
        EC.text_to_be_present_in_element_value((By.XPATH, '//*[@id="input_search:r6:"]'), arrive_location)
    )
    print("10")
    end.send_keys(Keys.ENTER)
    print("11")
    try:
        print("5")
        WebDriverWait(wd, 1).until(
            EC.any_of(
                EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
            )
        )
    except:
        end.send_keys(Keys.ENTER)
        try:
            print("6")
            WebDriverWait(wd, 1).until(
                EC.any_of(
                    EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                )
            )
        except:
            end.send_keys(Keys.ENTER)
            try:
                print("7")
                WebDriverWait(wd, 1).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                    )
                )
            except:
                end.send_keys(Keys.ENTER)
                try:
                    print("8")
                    WebDriverWait(wd, 1).until(
                        EC.any_of(
                            EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                        )
                    )
                except:
                    end.send_keys(Keys.ENTER)
                    try:
                        print("9")
                        WebDriverWait(wd, 1).until(
                            EC.any_of(
                                EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
                            )
                        )
                    except:
                        print("out")
                    else:
                        print("get1.1")
                else:
                    print("get2.1")
            else:
                print("get3.1")
        else:
            print("get4.1")
    else:
        print("get5.1")
    print("12")
    end.send_keys(Keys.ENTER)
    print("13")
    WebDriverWait(wd, 15).until(
        EC.any_of(
            EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div[2]/ul/li[1]')),
        )
    )
    print("14")
except:
    print("None")
else:
#차 == True일 때 요소 수집
    if isCar == True :
        wd.find_element(By.CSS_SELECTOR, '#section_content > div > div.sc-1gjw9x4.cSWvey > ul > li.sc-aqx8ux.bACEaq.item_search_tab').click()
        try:
            WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.CSS_SELECTOR, '#section_content > div > div.sc-531umo.iMtezi > div > div.direction_summary_list_wrap > ul > li:nth-child(1)')),
                        EC.presence_of_element_located((By.CSS_SELECTOR, '#section_content > div > div.sc-531umo.iMtezi > div > div > span')),
                    )
                )
            print("길 페이지 뜸")
        except:
            print("차 페이지가 안 뜸")
        else:
            try:
                wd.find_element(By.CSS_SELECTOR, '#section_content > div > div.sc-531umo.iMtezi > div > div.direction_summary_list_wrap')
            except:
                #차도가 없으면 도보를 찾기
                wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[1]/ul/li[3]/button').click()
                try:
                    WebDriverWait(wd, 15).until(
                        EC.any_of(
                            EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[1]/ul/li[1]')),
                        )
                    )
                except:
                    print("도보 없음, 출발지와 도착지가 같음")
                else:
                    Time = wd.find_element(By.CSS_SELECTOR, '#section_content div.direction_search_result_area li.sc-hzvcqx.sc-2b19ds.fbuohL.hHLcei.is_selected strong.sc-cdjj58 lelshy type_walk_direction').text
                    t_method = "도보"
            else:
                #차도가 있음
                print("차도가 있음")
                Time = wd.find_element(By.CSS_SELECTOR, '#section_content > div > div.sc-531umo.iMtezi > div > div.direction_summary_list_wrap > ul > li:nth-child(1) > div > div > div.route_summary_info_duration > strong').text
                t_method = "차"
                
        #시간 설정
        if '시간' in Time:
            t_takeHour, t_takeMinute = Time.split('시간')
        else:
            t_takeHour = None
            t_takeMinute = Time  # 분만 있는 경우
        
        t_takeMinute  = re.findall(r'\d+', t_takeMinute)
        t_takeMinute = t_takeMinute[0]

        if t_takeHour is not None:
            t_takeHour = int(t_takeHour)
        if t_takeMinute is not None:
            t_takeMinute = int(t_takeMinute)
        
        if t_takeMinute + t_startMinute >= 60 :
            t_goalHour = t_startHour + t_takeHour + 1
            t_goalMinute = t_startMinute + t_takeMinute - 60
        else :
            t_goalHour = t_startHour + t_takeHour
            t_goalMinute = t_startMinute + t_takeMinute
            
                
    else :
        #차가 없음
        wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[1]/ul/li[1]/button').click()
        try:
            WebDriverWait(wd, 15).until(
                        EC.any_of(
                            EC.presence_of_element_located((By.CSS_SELECTOR, '#section_content > div > div.sc-531umo.iMtezi > div > div')),
                        )
                    )
        except:
            #대중교통이 없으면 도보를 찾기
            wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[1]/ul/li[3]/button').click()
            try:
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[1]/ul/li[1]')),
                    )
                )
            except:
                print("도보 없음, 출발지와 도착지가 같음")
            else:
                Time = wd.find_element(By.CSS_SELECTOR, '#section_content div.direction_search_result_area li.sc-hzvcqx.sc-2b19ds.fbuohL.hHLcei.is_selected strong.sc-cdjj58 lelshy type_walk_direction').text
                t_method = "도보"
        else:
            #대중교통 이용
            t_method = "대중교통"
            #대중교통 시간 설정
            tx_startMinute = (t_startMinute + 9) // 10
            if tx_startMinute == 6:
                tx_startHour = t_startHour + 1
                tx_startMinute = 0
            else :
                tx_startHour = t_startHour
            #대중교통 시간설정 창 열기
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div/button[2]')),
                    )
                )
            wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div/button[2]').click()
            WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/button')),
                    )
                )
            #대중교통 시간 열기
            wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/button').click()
            #대중교통 시간 선택
            h = tx_startHour+1
            WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/div/div/ul')),
                    )
                )
            hour = wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/div/div/ul/li[%d]' %h)
            WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[2]/div/div/div/ul/li[%d]' %h)),
                    )
                )
            wd.execute_script("arguments[0].scrollIntoView();", hour)
            print("요소 스크롤")
            hour.click()
            print("요소 클릭")
            #대중교통 분 열기
            WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.CSS_SELECTOR, '#tab_pubtrans_directions > ul:nth-child(1) > li:nth-child(1)')),
                    )
                )
            wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[3]/div/button').click()
                
            #대중교통 분 선택
            WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[3]/div/div/div/ul')),
                    )
                )
            minute = wd.find_element(By.XPATH, '//*[@id="section_content"]/div/div[2]/div/div[2]/div[2]/div[3]/div/div/div/ul/li[%d]' %(tx_startMinute+1))
            minute.click()
            
            #대중교통 선택
            try:
                WebDriverWait(wd, 15).until(
                    EC.any_of(
                        EC.presence_of_element_located((By.CSS_SELECTOR, '#tab_pubtrans_directions > ul')),
                    )
                )
                print("목록 창 오픈")
                wd.find_element(By.CSS_SELECTOR, '#tab_pubtrans_directions > ul:nth-child(1) > li:nth-child(1)').click()
            except:
                print("나중에 출발")
                goTime = "다음날"
                wd.find_element(By.CSS_SELECTOR, '#tab_pubtrans_directions > ul:nth-child(3) > li:nth-child(1)').click()
                print("목록에서 클릭")
                try:
                    WebDriverWait(wd, 15).until(
                        EC.any_of(
                            EC.presence_of_element_located((By.CSS_SELECTOR, '#sub_panel div.panel_content div.sc-1ngh13j.kxqoYk li:nth-child(1) div.detail_step_icon_area span.blind')),
                        )
                    )
                except:
                    print("대중교통 팝업 창 안 뜸 오류")
                else:
                    li_elements = wd.find_elements(By.CSS_SELECTOR, '#sub_panel > div > div.panel_content > div > div > div.sc-1ngh13j.kxqoYk > ol > li')
                    body = wd.find_element(By.XPATH, '//*[@id="sub_panel"]/div/div[1]/div/div/div[2]')
                    for i in range(1, len(li_elements)):
                        print(len(li_elements))
                        c_method = wd.find_element(By.CSS_SELECTOR, '#sub_panel div.panel_content div.sc-1ngh13j.kxqoYk li:nth-child(%d) div.detail_step_icon_area span.blind' %i)
                        wd.execute_script("arguments[0].scrollIntoView();", c_method)
                        
                        c_methodList.append(c_method.text)
                        
                        Time = wd.find_element(By.CSS_SELECTOR, '#sub_panel div.panel_content div.sc-1ngh13j.kxqoYk li:nth-child(%d) div.detail_step_icon_area strong.time_travel' %i)
                        
                        c_timeHourList, c_timeMinuteList = childrenTakeTime(Time)
                        
                    goalTime = wd.find_element(By.CSS_SELECTOR, '#sub_panel div.panel_content div.sc-1ngh13j.kxqoYk li:nth-child(%d) div time' %len(li_elements))
            else:
                print("현재 출발")
                try:
                    WebDriverWait(wd, 15).until(
                        EC.any_of(
                            EC.presence_of_element_located((By.CSS_SELECTOR, '#sub_panel div.panel_content div.sc-1ngh13j.kxqoYk')),
                        )
                    )
                except:
                    print("대중교통 팝업 창 안 뜸 오류")
                    goTime = None
                else:
                    goTime = wd.find_element(By.CSS_SELECTOR, '#tab_pubtrans_directions ul:nth-child(1) li.sc-1tj2a62.efWKHx.is_selected div.base_info')
                    li_elements = wd.find_elements(By.CSS_SELECTOR, '#sub_panel > div > div.panel_content > div > div > div.sc-1ngh13j.kxqoYk > ol > li')
                    body = wd.find_element(By.XPATH, '//*[@id="sub_panel"]/div/div[1]/div/div/div[2]')
                    for i in range(1, len(li_elements)):
                        c_method = wd.find_element(By.CSS_SELECTOR, '#sub_panel div.panel_content div.sc-1ngh13j.kxqoYk li:nth-child(%d) div.detail_step_icon_area span.blind' %i)
                        wd.execute_script("arguments[0].scrollIntoView();", c_method)
                        
                        c_methodList.append(c_method.text)
                        
                        Time = wd.find_element(By.CSS_SELECTOR, '#sub_panel div.panel_content div.sc-1ngh13j.kxqoYk li:nth-child(%d) div.detail_step_icon_area strong.time_travel' %i)
                        
                        c_timeHourList, c_timeMinuteList = childrenTakeTime(Time)
                        
                    goalTime = wd.find_element(By.CSS_SELECTOR, '#sub_panel div.panel_content div.sc-1ngh13j.kxqoYk li:nth-child(%d) div time' %len(li_elements))
        t_takeHour, t_takeMinute, t_goalHour, t_goalMinute = takeTime(goTime, goalTime, t_startHour, t_startMinute)

wd.quit()


# In[26]:


#이동 수단
print(isCar)
#---------------------------------------
#교통 수단
print(c_methodList)
#이동시간 시
print(c_timeHourList)
#이동시간 분
print(c_timeMinuteList)
#---------------------------------------
#출발 시
print(t_startHour)
#출발 분
print(t_startMinute)
#총 걸린 시간
print(t_takeHour)
#총 걸린 분
print(t_takeMinute)
#도착 시
print(t_goalHour)
#도착 분
print(t_goalMinute)
#교통 수단
print(t_method)


# In[27]:


from sqlalchemy.orm import relationship

# 데이터베이스 연결
DATABASE_URL = "mysql+pymysql://CatStone:catstone@localhost:3306/catstonedb"
engine = create_engine(DATABASE_URL, echo=True)

Base = declarative_base()

class TransportParentModel(Base):
    __tablename__ = 'transportParent'
    tno = Column(Integer, primary_key=True, autoincrement=True)
    isCar = Column(Boolean)           # 추가 데이터 예시
    t_method = Column(String(13))
    t_startHour = Column(Integer)
    t_startMinute = Column(Integer)
    t_takeHour = Column(Integer)
    t_takeMinute = Column(Integer)
    t_goalHour = Column(Integer)
    t_goalMinute = Column(Integer)
    
    children = relationship("TransportChildModel", back_populates="parent")
    
class TransportChildModel(Base):
    __tablename__ = 'transportChild'
    tord = Column(Integer, primary_key=True, autoincrement=True)
    tno = Column(Integer, ForeignKey('transportParent.tno'))
    c_method = Column(String(36))
    c_takeHour = Column(Integer)
    c_takeMinute = Column(Integer)
    
    parent = relationship("TransportParentModel", back_populates="children")

Base.metadata.create_all(engine)

Session = sessionmaker(bind=engine)
session = Session()
#데이터베이스 모델 정의


# In[29]:


try:
    # 데이터 삽입
    TransportParent_data = [
        {
            'isCar': isCar,
            't_method': t_method,
            't_startHour': t_startHour,
            't_startMinute': t_startMinute,
            't_takeHour': t_takeHour,
            't_takeMinute': t_takeMinute,
            't_goalHour': t_goalHour,
            't_goalMinute': t_goalMinute,
        }
    ]
    # 세션에 추가
    session.bulk_insert_mappings(TransportParentModel, TransportParent_data)
    session.commit()
    
    if c_methodList :
        parent = session.query(TransportParentModel).order_by(TransportParentModel.tno.desc()).first()
        tno = parent.tno
        # 데이터 삽입
        TransportChild_data = [
            {
                'tno': tno,
                'c_method': c_method,
                'c_takeHour': c_takeHour,
                'c_takeMinute': c_takeMinute,
            }
            for c_method, c_takeHour, c_takeMinute 
            in zip(c_methodList, c_timeHourList, c_timeMinuteList)
        ]
        # 세션에 추가
        session.bulk_insert_mappings(TransportChildModel, TransportChild_data)
        
        # 모든 변경 사항을 커밋
        session.commit()
except Exception as e:
    session.rollback()
    print(f"error: {e}")

