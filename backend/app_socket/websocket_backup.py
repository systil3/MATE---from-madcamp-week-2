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
                store_user_location(websocket, user_id, latitude, longitude)
                
                # group_id가 None이면, 나는 위치 정보를 전달만 하고 전송은 안받겠다 의미
                if group_id != "None":
                    db = SessionLocal()
                    group_users = show_all_users_from_group(db, group_id)
                    
                    for group_user in group_users:
                        group_user_id = group_user.id
                        user_socket = get_user_socket_by_id(group_user_id)
                        if user_socket != None:
                            user_info = user_locations[user_socket]
                            text += f"user_id:{group_user_id} "
                            text += f"user_name:{group_user.name} "
                            latitude = user_info["latitude"]
                            longitude = user_info["longitude"]
                            text += f"latitude:{latitude} "
                            text += f"longitude:{longitude}\n"      
                            
                    await websocket.send_text(text)
                    db.close()
                    
            #요청의 유형이 푸시알림
            elif req_type == "push":
                
                # 앱으로 보낼 텍스트        
                text = "push\n"
                from_user_id = data.split("\n")[1].split(":")[1]
                db = SessionLocal()
                from_user = get_user(db, from_user_id)
                
                if from_user == None:
                    await websocket.send_text("Error: Cannot find requested user.")
                else:
                    message = data.split("\n")[2].split(":")[1]
                    to_user_id = data.split("\n")[3].split(":")[1]
                    to_user_socket = get_user_socket_by_id(to_user_id)

                    if to_user_socket != None:
                        text += f"user_id:{from_user_id} "
                        text += f"user_name:{from_user.name} "
                        text += f"message:{message} "
                        to_user_socket.send_text(text)
                    else:
                        # 요청한 유저가 연결되어 있지 않을 경우 처리
                        await websocket.send_text(f"Error: Requested user {to_user_id} not available.")
                            
            #요청의 유형이 위치 추적 실패 -> 혼란스럽지 않도록 정보를 제거
            elif req_type == "locationFail":
                user_id = data.split("\n")[1].split(":")[1]
                user_socket = get_user_socket_by_id(user_id)
                if user_socket != None:
                    remove_user_location(user_socket)
                    await websocket.send_text(f"Location Fail: deleted user {user_id}'s information.")
                else:
                    await websocket.send_text(f"Error: user {user_id} information already deleted.")

            #요청의 유형을 찾을 수 없음
            else:
                await websocket.send_text("Error: Unknown type of Request.")
                
            
    except WebSocketDisconnect as e:
        await print(f"WebSocket closed for {websocket}. Exception: {e}")
        #소켓 연결이 끊어지면 위치 정보를 제거해야 함
        #remove_user_location(websocket)           
                             
def store_user_location(websocket: WebSocket, user_id: str, latitude: float, longitude: float):
    # 유저 위치 정보를 딕셔너리에 저장  
    user_locations[websocket] = {"user_id": user_id, 
                                 "latitude": latitude, "longitude": longitude}

def get_user_socket_by_id(user_id: str):
    # 유저 아이디를 기반으로 해당 유저의 WebSocket을 찾아 반환
    # user_locations 딕셔너리를 활용
    for socket, user_info in user_locations.items():
        if user_info.get("user_id") == user_id:
            return socket
    return None

async def remove_user_location(websocket: WebSocket):
    # 소켓이 닫힐 때 호출되어 해당 소켓에 연결된 사용자 정보를 딕셔너리에서 제거
    user_info = user_locations.pop(websocket)
    if user_info:
        print(f"Removed user {user_info['user_id']}'s location information.")

