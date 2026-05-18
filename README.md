# 📬 Notification Hub

> 이메일, 슬랙, 카카오톡 알림을 단일 인터페이스로 추상화한 이벤트 기반 알림 발송 서버

## 기술 스택

- **Backend**: Java 17, Spring Boot 3, Apache Kafka
- **Database**: PostgreSQL, pgvector
- **Infra**: Docker Compose
- **외부 연동**: Gmail SMTP, Slack Bot Token, Kakao 메시지 API

## 주요 기능

- 단일 API로 다채널 알림 발송 (이메일 / 슬랙 / 카카오톡)
- Kafka 기반 비동기 처리
- DLQ + 자동 재시도 (최대 3회)
- 실패 로그 저장 및 관리자 조회 API
- Spring Security API Key 인증
- 템플릿 기반 메시지 조합

## 아키텍처

\`\`\`
[알림 요청자]
    ↓ POST /api/notifications (X-API-KEY 인증)
[Notification API Server] (Spring Boot)
    ↓ produce
[Kafka] - topic: notification.request
    ↓ consume
[Notification Consumer]
    ↓
[Channel Router] → 전략 패턴으로 채널 선택
    ↓
[Email Sender] / [Slack Sender] / [Kakao Sender]
    ↓ 실패 시
[Kafka] - topic: notification.dlq
    ↓
[DLQ Consumer] → 최대 3회 재시도 → 실패 로그 DB 저장
\`\`\`

## ERD

| 테이블 | 설명 |
|---|---|
| sender | 알림 요청 시스템 (API Key 관리) |
| notification_template | 채널별 메시지 템플릿 |
| notification_request | 알림 요청 내역 |
| notification_success_log | 발송 성공 로그 |
| notification_failure_log | 발송 실패 로그 |

## 실행 방법

\`\`\`bash
# 1. 환경변수 설정
cp .env.example .env
# .env 파일에 실제 값 입력

# 2. Docker Compose 실행
docker-compose up -d

# 3. Spring Boot 실행
./gradlew bootRun
\`\`\`

## API 사용 예시

\`\`\`bash
curl -X POST "http://localhost:8080/api/notifications" \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: {api-key}" \
  -d '{
    "channel": "EMAIL",
    "recipient": "hong@example.com",
    "templateCode": "APPROVAL_COMPLETE_EMAIL",
    "variables": {
      "name": "홍길동",
      "title": "결재요청알림"
    }
  }'
\`\`\`

## 개발 배경

CSR 결재 시스템에서 알림 발송을 외부 팀 DB 폴링 방식에 의존했던 경험에서 출발했습니다.
발송 시점 통제 불가, 채널 확장의 어려움, 실패 처리 불투명이라는 한계를 직접 해결하고자
이벤트 기반 알림 허브를 설계하고 구현했습니다.
