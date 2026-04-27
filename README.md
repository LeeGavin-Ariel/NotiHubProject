# NotiHubProject
Notification Hub Project

> 이메일, 슬랙, 카카오톡 알림을 단일 인터페이스로 추상화한 
> 이벤트 기반 알림 발송 서버

## 기술 스택
- Backend: Java 17, Spring Boot 3, Apache Kafka
- Database: PostgreSQL, pgvector
- Frontend: React, Tailwind CSS
- Infra: Docker Compose
- 외부 연동: Gmail SMTP, Slack Webhook, OpenAI API

## 주요 기능
- [ ] 단일 API로 다채널 알림 발송 (이메일 / 슬랙 / 카카오톡)
- [ ] Kafka 기반 비동기 처리
- [ ] DLQ + 자동 재시도 (최대 3회)
- [ ] 실패 로그 저장 및 관리자 조회
- [ ] RAG 기반 알림 장애 분석 어시스턴트
- [ ] React 관리자 대시보드

## 아키텍처

## 실행 방법


## 개발 배경
결재 시스템에서 알림 발송을 외부 팀 DB 폴링 방식에 의존했던 경험에서 출발했습니다.
발송 시점 통제 불가, 채널 확장의 어려움, 실패 처리 불투명이라는 한계를 직접 해결하고자
이벤트 기반 알림 허브를 설계하고 구현했습니다.
