# ChatGPT_Application

> OPEN AI API를 사용해서 여러가지 응용을 해보자 !

# 응용 #1: 링크의 내용 요약하기

### < Process >
1. 링크의 주소를 입력받는다.
2. 해당 주소의 html의 p 태그를 jsoup으로 크롤링한다.
3. 크롤링한 내용을 OPEN API에게 요약해달라고 한다.
4. 요약한 내용을 클라이언트에게 반환한다.