# 그룹에 속한 모든 유저들에게 위치 정보 요구 및 전달
'''async def websocket_endpoint(websocket: WebSocket):
    print("\n\n###########websocket connected############\n\n")
    await websocket.accept()
    
    try:
        while True:
            data = await websocket.receive_text()
            print(f"data: {data}")
            # 안드로이드 앱에서 보낸 요청 수신
            json_data = json.loads(data)

            # 만약 요청이 자신의 위치에 대한 업데이트를 전달하는 것이라면
            if json_data["type"] == "LocationUpdate":
                from_user_id = json_data["from_user_id"]
                latitude = float(json_data["latitude"])
                longitude = float(json_data["longitude"])
                to_group_id = json_data["to_group_id"]
                
                #딕셔너리에 user_locations가 저장이 안 되어 있을 경우 새로 저장
                store_user_location(websocket, from_user_id, latitude, longitude)

                # 그룹의 유저들에게 위치 정보 전송
                users = show_all_users_from_group(Depends(get_db()), to_group_id)
                for user in users:
                    to_user_id = user.id
                    to_user_socket = get_user_socket_by_id(to_user_id)
                    if to_user_socket:
                        await to_user_socket.send_text(f"Update: Got Location from {from_user_id}: {latitude}, {longitude}")
                    else:
                        # 요청한 유저 위치를 찾을 수 없는 경우(ex: 휴대폰이 꺼져 있을 때) 처리
                        await websocket.send_text(f"Error: Requested user {to_user_id} not availab.")   

            # 만약 요청이 특정 그룹의 위치 정보를 요구하는 것이라면
            elif json_data["type"] == "LocationRequest":
                from_user_id = json_data["from_user_id"]
                to_group_id = json_data["to_group_id"]
                to_user_socket = get_user_socket_by_id(to_user_id)
                
                users = show_all_users_from_group(Depends(get_db()), to_group_id)
                for user in users:
                    to_user_id = user.id
                    to_user_socket = get_user_socket_by_id(to_user_id)
                    if to_user_socket:
                        await to_user_socket.send_text(f"Request: LocationRequest from {from_user_id}")
                    else:
                        # 요청한 유저 위치를 찾을 수 없는 경우(ex: 휴대폰이 꺼져 있을 때) 처리
                        await websocket.send_text(f"Error: Requested user {to_user_id} not availab.")   
                        
            #만약 요청이 특정 그룹에게 푸시알림 보내는 거라면
            elif json_data["type"] == "Push":
                from_user_id = json_data["from_user_id"]
                to_group_id = json_data["to_group_id"]
                content = json_data["content"]
                
                users = show_all_users_from_group(Depends(get_db()), to_group_id)
                for user in users:
                    to_user_id = user.id
                    to_user_socket = get_user_socket_by_id(to_user_id)
                    if to_user_socket:
                        await to_user_socket.send_text(f"Push: got notification from {from_user_id}, content : {content}")
                    else:
                        # 요청한 유저 위치를 찾을 수 없는 경우
                        await websocket.send_text(f"Error: Requested user {to_user_id} not availab.")   
            
            #요청의 유형을 찾을 수 없음
            else:
                await websocket.send_text("Error: Unknown type of Request.")
            
    except WebSocketDisconnect as e:
        print(f"WebSocket closed for {websocket}. Exception: {e}")
        await remove_user_location(websocket)'''
    
# 특정 유저에게만 위치 정보 요구 및 전달
'''async def websocket_endpoint(websocket: WebSocket):
    print("\n\n###########websocket connected############\n\n")
    await websocket.accept()
    
    try:
        while True:
            data = await websocket.receive_text()
            print(f"data: {data}")
            # 안드로이드 앱에서 보낸 요청 수신
            json_data = json.loads(data)

            # 만약 요청이 자신의 위치에 대한 업데이트를 전달하는 것이라면
            if json_data["type"] == "LocationUpdate":
                from_user_id = json_data["from_user_id"]
                latitude = float(json_data["latitude"])
                longitude = float(json_data["longitude"])
                to_user_id = json_data["to_user_id"]
                
                #딕셔너리에 user_locations가 저장이 안 되어 있을 경우 새로 저장
                store_user_location(websocket, from_user_id, latitude, longitude)

                # 요청한 유저에게 위치 정보 전송 (??)
                to_user_socket = get_user_socket_by_id(to_user_id)
                if to_user_socket:
                    await to_user_socket.send_text(f"Update: Got Location from {from_user_id}: {latitude}, {longitude}")
                    
                else:
                    # 요청한 유저가 존재하지 않을 경우 처리
                    await websocket.send_text(f"Error: Requested user {to_user_id} not availab.")
                    

            # 만약 요청이 특정 사용자의 위치 정보를 요구하는 것이라면
            elif json_data["type"] == "LocationRequest":
                from_user_id = json_data["from_user_id"]
                to_user_id = json_data["to_user_id"]
                to_user_socket = get_user_socket_by_id(to_user_id)

                if to_user_socket:
                    # B 사용자의 위치 정보를 요청한 A 사용자에게 B 사용자의 위치 정보 전송
                    await to_user_socket.send_text(f"Request: LocationRequest from {from_user_id}")
                    
                else:
                    # 요청한 유저가 존재하지 않을 경우 처리
                    await websocket.send_text(f"Error: Requested user {to_user_id} not availab.")
            
            #만약 요청이 특정 사용자에게 푸시알림 보내는 거라면
            elif json_data["type"] == "Push":
                from_user_id = json_data["from_user_id"]
                to_user_id = json_data["to_user_id"]
                content = json_data["content"]
                to_user_socket = get_user_socket_by_id(to_user_id)            
                
                if to_user_socket:
                    # B 사용자의 위치 정보를 요청한 A 사용자에게 B 사용자의 위치 정보 전송
                    await to_user_socket.send_text(f"Push: got notification from {from_user_id}, content : {content}")
                    
                else:
                    # 요청한 유저가 존재하지 않을 경우 처리
                    await websocket.send_text(f"Error: Requested user {to_user_id} not availab.")   
            
            else:
                await websocket.send_text("Error: Unknown type of Request.")
            
    except WebSocketDisconnect as e:
        print(f"WebSocket closed for {websocket}. Exception: {e}")
        await remove_user_location(websocket)'''
    