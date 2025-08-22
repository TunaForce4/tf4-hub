# 허브 관리 시스템

MSA 기반 물류 관리 및 배송 시스템 내에서 허브 관리를 담당하는 서비스입니다.

## 주요 기능

### 🏢 허브 정보 관리
#### 1. 허브 생성 
  - 허브 이름, 주소 입력 → 새로운 허브 생성
  - 허브 이름 중복 검증
  - **Naver Maps API** 연동 → 위도, 경도 자동 저장
#### 2. 허브 조회 
  - 단건 조회: 허브 ID로 단건 조회, **Redis 캐싱** 적용
  - 목록 조회: 페이지네이션 (생성일시 기준 정렬)
  - `size=0`: 전체 허브 조회 (생성일시 기준 정렬)
#### 3. 허브 수정
  - 허브 이름 변경 시 중복 검증
  - 주소 변경 시 **위도, 경도 재조회 + 모든 경로 자동 재계산**
  - 변경 사유 필수 입력 (변경 이력 관리)
  - 캐시 무효화 적용
#### 4. 허브 삭제 
  - 소프트 삭제 (deletedAt, deletedBy)
  - 캐시 무효화 적용
#### 5. 관리자 등록
  - 허브마다 허브 관리자 지정 가능
  - Auth 서비스 연동으로 사용자 권한 검증
  - HUB 권한을 가진 사용자만 등록 가능
#### 6. 관리자 조회
  - 허브 관리자의 사용자 ID로 담당 허브 조회
  - 해당 관리자 ID에 맞는 허브가 없을 시 예외 처리

---------------------------------

### 🚚 허브 간 이동 경로 관리
#### 1. 허브 간 경로 정보 조회
  - 출발 허브 ID, 도착 허브 ID 기반 검색
  - **Redis 캐싱** 적용
#### 2. 허브 간 이동 경로 자동 생성
  - 새 허브 생성 시 기존 모든 허브와 양방향 경로 생성
  - **Naver Maps API** 기반 최적 경로 탐색
  - 이동 거리(km), 소요 시간(초) 자동 저장
#### 3. 경로 자동 업데이트
  - 허브 주소 변경 시 모든 연결 경로 자동 업데이트
  - **Naver Maps API** 호출로 실시간 교통 정보 반영
  - 캐시 무효화 적용
#### 4. 경로 자동 삭제
  - 허브 삭제 시 모든 연결 경로 자동 삭제
  - 소프트 삭제(deletedAt, deletedBy)
  - 캐시 무효화 적용
#### 5. 경로 수동 업데이트
  - MASTER 권한자가 특정 경로 재계산 가능
  - 업데이트 사유 필수 입력
  
---

### 외부 API 연동
#### 1. Naver Maps Geocoding API
  - 주소 → 위도,경도 검색
  - 유효하지 않은 주소 입력 시 예외 처리
#### 2. Naver Maps Directions 5 API
  - 두 지점 간 최적 경로 계산
  - 이동 거리(km), 소요 시간(초) 저장
#### 3. FeignClient
  - Auth 서비스와 통신
  - 허브 관리자 등록 시 등록하려는 사용자 ID가 HUB 권한인지 확인

---

### 권한 관리
  - MASTER 권한: 모든 기능 사용 가능
  - 그 외 권한: 조회 기능만 사용 가능

---

## 상세 담당 업무

### Hub Service
- 허브 도메인 API 구현
- Naver Maps API 연동: Geocoding(좌표 검색), Directions(경로 탐색)
- 자동 경로 생성 시스템: 허브 생성 시 기존 모든 허브와 양방향 연결
- Redis 캐싱 전략: 허브 및 경로 조회 시 결과 캐싱, 수정/삭제 시 무효화하여 데이터 일관성 유지
- 권한 정책 구현: MASTER 전체 권한, 기타 권한은 조회만 허용
- Auth 서비스 연동: FeignClient 기반 유저의 HUB 권한 검증 (관리자 등록 시 유효성 검사)
- 외부 API 예외 처리: 다중 경로 옵션 순차 검색, 유효하지 않은 주소 예외

--------

