# scouter-plugin-server-sentry

## 소개

스카우터에서 특정 지표의 이상이 감지되면 Sentry 로 메시지를 보낸다.

## 현재 측정가능한 지표

- Gc Time (Performance)
  - gc 시간이 설정한 임계치보다 높아지면 센트리 메시지 전송
- Active Service (Performance)
  - 동시에 사용중인 서비스 수가 설정한 임계치보다 높아지면 센트리 메시지 전송
- Error Counting (Xlog)
  - 설정한 시간내 에러가 몇개 이상 발생하면 센트리 메시지 전송 (ex: 10초에 100개)

## scouter.conf 설정

- ext_plugin_sentry_dsn=https://~~~~ : SentryDSN URL
- ext_plugin_sentry_counter_enabled=true : 성능 메트릭 수집 여부 (true / false)
- ext_plugin_sentry_alert_enabled=true : 센트리 알람 여부 (true / false)
- ext_plugin_sentry_debug_enabled=true : 로그파일 기록 여부 (true / false)
- ext_plugin_gc_time_threshold=1000 : GC Time 임계점 (long)
- ext_plugin_tps_threshold=8 : tps 임계점 (int)
- ext_plugin_active_service_threshold=3 : 활성 서비스 개수 임계점 (int)
- ext_plugin_error_time_range=10 : 에러 감지 범위 (int)
- ext_plugin_error_count=50 : 에러 수 임계점 (int)

## Dependencies

- scouter.common
- scouter.server
- io.sentry

## Build Environment

- java 1.8 이상
- Maven 3.x 이상

## 사용법

- mvn package 실행

- target/scouter-plugin-server-sentry-1.1.0.jar , target/lib 에 생성된 jar 파일을 스카우터 lib 폴더로 복사

- 적용대상 Object 재시작 (코드변경시 재시작 필수)
  - 설정만 변경하면 재시작하지 않아도 된다.
  
- conf 폴더의 scouter.conf 에 위 설정 입력 


## 비고

- InvocationTargetException 발생하면 target/lib 파일을 정상적으로 복사한건지 확인해볼것 
- 설정시 ext_plugin 은 붙여야 한다. (https://github.com/scouter-project/scouter/blob/master/scouter.document/main/Plugin-Guide_kr.md#2-annotation)