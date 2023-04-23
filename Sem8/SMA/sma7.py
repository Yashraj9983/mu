!pip install google-api-python-client
!pip install -q transformers
from transformers import pipeline
sentiment_pipeline = pipeline("sentiment-analysis")
from googleapiclient.discovery import build
import pandas as pd
import seaborn as sns
from matplotlib import pyplot as plt
import json
api_key = 'AIzaSyDt-sdpHBbr-LZJYFbYTWmFbKqS561xxv4'
youtube = build('youtube','v3',developerKey=api_key)
videoIds=[
    ["nopEs-K4ExM", "World's Fastest Car!"],
    ['kTny1iFuTh0', "Cheetahs Takedown a Wildebeest | The Way of the Cheetah" ],
    ['4fHMbXWcoq0', "How to tie a tie EASY WAY"],
    ['X0tOpBuYasI', "Black Adam – Official Trailer 1"],
    ['qEVUtrk8_B4', "John Wick: Chapter 4 (2023 Movie) Official Trailer "],
    ['I6Dgml4wuNs', "How to Comment on YouTube Videos? "],
]
all_comments=[]
for k in videoIds:
  commentsList = []
  request = youtube.commentThreads().list(
          part="snippet",
          maxResults=100,
          videoId=k[0]
      )
  response = request.execute()
  for i in range(len(response['items'])):
      comment = response['items'][i]['snippet']['topLevelComment']['snippet']['textOriginal']                    
      commentsList.append(comment)
      
  all_comments.append(commentsList)
sentiments=[]
k=0
for j in all_comments:
  t=sentiment_pipeline(j)
  p=0
  n=0
  for i in t:
    if i['label']=="NEGATIVE":
      n+=1
    elif i['label']=="POSITIVE":
      p+=1
  sentiments.append([videoIds[k][1], p,n])  
  k+=1