## 주요 코드
### 허브 생성 및 자동 경로 계산
```java
    @Transactional
    public HubCreateResponseDto createHub(String roles, HubCreateRequestDto requestDto) {
        // 권한 확인
        validateMasterRole(roles);

        //허브 이름 중복 검증
        checkDuplicateHubName(requestDto.hubName());

        // 위도, 경도는 네이버 지도 API 호출로 설정
        Map<String, Double> coordinates = naverMapsService.getCoordinates(requestDto.hubAddress());
        Double latitude =   coordinates.get("latitude");
        Double longitude =   coordinates.get("longitude");

        Hub hub = Hub.builder()
                .hubName(requestDto.hubName())
                .hubAddress(requestDto.hubAddress())
                .hubLatitude(latitude)
                .hubLongitude(longitude)
                .build();

        Hub savedHub = hubRepository.save(hub);

        // 허브 경로 서비스 호출, 허브 간 이동 경로 자동 생성
        hubRouteService.createHubRoutesAutomatically(savedHub);

        return new HubCreateResponseDto(savedHub.getHubId());
    }
```
```java
    @Transactional
    public void createHubRoutesAutomatically(Hub newHub){
        // newHub를 제외한 기존 허브 모두 조회
        List<Hub> hubList = hubRepository.findAllByHubIdNot(newHub.getHubId());

        // newHub를 출발지로 설정, 모든 허브와 연결
        for(Hub goalHub : hubList){
            createHubRoute(newHub, goalHub);
        }

        // newHub를 도착지로 설정, 모든 허브와 연결
        for(Hub startHub : hubList){
            createHubRoute(startHub, newHub);
        }
    }
```
```java
    /*출발허브, 도착허브 인스턴스를 매개변수로 받아서 허브 경로 생성 */
    private void createHubRoute(Hub startHub, Hub goalHub){
        // Naver Maps API 호출하여 이동 거리와 시간 검색
        Map<String, Number> directions = naverMapsService.getDirections(startHub, goalHub);
        Double distance = directions.get("distance").doubleValue();
        Long transitTime = directions.get("transitTime").longValue();

        HubRoute hubRoute = HubRoute.builder()
                .originHubId(startHub.getHubId())
                .destinationHubId(goalHub.getHubId())
                .transitTime(transitTime)
                .distance(distance)
                .build();

        hubRouteRepository.save(hubRoute);
    }
```
---

## 트러블 슈팅

### Naver Maps API 응답 구조 미확인으로 인한 오류
#### 1. 문제 확인
- 경로 생성 시 INVALID_HUB_ROUTE 예외가 지속적으로 발생
- 경로 옵션이 여러 개인데, 받아온 옵션 중 traoptimal이 없는 경우 문제가 생기는 것으로 판단
```java
        JsonNode routeNode = response.get("route");
        if (routeNode == null || !routeNode.has("traoptimal") || !routeNode.get("traoptimal").isArray() || routeNode.get("traoptimal").size() == 0) {
            throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
        }

        JsonNode firstRoute = routeNode.get("traoptimal").get(0);
        JsonNode summary = firstRoute.get("summary");
        if (summary == null || !summary.has("distance") || !summary.has("duration")) {
            throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
        }
```
#### 2. 시도
- 경로 옵션을 순차적으로 확인한 후 경로를 가져오도록 수정
#### 3. 해결 & 결과
- 수정 후 정상적으로 경로가 생성되는 것을 확인
```java
            String[] routeOptions = {"traoptimal", "trafast", "tracomfort"};
            JsonNode selectedRoute = null;
            String selectedOption = null;

            for (String option : routeOptions) {
                if (routeNode.has(option) && routeNode.get(option).isArray() && !routeNode.get(option).isEmpty()) {
                    selectedRoute = routeNode.get(option);
                    selectedOption = option;
                    log.info("경로 옵션 '{}' 사용 가능", option);
                    break;
                }
            }

            if (selectedRoute == null) {
                log.error("사용 가능한 경로 옵션이 없음. route 노드: {}", routeNode);
                throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
            }
```

---

## 프로젝트 회고
### 황선영
 이번 프로젝트를 통해 처음으로 MSA 기반 시스템을 팀원들과 협업하며 개발하는 경험을 할 수 있었습니다. 협업 과정에서 GitHub의 PR과 브랜치 전략을 직접 사용해 보며 협업 방식을 익힐 수 있었고, 팀원분들이 적극적으로 피드백과 의견을 주셔서 많은 것을 배울 수 있었습니다!

허브 서비스를 맡아 개발하면서 Naver Maps API를 연동해 실시간 경로 탐색 및 저장 기능을 구현했는데, 이 과정에서 외부 API를 다루는 방법을 조금이나마 배울 수 있었습니다.
 
 다만 메시지 큐를 활용한 비동기 처리나 모니터링 시스템 구축 등 시간이 부족해서 시도하지 못한 부분이 많아서 아쉬웠습니다.
