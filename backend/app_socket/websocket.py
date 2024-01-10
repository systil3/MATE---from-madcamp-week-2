from fastapi import APIRouter, FastAPI, WebSocket
from database import SessionLocal
from typing import Dict
from starlette.websockets import WebSocketDisconnect
from db.crud.crud_group import *
import helper_funcs
import json

# 유저의 위치 정보를 저장할 딕셔너리
user_locations = {
   
}

# 딕셔너리에 유저 정보를 저장하고, 동시에 그룹에 속한 모든 유저들에게서 위치 정보를 전달받음
# Group Tag가 None일 경우 '나는 전달만 하겠다' 는 의미
async def websocket_endpoint(websocket: WebSocket):
    print("\n\n###########websocket connected############\n\n")
    await websocket.accept()
    try:
        while True:
            data = await websocket.receive_text()
            print(f"data: {data}")
            # 안드로이드 앱에서 보낸 요청 수신
            
            req_type = data.split("\n")[0]
            
            #요청의 유형이 장소 
            if req_type == "location":
                # 앱으로 보낼 텍스트        
                text = "location\n"
                user_id = data.split("\n")[1].split(":")[1]
                latitude = data.split("\n")[2].split(":")[1]
                longitude = data.split("\n")[3].split(":")[1]
                group_id = data.split("\n")[4].split(":")[1]
                
                #딕셔너리에 user_locations 업데이트, 저장이 안 되어 있을 경우 새로 저장
                store_user_location(user_id, latitude, longitude)
                
                # group_id가 None이면, 나는 위치 정보를 전달만 하고 전송은 안받겠다 의미
                if group_id != "None":
                    db = SessionLocal()
                    group_users = show_all_users_from_group(db, group_id)
                    
                    for group_user in group_users:
                        group_user_id = group_user.id
                        user_info = get_user_info_by_id(group_user_id)
                        if user_info != None:
                            text += f"user_id:{group_user_id} "
                            text += f"user_name:{group_user.name} "
                            latitude = user_info["latitude"]
                            longitude = user_info["longitude"]
                            text += f"latitude:{latitude} "
                            text += f"longitude:{longitude}\n"      
                            
                    await websocket.send_text(text)
                    db.close()
                    
            #요청의 유형이 위치 추적 실패 -> 혼란스럽지 않도록 정보를 제거
            elif req_type == "locationFail":
                user_id = data.split("\n")[1].split(":")[1]
                user_info = get_user_info_by_id(user_id)
                if user_info != None:
                    remove_user_location(user_id)
                    await websocket.send_text(f"Location Fail: deleted user {user_id}'s information.")
                else:
                    await websocket.send_text(f"Error: user {user_id} information already deleted.")

            #요청의 유형을 찾을 수 없음
            else:
                await websocket.send_text("Error: Unknown type of Request.")
                
            
    except WebSocketDisconnect as e:
        print(f"WebSocket closed for {websocket}. Exception: {e}")
        #소켓 연결이 끊어지면 위치 정보를 제거해야 함
        #remove_user_location(websocket)           
                             
def store_user_location(user_id: str, latitude: float, longitude: float):
    # 유저 위치 정보를 딕셔너리에 저장  
    user_locations[user_id] = {"user_id" : user_id, "latitude": latitude, "longitude": longitude}

def get_user_info_by_id(user_id: str):
    return user_locations.get(user_id, None)
    
async def remove_user_location(user_id: str):
    # 소켓이 닫힐 때 호출되어 해당 소켓에 연결된 사용자 정보를 딕셔너리에서 제거
    user_info = user_locations.pop(user_id)
    if user_info:
        print(f"Removed user {user_info['user_id']}'s location information.")

