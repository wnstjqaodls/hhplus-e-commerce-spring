# 항해플러스 프로젝트 전용 VM 배포 계획

> 목표: Proxmox에 전용 LXC 생성 → 이커머스 + TDD 프로젝트 상시 구동
> 학습/개발 전용 환경으로 활용

---

## 1. VM 사양

| 항목 | 값 | 근거 |
|------|-----|------|
| Type | LXC (Privileged) | Docker 구동 필요 |
| ID | 110 | 기존 서비스 ID 범위 고려 |
| Hostname | `lxc110-hhplus` | |
| OS | Ubuntu 22.04 | |
| CPU | 4 cores | Kafka + MySQL + App 동시 구동 |
| RAM | 8GB | Kafka 2GB + MySQL 1GB + Redis 512MB + App 1GB + 여유 |
| Disk | 40GB | MySQL 데이터 + Docker 이미지 + 소스코드 |
| Network | VLAN 20 (Dev) | `192.168.20.110` |
| DNS | hhplus.qwer4.org (선택) | CF Tunnel 연동 시 |

### 사양 산정 근거
```
MySQL 8.0:     ~800MB RAM, ~5GB disk
Redis 7.4:     ~200MB RAM
Kafka + ZK:    ~2GB RAM, ~3GB disk
App (Spring):  ~512MB RAM
Prometheus:    ~256MB RAM, ~2GB disk
Grafana:       ~128MB RAM
Docker images: ~10GB disk
Source code:    ~500MB
여유:           ~4GB RAM, ~20GB disk
```

---

## 2. 설치 순서

### Step 1: LXC 생성 (Proxmox)

```bash
# Proxmox 호스트에서
pct create 110 local:vztmpl/ubuntu-22.04-standard_22.04-1_amd64.tar.zst \
  --hostname lxc110-hhplus \
  --memory 8192 \
  --cores 4 \
  --rootfs local-lvm:40 \
  --net0 name=eth0,bridge=vmbr0,tag=20,ip=192.168.20.110/24,gw=192.168.20.1 \
  --features nesting=1,keyctl=1 \
  --unprivileged 0 \
  --password "dnjsvudwnd@HH" \
  --start 1

# nesting=1: Docker in LXC 필수
# keyctl=1: Docker overlay filesystem 지원
```

### Step 2: 기본 패키지 설치

```bash
apt update && apt upgrade -y
apt install -y \
  curl wget git vim \
  openjdk-17-jdk \
  ca-certificates gnupg lsb-release \
  unzip

# Gradle (SDKMAN 사용)
curl -s "https://get.sdkman.io" | bash
source ~/.sdkman/bin/sdkman-init.sh
sdk install gradle

# Docker
curl -fsSL https://get.docker.com | sh
systemctl enable docker
systemctl start docker

# Docker Compose v2
apt install -y docker-compose-plugin

# 환경변수
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

### Step 3: 프로젝트 클론

```bash
mkdir -p /opt/hhplus && cd /opt/hhplus

# E-Commerce
git clone https://github.com/wnstjqaodls/hhplus-e-commerce-spring.git
cd hhplus-e-commerce-spring

# TDD
cd /opt/hhplus
git clone https://github.com/wnstjqaodls/hhplus-tdd-java.git
```

### Step 4: 인프라 구동 (Docker Compose)

```bash
cd /opt/hhplus/hhplus-e-commerce-spring
docker compose up -d

# 확인
docker compose ps
# 예상 서비스: mysql, redis, kafka, zookeeper, control-center, prometheus, grafana
```

### Step 5: 애플리케이션 빌드 & 실행

```bash
# E-Commerce
cd /opt/hhplus/hhplus-e-commerce-spring
./gradlew build -x test  # 초기 빌드 (테스트 스킵)
./gradlew bootRun &

