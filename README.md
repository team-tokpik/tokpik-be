<img width="1920" alt="0" src="https://github.com/user-attachments/assets/275a6550-854a-4afc-9b64-fb34197e954f">
<br/><br/>
<div align="center">
<a href="https://github.com/team-tokpik/tokpik-be/wiki"> 📚 BE 위키<a/> | <a href="https://github.com/team-tokpik/tokpik-be/wiki/DB-%EC%84%A4%EA%B3%84(ERD-&-Index)"> 📑 DB 설계<a/> | <a href="https://github.com/team-tokpik/tokpik-be/wiki/API-%EB%AA%85%EC%84%B8%EC%84%9C"> 📜 API 명세서<a/> | <a href="https://github.com/team-tokpik/tokpik-be/wiki/Commit-&-Branch-Convention"> 🌳 Commit & Branch Convention<a/>
</div>
  
## 서비스 개요
<img width="1920" alt="1" src="https://github.com/user-attachments/assets/516c1ebd-56c4-4d6a-bcd2-e9dd36ae4eff">
<img width="1920" alt="10" src="https://github.com/user-attachments/assets/8a52d547-8745-44e0-a8b9-44dd0b47bbc2">
<img width="1920" alt="12" src="https://github.com/user-attachments/assets/e77ffd6f-d661-4af4-b434-f189ab70e0f0">
<img width="1920" alt="14" src="https://github.com/user-attachments/assets/d217b419-26ef-4ad3-a8ed-900a5eb47660">
<img width="1920" alt="15" src="https://github.com/user-attachments/assets/1d68f17f-97af-40a1-a614-a9d52f0d2f01">

## 기술 스택
### BE
<img width="782" alt="BE 기술스택" src="https://github.com/user-attachments/assets/6ccf5217-e520-439f-8115-9e5897599df1">

### Infra
<img width="686" alt="Infra 기술 스택 (1)" src="https://github.com/user-attachments/assets/ede2c7fc-b730-4272-8dbf-a26e179ac102">

## 서비스 아키텍처
<img width="710" alt="image" src="https://github.com/user-attachments/assets/e34705c2-38b0-41ab-8999-874afd7749e1">

## CI/CD 
<img width="1071" alt="image" src="https://github.com/user-attachments/assets/8556e562-7fc0-4b83-a3e5-059be179436b">

## 주요 기술적 성과
### Look Aside + Write Around 캐싱 전략 도입, 조회 성능 83% 개선  
<br/>
<div align="center">
  <img width="305" alt="image" src="https://github.com/user-attachments/assets/d67d4e1a-52ed-4a26-ab8b-62bce9e432a5">
</div>
<br/>

- 연관 대화 주제 조회 API의 경우, 반복적으로 동일 데이터를 제공하며 높은 읽기 빈도 발생
- 한 번에 많은 요청 발생 시 응답 시간이 지연되고 DB 부하가 급증하는 문제 발생
- Redis를 활용한 __Look Aside + Write Around 캐싱 전략__ 도입:
    1. 캐시에서 먼저 데이터를 조회 (Look Aside)
    2. 캐시 미스 발생 시 DB에서 데이터를 조회하고 결과를 캐시에 저장
    3. 데이터 갱신 시 DB만 업데이트하고 캐시는 조회 시점에 갱신(Write Around)
- 캐시 적용 결과:
    - 응답 시간: __36ms에서 6ms로 83% 감소__
- 캐시 최적화:
  - 데이터 특성을 고려하여 캐시 만료 시간을 60분으로 설정
  - 정기적인 모니터링을 통해 캐시 크기와 만료 시간 최적화 진행
    
### RTR(Refresh Token Rotation) 도입을 통한 인증/인가 보안 강화
<br/>
<img width="807" alt="image" src="https://github.com/user-attachments/assets/23e50e04-44c0-4d22-9959-0e571c34e8c8">
<br/>

- access token만 사용할 경우 이하 문제들 존재:
    - token 유효 기간이 짧을 경우 만료될 때마다 사용자 재로그인 필요
    - token 유효 기간이 길 경우 제3자가 token 탈취 시 긴 기간동안 사용자인 척 악의적인 요청을 할 수 있게 됨
- access token의 유효기간을 5시간으로 짧게 설정하고, 주기적으로 이를 갱신하는 데 사용할 3일 유효 기간의 refresh token을 함께 사용하도록 구성
- 하지만 refresh token이 탈취당할 경우 탈취자가 유효 기간동안 access token을 계속 발급받을 수 있는 문제 존재
- access token 갱신 시 refresh token도 재발급하는 __RTR(Refresh Token Rotation)__ 도입, 인증/인가 보안 강화
- RTR 도입 효과:
    - refresh token 탈취 시 최대 노출 시간을 5시간으로 단축
    - 이전 refresh token 무효화로 탈취된 token의 재사용 방지

## 팀원 소개 
[안민재](https://github.com/Minjae-An)|[박현지](https://github.com/hhyuun)|
|:-:|:-:|
<img src="https://avatars.githubusercontent.com/u/101340860?v=4" height=160 width=160px></img> | <img src="https://avatars.githubusercontent.com/u/119161420?v=4" height=160 width=160px></img> |

