!pip install google-api-python-client
from googleapiclient.discovery import build
import pandas as pd
import seaborn as sns
from matplotlib import pyplot as plt
import json
api_key = 'AIzaSyDt-sdpHBbr-LZJYFbYTWmFbKqS561xxv4'
youtube = build('youtube','v3',developerKey=api_key)
channel_id = [
    "UCJIfeSCssxSC_Dhc5s7woww", # lex clips
    "UCeVMnSShP_Iviwkknt83cww", # code with harry
    "UCHAK6CyegY22Zj2GWrcaIxg", # Tech Vision
    "UCzFaxt7hgSHoQVU1GbHyK0Q", # NKTechOfficial
    "UCTB_Mn10B2ZL3RUnPteVKqA", # TopprClass810
    "UCyHta2dyCTkf29AB67AYn7A", # 5MinutesEngineering    
]
def get_channel_stats(channel_id):
  all_data = []
  request = youtube.channels().list(
                part='snippet,contentDetails,statistics',
                id=','.join(channel_id))
  response = request.execute()
  for i in range(len(response['items'])):
    data = dict(Channel_name = response['items'][i]['snippet']['title'],
                 Subscribers = response['items'][i]['statistics']['subscriberCount'],
                 Views = response['items'][i]['statistics']['viewCount'],
                 Total_videos = response['items'][i]['statistics']['videoCount'],
                 playlist_id = response['items'][i]['contentDetails'] 
                                            ['relatedPlaylists']['uploads'],
                )
    all_data.append(data)
  return all_data
allchannestats = get_channel_stats(channel_id)