# TDD
cd /opt/hhplus/hhplus-tdd-java
./gradlew build -x test
./gradlew bootRun --server.port=8081 &
```

### Step 6: systemd 서비스 등록 (자동 시작)

```bash
cat > /etc/systemd/system/hhplus-ecommerce.service << 'EOF'
[Unit]
Description=HHPlus E-Commerce Spring Boot
After=docker.service
Requires=docker.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/hhplus/hhplus-e-commerce-spring
ExecStartPre=/usr/bin/docker compose up -d
ExecStart=/opt/hhplus/hhplus-e-commerce-spring/gradlew bootRun
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable hhplus-ecommerce
```

---

## 3. 홈랩 통합 체크리스트

기존 홈랩 표준 절차 (MEMORY.md 기반):

- [ ] **nginx 설정** (Pi 또는 LXC 105)
  ```nginx
  # /etc/nginx/sites-available/hhplus
  server {
      listen 80;
      server_name hhplus.qwer4.org;
      location / {
          proxy_pass http://192.168.20.110:8080;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
      }
  }
  ```

- [ ] **CF Tunnel 인그레스** (선택 — 외부 접근 필요 시)
  ```yaml
  - hostname: hhplus.qwer4.org
    service: http://192.168.20.110:8080
  ```

- [ ] **fetch-health.py 추가** (Pi)
  ```python
  "hhplus": {"url": "http://192.168.20.110:8080/actuator/health", "name": "HHPlus E-Commerce"},
  ```

- [ ] **portal admin 카드 추가** (선택)

- [ ] **LibreNMS 등록**
  ```bash
  su -s /bin/bash librenms -c "cd /opt/librenms && php artisan device:add 192.168.20.110 --force"
  ```

- [ ] **full-health-test.py 추가** (도메인 설정 시)

---

## 4. 접근 포인트

| 서비스 | URL | 비고 |
|--------|-----|------|
| E-Commerce API | http://192.168.20.110:8080 | Swagger: /swagger-ui |
| TDD API | http://192.168.20.110:8081 | |
| MySQL | 192.168.20.110:3306 | user: application |
| Redis | 192.168.20.110:6379 | |
| Kafka | 192.168.20.110:9092 | |
| Kafka Control Center | http://192.168.20.110:9021 | |
| Prometheus | http://192.168.20.110:9090 | |
| Grafana | http://192.168.20.110:3000 | admin/admin |
| SSH | 192.168.20.110:22 | root/dnjsvudwnd@HH |

---

## 5. 개발 워크플로우

### 로컬 (Windows) → VM 동기화

**방법 A: Git push/pull (권장)**
```bash
# 로컬에서 개발 → commit → push
# VM에서 pull → rebuild
cd /opt/hhplus/hhplus-e-commerce-spring
git pull origin step19
./gradlew build -x test
systemctl restart hhplus-ecommerce
```

**방법 B: rsync/scp (빠른 테스트)**
```bash
# 로컬 변경 파일만 전송
scp -r src/ root@192.168.20.110:/opt/hhplus/hhplus-e-commerce-spring/
# VM에서 rebuild
ssh root@192.168.20.110 "cd /opt/hhplus/hhplus-e-commerce-spring && ./gradlew build -x test"
```

### Hot Reload 개발
```bash
# VM에서 Spring DevTools 활성화 (이미 설정됨)
# application.yml: spring.devtools.restart.enabled=true
# 파일 변경 시 자동 재시작
```

---

## 6. 리소스 모니터링

```bash
# Docker 리소스 사용량
docker stats --no-stream

# 디스크 사용량
df -h /
docker system df

# 메모리 전체 현황
free -h

# Kafka 토픽 확인
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

---

## 7. 백업

```bash
# MySQL 데이터 백업 (NAS로)
docker exec mysql mysqldump -u application -papplication hhplus > /tmp/hhplus-backup.sql
scp /tmp/hhplus-backup.sql KJS@192.168.40.2:/backups/hhplus/

# 전체 LXC 스냅샷 (Proxmox)
pct snapshot 110 hhplus-base --description "초기 구성 완료"
```
